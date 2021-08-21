package de.invesdwin.context.integration.serde.basic;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.ISerde;

@Immutable
public class LongSerde implements ISerde<Long> {

    public static final LongSerde GET = new LongSerde();
    public static final int FIXED_LENGTH = Long.BYTES;

    @Override
    public Long fromBytes(final byte[] bytes) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getLong();
    }

    @Override
    public byte[] toBytes(final Long obj) {
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(obj);
        return buffer.array();
    }
}
