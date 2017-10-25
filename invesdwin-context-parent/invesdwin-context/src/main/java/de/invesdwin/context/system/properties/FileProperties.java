package de.invesdwin.context.system.properties;

import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;

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
            if (!file.exists()) {
                FileUtils.touch(file);
            }
            final PropertiesConfiguration conf = builder
                    .configure(new Parameters().fileBased().setFile(file).setThrowExceptionOnMissing(true))
                    .getConfiguration();
            return conf;
        } catch (final ConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
