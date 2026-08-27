package de.invesdwin.context.system.properties;

import java.io.File;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.sync.ReadWriteSynchronizer;

import de.invesdwin.util.lang.Files;

@ThreadSafe
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
                Files.touchQuietly(file);
            }
            PropertiesBuilderParameters params = new Parameters().properties().setFile(file);
            if (isThreadSafe()) {
                params = params.setSynchronizer(new ReadWriteSynchronizer());
            }
            final PropertiesConfiguration config = builder.configure(params).getConfiguration();
            return config;
        } catch (final ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isThreadSafe() {
        return true;
    }

}
