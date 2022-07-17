package de.invesdwin.context.integration.streams.compression;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.streams.encryption.IEncryptionFactory;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class EncryptionAsCompressionFactory implements ICompressionFactory {

    private final IEncryptionFactory encryptionFactory;

    public EncryptionAsCompressionFactory(final IEncryptionFactory encryptionFactory) {
        this.encryptionFactory = encryptionFactory;
    }

    @Override
    public OutputStream newCompressor(final OutputStream out, final boolean large) {
        return encryptionFactory.newEncryptor(out);
    }

    @Override
    public InputStream newDecompressor(final InputStream in) {
        return encryptionFactory.newDecryptor(in);
    }

    @Override
    public int compress(final IByteBuffer src, final IByteBuffer dest) {
        return encryptionFactory.encrypt(src, dest);
    }

    @Override
    public int decompress(final IByteBuffer src, final IByteBuffer dest) {
        return encryptionFactory.decrypt(src, dest);
    }

    @Override
    public <T> ISerde<T> maybeWrap(final ISerde<T> delegate) {
        return encryptionFactory.maybeWrap(delegate);
    }

}
