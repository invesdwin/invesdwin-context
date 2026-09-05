package de.invesdwin.context.integration.filechannel.nio.atomic;

import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.filechannel.info.path.FileChannelPath;
import de.invesdwin.context.integration.filechannel.info.path.FileChannelPaths;
import de.invesdwin.context.integration.filechannel.info.path.IFileChannelPath;
import de.invesdwin.instrument.DynamicInstrumentationProperties;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.string.Charsets;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.millis.FDateMillis;

/**
 * Encapsulates the path context and background maintenance duties for an {@link AtomicNioFileChannel}.
 * 
 * <p>
 * <b>Pattern used:</b> Employs a lazily-initialized background cleanup routine shared via memory derivation. To avoid
 * unbounded accumulation of stale temporary files (which can happen if a process crashes mid-write), it occasionally
 * audits the directory. Reuses internal state via {@link #derive(IFileChannelPath)} when iterating over siblings to
 * prevent redundant timestamp tracking or repeated file I/O operations for directory cleanup.
 */
@ThreadSafe
public class AtomicNioFileChannelPath implements IFileChannelPath {

    public static final String TMP_EXTENSION = ".tmp";
    public static final String TMP_SUFFIX = "_"
            + Files.normalizePath(DynamicInstrumentationProperties.getManagementName())
            + AtomicNioFileChannelPath.TMP_EXTENSION;
    private static final long UNINITIALIZED_DIRECTORY_CLEANUP_TIME = -1L;
    private static final long CLEANUP_INTERVAL_MILLIS = 24 * FTimeUnit.MILLISECONDS_IN_HOUR;
    private static final long STALE_TEMP_FILE_AGE_MILLIS = 12 * FTimeUnit.MILLISECONDS_IN_HOUR;
    private static final String CLEANUP_MARKER_FILENAME = ".cleanup";

    private final IFileChannelPath path;
    private final AtomicLong directoryCleanupTime;
    private final Path directoryPath;

    public AtomicNioFileChannelPath(final String serverUri) {
        this(URIs.asUri(serverUri));
    }

    public AtomicNioFileChannelPath(final URI serverUri) {
        this(FileChannelPath.valueOf(serverUri, AtomicNioFileChannel.DEFAULT_SERVER_URI_F));
    }

    public AtomicNioFileChannelPath(final IFileChannelPath path) {
        this.path = path;
        final URI directoryUri = FileChannelPaths.newDirectoryUri(path.getBaseServerUri(), path.getAbsoluteDirectory());
        this.directoryPath = Paths.get(directoryUri);
        // Initialize lazily with -1L to avoid File I/O in the constructor
        this.directoryCleanupTime = new AtomicLong(UNINITIALIZED_DIRECTORY_CLEANUP_TIME);
    }

    private AtomicNioFileChannelPath(final IFileChannelPath path, final AtomicLong directoryCleanupTime,
            final Path directoryPath) {
        this.path = path;
        this.directoryCleanupTime = directoryCleanupTime;
        this.directoryPath = directoryPath;
    }

    public AtomicNioFileChannelPath derive(final URI newServerUri) {
        return derive(FileChannelPath.valueOf(newServerUri, AtomicNioFileChannel.DEFAULT_SERVER_URI_F));
    }

    public AtomicNioFileChannelPath derive(final IFileChannelPath newPath) {
        if (Objects.equals(getAbsoluteDirectory(), newPath.getAbsoluteDirectory())) {
            // Reuse the existing AtomicLong and parsed Path since the directory hasn't changed
            return new AtomicNioFileChannelPath(newPath, this.directoryCleanupTime, this.directoryPath);
        }
        return new AtomicNioFileChannelPath(newPath);
    }

    @Override
    public URI getServerUri() {
        return path.getServerUri();
    }

    @Override
    public URI getBaseServerUri() {
        return path.getBaseServerUri();
    }

    @Override
    public String getAbsoluteDirectory() {
        return path.getAbsoluteDirectory();
    }

    @Override
    public String getFilename() {
        return path.getFilename();
    }

    public void maybeRunCleanup() {
        final long now = FDateMillis.nowMillis();
        long last = directoryCleanupTime.get();

        // Lazy initialization block
        if (last == UNINITIALIZED_DIRECTORY_CLEANUP_TIME) {
            last = newInitialCleanupTime(this.directoryPath);
            // Attempt to set it. If another thread already initialized it (or ran a cleanup),
            // this safely fails and we re-fetch the latest value below.
            directoryCleanupTime.compareAndSet(UNINITIALIZED_DIRECTORY_CLEANUP_TIME, last);
            last = directoryCleanupTime.get();
        }

        if (now - last < CLEANUP_INTERVAL_MILLIS) {
            return;
        }

        final Path markerPath = directoryPath.resolve(CLEANUP_MARKER_FILENAME);
        try {
            if (Files.exists(markerPath)) {
                final long lastModified = Files.getLastModifiedTime(markerPath).toMillis();
                if (now - lastModified < CLEANUP_INTERVAL_MILLIS) {
                    directoryCleanupTime.set(now);
                    return;
                }
            }
        } catch (final IOException e) {
            // Ignore and proceed to attempt claiming the slot
        }

        try {
            // Inline atomic write for the marker path
            final Path tempMarkerPath = markerPath
                    .resolveSibling(Files.normalizeFilename(markerPath.getFileName().toString() + TMP_SUFFIX));
            Files.write(tempMarkerPath, String.valueOf(now).getBytes(Charsets.defaultCharset()));
            Files.move(tempMarkerPath, markerPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);

            directoryCleanupTime.set(now);
            cleanupStaleTempFiles();
        } catch (final IOException e) {
            directoryCleanupTime.set(now);
        }
    }

    private long newInitialCleanupTime(final Path dir) {
        final Path markerPath = dir.resolve(CLEANUP_MARKER_FILENAME);
        try {
            if (Files.exists(markerPath)) {
                return Files.getLastModifiedTime(markerPath).toMillis();
            }
        } catch (final IOException e) {
            // Fall back to 0L if reading the timestamp fails
        }
        return 0;
    }

    private void cleanupStaleTempFiles() {
        final long now = FDateMillis.nowMillis();
        cleanupDirectory(directoryPath, now);
    }

    private boolean cleanupDirectory(final Path dir, final long now) {
        boolean isEmpty = true;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (final Path p : stream) {
                if (Files.isDirectory(p)) {
                    if (cleanupDirectory(p, now)) {
                        try {
                            Files.deleteIfExists(p);
                        } catch (final IOException e) {
                            isEmpty = false; // Directory could not be removed (e.g. concurrent write)
                        }
                    } else {
                        isEmpty = false;
                    }
                } else {
                    final String fileName = p.getFileName().toString();
                    if (fileName.endsWith(TMP_EXTENSION)) {
                        try {
                            if (now - Files.getLastModifiedTime(p).toMillis() > STALE_TEMP_FILE_AGE_MILLIS) {
                                Files.deleteIfExists(p);
                            } else {
                                isEmpty = false;
                            }
                        } catch (final NoSuchFileException e) {
                            // Concurrently deleted by another process
                        } catch (final IOException e) {
                            isEmpty = false;
                        }
                    } else {
                        isEmpty = false;
                    }
                }
            }
        } catch (final IOException e) {
            isEmpty = false;
        }
        return isEmpty;
    }
}