package de.invesdwin.context.integration.streams.encryption.aes;

import java.io.InputStream;
import java.io.OutputStream;

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
    private final byte[] key;
    private final byte[] iv;

    public AesEncryptionFactory(final AesAlgorithm algorithm, final byte[] key, final byte[] iv) {
        this.algorithm = algorithm;
        this.key = key;
        this.iv = iv;
    }

    @Override
    public OutputStream newEncryptor(final OutputStream out) {
        return algorithm.newEncryptor(out, key, iv);
    }

    @Override
    public InputStream newDecryptor(final InputStream in) {
        return algorithm.newDecryptor(in, key, iv);
    }

    @Override
    public int encrypt(final IByteBuffer src, final IByteBuffer dest) {
        try (CryptoCipher cipher = algorithm.newCipher()) {
            cipher.init(Cipher.ENCRYPT_MODE, algorithm.newKey(key), algorithm.newIv(iv));
            return cipher.doFinal(src.asNioByteBuffer(), dest.asNioByteBuffer());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int decrypt(final IByteBuffer src, final IByteBuffer dest) {
        try (CryptoCipher cipher = algorithm.newCipher()) {
            cipher.init(Cipher.DECRYPT_MODE, algorithm.newKey(key), algorithm.newIv(iv));
            return cipher.doFinal(src.asNioByteBuffer(), dest.asNioByteBuffer());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public <T> ISerde<T> maybeWrap(final ISerde<T> serde) {
        return new EncryptingDelegateSerde<>(serde, this);
    }

}
