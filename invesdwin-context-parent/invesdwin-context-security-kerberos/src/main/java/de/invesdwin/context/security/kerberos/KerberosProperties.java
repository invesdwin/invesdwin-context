package de.invesdwin.context.security.kerberos;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import org.apache.directory.shared.kerberos.KerberosUtils;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.springframework.core.io.Resource;
import org.springframework.security.kerberos.authentication.sun.GlobalSunJaasKerberosConfig;

import de.invesdwin.context.beans.init.PreMergedContext;
import de.invesdwin.context.security.kerberos.internal.config.DefaultKrb5ConfGenerator;
import de.invesdwin.context.security.kerberos.internal.config.DefaultKrb5KeytabGenerator;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.util.assertions.Assertions;

@Immutable
public final class KerberosProperties {

    public static final String ROLE_KERBEROS_AUTHENTICATED = "KERBEROS_AUTHENTICATED";

    public static final boolean KERBEROS_DEBUG;
    public static final URI KERBEROS_SERVER_URI;
    public static final String KERBEROS_PRIMARY_REALM;
    public static final String KERBEROS_SERVICE_PRINCIPAL;
    public static final String KERBEROS_SERVICE_PASSPHRASE;
    public static final Resource KERBEROS_KEYTAB_RESOURCE;
    public static final Resource KERBEROS_KRB5CONF_RESOURCE;

    static {
        try {
            final SystemProperties systemProperties = new SystemProperties(KerberosProperties.class);
            KERBEROS_DEBUG = systemProperties.getBoolean("KERBEROS_DEBUG");
            KERBEROS_SERVER_URI = systemProperties.getURI("KERBEROS_SERVER_URI", true);
            KERBEROS_PRIMARY_REALM = systemProperties.getString("KERBEROS_PRIMARY_REALM");
            KERBEROS_SERVICE_PRINCIPAL = systemProperties.getString("KERBEROS_SERVICE_PRINCIPAL");

            // generate krb5.conf
            final String kerberosKrb5ConfResourceKey = "KERBEROS_KRB5CONF_RESOURCE";
            if (systemProperties.containsValue(kerberosKrb5ConfResourceKey)) {
                KERBEROS_KRB5CONF_RESOURCE = PreMergedContext.getInstance()
                        .getResource(systemProperties.getString(kerberosKrb5ConfResourceKey));
            } else {
                KERBEROS_KRB5CONF_RESOURCE = new DefaultKrb5ConfGenerator().newKrb5ConfResource();
                systemProperties.setString(kerberosKrb5ConfResourceKey, KERBEROS_KRB5CONF_RESOURCE.getURI().toString());
            }

            //keytab generator already accesses sun.security.krb5.Config which will load krb5 from the property that is set, thus need to set property already now!
            final GlobalSunJaasKerberosConfig config = new GlobalSunJaasKerberosConfig();
            config.setDebug(KERBEROS_DEBUG);
            config.setKrbConfLocation(KERBEROS_KRB5CONF_RESOURCE.getFile().getAbsolutePath());
            config.afterPropertiesSet();
            refreshKrbConf();

            //read passphrase
            final String kerberosServicePassphraseKey = "KERBEROS_SERVICE_PASSPHRASE";
            if (systemProperties.containsValue(kerberosServicePassphraseKey)) {
                KERBEROS_SERVICE_PASSPHRASE = systemProperties.getString(kerberosServicePassphraseKey);
            } else {
                KERBEROS_SERVICE_PASSPHRASE = null;
            }

            //generate/use keytab
            final String kerberosKeytabResourceKey = "KERBEROS_KEYTAB_RESOURCE";
            if (systemProperties.containsValue(kerberosKeytabResourceKey)) {
                KERBEROS_KEYTAB_RESOURCE = PreMergedContext.getInstance()
                        .getResource(systemProperties.getString(kerberosKeytabResourceKey));
                Assertions.assertThat(KERBEROS_SERVICE_PASSPHRASE)
                        .as("When [%s] is specified, then [%s] needs to be empty!", kerberosKeytabResourceKey,
                                kerberosServicePassphraseKey)
                        .isNull();
            } else {
                Assertions.assertThat(KERBEROS_SERVICE_PASSPHRASE)
                        .as("When [%s] is not specified, then [%s] needs to be filled!", kerberosKeytabResourceKey,
                                kerberosServicePassphraseKey)
                        .isNotNull();
                KERBEROS_KEYTAB_RESOURCE = new DefaultKrb5KeytabGenerator().newKrb5KeytabResource();
                systemProperties.setString(kerberosKeytabResourceKey, KERBEROS_KEYTAB_RESOURCE.getURI().toString());
            }

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private KerberosProperties() {}

    public static void refreshKrbConf() {
        try {
            final Class<?> classRef;
            if (new SystemProperties().getString("java.vendor").contains("IBM")) {
                classRef = Class.forName("com.ibm.security.krb5.internal.Config");
            } else {
                classRef = Class.forName("sun.security.krb5.Config");
            }
            final Method refreshMethod = classRef.getMethod("refresh", new Class[0]);
            refreshMethod.invoke(classRef, new Object[0]);
        } catch (final ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<EncryptionType> getEncryptionTypes() {
        final Set<EncryptionType> enctypes = new HashSet<EncryptionType>();
        for (final EncryptionType enctype : EncryptionType.values()) {
            try {
                if (enctype.getValue() > 0 && KerberosUtils.getAlgoNameFromEncType(enctype) != null) {
                    enctypes.add(enctype);
                }
            } catch (final IllegalArgumentException e) {
                continue;
            }
        }
        Assertions.assertThat(enctypes.remove(EncryptionType.RC4_HMAC)).isTrue(); //arcfour results in checksum failed error right now -.-
        return enctypes;
    }

}
