package de.invesdwin.context.system.properties;

import java.util.Map;
import java.util.Properties;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * This class works as an enhancement for PropertyPlaceholderConfigurer.
 * 
 * This gives properties from XML a higher priority to those from properties files.
 * 
 * @see <a
 *      href="http://rolfje.wordpress.com/2008/07/23/spring-systempropertyinitilizingbean/>PropertyPlaceholderConfigurer
 *      < / a >
 * 
 */
@NotThreadSafe
public class SystemPropertiesDefinition extends PropertyPlaceholderConfigurer implements InitializingBean {

    private Map<String, String> toBeSetSystemProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (toBeSetSystemProperties == null || toBeSetSystemProperties.isEmpty()) {
            // No properties to initialize
            return;
        }
        SystemProperties.setSystemProperties(toBeSetSystemProperties, true);
        toBeSetSystemProperties = null; //not needed anymore
    }

    public void setSystemProperties(final Map<String, String> systemProperties) {
        this.toBeSetSystemProperties = systemProperties;
    }

    @Override
    protected void processProperties(final ConfigurableListableBeanFactory beanFactoryToProcess, final Properties props) {
        SystemProperties.setSystemProperties(props, true);
        super.processProperties(beanFactoryToProcess, props);
    }

}
