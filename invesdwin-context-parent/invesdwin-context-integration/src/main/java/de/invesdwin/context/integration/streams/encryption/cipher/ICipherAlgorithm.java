package de.invesdwin.context.integration.streams.encryption.cipher;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import de.invesdwin.context.integration.streams.encryption.cipher.pool.CryptoCipherObjectPool;
import de.invesdwin.context.integration.streams.encryption.cipher.pool.ICryptoCipherFactory;
import de.invesdwin.context.integration.streams.encryption.cipher.pool.MutableIvParameterSpec;
import de.invesdwin.context.integration.streams.encryption.cipher.pool.MutableIvParameterSpecObjectPool;

public interface ICipherAlgorithm extends ICryptoCipherFactory {

    String getAlgorithm();

    int getIvBytes();

    OutputStream newEncryptor(OutputStream out, byte[] key, byte[] iv);

    InputStream newDecryptor(InputStream in, byte[] key, byte[] iv);

    CryptoCipherObjectPool getCipherPool();

    MutableIvParameterSpecObjectPool getIvParameterSpecPool();

    Key wrapKey(byte[] key);

    AlgorithmParameterSpec wrapIv(byte[] iv);

    AlgorithmParameterSpec wrapIv(MutableIvParameterSpec iv);

}
