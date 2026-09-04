package de.invesdwin.context.system.properties.concurrent.multiprocess;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.configuration2.AbstractConfiguration;

import de.invesdwin.context.system.properties.AProperties;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.string.Charsets;

@ThreadSafe
public class AtomicFilesProperties extends AProperties {

    private static final String PROPERTY_FILE_EXTENSION = ".property";

    private final AtomicFilesHelper atomicFilesHelper;

    public AtomicFilesProperties(final File baseFolder) {
        this(new AtomicFilesHelper(newDefaultFolder(baseFolder)));
    }

    public AtomicFilesProperties(final AtomicFilesHelper atomicFilesHelper) {
        this.atomicFilesHelper = atomicFilesHelper;
    }

    public static File newDefaultFolder(final File baseFolder) {
        return new File(baseFolder, AtomicFilesProperties.class.getSimpleName());
    }

    @Override
    protected AbstractConfiguration createDelegate() {
        return new AbstractConfiguration() {

            private Path getPath(final String key) {
                return atomicFilesHelper.getFolder().resolve(Files.normalizeFilename(key + PROPERTY_FILE_EXTENSION));
            }

            private boolean isValidPropertyFile(final Path path) {
                final String fileName = path.getFileName().toString();
                return Files.isRegularFile(path) && fileName.endsWith(PROPERTY_FILE_EXTENSION);
            }

            private List<Path> listValidPropertyFiles() {
                final List<Path> paths = new ArrayList<>();
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(atomicFilesHelper.getFolder())) {
                    for (final Path path : stream) {
                        if (isValidPropertyFile(path)) {
                            paths.add(path);
                        }
                    }
                } catch (final IOException e) {
                    throw new RuntimeException(
                            "Failed to list properties directory: " + atomicFilesHelper.getFolder().toAbsolutePath(),
                            e);
                }
                return paths;
            }

            private String readProperty(final Path path) {
                try {
                    return new String(Files.readAllBytes(path), Charsets.defaultCharset());
                } catch (final NoSuchFileException e) {
                    return null;
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to read property file: " + path.toAbsolutePath(), e);
                }
            }

            @Override
            protected boolean isEmptyInternal() {
                atomicFilesHelper.maybeRunCleanup();
                final Iterator<String> keys = getKeysInternal();
                return !keys.hasNext();
            }

            @Override
            protected Object getPropertyInternal(final String key) {
                final Path path = getPath(key);
                if (!Files.exists(path)) {
                    return null;
                }
                return readProperty(path);
            }

            @Override
            protected Iterator<String> getKeysInternal() {
                atomicFilesHelper.maybeRunCleanup();
                final List<String> keys = new ArrayList<>();
                for (final Path path : listValidPropertyFiles()) {
                    final String fileName = path.getFileName().toString();
                    final String key = fileName.substring(0, fileName.length() - PROPERTY_FILE_EXTENSION.length());
                    keys.add(key);
                }
                return keys.iterator();
            }

            @Override
            protected boolean containsKeyInternal(final String key) {
                return Files.exists(getPath(key));
            }

            @Override
            protected void clearPropertyDirect(final String key) {
                try {
                    Files.deleteIfExists(getPath(key));
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to delete property file for key: " + key, e);
                }
            }

            @Override
            protected void addPropertyDirect(final String key, final Object value) {
                atomicFilesHelper.maybeRunCleanup();
                final Path targetPath = getPath(key);
                try {
                    atomicFilesHelper.writeAtomic(targetPath, String.valueOf(value));
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to write property to shared directory: " + key, e);
                }
            }

            @Override
            protected boolean containsValueInternal(final Object value) {
                atomicFilesHelper.maybeRunCleanup();
                if (value == null) {
                    return false;
                }
                final String valueStr = String.valueOf(value);
                for (final Path path : listValidPropertyFiles()) {
                    final String content = readProperty(path);
                    if (valueStr.equals(content)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}