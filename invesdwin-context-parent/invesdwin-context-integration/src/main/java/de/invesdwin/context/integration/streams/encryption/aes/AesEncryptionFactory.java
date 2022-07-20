package de.invesdwin.context.integration.streams.encryption.aes;

import javax.annotation.concurrent.Immutable;

@Immutable
public class AesEncryptionFactory extends AesEncryptionFactoryDerivedIV {

    public AesEncryptionFactory(final byte[] derivedKey, final byte[] derivedIV) {
        super(AesAlgorithm.DEFAULT, derivedKey, derivedIV);
    }

}
