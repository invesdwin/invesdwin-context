package de.invesdwin.context.integration.streams.compressor.lz4;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.streams.compressor.ICompressionFactory;

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

}
