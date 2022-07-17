package de.invesdwin.context.integration.streams.encryption;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.streams.compression.ICompressionFactory;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class EncryptingDelegateSerde<E> implements ISerde<E> {

    private final ISerde<E> delegate;
    private final IEncryptionFactory encryptionFactory;

    /**
     * WARNING: for internal use only. Use maybeWrap() instead.
     */
    @Deprecated
    public EncryptingDelegateSerde(final ISerde<E> delegate, final IEncryptionFactory encryptionFactory) {
        Assertions.assertThat(delegate).isNotInstanceOf(EncryptingDelegateSerde.class);
        this.delegate = delegate;
        this.encryptionFactory = encryptionFactory;
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
        if (encryptionFactory == DisabledEncryptionFactory.INSTANCE) {
            //we can save a copy here
            return delegate.fromBuffer(buffer, length);
        } else {
            final IByteBuffer decryptedBuffer = ByteBuffers.EXPANDABLE_POOL.borrowObject();
            try {
                final int decryptedLength = encryptionFactory.encrypt(buffer, decryptedBuffer);
                return delegate.fromBuffer(decryptedBuffer, decryptedLength);
            } finally {
                ByteBuffers.EXPANDABLE_POOL.returnObject(decryptedBuffer);
            }
        }
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final E obj) {
        if (obj == null) {
            return 0;
        }
        if (encryptionFactory == DisabledEncryptionFactory.INSTANCE) {
            //we can save a copy here
            return delegate.toBuffer(buffer, obj);
        } else {
            final IByteBuffer decryptedBuffer = ByteBuffers.EXPANDABLE_POOL.borrowObject();
            try {
                final int decryptedLength = delegate.toBuffer(decryptedBuffer, obj);
                return encryptionFactory.decrypt(decryptedBuffer.sliceTo(decryptedLength), buffer);
            } finally {
                ByteBuffers.EXPANDABLE_POOL.returnObject(decryptedBuffer);
            }
        }
    }

    public static <T> ISerde<T> maybeWrap(final ISerde<T> delegate, final ICompressionFactory compressionFactory) {
        return compressionFactory.maybeWrap(delegate);
    }

}
