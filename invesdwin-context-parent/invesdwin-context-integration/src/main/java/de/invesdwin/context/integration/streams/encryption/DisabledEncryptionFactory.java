package de.invesdwin.context.integration.streams.encryption;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.streams.encryption.crypto.ICipherAlgorithm;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.pool.buffered.PooledFastBufferedOutputStream;

@Immutable
public final class DisabledEncryptionFactory implements IEncryptionFactory {

    public static final DisabledEncryptionFactory INSTANCE = new DisabledEncryptionFactory();

    private DisabledEncryptionFactory() {
    }

    @Override
    public ICipherAlgorithm getAlgorithm() {
        return null;
    }

    @Override
    public OutputStream newEncryptor(final OutputStream out) {
        //buffering is better for write throughput to file
        return PooledFastBufferedOutputStream.newInstance(out);
    }

    @Override
    public InputStream newDecryptor(final InputStream in) {
        return in;
    }

    @Override
    public int encrypt(final IByteBuffer src, final IByteBuffer dest) {
        dest.putBytes(0, src);
        return src.capacity();
    }

    @Override
    public int decrypt(final IByteBuffer src, final IByteBuffer dest) {
        dest.putBytes(0, src);
        return src.capacity();
    }

    @Override
    public <T> ISerde<T> maybeWrap(final ISerde<T> serde) {
        return serde;
    }

}
