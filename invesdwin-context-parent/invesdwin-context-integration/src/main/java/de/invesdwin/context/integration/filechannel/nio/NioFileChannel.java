package de.invesdwin.context.integration.filechannel.nio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.integration.filechannel.IFileChannel;
import de.invesdwin.context.integration.filechannel.info.FileChannelInfos;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.UUIDs;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.closeable.Closeables;
import de.invesdwin.util.time.date.FDate;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

@NotThreadSafe
public class NioFileChannel implements IFileChannel {

    public static final String DEFAULT_SERVER_URI = "file://";

    private final String serverUri;
    private String directory;
    private String filename;
    private byte[] emptyFileContent = Bytes.EMPTY_ARRAY;
    private boolean connected = false;

    public NioFileChannel() {
        this(DEFAULT_SERVER_URI);
    }

    public NioFileChannel(final URI serverUri) {
        this(serverUri != null ? serverUri.toString() : DEFAULT_SERVER_URI);
    }

    public NioFileChannel(final String serverUri) {
        if (serverUri == null) {
            this.serverUri = DEFAULT_SERVER_URI;
        } else {
            this.serverUri = Strings.removeEnd(serverUri, "/");
        }
    }

    public NioFileChannel(final URI serverUri, final String directory) {
        this(serverUri != null ? serverUri.toString() : DEFAULT_SERVER_URI, directory);
    }

    public NioFileChannel(final String serverUri, final String directory) {
        this(serverUri);
        if (directory != null) {
            setDirectory(directory);
        }
    }

    //CHECKSTYLE:OFF
    @Override
    public IFileChannel withDirectory(final String directory) {
        //CHECKSTYLE:ON
        final NioFileChannel instance = new NioFileChannel(getServerUri(), directory);
        instance.emptyFileContent = emptyFileContent;
        instance.filename = filename;
        return instance;
    }

    @Override
    public String getServerUri() {
        return serverUri;
    }

    @Override
    public String getDirectory() {
        if (directory == null) {
            throw new NullPointerException("please call setDirectory(...) first");
        }
        return directory;
    }

    @Override
    public void setDirectory(final String directory) {
        if (directory == null) {
            this.directory = null;
        } else {
            this.directory = Strings
                    .putSuffix(Strings.putPrefix(directory.replace("\\", "/").replaceAll("[/]+", "/"), "/"), "/");
            createDirectoryIfNotExists();
        }
    }

    @Override
    public void setFilename(final String filename) {
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        if (filename == null) {
            throw new NullPointerException("please call setFilename(...) first");
        }
        return filename;
    }

    @Override
    public byte[] getEmptyFileContent() {
        return emptyFileContent;
    }

    @Override
    public void setEmptyFileContent(final byte[] emptyFileContent) {
        this.emptyFileContent = emptyFileContent;
    }

    @Override
    public void createUniqueFile() {
        createUniqueFile(NioFileChannel.class.getSimpleName() + "_", ".channel");
    }

    @Override
    public void createUniqueFile(final String filenamePrefix, final String filenameSuffix) {
        assertConnected();
        while (true) {
            final String filename = filenamePrefix + UUIDs.newPseudoRandomUUID() + filenameSuffix;
            setFilename(filename);
            if (!exists()) {
                upload(new FastByteArrayInputStream(getEmptyFileContent()));
                Assertions.checkTrue(exists());
                break;
            }
        }
    }

    @Override
    public void connect() {
        connected = true;
        createDirectoryIfNotExists();
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean exists() {
        assertConnected();
        return Files.exists(resolveFilePath());
    }

    @Override
    public long length() {
        assertConnected();
        try {
            final Path path = resolveFilePath();
            if (Files.exists(path)) {
                return Files.size(path);
            }
            return -1;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FDate lastModified() {
        assertConnected();
        try {
            final Path path = resolveFilePath();
            if (Files.exists(path)) {
                return new FDate(Files.getLastModifiedTime(path).toMillis());
            }
            return null;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NioFileInfo info() {
        assertConnected();
        final Path path = resolveFilePath();
        if (Files.exists(path)) {
            return NioFileInfo.valueOf(getServerUri(), getDirectory(), path);
        }
        return null;
    }

    @Override
    public List<NioFileInfo> list() {
        assertConnected();
        final Path dirPath = resolveDirectoryPath();
        if (!Files.exists(dirPath)) {
            return java.util.Collections.emptyList();
        }

        try (Stream<Path> stream = Files.list(dirPath)) {
            return stream.map(path -> NioFileInfo.valueOf(getServerUri(), getDirectory(), path))
                    .collect(Collectors.toList());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<NioFileInfo> listFiles() {
        return (List<NioFileInfo>) IFileChannel.super.listFiles();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<NioFileInfo> listDirectories() {
        return (List<NioFileInfo>) IFileChannel.super.listDirectories();
    }

    private void assertConnected() {
        Assertions.checkTrue(isConnected(), "Please call connect() first");
    }

    private void createDirectoryIfNotExists() {
        if (this.directory != null && isConnected()) {
            try {
                Files.createDirectories(resolveDirectoryPath());
            } catch (final IOException e) {
                throw new RuntimeException("Failed to create directory structure at " + resolveDirectoryPath(), e);
            }
        }
    }

    private Path resolveDirectoryPath() {
        final String uriStr = FileChannelInfos.newDirectoryUri(getServerUri(), getDirectory())
                .replaceAll("(?<!:)/{3,}", "///");
        return Paths.get(URI.create(uriStr));
    }

    private Path resolveFilePath() {
        final String uriStr = FileChannelInfos.newFileUri(getServerUri(), getDirectory(), getFilename())
                .replaceAll("(?<!:)/{3,}", "///");
        return Paths.get(URI.create(uriStr));
    }

    @Override
    public void rename(final String filename) {
        assertConnected();
        try {
            final Path source = resolveFilePath();
            final String targetUriStr = FileChannelInfos.newFileUri(getServerUri(), getDirectory(), filename)
                    .replaceAll("(?<!:)/{3,}", "///");
            final Path target = Paths.get(URI.create(targetUriStr));
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            setFilename(filename);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void upload(final File file) {
        assertConnected();
        try {
            Files.copy(file.toPath(), resolveFilePath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void upload(final byte[] bytes) {
        upload(new FastByteArrayInputStream(bytes));
    }

    @Override
    public void upload(final InputStream input) {
        assertConnected();
        try {
            Files.copy(input, resolveFilePath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.close(input);
        }
    }

    @Override
    public void download(final File destination) {
        assertConnected();
        try {
            final Path source = resolveFilePath();
            if (Files.exists(source)) {
                de.invesdwin.util.lang.Files.forceMkdirParent(destination);
                Files.copy(source, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] download() {
        assertConnected();
        try {
            final Path source = resolveFilePath();
            if (Files.exists(source)) {
                return Files.readAllBytes(source);
            }
            return null;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete() {
        assertConnected();
        try {
            Files.deleteIfExists(resolveFilePath());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        connected = false;
    }

    @Override
    public OutputStream uploadOutputStream() {
        assertConnected();
        try {
            return Files.newOutputStream(resolveFilePath());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream downloadInputStream() {
        assertConnected();
        try {
            final Path source = resolveFilePath();
            if (Files.exists(source)) {
                return Files.newInputStream(source);
            }
            return null;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File getLocalTempFile() {
        final Path path = resolveFilePath();
        try {
            return path.toFile();
        } catch (final UnsupportedOperationException e) {
            final File directory = new File(ContextProperties.TEMP_DIRECTORY, getDirectory());
            try {
                de.invesdwin.util.lang.Files.forceMkdir(directory);
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
            final File file = new File(directory, getFilename());
            de.invesdwin.util.lang.Files.deleteQuietly(file);
            return file;
        }
    }

    @Override
    public void reconnect() {
        close();
        connect();
    }

    @Override
    public String toString() {
        return FileChannelInfos.toString(this);
    }
}