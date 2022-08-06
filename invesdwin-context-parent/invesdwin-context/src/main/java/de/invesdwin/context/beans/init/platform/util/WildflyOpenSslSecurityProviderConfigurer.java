package de.invesdwin.context.beans.init.platform.util;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.reflection.Reflections;

/**
 * https://github.com/corretto/amazon-corretto-crypto-provider#configuration
 */
@Immutable
public final class WildflyOpenSslSecurityProviderConfigurer {

    public static final String WILDFLY_OPENSSL_SECURITY_PROVIDER_CLASS = "org.wildfly.openssl.OpenSSLProvider";

    private WildflyOpenSslSecurityProviderConfigurer() {
    }

    public static void configure() {
        final Class<Object> wildflyClass = Reflections.classForName(WILDFLY_OPENSSL_SECURITY_PROVIDER_CLASS);
        Reflections.method("register").in(wildflyClass).invoke();
    }

}
