package de.invesdwin.context.beans.init.platform.util;

import java.security.Security;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ConscryptConfigurer {

    private ConscryptConfigurer() {
    }

    public static void configure() {
        if (org.conscrypt.Conscrypt.isAvailable()) {
            //Fixes: https://github.com/google/conscrypt/issues/869
            org.conscrypt.Conscrypt.setUseEngineSocketByDefault(true);
            Security.insertProviderAt(new org.conscrypt.OpenSSLProvider(), 1);
        }
    }

}
