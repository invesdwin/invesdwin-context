package de.invesdwin.context.integration.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.ISerde;
import de.invesdwin.util.math.Bytes;

@Immutable
public class VoidSerde implements ISerde<Void> {

    public static final VoidSerde GET = new VoidSerde();

    @Override
    public Void fromBytes(final byte[] bytes) {
        return null;
    }

    @Override
    public byte[] toBytes(final Void obj) {
        return Bytes.EMPTY_ARRAY;
    }

}
