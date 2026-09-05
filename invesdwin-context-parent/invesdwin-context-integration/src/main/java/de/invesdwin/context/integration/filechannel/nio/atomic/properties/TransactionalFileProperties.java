package de.invesdwin.context.integration.filechannel.nio.atomic.properties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.configuration2.AbstractConfiguration;

import de.invesdwin.context.integration.filechannel.nio.atomic.AtomicNioFileChannel;
import de.invesdwin.context.integration.filechannel.nio.atomic.AtomicNioFileChannelPath;
import de.invesdwin.context.system.properties.AProperties;
import de.invesdwin.context.system.properties.ICloseableProperties;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.pool.PooledFastByteArrayOutputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

/**
 * Properties implementation utilizing a batch update pattern within a single properties file.
 * 
 * <p>
 * <b>When to use:</b> Use this when an updater needs to modify multiple parameters in a batch with idempotent results
 * between processes. It ensures that consumers only see fully completed configuration states rather than intermediate,
 * partially-updated states.
 * 
 * <p>
 * <b>Pattern used:</b> Loads all properties into memory, allowing modifications to be buffered. Upon invoking
 * {@code close()}, the entire state is serialized and flushed to a single shared {@code transactional.properties} file
 * via an atomic move operation (provided by {@link AtomicNioFileChannel}).
 */
@ThreadSafe
public class TransactionalFileProperties extends AProperties implements ICloseableProperties {

    private static final String PROPERTIES_FILENAME = "transactional.properties";

    private final AtomicNioFileChannel fileChannel;
    private final AtomicNioFileChannel targetChannel;
    private volatile Properties propertiesFile;
    private volatile boolean modified;

    public TransactionalFileProperties(final File baseFolder) {
        //CHECKSTYLE:OFF
        this(new AtomicNioFileChannel(newDefaultFolder(baseFolder).toURI()));
        //CHECKSTYLE:ON
    }

    public TransactionalFileProperties(final AtomicNioFileChannelPath path) {
        //CHECKSTYLE:OFF
        this(new AtomicNioFileChannel(path));
        //CHECKSTYLE:ON
    }

    public TransactionalFileProperties(final AtomicNioFileChannel fileChannel) {
        this.fileChannel = fileChannel;
        this.targetChannel = fileChannel.withFilename(PROPERTIES_FILENAME);
    }

    public static File newDefaultFolder(final File baseFolder) {
        return new File(baseFolder, TransactionalFileProperties.class.getSimpleName());
    }

    public AtomicNioFileChannel getFileChannel() {
        return fileChannel;
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
        final byte[] bytes = targetChannel.downloadBytes();
        if (bytes != null) {
            try (InputStream in = new FastByteArrayInputStream(bytes)) {
                props.load(in);
            } catch (final IOException e) {
                throw new RuntimeException("Failed to load properties from channel: " + targetChannel, e);
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
        if (!modified) {
            return;
        }
        try (PooledFastByteArrayOutputStream out = PooledFastByteArrayOutputStream.newInstance()) {
            getPropertiesFile().store(out, null);
            targetChannel.upload(out.asInputStream());
        } catch (final IOException e) {
            throw new RuntimeException("Failed to save properties to channel: " + targetChannel, e);
        }
    }
}