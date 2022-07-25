package de.invesdwin.context.integration.compression.lz4;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.compression.CompressionDelegateSerde;
import de.invesdwin.context.integration.compression.ICompressionFactory;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.pool.APooledInputStream;
import de.invesdwin.util.streams.pool.APooledOutputStream;
import net.jpountz.lz4.LZ4Compressor;

@Immutable
public final class HighLZ4CompressionFactory implements ICompressionFactory {

    public static final HighLZ4CompressionFactory INSTANCE = new HighLZ4CompressionFactory();

    private HighLZ4CompressionFactory() {
    }

    @Override
    public APooledOutputStream newCompressor(final OutputStream out, final boolean large) {
        if (large) {
            return LZ4Streams.newLargeHighLZ4OutputStream(out);
        } else {
            return LZ4Streams.newHighLZ4OutputStream(out);
        }
    }

    @Override
    public APooledInputStream newDecompressor(final InputStream in) {
        return LZ4Streams.newDefaultLZ4InputStream(in);
    }

    @Override
    public int compress(final IByteBuffer src, final IByteBuffer dest) {
        final LZ4Compressor compressor = LZ4Streams.newHighLZ4Compressor();
        return LZ4Streams.compress(compressor, src, dest);
    }

    @Override
    public int decompress(final IByteBuffer src, final IByteBuffer dest) {
        return LZ4Streams.decompress(src, dest);
    }

    @SuppressWarnings("deprecation")
    @Override
    public <T> ISerde<T> maybeWrap(final ISerde<T> serde) {
        return new CompressionDelegateSerde<>(serde, this);
    }

}
