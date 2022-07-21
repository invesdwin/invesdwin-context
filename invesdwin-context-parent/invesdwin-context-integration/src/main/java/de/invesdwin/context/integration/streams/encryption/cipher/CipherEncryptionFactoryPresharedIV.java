package de.invesdwin.context.integration.streams.encryption.cipher;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

import javax.annotation.concurrent.Immutable;
import javax.crypto.Cipher;

import org.apache.commons.crypto.cipher.CryptoCipher;

import de.invesdwin.context.integration.streams.encryption.EncryptingDelegateSerde;
import de.invesdwin.context.integration.streams.encryption.IEncryptionFactory;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

/**
 * Less data on the wire because IV is not stored with the message, but requires the IV to be calculated or preshared
 * from the outside. Each message needs to have its unique IV normally. This can be achieved by mutating the presharedIV
 * byte array reference from the outside.
 */
@Immutable
public class CipherEncryptionFactoryPresharedIV implements IEncryptionFactory {

    private final ICipherAlgorithm algorithm;
    private final byte[] key;
    private final Key keyWrapped;
    private final byte[] presharedIv;

    public CipherEncryptionFactoryPresharedIV(final ICipherAlgorithm algorithm, final byte[] key,
            final byte[] presharedIv) {
        this.algorithm = algorithm;
        this.key = key;
        this.keyWrapped = algorithm.wrapKey(key);
        this.presharedIv = presharedIv;
        if (presharedIv.length != algorithm.getIvBytes()) {
            throw new IllegalArgumentException(
                    "iv.length[" + presharedIv.length + "] != algorithm.getIvBytes[" + algorithm.getIvBytes() + "]");
        }
    }

    @Override
    public ICipherAlgorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public OutputStream newEncryptor(final OutputStream out) {
        return algorithm.newEncryptor(out, key, presharedIv);
    }

    @Override
    public InputStream newDecryptor(final InputStream in) {
        return algorithm.newDecryptor(in, key, presharedIv);
    }

    @Override
    public int encrypt(final IByteBuffer src, final IByteBuffer dest) {
        final CryptoCipher cipher = algorithm.getCipherPool().borrowObject();
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyWrapped, algorithm.wrapIv(presharedIv));
            final IByteBuffer payloadBuffer = dest.sliceFrom(algorithm.getIvBytes());
            final int length = cipher.doFinal(src.asNioByteBuffer(), payloadBuffer.asNioByteBuffer());
            return algorithm.getIvBytes() + length;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            algorithm.getCipherPool().returnObject(cipher);
        }
    }

    @Override
    public int decrypt(final IByteBuffer src, final IByteBuffer dest) {
        final CryptoCipher cipher = algorithm.getCipherPool().borrowObject();
        try {
            cipher.init(Cipher.DECRYPT_MODE, keyWrapped, algorithm.wrapIv(presharedIv));
            final IByteBuffer payloadBuffer = src.sliceFrom(algorithm.getIvBytes());
            final int length = cipher.doFinal(payloadBuffer.asNioByteBuffer(), dest.asNioByteBuffer());
            return length;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            algorithm.getCipherPool().returnObject(cipher);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public <T> ISerde<T> maybeWrap(final ISerde<T> serde) {
        return new EncryptingDelegateSerde<>(serde, this);
    }

}
