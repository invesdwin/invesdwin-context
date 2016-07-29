package de.invesdwin.context.beans.init.locations;

import java.util.List;

public interface IContextLocation {

    /**
     * Context files must reside in /META-INF/. The file name should not contain the path.
     */
    List<PositionedResource> getContextResources();

}
