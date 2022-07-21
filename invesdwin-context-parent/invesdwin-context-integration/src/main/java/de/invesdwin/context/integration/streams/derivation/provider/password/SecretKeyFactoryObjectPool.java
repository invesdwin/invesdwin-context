package de.invesdwin.context.integration.streams.derivation.provider.password;

import java.security.NoSuchAlgorithmException;

import javax.annotation.concurrent.ThreadSafe;
import javax.crypto.SecretKeyFactory;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;

@ThreadSafe
public final class SecretKeyFactoryObjectPool extends AAgronaObjectPool<SecretKeyFactory> {

    private final String algorithm;

    public SecretKeyFactoryObjectPool(final String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    protected SecretKeyFactory newObject() {
        try {
            return SecretKeyFactory.getInstance(this.algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
