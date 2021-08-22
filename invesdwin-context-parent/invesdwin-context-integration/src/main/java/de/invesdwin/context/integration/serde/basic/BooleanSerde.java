package de.invesdwin.context.integration.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.ISerde;
import de.invesdwin.context.integration.serde.SerdeBaseMethods;
import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.math.Booleans;

@Immutable
public class BooleanSerde implements ISerde<Boolean> {

    public static final BooleanSerde GET = new BooleanSerde();
    public static final int FIXED_LENGTH = Booleans.BYTES;

    @Override
    public Boolean fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final Boolean obj) {
        return SerdeBaseMethods.toBytes(this, obj, FIXED_LENGTH);
    }

    @Override
    public Boolean fromBuffer(final IByteBuffer buffer) {
        return Booleans.extractBoolean(buffer, 0);
    }

    @Override
    public int toBuffer(final Boolean obj, final IByteBuffer buffer) {
        Booleans.putBoolean(buffer, 0, obj);
        return FIXED_LENGTH;
    }

}
