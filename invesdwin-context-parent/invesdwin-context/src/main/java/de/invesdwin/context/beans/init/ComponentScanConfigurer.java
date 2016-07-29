package de.invesdwin.context.beans.init;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.log.error.Err;

@ThreadSafe
public class ComponentScanConfigurer {

    public Map<String, Resource> getApplicationContextXmlConfigs(final boolean defaultLazyInit) {
        try {
            final Map<String, Resource> xmls = new HashMap<String, Resource>();
            for (final String basePackage : ContextProperties.getBasePackages()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                sb.append("\n<beans default-lazy-init=\"");
                sb.append(defaultLazyInit);
                sb.append("\" xmlns=\"http://www.springframework.org/schema/beans\"");
                sb.append("\n\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
                sb.append("\n\txmlns:context=\"http://www.springframework.org/schema/context\"");
                sb.append("\n\txsi:schemaLocation=\"http://www.springframework.org/schema/beans");
                sb.append("\n\t\thttp://www.springframework.org/schema/beans/spring-beans.xsd");
                sb.append("\n\t\thttp://www.springframework.org/schema/context");
                sb.append("\n\t\thttp://www.springframework.org/schema/context/spring-context.xsd\">");
                sb.append("\n\n\t<context:component-scan base-package=\"");
                sb.append(basePackage);
                sb.append("\" />");
                sb.append("\n</beans>");
                xmls.put(basePackage, new ByteArrayResource(sb.toString().getBytes()));
            }
            return xmls;
        } catch (final Exception e) {
            throw Err.process(e);
        }
    }
}
