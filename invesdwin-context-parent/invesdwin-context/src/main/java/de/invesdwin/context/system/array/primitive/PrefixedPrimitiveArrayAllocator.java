package de.invesdwin.context.system.array.primitive;

import java.io.File;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.context.system.properties.PrefixedDelegateProperties;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.array.primitive.IBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IDoublePrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.collections.attributes.PrefixedDelegateAttributesMap;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@ThreadSafe
public class PrefixedPrimitiveArrayAllocator implements IPrimitiveArrayAllocator {

    private final IPrimitiveArrayAllocator delegate;
    private final String prefix;
    private PrefixedDelegateAttributesMap attributes;
    private PrefixedDelegateProperties properties;
    private final File directory;

    public PrefixedPrimitiveArrayAllocator(final IPrimitiveArrayAllocator delegate, final String prefix) {
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
    public IByteBuffer getByteBuffer(final String id) {
        return delegate.getByteBuffer(prefix + id);
    }

    @Override
    public IDoublePrimitiveArray getDoubleArray(final String id) {
        return delegate.getDoubleArray(prefix + id);
    }

    @Override
    public IIntegerPrimitiveArray getIntegerArray(final String id) {
        return delegate.getIntegerArray(prefix + id);
    }

    @Override
    public IBooleanPrimitiveArray getBooleanArray(final String id) {
        return delegate.getBooleanArray(prefix + id);
    }

    @Override
    public IPrimitiveBitSet getBitSet(final String id) {
        return delegate.getBitSet(prefix + id);
    }

    @Override
    public ILongPrimitiveArray getLongArray(final String id) {
        return delegate.getLongArray(prefix + id);
    }

    @Override
    public IByteBuffer newByteBuffer(final String id, final int size) {
        return delegate.newByteBuffer(prefix + id, size);
    }

    @Override
    public IDoublePrimitiveArray newDoubleArray(final String id, final int size) {
        return delegate.newDoubleArray(prefix + id, size);
    }

    @Override
    public IIntegerPrimitiveArray newIntegerArray(final String id, final int size) {
        return delegate.newIntegerArray(prefix + id, size);
    }

    @Override
    public IBooleanPrimitiveArray newBooleanArray(final String id, final int size) {
        return delegate.newBooleanArray(prefix + id, size);
    }

    @Override
    public IPrimitiveBitSet newBitSet(final String id, final int size) {
        return delegate.newBitSet(prefix + id, size);
    }

    @Override
    public ILongPrimitiveArray newLongArray(final String id, final int size) {
        return delegate.newLongArray(prefix + id, size);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof PrefixedPrimitiveArrayAllocator) {
            final PrefixedPrimitiveArrayAllocator cObj = (PrefixedPrimitiveArrayAllocator) obj;
            return Objects.equals(prefix, cObj.prefix) && Objects.equals(delegate, cObj.delegate);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(PrefixedPrimitiveArrayAllocator.class, prefix, delegate);
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
    public boolean isOnHeap(final int size) {
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
