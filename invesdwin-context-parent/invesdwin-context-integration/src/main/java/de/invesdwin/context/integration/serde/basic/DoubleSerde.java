package de.invesdwin.context.integration.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.ISerde;
import de.invesdwin.context.integration.serde.SerdeBaseMethods;
import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.math.Doubles;

@Immutable
public final class DoubleSerde implements ISerde<Double> {

    public static final DoubleSerde GET = new DoubleSerde();
    public static final int FIXED_LENGTH = Double.BYTES;

    private DoubleSerde() {
    }

    @Override
    public Double fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final Double obj) {
        return SerdeBaseMethods.toBytes(this, obj, FIXED_LENGTH);
    }

    @Override
    public Double fromBuffer(final IByteBuffer buffer) {
        return Doubles.extractDouble(buffer, 0);
    }

    @Override
    public int toBuffer(final Double obj, final IByteBuffer buffer) {
        Doubles.putDouble(buffer, 0, obj);
        return FIXED_LENGTH;
    }

}
