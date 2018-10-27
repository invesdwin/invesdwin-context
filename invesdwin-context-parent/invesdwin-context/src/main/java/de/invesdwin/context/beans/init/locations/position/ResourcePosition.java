package de.invesdwin.context.beans.init.locations.position;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;

@Immutable
public class ResourcePosition extends AValueObject {

    public static final ResourcePosition START = new ResourcePosition(0);
    public static final ResourcePosition MIDDLE = new ResourcePosition(5000);
    public static final ResourcePosition END = new ResourcePosition(10000);

    public static final ResourcePosition DEFAULT = MIDDLE;
    private final int priority;

    public ResourcePosition(final int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public ResourcePosition before() {
        return new ResourcePosition(priority - 1);
    }

    public ResourcePosition after() {
        return new ResourcePosition(priority + 1);
    }

    @Override
    public int compareTo(final Object o) {
        if (o instanceof ResourcePosition) {
            final ResourcePosition oPosition = (ResourcePosition) o;
            final Integer thisPositionPriority = getPriority();
            final Integer otherPositionPriority = oPosition.getPriority();
            return thisPositionPriority.compareTo(otherPositionPriority);
        } else {
            return 1;
        }
    }

}
