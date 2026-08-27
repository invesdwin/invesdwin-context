package de.invesdwin.context.integration.filechannel.info.path;

import java.net.URI;
import java.util.function.Supplier;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.uri.URIs;

@Immutable
public final class UriFileChannelPath implements IFileChannelPath {
    private final URI serverUri;
    private final URI baseServerUri;
    private final String absoluteDirectory;
    private final String filename;

    private UriFileChannelPath(final URI serverUri, final URI baseServerUri, final String absoluteDirectory,
            final String filename) {
        this.serverUri = serverUri != null ? serverUri : baseServerUri;
        this.baseServerUri = baseServerUri;
        this.absoluteDirectory = absoluteDirectory;
        this.filename = filename;
    }

    @Override
    public URI getServerUri() {
        return serverUri;
    }

    @Override
    public URI getBaseServerUri() {
        return baseServerUri;
    }

    @Override
    public String getAbsoluteDirectory() {
        return absoluteDirectory;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        return FileChannelPaths.toString(baseServerUri, absoluteDirectory, filename);
    }

    /**
     * Creates a {@link UriFileChannelPath} by auto-detecting whether the final path segment represents a file or a
     * directory.
     * <p>
     * If the clean path ends with a slash ({@code /}), it is treated as a directory. Otherwise, if the last path
     * segment contains a dot ({@code .}), it is parsed as a file; if it contains no dot, it is treated as a directory.
     *
     * @param serverUri
     *            the server URI to parse; must not be {@code null}
     * @param defaultServerUriF
     *            supplier for the fallback base server URI if {@code serverUri} lacks a scheme
     * @return the parsed {@link UriFileChannelPath}
     * @throws NullPointerException
     *             if {@code serverUri} is {@code null}
     */
    public static UriFileChannelPath valueOf(final URI serverUri, final Supplier<URI> defaultServerUriF) {
        if (serverUri == null) {
            throw new NullPointerException("serverUri cannot be null");
        }

        // Extract Base Server URI
        final URI baseServerUri;
        if (serverUri.getScheme() != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(serverUri.getScheme()).append("://");
            if (Strings.isNotBlank(serverUri.getAuthority())) {
                sb.append(serverUri.getAuthority());
            } else {
                sb.append("/");
            }
            baseServerUri = URIs.asUri(sb.toString());
        } else {
            baseServerUri = defaultServerUriF.get();
        }

        // Extract Base Directory and Filename in one pass
        final String path = serverUri.getPath();
        if (Strings.isBlank(path) || "/".equals(path)) {
            return new UriFileChannelPath(serverUri, baseServerUri, "/", null);
        }

        final String cleanPath = path.replace("\\", "/").replaceAll("[/]+", "/");
        if (cleanPath.endsWith("/")) {
            return new UriFileChannelPath(serverUri, baseServerUri, cleanPath, null);
        }

        final int lastSlash = cleanPath.lastIndexOf('/');
        final String candidateFilename = lastSlash == -1 ? cleanPath : cleanPath.substring(lastSlash + 1);

        if (candidateFilename.contains(".")) {
            final String baseDirectory = lastSlash == -1 ? "/" : cleanPath.substring(0, lastSlash + 1);
            return new UriFileChannelPath(serverUri, baseServerUri, baseDirectory, candidateFilename);
        } else {
            final String baseDirectory = cleanPath + "/";
            return new UriFileChannelPath(serverUri, baseServerUri, baseDirectory, null);
        }
    }

    /**
     * Creates a {@link UriFileChannelPath} by explicitly treating the entire path as a directory.
     * <p>
     * Bypasses extension/dot auto-detection logic. Appends a trailing slash ({@code /}) to the path if missing and sets
     * the filename to {@code null}.
     *
     * @param serverUri
     *            the server URI to parse; must not be {@code null}
     * @param defaultServerUriF
     *            supplier for the fallback base server URI if {@code serverUri} lacks a scheme
     * @return the parsed directory {@link UriFileChannelPath}
     * @throws NullPointerException
     *             if {@code serverUri} is {@code null}
     */
    public static UriFileChannelPath valueOfDirectory(final URI serverUri, final Supplier<URI> defaultServerUriF) {
        if (serverUri == null) {
            throw new NullPointerException("serverUri cannot be null");
        }

        // Extract Base Server URI
        final URI baseServerUri;
        if (serverUri.getScheme() != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(serverUri.getScheme()).append("://");
            if (Strings.isNotBlank(serverUri.getAuthority())) {
                sb.append(serverUri.getAuthority());
            } else {
                sb.append("/");
            }
            baseServerUri = URIs.asUri(sb.toString());
        } else {
            baseServerUri = defaultServerUriF.get();
        }

        // Extract Base Directory
        final String path = serverUri.getPath();
        if (Strings.isBlank(path) || "/".equals(path)) {
            return new UriFileChannelPath(serverUri, baseServerUri, "/", null);
        }

        final String cleanPath = path.replace("\\", "/").replaceAll("[/]+", "/");
        if (cleanPath.endsWith("/")) {
            return new UriFileChannelPath(serverUri, baseServerUri, cleanPath, null);
        }

        // Explicitly treat the remaining path as a directory
        final String baseDirectory = cleanPath + "/";
        return new UriFileChannelPath(serverUri, baseServerUri, baseDirectory, null);
    }

    /**
     * Creates a {@link UriFileChannelPath} by explicitly treating the final segment of the path as a filename.
     * <p>
     * Bypasses extension/dot auto-detection logic so filenames without extensions (e.g., {@code /etc/hosts}) are
     * correctly populated. If the URI path explicitly ends with a trailing slash ({@code /}), it remains treated as a
     * directory with a {@code null} filename.
     *
     * @param serverUri
     *            the server URI to parse; must not be {@code null}
     * @param defaultServerUriF
     *            supplier for the fallback base server URI if {@code serverUri} lacks a scheme
     * @return the parsed file {@link UriFileChannelPath}
     * @throws NullPointerException
     *             if {@code serverUri} is {@code null}
     */
    public static UriFileChannelPath valueOfFile(final URI serverUri, final Supplier<URI> defaultServerUriF) {
        if (serverUri == null) {
            throw new NullPointerException("serverUri cannot be null");
        }

        // Extract Base Server URI
        final URI baseServerUri;
        if (serverUri.getScheme() != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(serverUri.getScheme()).append("://");
            if (Strings.isNotBlank(serverUri.getAuthority())) {
                sb.append(serverUri.getAuthority());
            } else {
                sb.append("/");
            }
            baseServerUri = URIs.asUri(sb.toString());
        } else {
            baseServerUri = defaultServerUriF.get();
        }

        // Extract Base Directory and Filename
        final String path = serverUri.getPath();
        if (Strings.isBlank(path) || "/".equals(path)) {
            return new UriFileChannelPath(serverUri, baseServerUri, "/", null);
        }

        final String cleanPath = path.replace("\\", "/").replaceAll("[/]+", "/");
        if (cleanPath.endsWith("/")) {
            // If it ends with a slash, it's explicitly a directory. Filename remains null.
            return new UriFileChannelPath(serverUri, baseServerUri, cleanPath, null);
        }

        // Explicitly treat the last segment as a file, ignoring the dot check
        final int lastSlash = cleanPath.lastIndexOf('/');
        final String filename = lastSlash == -1 ? cleanPath : cleanPath.substring(lastSlash + 1);
        final String baseDirectory = lastSlash == -1 ? "/" : cleanPath.substring(0, lastSlash + 1);

        return new UriFileChannelPath(serverUri, baseServerUri, baseDirectory, filename);
    }

    public static URI extractBaseServerUri(final URI uri, final Supplier<URI> defaultServerUriF) {
        if (uri == null) {
            return defaultServerUriF.get();
        }
        final StringBuilder sb = new StringBuilder();
        if (uri.getScheme() != null) {
            sb.append(uri.getScheme()).append("://");
        } else {
            return defaultServerUriF.get();
        }
        if (Strings.isNotBlank(uri.getAuthority())) {
            sb.append(uri.getAuthority());
        } else {
            sb.append("/");
        }
        return URI.create(sb.toString());
    }

    public static String extractAbsoluteDirectory(final URI uri) {
        if (uri == null) {
            return "/";
        }
        final String path = uri.getPath();
        if (Strings.isBlank(path) || "/".equals(path)) {
            return "/";
        }
        final String cleanPath = path.replace("\\", "/").replaceAll("[/]+", "/");
        if (cleanPath.endsWith("/")) {
            return cleanPath;
        }
        final int lastSlash = cleanPath.lastIndexOf('/');
        final String candidateFilename = lastSlash == -1 ? cleanPath : cleanPath.substring(lastSlash + 1);

        if (candidateFilename.contains(".")) {
            return lastSlash == -1 ? "/" : cleanPath.substring(0, lastSlash + 1);
        }
        return cleanPath + "/";
    }

    public static String extractFileName(final URI uri) {
        if (uri == null) {
            return null;
        }
        final String path = uri.getPath();
        if (Strings.isBlank(path) || "/".equals(path)) {
            return null;
        }
        final String cleanPath = path.replace("\\", "/").replaceAll("[/]+", "/");
        if (cleanPath.endsWith("/")) {
            return null;
        }
        final int lastSlash = cleanPath.lastIndexOf('/');
        final String candidateFilename = lastSlash == -1 ? cleanPath : cleanPath.substring(lastSlash + 1);

        if (candidateFilename.contains(".")) {
            return candidateFilename;
        }
        return null;
    }

}