package de.invesdwin.context.integration.streams.compressor.lz4;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.streams.compressor.CompressingDelegateSerde;
import de.invesdwin.context.integration.streams.compressor.ICompressionFactory;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.IByteBuffer;
import net.jpountz.lz4.LZ4Compressor;

@Immutable
public final class FastLZ4CompressionFactory implements ICompressionFactory {

    public static final FastLZ4CompressionFactory INSTANCE = new FastLZ4CompressionFactory();

    private FastLZ4CompressionFactory() {
    }

    @Override
    public OutputStream newCompressor(final OutputStream out, final boolean large) {
        if (large) {
            return LZ4Streams.newLargeFastLZ4OutputStream(out);
        } else {
            return LZ4Streams.newFastLZ4OutputStream(out);
        }
    }

    @Override
    public InputStream newDecompressor(final InputStream in) {
        return LZ4Streams.newDefaultLZ4InputStream(in);
    }

    @Override
    public int compress(final IByteBuffer src, final IByteBuffer dest) {
        final LZ4Compressor compressor = LZ4Streams.newFastLZ4Compressor();
        return LZ4Streams.compress(compressor, src, dest);
    }

    @Override
    public int decompress(final IByteBuffer src, final IByteBuffer dest) {
        return LZ4Streams.decompress(src, dest);
    }

    @SuppressWarnings("deprecation")
    @Override
    public <T> ISerde<T> maybeWrap(final ISerde<T> serde) {
        return new CompressingDelegateSerde<>(serde, this);
    }

}