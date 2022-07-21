package de.invesdwin.context.integration.streams.derivation.password;

import java.security.spec.InvalidKeySpecException;

import javax.annotation.concurrent.Immutable;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import de.invesdwin.context.integration.streams.authentication.mac.IMacAlgorithm;
import de.invesdwin.context.integration.streams.authentication.mac.hmac.HmacAlgorithm;
import de.invesdwin.util.math.Bytes;

/**
 * Adapted from: org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
 * 
 * A java implementation of https://github.com/ctz/fastpbkdf2 would be nice.
 */
@Immutable
public class Pbkdf2PasswordEncoder implements IPasswordEncoder {

    public static final String ALGORITHM_PREFIX = "PBKDF2With";
    //takes about 200ms for 200k iterations on an I9-9900k
    public static final int DEFAULT_ITERATIONS = 200_000;
    public static final IMacAlgorithm DEFAULT_MAC_ALGORITHM = HmacAlgorithm.HMAC_SHA_512;
    public static final IPasswordEncoder INSTANCE = new Pbkdf2PasswordEncoder();

    private final int iterations;
    private final byte[] secret;
    private final IMacAlgorithm macAlgorithm;
    private final String algorithm;
    private final SecretKeyFactoryObjectPool secretKeyFactoryPool;

    public Pbkdf2PasswordEncoder() {
        this(Pbkdf2PasswordEncoder.class.getName().getBytes());
    }

    public Pbkdf2PasswordEncoder(final byte[] secret) {
        this(secret, DEFAULT_ITERATIONS);
    }

    public Pbkdf2PasswordEncoder(final byte[] secret, final int iterations) {
        this(secret, iterations, DEFAULT_MAC_ALGORITHM);
    }

    public Pbkdf2PasswordEncoder(final byte[] secret, final int iterations, final IMacAlgorithm macAlgorithm) {
        this.secret = secret;
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
    public byte[] encode(final byte[] salt, final byte[] password, final int length) {
        final PBEKeySpec spec = new PBEKeySpec(new String(password).toCharArray(), Bytes.concatenate(salt, this.secret),
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
