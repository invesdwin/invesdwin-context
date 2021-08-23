package de.invesdwin.context.integration.serde;

import java.io.Serializable;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.SerializationException;
import org.nustaq.serialization.simpleapi.DefaultCoder;
import org.nustaq.serialization.simpleapi.FSTCoder;

import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.math.Bytes;

/**
 * This serializing serde is suitable for IPC
 */
@Immutable
public class RemoteFastSerializingSerde<E> implements ISerde<E> {

    /**
     * synchronized is fine here since the serialization/deserialization is normally not the cause for multithreaded
     * slowdowns
     */
    @GuardedBy("this")
    private final FSTCoder coder;

    public RemoteFastSerializingSerde(final boolean shared, final Class<E> type) {
        if (type != null && Serializable.class.isAssignableFrom(type)) {
            this.coder = new DefaultCoder(shared, type);
        } else {
            this.coder = new DefaultCoder(shared);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized E fromBytes(final byte[] bytes) {
        if (bytes.length == 0) {
            return null;
        }
        try {
            return (E) coder.toObject(bytes);
        } catch (final Throwable t) {
            throw new SerializationException(t);
        }
    }

    @Override
    public synchronized byte[] toBytes(final E obj) {
        if (obj == null) {
            return Bytes.EMPTY_ARRAY;
        }
        return coder.toByteArray(obj);
    }

    @Override
    public E fromBuffer(final IByteBuffer buffer) {
        return SerdeBaseMethods.fromBuffer(this, buffer);
    }

    @Override
    public int toBuffer(final E obj, final IByteBuffer buffer) {
        return SerdeBaseMethods.toBuffer(this, obj, buffer);
    }

}
