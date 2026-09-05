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

import de.invesdwin.context.integration.filechannel.IFileChannel;
import de.invesdwin.context.integration.filechannel.info.path.FileChannelPaths;
import de.invesdwin.context.integration.filechannel.nio.NioFileChannel;
import de.invesdwin.context.integration.filechannel.registry.FileChannelRegistry;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.string.Strings;
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
 */
@NotThreadSafe
public class AtomicNioFileChannel extends NioFileChannel {

    private final AtomicNioFileChannelPath atomicPath;

    public AtomicNioFileChannel(final String serverUri) {
        this(new AtomicNioFileChannelPath(serverUri));
    }

    public AtomicNioFileChannel(final URI serverUri) {
        this(new AtomicNioFileChannelPath(serverUri));
    }

    public AtomicNioFileChannel(final AtomicNioFileChannelPath path) {
        super(path);
        this.atomicPath = path;
    }

    public AtomicNioFileChannelPath getAtomicPath() {
        return atomicPath;
    }

    // --- Override Factories to preserve AtomicNioFileChannel Type ---

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withSubDirectory(final String subDirectory) {
        final AtomicNioFileChannel instance = new AtomicNioFileChannel(atomicPath.derive(getServerUri()));
        //CHECKSTYLE:ON
        instance.setEmptyFileContent(getEmptyFileContent());
        instance.setFilename(getFilename());
        instance.setSubDirectory(subDirectory);
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withBaseServerUri(final URI baseServerUri) {
        //CHECKSTYLE:ON
        final URI newServerUri = FileChannelPaths.newDirectoryUri(baseServerUri, getBaseDirectory());
        //CHECKSTYLE:OFF
        final AtomicNioFileChannel instance = new AtomicNioFileChannel(atomicPath.derive(newServerUri));
        //CHECKSTYLE:ON
        instance.setEmptyFileContent(getEmptyFileContent());
        instance.setSubDirectory(getSubDirectory());
        if (getFilename() != null) {
            instance.setFilename(getFilename());
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
        //CHECKSTYLE:ON
        final URI newServerUri = FileChannelPaths.newDirectoryUri(getBaseServerUri(), baseDirectory);
        //CHECKSTYLE:OFF
        final AtomicNioFileChannel instance = new AtomicNioFileChannel(atomicPath.derive(newServerUri));
        //CHECKSTYLE:ON
        instance.setEmptyFileContent(getEmptyFileContent());
        instance.setSubDirectory(getSubDirectory());
        if (getFilename() != null) {
            instance.setFilename(getFilename());
        }
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withAbsoluteDirectory(final String absoluteDirectory) {
        //CHECKSTYLE:ON
        final URI newServerUri = FileChannelPaths.newDirectoryUri(getBaseServerUri(), absoluteDirectory);
        //CHECKSTYLE:OFF
        final AtomicNioFileChannel instance = new AtomicNioFileChannel(atomicPath.derive(newServerUri));
        //CHECKSTYLE:ON
        instance.setEmptyFileContent(getEmptyFileContent());
        if (getFilename() != null) {
            instance.setFilename(getFilename());
        }
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withSubPath(final String subPath) {
        final AtomicNioFileChannel instance = new AtomicNioFileChannel(atomicPath.derive(getServerUri()));
        //CHECKSTYLE:ON
        instance.setEmptyFileContent(getEmptyFileContent());
        instance.setSubPath(subPath);
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withSubPath(final Path path) {
        final AtomicNioFileChannel instance = new AtomicNioFileChannel(atomicPath.derive(getServerUri()));
        //CHECKSTYLE:ON
        instance.setEmptyFileContent(getEmptyFileContent());
        instance.setSubPath(path);
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withFilename(final String filename) {
        final AtomicNioFileChannel instance = new AtomicNioFileChannel(atomicPath.derive(getServerUri()));
        //CHECKSTYLE:ON
        instance.setEmptyFileContent(getEmptyFileContent());
        instance.setSubDirectory(getSubDirectory());
        instance.setFilename(filename);
        return instance;
    }

    //CHECKSTYLE:OFF
    @Override
    public AtomicNioFileChannel withAbsolutePath(final String path) {
        //CHECKSTYLE:ON
        if (Strings.isBlank(path)) {
            //CHECKSTYLE:OFF
            final AtomicNioFileChannel instance = new AtomicNioFileChannel(atomicPath.derive(getBaseServerUri()));
            //CHECKSTYLE:ON
            instance.setEmptyFileContent(getEmptyFileContent());
            instance.setSubPath((String) null);
            return instance;
        }
        if (path.contains("://")) {
            final IFileChannel registryChannel = FileChannelRegistry.newInstance(path);
            if (registryChannel instanceof AtomicNioFileChannel) {
                return (AtomicNioFileChannel) registryChannel;
            }
            return (AtomicNioFileChannel) registryChannel;
        } else {
            //CHECKSTYLE:OFF
            final AtomicNioFileChannel instance = new AtomicNioFileChannel(atomicPath.derive(getBaseServerUri()));
            //CHECKSTYLE:ON
            instance.setEmptyFileContent(getEmptyFileContent());
            instance.setSubPath(path);
            return instance;
        }
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
        atomicPath.maybeRunCleanup();
        try {
            final Path targetPath = Paths.get(getFileUri());
            final Path tempPath = targetPath.resolveSibling(
                    Files.normalizeFilename(targetPath.getFileName().toString() + AtomicNioFileChannelPath.TMP_SUFFIX));
            Files.copy(file.toPath(), tempPath, StandardCopyOption.REPLACE_EXISTING);
            Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
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
        atomicPath.maybeRunCleanup();
        try {
            final Path targetPath = Paths.get(getFileUri());
            final Path tempPath = targetPath.resolveSibling(
                    Files.normalizeFilename(targetPath.getFileName().toString() + AtomicNioFileChannelPath.TMP_SUFFIX));
            Files.copy(input, tempPath, StandardCopyOption.REPLACE_EXISTING);
            Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
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
        atomicPath.maybeRunCleanup();
        try {
            final Path targetPath = Paths.get(getFileUri());
            final Path tempPath = targetPath.resolveSibling(
                    Files.normalizeFilename(targetPath.getFileName().toString() + AtomicNioFileChannelPath.TMP_SUFFIX));

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
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}