package de.invesdwin.context.integration.streams.encryption.crypto;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import de.invesdwin.context.integration.streams.encryption.crypto.pool.CryptoCipherObjectPool;
import de.invesdwin.context.integration.streams.encryption.crypto.pool.ICryptoCipherFactory;
import de.invesdwin.context.integration.streams.encryption.crypto.pool.MutableIvParameterSpec;
import de.invesdwin.context.integration.streams.encryption.crypto.pool.MutableIvParameterSpecObjectPool;

public interface ICryptoAlgorithm extends ICryptoCipherFactory {

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
