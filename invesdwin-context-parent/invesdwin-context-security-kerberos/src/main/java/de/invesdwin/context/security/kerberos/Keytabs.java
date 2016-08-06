package de.invesdwin.context.security.kerberos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.FileUtils;
import org.apache.directory.server.kerberos.shared.crypto.encryption.KerberosKeyFactory;
import org.apache.directory.server.kerberos.shared.keytab.Keytab;
import org.apache.directory.server.kerberos.shared.keytab.KeytabEntry;
import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.EncryptionKey;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.UniqueNameGenerator;

@NotThreadSafe
public final class Keytabs {

    private static final UniqueNameGenerator UNIQUE_NAME_GENERATOR = new UniqueNameGenerator();

    private Keytabs() {}

    public static File createKeytab(final String principalName, final String passPhrase) {
        final Map<String, String> principalName_passPhrase = new HashMap<String, String>();
        principalName_passPhrase.put(principalName, passPhrase);
        return createKeytab(principalName_passPhrase);
    }

    public static File createKeytab(final String principalName, final String passPhrase, final File file) {
        final Map<String, String> principalName_passPhrase = new HashMap<String, String>();
        principalName_passPhrase.put(principalName, passPhrase);
        return createKeytab(principalName_passPhrase, file);
    }

    public static File createKeytab(final Map<String, String> principalName_passPhrase) {
        final File file = new File(ContextProperties.TEMP_DIRECTORY,
                Keytabs.class.getSimpleName() + "/" + UNIQUE_NAME_GENERATOR.get("keyab") + ".keytab");
        return createKeytab(principalName_passPhrase, file);
    }

    public static File createKeytab(final Map<String, String> principalName_passPhrase, final File file) {
        final KerberosTime timeStamp = new KerberosTime();
        final int principalType = 1; // KRB5_NT_PRINCIPAL

        final Keytab keytab = Keytab.getInstance();
        final List<KeytabEntry> entries = new ArrayList<KeytabEntry>();

        for (final Entry<String, String> e : principalName_passPhrase.entrySet()) {
            final String principalName = e.getKey();
            Assertions.assertThat(principalName).endsWith("@" + KerberosProperties.KERBEROS_PRIMARY_REALM);
            final String passPhrase = e.getValue();
            for (final Map.Entry<EncryptionType, EncryptionKey> keyEntry : KerberosKeyFactory
                    .getKerberosKeys(principalName, passPhrase, KerberosProperties.getEncryptionTypes()).entrySet()) {
                final EncryptionKey key = keyEntry.getValue();
                final byte keyVersion = (byte) key.getKeyVersion();
                entries.add(new KeytabEntry(principalName, principalType, timeStamp, keyVersion, key));
            }
        }
        keytab.setEntries(entries);
        try {
            FileUtils.forceMkdir(file.getParentFile());
            keytab.write(file);
        } catch (final IOException e1) {
            throw new RuntimeException(e1);
        }
        return file;
    }

}
