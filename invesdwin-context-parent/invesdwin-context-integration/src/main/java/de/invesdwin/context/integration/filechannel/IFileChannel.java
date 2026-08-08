package de.invesdwin.context.integration.filechannel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.integration.filechannel.info.FileChannelInfos;
import de.invesdwin.context.integration.filechannel.info.IFileChannelInfo;
import de.invesdwin.context.integration.filechannel.info.IFileInfo;
import de.invesdwin.context.integration.filechannel.registry.FileChannelRegistry;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.streams.closeable.ISafeCloseable;

public interface IFileChannel extends ISafeCloseable, IFileChannelInfo {

    Path DOT_DOT = Paths.get("..");

    IFileChannel setFilename(String filename);

    /**
     * Sets the relative sub-directory within the base path.
     */
    IFileChannel setSubDirectory(String subDirectory);

    /**
     * Sets both the sub-directory and filename from a single path string, delegating safe path normalization to
     * standard NIO while validating it does not escape the base directory.
     */
    //CHECKSTYLE:OFF
    default IFileChannel setSubPath(final String path) {
        //CHECKSTYLE:ON
        if (Strings.isBlank(path)) {
            setSubDirectory("");
            setFilename(null);
            return this;
        }

        int start = 0;

        // Handle full URIs if present without heavy URI object allocation
        final int schemeIndex = path.indexOf("://");
        if (schemeIndex >= 0) {
            final int slashAfterScheme = path.indexOf('/', schemeIndex + 3);
            if (slashAfterScheme >= 0) {
                start = slashAfterScheme;
            } else {
                setSubDirectory("");
                setFilename(null);
                return this;
            }
        }

        // Delegate normalization to NIO Paths to securely resolve internal '.' and '..' segments
        final Path normalizedPath = Paths.get(start > 0 ? path.substring(start) : path).normalize();

        // Verify normalization didn't leave unresolved traversal segments escaping the root (zero-allocation check)
        for (final Path segment : normalizedPath) {
            if (segment.equals(DOT_DOT)) {
                throw new IllegalArgumentException("Path traversal escaping the base is not allowed: " + path);
            }
        }

        final String normalizedPathStr = normalizedPath.toString();
        final String cleanPath = normalizedPathStr.indexOf('\\') >= 0 ? normalizedPathStr.replace('\\', '/')
                : normalizedPathStr;
        final int cleanLen = cleanPath.length();
        int cleanStart = 0;

        final String baseDir = getBaseDirectory();
        if (baseDir != null && !"/".equals(baseDir)) {
            final String normalizedBaseRaw = Paths.get(baseDir).normalize().toString();
            final String normalizedBase = normalizedBaseRaw.indexOf('\\') >= 0 ? normalizedBaseRaw.replace('\\', '/')
                    : normalizedBaseRaw;
            final int baseLen = normalizedBase.length();

            if (cleanLen > 0 && cleanPath.charAt(0) == '/') {
                final boolean startsWithBase = cleanPath.startsWith(normalizedBase, 0);
                final boolean startsWithBaseAfterSlash = cleanPath.startsWith(normalizedBase, 1);
                if (!startsWithBase && !startsWithBaseAfterSlash) {
                    throw new IllegalArgumentException("Path [" + path
                            + "] attempts to modify or escape outside the base directory [" + baseDir + "]");
                }
                if (startsWithBase) {
                    cleanStart = baseLen;
                } else {
                    cleanStart = 1 + baseLen;
                }
            } else {
                if (cleanPath.startsWith(normalizedBase, 0)) {
                    cleanStart = baseLen;
                }
            }
        } else {
            if (cleanLen > 0 && cleanPath.charAt(0) == '/') {
                cleanStart = 1;
            }
        }

        while (cleanStart < cleanLen && cleanPath.charAt(cleanStart) == '/') {
            cleanStart++;
        }

        final int lastSlashIndex = cleanPath.lastIndexOf('/');
        if (lastSlashIndex >= cleanStart) {
            setSubDirectory(cleanPath.substring(cleanStart, lastSlashIndex));
            setFilename(cleanPath.substring(lastSlashIndex + 1));
        } else {
            setSubDirectory("");
            setFilename(cleanStart < cleanLen ? cleanPath.substring(cleanStart) : cleanPath);
        }
        return this;
    }

    /**
     * Sets both the sub-directory and filename from a java.nio.file.Path.
     */
    default IFileChannel setSubPath(final Path path) {
        if (path == null) {
            return setSubPath((String) null);
        }
        return setSubPath(path.toString());
    }

    /**
     * Creates a new instance with the given base server URI, retaining the base directory, sub-directory, and filename.
     */
    default IFileChannel withBaseServerUri(final URI baseServerUri) {
        final URI newServerUri = FileChannelInfos.newDirectoryUri(baseServerUri, getBaseDirectory());
        final IFileChannel clone = FileChannelRegistry.newInstance(newServerUri);
        clone.setEmptyFileContent(getEmptyFileContent());
        clone.setSubDirectory(getSubDirectory());
        clone.setFilename(getFilename());
        return clone;
    }

    /**
     * Creates a new instance with the given base server URI string.
     */
    default IFileChannel withBaseServerUri(final String baseServerUri) {
        return withBaseServerUri(URIs.asUri(baseServerUri));
    }

    /**
     * Creates a new instance with the given base directory, retaining the base server URI, sub-directory, and filename.
     */
    default IFileChannel withBaseDirectory(final String baseDirectory) {
        final URI newServerUri = FileChannelInfos.newDirectoryUri(getBaseServerUri(), baseDirectory);
        final IFileChannel clone = FileChannelRegistry.newInstance(newServerUri);
        clone.setEmptyFileContent(getEmptyFileContent());
        clone.setSubDirectory(getSubDirectory());
        clone.setFilename(getFilename());
        return clone;
    }

    /**
     * Creates a new instance with the given absolute directory.
     */
    default IFileChannel withAbsoluteDirectory(final String absoluteDirectory) {
        final URI newServerUri = FileChannelInfos.newDirectoryUri(getBaseServerUri(), absoluteDirectory);
        final IFileChannel clone = FileChannelRegistry.newInstance(newServerUri);
        clone.setEmptyFileContent(getEmptyFileContent());
        clone.setFilename(getFilename());
        return clone;
    }

    /**
     * Creates a new instance with the given sub-path (relative to base directory).
     */
    default IFileChannel withSubPath(final String subPath) {
        final IFileChannel clone = FileChannelRegistry.newInstance(getServerUri());
        clone.setEmptyFileContent(getEmptyFileContent());
        clone.setSubPath(subPath);
        return clone;
    }

    /**
     * Creates a new instance with the given java.nio.file.Path sub-path.
     */
    default IFileChannel withSubPath(final Path path) {
        if (path == null) {
            return withSubPath((String) null);
        }
        return withSubPath(path.toString());
    }

    /**
     * Creates a new instance with the given filename, retaining base server URI, base directory, and sub-directory.
     */
    default IFileChannel withFilename(final String filename) {
        final IFileChannel clone = FileChannelRegistry.newInstance(getServerUri());
        clone.setEmptyFileContent(getEmptyFileContent());
        clone.setSubDirectory(getSubDirectory());
        clone.setFilename(filename);
        return clone;
    }

    /**
     * Creates a new instance with the given absolute path (expecting no baseServerUri, containing baseDirectory,
     * subDirectory, and filename).
     */
    default IFileChannel withAbsolutePath(final String path) {
        if (Strings.isBlank(path)) {
            final IFileChannel clone = FileChannelRegistry.newInstance(getBaseServerUri());
            clone.setEmptyFileContent(getEmptyFileContent());
            clone.setSubPath((String) null);
            return clone;
        }
        if (path.contains("://")) {
            return FileChannelRegistry.newInstance(path);
        } else {
            final IFileChannel clone = FileChannelRegistry.newInstance(getBaseServerUri());
            clone.setEmptyFileContent(getEmptyFileContent());
            clone.setSubPath(path);
            return clone;
        }
    }

    /**
     * Creates a new instance with the given java.nio.file.Path absolute path.
     */
    default IFileChannel withAbsolutePath(final Path path) {
        if (path == null) {
            return withAbsolutePath((String) null);
        }
        return withAbsolutePath(path.toString());
    }

    byte[] getEmptyFileContent();

    IFileChannel setEmptyFileContent(byte[] emptyFileContent);

    IFileChannel createUniqueFile();

    IFileChannel createUniqueFile(String filenamePrefix, String filenameSuffix);

    default IFileChannel connect() {
        return connect(true);
    }

    IFileChannel connect(boolean createDirectory);

    IFileChannel createDirectory();

    boolean isConnected();

    boolean exists();

    IFileInfo info();

    List<? extends IFileInfo> list();

    default List<? extends IFileInfo> listFiles() {
        final List<? extends IFileInfo> list = list();
        final List<IFileInfo> files = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            final IFileInfo file = list.get(i);
            if (file.isFile()) {
                files.add(file);
            }
        }
        return files;
    }

    default List<? extends IFileInfo> listDirectories() {
        final List<? extends IFileInfo> list = list();
        final List<IFileInfo> directories = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            final IFileInfo directory = list.get(i);
            if (directory.isDirectory()) {
                directories.add(directory);
            }
        }
        return directories;
    }

    IFileChannel upload(File file);

    IFileChannel upload(byte[] bytes);

    IFileChannel upload(InputStream input);

    default IFileChannel uploadString(final String str) {
        return upload(str.getBytes(StandardCharsets.UTF_8));
    }

    IFileChannel download(File destination);

    IFileChannel rename(String filename);

    byte[] download();

    default String downloadString() {
        final byte[] bytes = download();
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    IFileChannel delete();

    OutputStream newUpload();

    default File downloadLocalTempFile() {
        final File directory = new File(ContextProperties.TEMP_DIRECTORY, getAbsoluteDirectory());
        try {
            Files.forceMkdir(directory);
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }

        final File file = new File(directory, getFilename());
        Files.deleteQuietly(file);
        if (exists()) {
            download(file);
        }
        return file;
    }

    default IFileChannel reconnect() {
        return reconnect(true);
    }

    IFileChannel reconnect(boolean createDirectory);

    InputStream newDownload();

    /**
     * Creates a new instance with the given relative sub-directory.
     */
    IFileChannel withSubDirectory(String subDirectory);

    default IFileChannel copy(final String targetAbsolutePath) {
        return copy(withAbsolutePath(targetAbsolutePath));
    }

    default IFileChannel copy(final Path targetPath) {
        return copy(withAbsolutePath(targetPath));
    }

    default IFileChannel copy(final IFileChannel targetChannel) {
        connect(false);
        try (InputStream in = newDownload()) {
            if (in == null) {
                throw new IllegalStateException("Source file not found: " + this);
            }
            targetChannel.upload(in);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return targetChannel;
    }

    default IFileChannel move(final String targetAbsolutePath) {
        return move(withAbsolutePath(targetAbsolutePath));
    }

    default IFileChannel move(final Path targetPath) {
        return move(withAbsolutePath(targetPath));
    }

    default IFileChannel move(final IFileChannel targetChannel) {
        connect(false);
        if (getClass().isInstance(targetChannel)) {
            moveSameType(targetChannel);
        } else {
            copy(targetChannel);
            delete();
        }
        return targetChannel;
    }

    void moveSameType(IFileChannel targetChannel);
}