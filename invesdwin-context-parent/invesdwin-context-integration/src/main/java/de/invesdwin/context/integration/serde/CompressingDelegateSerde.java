package de.invesdwin.context.integration.serde;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.io.IOUtils;

import de.invesdwin.context.integration.streams.LZ4Streams;
import de.invesdwin.norva.beanpath.CountingOutputStream;
import de.invesdwin.util.lang.buffer.ByteBuffers;
import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.math.Bytes;
import io.netty.util.concurrent.FastThreadLocal;

@Immutable
public class CompressingDelegateSerde<E> implements ISerde<E> {

    private static final FastThreadLocal<IByteBuffer> EXPANDABLE_BUFFER_REF = new FastThreadLocal<IByteBuffer>() {
        @Override
        protected IByteBuffer initialValue() throws Exception {
            return ByteBuffers.allocateExpandable();
        }
    };

    private final ISerde<E> delegate;

    public CompressingDelegateSerde(final ISerde<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public E fromBuffer(final IByteBuffer buffer) {
        if (buffer.capacity() == 0) {
            return null;
        }
        try {
            final InputStream in = newDecompressor(buffer.asInputStream());
            final IByteBuffer buf = EXPANDABLE_BUFFER_REF.get();
            final int length = IOUtils.copy(in, buf.asOutputStream());
            in.close();
            return delegate.fromBuffer(buf.sliceTo(length));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public E fromBytes(final byte[] bytes) {
        if (bytes.length == 0) {
            return null;
        }
        try {
            final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            final InputStream in = newDecompressor(bis);
            final IByteBuffer buf = EXPANDABLE_BUFFER_REF.get();
            final int length = IOUtils.copy(in, buf.asOutputStream());
            in.close();
            return delegate.fromBuffer(buf.sliceTo(length));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int toBuffer(final E obj, final IByteBuffer buffer) {
        if (obj == null) {
            return 0;
        }
        try {
            final CountingOutputStream cout = new CountingOutputStream(buffer.asOutputStream());
            final OutputStream out = newCompressor(cout);
            final IByteBuffer buf = EXPANDABLE_BUFFER_REF.get();
            final int length = delegate.toBuffer(obj, buf);
            IOUtils.copy(buf.asInputStreamTo(length), out);
            out.close();
            return cout.getCount();
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
            final OutputStream out = newCompressor(bos);
            final IByteBuffer buf = EXPANDABLE_BUFFER_REF.get();
            final int length = delegate.toBuffer(obj, buf);
            IOUtils.copy(buf.asInputStreamTo(length), out);
            out.close();
            return bos.toByteArray();
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
