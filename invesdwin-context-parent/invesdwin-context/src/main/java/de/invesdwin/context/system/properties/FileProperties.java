package de.invesdwin.context.system.properties;

import java.io.File;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

@NotThreadSafe
public class FileProperties extends AProperties {

    private final File file;

    public FileProperties(final File file) {
        this.file = file;
    }

    @Override
    protected AbstractConfiguration createDelegate() {
        final FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(
                PropertiesConfiguration.class);
        builder.setAutoSave(true);
        try {
            final PropertiesConfiguration conf = builder.configure(new Parameters().properties().setFile(file))
                    .getConfiguration();
            return conf;
        } catch (final ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
