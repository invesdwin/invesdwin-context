package de.invesdwin.context.integration.streams.compressor;

import java.io.InputStream;
import java.io.OutputStream;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

public interface ICompressionFactory {

    OutputStream newCompressor(OutputStream out, boolean large);

    InputStream newDecompressor(InputStream in);

    int compress(IByteBuffer src, IByteBuffer dest);

    int decompress(IByteBuffer src, IByteBuffer dest);

    <T> ISerde<T> maybeWrap(ISerde<T> delegate);

}
