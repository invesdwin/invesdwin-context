package de.invesdwin.context.system.properties.concurrent.multiprocess;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Properties;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.configuration2.AbstractConfiguration;

import de.invesdwin.context.system.properties.AProperties;
import de.invesdwin.context.system.properties.ICloseableProperties;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.pool.PooledFastByteArrayOutputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

@ThreadSafe
public class TransactionalFileProperties extends AProperties implements ICloseableProperties {

    private static final String PROPERTIES_FILENAME = "transactional.properties";

    private final AtomicFilesHelper atomicFilesHelper;
    private final Path targetFile;
    private volatile Properties propertiesFile;
    private volatile boolean modified;

    public TransactionalFileProperties(final File baseFolder) {
        this(new AtomicFilesHelper(newDefaultFolder(baseFolder)));
    }

    public TransactionalFileProperties(final AtomicFilesHelper atomicFilesHelper) {
        this.atomicFilesHelper = atomicFilesHelper;
        this.targetFile = atomicFilesHelper.getFolder().resolve(PROPERTIES_FILENAME);
    }

    public static File newDefaultFolder(final File baseFolder) {
        return new File(baseFolder, TransactionalFileProperties.class.getSimpleName());
    }

    private Properties getPropertiesFile() {
        Properties result = propertiesFile;
        if (result == null) {
            synchronized (this) {
                result = propertiesFile;
                if (result == null) {
                    result = new Properties();
                    loadProperties(result);
                    propertiesFile = result;
                }
            }
        }
        return result;
    }

    private void loadProperties(final Properties props) {
        if (Files.exists(targetFile)) {
            try {
                final byte[] bytes = Files.readAllBytes(targetFile);
                try (InputStream in = new FastByteArrayInputStream(bytes)) {
                    props.load(in);
                }
            } catch (final IOException e) {
                throw new RuntimeException("Failed to load properties from: " + targetFile.toAbsolutePath(), e);
            }
        }
    }

    @Override
    protected AbstractConfiguration createDelegate() {
        return new AbstractConfiguration() {

            @Override
            protected boolean isEmptyInternal() {
                return getPropertiesFile().isEmpty();
            }

            @Override
            protected Object getPropertyInternal(final String key) {
                return getPropertiesFile().getProperty(key);
            }

            @Override
            protected Iterator<String> getKeysInternal() {
                return getPropertiesFile().stringPropertyNames().iterator();
            }

            @Override
            protected boolean containsKeyInternal(final String key) {
                return getPropertiesFile().containsKey(key);
            }

            @Override
            protected void clearPropertyDirect(final String key) {
                final Object previous = getPropertiesFile().remove(key);
                if (previous != null) {
                    modified = true;
                }
            }

            @Override
            protected void clearInternal() {
                final Properties props = getPropertiesFile();
                if (!props.isEmpty()) {
                    props.clear();
                    modified = true;
                }
            }

            @Override
            protected void addPropertyDirect(final String key, final Object value) {
                final String newValue = String.valueOf(value);
                final Object previous = getPropertiesFile().setProperty(key, newValue);
                if (!Objects.equals(previous, newValue)) {
                    modified = true;
                }
            }

            @Override
            protected boolean containsValueInternal(final Object value) {
                return getPropertiesFile().containsValue(String.valueOf(value));
            }
        };
    }

    @Override
    public void close() {
        atomicFilesHelper.maybeRunCleanup();
        if (!modified) {
            return;
        }
        try (PooledFastByteArrayOutputStream out = PooledFastByteArrayOutputStream.newInstance()) {
            getPropertiesFile().store(out, null);
            atomicFilesHelper.writeAtomic(targetFile, out.asInputStream());
        } catch (final IOException e) {
            throw new RuntimeException("Failed to save properties to: " + targetFile.toAbsolutePath(), e);
        }
    }
}