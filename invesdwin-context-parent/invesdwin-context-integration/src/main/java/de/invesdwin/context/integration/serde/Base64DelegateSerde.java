package de.invesdwin.context.integration.serde;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.codec.binary.Base64;

@Immutable
public class Base64DelegateSerde<E> implements ISerde<E> {

    private final ISerde<E> delegate;

    public Base64DelegateSerde(final ISerde<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public E fromBytes(final byte[] bytes) {
        final byte[] decodedBytes = Base64.decodeBase64(bytes);
        return delegate.fromBytes(decodedBytes);
    }

    @Override
    public byte[] toBytes(final E obj) {
        final byte[] bytes = delegate.toBytes(obj);
        final byte[] encodedBytes = Base64.encodeBase64(bytes);
        return encodedBytes;
    }

}
