package de.invesdwin.context.beans.init.platform.util;

import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.annotation.concurrent.Immutable;
import javax.crypto.Cipher;

import de.invesdwin.context.log.Log;

@Immutable
public final class CryptoPolicyConfigurer {

    private static final int MAX_ALLOWED_KEY_LENGTH_LOWER_BOUND = 256;

    private CryptoPolicyConfigurer() {
    }

    public static void configure() {
        Security.setProperty("crypto.policy", "unlimited");

        try {
            final int maxAllowedKeyLength = Cipher.getMaxAllowedKeyLength("AES");
            if (maxAllowedKeyLength < MAX_ALLOWED_KEY_LENGTH_LOWER_BOUND) {
                newLog().warn("AES key length of %s is required, but the detected maximum key length is %s. "
                        + "This may indicate that the environment is missing the JCE Unlimited Strength Jurisdiction Policy Files.",
                        MAX_ALLOWED_KEY_LENGTH_LOWER_BOUND, maxAllowedKeyLength);
            }
        } catch (final NoSuchAlgorithmException e) {
            newLog().catching(e);
        }
    }

    private static Log newLog() {
        return new Log(CryptoPolicyConfigurer.class);
    }

}
