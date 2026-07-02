package de.invesdwin.context.system.properties;

import java.net.URL;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.FileBasedBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.sync.ReadWriteSynchronizer;

@ThreadSafe
public class URLProperties extends AProperties {

    private final URL url;

    public URLProperties(final URL url) {
        this.url = url;
    }

    @Override
    protected AbstractConfiguration createDelegate() {
        final FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(
                PropertiesConfiguration.class);
        builder.setAutoSave(true);
        try {
            final FileBasedBuilderParameters params = new Parameters().fileBased().setURL(url);
            if (isThreadSafe()) {
                params.setSynchronizer(new ReadWriteSynchronizer());
            }
            final PropertiesConfiguration conf = builder.configure(params).getConfiguration();
            return conf;
        } catch (final ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isThreadSafe() {
        return true;
    }

}
