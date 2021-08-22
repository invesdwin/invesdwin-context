package de.invesdwin.context.integration.serde.basic;

import java.io.UnsupportedEncodingException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.ISerde;
import de.invesdwin.context.integration.serde.SerdeBaseMethods;
import de.invesdwin.util.lang.buffer.IByteBuffer;

@Immutable
public class StringSerde implements ISerde<String> {

    public static final StringSerde GET = new StringSerde();

    @Override
    public String fromBytes(final byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] toBytes(final String obj) {
        try {
            return obj.getBytes("UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String fromBuffer(final IByteBuffer buffer) {
        return SerdeBaseMethods.fromBuffer(this, buffer);
    }

    @Override
    public int toBuffer(final String obj, final IByteBuffer buffer) {
        return SerdeBaseMethods.toBuffer(this, obj, buffer);
    }
}
