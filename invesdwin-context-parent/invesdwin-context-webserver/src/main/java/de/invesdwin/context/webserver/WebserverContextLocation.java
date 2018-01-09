package de.invesdwin.context.webserver;

import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.beans.init.locations.AConditionalContextLocation;
import de.invesdwin.context.beans.init.locations.PositionedResource;
import de.invesdwin.context.beans.init.locations.PositionedResource.ResourcePosition;

/**
 * Webserver should only be started explicitly.
 * 
 */
@ThreadSafe
@Named
public class WebserverContextLocation extends AConditionalContextLocation {

    private static volatile boolean activated = true;

    @Override
    protected List<PositionedResource> getContextResourcesIfConditionSatisfied() {
        return Arrays.asList(getContextLocation());
    }

    public static PositionedResource getContextLocation() {
        if (WebserverProperties.SSL_ENABLED) {
            return PositionedResource.of(new ClassPathResource("/META-INF/ctx.webserver.ssl.xml"),
                    ResourcePosition.START);
        } else {
            return PositionedResource.of(new ClassPathResource("/META-INF/ctx.webserver.xml"), ResourcePosition.START);
        }
    }

    @Override
    protected boolean isConditionSatisfied() {
        return activated;
    }

    public static void activate() {
        activated = true;
    }

    public static void deactivate() {
        activated = false;
    }

    public static boolean isActivated() {
        return activated;
    }

}
