package de.invesdwin.context.system.properties;

import java.net.URL;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import de.invesdwin.context.log.error.Err;

@NotThreadSafe
public class URLProperties extends AProperties {

    private final URL url;

    public URLProperties(final URL url) {
        this.url = url;
    }

    @Override
    protected AbstractConfiguration createDelegate() {
        try {
            final PropertiesConfiguration conf = new PropertiesConfiguration(url);
            conf.setAutoSave(true);
            return conf;
        } catch (final ConfigurationException e) {
            throw Err.process(e);
        }
    }

}
