package de.invesdwin.context.integration.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.Marshaller;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.ClassUtils;

import de.invesdwin.context.integration.IMergedJaxbContextPath;
import de.invesdwin.util.assertions.Assertions;

/**
 * Collects all generated IMergedContextPaths and adds them to the Jaxb2Marshaller.
 * 
 * @author subes
 * 
 */
@ThreadSafe
public class MergedJaxb2Marshaller extends Jaxb2Marshaller implements ApplicationContextAware {

    public MergedJaxb2Marshaller() {
        final Map<String, Object> properties = new HashMap<String, Object>();
        //never format output to preserve performance
        properties.put(Marshaller.JAXB_FORMATTED_OUTPUT, false);
        super.setMarshallerProperties(properties);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        final Map<String, IMergedJaxbContextPath> mergedContextPaths = applicationContext.getBeansOfType(IMergedJaxbContextPath.class);
        final List<String> contextPaths = new ArrayList<String>();
        final List<Resource> schemas = new ArrayList<Resource>();
        for (final IMergedJaxbContextPath mergedContextPath : mergedContextPaths.values()) {
            contextPaths.add(mergedContextPath.getContextPath());
            final String resource = mergedContextPath.getSchemaPath();
            final Resource cpResource = new ClassPathResource(resource, ClassUtils.getDefaultClassLoader());
            Assertions.assertThat(cpResource.exists()).as("%s", cpResource).isTrue();
            schemas.add(cpResource);
        }
        super.setContextPaths(contextPaths.toArray(new String[0]));
        super.setSchemas(schemas.toArray(new Resource[0]));
    }

}
