package de.invesdwin.context.security.kerberos.internal.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.security.kerberos.KerberosProperties;

@NotThreadSafe
public class DefaultKrb5ConfGenerator {

    @GuardedBy("this.class")
    private static File alreadyGenerated;

    public Resource newKrb5ConfResource() {
        synchronized (DefaultKrb5ConfGenerator.class) {
            try {
                if (alreadyGenerated == null || !alreadyGenerated.exists()) {
                    final String template = getTemplate();
                    final Map<String, String> properties = new HashMap<String, String>();
                    properties.put("HOSTNAME", KerberosProperties.KERBEROS_SERVER_URI.getHost());
                    properties.put("PORT", String.valueOf(KerberosProperties.KERBEROS_SERVER_URI.getPort()));
                    properties.put("REALM", KerberosProperties.KERBEROS_PRIMARY_REALM);
                    properties.put("ENCTYPES", getEncryptionTypesStr());
                    final String replaced = StrSubstitutor.replace(template, properties);
                    final File file = new File(ContextProperties.TEMP_CLASSPATH_DIRECTORY, "META-INF/krb5.conf");
                    FileUtils.write(file, replaced);
                    alreadyGenerated = file;
                }
                return new FileSystemResource(alreadyGenerated);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getEncryptionTypesStr() {
        final StringBuilder sb = new StringBuilder();
        for (final EncryptionType e : KerberosProperties.getEncryptionTypes()) {
            sb.append(e.getName());
            sb.append(" ");
        }
        return sb.toString();
    }

    private String getTemplate() throws IOException {
        final ClassPathResource templateResource = new ClassPathResource("META-INF/template.krb5.conf");
        final InputStream in = templateResource.getInputStream();
        final String template = IOUtils.toString(in);
        in.close();
        return template;
    }

}