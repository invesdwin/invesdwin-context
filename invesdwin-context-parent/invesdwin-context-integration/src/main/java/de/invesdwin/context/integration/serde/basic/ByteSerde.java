package de.invesdwin.context.integration.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.ISerde;
import de.invesdwin.context.integration.serde.SerdeBaseMethods;
import de.invesdwin.util.lang.buffer.IByteBuffer;

@Immutable
public class ByteSerde implements ISerde<byte[]> {

    public static final ByteSerde GET = new ByteSerde();

    @Override
    public byte[] fromBytes(final byte[] bytes) {
        return bytes;
    }

    @Override
    public byte[] toBytes(final byte[] obj) {
        return obj;
    }

    @Override
    public byte[] fromBuffer(final IByteBuffer buffer) {
        return SerdeBaseMethods.fromBuffer(this, buffer);
    }

    @Override
    public int toBuffer(final byte[] obj, final IByteBuffer buffer) {
        return SerdeBaseMethods.toBuffer(this, obj, buffer);
    }
}
