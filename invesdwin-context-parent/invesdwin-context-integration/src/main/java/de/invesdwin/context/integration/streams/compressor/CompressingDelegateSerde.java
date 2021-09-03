package de.invesdwin.context.integration.streams.compressor;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.streams.buffer.ByteBuffers;
import de.invesdwin.util.streams.buffer.IByteBuffer;

@Immutable
public class CompressingDelegateSerde<E> implements ISerde<E> {

    private final ISerde<E> delegate;
    private final ICompressionFactory compressionFactory;

    /**
     * WARNING: for internal use only. Use maybeWrap() instead.
     */
    @Deprecated
    public CompressingDelegateSerde(final ISerde<E> delegate, final ICompressionFactory compressionFactory) {
        Assertions.assertThat(delegate).isNotInstanceOf(CompressingDelegateSerde.class);
        this.delegate = delegate;
        this.compressionFactory = compressionFactory;
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
        if (compressionFactory == DisabledCompressionFactory.INSTANCE) {
            //we can save a copy here
            return delegate.fromBuffer(buffer, length);
        } else {
            final IByteBuffer decompressedBuffer = ByteBuffers.EXPANDABLE_POOL.borrowObject();
            try {
                final int decompressedLength = compressionFactory.decompress(buffer, decompressedBuffer);
                return delegate.fromBuffer(decompressedBuffer, decompressedLength);
            } finally {
                ByteBuffers.EXPANDABLE_POOL.returnObject(decompressedBuffer);
            }
        }
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final E obj) {
        if (obj == null) {
            return 0;
        }
        if (compressionFactory == DisabledCompressionFactory.INSTANCE) {
            //we can save a copy here
            return delegate.toBuffer(buffer, obj);
        } else {
            final IByteBuffer compressedBuffer = ByteBuffers.EXPANDABLE_POOL.borrowObject();
            try {
                final int decompressedLength = delegate.toBuffer(buffer, obj);
                return compressionFactory.compress(buffer.sliceTo(decompressedLength), compressedBuffer);
            } finally {
                ByteBuffers.EXPANDABLE_POOL.returnObject(compressedBuffer);
            }
        }
    }

    public static <T> ISerde<T> maybeWrap(final ISerde<T> delegate, final ICompressionFactory compressionFactory) {
        return compressionFactory.maybeWrap(delegate);
    }

}
