package de.invesdwin.context.integration.filechannel.registry;

import java.util.Collection;

public interface IFileChannelFactoryProvider {

    Collection<IFileChannelFactory> newFactories();

    /**
     * Returns the priority of this provider. Lower values have higher priority
     */
    default int getPriority() {
        return Integer.MAX_VALUE;
    }
}