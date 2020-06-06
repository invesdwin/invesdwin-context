package de.invesdwin.context.webserver;

import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.Enumeration;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;
import org.springframework.core.io.Resource;

import de.invesdwin.context.beans.init.PreMergedContext;
import de.invesdwin.context.test.ATest;

@NotThreadSafe
public class ExtractPublicKeyFromKeystore extends ATest {

    @Test
    public void extractPublicKeyFromKeystore() throws Exception {
        final Resource keystoreResource = PreMergedContext.getInstance()
                .getResource(WebserverProperties.getKeystoreResource());
        final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        final String password = WebserverProperties.getKeystoreStorepass();
        keystore.load(keystoreResource.getInputStream(), password.toCharArray());
        final Enumeration<String> enumeration = keystore.aliases();
        while (enumeration.hasMoreElements()) {
            final String alias = enumeration.nextElement();
            final Certificate certificate = keystore.getCertificate(alias);
            final PublicKey publicKey = keystore.getCertificate(alias).getPublicKey();
            final byte[] encodedCertKey = certificate.getEncoded();
            final byte[] encodedPublicKey = publicKey.getEncoded();
            final String b64PublicKey = Base64.getMimeEncoder().encodeToString(encodedPublicKey);
            final String b64CertKey = Base64.getMimeEncoder().encodeToString(encodedCertKey);
            final String publicKeyString = "-----BEGIN CERTIFICATE-----\n" + b64PublicKey
                    + "\n-----END CERTIFICATE-----";

            final String certKeyString = "-----BEGIN CERTIFICATE-----\n" + b64CertKey + "\n-----END CERTIFICATE-----";
            log.info(publicKeyString);
            log.info(certKeyString);
        }

    }

}
