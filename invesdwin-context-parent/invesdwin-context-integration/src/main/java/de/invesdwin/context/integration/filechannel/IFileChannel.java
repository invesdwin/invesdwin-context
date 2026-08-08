package de.invesdwin.context.integration.filechannel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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

    IFileChannel setFilename(String filename);

    /**
     * Sets the relative sub-directory within the base path.
     */
    IFileChannel setSubDirectory(String subDirectory);

    /**
     * Sets both the sub-directory and filename from a single path string, validating that it does not attempt to modify
     * or escape outside the base directory.
     */
    default IFileChannel setSubPath(final String path) {
        if (Strings.isBlank(path)) {
            setSubDirectory("");
            setFilename(null);
            return this;
        }

        String cleanPath = path.replace("\\", "/");

        // Handle full URIs if present
        if (cleanPath.contains("://")) {
            try {
                final URI uri = URIs.asUri(cleanPath);
                final String uriPath = uri.getPath();
                if (Strings.isNotBlank(uriPath)) {
                    cleanPath = uriPath;
                }
            } catch (final Exception e) {
                final int schemeIndex = cleanPath.indexOf("://");
                final int pathIndex = cleanPath.indexOf('/', schemeIndex + 3);
                if (pathIndex >= 0) {
                    cleanPath = cleanPath.substring(pathIndex);
                }
            }
        }

        cleanPath = cleanPath.replaceAll("[/]+", "/");

        // Guard against traversal attempts escaping the base directory
        if (cleanPath.contains("..")) {
            throw new IllegalArgumentException("Path traversal with '..' is not allowed: " + path);
        }

        final String baseDir = getBaseDirectory();
        if (baseDir != null && !"/".equals(baseDir)) {
            final String normalizedBase = baseDir.replace("\\", "/").replaceAll("[/]+", "/");
            if (cleanPath.startsWith("/")) {
                if (!cleanPath.startsWith(normalizedBase) && !cleanPath.startsWith("/" + normalizedBase)) {
                    throw new IllegalArgumentException("Path [" + path
                            + "] attempts to modify or escape outside the base directory [" + baseDir + "]");
                }
                if (cleanPath.startsWith(normalizedBase)) {
                    cleanPath = cleanPath.substring(normalizedBase.length());
                } else if (cleanPath.startsWith("/" + normalizedBase)) {
                    cleanPath = cleanPath.substring(normalizedBase.length() + 1);
                }
            } else {
                if (cleanPath.startsWith(normalizedBase)) {
                    cleanPath = cleanPath.substring(normalizedBase.length());
                }
            }
        } else {
            if (cleanPath.startsWith("/")) {
                cleanPath = cleanPath.substring(1);
            }
        }

        while (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }

        final int lastSlashIndex = cleanPath.lastIndexOf('/');
        if (lastSlashIndex >= 0) {
            setSubDirectory(cleanPath.substring(0, lastSlashIndex));
            setFilename(cleanPath.substring(lastSlashIndex + 1));
        } else {
            setSubDirectory("");
            setFilename(cleanPath);
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
        try {
            clone.setFilename(getFilename());
        } catch (final Exception e) {
            // filename not set
        }
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
        try {
            clone.setFilename(getFilename());
        } catch (final Exception e) {
            // filename not set
        }
        return clone;
    }

    /**
     * Creates a new instance with the given absolute directory.
     */
    default IFileChannel withAbsoluteDirectory(final String absoluteDirectory) {
        final URI newServerUri = FileChannelInfos.newDirectoryUri(getBaseServerUri(), absoluteDirectory);
        final IFileChannel clone = FileChannelRegistry.newInstance(newServerUri);
        clone.setEmptyFileContent(getEmptyFileContent());
        try {
            clone.setFilename(getFilename());
        } catch (final Exception e) {
            // filename not set
        }
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
        final List<IFileInfo> files = new ArrayList<>();
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
        final List<IFileInfo> directories = new ArrayList<>();
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
            throw new RuntimeException(ex);
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
                throw new RuntimeException("Source file not found: " + this);
            }
            targetChannel.upload(in);
        } catch (final IOException e) {
            throw new RuntimeException(e);
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