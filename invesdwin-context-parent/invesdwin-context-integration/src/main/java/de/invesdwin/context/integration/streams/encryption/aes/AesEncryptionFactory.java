package de.invesdwin.context.integration.streams.encryption.aes;

import javax.annotation.concurrent.Immutable;

@Immutable
public class AesEncryptionFactory extends AesEncryptionFactoryCountedIV {

    public AesEncryptionFactory(final byte[] key) {
        super(AesAlgorithm.DEFAULT, key);
    }

}
