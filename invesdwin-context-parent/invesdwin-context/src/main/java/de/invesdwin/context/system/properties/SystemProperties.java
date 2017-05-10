package de.invesdwin.context.system.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.MapConfiguration;
import org.springframework.core.io.Resource;

import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.context.beans.init.PreMergedContext;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.assertions.Assertions;

/**
 * This class can be used to conveniently access system properties. Properties files are generally loaded into system
 * properties by the bootstrap mechanism and are available there.
 * 
 * @author subes
 * 
 */
@NotThreadSafe
public class SystemProperties extends AProperties {

    private final String prefix;

    static {
        if (PlatformInitializerProperties.isAllowed()) {
            try {
                //Load properties if not done so yet
                Assertions.assertThat(PreMergedContext.class).isNotNull();
            } catch (final Throwable t) {
                PlatformInitializerProperties.logInitializationFailedIsIgnored(t);
            }
        }
    }

    public SystemProperties() {
        this((String) null);
    }

    public SystemProperties(final Class<?> accessor) {
        this(accessor.getName());
    }

    public SystemProperties(final String accessor) {
        if (accessor == null) {
            this.prefix = "";
        } else {
            this.prefix = accessor + ".";
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected AbstractConfiguration createDelegate() {
        //CHECKSTYLE:OFF Properties
        final MapConfiguration delegate = new MapConfiguration((Map) System.getProperties());
        //CHECKSTYLE:ON
        delegate.setThrowExceptionOnMissing(true);
        return delegate;
    }

    @Override
    protected String getPropertyPrefix() {
        return prefix;
    }

    /**
     * Should only be used by infrastructure classes.
     */
    public static void setSystemProperties(final Resource propsResource, final boolean overwrite) {
        try {
            final Properties props = new Properties();
            final InputStream in = propsResource.getInputStream();
            props.load(in);
            in.close();
            setSystemProperties(props, overwrite);
        } catch (final IOException e) {
            throw Err.process(e);
        }
    }

    /**
     * Should only be used by infrastructure classes.
     */
    public static void setSystemProperties(final Properties props, final boolean overwrite) {
        final Map<String, String> systemProperties = new HashMap<String, String>();
        for (final Entry<Object, Object> entry : props.entrySet()) {
            final String key = (String) entry.getKey();
            final String value = (String) entry.getValue();
            systemProperties.put(key, value);
        }
        setSystemProperties(systemProperties, overwrite);
    }

    /**
     * Should only be used by infrastructure classes.
     */
    public static void setSystemProperties(final Map<String, String> systemProperties, final boolean overwrite) {
        final SystemProperties sysprops = new SystemProperties();
        for (final Entry<String, String> entry : systemProperties.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            //-D Properties that have been given via commandline do not get overwritten
            if (overwrite || !sysprops.containsKey(key)) {
                sysprops.setString(key, value);
            }
        }
    }

}
