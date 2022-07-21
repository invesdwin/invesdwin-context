package de.invesdwin.context.integration.streams.key.password.pbkdf2;

import java.security.spec.InvalidKeySpecException;

import javax.annotation.concurrent.Immutable;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import de.invesdwin.context.integration.streams.authentication.mac.IMacAlgorithm;
import de.invesdwin.context.integration.streams.authentication.mac.hmac.HmacAlgorithm;
import de.invesdwin.context.integration.streams.key.password.IPasswordHasher;
import de.invesdwin.util.math.Bytes;

/**
 * Adapted from: org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
 * 
 * A java implementation of https://github.com/ctz/fastpbkdf2 would be nice.
 */
@Immutable
public class Pbkdf2PasswordHasher implements IPasswordHasher {

    public static final String ALGORITHM_PREFIX = "PBKDF2With";
    //takes about 200ms for 200k iterations on an I9-9900k
    public static final int DEFAULT_ITERATIONS = 200_000;
    public static final IMacAlgorithm DEFAULT_MAC_ALGORITHM = HmacAlgorithm.HMAC_SHA_512;
    public static final IPasswordHasher INSTANCE = new Pbkdf2PasswordHasher();

    private final int iterations;
    private final byte[] pepper;
    private final IMacAlgorithm macAlgorithm;
    private final String algorithm;
    private final SecretKeyFactoryObjectPool secretKeyFactoryPool;

    public Pbkdf2PasswordHasher() {
        this(Pbkdf2PasswordHasher.class.getName().getBytes());
    }

    public Pbkdf2PasswordHasher(final byte[] pepper) {
        this(pepper, DEFAULT_ITERATIONS);
    }

    public Pbkdf2PasswordHasher(final byte[] pepper, final int iterations) {
        this(pepper, iterations, DEFAULT_MAC_ALGORITHM);
    }

    public Pbkdf2PasswordHasher(final byte[] pepper, final int iterations, final IMacAlgorithm macAlgorithm) {
        this.pepper = pepper;
        this.iterations = iterations;
        this.macAlgorithm = macAlgorithm;
        this.algorithm = ALGORITHM_PREFIX + macAlgorithm.getAlgorithm();
        this.secretKeyFactoryPool = new SecretKeyFactoryObjectPool(algorithm);
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public IMacAlgorithm getMacAlgorithm() {
        return macAlgorithm;
    }

    @Override
    public byte[] hash(final byte[] salt, final byte[] password, final int length) {
        final PBEKeySpec spec = new PBEKeySpec(new String(password).toCharArray(), Bytes.concatenate(salt, this.pepper),
                this.iterations, length);
        final SecretKeyFactory secretKeyFactory = secretKeyFactoryPool.borrowObject();
        try {
            return secretKeyFactory.generateSecret(spec).getEncoded();
        } catch (final InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } finally {
            secretKeyFactoryPool.returnObject(secretKeyFactory);
        }
    }

}
