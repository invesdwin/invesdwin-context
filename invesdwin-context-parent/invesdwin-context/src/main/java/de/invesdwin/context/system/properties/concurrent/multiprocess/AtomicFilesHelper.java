package de.invesdwin.context.system.properties.concurrent.multiprocess;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.instrument.DynamicInstrumentationProperties;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.string.Charsets;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.millis.FDateMillis;

@ThreadSafe
public class AtomicFilesHelper {

    private static final long CLEANUP_INTERVAL_MILLIS = 24 * FTimeUnit.MILLISECONDS_IN_HOUR;
    private static final long STALE_TEMP_FILE_AGE_MILLIS = 12 * FTimeUnit.MILLISECONDS_IN_HOUR;
    private static final String CLEANUP_MARKER_FILENAME = ".cleanup";

    private static final String TMP_EXTENSION = ".tmp";
    private static final String TMP_SUFFIX = "_"
            + Files.normalizePath(DynamicInstrumentationProperties.getManagementName()) + TMP_EXTENSION;
    private final Path folder;
    private final AtomicLong lastCleanupTime;

    public AtomicFilesHelper(final File folder) {
        this(folder.toPath());
    }

    public AtomicFilesHelper(final Path folder) {
        this.folder = folder;
        try {
            Files.createDirectories(this.folder);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to create properties directory: " + this.folder, e);
        }
        final long initialCleanupTime = newInitialCleanupTime();
        this.lastCleanupTime = new AtomicLong(initialCleanupTime);
        maybeRunCleanup();
    }

    public Path getFolder() {
        return folder;
    }

    private void prepareParentDirectory(final Path targetPath) throws IOException {
        final Path parent = targetPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    public void writeAtomic(final File targetPath, final InputStream inputStream) throws IOException {
        writeAtomic(targetPath.toPath(), inputStream);
    }

    public void writeAtomic(final Path targetPath, final InputStream inputStream) throws IOException {
        prepareParentDirectory(targetPath);
        final Path tempPath = targetPath
                .resolveSibling(Files.normalizeFilename(targetPath.getFileName().toString() + TMP_SUFFIX));
        Files.copy(inputStream, tempPath);
        Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    public void writeAtomic(final File targetPath, final String content) throws IOException {
        writeAtomic(targetPath.toPath(), content);
    }

    public void writeAtomic(final Path targetPath, final String content) throws IOException {
        writeAtomic(targetPath, content.getBytes(Charsets.defaultCharset()));
    }

    public void writeAtomic(final File targetPath, final byte[] bytes) throws IOException {
        writeAtomic(targetPath.toPath(), bytes);
    }

    public void writeAtomic(final Path targetPath, final byte[] bytes) throws IOException {
        prepareParentDirectory(targetPath);
        final Path tempPath = targetPath
                .resolveSibling(Files.normalizeFilename(targetPath.getFileName().toString() + TMP_SUFFIX));
        Files.write(tempPath, bytes);
        Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    private long newInitialCleanupTime() {
        final Path markerPath = folder.resolve(CLEANUP_MARKER_FILENAME);
        try {
            if (Files.exists(markerPath)) {
                return Files.getLastModifiedTime(markerPath).toMillis();
            }
        } catch (final IOException e) {
            // Fall back to 0L if reading the timestamp fails
        }
        return 0;
    }

    public void maybeRunCleanup() {
        final long now = FDateMillis.nowMillis();
        final long last = lastCleanupTime.get();

        if (now - last < CLEANUP_INTERVAL_MILLIS) {
            return;
        }

        final Path markerPath = folder.resolve(CLEANUP_MARKER_FILENAME);
        try {
            if (Files.exists(markerPath)) {
                final long lastModified = Files.getLastModifiedTime(markerPath).toMillis();
                if (now - lastModified < CLEANUP_INTERVAL_MILLIS) {
                    lastCleanupTime.set(now);
                    return;
                }
            }
        } catch (final IOException e) {
            // Ignore and proceed to attempt claiming the slot
        }

        try {
            writeAtomic(markerPath, String.valueOf(now).getBytes(Charsets.defaultCharset()));
            lastCleanupTime.set(now);
            cleanupStaleTempFiles();
        } catch (final IOException e) {
            lastCleanupTime.set(now);
        }
    }

    private void cleanupStaleTempFiles() {
        final long now = FDateMillis.nowMillis();
        cleanupDirectory(folder, now);
    }

    private boolean cleanupDirectory(final Path dir, final long now) {
        boolean isEmpty = true;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (final Path path : stream) {
                if (Files.isDirectory(path)) {
                    if (cleanupDirectory(path, now)) {
                        try {
                            Files.deleteIfExists(path);
                        } catch (final IOException e) {
                            isEmpty = false; // Directory could not be removed (e.g. concurrent write)
                        }
                    } else {
                        isEmpty = false;
                    }
                } else {
                    final String fileName = path.getFileName().toString();
                    if (fileName.endsWith(TMP_EXTENSION)) {
                        try {
                            if (now - Files.getLastModifiedTime(path).toMillis() > STALE_TEMP_FILE_AGE_MILLIS) {
                                Files.deleteIfExists(path);
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