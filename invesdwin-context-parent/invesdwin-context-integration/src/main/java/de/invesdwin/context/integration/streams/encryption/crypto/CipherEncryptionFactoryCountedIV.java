package de.invesdwin.context.integration.streams.encryption.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.Immutable;
import javax.crypto.Cipher;

import org.apache.commons.crypto.cipher.CryptoCipher;

import de.invesdwin.context.integration.streams.encryption.EncryptingDelegateSerde;
import de.invesdwin.context.integration.streams.encryption.IEncryptionFactory;
import de.invesdwin.context.integration.streams.encryption.crypto.pool.MutableIvParameterSpec;
import de.invesdwin.context.integration.streams.random.CryptoRandomGenerator;
import de.invesdwin.context.integration.streams.random.CryptoRandomGeneratorObjectPool;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.ALazyDelegateInputStream;
import de.invesdwin.util.streams.ALazyDelegateOutputStream;
import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.OutputStreams;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

/**
 * Counted IV has a good speed while sending the IV over the wire for interoperability. This is not as secure as the
 * derived IV version.
 */
@Immutable
public class CipherEncryptionFactoryCountedIV implements IEncryptionFactory {

    private final ICipherAlgorithm algorithm;
    private final byte[] key;
    private final Key keyWrapped;
    private final byte[] initIV;
    private final AtomicLong ivCounter;

    public CipherEncryptionFactoryCountedIV(final ICipherAlgorithm algorithm, final byte[] key) {
        this.algorithm = algorithm;
        this.key = key;
        this.keyWrapped = algorithm.wrapKey(key);
        this.initIV = newInitIV(algorithm.getIvBytes());
        this.ivCounter = new AtomicLong();
        if (initIV.length != algorithm.getIvBytes()) {
            throw new IllegalArgumentException(
                    "initIV.length[" + initIV.length + "] != algorithm.getIvBytes[" + algorithm.getIvBytes() + "]");
        }
    }

    @Override
    public ICipherAlgorithm getAlgorithm() {
        return algorithm;
    }

    protected byte[] newInitIV(final int ivBytes) {
        final byte[] initIV = ByteBuffers.allocateByteArray(ivBytes);
        final CryptoRandomGenerator random = CryptoRandomGeneratorObjectPool.INSTANCE.borrowObject();
        try {
            random.nextBytes(initIV);
        } finally {
            CryptoRandomGeneratorObjectPool.INSTANCE.returnObject(random);
        }
        return initIV;
    }

    protected void calculateIV(final byte[] iv) {
        CipherEncryptionFactoryDerivedIV.calculateIV(initIV, ivCounter.incrementAndGet(), iv);
    }

    @Override
    public OutputStream newEncryptor(final OutputStream out) {
        return new ALazyDelegateOutputStream() {
            @Override
            protected OutputStream newDelegate() {
                //transmit the first IV through the buffer on first access, afterwards switch to the more efficient counting method
                final byte[] initIV = ByteBuffers.allocateByteArray(algorithm.getIvBytes());
                calculateIV(initIV);
                try {
                    OutputStreams.write(out, initIV);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
                return algorithm.newEncryptor(out, key, initIV);
            }
        };
    }

    @Override
    public InputStream newDecryptor(final InputStream in) {
        return new ALazyDelegateInputStream() {
            @Override
            protected InputStream newDelegate() {
                final byte[] initIV = ByteBuffers.allocateByteArray(algorithm.getIvBytes());
                try {
                    InputStreams.readFully(in, initIV);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
                return algorithm.newDecryptor(in, key, initIV);
            }
        };
    }

    @Override
    public int encrypt(final IByteBuffer src, final IByteBuffer dest) {
        final CryptoCipher cipher = algorithm.getCipherPool().borrowObject();
        final MutableIvParameterSpec iv = algorithm.getIvParameterSpecPool().borrowObject();
        //each message should be encrypted with a unique IV, the IV can be transmitted unencrypted with the message
        //use the streaming encryptor/decryptor for a solution with less overhead
        calculateIV(iv.getIV());
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyWrapped, algorithm.wrapIv(iv));
            dest.putBytes(0, iv.getIV());
            final IByteBuffer payloadBuffer = dest.sliceFrom(algorithm.getIvBytes());
            final int length = cipher.doFinal(src.asNioByteBuffer(), payloadBuffer.asNioByteBuffer());
            return algorithm.getIvBytes() + length;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            algorithm.getIvParameterSpecPool().returnObject(iv);
            algorithm.getCipherPool().returnObject(cipher);
        }
    }

    @Override
    public int decrypt(final IByteBuffer src, final IByteBuffer dest) {
        final CryptoCipher cipher = algorithm.getCipherPool().borrowObject();
        final MutableIvParameterSpec iv = algorithm.getIvParameterSpecPool().borrowObject();
        try {
            src.getBytes(0, iv.getIV());
            cipher.init(Cipher.DECRYPT_MODE, keyWrapped, algorithm.wrapIv(iv));
            final IByteBuffer payloadBuffer = src.sliceFrom(algorithm.getIvBytes());
            final int length = cipher.doFinal(payloadBuffer.asNioByteBuffer(), dest.asNioByteBuffer());
            return length;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            algorithm.getIvParameterSpecPool().returnObject(iv);
            algorithm.getCipherPool().returnObject(cipher);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public <T> ISerde<T> maybeWrap(final ISerde<T> serde) {
        return new EncryptingDelegateSerde<>(serde, this);
    }

}