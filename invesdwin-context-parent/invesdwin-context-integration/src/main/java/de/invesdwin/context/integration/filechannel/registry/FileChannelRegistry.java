package de.invesdwin.context.integration.filechannel.registry;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.integration.filechannel.IFileChannel;
import de.invesdwin.context.log.Log;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.lang.uri.URIs;

@ThreadSafe
public final class FileChannelRegistry {

    private static final Log LOG = new Log(FileChannelRegistry.class);
    private static final Map<String, IFileChannelFactory> FACTORIES = ILockCollectionFactory.getInstance(true)
            .newConcurrentMap();
    private static final URI FALLBACK_SERVER_URI = new File(ContextProperties.getCacheDirectory(),
            FileChannelRegistry.class.getSimpleName() + "_FALLBACK").toURI();

    static {
        registerDiscoveredFactories();
    }

    private FileChannelRegistry() {}

    public static void register(final IFileChannelFactoryProvider provider) {
        if (provider == null) {
            return;
        }
        final Collection<IFileChannelFactory> factories = provider.newFactories();
        if (factories == null || factories.isEmpty()) {
            return;
        }
        for (final IFileChannelFactory factory : factories) {
            register(factory);
        }
    }

    public static void register(final IFileChannelFactory factory) {
        if (factory != null && factory.getScheme() != null) {
            final String scheme = factory.getScheme().toLowerCase();
            final IFileChannelFactory existing = FACTORIES.put(scheme, factory);
            if (existing != null) {
                LOG.trace("Overwrote IFileChannelFactory for scheme [%s] with higher priority implementation", scheme);
            } else {
                LOG.trace("Registered IFileChannelFactory for scheme [%s]", scheme);
            }
        }
    }

    public static void unregister(final IFileChannelFactory factory) {
        if (factory != null && factory.getScheme() != null) {
            FACTORIES.remove(factory.getScheme().toLowerCase());
        }
    }

    public static void registerDiscoveredFactories() {
        try {
            final ServiceLoader<IFileChannelFactoryProvider> loader = ServiceLoader
                    .load(IFileChannelFactoryProvider.class);
            final List<IFileChannelFactoryProvider> providers = new ArrayList<>();
            for (final IFileChannelFactoryProvider provider : loader) {
                providers.add(provider);
            }

            // Sort providers by priority descending: higher numerical values (low priority)
            // register first, and lower numerical values (high priority) register last to override conflicts.
            providers.sort(Comparator.comparingInt(IFileChannelFactoryProvider::getPriority).reversed());

            for (final IFileChannelFactoryProvider provider : providers) {
                final Collection<IFileChannelFactory> providerFactories = provider.newFactories();
                if (providerFactories != null) {
                    for (final IFileChannelFactory factory : providerFactories) {
                        register(factory);
                    }
                }
            }
        } catch (final Throwable t) {
            LOG.warn("Failed to load IFileChannelFactoryProviders via ServiceLoader", t);
        }
    }

    public static IFileChannel newInstance(final String serverUriStr) {
        if (serverUriStr == null) {
            return newInstance((URI) null);
        }
        return newInstance(URIs.asUri(serverUriStr));
    }

    public static IFileChannel newInstance(final URI serverUri) {
        final String scheme;
        final URI effectiveUri;
        if (serverUri == null) {
            throw new NullPointerException("serverUri cannot be null");
        }
        if (serverUri.getScheme() == null) {
            scheme = "file";
            effectiveUri = URI.create("file:" + serverUri.toString());
        } else {
            scheme = serverUri.getScheme();
            effectiveUri = serverUri;
        }

        final IFileChannelFactory factory = FACTORIES.get(scheme.toLowerCase());
        if (factory == null) {
            throw new IllegalArgumentException("No IFileChannelFactory registered for scheme: " + scheme
                    + ". Available schemes: " + FACTORIES.keySet());
        }
        return factory.newInstance(effectiveUri);
    }
}