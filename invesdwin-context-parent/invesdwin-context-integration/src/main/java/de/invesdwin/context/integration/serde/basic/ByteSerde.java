package de.invesdwin.context.integration.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.ISerde;

@Immutable
public class ByteSerde implements ISerde<byte[]> {

    public static final ByteSerde GET = new ByteSerde();
    public static final int FIXED_LENGTH = Byte.BYTES;

    @Override
    public byte[] fromBytes(final byte[] bytes) {
        return bytes;
    }

    @Override
    public byte[] toBytes(final byte[] obj) {
        return obj;
    }
}
