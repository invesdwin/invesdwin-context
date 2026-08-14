package de.invesdwin.context.integration.filechannel.nio;

import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.filechannel.registry.IFileChannelFactory;
import de.invesdwin.context.integration.filechannel.registry.IFileChannelFactoryProvider;
import de.invesdwin.context.log.Log;
import de.invesdwin.util.collections.Collections;

@Immutable
public class NioFileChannelFactoryProvider implements IFileChannelFactoryProvider {

    private static final Log LOG = new Log(NioFileChannelFactoryProvider.class);

    @Override
    public int getPriority() {
        return 10_000;
    }

    @Override
    public Collection<IFileChannelFactory> newFactories() {
        final List<IFileChannelFactory> factories = new ArrayList<>();
        try {
            for (final FileSystemProvider provider : FileSystemProvider.installedProviders()) {
                final String scheme = provider.getScheme();
                if (scheme != null) {
                    final String lowerScheme = scheme.toLowerCase();
                    factories.add(new NioFileChannelFactory(lowerScheme));
                }
            }
        } catch (final Throwable t) {
            LOG.warn("Failed to query installed Nio2 FileSystemProviders", t);
        }
        return Collections.unmodifiableCollection(factories);
    }
}