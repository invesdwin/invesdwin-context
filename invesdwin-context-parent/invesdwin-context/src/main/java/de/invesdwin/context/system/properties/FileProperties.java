package de.invesdwin.context.system.properties;

import java.io.File;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import de.invesdwin.context.log.error.Err;

@NotThreadSafe
public class FileProperties extends AProperties {

    private final File file;

    public FileProperties(final File file) {
        this.file = file;
    }

    @Override
    protected AbstractConfiguration createDelegate() {
        try {
            final PropertiesConfiguration conf = new PropertiesConfiguration(file);
            conf.setAutoSave(true);
            return conf;
        } catch (final ConfigurationException e) {
            throw Err.process(e);
        }
    }

}
