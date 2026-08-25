package de.invesdwin.context.integration.filechannel.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.IOUtils;

import de.invesdwin.context.integration.filechannel.IFileChannel;
import de.invesdwin.context.integration.filechannel.info.path.FileChannelPath;
import de.invesdwin.context.integration.filechannel.info.path.FileChannelPaths;
import de.invesdwin.context.integration.filechannel.registry.FileChannelRegistry;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.UUIDs;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.closeable.Closeables;
import de.invesdwin.util.time.date.FDate;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

@NotThreadSafe
public class IoFileChannel implements IFileChannel {

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

    public IoFileChannel(final File file) {
        this(DEFAULT_SERVER_URI);
        if (file != null) {
            if (file.getParent() != null) {
                setSubDirectory(file.getParent());
            }
            setFilename(file.getName());
        }
    }

    public IoFileChannel() {
        this(DEFAULT_SERVER_URI);
    }

    public IoFileChannel(final URI serverUri) {
        final FileChannelPath path = FileChannelPath.valueOf(serverUri, DEFAULT_SERVER_URI);
        this.serverUri = path.getServerUri();
        this.baseServerUri = path.getBaseServerUri();
        this.baseDirectory = path.getAbsoluteDirectory();
        this.filename = path.getFilename();
    }

    public IoFileChannel(final String serverUri) {
        this(serverUri == null ? null : URIs.asUri(serverUri));
    }

    //CHECKSTYLE:OFF
    @Override
    public IoFileChannel withSubDirectory(final String subDirectory) {
        final IoFileChannel instance = new IoFileChannel(serverUri);
        //CHECKSTYLE:ON
        instance.emptyFileContent = emptyFileContent;
        instance.filename = filename;
        instance.setSubDirectory(subDirectory);
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public IoFileChannel withBaseServerUri(final URI baseServerUri) {
        //CHECKSTYLE:ON
        final URI newServerUri = FileChannelPaths.newDirectoryUri(baseServerUri, getBaseDirectory());
        //CHECKSTYLE:OFF
        final IoFileChannel instance = new IoFileChannel(newServerUri);
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
    public IoFileChannel withBaseServerUri(final String baseServerUri) {
        //CHECKSTYLE:ON
        return withBaseServerUri(URIs.asUri(baseServerUri));
    }

    //CHECKSTYLE:OFF
    @Override
    public IoFileChannel withBaseDirectory(final String baseDirectory) {
        //CHECKSTYLE:ON
        final URI newServerUri = FileChannelPaths.newDirectoryUri(getBaseServerUri(), baseDirectory);
        //CHECKSTYLE:OFF
        final IoFileChannel instance = new IoFileChannel(newServerUri);
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
    public IoFileChannel withAbsoluteDirectory(final String absoluteDirectory) {
        //CHECKSTYLE:ON
        final URI newServerUri = FileChannelPaths.newDirectoryUri(getBaseServerUri(), absoluteDirectory);
        //CHECKSTYLE:OFF
        final IoFileChannel instance = new IoFileChannel(newServerUri);
        //CHECKSTYLE:ON
        instance.emptyFileContent = emptyFileContent;
        if (getFilename() != null) {
            instance.setFilename(getFilename());
        }
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public IoFileChannel withSubPath(final String subPath) {
        final IoFileChannel instance = new IoFileChannel(serverUri);
        //CHECKSTYLE:ON
        instance.emptyFileContent = emptyFileContent;
        instance.setSubPath(subPath);
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public IoFileChannel withSubPath(final Path path) {
        final IoFileChannel instance = new IoFileChannel(serverUri);
        //CHECKSTYLE:ON
        instance.emptyFileContent = emptyFileContent;
        instance.setSubPath(path);
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public IoFileChannel withFilename(final String filename) {
        final IoFileChannel instance = new IoFileChannel(serverUri);
        //CHECKSTYLE:ON
        instance.emptyFileContent = emptyFileContent;
        instance.setSubDirectory(getSubDirectory());
        instance.setFilename(filename);
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public IoFileChannel withAbsolutePath(final String path) {
        //CHECKSTYLE:ON
        if (Strings.isBlank(path)) {
            //CHECKSTYLE:OFF
            final IoFileChannel instance = new IoFileChannel(getBaseServerUri());
            //CHECKSTYLE:ON
            instance.emptyFileContent = emptyFileContent;
            instance.setSubPath((String) null);
            return instance;
        }
        if (path.contains("://")) {
            return (IoFileChannel) FileChannelRegistry.newInstance(path);
        } else {
            //CHECKSTYLE:OFF
            final IoFileChannel instance = new IoFileChannel(getBaseServerUri());
            //CHECKSTYLE:ON
            instance.emptyFileContent = emptyFileContent;
            instance.setSubPath(path);
            return instance;
        }
    }

    //CHECKSTYLE:OFF
    @Override
    public IoFileChannel withAbsolutePath(final Path path) {
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
    public IoFileChannel setSubDirectory(final String subDirectory) {
        final String newSubDirectory = subDirectory != null ? subDirectory : "";
        if (!this.subDirectory.equals(newSubDirectory)) {
            this.subDirectory = newSubDirectory;
            this.directoryCreated = false;
        }
        return this;
    }

    @Override
    public IoFileChannel setFilename(final String filename) {
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
    public IoFileChannel setEmptyFileContent(final byte[] emptyFileContent) {
        this.emptyFileContent = emptyFileContent;
        return this;
    }

    @Override
    public IoFileChannel createUniqueFile() {
        return createUniqueFile(IoFileChannel.class.getSimpleName() + "_", ".channel");
    }

    @Override
    public IoFileChannel createUniqueFile(final String filenamePrefix, final String filenameSuffix) {
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
    public IoFileChannel connect() {
        return connect(true);
    }

    @Override
    public IoFileChannel connect(final boolean createDirectory) {
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
            return resolveDirectory().exists();
        }
        return resolveFile().exists();
    }

    @Override
    public long length() {
        connect(false);
        final File file = resolveFile();
        if (file.exists()) {
            return file.length();
        }
        return -1;
    }

    @Override
    public FDate lastModified() {
        connect(false);
        final File file = resolveFile();
        if (file.exists()) {
            return new FDate(file.lastModified());
        }
        return null;
    }

    @Override
    public IoFileInfo info() {
        connect(false);
        final File file = resolveFile();
        if (file.exists()) {
            return IoFileInfo.valueOf(serverUri, baseServerUri, baseDirectory, subDirectory, file);
        }
        return null;
    }

    @Override
    public List<IoFileInfo> list() {
        connect(false);
        final File dir = resolveDirectory();
        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptyList();
        }

        final File[] files = dir.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(files)
                .map(file -> IoFileInfo.valueOf(serverUri, baseServerUri, baseDirectory, subDirectory, file))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IoFileInfo> listFiles() {
        return (List<IoFileInfo>) IFileChannel.super.listFiles();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IoFileInfo> listDirectories() {
        return (List<IoFileInfo>) IFileChannel.super.listDirectories();
    }

    private void ensureDirectoryCreated() {
        connect(false);
        if (!directoryCreated) {
            createDirectory();
        }
    }

    @Override
    public IoFileChannel createDirectory() {
        connect(false);
        try {
            Files.forceMkdir(resolveDirectory());
            directoryCreated = true;
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to create directory structure at " + resolveDirectory(), e);
        }
        return this;
    }

    private File resolveDirectory() {
        return new File(FileChannelPaths.newDirectoryUri(baseServerUri, getAbsoluteDirectory()));
    }

    private File resolveFile() {
        return new File(resolveDirectory(), getFilename());
    }

    @Override
    public IoFileChannel rename(final String filename) {
        connect(false);
        final File source = resolveFile();
        final File target = new File(resolveDirectory(), filename);
        if (!source.renameTo(target)) {
            throw new IllegalStateException("Failed to rename file from " + source + " to " + target);
        }
        setFilename(filename);
        return this;
    }

    @Deprecated
    @Override
    public void moveSameType(final IFileChannel targetChannel) {
        final IoFileChannel targetIo = (IoFileChannel) targetChannel;
        targetIo.ensureDirectoryCreated();
        final File source = resolveFile();
        final File target = targetIo.resolveFile();
        try {
            Files.forceMkdirParent(target);
            if (!source.renameTo(target)) {
                Files.copyFile(source, target);
                Files.deleteQuietly(source);
            }
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to move file from " + source + " to " + target, e);
        }
        setSubDirectory(targetIo.getSubDirectory());
        setFilename(targetIo.getFilename());
    }

    @Override
    public IoFileChannel upload(final File file) {
        ensureDirectoryCreated();
        try (InputStream in = new FileInputStream(file); OutputStream out = new FileOutputStream(resolveFile())) {
            IOUtils.copyLarge(in, out);
            return this;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public IoFileChannel upload(final byte[] bytes) {
        ensureDirectoryCreated();
        try (FileOutputStream out = new FileOutputStream(resolveFile())) {
            out.write(bytes);
            return this;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public IoFileChannel upload(final InputStream input) {
        ensureDirectoryCreated();
        try (OutputStream out = new FileOutputStream(resolveFile())) {
            IOUtils.copyLarge(input, out);
            return this;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            Closeables.close(input);
        }
    }

    @Override
    public IoFileChannel download(final File destination) {
        connect(false);
        try (InputStream in = new FileInputStream(resolveFile())) {
            Files.forceMkdirParent(destination);
            try (OutputStream out = new FileOutputStream(destination)) {
                IOUtils.copyLarge(in, out);
            }
        } catch (final FileNotFoundException e) {
            // Do nothing if source does not exist
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    @Override
    public byte[] downloadBytes() {
        connect(false);
        try (FileInputStream in = new FileInputStream(resolveFile())) {
            return IOUtils.toByteArray(in);
        } catch (final FileNotFoundException e) {
            return null;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public IoFileChannel delete() {
        connect(false);
        Files.deleteQuietly(resolveFile());
        return this;
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
            return new FileOutputStream(resolveFile());
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public InputStream newDownload() {
        connect(false);
        try {
            return new FileInputStream(resolveFile());
        } catch (final FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public IoFileChannel reconnect(final boolean createDirectory) {
        close();
        connect(createDirectory);
        return this;
    }

    @Override
    public String toString() {
        return FileChannelPaths.toString(this);
    }
}