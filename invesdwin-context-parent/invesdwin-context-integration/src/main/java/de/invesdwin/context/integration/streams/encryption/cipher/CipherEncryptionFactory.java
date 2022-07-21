package de.invesdwin.context.integration.streams.encryption.cipher;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.streams.derivation.provider.IDerivedKeyProvider;
import de.invesdwin.context.integration.streams.encryption.cipher.aes.AesAlgorithm;
import de.invesdwin.context.integration.streams.encryption.cipher.aes.AesKeyLength;

@Immutable
public class CipherEncryptionFactory extends CipherEncryptionFactoryDerivedIV {

    public CipherEncryptionFactory(final byte[] derivedKey, final byte[] derivedIV) {
        this(AesAlgorithm.DEFAULT, derivedKey, derivedIV);
    }

    public CipherEncryptionFactory(final IDerivedKeyProvider derivedKeyProvider) {
        this(AesAlgorithm.DEFAULT, derivedKeyProvider);
    }

    public CipherEncryptionFactory(final ICipherAlgorithm algorithm, final IDerivedKeyProvider derivedKeyProvider) {
        super(algorithm, derivedKeyProvider.newDerivedKey("crypto-key".getBytes(), AesKeyLength._256.getBytes()),
                derivedKeyProvider.newDerivedKey("crypto-iv".getBytes(), algorithm.getIvBytes()));
    }

    public CipherEncryptionFactory(final ICipherAlgorithm algorithm, final byte[] derivedKey, final byte[] derivedIV) {
        super(algorithm, derivedKey, derivedIV);
    }

}
