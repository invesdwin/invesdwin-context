package de.invesdwin.context.integration.filechannel.info.path;

import java.net.URI;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.uri.URIs;

@Immutable
public final class FileChannelPaths {

    private FileChannelPaths() {}

    public static URI combineUri(final URI baseDirectory, final String subDirectory) {
        return URIs.asUri(combinePath(baseDirectory, subDirectory));
    }

    public static String combinePath(final URI baseDirectory, final String subDirectory) {
        return combinePath(baseDirectory.toString(), subDirectory);
    }

    //CHECKSTYLE:OFF
    public static String combinePath(final String baseDirectory, final String subDirectory) {
        //CHECKSTYLE:ON
        if (Strings.isBlank(subDirectory)) {
            return Strings.isBlank(baseDirectory) ? "" : Strings.putSuffix(baseDirectory, "/");
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

        // If subDirectory was purely slashes, ensure baseDirectory ends with '/'
        if (i == len) {
            return Strings.isBlank(baseDirectory) ? "/" : Strings.putSuffix(baseDirectory, "/");
        }

        final int baseLen = baseDirectory != null ? baseDirectory.length() : 0;
        final StringBuilder sb = new StringBuilder(baseLen + len - i + 2);

        boolean lastWasSlash = false;

        // 2. Append base directory and ensure proper joiner slash
        if (baseLen > 0) {
            sb.append(baseDirectory);
            final char lastBaseChar = baseDirectory.charAt(baseLen - 1);
            if (lastBaseChar == '/' || lastBaseChar == '\\') {
                if (lastBaseChar == '\\') {
                    sb.setCharAt(baseLen - 1, '/');
                }
                lastWasSlash = true;
            } else {
                sb.append('/');
                lastWasSlash = true;
            }
        }

        // 3. Single pass to append subDirectory, convert backslashes, and squash slashes
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

        // 4. Ensure trailing slash inline
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