package de.invesdwin.context.beans.init.locations;

import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

/**
 * With this you can make a spring context only load if a condition is satisfied.
 */
@ThreadSafe
public abstract class AConditionalContextLocation implements IContextLocation {

    @Override
    public final List<PositionedResource> getContextResources() {
        if (isConditionSatisfied()) {
            return getContextResourcesIfConditionSatisfied();
        } else {
            return null;
        }
    }

    /**
     * Only gets called if the given condition is satisfied.
     */
    protected abstract List<PositionedResource> getContextResourcesIfConditionSatisfied();

    protected abstract boolean isConditionSatisfied();

}
