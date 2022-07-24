package de.invesdwin.context.beans.init.platform.util;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.lang.reflection.Reflections;

/**
 * https://github.com/corretto/amazon-corretto-crypto-provider#configuration
 */
@Immutable
public final class AmazonCorrettoSecurityProviderConfigurer {

    public static final String AMAZON_CORRETTO_CRYPTO_PROVIDER_CLASS = "com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider";

    private AmazonCorrettoSecurityProviderConfigurer() {
    }

    public static void configure() {
        final Class<Object> correttoClass = Reflections.classForName(AMAZON_CORRETTO_CRYPTO_PROVIDER_CLASS);
        final Object instance = Reflections.field("INSTANCE").ofType(correttoClass).in(correttoClass).get();
        final Throwable loadingError = Reflections.method("getLoadingError")
                .withReturnType(Throwable.class)
                .in(instance)
                .invoke();
        if (loadingError == null) {
            try {
                Reflections.method("assertHealthy").in(instance).invoke();
            } catch (final Throwable t) {
                Err.process(new RuntimeException("ignoring", t));
                return;
            }
            //Fixes: https://github.com/google/conscrypt/issues/869
            Reflections.method("install").in(correttoClass).invoke();
        }
    }

}
