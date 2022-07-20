package de.invesdwin.context.integration.streams.encryption.crypto;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.streams.derivation.provider.IDerivedKeyProvider;
import de.invesdwin.context.integration.streams.encryption.crypto.aes.AesAlgorithm;
import de.invesdwin.context.integration.streams.encryption.crypto.aes.AesKeyLength;

@Immutable
public class CryptoEncryptionFactory extends CryptoEncryptionFactoryDerivedIV {

    public CryptoEncryptionFactory(final byte[] derivedKey, final byte[] derivedIV) {
        this(AesAlgorithm.DEFAULT, derivedKey, derivedIV);
    }

    public CryptoEncryptionFactory(final IDerivedKeyProvider derivedKeyProvider) {
        this(AesAlgorithm.DEFAULT, derivedKeyProvider);
    }

    public CryptoEncryptionFactory(final ICryptoAlgorithm algorithm, final IDerivedKeyProvider derivedKeyProvider) {
        super(algorithm, derivedKeyProvider.newDerivedKey("crypto-key".getBytes(), AesKeyLength._256.getBytes()),
                derivedKeyProvider.newDerivedKey("crypto-iv".getBytes(), algorithm.getIvBytes()));
    }

    public CryptoEncryptionFactory(final ICryptoAlgorithm algorithm, final byte[] derivedKey, final byte[] derivedIV) {
        super(algorithm, derivedKey, derivedIV);
    }

}
