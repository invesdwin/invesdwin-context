package de.invesdwin.context.integration.filechannel.info;

import java.net.URI;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.string.Strings;

@Immutable
public final class FileChannelInfos {

    private FileChannelInfos() {}

    public static URI extractBaseServerUri(final URI uri, final URI defaultServerUri) {
        if (uri == null) {
            return defaultServerUri;
        }
        final StringBuilder sb = new StringBuilder();
        if (uri.getScheme() != null) {
            sb.append(uri.getScheme()).append("://");
        } else {
            return defaultServerUri;
        }
        if (uri.getAuthority() != null) {
            sb.append(uri.getAuthority());
        }
        return URI.create(sb.toString());
    }

    public static String extractBaseDirectory(final URI uri) {
        if (uri == null) {
            return "/";
        }
        final String path = uri.getPath();
        if (Strings.isBlank(path) || "/".equals(path)) {
            return "/";
        }
        return Strings.putSuffix(path.replace("\\", "/").replaceAll("[/]+", "/"), "/");
    }

    public static String combinePath(final String baseDirectory, final String subDirectory) {
        if (Strings.isBlank(subDirectory)) {
            return baseDirectory;
        }
        String cleanDir = subDirectory.replace("\\", "/").replaceAll("[/]+", "/");
        while (cleanDir.startsWith("/")) {
            cleanDir = cleanDir.substring(1);
        }
        if (cleanDir.isEmpty()) {
            return baseDirectory;
        }
        return Strings.putSuffix(baseDirectory + cleanDir, "/");
    }

    public static URI newDirectoryUri(final IFileChannelInfo info) {
        return newDirectoryUri(info.getBaseServerUri(), info.getAbsoluteDirectory());
    }

    public static URI newDirectoryUri(final URI serverUri, final String directory) {
        final String uriStr = Strings.putSuffix(serverUri != null ? serverUri.toString() : "", "/")
                + Strings.putSuffix(directory, "/");
        return URI.create(uriStr);
    }

    public static URI newFileUri(final IFileChannelInfo info) {
        return newFileUri(info.getBaseServerUri(), info.getAbsoluteDirectory(), info.getFilename());
    }

    public static URI newFileUri(final URI serverUri, final String directory, final String filename) {
        final String uriStr = Strings.putSuffix(serverUri != null ? serverUri.toString() : "", "/")
                + Strings.putSuffix(directory, "/") + filename;
        return URI.create(uriStr);
    }

    public static String newAbsolutePath(final IFileChannelInfo info) {
        final String directory = info.getAbsoluteDirectory();
        final String filename = info.getFilename();
        return newAbsolutePath(directory, filename);
    }

    public static String newAbsolutePath(final String directory, final String filename) {
        return Strings.putSuffix(directory, "/") + filename;
    }

    public static String toString(final IFileChannelInfo info) {
        return toString(info.getServerUri(), info.getAbsoluteDirectory(), info.getFilename());
    }

    public static String toString(final URI serverUri, final String directory, final String filename) {
        return Strings.putSuffix(serverUri != null ? serverUri.toString() : "", "/") + Strings.putSuffix(directory, "/")
                + filename;
    }

    public static String toString(final String serverUri, final String directory, final String filename) {
        return Strings.putSuffix(serverUri, "/") + Strings.putSuffix(directory, "/") + filename;
    }

}