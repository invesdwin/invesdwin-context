package de.invesdwin.context.integration.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.ISerde;
import de.invesdwin.context.integration.serde.SerdeBaseMethods;
import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.math.Longs;

@Immutable
public class LongSerde implements ISerde<Long> {

    public static final LongSerde GET = new LongSerde();
    public static final int FIXED_LENGTH = Long.BYTES;

    @Override
    public Long fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final Long obj) {
        return SerdeBaseMethods.toBytes(this, obj, FIXED_LENGTH);
    }

    @Override
    public Long fromBuffer(final IByteBuffer buffer) {
        return Longs.extractLong(buffer, 0);
    }

    @Override
    public int toBuffer(final Long obj, final IByteBuffer buffer) {
        Longs.putLong(buffer, 0, obj);
        return FIXED_LENGTH;
    }
}
