package de.invesdwin.context.beans.init.locations;

import java.util.List;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import org.springframework.context.support.GenericApplicationContext;

import de.invesdwin.context.beans.init.ApplicationContexts;

/**
 * With this you can use the bootstrap process to check which beans are configured and load the appropriate spring
 * config for it. This will scan for beans that have the @Named annotation. Beans that get instantiated after the
 * bootstrap won't be found.
 * 
 * @author subes
 * 
 */
@ThreadSafe
public abstract class ABeanDependantContextLocation implements IContextLocation {

    @Inject
    private GenericApplicationContext ctx;

    /**
     * Only gets called is the dependant bean does not exist.
     */
    protected abstract List<PositionedResource> getContextResourcesIfBeanExists();

    /**
     * Only gets called if the dependant bean exists.
     */
    protected abstract List<PositionedResource> getContextResourcesIfBeanNotExists();

    protected abstract Class<?> getDependantBeanType();

    @Override
    public List<PositionedResource> getContextResources() {
        final Class<?> beanType = getDependantBeanType();
        if (beanExists(beanType)) {
            return getContextResourcesIfBeanExists();
        } else {
            return getContextResourcesIfBeanNotExists();
        }
    }

    protected boolean beanExists(final Class<?> beanType) {
        return ApplicationContexts.beanExists(ctx, beanType);
    }

}
