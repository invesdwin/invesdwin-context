package de.invesdwin.context.integration.streams.random;

import java.security.GeneralSecurityException;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.crypto.random.CryptoRandom;
import org.apache.commons.crypto.random.CryptoRandomFactory;

import de.invesdwin.context.system.properties.SystemProperties;

@Immutable
public final class CryptoRandomGenerators {

    private CryptoRandomGenerators() {
    }

    public static CryptoRandomGenerator newCryptoRandom() {
        try {
            final CryptoRandom cryptoRandom = CryptoRandomFactory.getCryptoRandom(SystemProperties.SYSTEM_PROPERTIES);
            return new CryptoRandomGenerator(cryptoRandom);
        } catch (final GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static CryptoRandomGenerator newCryptoRandom(final long seed) {
        final CryptoRandomGenerator random = newCryptoRandom();
        random.setSeed(seed);
        return random;
    }

}
