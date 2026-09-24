package de.invesdwin.context.integration.filechannel.nio.atomic;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.filechannel.info.path.FileChannelPath;
import de.invesdwin.context.integration.filechannel.info.path.FileChannelPaths;
import de.invesdwin.context.integration.filechannel.info.path.IFileChannelPath;
import de.invesdwin.context.integration.filechannel.nio.NioFileChannel;
import de.invesdwin.util.concurrent.lock.file.AtomicNioFileChannelContext;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.streams.closeable.Closeables;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

/**
 * A file channel implementation that ensures file write operations are atomic.
 * 
 * <p>
 * <b>Pattern used:</b> Leverages a write-to-temporary-file and atomic-rename strategy. Data is first streamed or copied
 * into a sibling temporary file. Once fully flushed, the file is moved to its final destination. This prevents
 * consumers from reading partial or corrupted data during slow uploads or system crashes.
 * 
 * WARNING: this implementation only handles conflicts between separate processes, within a process, this implementation
 * is not thread-safe and can throw FileAlreadyExistsException when concurrent writes happen to the same temp file.
 */
@NotThreadSafe
public class AtomicNioFileChannel extends NioFileChannel {

    private final AtomicNioFileChannelContext context;

    public AtomicNioFileChannel(final IFileChannelPath path) {
        this(path, new AtomicNioFileChannelContext());
    }

    public AtomicNioFileChannel(final IFileChannelPath path, final AtomicNioFileChannelContext context) {
        super(path);
        this.context = context;
    }

    public AtomicNioFileChannelContext getContext() {
        return context;
    }

    // --- Override Factories to preserve AtomicNioFileChannel Type ---

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withSubDirectory(final String subDirectory) {
        final AtomicNioFileChannel instance = newDirectory(
                FileChannelPaths.newDirectoryUri(getServerUri(), subDirectory), context.clone());
        //CHECKSTYLE:ON
        instance.setEmptyFileContent(getEmptyFileContent());
        instance.setFileName(getFileName());
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withBaseServerUri(final URI baseServerUri) {
        final AtomicNioFileChannel instance = newDirectory(
                FileChannelPaths.newDirectoryUri(baseServerUri, getBaseDirectory()), context.clone());
        //CHECKSTYLE:ON
        instance.setEmptyFileContent(getEmptyFileContent());
        instance.setSubDirectory(getSubDirectory());
        if (getFileName() != null) {
            instance.setFileName(getFileName());
        }
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withBaseServerUri(final String baseServerUri) {
        //CHECKSTYLE:ON
        return withBaseServerUri(URIs.asUri(baseServerUri));
    }

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withBaseDirectory(final String baseDirectory) {
        final AtomicNioFileChannel instance = newDirectory(
                FileChannelPaths.newDirectoryUri(getBaseServerUri(), baseDirectory), context.clone());
        //CHECKSTYLE:ON
        instance.setEmptyFileContent(getEmptyFileContent());
        instance.setSubDirectory(getSubDirectory());
        if (getFileName() != null) {
            instance.setFileName(getFileName());
        }
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withAbsoluteDirectory(final String absoluteDirectory) {
        final AtomicNioFileChannel instance = newDirectory(
                FileChannelPaths.newDirectoryUri(getBaseServerUri(), absoluteDirectory), context.clone());
        //CHECKSTYLE:ON
        instance.setEmptyFileContent(getEmptyFileContent());
        if (getFileName() != null) {
            instance.setFileName(getFileName());
        }
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withSubPath(final String subPath) {
        final AtomicNioFileChannel instance = newDirectory(getServerUri(), context.clone());
        //CHECKSTYLE:ON
        instance.setEmptyFileContent(getEmptyFileContent());
        instance.setSubPath(subPath);
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withSubPath(final Path path) {
        final AtomicNioFileChannel instance = newDirectory(getServerUri(), context.clone());
        //CHECKSTYLE:ON
        instance.setEmptyFileContent(getEmptyFileContent());
        instance.setSubPath(path);
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withFilename(final String filename) {
        final AtomicNioFileChannel instance = newDirectory(getServerUri(), context.clone());
        //CHECKSTYLE:ON
        instance.setEmptyFileContent(getEmptyFileContent());
        instance.setSubDirectory(getSubDirectory());
        instance.setFileName(filename);
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withAbsolutePath(final String path) {
        final AtomicNioFileChannel instance = newDirectory(FileChannelPaths.newDirectoryUri(getBaseServerUri(), path),
                context.clone());
        //CHECKSTYLE:ON
        instance.setEmptyFileContent(getEmptyFileContent());
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withAbsolutePath(final Path path) {
        //CHECKSTYLE:ON
        return withAbsolutePath(path != null ? path.toString() : null);
    }

    // --- Override Upload Operations for Atomic Writes ---

    @Override
    public AtomicNioFileChannel upload(final File file) {
        connect(true);
        maybeRunCleanup();
        try {
            final Path targetPath = Paths.get(getFileUri());
            final String targetFilename = targetPath.getFileName().toString();
            if (targetFilename.endsWith(AtomicNioFileChannelContext.TMP_SUFFIX)) {
                Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                final Path tempPath = targetPath.resolveSibling(
                        Files.normalizeFileName(targetFilename + AtomicNioFileChannelContext.TMP_SUFFIX));
                Files.copy(file.toPath(), tempPath, StandardCopyOption.REPLACE_EXISTING);
                Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            return this;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public AtomicNioFileChannel upload(final byte[] bytes) {
        return upload(new FastByteArrayInputStream(bytes));
    }

    @Override
    public AtomicNioFileChannel upload(final InputStream input) {
        connect(true);
        maybeRunCleanup();
        try {
            final Path targetPath = Paths.get(getFileUri());
            final String targetFilename = targetPath.getFileName().toString();
            if (targetFilename.endsWith(AtomicNioFileChannelContext.TMP_SUFFIX)) {
                Files.copy(input, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                final Path tempPath = targetPath.resolveSibling(
                        Files.normalizeFileName(targetFilename + AtomicNioFileChannelContext.TMP_SUFFIX));
                Files.copy(input, tempPath, StandardCopyOption.REPLACE_EXISTING);
                Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            return this;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            Closeables.close(input);
        }
    }

    @Override
    public OutputStream newUpload() {
        connect(true);
        maybeRunCleanup();
        try {
            final Path targetPath = Paths.get(getFileUri());
            final String targetFilename = targetPath.getFileName().toString();
            if (targetFilename.endsWith(AtomicNioFileChannelContext.TMP_SUFFIX)) {
                return Files.newOutputStream(targetPath);
            } else {
                final Path tempPath = targetPath.resolveSibling(Files.normalizeFileName(
                        targetPath.getFileName().toString() + AtomicNioFileChannelContext.TMP_SUFFIX));

                final OutputStream out = Files.newOutputStream(tempPath);
                return new FilterOutputStream(out) {
                    private boolean closed = false;

                    @Override
                    public void close() throws IOException {
                        if (closed) {
                            return;
                        }
                        closed = true;
                        super.close();
                        Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                };
            }
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void maybeRunCleanup() {
        final Path directoryPath = FileChannelPaths.toPath(getDirectoryUri());
        context.maybeRunCleanup(directoryPath);
    }

    public static AtomicNioFileChannel newInstance(final URI serverUri) {
        return new AtomicNioFileChannel(FileChannelPath.newInstance(serverUri, DEFAULT_SERVER_URI_F));
    }

    public static AtomicNioFileChannel newFile(final URI serverUri) {
        return new AtomicNioFileChannel(FileChannelPath.newFile(serverUri, DEFAULT_SERVER_URI_F));
    }

    public static AtomicNioFileChannel newDirectory(final URI serverUri) {
        return new AtomicNioFileChannel(FileChannelPath.newDirectory(serverUri, DEFAULT_SERVER_URI_F));
    }

    public static AtomicNioFileChannel newInstance(final IFileChannelPath path) {
        return new AtomicNioFileChannel(path);
    }

    public static AtomicNioFileChannel newInstance(final URI serverUri, final AtomicNioFileChannelContext context) {
        return new AtomicNioFileChannel(FileChannelPath.newInstance(serverUri, DEFAULT_SERVER_URI_F), context);
    }

    public static AtomicNioFileChannel newFile(final URI serverUri, final AtomicNioFileChannelContext context) {
        return new AtomicNioFileChannel(FileChannelPath.newFile(serverUri, DEFAULT_SERVER_URI_F), context);
    }

    public static AtomicNioFileChannel newDirectory(final URI serverUri, final AtomicNioFileChannelContext context) {
        return new AtomicNioFileChannel(FileChannelPath.newDirectory(serverUri, DEFAULT_SERVER_URI_F), context);
    }

    public static AtomicNioFileChannel newInstance(final IFileChannelPath path,
            final AtomicNioFileChannelContext context) {
        return new AtomicNioFileChannel(path, context);
    }
}