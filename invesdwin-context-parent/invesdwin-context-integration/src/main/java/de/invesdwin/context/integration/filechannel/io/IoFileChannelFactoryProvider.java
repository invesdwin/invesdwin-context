package de.invesdwin.context.integration.filechannel.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.filechannel.registry.IFileChannelFactory;
import de.invesdwin.context.integration.filechannel.registry.IFileChannelFactoryProvider;
import de.invesdwin.util.collections.Collections;

@Immutable
public class IoFileChannelFactoryProvider implements IFileChannelFactoryProvider {

    public static final String[] SCHEMES = new String[] { "file" };

    @Override
    public int getPriority() {
        return 10_000;
    }

    @Override
    public Collection<IFileChannelFactory> newFactories() {
        final List<IFileChannelFactory> factories = new ArrayList<>(SCHEMES.length);
        for (int i = 0; i < SCHEMES.length; i++) {
            final String scheme = SCHEMES[i];
            factories.add(new IoFileChannelFactory(scheme));
        }
        return Collections.unmodifiableCollection(factories);
    }
}