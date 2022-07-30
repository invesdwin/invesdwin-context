package de.invesdwin.context.beans.init.platform.util;

import java.security.Provider;
import java.security.Security;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.reflection.Reflections;

@Immutable
public final class BouncyCastleSecurityProviderConfigurer {

    public static final String BOUNCY_CASTLE_PROVIDER_CLASS = "org.bouncycastle.jce.provider.BouncyCastleProvider";

    private BouncyCastleSecurityProviderConfigurer() {
    }

    public static void configure() {
        final Class<Object> bouncyCastleProviderClass = Reflections.classForName(BOUNCY_CASTLE_PROVIDER_CLASS);
        try {
            //bouncy castle is low, so we only add the provider instead of putting it at position 0
            Security.addProvider((Provider) bouncyCastleProviderClass.getDeclaredConstructor().newInstance());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
