package de.invesdwin.context.integration.streams.encryption.aes;

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

@Immutable
public class AesEncryptionFactory implements IEncryptionFactory {

    private final AesAlgorithm algorithm;
    private final byte[] keyBytes;
    private final Key key;
    private final byte[] ivBytes;

    public AesEncryptionFactory(final AesAlgorithm algorithm, final byte[] keyBytes, final byte[] ivBytes) {
        this.algorithm = algorithm;
        this.keyBytes = keyBytes;
        this.key = algorithm.newKey(keyBytes);
        this.ivBytes = ivBytes;
    }

    @Override
    public OutputStream newEncryptor(final OutputStream out) {
        return algorithm.newEncryptor(out, keyBytes, ivBytes);
    }

    @Override
    public InputStream newDecryptor(final InputStream in) {
        return algorithm.newDecryptor(in, keyBytes, ivBytes);
    }

    @Override
    public int encrypt(final IByteBuffer src, final IByteBuffer dest) {
        final CryptoCipher cipher = algorithm.getCipherPool().borrowObject();
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, algorithm.newIv(ivBytes));
            return cipher.doFinal(src.asNioByteBuffer(), dest.asNioByteBuffer());
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
            cipher.init(Cipher.DECRYPT_MODE, key, algorithm.newIv(ivBytes));
            return cipher.doFinal(src.asNioByteBuffer(), dest.asNioByteBuffer());
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
