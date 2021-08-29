package de.invesdwin.context.integration.serde;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.IByteBuffer;

/**
 * This serializing serde is only suitable inside the current JVM
 */
@Immutable
public class LocalFastSerializingSerde<E extends Serializable> implements ISerde<E> {

    @SuppressWarnings("rawtypes")
    private static final LocalFastSerializingSerde INSTANCE = new LocalFastSerializingSerde<>();

    @Override
    public E fromBytes(final byte[] bytes) {
        return Objects.deserialize(bytes);
    }

    @Override
    public byte[] toBytes(final E obj) {
        return Objects.serialize(obj);
    }

    @Override
    public E fromBuffer(final IByteBuffer buffer, final int length) {
        return Objects.deserialize(buffer.asInputStreamTo(length));
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final E obj) {
        return Objects.serialize(obj, buffer.asOutputStream());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> LocalFastSerializingSerde<T> get() {
        return INSTANCE;
    }

}
