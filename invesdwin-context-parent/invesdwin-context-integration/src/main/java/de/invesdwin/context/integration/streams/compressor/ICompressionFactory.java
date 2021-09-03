package de.invesdwin.context.integration.streams.compressor;

import java.io.InputStream;
import java.io.OutputStream;

public interface ICompressionFactory {

    OutputStream newCompressor(OutputStream out, boolean large);

    InputStream newDecompressor(InputStream in);

}
