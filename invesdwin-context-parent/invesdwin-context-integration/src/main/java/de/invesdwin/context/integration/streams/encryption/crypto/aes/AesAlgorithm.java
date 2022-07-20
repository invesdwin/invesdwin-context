package de.invesdwin.context.integration.streams.encryption.crypto.aes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.annotation.concurrent.Immutable;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.cipher.CryptoCipherFactory;
import org.apache.commons.crypto.stream.CryptoInputStream;
import org.apache.commons.crypto.stream.CryptoOutputStream;
import org.apache.commons.crypto.stream.CtrCryptoInputStream;
import org.apache.commons.crypto.stream.CtrCryptoOutputStream;
import org.apache.commons.crypto.utils.Utils;

import de.invesdwin.context.integration.streams.encryption.crypto.ICipherAlgorithm;
import de.invesdwin.context.integration.streams.encryption.crypto.pool.CryptoCipherObjectPool;
import de.invesdwin.context.integration.streams.encryption.crypto.pool.MutableIvParameterSpec;
import de.invesdwin.context.integration.streams.encryption.crypto.pool.MutableIvParameterSpecObjectPool;
import de.invesdwin.context.system.properties.SystemProperties;

/**
 * https://stackoverflow.com/questions/1220751/how-to-choose-an-aes-encryption-mode-cbc-ecb-ctr-ocb-cfb
 * 
 * https://crypto.stackexchange.com/questions/48628/why-is-padding-used-in-cbc-mode
 *
 * https://crypto.stackexchange.com/questions/41601/aes-gcm-recommended-iv-size-why-12-bytes
 * 
 * https://crypto.stackexchange.com/questions/2173/how-to-calculate-an-iv-when-i-have-a-shared-private-key
 * 
 * AES-GCM does authenticated encryption, should be streaming capable but there is no impl in commons-crypto, slower
 * than CTR
 * 
 * https://blog.synopse.info/?post/2021/02/13/Fastest-AES-PRNG%2C-AES-CTR-and-AES-GCM-Delphi-implementation
 * 
 * https://stackoverflow.com/questions/54659935/java-aes-gcm-very-slow-compared-to-aes-ctr
 */
@Immutable
public enum AesAlgorithm implements ICipherAlgorithm {
    /**
     * encryption only, full blocks, not streaming capable
     * 
     * input/output stream can only encrypt/decrypt full file and needs to be closed
     */
    AES_CBC_PKCS5Padding("AES/CBC/PKCS5Padding", CryptoCipherFactory.AES_BLOCK_SIZE) {
        @Override
        public OutputStream newEncryptor(final OutputStream out, final byte[] key, final byte[] iv) {
            try {
                return new CryptoOutputStream(getAlgorithm(), SystemProperties.SYSTEM_PROPERTIES, out, wrapKey(key),
                        wrapIv(iv));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public InputStream newDecryptor(final InputStream in, final byte[] key, final byte[] iv) {
            try {
                return new CryptoInputStream(getAlgorithm(), SystemProperties.SYSTEM_PROPERTIES, in, wrapKey(key),
                        wrapIv(iv));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public AlgorithmParameterSpec wrapIv(final byte[] iv) {
            return new MutableIvParameterSpec(iv);
        }

        @Override
        public AlgorithmParameterSpec wrapIv(final MutableIvParameterSpec iv) {
            return iv;
        }
    },
    /**
     * encryption only, streaming capable
     */
    AES_CTR_NoPadding("AES/CTR/NoPadding", CryptoCipherFactory.AES_BLOCK_SIZE) {
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
        public AlgorithmParameterSpec wrapIv(final byte[] iv) {
            return new MutableIvParameterSpec(iv);
        }

        @Override
        public AlgorithmParameterSpec wrapIv(final MutableIvParameterSpec iv) {
            return iv;
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
        public AlgorithmParameterSpec wrapIv(final byte[] iv) {
            return new GCMParameterSpec(AesKeyLength._128.getBits(), iv);
        }

        @Override
        public AlgorithmParameterSpec wrapIv(final MutableIvParameterSpec iv) {
            return new GCMParameterSpec(AesKeyLength._128.getBits(), iv.getIV());
        }
    };

    public static final AesAlgorithm DEFAULT = AES_CTR_NoPadding;

    private final String algorithm;
    private final int ivBytes;
    private final CryptoCipherObjectPool cipherPool;
    private final MutableIvParameterSpecObjectPool ivParameterSpecPool;

    AesAlgorithm(final String algorithm, final int ivBytes) {
        this.algorithm = algorithm;
        this.ivBytes = ivBytes;
        this.cipherPool = new CryptoCipherObjectPool(this);
        this.ivParameterSpecPool = new MutableIvParameterSpecObjectPool(ivBytes);
    }

    @Override
    public String toString() {
        return algorithm;
    }

    @Override
    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public int getIvBytes() {
        return ivBytes;
    }

    @Override
    public CryptoCipherObjectPool getCipherPool() {
        return cipherPool;
    }

    @Override
    public MutableIvParameterSpecObjectPool getIvParameterSpecPool() {
        return ivParameterSpecPool;
    }

    @Override
    public CryptoCipher newCryptoCipher() {
        try {
            return Utils.getCipherInstance(getAlgorithm(), SystemProperties.SYSTEM_PROPERTIES);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Key wrapKey(final byte[] key) {
        return new SecretKeySpec(key, "AES");
    }

}
