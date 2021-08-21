package de.invesdwin.context.integration.serde.basic;

import java.io.UnsupportedEncodingException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.ISerde;

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
}
