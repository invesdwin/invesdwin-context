package de.invesdwin.context.system.properties.concurrent;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.configuration2.AbstractConfiguration;

import de.invesdwin.context.system.properties.AProperties;
import de.invesdwin.instrument.DynamicInstrumentationProperties;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.string.Charsets;
import de.invesdwin.util.time.date.millis.FDateMillis;

@ThreadSafe
public class MultiprocessFileProperties extends AProperties {

    private static final long STALE_TEMP_FILE_AGE_MILLIS = 10 * 60 * 1000L; // 10 minutes
    private static final long CLEANUP_INTERVAL_MILLIS = 24 * 60 * 60 * 1000L; // 24 hours
    private static final String CLEANUP_MARKER_FILENAME = ".cleanup";

    private final Path folderPath;
    private final AtomicLong lastCleanupTime;

    public MultiprocessFileProperties(final File baseFolder) {
        this.folderPath = new File(baseFolder, MultiprocessFileProperties.class.getSimpleName()).toPath();
        try {
            Files.createDirectories(this.folderPath);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to create properties directory: " + this.folderPath, e);
        }
        final long initialCleanupTime = newInitialCleanupTime();
        this.lastCleanupTime = new AtomicLong(initialCleanupTime);
        maybeRunCleanup();
    }

    private long newInitialCleanupTime() {
        // Read the last cleanup timestamp from the shared marker file if it exists,
        // otherwise default to 0L to force an immediate cleanup check on startup.
        final Path markerPath = folderPath.resolve(CLEANUP_MARKER_FILENAME);
        try {
            if (Files.exists(markerPath)) {
                return Files.getLastModifiedTime(markerPath).toMillis();
            }
        } catch (final IOException e) {
            // Fall back to 0L if reading the timestamp fails
        }
        return 0;
    }

    private void maybeRunCleanup() {
        final long now = FDateMillis.nowMillis();
        final long last = lastCleanupTime.get();

        // Tier 1: Fast in-memory check to skip shared disk lookups if run recently in this JVM
        if (now - last < CLEANUP_INTERVAL_MILLIS) {
            return;
        }

        final Path markerPath = folderPath.resolve(CLEANUP_MARKER_FILENAME);
        try {
            if (Files.exists(markerPath)) {
                final long lastModified = Files.getLastModifiedTime(markerPath).toMillis();
                if (now - lastModified < CLEANUP_INTERVAL_MILLIS) {
                    // Cleaned up recently by another process; update local state and skip
                    lastCleanupTime.set(now);
                    return;
                }
            }
        } catch (final IOException e) {
            // Ignore and proceed to attempt claiming the slot
        }

        // Tier 2: Attempt to claim the cleanup slot atomically across processes using a temp file swap
        final Path tempMarker = folderPath
                .resolve(CLEANUP_MARKER_FILENAME + "_" + DynamicInstrumentationProperties.getManagementName() + ".tmp");
        try {
            Files.write(tempMarker, String.valueOf(now).getBytes(Charsets.defaultCharset()));
            Files.move(tempMarker, markerPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);

            // Successfully won the race; update local tracker and run cleanup
            lastCleanupTime.set(now);
            cleanupStaleTempFiles();
        } catch (final IOException e) {
            // Another process raced and won; update local tracker to prevent immediate retries
            lastCleanupTime.set(now);
        } finally {
            try {
                Files.deleteIfExists(tempMarker);
            } catch (final IOException e) {
                // Ignore cleanup error for temporary marker
            }
        }
    }

    private void cleanupStaleTempFiles() {
        final long now = FDateMillis.nowMillis();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
            for (final Path path : stream) {
                final String fileName = path.getFileName().toString();
                if (Files.isRegularFile(path) && fileName.endsWith(".tmp")
                        && !fileName.equals(CLEANUP_MARKER_FILENAME)) {
                    try {
                        if (now - Files.getLastModifiedTime(path).toMillis() > STALE_TEMP_FILE_AGE_MILLIS) {
                            Files.deleteIfExists(path);
                        }
                    } catch (final NoSuchFileException e) {
                        // Concurrently deleted by another process; safe to ignore
                    } catch (final IOException e) {
                        // Non-fatal background cleanup issue
                    }
                }
            }
        } catch (final IOException e) {
            // Non-fatal if directory stream fails during maintenance sweep
        }
    }

    @Override
    protected AbstractConfiguration createDelegate() {
        return new AbstractConfiguration() {

            private Path getPath(final String key) {
                return folderPath.resolve(Files.normalizeFilename(key));
            }

            private boolean isValidPropertyFile(final Path path) {
                final String fileName = path.getFileName().toString();
                return Files.isRegularFile(path) && !fileName.endsWith(".tmp")
                        && !fileName.equals(CLEANUP_MARKER_FILENAME);
            }

            @Override
            protected boolean isEmptyInternal() {
                maybeRunCleanup();
                final Iterator<String> keys = getKeysInternal();
                return !keys.hasNext();
            }

            @Override
            protected Object getPropertyInternal(final String key) {
                final Path path = getPath(key);
                if (!Files.exists(path)) {
                    return null;
                }
                try {
                    return new String(Files.readAllBytes(path), Charsets.defaultCharset());
                } catch (final NoSuchFileException e) {
                    return null;
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to read property file: " + path.toAbsolutePath(), e);
                }
            }

            @Override
            protected Iterator<String> getKeysInternal() {
                maybeRunCleanup();
                final List<String> keys = new ArrayList<>();
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
                    for (final Path path : stream) {
                        if (isValidPropertyFile(path)) {
                            keys.add(path.getFileName().toString());
                        }
                    }
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to list properties directory: " + folderPath.toAbsolutePath(),
                            e);
                }
                return keys.iterator();
            }

            @Override
            protected boolean containsKeyInternal(final String key) {
                return Files.exists(getPath(key));
            }

            @Override
            protected void clearPropertyDirect(final String key) {
                try {
                    Files.deleteIfExists(getPath(key));
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to delete property file for key: " + key, e);
                }
            }

            @Override
            protected void addPropertyDirect(final String key, final Object value) {
                maybeRunCleanup();
                final Path targetPath = getPath(key);
                final String tempSuffix = "_" + DynamicInstrumentationProperties.getManagementName() + ".tmp";
                final Path tempPath = getPath(key + tempSuffix);

                try {
                    Files.write(tempPath, String.valueOf(value).getBytes(Charsets.defaultCharset()));
                    Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE,
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to write property to shared directory: " + key, e);
                } finally {
                    try {
                        Files.deleteIfExists(tempPath);
                    } catch (final NoSuchFileException e) {
                        // Already cleaned up or moved
                    } catch (final IOException e) {
                        // Non-fatal temporary file cleanup failure
                    }
                }
            }

            @Override
            protected boolean containsValueInternal(final Object value) {
                maybeRunCleanup();
                if (value == null) {
                    return false;
                }
                final String valueStr = String.valueOf(value);
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
                    for (final Path path : stream) {
                        if (isValidPropertyFile(path)) {
                            try {
                                final String content = new String(Files.readAllBytes(path), Charsets.defaultCharset());
                                if (valueStr.equals(content)) {
                                    return true;
                                }
                            } catch (final NoSuchFileException e) {
                                // File removed concurrently; skip safely
                            } catch (final IOException e) {
                                throw new RuntimeException(
                                        "Failed to read property file during value scan: " + path.toAbsolutePath(), e);
                            }
                        }
                    }
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to scan properties directory: " + folderPath.toAbsolutePath(),
                            e);
                }
                return false;
            }
        };
    }
}