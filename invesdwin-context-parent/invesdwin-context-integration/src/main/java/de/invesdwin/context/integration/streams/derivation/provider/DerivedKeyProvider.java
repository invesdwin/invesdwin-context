package de.invesdwin.context.integration.streams.derivation.provider;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.streams.derivation.IDerivationFactory;
import de.invesdwin.context.integration.streams.derivation.hkdf.HkdfDerivationFactory;
import de.invesdwin.context.integration.streams.derivation.provider.password.IPasswordEncoder;
import de.invesdwin.context.integration.streams.derivation.provider.password.Pbkdf2PasswordEncoder;

/**
 * Key derivation techniques are: Password+PBKDF2+HKDFexpands or Random+HKDFextract+HKDFexpands
 * 
 * We can derive AES-KEY, AES-IV, MAC-KEY from the initial Password or Random. scrypt and bcrypt are alternatives to
 * PBKDF2
 * 
 * The salt should be a random value always that can be publicly exchanged before deriving additional passwords. The
 * password or random value should be transmitted over a secure channel or pre-shared. Though if a secure channel is
 * used anyhow, then it is best to transmit the salt and the key together over the secure channel. Using a null or fixed
 * salt is less secure.
 * 
 * https://security.stackexchange.com/questions/48000/why-would-you-need-a-salt-for-aes-cbs-when-iv-is-already-randomly-generated-and
 */
@Immutable
public class DerivedKeyProvider implements IDerivedKeyProvider {

    private final IDerivationFactory derivationFactory;
    private final byte[] salt;
    private final byte[] key;

    public DerivedKeyProvider(final byte[] salt, final byte[] key, final IDerivationFactory derivationFactory) {
        this.salt = salt;
        this.key = key;
        this.derivationFactory = derivationFactory;
    }

    public byte[] getSalt() {
        return salt;
    }

    @Override
    public byte[] newDerivedKey(final byte[] info, final int length) {
        return derivationFactory.expand(key, info, length);
    }

    public static DerivedKeyProvider fromPassword(final byte[] salt, final String password) {
        return fromPassword(salt, password, HkdfDerivationFactory.DEFAULT, Pbkdf2PasswordEncoder.DEFAULT);
    }

    public static DerivedKeyProvider fromPassword(final byte[] salt, final byte[] password) {
        return fromPassword(salt, password, HkdfDerivationFactory.DEFAULT, Pbkdf2PasswordEncoder.DEFAULT);
    }

    public static DerivedKeyProvider fromRandom(final byte[] salt, final byte[] random) {
        return fromRandom(salt, random, HkdfDerivationFactory.DEFAULT);
    }

    public static DerivedKeyProvider fromPassword(final byte[] salt, final String password,
            final IDerivationFactory derivationFactory, final IPasswordEncoder passwordEncoder) {
        return fromPassword(salt, password.getBytes(), derivationFactory, passwordEncoder);
    }

    public static DerivedKeyProvider fromPassword(final byte[] salt, final byte[] password,
            final IDerivationFactory derivationFactory, final IPasswordEncoder passwordEncoder) {
        final byte[] key = passwordEncoder.encode(salt, password, derivationFactory.getAlgorithm().getMacLength());
        return new DerivedKeyProvider(salt, key, derivationFactory);
    }

    public static DerivedKeyProvider fromRandom(final byte[] salt, final byte[] random,
            final IDerivationFactory derivationFactory) {
        final byte[] key = derivationFactory.extract(salt, random);
        return new DerivedKeyProvider(salt, key, derivationFactory);
    }

}
