package de.invesdwin.context.integration.persistentmap.large.storage;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.jspecify.annotations.Nullable;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;

import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummary;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.reference.WeakThreadLocalReference;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.string.Charsets;
import de.invesdwin.util.marshallers.serde.large.ILargeSerde;
import de.invesdwin.util.marshallers.serde.large.ILargeSerdeLengthProvider;
import de.invesdwin.util.streams.buffer.memory.ClosedMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ADelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.extend.ChronicleMappedExpandableMemoryBuffer;
import de.invesdwin.util.streams.closeable.ISafeCloseable;
import io.netty.util.concurrent.FastThreadLocal;
import net.openhft.chronicle.core.OS;

/**
 * A thread-safe chunk storage implementation leveraging Netty's {@link FastThreadLocal} to encapsulate non-thread-safe
 * Chronicle expandable memory buffers per thread. Optimized to prevent Windows file-locking issues and pagefile
 * exhaustion.
 */
@ThreadSafe
public class ThreadLocalChronicleLargeMappedFileChunkStorage<V> implements IChunkStorage<V> {

    public static final long DEFAULT_CHUNK_SIZE = 1024 * 1024 * 1024; // 1 GB

    private final File memoryDirectory;
    private final File memoryFile;
    private final File positionFile;
    private final ILargeSerde<V> valueSerde;
    private final ILargeSerdeLengthProvider<V> valueSerdeLengthProvider;

    private final IReadWriteLock lock = ILockCollectionFactory.getInstance(true)
            .newReadWriteLock(ThreadLocalChronicleLargeMappedFileChunkStorage.class.getSimpleName() + "_lock");

    @GuardedBy("lock")
    private long position;
    private final boolean readOnly;
    private final boolean closeAllowed;
    private final ChunkStorageMetadata metadata;

    // Track active allocations globally so they can be unmapped on global close()/clear()
    private final Set<VersionedBuffer> activeBuffers = newActiveBuffers();

    // Tracks global file mutations (like truncating/clearing) to invalidate thread-local caches
    private final AtomicInteger structureVersion = new AtomicInteger(0);

    // Wrapper to bind the native buffer view with its lifecycle epoch version
    private final class VersionedBuffer extends ADelegateMemoryBuffer implements ISafeCloseable {
        private ICloseableMemoryBuffer buffer;
        private int version;

        private VersionedBuffer() {
            activeBuffers.add(this);
        }

        @Override
        protected ICloseableMemoryBuffer getDelegate() {
            ICloseableMemoryBuffer bufferCopy = this.buffer;
            if (bufferCopy == null) {
                lock.readLock().lock();
                try {
                    if (!memoryFile.getParentFile().exists()) {
                        try {
                            Files.forceMkdir(memoryFile.getParentFile());
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    this.version = structureVersion.get();
                    bufferCopy = newDelegate();
                    this.buffer = bufferCopy;
                } finally {
                    lock.readLock().unlock();
                }
                return bufferCopy;
            }
            final int newVersion = structureVersion.get();
            if (version != newVersion) {
                lock.readLock().lock();
                try {
                    if (bufferCopy != null) {
                        bufferCopy.close();
                        buffer = null;
                    }
                    this.version = structureVersion.get();
                    bufferCopy = newDelegate();
                    this.buffer = bufferCopy;
                } finally {
                    lock.readLock().unlock();
                }
            }
            return bufferCopy;
        }

        private ChronicleMappedExpandableMemoryBuffer newDelegate() {
            return new ChronicleMappedExpandableMemoryBuffer(DEFAULT_CHUNK_SIZE, memoryFile, false, OS.pageSize(),
                    readOnly, closeAllowed);
        }

        @Override
        public void close() {
            final ICloseableMemoryBuffer bufferCopy = buffer;
            if (bufferCopy != null) {
                bufferCopy.close();
            }
            buffer = ClosedMemoryBuffer.INSTANCE;
            activeBuffers.remove(this);
        }
    }

    private final WeakThreadLocalReference<VersionedBuffer> threadLocalBuffer = new WeakThreadLocalReference<VersionedBuffer>() {
        @Override
        protected VersionedBuffer initialValue() {
            return new VersionedBuffer();
        }

        @Override
        protected boolean isCloseOnRemoval() {
            return true;
        }
    };

    @SuppressWarnings("unchecked")
    public ThreadLocalChronicleLargeMappedFileChunkStorage(final File memoryDirectory, final ILargeSerde<V> valueSerde,
            final boolean readOnly, final boolean closeAllowed) {
        this.memoryDirectory = memoryDirectory;
        this.memoryFile = new File(memoryDirectory, "memory.bin");
        this.positionFile = new File(memoryDirectory, "memory.pos");
        this.valueSerde = valueSerde;
        if (valueSerde instanceof ILargeSerdeLengthProvider) {
            this.valueSerdeLengthProvider = (ILargeSerdeLengthProvider<V>) valueSerde;
        } else {
            this.valueSerdeLengthProvider = null;
        }
        this.readOnly = readOnly;
        this.closeAllowed = closeAllowed;
        this.metadata = new ChunkStorageMetadata(memoryDirectory);
        if (memoryFile.exists()) {
            this.position = readPosition();
        }
    }

    private Set<VersionedBuffer> newActiveBuffers() {
        final Cache<VersionedBuffer, Boolean> cache = Caffeine.newBuilder()
                .weakKeys()
                .removalListener((@Nullable final VersionedBuffer key, @Nullable final Boolean value,
                        final RemovalCause cause) -> {
                    if (key != null) {
                        key.close();
                    }
                })
                .<VersionedBuffer, Boolean> build();
        return Collections.newSetFromMap(cache.asMap());
    }

    private long readPosition() {
        return Long.parseLong(Files.readFileToStringNoThrow(positionFile, Charsets.DEFAULT));
    }

    private void writePosition(final long position) {
        try {
            Files.writeStringToFile(positionFile, Long.toString(position), Charsets.DEFAULT);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resolves the thread-local buffer view, ensuring it matches the current global context version.
     */
    private VersionedBuffer getReaderBuffer() {
        return threadLocalBuffer.get();
    }

    @Override
    public V get(final ChunkSummary summary) {
        lock.readLock().lock();
        try {
            final long length = summary.getMemoryLength();
            final long memoryOffset = summary.getPrecedingMemoryOffset() + summary.getMemoryOffset();

            // Isolated slice creation prevents inter-thread racing on index/position variables
            final IMemoryBuffer buffer = getReaderBuffer().newSlice(memoryOffset, length);
            return valueSerde.fromBuffer(buffer);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public ChunkSummary put(final V value) {
        if (valueSerdeLengthProvider != null) {
            final long length = valueSerdeLengthProvider.getLength(value);
            return writeValueNonBlocking(value, length);
        } else {
            return writeValueBlocking(value);
        }
    }

    private ChunkSummary writeValueBlocking(final V value) {
        final long addressOffset;
        final long length;
        lock.writeLock().lock();
        try {
            addressOffset = position;
            final IMemoryBuffer mf = getReaderBuffer().getDelegate();
            length = valueSerde.toBuffer(mf.sliceFrom(addressOffset), value);
            position += length;
            writePosition(position);
        } finally {
            lock.writeLock().unlock();
        }

        lock.readLock().lock();
        try {
            final ChunkSummary summary = new ChunkSummary(memoryFile.getName(), 0L, addressOffset, length);
            metadata.putSummary(summary);
            return summary;
        } finally {
            lock.readLock().unlock();
        }
    }

    private ChunkSummary writeValueNonBlocking(final V value, final long length) {
        final long addressOffset;
        lock.writeLock().lock();
        try {
            addressOffset = position;
            position += length;
            writePosition(position);
        } finally {
            lock.writeLock().unlock();
        }

        lock.readLock().lock();
        try {
            final IMemoryBuffer mf = getReaderBuffer().getDelegate();
            final long writtenLength = valueSerde.toBuffer(mf.sliceFrom(addressOffset), value);
            Assertions.checkEquals(length, writtenLength);

            final ChunkSummary summary = new ChunkSummary(memoryFile.getName(), 0L, addressOffset, length);
            metadata.putSummary(summary);
            return summary;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void remove(final ChunkSummary summary) {
        // noop
    }

    @Override
    public boolean isRemovable() {
        return false;
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            // Step 1: Invalidate future reads/writes across all threads immediately
            structureVersion.incrementAndGet();

            // Step 2: Force close all existing open allocations to free Windows memory maps
            for (final VersionedBuffer buffer : activeBuffers) {
                try {
                    buffer.close();
                } catch (final Exception ignored) {
                    // Suppress to ensure every handle gets an explicit close execution attempt
                }
            }
            activeBuffers.clear();
            threadLocalBuffer.remove(); // Clean up current thread

            // Step 3: Delete physical files safely now that all maps are dropped
            Files.deleteQuietly(memoryDirectory);
            position = 0;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void close() {
        lock.writeLock().lock();
        try {
            structureVersion.incrementAndGet();
            for (final VersionedBuffer buffer : activeBuffers) {
                buffer.close();
            }
            activeBuffers.clear();
            threadLocalBuffer.remove();
        } finally {
            lock.writeLock().unlock();
        }
    }
}