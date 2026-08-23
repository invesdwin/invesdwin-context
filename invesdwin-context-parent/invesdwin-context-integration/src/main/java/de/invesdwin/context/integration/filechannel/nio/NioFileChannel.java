package de.invesdwin.context.integration.filechannel.nio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.filechannel.IFileChannel;
import de.invesdwin.context.integration.filechannel.info.path.FileChannelPath;
import de.invesdwin.context.integration.filechannel.info.path.FileChannelPaths;
import de.invesdwin.context.integration.filechannel.registry.FileChannelRegistry;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.UUIDs;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.closeable.Closeables;
import de.invesdwin.util.time.date.FDate;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

@NotThreadSafe
public class NioFileChannel implements IFileChannel {

    public static final String DEFAULT_SERVER_URI_STR = "file:///";
    public static final URI DEFAULT_SERVER_URI = URI.create(DEFAULT_SERVER_URI_STR);

    private final URI serverUri;
    private final URI baseServerUri;
    private final String baseDirectory;
    private String subDirectory = "";
    private String filename;
    private byte[] emptyFileContent = Bytes.EMPTY_ARRAY;
    private boolean connected = false;
    private boolean directoryCreated = false;

    public NioFileChannel() {
        this(DEFAULT_SERVER_URI);
    }

    public NioFileChannel(final URI serverUri) {
        final FileChannelPath path = FileChannelPath.valueOf(serverUri, DEFAULT_SERVER_URI);
        this.serverUri = path.getServerUri();
        this.baseServerUri = path.getBaseServerUri();
        this.baseDirectory = path.getAbsoluteDirectory();
        this.filename = path.getFilename();
    }

    public NioFileChannel(final String serverUri) {
        this(serverUri == null ? null : URIs.asUri(serverUri));
    }

    //CHECKSTYLE:OFF
    @Override
    public NioFileChannel withSubDirectory(final String subDirectory) {
        final NioFileChannel instance = new NioFileChannel(serverUri);
        //CHECKSTYLE:ON
        instance.emptyFileContent = emptyFileContent;
        instance.filename = filename;
        instance.setSubDirectory(subDirectory);
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public NioFileChannel withBaseServerUri(final URI baseServerUri) {
        //CHECKSTYLE:ON
        final URI newServerUri = FileChannelPaths.newDirectoryUri(baseServerUri, getBaseDirectory());
        //CHECKSTYLE:OFF
        final NioFileChannel instance = new NioFileChannel(newServerUri);
        //CHECKSTYLE:ON
        instance.emptyFileContent = emptyFileContent;
        instance.setSubDirectory(getSubDirectory());
        if (getFilename() != null) {
            instance.setFilename(getFilename());
        }
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public NioFileChannel withBaseServerUri(final String baseServerUri) {
        //CHECKSTYLE:ON
        return withBaseServerUri(URIs.asUri(baseServerUri));
    }

    //CHECKSTYLE:OFF
    @Override
    public NioFileChannel withBaseDirectory(final String baseDirectory) {
        //CHECKSTYLE:ON
        final URI newServerUri = FileChannelPaths.newDirectoryUri(getBaseServerUri(), baseDirectory);
        //CHECKSTYLE:OFF
        final NioFileChannel instance = new NioFileChannel(newServerUri);
        //CHECKSTYLE:ON
        instance.emptyFileContent = emptyFileContent;
        instance.setSubDirectory(getSubDirectory());
        if (getFilename() != null) {
            instance.setFilename(getFilename());
        }
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public NioFileChannel withAbsoluteDirectory(final String absoluteDirectory) {
        //CHECKSTYLE:ON
        final URI newServerUri = FileChannelPaths.newDirectoryUri(getBaseServerUri(), absoluteDirectory);
        //CHECKSTYLE:OFF
        final NioFileChannel instance = new NioFileChannel(newServerUri);
        //CHECKSTYLE:ON
        instance.emptyFileContent = emptyFileContent;
        if (getFilename() != null) {
            instance.setFilename(getFilename());
        }
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public NioFileChannel withSubPath(final String subPath) {
        final NioFileChannel instance = new NioFileChannel(serverUri);
        //CHECKSTYLE:ON
        instance.emptyFileContent = emptyFileContent;
        instance.setSubPath(subPath);
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public NioFileChannel withSubPath(final Path path) {
        final NioFileChannel instance = new NioFileChannel(serverUri);
        //CHECKSTYLE:ON
        instance.emptyFileContent = emptyFileContent;
        instance.setSubPath(path);
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public NioFileChannel withFilename(final String filename) {
        final NioFileChannel instance = new NioFileChannel(serverUri);
        //CHECKSTYLE:ON
        instance.emptyFileContent = emptyFileContent;
        instance.setSubDirectory(getSubDirectory());
        instance.setFilename(filename);
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public NioFileChannel withAbsolutePath(final String path) {
        //CHECKSTYLE:ON
        if (Strings.isBlank(path)) {
            //CHECKSTYLE:OFF
            final NioFileChannel instance = new NioFileChannel(getBaseServerUri());
            //CHECKSTYLE:ON
            instance.emptyFileContent = emptyFileContent;
            instance.setSubPath((String) null);
            return instance;
        }
        if (path.contains("://")) {
            return (NioFileChannel) FileChannelRegistry.newInstance(path);
        } else {
            //CHECKSTYLE:OFF
            final NioFileChannel instance = new NioFileChannel(getBaseServerUri());
            //CHECKSTYLE:ON
            instance.emptyFileContent = emptyFileContent;
            instance.setSubPath(path);
            return instance;
        }
    }

    //CHECKSTYLE:OFF
    @Override
    public NioFileChannel withAbsolutePath(final Path path) {
        //CHECKSTYLE:ON
        return withAbsolutePath(path != null ? path.toString() : null);
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
    public NioFileChannel setSubDirectory(final String subDirectory) {
        final String newSubDirectory = subDirectory != null ? subDirectory : "";
        if (!this.subDirectory.equals(newSubDirectory)) {
            this.subDirectory = newSubDirectory;
            this.directoryCreated = false;
        }
        return this;
    }

    @Override
    public NioFileChannel setFilename(final String filename) {
        this.filename = filename;
        return this;
    }

    @Override
    public String getFilename() {
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
        ensureDirectoryCreated();
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
        return connect(true);
    }

    @Override
    public NioFileChannel connect(final boolean createDirectory) {
        connected = true;
        if (createDirectory && !directoryCreated) {
            createDirectory();
        }
        return this;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean exists() {
        connect(false);
        if (filename == null) {
            return Files.exists(resolveDirectoryPath());
        }
        return Files.exists(resolveFilePath());
    }

    @Override
    public long length() {
        connect(false);
        try {
            return Files.size(resolveFilePath());
        } catch (final NoSuchFileException e) {
            return -1;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public FDate lastModified() {
        connect(false);
        try {
            return new FDate(Files.getLastModifiedTime(resolveFilePath()).toMillis());
        } catch (final NoSuchFileException e) {
            return null;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public NioFileInfo info() {
        connect(false);
        final Path path = resolveFilePath();
        if (Files.exists(path)) {
            return NioFileInfo.valueOf(serverUri, baseServerUri, baseDirectory, subDirectory, path);
        }
        return null;
    }

    @Override
    public List<NioFileInfo> list() {
        connect(false);
        final Path dirPath = resolveDirectoryPath();
        if (!Files.exists(dirPath)) {
            return java.util.Collections.emptyList();
        }

        try (Stream<Path> stream = Files.list(dirPath)) {
            return stream.map(path -> NioFileInfo.valueOf(serverUri, baseServerUri, baseDirectory, subDirectory, path))
                    .collect(Collectors.toList());
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
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

    private void ensureDirectoryCreated() {
        connect(false);
        if (!directoryCreated) {
            createDirectory();
        }
    }

    @Override
    public NioFileChannel createDirectory() {
        connect(false);
        try {
            Files.createDirectories(resolveDirectoryPath());
            directoryCreated = true;
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to create directory structure at " + resolveDirectoryPath(), e);
        }
        return this;
    }

    private Path resolveDirectoryPath() {
        return Paths.get(getDirectoryUri());
    }

    private Path resolveFilePath() {
        return Paths.get(getFileUri());
    }

    @Override
    public NioFileChannel rename(final String filename) {
        connect(false);
        try {
            final Path source = resolveFilePath();
            final Path target = Paths.get(FileChannelPaths.newFileUri(baseServerUri, getAbsoluteDirectory(), filename));
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            setFilename(filename);
            return this;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void moveSameType(final IFileChannel targetChannel) {
        try {
            final NioFileChannel targetNio = (NioFileChannel) targetChannel;
            targetNio.ensureDirectoryCreated();
            final Path source = resolveFilePath();
            final Path target = targetNio.resolveFilePath();
            java.nio.file.Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            setSubDirectory(targetNio.getSubDirectory());
            setFilename(targetNio.getFilename());
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public NioFileChannel upload(final File file) {
        ensureDirectoryCreated();
        try {
            Files.copy(file.toPath(), resolveFilePath(), StandardCopyOption.REPLACE_EXISTING);
            return this;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public NioFileChannel upload(final byte[] bytes) {
        return upload(new FastByteArrayInputStream(bytes));
    }

    @Override
    public NioFileChannel upload(final InputStream input) {
        ensureDirectoryCreated();
        try {
            Files.copy(input, resolveFilePath(), StandardCopyOption.REPLACE_EXISTING);
            return this;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            Closeables.close(input);
        }
    }

    @Override
    public NioFileChannel download(final File destination) {
        connect(false);
        try (InputStream in = Files.newInputStream(resolveFilePath())) {
            de.invesdwin.util.lang.Files.forceMkdirParent(destination);
            Files.copy(in, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (final NoSuchFileException e) {
            // Do nothing if source does not exist
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    @Override
    public byte[] downloadBytes() {
        connect(false);
        try {
            return Files.readAllBytes(resolveFilePath());
        } catch (final NoSuchFileException e) {
            return null;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public NioFileChannel delete() {
        connect(false);
        try {
            Files.deleteIfExists(resolveFilePath());
            return this;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() {
        connected = false;
        directoryCreated = false;
    }

    @Override
    public OutputStream newUpload() {
        ensureDirectoryCreated();
        try {
            return Files.newOutputStream(resolveFilePath());
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public InputStream newDownload() {
        connect(false);
        try {
            return Files.newInputStream(resolveFilePath());
        } catch (final NoSuchFileException e) {
            return null;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public NioFileChannel reconnect(final boolean createDirectory) {
        close();
        connect(createDirectory);
        return this;
    }

    @Override
    public String toString() {
        return FileChannelPaths.toString(this);
    }
}