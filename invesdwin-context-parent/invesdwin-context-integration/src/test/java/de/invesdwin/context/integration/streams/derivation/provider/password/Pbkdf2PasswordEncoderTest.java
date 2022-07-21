package de.invesdwin.context.integration.streams.derivation.provider.password;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.integration.streams.random.CryptoRandomGenerator;
import de.invesdwin.context.integration.streams.random.CryptoRandomGeneratorObjectPool;
import de.invesdwin.util.time.Instant;

@NotThreadSafe
public class Pbkdf2PasswordEncoderTest {

    @Test
    public void testDuration() {
        final byte[] salt = new byte[64];
        final byte[] password = new byte[64];
        final CryptoRandomGenerator randomGenerator = CryptoRandomGeneratorObjectPool.INSTANCE.borrowObject();
        randomGenerator.nextBytes(salt);
        randomGenerator.nextBytes(password);
        randomGenerator.close();
        for (int i = 0; i < 10; i++) {
            final Instant start = new Instant();
            final byte[] encode = Pbkdf2PasswordEncoder.INSTANCE.encode(salt, salt, 32);
            //CHECKSTYLE:OFF
            System.out.println(start);
            //CHECKSTYLE:ON
        }
    }

}
