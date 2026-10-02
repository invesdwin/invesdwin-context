package de.invesdwin.context.integration.filechannel.registry;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.filechannel.IFileChannel;
import de.invesdwin.context.integration.filechannel.info.path.FileChannelPath;
import de.invesdwin.context.integration.filechannel.info.path.IFileChannelPath;
import de.invesdwin.context.log.Log;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;

@ThreadSafe
public final class FileChannelRegistry {

    private static final Log LOG = new Log(FileChannelRegistry.class);
    private static final Map<String, IFileChannelFactory> FACTORIES = ILockCollectionFactory.getInstance(true)
            .newConcurrentMap();

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

    public static IFileChannel newFile(final File path) {
        return newInstance(FileChannelPath.newFile(path));
    }

    public static IFileChannel newDirectory(final File path) {
        return newInstance(FileChannelPath.newDirectory(path));
    }

    public static IFileChannel newInstance(final File path) {
        return newInstance(FileChannelPath.newInstance(path));
    }

    public static IFileChannel newFile(final Path path) {
        return newInstance(FileChannelPath.newFile(path));
    }

    public static IFileChannel newDirectory(final Path path) {
        return newInstance(FileChannelPath.newDirectory(path));
    }

    public static IFileChannel newInstance(final Path path) {
        return newInstance(FileChannelPath.newInstance(path));
    }

    public static IFileChannel newInstance(final String serverUri) {
        if (serverUri == null) {
            return newInstance((URI) null);
        }
        return newInstance(FileChannelPath.newInstance(serverUri));
    }

    public static IFileChannel newFile(final String serverUri) {
        if (serverUri == null) {
            return newFile((URI) null);
        }
        return newInstance(FileChannelPath.newFile(serverUri));
    }

    public static IFileChannel newDirectory(final String serverUri) {
        if (serverUri == null) {
            return newDirectory((URI) null);
        }
        return newInstance(FileChannelPath.newDirectory(serverUri));
    }

    public static IFileChannel newFile(final URI serverUri) {
        return newInstance(FileChannelPath.newFile(serverUri));
    }

    public static IFileChannel newDirectory(final URI serverUri) {
        return newInstance(FileChannelPath.newDirectory(serverUri));
    }

    public static IFileChannel newInstance(final URI serverUri) {
        return newInstance(FileChannelPath.newInstance(serverUri));
    }

    public static IFileChannel newInstance(final IFileChannelPath path) {
        if (path == null) {
            throw new NullPointerException("path cannot be null");
        }
        final String scheme;
        if (path.getScheme() == null) {
            scheme = "file";
        } else {
            scheme = path.getScheme();
        }
        final IFileChannelFactory factory = FACTORIES.get(scheme.toLowerCase());
        if (factory == null) {
            throw new IllegalArgumentException("No IFileChannelFactory registered for scheme: " + scheme
                    + ". Available schemes: " + FACTORIES.keySet());
        }
        return factory.newInstance(path);
    }
}