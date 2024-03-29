package de.invesdwin.context.integration.compression;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.pool.buffered.PooledFastBufferedOutputStream;

@Immutable
public final class DisabledCompressionFactory implements ICompressionFactory {

    public static final DisabledCompressionFactory INSTANCE = new DisabledCompressionFactory();

    private DisabledCompressionFactory() {
    }

    @Override
    public OutputStream newCompressor(final OutputStream out, final boolean large) {
        //buffering is better for write throughput to file
        return PooledFastBufferedOutputStream.newInstance(out);
    }

    @Override
    public InputStream newDecompressor(final InputStream in) {
        return in;
    }

    @Override
    public int compress(final IByteBuffer src, final IByteBuffer dest) {
        dest.putBytes(0, src);
        return src.capacity();
    }

    @Override
    public int decompress(final IByteBuffer src, final IByteBuffer dest) {
        dest.putBytes(0, src);
        return src.capacity();
    }

    @Override
    public <T> ISerde<T> maybeWrap(final ISerde<T> serde) {
        return serde;
    }

}
