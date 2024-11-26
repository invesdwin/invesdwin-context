package de.invesdwin.context.system.array;

import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.norva.beanpath.spi.IUnwrap;
import de.invesdwin.util.collections.array.IBooleanArray;
import de.invesdwin.util.collections.array.IDoubleArray;
import de.invesdwin.util.collections.array.IIntegerArray;
import de.invesdwin.util.collections.array.ILongArray;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

/**
 * An instance of a primitive array allocator should only be shared within the same backtesting engine. Otherwise
 * engines might get confused when accessing and sharing cached time indexes.
 */
public interface IPrimitiveArrayAllocator extends IUnwrap {

    IByteBuffer getByteBuffer(String id);

    IDoubleArray getDoubleArray(String id);

    IIntegerArray getIntegerArray(String id);

    IBooleanArray getBooleanArray(String id);

    IBitSet getBitSet(String id);

    ILongArray getLongArray(String id);

    IByteBuffer newByteBuffer(String id, int size);

    IDoubleArray newDoubleArray(String id, int size);

    IIntegerArray newIntegerArray(String id, int size);

    IBooleanArray newBooleanArray(String id, int size);

    IBitSet newBitSet(String id, int size);

    ILongArray newLongArray(String id, int size);

    IAttributesMap getAttributes();

    IProperties getProperties();

    void clear();

    static IPrimitiveArrayAllocator newInstance() {
        return new OnHeapPrimitiveArrayAllocator();
    }

}
