package de.invesdwin.context.integration.filechannel.nio.atomic;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.filechannel.IFileChannel;
import de.invesdwin.context.integration.filechannel.info.path.FileChannelPaths;
import de.invesdwin.context.integration.filechannel.info.path.IFileChannelPath;
import de.invesdwin.util.concurrent.lock.file.FileChannelLock;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

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
public class AtomicNioFileChannelContext implements Cloneable {

    public static final String TMP_EXTENSION = FileChannelLock.TMP_EXTENSION;
    public static final String TMP_SUFFIX = FileChannelLock.TMP_SUFFIX;

    public static final long UNINITIALIZED_DIRECTORY_CLEANUP_TIME = -1L;
    public static final String CLEANUP_MARKER_FILENAME = ".cleanup";
    public static final Duration CLEANUP_INTERVAL = Duration.ONE_DAY;
    public static final Duration STALE_TEMP_FILE_AGE = new Duration(12, FTimeUnit.HOURS);

    @GuardedBy("this")
    private Path directoryPath;
    @GuardedBy("this")
    private AtomicLong directoryCleanupTime;

    @Override
    protected AtomicNioFileChannelContext clone() {
        try {
            return (AtomicNioFileChannelContext) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void maybeRunCleanup(final IFileChannel fileChannel) {
        attach(fileChannel);

        final FDate now = FDate.now();
        long last = directoryCleanupTime.get();

        // Lazy initialization block
        if (last == UNINITIALIZED_DIRECTORY_CLEANUP_TIME) {
            last = newInitialCleanupTime(this.directoryPath);
            // Attempt to set it. If another thread already initialized it (or ran a cleanup),
            // this safely fails and we re-fetch the latest value below.
            directoryCleanupTime.compareAndSet(UNINITIALIZED_DIRECTORY_CLEANUP_TIME, last);
            last = directoryCleanupTime.get();
        }

        if (CLEANUP_INTERVAL.isGreaterThanMillis(now.millisValue() - last)) {
            return;
        }

        final Path markerPath = directoryPath.resolve(CLEANUP_MARKER_FILENAME);
        if (Files.exists(markerPath)) {
            final long lastModified = Files.lastModifiedNoThrow(markerPath);
            if (CLEANUP_INTERVAL.isGreaterThanMillis(now.millisValue() - lastModified)) {
                directoryCleanupTime.set(lastModified);
                return;
            }
        }

        try {
            // Inline atomic write for the marker path
            final Path tempMarkerPath = markerPath
                    .resolveSibling(Files.normalizeFilename(markerPath.getFileName().toString() + TMP_SUFFIX));
            Files.writeString(tempMarkerPath, now.toString());
            Files.move(tempMarkerPath, markerPath, StandardCopyOption.REPLACE_EXISTING);

            directoryCleanupTime.set(now.millisValue());
            cleanupStaleTempFiles();
        } catch (final IOException e) {
            directoryCleanupTime.set(now.millisValue());
        }
    }

    private void attach(final IFileChannel fileChannel) {
        final Path newDirectoryPath = FileChannelPaths.toPath(fileChannel.getDirectoryUri());
        if (!Objects.equals(newDirectoryPath, directoryPath)) {
            directoryPath = newDirectoryPath;
            directoryCleanupTime = new AtomicLong(UNINITIALIZED_DIRECTORY_CLEANUP_TIME);
        }
    }

    private long newInitialCleanupTime(final Path dir) {
        final Path markerPath = dir.resolve(CLEANUP_MARKER_FILENAME);
        if (Files.exists(markerPath)) {
            return Files.lastModifiedNoThrow(markerPath);
        }
        return 0;
    }

    private void cleanupStaleTempFiles() {
        Files.cleanupStaleTempFiles(directoryPath, STALE_TEMP_FILE_AGE, TMP_EXTENSION);
    }

}