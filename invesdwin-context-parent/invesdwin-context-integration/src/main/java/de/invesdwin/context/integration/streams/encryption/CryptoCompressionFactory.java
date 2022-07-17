package de.invesdwin.context.integration.streams.encryption;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;
import javax.crypto.Cipher;

import org.apache.commons.crypto.cipher.CryptoCipher;

import de.invesdwin.context.integration.streams.compression.CompressingDelegateSerde;
import de.invesdwin.context.integration.streams.compression.ICompressionFactory;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class CryptoCompressionFactory implements ICompressionFactory {

    private final CryptoAlgorithm algorithm;
    private final byte[] key;
    private final byte[] iv;

    public CryptoCompressionFactory(final CryptoAlgorithm algorithm, final byte[] key, final byte[] iv) {
        this.algorithm = algorithm;
        this.key = key;
        this.iv = iv;
    }

    @Override
    public OutputStream newCompressor(final OutputStream out, final boolean large) {
        return algorithm.newEncryptor(out, key, iv);
    }

    @Override
    public InputStream newDecompressor(final InputStream in) {
        return algorithm.newDecryptor(in, key, iv);
    }

    @Override
    public int compress(final IByteBuffer src, final IByteBuffer dest) {
        try (CryptoCipher cipher = algorithm.newCipher()) {
            cipher.init(Cipher.ENCRYPT_MODE, algorithm.newKey(key), algorithm.newIv(iv));
            return cipher.doFinal(src.asNioByteBuffer(), dest.asNioByteBuffer());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int decompress(final IByteBuffer src, final IByteBuffer dest) {
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
        return new CompressingDelegateSerde<>(serde, this);
    }

}
