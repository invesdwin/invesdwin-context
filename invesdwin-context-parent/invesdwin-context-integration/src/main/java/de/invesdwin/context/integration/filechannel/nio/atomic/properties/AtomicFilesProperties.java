package de.invesdwin.context.integration.filechannel.nio.atomic.properties;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.configuration2.AbstractConfiguration;

import de.invesdwin.context.integration.filechannel.nio.NioFileInfo;
import de.invesdwin.context.integration.filechannel.nio.atomic.AtomicNioFileChannel;
import de.invesdwin.context.system.properties.AProperties;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.string.Charsets;

/**
 * Properties implementation utilizing a highly concurrent file-per-property pattern.
 * 
 * <p>
 * <b>When to use:</b> Use this when you have multiple readers and writers on multiple processes over a shared node
 * concurrently. Because each property is stored as a separate file, individual property updates do not lock or
 * overwrite the entire configuration set, avoiding race conditions during simultaneous distinct modifications.
 * 
 * <p>
 * <b>Pattern used:</b> Maps individual properties to dedicated files with a specific extension ({@code .property})
 * within a shared directory. Modifications are delegated to an {@link AtomicNioFileChannel} which safely performs
 * atomic writes.
 */
@ThreadSafe
public class AtomicFilesProperties extends AProperties {

    private static final String PROPERTY_FILE_EXTENSION = ".property";

    private final AtomicNioFileChannel fileChannel;

    public AtomicFilesProperties(final File baseFolder) {
        //CHECKSTYLE:OFF
        this(new AtomicNioFileChannel(newDefaultFolder(baseFolder).toURI()));
        //CHECKSTYLE:ON
    }

    public AtomicFilesProperties(final AtomicNioFileChannel fileChannel) {
        this.fileChannel = fileChannel;
    }

    public static File newDefaultFolder(final File baseFolder) {
        return new File(baseFolder, AtomicFilesProperties.class.getSimpleName());
    }

    public AtomicNioFileChannel getFileChannel() {
        return fileChannel;
    }

    @Override
    protected AbstractConfiguration createDelegate() {
        return new AbstractConfiguration() {

            private AtomicNioFileChannel getChannel(final String key) {
                return fileChannel.withFilename(Files.normalizeFilename(key + PROPERTY_FILE_EXTENSION));
            }

            private boolean isValidPropertyFile(final NioFileInfo info) {
                final String fileName = info.getFilename();
                return info.isFile() && fileName != null && fileName.endsWith(PROPERTY_FILE_EXTENSION);
            }

            private List<NioFileInfo> listValidPropertyFiles() {
                final List<NioFileInfo> validFiles = new ArrayList<>();
                for (final NioFileInfo info : fileChannel.list()) {
                    if (isValidPropertyFile(info)) {
                        validFiles.add(info);
                    }
                }
                return validFiles;
            }

            private String readProperty(final AtomicNioFileChannel channel) {
                final byte[] bytes = channel.downloadBytes();
                if (bytes == null) {
                    return null;
                }
                return new String(bytes, Charsets.defaultCharset());
            }

            @Override
            protected boolean isEmptyInternal() {
                final Iterator<String> keys = getKeysInternal();
                return !keys.hasNext();
            }

            @Override
            protected Object getPropertyInternal(final String key) {
                final AtomicNioFileChannel channel = getChannel(key);
                if (!channel.exists()) {
                    return null;
                }
                return readProperty(channel);
            }

            @Override
            protected Iterator<String> getKeysInternal() {
                final List<String> keys = new ArrayList<>();
                for (final NioFileInfo info : listValidPropertyFiles()) {
                    final String fileName = info.getFilename();
                    final String key = fileName.substring(0, fileName.length() - PROPERTY_FILE_EXTENSION.length());
                    keys.add(key);
                }
                return keys.iterator();
            }

            @Override
            protected boolean containsKeyInternal(final String key) {
                return getChannel(key).exists();
            }

            @Override
            protected void clearPropertyDirect(final String key) {
                getChannel(key).delete();
            }

            @Override
            protected void addPropertyDirect(final String key, final Object value) {
                getChannel(key).upload(String.valueOf(value).getBytes(Charsets.defaultCharset()));
            }

            @Override
            protected boolean containsValueInternal(final Object value) {
                if (value == null) {
                    return false;
                }
                final String valueStr = String.valueOf(value);
                for (final NioFileInfo info : listValidPropertyFiles()) {
                    final AtomicNioFileChannel channel = fileChannel.withFilename(info.getFilename());
                    final String content = readProperty(channel);
                    if (valueStr.equals(content)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}