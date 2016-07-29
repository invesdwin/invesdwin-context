package de.invesdwin.context.system.classpath;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.log.error.Err;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.instrument.DynamicInstrumentationReflections;

@Immutable
public class ClasspathResourceProcessor {

    /*
     * prevent eclipse plugin classpath from polluting the jar
     */
    private static final String[] BLACKLISTED_PATH_PATTERNS = { ".*/org\\.eclipse\\.osgi/bundles/.*/\\.cp/" };

    public void process(final IClasspathResourceVisitor visitor) {
        final String classpath = new SystemProperties().getString("java.class.path");
        final String[] paths = classpath.split(File.pathSeparator);

        final Set<String> pathsAddedToSystemClassLoader = DynamicInstrumentationReflections
                .getPathsAddedToSystemClassLoader();
        for (final String path : paths) {
            for (final String blacklistedPathPattern : BLACKLISTED_PATH_PATTERNS) {
                if (path.matches(blacklistedPathPattern)) {
                    continue;
                }
            }
            if (pathsAddedToSystemClassLoader.contains(path)) {
                continue;
            }
            final File file = new File(path);
            if (file.exists()) {
                if (!process(file, file, visitor)) {
                    visitor.finish();
                    return;
                }
            }
        }

        visitor.finish();
    }

    private boolean process(final File root, final File file, final IClasspathResourceVisitor visitor) {
        if (file.isDirectory()) {
            if (!processDirectory(root, file, visitor)) {
                return false;
            }
            for (final File child : file.listFiles()) {
                if (!process(root, child, visitor)) {
                    return false;
                }
            }
        } else {
            try {
                if (file.getName().toLowerCase().endsWith(".jar")) {
                    if (!processJar(file, visitor)) {
                        return false;
                    }
                } else {
                    if (!processFile(root, file, visitor)) {
                        return false;
                    }
                }
            } catch (final IOException e) {
                throw Err.process(e);
            }
        }

        return true;
    }

    private boolean processFile(final File root, final File file, final IClasspathResourceVisitor visitor)
            throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            final String fullPath = file.getAbsolutePath();
            final String resourcePath = createResourcePath(root, file);
            if (!visitor.visit(fullPath, resourcePath, in)) {
                return false;
            }
        }
        return true;
    }

    /**
     * we also need to process directories, since if they are missing in newly generated jars, classpath scanning by
     * spring does not work!
     */
    private boolean processDirectory(final File root, final File file, final IClasspathResourceVisitor visitor) {
        final String fullPath = file.getAbsolutePath();
        final String resourcePath = createResourcePath(root, file) + "/";
        return visitor.visit(fullPath, resourcePath, new ByteArrayInputStream(new byte[0]));
    }

    private boolean processJar(final File file, final IClasspathResourceVisitor visitor) throws IOException {
        try (JarFile jar = new JarFile(file)) {
            if (jar != null) {
                final Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    final JarEntry entry = entries.nextElement();
                    try (InputStream in = jar.getInputStream(entry)) {
                        final String fullPath = file.getAbsolutePath() + "!" + entry.getName();
                        final String resourcePath = entry.getName();
                        if (!visitor.visit(fullPath, resourcePath, in)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private String createResourcePath(final File root, final File file) {
        final StringBuffer sb = new StringBuffer();
        sb.append(file.getName());
        File curFile = file.getParentFile();
        while (curFile != null && !curFile.equals(root)) {
            sb.insert(0, File.separator).insert(0, curFile.getName());
            curFile = curFile.getParentFile();
        }
        return sb.toString();
    }

}
