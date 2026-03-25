package de.invesdwin.context.system.array.primitive;

import java.io.Closeable;
import java.io.File;

import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.norva.beanpath.spi.IUnwrap;
import de.invesdwin.util.collections.array.primitive.IBooleanPrimtiveArray;
import de.invesdwin.util.collections.array.primitive.IDoublePrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

/**
 * An instance of a primitive array allocator should only be shared within the same backtesting engine. Otherwise
 * engines might get confused when accessing and sharing cached time indexes.
 */
public interface IPrimitiveArrayAllocator extends IUnwrap, Closeable {

    IByteBuffer getByteBuffer(String id);

    IDoublePrimitiveArray getDoubleArray(String id);

    IIntegerPrimitiveArray getIntegerArray(String id);

    IBooleanPrimtiveArray getBooleanArray(String id);

    IPrimitiveBitSet getBitSet(String id);

    ILongPrimitiveArray getLongArray(String id);

    IByteBuffer newByteBuffer(String id, int size);

    IDoublePrimitiveArray newDoubleArray(String id, int size);

    IIntegerPrimitiveArray newIntegerArray(String id, int size);

    IBooleanPrimtiveArray newBooleanArray(String id, int size);

    IPrimitiveBitSet newBitSet(String id, int size);

    ILongPrimitiveArray newLongArray(String id, int size);

    IAttributesMap getAttributes();

    IProperties getProperties();

    void clear();

    boolean isOnHeap(int size);

    File getDirectory();

    @Override
    void close();

    ILock getLock(String id);

}
