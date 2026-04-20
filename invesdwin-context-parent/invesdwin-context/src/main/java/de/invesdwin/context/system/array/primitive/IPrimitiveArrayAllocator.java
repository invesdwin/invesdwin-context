package de.invesdwin.context.system.array.primitive;

import de.invesdwin.context.system.array.base.IBaseArrayAllocator;
import de.invesdwin.util.collections.array.primitive.IBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IDoublePrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

/**
 * An instance of a primitive array allocator should only be shared within the same backtesting engine. Otherwise
 * engines might get confused when accessing and sharing cached time indexes.
 */
public interface IPrimitiveArrayAllocator extends IBaseArrayAllocator {

    IByteBuffer getByteBuffer(String id);

    IDoublePrimitiveArray getDoubleArray(String id);

    IIntegerPrimitiveArray getIntegerArray(String id);

    IBooleanPrimitiveArray getBooleanArray(String id);

    IPrimitiveBitSet getBitSet(String id);

    ILongPrimitiveArray getLongArray(String id);

    IByteBuffer newByteBuffer(String id, int size);

    IDoublePrimitiveArray newDoubleArray(String id, int size);

    IIntegerPrimitiveArray newIntegerArray(String id, int size);

    IBooleanPrimitiveArray newBooleanArray(String id, int size);

    IPrimitiveBitSet newBitSet(String id, int size);

    ILongPrimitiveArray newLongArray(String id, int size);

    boolean isOnHeap(int size);

}
