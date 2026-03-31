package de.invesdwin.context.system.array.large;

import java.io.Closeable;
import java.io.File;

import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.norva.beanpath.spi.IUnwrap;
import de.invesdwin.util.collections.array.large.IBooleanLargeArray;
import de.invesdwin.util.collections.array.large.IDoubleLargeArray;
import de.invesdwin.util.collections.array.large.IIntegerLargeArray;
import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

/**
 * An instance of a Large array allocator should only be shared within the same backtesting engine. Otherwise engines
 * might get confused when accessing and sharing cached time indexes.
 */
public interface ILargeArrayAllocator extends IUnwrap, Closeable {

    IMemoryBuffer getMemoryBuffer(String id);

    IDoubleLargeArray getDoubleArray(String id);

    IIntegerLargeArray getIntegerArray(String id);

    IBooleanLargeArray getBooleanArray(String id);

    ILargeBitSet getBitSet(String id);

    ILongLargeArray getLongArray(String id);

    IMemoryBuffer newMemoryBuffer(String id, long size);

    IDoubleLargeArray newDoubleArray(String id, long size);

    IIntegerLargeArray newIntegerArray(String id, long size);

    IBooleanLargeArray newBooleanArray(String id, long size);

    ILargeBitSet newBitSet(String id, long size);

    ILongLargeArray newLongArray(String id, long size);

    IAttributesMap getAttributes();

    IProperties getProperties();

    void clear();

    boolean isOnHeap(long size);

    File getDirectory();

    @Override
    void close();

    ILock getLock(String id);

}
