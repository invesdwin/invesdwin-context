package de.invesdwin.context.integration.streams.authentication;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class AuthenticatingDelegateSerde<E> implements ISerde<E> {

    private final ISerde<E> delegate;
    private final IAuthenticationFactory authenticationFactory;

    /**
     * WARNING: for internal use only. Use maybeWrap() instead.
     */
    @Deprecated
    public AuthenticatingDelegateSerde(final ISerde<E> delegate, final IAuthenticationFactory authenticationFactory) {
        Assertions.assertThat(delegate).isNotInstanceOf(AuthenticatingDelegateSerde.class);
        this.delegate = delegate;
        this.authenticationFactory = authenticationFactory;
    }

    @Override
    public E fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final E obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public E fromBuffer(final IByteBuffer buffer, final int length) {
        if (length == 0) {
            return null;
        }
        if (authenticationFactory == DisabledAuthenticationFactory.INSTANCE) {
            //we can save a copy here
            return delegate.fromBuffer(buffer, length);
        } else {
            final IByteBuffer signedBuffer = ByteBuffers.EXPANDABLE_POOL.borrowObject();
            try {
                final int signedLength = authenticationFactory.sign(buffer, signedBuffer);
                return delegate.fromBuffer(signedBuffer, signedLength);
            } finally {
                ByteBuffers.EXPANDABLE_POOL.returnObject(signedBuffer);
            }
        }
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final E obj) {
        if (obj == null) {
            return 0;
        }
        if (authenticationFactory == DisabledAuthenticationFactory.INSTANCE) {
            //we can save a copy here
            return delegate.toBuffer(buffer, obj);
        } else {
            final IByteBuffer verifiedBuffer = ByteBuffers.EXPANDABLE_POOL.borrowObject();
            try {
                final int verifiedLength = delegate.toBuffer(verifiedBuffer, obj);
                return authenticationFactory.verify(verifiedBuffer.sliceTo(verifiedLength), buffer);
            } finally {
                ByteBuffers.EXPANDABLE_POOL.returnObject(verifiedBuffer);
            }
        }
    }

    public static <T> ISerde<T> maybeWrap(final ISerde<T> delegate,
            final IAuthenticationFactory authenticationFactory) {
        return authenticationFactory.maybeWrap(delegate);
    }

}
