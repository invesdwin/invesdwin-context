package de.invesdwin.context.beans.init.internal;

import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.beans.init.locations.IContextLocation;
import de.invesdwin.context.beans.init.locations.PositionedResource;
import de.invesdwin.context.beans.init.locations.position.ResourcePosition;

@Named
@Immutable
public class SpringContextLocation implements IContextLocation {
    @Override
    public List<PositionedResource> getContextResources() {
        return Arrays.asList(
                PositionedResource.of(new ClassPathResource("/META-INF/ctx.spring.xml"), ResourcePosition.START));
    }

}