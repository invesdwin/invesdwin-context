package de.invesdwin.context.integration.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.ISerde;
import de.invesdwin.context.integration.serde.SerdeBaseMethods;
import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.math.decimal.Decimal;

@Immutable
public final class DecimalSerde implements ISerde<Decimal> {

    public static final DecimalSerde GET = new DecimalSerde();
    public static final int FIXED_LENGTH = Decimal.BYTES;

    private DecimalSerde() {
    }

    @Override
    public Decimal fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final Decimal obj) {
        return SerdeBaseMethods.toBytes(this, obj, FIXED_LENGTH);
    }

    @Override
    public Decimal fromBuffer(final IByteBuffer buffer) {
        return Decimal.extractDecimal(buffer, 0);
    }

    @Override
    public int toBuffer(final Decimal obj, final IByteBuffer buffer) {
        Decimal.putDecimal(buffer, 0, obj);
        return FIXED_LENGTH;
    }

}
