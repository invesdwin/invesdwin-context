package de.invesdwin.context.system.array.large;

import java.io.File;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.context.system.properties.PrefixedDelegateProperties;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.array.large.IBooleanLargeArray;
import de.invesdwin.util.collections.array.large.IDoubleLargeArray;
import de.invesdwin.util.collections.array.large.IIntegerLargeArray;
import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.collections.attributes.PrefixedDelegateAttributesMap;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@ThreadSafe
public class PrefixedLargeArrayAllocator implements ILargeArrayAllocator {

    private final ILargeArrayAllocator delegate;
    private final String prefix;
    private PrefixedDelegateAttributesMap attributes;
    private PrefixedDelegateProperties properties;
    private final File directory;

    public PrefixedLargeArrayAllocator(final ILargeArrayAllocator delegate, final String prefix) {
        this.delegate = delegate;
        this.prefix = prefix;
        Assertions.checkNotBlank(prefix);
        this.directory = newDirectory();
    }

    private File newDirectory() {
        final File directory = delegate.getDirectory();
        if (directory == null) {
            return null;
        }
        return new File(directory, Files.normalizeFilename(prefix));
    }

    @Override
    public IMemoryBuffer getMemoryBuffer(final String id) {
        return delegate.getMemoryBuffer(prefix + id);
    }

    @Override
    public IDoubleLargeArray getDoubleArray(final String id) {
        return delegate.getDoubleArray(prefix + id);
    }

    @Override
    public IIntegerLargeArray getIntegerArray(final String id) {
        return delegate.getIntegerArray(prefix + id);
    }

    @Override
    public IBooleanLargeArray getBooleanArray(final String id) {
        return delegate.getBooleanArray(prefix + id);
    }

    @Override
    public ILargeBitSet getBitSet(final String id) {
        return delegate.getBitSet(prefix + id);
    }

    @Override
    public ILongLargeArray getLongArray(final String id) {
        return delegate.getLongArray(prefix + id);
    }

    @Override
    public IMemoryBuffer newMemoryBuffer(final String id, final long size) {
        return delegate.newMemoryBuffer(prefix + id, size);
    }

    @Override
    public IDoubleLargeArray newDoubleArray(final String id, final long size) {
        return delegate.newDoubleArray(prefix + id, size);
    }

    @Override
    public IIntegerLargeArray newIntegerArray(final String id, final long size) {
        return delegate.newIntegerArray(prefix + id, size);
    }

    @Override
    public IBooleanLargeArray newBooleanArray(final String id, final long size) {
        return delegate.newBooleanArray(prefix + id, size);
    }

    @Override
    public ILargeBitSet newBitSet(final String id, final long size) {
        return delegate.newBitSet(prefix + id, size);
    }

    @Override
    public ILongLargeArray newLongArray(final String id, final long size) {
        return delegate.newLongArray(prefix + id, size);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof PrefixedLargeArrayAllocator) {
            final PrefixedLargeArrayAllocator cObj = (PrefixedLargeArrayAllocator) obj;
            return Objects.equals(prefix, cObj.prefix) && Objects.equals(delegate, cObj.delegate);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(PrefixedLargeArrayAllocator.class, prefix, delegate);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(prefix).addValue(delegate).toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (type.isAssignableFrom(getClass())) {
            return (T) this;
        } else {
            return delegate.unwrap(type);
        }
    }

    @Override
    public IAttributesMap getAttributes() {
        if (attributes == null) {
            synchronized (this) {
                if (attributes == null) {
                    attributes = new PrefixedDelegateAttributesMap(delegate.getAttributes(), prefix);
                }
            }
        }
        return attributes;
    }

    @Override
    public IProperties getProperties() {
        if (properties == null) {
            synchronized (this) {
                if (properties == null) {
                    properties = new PrefixedDelegateProperties(delegate.getProperties(), prefix);
                }
            }
        }
        return properties;
    }

    @Override
    public void clear() {
        delegate.clear();
        attributes = null;
        properties = null;
    }

    @Override
    public boolean isOnHeap(final long size) {
        return delegate.isOnHeap(size);
    }

    @Override
    public File getDirectory() {
        return directory;
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public ILock getLock(final String id) {
        return delegate.getLock(prefix + id);
    }

}
