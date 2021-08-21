package de.invesdwin.context.integration.serde.basic;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.ISerde;
import de.invesdwin.util.math.Doubles;

@Immutable
public final class DoubleSerde implements ISerde<Double> {

    public static final DoubleSerde GET = new DoubleSerde();
    public static final Integer FIXED_LENGTH = Double.BYTES;

    private DoubleSerde() {
    }

    @Override
    public Double fromBytes(final byte[] bytes) {
        final ByteBuffer buf = ByteBuffer.wrap(bytes);
        return Doubles.extractDouble(buf);
    }

    @Override
    public byte[] toBytes(final Double obj) {
        final ByteBuffer buf = ByteBuffer.allocate(FIXED_LENGTH);
        Doubles.putDouble(buf, obj);
        return buf.array();
    }

}
