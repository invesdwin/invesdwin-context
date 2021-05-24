package de.invesdwin.context.beans.init.platform.util;

import java.security.Provider;
import java.security.Security;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.reflection.Reflections;

@Immutable
public final class ConscryptConfigurer {

    public static final String CONSCRYPT_CLASS = "org.conscrypt.Conscrypt";

    private ConscryptConfigurer() {
    }

    public static void configure() {
        final Class<Object> conscryptClass = Reflections.classForName(CONSCRYPT_CLASS);
        final boolean isAvailable = Reflections.method("isAvailable")
                .withReturnType(boolean.class)
                .in(conscryptClass)
                .invoke();
        if (isAvailable) {
            //Fixes: https://github.com/google/conscrypt/issues/869
            Reflections.method("setUseEngineSocketByDefault")
                    .withParameterTypes(boolean.class)
                    .in(conscryptClass)
                    .invoke(true);
            final Class<Provider> openSslProviderClass = Reflections.classForName("org.conscrypt.OpenSSLProvider");
            try {
                Security.insertProviderAt(openSslProviderClass.getDeclaredConstructor().newInstance(), 1);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
