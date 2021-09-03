package de.invesdwin.context.integration.streams.compressor.lz4;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.streams.compressor.ICompressionFactory;

@Immutable
public final class HighLZ4CompressionFactory implements ICompressionFactory {

    public static final HighLZ4CompressionFactory INSTANCE = new HighLZ4CompressionFactory();

    private HighLZ4CompressionFactory() {
    }

    @Override
    public OutputStream newCompressor(final OutputStream out, final boolean large) {
        if (large) {
            return LZ4Streams.newLargeHighLZ4OutputStream(out);
        } else {
            return LZ4Streams.newHighLZ4OutputStream(out);
        }
    }

    @Override
    public InputStream newDecompressor(final InputStream in) {
        return LZ4Streams.newDefaultLZ4InputStream(in);
    }

}
