package de.invesdwin.context.integration.streams.derivation.provider;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.streams.derivation.IDerivationFactory;
import de.invesdwin.context.integration.streams.derivation.hkdf.HkdfDerivationFactory;

@Immutable
public class DerivedKeyProvider implements IDerivedKeyProvider {

    private final IDerivationFactory derivationFactory;
    private final byte[] key;

    public DerivedKeyProvider(final IDerivationFactory derivationFactory, final byte[] key) {
        this.derivationFactory = derivationFactory;
        this.key = key;
    }

    @Override
    public byte[] newDerivedKey(final byte[] info, final int length) {
        return derivationFactory.expand(key, info, length);
    }

    public static DerivedKeyProvider fromPassword(final byte[] salt, final String password) {
        return fromPassword(salt, password, HkdfDerivationFactory.DEFAULT);
    }

    public static DerivedKeyProvider fromPassword(final byte[] salt, final byte[] password) {
        return fromPassword(salt, password, HkdfDerivationFactory.DEFAULT);
    }

    public static DerivedKeyProvider fromRandom(final byte[] salt, final byte[] random) {
        return fromRandom(salt, random, HkdfDerivationFactory.DEFAULT);
    }

    public static DerivedKeyProvider fromPassword(final byte[] salt, final String password,
            final IDerivationFactory derivationFactory) {
        return fromPassword(salt, password.getBytes(), derivationFactory);
    }

    public static DerivedKeyProvider fromPassword(final byte[] salt, final byte[] password,
            final IDerivationFactory derivationFactory) {
        //TODO: pbkdf2
        return null;
    }

    public static DerivedKeyProvider fromRandom(final byte[] salt, final byte[] random,
            final IDerivationFactory derivationFactory) {
        final byte[] key = derivationFactory.extract(salt, random);
        return new DerivedKeyProvider(derivationFactory, key);

    }

}
