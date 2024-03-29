package de.invesdwin.context.integration.marshaller.internal;

import java.util.List;

import javax.annotation.concurrent.ThreadSafe;
import jakarta.inject.Named;

import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.beans.init.locations.ABeanDependantContextLocation;
import de.invesdwin.context.beans.init.locations.PositionedResource;
import de.invesdwin.context.beans.init.locations.position.ResourcePosition;
import de.invesdwin.context.integration.marshaller.IMergedJaxbContextPath;
import de.invesdwin.util.collections.Arrays;

@Named
@ThreadSafe
public class IntegrationMarshallerContextLocation extends ABeanDependantContextLocation {

    @Override
    protected Class<?> getDependantBeanType() {
        return IMergedJaxbContextPath.class;
    }

    @Override
    protected List<PositionedResource> getContextResourcesIfBeanExists() {
        return Arrays.asList(PositionedResource.of(new ClassPathResource("/META-INF/ctx.integration.marshaller.xml"),
                ResourcePosition.START));
    }

    @Override
    protected List<PositionedResource> getContextResourcesIfBeanNotExists() {
        return null;
    }

}
