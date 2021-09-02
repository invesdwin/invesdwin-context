package de.invesdwin.context.integration.serde;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.io.IOUtils;

import de.invesdwin.context.integration.streams.compressor.lz4.LZ4Streams;
import de.invesdwin.norva.beanpath.CountingOutputStream;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.buffer.ByteBuffers;
import de.invesdwin.util.streams.buffer.IByteBuffer;

@Immutable
public class CompressingDelegateSerde<E> implements ISerde<E> {

    private final ISerde<E> delegate;

    public CompressingDelegateSerde(final ISerde<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public E fromBytes(final byte[] bytes) {
        if (bytes.length == 0) {
            return null;
        }
        try {
            final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            try (InputStream in = newDecompressor(bis)) {
                final IByteBuffer buf = ByteBuffers.EXPANDABLE_POOL.borrowObject();
                try {
                    final int length = IOUtils.copy(in, buf.asOutputStream());
                    return delegate.fromBuffer(buf.sliceTo(length), length);
                } finally {
                    ByteBuffers.EXPANDABLE_POOL.returnObject(buf);
                }
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] toBytes(final E obj) {
        if (obj == null) {
            return Bytes.EMPTY_ARRAY;
        }
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (OutputStream out = newCompressor(bos)) {
                final IByteBuffer buf = ByteBuffers.EXPANDABLE_POOL.borrowObject();
                try {
                    final int length = delegate.toBuffer(buf, obj);
                    IOUtils.copy(buf.asInputStreamTo(length), out);
                    return bos.toByteArray();
                } finally {
                    ByteBuffers.EXPANDABLE_POOL.returnObject(buf);
                }
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public E fromBuffer(final IByteBuffer buffer, final int length) {
        if (length == 0) {
            return null;
        }
        try {
            try (InputStream in = newDecompressor(buffer.asInputStream())) {
                final IByteBuffer buf = ByteBuffers.EXPANDABLE_POOL.borrowObject();
                try {
                    final int actualLength = IOUtils.copy(in, buf.asOutputStream());
                    return delegate.fromBuffer(buf.sliceTo(actualLength), actualLength);
                } finally {
                    ByteBuffers.EXPANDABLE_POOL.returnObject(buf);
                }
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final E obj) {
        if (obj == null) {
            return 0;
        }
        try {
            final CountingOutputStream cout = new CountingOutputStream(buffer.asOutputStream());
            try (OutputStream out = newCompressor(cout)) {
                final IByteBuffer buf = ByteBuffers.EXPANDABLE_POOL.borrowObject();
                try {
                    final int length = delegate.toBuffer(buf, obj);
                    IOUtils.copy(buf.asInputStreamTo(length), out);
                    return cout.getCount();
                } finally {
                    ByteBuffers.EXPANDABLE_POOL.returnObject(buf);
                }
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected OutputStream newCompressor(final OutputStream out) {
        return LZ4Streams.newDefaultLZ4OutputStream(out);
    }

    protected InputStream newDecompressor(final InputStream in) {
        return LZ4Streams.newDefaultLZ4InputStream(in);
    }

}
