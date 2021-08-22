package de.invesdwin.context.integration.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.ISerde;
import de.invesdwin.context.integration.serde.SerdeBaseMethods;
import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.math.Integers;

@Immutable
public class IntegerSerde implements ISerde<Integer> {

    public static final IntegerSerde GET = new IntegerSerde();
    public static final int FIXED_LENGTH = Integer.BYTES;

    @Override
    public Integer fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final Integer obj) {
        return SerdeBaseMethods.toBytes(this, obj, FIXED_LENGTH);
    }

    @Override
    public Integer fromBuffer(final IByteBuffer buffer) {
        return Integers.extractInteger(buffer, 0);
    }

    @Override
    public int toBuffer(final Integer obj, final IByteBuffer buffer) {
        Integers.putInteger(buffer, 0, obj);
        return FIXED_LENGTH;
    }

}
