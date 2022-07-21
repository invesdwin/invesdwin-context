package de.invesdwin.context.integration.streams.encryption;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.streams.compression.ICompressionFactory;
import de.invesdwin.context.integration.streams.encryption.cipher.ICipherAlgorithm;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class CompressionAsEncryptionFactory implements IEncryptionFactory {

    private final ICompressionFactory compressionFactory;

    public CompressionAsEncryptionFactory(final ICompressionFactory compressionFactory) {
        this.compressionFactory = compressionFactory;
    }

    @Override
    public ICipherAlgorithm getAlgorithm() {
        return null;
    }

    @Override
    public OutputStream newEncryptor(final OutputStream out) {
        return compressionFactory.newCompressor(out, false);
    }

    @Override
    public InputStream newDecryptor(final InputStream in) {
        return compressionFactory.newDecompressor(in);
    }

    @Override
    public int encrypt(final IByteBuffer src, final IByteBuffer dest) {
        return compressionFactory.compress(src, dest);
    }

    @Override
    public int decrypt(final IByteBuffer src, final IByteBuffer dest) {
        return compressionFactory.decompress(src, dest);
    }

    @Override
    public <T> ISerde<T> maybeWrap(final ISerde<T> delegate) {
        return compressionFactory.maybeWrap(delegate);
    }

}
