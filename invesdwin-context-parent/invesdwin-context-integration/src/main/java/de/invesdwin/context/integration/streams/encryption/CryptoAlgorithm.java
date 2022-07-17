package de.invesdwin.context.integration.streams.encryption;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.annotation.concurrent.Immutable;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.stream.CryptoInputStream;
import org.apache.commons.crypto.stream.CryptoOutputStream;
import org.apache.commons.crypto.stream.CtrCryptoInputStream;
import org.apache.commons.crypto.stream.CtrCryptoOutputStream;
import org.apache.commons.crypto.utils.Utils;

import de.invesdwin.context.system.properties.SystemProperties;

/**
 * https://stackoverflow.com/questions/1220751/how-to-choose-an-aes-encryption-mode-cbc-ecb-ctr-ocb-cfb
 * 
 * https://crypto.stackexchange.com/questions/48628/why-is-padding-used-in-cbc-mode
 *
 * https://crypto.stackexchange.com/questions/41601/aes-gcm-recommended-iv-size-why-12-bytes
 * 
 * https://crypto.stackexchange.com/questions/2173/how-to-calculate-an-iv-when-i-have-a-shared-private-key
 */
@Immutable
public enum CryptoAlgorithm {
    /**
     * encryption only, full blocks, not streaming capable
     */
    AES_CBC_PKCS5Padding("AES/CBC/PKCS5Padding", 16) {
        @Override
        public OutputStream newEncryptor(final OutputStream out, final byte[] key, final byte[] iv) {
            try {
                return new CryptoOutputStream(getTransformation(), SystemProperties.SYSTEM_PROPERTIES, out, newKey(key),
                        newIv(iv));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public InputStream newDecryptor(final InputStream in, final byte[] key, final byte[] iv) {
            try {
                return new CryptoInputStream(getTransformation(), SystemProperties.SYSTEM_PROPERTIES, in, newKey(key),
                        newIv(iv));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public AlgorithmParameterSpec newIv(final byte[] iv) {
            return new IvParameterSpec(iv);
        }
    },
    /**
     * encryption only, streaming capable
     */
    AES_CTR_NoPadding("AES/CTR/NoPadding", 16) {
        @Override
        public OutputStream newEncryptor(final OutputStream out, final byte[] key, final byte[] iv) {
            try {
                return new CtrCryptoOutputStream(SystemProperties.SYSTEM_PROPERTIES, out, key, iv);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public InputStream newDecryptor(final InputStream in, final byte[] key, final byte[] iv) {
            try {
                return new CtrCryptoInputStream(SystemProperties.SYSTEM_PROPERTIES, in, key, iv);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public AlgorithmParameterSpec newIv(final byte[] iv) {
            return new IvParameterSpec(iv);
        }
    },
    /**
     * authenticated encryption, should be streaming capable but there is no impl in commons-crypto, slower than CTR
     * 
     * https://blog.synopse.info/?post/2021/02/13/Fastest-AES-PRNG%2C-AES-CTR-and-AES-GCM-Delphi-implementation
     * 
     * https://stackoverflow.com/questions/54659935/java-aes-gcm-very-slow-compared-to-aes-ctr
     */
    AES_GCM_NoPadding("AES/GCM/NoPadding", 12) {
        @Override
        public OutputStream newEncryptor(final OutputStream out, final byte[] key, final byte[] iv) {
            throw new UnsupportedOperationException("streams not yet supported for GCM in commons-crypto");
        }

        @Override
        public InputStream newDecryptor(final InputStream in, final byte[] key, final byte[] iv) {
            throw new UnsupportedOperationException("streams not yet supported for GCM in commons-crypto");
        }

        @Override
        public AlgorithmParameterSpec newIv(final byte[] iv) {
            return new GCMParameterSpec(CryptoKeyLength._128.getBits(), iv);
        }
    };

    private final String transformation;
    private final int ivBytes;

    CryptoAlgorithm(final String transformation, final int ivBytes) {
        this.transformation = transformation;
        this.ivBytes = ivBytes;
    }

    @Override
    public String toString() {
        return transformation;
    }

    public String getTransformation() {
        return transformation;
    }

    public int getIvBytes() {
        return ivBytes;
    }

    public abstract OutputStream newEncryptor(OutputStream out, byte[] key, byte[] iv);

    public abstract InputStream newDecryptor(InputStream in, byte[] key, byte[] iv);

    public Key newKey(final byte[] key) {
        return new SecretKeySpec(key, "AES");
    }

    public abstract AlgorithmParameterSpec newIv(byte[] iv);

    public CryptoCipher newCipher() {
        try {
            return Utils.getCipherInstance(getTransformation(), SystemProperties.SYSTEM_PROPERTIES);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
