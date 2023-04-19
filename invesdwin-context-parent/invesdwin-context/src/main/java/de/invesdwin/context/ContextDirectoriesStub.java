package de.invesdwin.context;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.FilenameUtils;

import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.stub.StubSupport;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.Objects;
import jakarta.inject.Named;

@Named
@NotThreadSafe
public class ContextDirectoriesStub extends StubSupport {

    private static final Set<String> PROTECTED_DIRECTORIES = new HashSet<String>();

    static {
        addProtectedDirectory(ContextProperties.TEMP_CLASSPATH_DIRECTORY);
        addProtectedDirectory(ContextProperties.TEMP_DIRECTORY_LOCK.getFile());
        if (PlatformInitializerProperties.isKeepSystemHomeDuringTests()) {
            addProtectedDirectory(ContextProperties.getHomeDirectory());
            addProtectedDirectory(ContextProperties.getHomeDataDirectory());
        }
    }

    /**
     * Can not clean up in setupOnce because a startup process might create important files during bootstrap that will
     * be missing then (e.g. SubscriptionStorageFile during IQFeed startup).
     */
    @Override
    public void tearDownOnce(final ATest test) throws Exception {
        cleanDirectory(ContextProperties.getCacheDirectory());
        cleanDirectory(ContextProperties.TEMP_DIRECTORY);
        final File homeDirectory = ContextProperties.getHomeDirectory();
        cleanDirectory(homeDirectory);
        final File homeDataDirectory = ContextProperties.getHomeDataDirectory();
        if (!Objects.equals(homeDirectory, homeDataDirectory)) {
            cleanDirectory(homeDataDirectory);
        }
    }

    private void cleanDirectory(final File dir) {
        if (dir != null && dir.exists()) {
            for (final File f : dir.listFiles()) {
                boolean isProtectedDir = false;
                for (final String protectedDir : PROTECTED_DIRECTORIES) {
                    if (FilenameUtils.normalizeNoEndSeparator(f.getAbsolutePath()).startsWith(protectedDir)) {
                        isProtectedDir = true;
                        break;
                    }
                }
                if (!isProtectedDir) {
                    Files.deleteQuietly(f);
                }
            }
        }
    }

    public static void addProtectedDirectory(final File protectedDirectory) {
        PROTECTED_DIRECTORIES.add(FilenameUtils.normalizeNoEndSeparator(protectedDirectory.getAbsolutePath()));
    }

}
