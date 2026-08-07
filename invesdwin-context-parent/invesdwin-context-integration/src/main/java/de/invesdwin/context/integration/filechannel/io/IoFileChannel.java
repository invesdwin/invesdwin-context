package de.invesdwin.context.integration.filechannel.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.integration.filechannel.IFileChannel;
import de.invesdwin.context.integration.filechannel.info.FileChannelInfos;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.UUIDs;
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
        this.serverUri = serverUri != null ? serverUri : DEFAULT_SERVER_URI;
        this.baseServerUri = FileChannelInfos.extractBaseServerUri(this.serverUri, DEFAULT_SERVER_URI);
        this.baseDirectory = FileChannelInfos.extractBaseDirectory(this.serverUri);
    }

    public IoFileChannel(final String serverUri) {
        this(serverUri == null ? null : URIs.asUri(serverUri));
    }

    //CHECKSTYLE:OFF
    @Override
    public IFileChannel withSubDirectory(final String subDirectory) {
        //CHECKSTYLE:ON
        final IoFileChannel instance = new IoFileChannel(serverUri);
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
    public IoFileChannel setSubDirectory(final String subDirectory) {
        this.subDirectory = subDirectory != null ? subDirectory : "";
        createDirectoryIfNotExists();
        return this;
    }

    @Override
    public IoFileChannel setFilename(final String filename) {
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
    public IoFileChannel connect() {
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
        return resolveFile().exists();
    }

    @Override
    public long length() {
        assertConnected();
        final File file = resolveFile();
        if (file.exists()) {
            return file.length();
        }
        return -1;
    }

    @Override
    public FDate lastModified() {
        assertConnected();
        final File file = resolveFile();
        if (file.exists()) {
            return new FDate(file.lastModified());
        }
        return null;
    }

    @Override
    public IoFileInfo info() {
        assertConnected();
        final File file = resolveFile();
        if (file.exists()) {
            return IoFileInfo.valueOf(serverUri, baseServerUri, baseDirectory, subDirectory, file);
        }
        return null;
    }

    @Override
    public List<IoFileInfo> list() {
        assertConnected();
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

    private void assertConnected() {
        Assertions.checkTrue(isConnected(), "Please call connect() first");
    }

    private void createDirectoryIfNotExists() {
        if (isConnected()) {
            try {
                Files.forceMkdir(resolveDirectory());
            } catch (final IOException e) {
                throw new RuntimeException("Failed to create directory structure at " + resolveDirectory(), e);
            }
        }
    }

    private File resolveDirectory() {
        return new File(FileChannelInfos.newDirectoryUri(baseServerUri, getAbsoluteDirectory()));
    }

    private File resolveFile() {
        return new File(resolveDirectory(), getFilename());
    }

    @Override
    public IoFileChannel rename(final String filename) {
        assertConnected();
        final File source = resolveFile();
        final File target = new File(resolveDirectory(), filename);
        if (!source.renameTo(target)) {
            throw new RuntimeException("Failed to rename file from " + source + " to " + target);
        }
        setFilename(filename);
        return this;
    }

    @Override
    public IoFileChannel upload(final File file) {
        assertConnected();
        try (InputStream in = new FileInputStream(file); OutputStream out = new FileOutputStream(resolveFile())) {
            copyStream(in, out);
            return this;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IoFileChannel upload(final byte[] bytes) {
        assertConnected();
        try (FileOutputStream out = new FileOutputStream(resolveFile())) {
            out.write(bytes);
            return this;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IoFileChannel upload(final InputStream input) {
        assertConnected();
        try (OutputStream out = new FileOutputStream(resolveFile())) {
            copyStream(input, out);
            return this;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.close(input);
        }
    }

    @Override
    public IoFileChannel download(final File destination) {
        assertConnected();
        final File source = resolveFile();
        if (source.exists()) {
            try {
                Files.forceMkdirParent(destination);
                try (InputStream in = new FileInputStream(source);
                        OutputStream out = new FileOutputStream(destination)) {
                    copyStream(in, out);
                }
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    @Override
    public byte[] download() {
        assertConnected();
        final File source = resolveFile();
        if (!source.exists()) {
            return null;
        }

        try (FileInputStream in = new FileInputStream(source)) {
            final byte[] data = new byte[(int) source.length()];
            int bytesRead = 0;
            while (bytesRead < data.length) {
                final int result = in.read(data, bytesRead, data.length - bytesRead);
                if (result == -1) {
                    break;
                }
                bytesRead += result;
            }
            return data;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IoFileChannel delete() {
        assertConnected();
        Files.deleteQuietly(resolveFile());
        return this;
    }

    @Override
    public void close() {
        connected = false;
    }

    @Override
    public OutputStream uploadOutputStream() {
        assertConnected();
        try {
            return new FileOutputStream(resolveFile());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream downloadInputStream() {
        assertConnected();
        final File source = resolveFile();
        if (source.exists()) {
            try {
                return new FileInputStream(source);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public File getLocalTempFile() {
        try {
            return resolveFile();
        } catch (final UnsupportedOperationException | IllegalArgumentException e) {
            final File directory = new File(ContextProperties.TEMP_DIRECTORY, getAbsoluteDirectory());
            try {
                Files.forceMkdir(directory);
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
            final File file = new File(directory, getFilename());
            Files.deleteQuietly(file);
            return file;
        }
    }

    @Override
    public IoFileChannel reconnect() {
        close();
        connect();
        return this;
    }

    @Override
    public String toString() {
        return FileChannelInfos.toString(this);
    }

    private void copyStream(final InputStream in, final OutputStream out) throws IOException {
        final byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}