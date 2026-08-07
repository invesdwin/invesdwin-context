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
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.closeable.Closeables;
import de.invesdwin.util.time.date.FDate;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

@NotThreadSafe
public class NioFileChannel implements IFileChannel {

    public static final String DEFAULT_SERVER_URI_STR = "file://";
    public static final URI DEFAULT_SERVER_URI = URI.create(DEFAULT_SERVER_URI_STR);

    private final URI serverUri;
    private final URI baseServerUri;
    private final String baseDirectory;
    private String subDirectory = "";
    private String filename;
    private byte[] emptyFileContent = Bytes.EMPTY_ARRAY;
    private boolean connected = false;

    public NioFileChannel() {
        this(DEFAULT_SERVER_URI);
    }

    public NioFileChannel(final URI serverUri) {
        this.serverUri = serverUri != null ? serverUri : DEFAULT_SERVER_URI;
        this.baseServerUri = FileChannelInfos.extractBaseServerUri(this.serverUri, DEFAULT_SERVER_URI);
        this.baseDirectory = FileChannelInfos.extractBaseDirectory(this.serverUri);
    }

    public NioFileChannel(final String serverUri) {
        this(serverUri == null ? null : URIs.asUri(serverUri));
    }

    //CHECKSTYLE:OFF
    @Override
    public IFileChannel withSubDirectory(final String subDirectory) {
        //CHECKSTYLE:ON
        final NioFileChannel instance = new NioFileChannel(serverUri);
        instance.emptyFileContent = emptyFileContent;
        instance.filename = filename;
        instance.setSubDirectory(subDirectory);
        return instance;
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
    public String getBaseDirectory() {
        return baseDirectory;
    }

    @Override
    public String getSubDirectory() {
        return subDirectory;
    }

    @Override
    public String getAbsoluteDirectory() {
        return FileChannelInfos.combinePath(baseDirectory, subDirectory);
    }

    @Override
    public NioFileChannel setSubDirectory(final String subDirectory) {
        this.subDirectory = subDirectory != null ? subDirectory : "";
        createDirectoryIfNotExists();
        return this;
    }

    @Override
    public NioFileChannel setFilename(final String filename) {
        this.filename = filename;
        return this;
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
    public NioFileChannel setEmptyFileContent(final byte[] emptyFileContent) {
        this.emptyFileContent = emptyFileContent;
        return this;
    }

    @Override
    public NioFileChannel createUniqueFile() {
        createUniqueFile(NioFileChannel.class.getSimpleName() + "_", ".channel");
        return this;
    }

    @Override
    public NioFileChannel createUniqueFile(final String filenamePrefix, final String filenameSuffix) {
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
        return this;
    }

    @Override
    public NioFileChannel connect() {
        connected = true;
        createDirectoryIfNotExists();
        return this;
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
            return NioFileInfo.valueOf(serverUri, baseServerUri, baseDirectory, subDirectory, path);
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
            return stream.map(path -> NioFileInfo.valueOf(serverUri, baseServerUri, baseDirectory, subDirectory, path))
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
        if (isConnected()) {
            try {
                Files.createDirectories(resolveDirectoryPath());
            } catch (final IOException e) {
                throw new RuntimeException("Failed to create directory structure at " + resolveDirectoryPath(), e);
            }
        }
    }

    private Path resolveDirectoryPath() {
        return Paths.get(getDirectoryUri());
    }

    private Path resolveFilePath() {
        return Paths.get(getFileUri());
    }

    @Override
    public NioFileChannel rename(final String filename) {
        assertConnected();
        try {
            final Path source = resolveFilePath();
            final Path target = Paths.get(FileChannelInfos.newFileUri(baseServerUri, getAbsoluteDirectory(), filename));
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            setFilename(filename);
            return this;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NioFileChannel upload(final File file) {
        assertConnected();
        try {
            Files.copy(file.toPath(), resolveFilePath(), StandardCopyOption.REPLACE_EXISTING);
            return this;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NioFileChannel upload(final byte[] bytes) {
        return upload(new FastByteArrayInputStream(bytes));
    }

    @Override
    public NioFileChannel upload(final InputStream input) {
        assertConnected();
        try {
            Files.copy(input, resolveFilePath(), StandardCopyOption.REPLACE_EXISTING);
            return this;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.close(input);
        }
    }

    @Override
    public NioFileChannel download(final File destination) {
        assertConnected();
        try {
            final Path source = resolveFilePath();
            if (Files.exists(source)) {
                de.invesdwin.util.lang.Files.forceMkdirParent(destination);
                Files.copy(source, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return this;
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
    public NioFileChannel delete() {
        assertConnected();
        try {
            Files.deleteIfExists(resolveFilePath());
            return this;
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
            final File directory = new File(ContextProperties.TEMP_DIRECTORY, getAbsoluteDirectory());
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
    public NioFileChannel reconnect() {
        close();
        connect();
        return this;
    }

    @Override
    public String toString() {
        return FileChannelInfos.toString(this);
    }
}