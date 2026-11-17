package de.invesdwin.context.integration.filechannel.nio.atomic.properties;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.configuration2.AbstractConfiguration;

import de.invesdwin.context.integration.filechannel.info.path.FileChannelPath;
import de.invesdwin.context.integration.filechannel.nio.NioFileInfo;
import de.invesdwin.context.integration.filechannel.nio.atomic.AtomicNioFileChannel;
import de.invesdwin.context.system.properties.AProperties;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.concurrent.lock.file.AtomicNioFileChannelContext;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.string.Charsets;
import de.invesdwin.util.time.date.FDate;

/**
 * Properties implementation utilizing a highly concurrent file-per-property pattern.
 * 
 * <p>
 * <b>When to use:</b> Use this when you have multiple readers and writers on multiple processes over a shared node
 * concurrently. Because each property is stored as a separate file, individual property updates do not lock or
 * overwrite the entire configuration set, avoiding race conditions during simultaneous distinct modifications.
 * 
 * <p>
 * <b>Pattern used:</b> Maps individual properties to dedicated files with a specific extension ({@code .property})
 * within a shared directory. Modifications are delegated to an {@link AtomicNioFileChannel} which safely performs
 * atomic writes.
 * 
 * WARNING: A single property is not thread-safe, if multiple threads write the same temp file concurrently, the first
 * one wins.
 */
@ThreadSafe
public class AtomicFilesProperties extends AProperties {

    public static final String PROPERTY_FILE_EXTENSION = ".property";

    /**
     * Bound for {@link #collectDiskKeys}: a subdirectory of the properties folder may be a symlink back to an
     * ancestor, and the scan must not turn that into an endless descent.
     */
    private static final int MAX_DIRECTORY_DEPTH = 16;

    /**
     * Separator inside a property key, matching what {@link Files#normalizePath(String)} leaves as a path separator -
     * the key of a nested property file has to be spelled exactly as the caller spells it.
     */
    private static final String KEY_PATH_SEPARATOR = "/";

    private final AtomicNioFileChannel fileChannel;

    // Cache structures
    private final Set<String> knownKeys = ILockCollectionFactory.getInstance(true).newConcurrentSet();
    private final Map<String, String> valueCache = ILockCollectionFactory.getInstance(true).newConcurrentMap();
    private volatile FDate lastDirectoryScan = null;

    /**
     * WARNING: it is recommended to use a separate directory for the properties files to avoid conflicts with other
     * files during atomic move operations and tmp file cleanups. Here we expect a dedicated directory for the
     * properties files.
     */
    public AtomicFilesProperties(final File directory) {
        //CHECKSTYLE:OFF
        this(new AtomicNioFileChannel(FileChannelPath.newDirectory(directory)));
        //CHECKSTYLE:ON
    }

    public AtomicFilesProperties(final File directory, final AtomicNioFileChannelContext context) {
        //CHECKSTYLE:OFF
        this(new AtomicNioFileChannel(FileChannelPath.newDirectory(directory), context));
        //CHECKSTYLE:ON
    }

    public AtomicFilesProperties(final AtomicNioFileChannel fileChannel) {
        if (fileChannel.getFileName() != null) {
            throw new IllegalArgumentException("The provided path must be a directory: " + fileChannel);
        }
        this.fileChannel = fileChannel;
    }

    public AtomicNioFileChannel getFileChannel() {
        return fileChannel;
    }

    private void syncCacheWithDirectory() {
        final FDate currentModTime = fileChannel.lastModified();

        if (lastDirectoryScan != null && currentModTime != null && currentModTime.equals(lastDirectoryScan)) {
            return;
        }

        synchronized (this) {
            if (lastDirectoryScan != null && currentModTime != null && currentModTime.equals(lastDirectoryScan)) {
                return;
            }

            final Set<String> currentDiskKeys = new java.util.HashSet<>();
            collectDiskKeys(fileChannel, "", currentDiskKeys, 0);

            // Sync known keys
            knownKeys.retainAll(currentDiskKeys);
            knownKeys.addAll(currentDiskKeys);

            // Invalidate value cache on folder modification to ensure updated values are re-read lazily
            valueCache.keySet().retainAll(currentDiskKeys);
            valueCache.clear();

            lastDirectoryScan = currentModTime;
        }
    }

    /**
     * Collects every property file below the properties folder, naming each key by its path relative to that folder.
     * <p>
     * Descending is what makes a nested key readable at all: a key is written through
     * {@link Files#normalizePath(String)}, which leaves slashes alone, so a key containing one lands in a subdirectory,
     * while every read is gated on {@link #knownKeys} and a flat listing yields only leaf names. Before this, a nested
     * key written by one process was absent from the next one and its value answered as null although the file was on
     * disk.
     * <p>
     * The rescan is triggered by the modification time of the properties folder, which a write into an existing
     * subdirectory does not change; an instance thus sees the nested keys it wrote and those already present when it
     * first scanned, and picks up another process's nested writes as a new instance.
     */
    private static void collectDiskKeys(final AtomicNioFileChannel directory, final String keyPrefix,
            final Set<String> diskKeys, final int depth) {
        try (ICloseableIterator<NioFileInfo> iterator = directory.listIterator()) {
            while (iterator.hasNext()) {
                final NioFileInfo info = iterator.next();
                final String fileName = info.getFileName();
                if (fileName == null) {
                    continue;
                }
                if (info.isDirectory()) {
                    if (depth < MAX_DIRECTORY_DEPTH) {
                        collectDiskKeys(
                                directory.withAbsoluteDirectory(
                                        directory.getAbsoluteDirectory() + KEY_PATH_SEPARATOR + fileName),
                                keyPrefix + fileName + KEY_PATH_SEPARATOR, diskKeys, depth + 1);
                    }
                } else if (info.isFile() && fileName.endsWith(PROPERTY_FILE_EXTENSION)) {
                    diskKeys.add(keyPrefix
                            + fileName.substring(0, fileName.length() - PROPERTY_FILE_EXTENSION.length()));
                }
            }
        }
    }

    @Override
    protected AbstractConfiguration createDelegate() {
        return new AbstractConfiguration() {

            private AtomicNioFileChannel getChannel(final String key) {
                return fileChannel.withFilename(Files.normalizePath(key + PROPERTY_FILE_EXTENSION));
            }

            private String readProperty(final AtomicNioFileChannel channel) {
                final byte[] bytes = channel.downloadBytes();
                if (bytes == null) {
                    return null;
                }
                return new String(bytes, Charsets.defaultCharset());
            }

            @Override
            protected boolean isEmptyInternal() {
                syncCacheWithDirectory();
                return knownKeys.isEmpty();
            }

            @Override
            protected Object getPropertyInternal(final String key) {
                syncCacheWithDirectory();
                if (!knownKeys.contains(key)) {
                    return null;
                }

                // Check lazy cache first
                final String cachedValue = valueCache.get(key);
                if (cachedValue != null) {
                    return cachedValue;
                }

                // Lazy load from disk
                final AtomicNioFileChannel channel = getChannel(key);
                final String value = readProperty(channel);
                if (value != null) {
                    valueCache.put(key, value);
                }
                return value;
            }

            @Override
            protected Iterator<String> getKeysInternal() {
                syncCacheWithDirectory();
                return new ArrayList<>(knownKeys).iterator();
            }

            @Override
            protected boolean containsKeyInternal(final String key) {
                syncCacheWithDirectory();
                return knownKeys.contains(key);
            }

            @Override
            protected void clearPropertyDirect(final String key) {
                getChannel(key).delete();
                knownKeys.remove(key);
                valueCache.remove(key);
            }

            @Override
            protected void addPropertyDirect(final String key, final Object value) {
                try {
                    final String valueStr = String.valueOf(value);
                    getChannel(key).uploadString(valueStr);
                    knownKeys.add(key);
                    valueCache.put(key, valueStr);
                } catch (final Throwable t) {
                    //first one wins when doing concurrent writes to the process specific temp file for this property
                    if (!Throwables.isCausedByAnyType(t, FileAlreadyExistsException.class, NoSuchFileException.class)) {
                        throw t;
                    }
                }
            }

            @Override
            protected boolean containsValueInternal(final Object value) {
                if (value == null) {
                    return false;
                }
                syncCacheWithDirectory();
                final String valueStr = String.valueOf(value);

                for (final String key : knownKeys) {
                    final Object propValue = getPropertyInternal(key);
                    if (valueStr.equals(propValue)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}