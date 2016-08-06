package de.invesdwin.context.security.kerberos.internal.config;

import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.security.kerberos.KerberosProperties;
import de.invesdwin.context.security.kerberos.Keytabs;

@NotThreadSafe
public class DefaultKrb5KeytabGenerator {

    @GuardedBy("this.class")
    private static File alreadyGenerated;

    private void createContent(final File file) throws IOException {
        Keytabs.createKeytab(KerberosProperties.KERBEROS_SERVICE_PRINCIPAL, "asdf", file);
    }

    public Resource newKrb5KeytabResource() {
        synchronized (DefaultKrb5ConfGenerator.class) {
            try {
                if (alreadyGenerated == null || !alreadyGenerated.exists()) {
                    final File file = new File(ContextProperties.TEMP_CLASSPATH_DIRECTORY, "META-INF/krb5.keytab");
                    createContent(file);
                    alreadyGenerated = file;
                }
                return new FileSystemResource(alreadyGenerated);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
