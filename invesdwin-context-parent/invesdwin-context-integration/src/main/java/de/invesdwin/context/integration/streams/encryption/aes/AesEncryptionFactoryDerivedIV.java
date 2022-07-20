package de.invesdwin.context.integration.streams.encryption.aes;

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
import de.invesdwin.context.integration.streams.encryption.pool.MutableIvParameterSpec;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.ALazyDelegateInputStream;
import de.invesdwin.util.streams.ALazyDelegateOutputStream;
import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.OutputStreams;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

/**
 * Derived IV is the best compromise between security and speed. It does not send the IV over the wire, instead it only
 * sends the counter (sequence number which has half the length of the IV). It expects both sides to use the same key
 * agreement protocol for the derivedIV and counted derivations of it.
 * 
 * Key derivation techniques are: Password+PBKDF2+HKDFexpands or Random+HKDFextract+HKDFexpands
 * 
 * We can derive AES-KEY, AES-IV, MAC-KEY from the initial Password or Random. scrypt and bcrypt are alternatives to
 * PBKDF2
 */
@Immutable
public class AesEncryptionFactoryDerivedIV implements IEncryptionFactory {

    private final AesAlgorithm algorithm;
    private final byte[] key;
    private final Key keyWrapped;
    private final byte[] initIV;
    private final AtomicLong ivCounter;

    public AesEncryptionFactoryDerivedIV(final AesAlgorithm algorithm, final byte[] derivedKey,
            final byte[] derivedIv) {
        this.algorithm = algorithm;
        this.key = derivedKey;
        this.keyWrapped = AesAlgorithm.wrapKey(derivedKey);
        this.initIV = derivedIv;
        this.ivCounter = new AtomicLong();
        if (initIV.length != algorithm.getIvBytes()) {
            throw new IllegalArgumentException(
                    "initIV.length[" + initIV.length + "] != algorithm.getIvBytes[" + algorithm.getIvBytes() + "]");
        }
    }

    public AesAlgorithm getAlgorithm() {
        return algorithm;
    }

    protected long calculateIV(final byte[] iv) {
        final long counter = ivCounter.incrementAndGet();
        calculateIV(initIV, counter, iv);
        return counter;
    }

    public static void calculateIV(final byte[] initIV, final long pCounter, final byte[] iv) {
        long counter = pCounter;
        int i = iv.length; // IV length
        int j = 0; // counter bytes index
        int sum = 0;
        while (i-- > 0) {
            // (sum >>> Byte.SIZE) is the carry for addition
            sum = (initIV[i] & 0xff) + (sum >>> Byte.SIZE); // NOPMD
            if (j++ < 8) { // Big-endian, and long is 8 bytes length
                sum += (byte) counter & 0xff;
                counter >>>= 8;
            }
            iv[i] = (byte) sum;
        }
    }

    @Override
    public OutputStream newEncryptor(final OutputStream out) {
        return new ALazyDelegateOutputStream() {
            @Override
            protected OutputStream newDelegate() {
                //transmit the first IV through the buffer on first access, afterwards switch to the more efficient counting method
                final byte[] initIV = ByteBuffers.allocateByteArray(algorithm.getIvBytes());
                final long counter = calculateIV(initIV);
                try {
                    OutputStreams.writeLong(out, counter);
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
                final long counter;
                try {
                    counter = InputStreams.readLong(in);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
                final byte[] countedIV = ByteBuffers.allocateByteArray(algorithm.getIvBytes());
                calculateIV(initIV, counter, countedIV);
                return algorithm.newDecryptor(in, key, countedIV);
            }
        };
    }

    @Override
    public int encrypt(final IByteBuffer src, final IByteBuffer dest) {
        final CryptoCipher cipher = algorithm.getCipherPool().borrowObject();
        final MutableIvParameterSpec iv = algorithm.getIvParameterSpecPool().borrowObject();
        //each message should be encrypted with a unique IV, the IV can be transmitted unencrypted with the message
        //use the streaming encryptor/decryptor for a solution with less overhead
        final long counter = calculateIV(iv.getIV());
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyWrapped, algorithm.wrapIv(iv));
            dest.putLong(0, counter);
            final IByteBuffer payloadBuffer = dest.sliceFrom(Long.BYTES);
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
            final long counter = src.getLong(0);
            calculateIV(initIV, counter, iv.getIV());
            cipher.init(Cipher.DECRYPT_MODE, keyWrapped, algorithm.wrapIv(iv));
            final IByteBuffer payloadBuffer = src.sliceFrom(Long.BYTES);
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
