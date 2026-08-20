package de.invesdwin.context.integration.filechannel.info.path;

import java.net.URI;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.string.Strings;

@Immutable
public final class FileChannelPaths {

    private FileChannelPaths() {}

    public static String combinePath(final String baseDirectory, final String subDirectory) {
        if (Strings.isBlank(subDirectory)) {
            return baseDirectory;
        }

        final int len = subDirectory.length();
        int i = 0;

        // 1. Skip all leading slashes and backslashes without substring allocations
        while (i < len) {
            final char c = subDirectory.charAt(i);
            if (c == '/' || c == '\\') {
                i++;
            } else {
                break;
            }
        }

        // If the subDirectory was purely slashes, just return the baseDirectory
        if (i == len) {
            return baseDirectory;
        }

        // 2. Pre-size the StringBuilder to avoid resizing during append
        // Capacity: base directory length + remaining characters + 1 for potential trailing slash
        final StringBuilder sb = new StringBuilder(baseDirectory.length() + len - i + 1);
        sb.append(baseDirectory);

        // 3. Single pass to append characters, convert backslashes, and squash multiple slashes
        boolean lastWasSlash = false;
        while (i < len) {
            final char c = subDirectory.charAt(i);
            if (c == '/' || c == '\\') {
                if (!lastWasSlash) {
                    sb.append('/');
                    lastWasSlash = true;
                }
            } else {
                sb.append(c);
                lastWasSlash = false;
            }
            i++;
        }

        // 4. Ensure trailing slash inline (replaces the need for Strings.putSuffix())
        if (!lastWasSlash) {
            sb.append('/');
        }

        return sb.toString();
    }

    public static URI newDirectoryUri(final IFileChannelPath info) {
        return newDirectoryUri(info.getBaseServerUri(), info.getAbsoluteDirectory());
    }

    public static URI newDirectoryUri(final URI serverUri, final String directory) {
        return newDirectoryUri(serverUri.toString(), directory);
    }

    public static URI newDirectoryUri(final String serverUri, final String directory) {
        final String uriStr = combinePath(serverUri, directory);
        return URI.create(uriStr);
    }

    public static URI newFileUri(final IFileChannelPath info) {
        return newFileUri(info.getBaseServerUri(), info.getAbsoluteDirectory(), info.getFilename());
    }

    public static URI newFileUri(final URI serverUri, final String directory, final String filename) {
        return newFileUri(serverUri.toString(), directory, filename);
    }

    public static URI newFileUri(final String serverUri, final String directory, final String filename) {
        final String uriStr = toString(serverUri, directory, filename);
        return URI.create(uriStr);
    }

    public static URI newFileUri(final URI serverUri, final String filename) {
        return newFileUri(serverUri.toString(), filename);
    }

    public static URI newFileUri(final String serverUri, final String filename) {
        final String uriStr = toString(serverUri, null, filename);
        return URI.create(uriStr);
    }

    public static String newAbsolutePath(final IFileChannelPath info) {
        final String directory = info.getAbsoluteDirectory();
        final String filename = info.getFilename();
        return newAbsolutePath(directory, filename);
    }

    public static String newAbsolutePath(final String directory, final String filename) {
        return Strings.putSuffix(directory, "/") + filename;
    }

    public static String toString(final IFileChannelPath info) {
        return toString(info.getBaseServerUri(), info.getAbsoluteDirectory(), info.getFilename());
    }

    public static String toString(final URI serverUri, final String directory, final String filename) {
        return toString(serverUri.toString(), directory, filename);
    }

    public static String toString(final String serverUri, final String directory, final String filename) {
        return combinePath(serverUri, directory) + Strings.asStringEmptyText(filename);
    }

}