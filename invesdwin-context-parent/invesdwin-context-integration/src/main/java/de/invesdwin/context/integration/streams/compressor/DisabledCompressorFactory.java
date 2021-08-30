package de.invesdwin.context.integration.streams.compressor;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledCompressorFactory implements ICompressorFactory {

    public static final DisabledCompressorFactory INSTANCE = new DisabledCompressorFactory();

    private DisabledCompressorFactory() {
    }

    @Override
    public OutputStream newCompressor(final OutputStream out, final boolean large) {
        return out;
    }

    @Override
    public InputStream newDecompressor(final InputStream in) {
        return in;
    }

}
