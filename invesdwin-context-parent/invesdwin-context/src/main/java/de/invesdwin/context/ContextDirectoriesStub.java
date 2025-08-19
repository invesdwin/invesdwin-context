package de.invesdwin.context;

import java.io.File;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.io.FilenameUtils;

import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.TestContext;
import de.invesdwin.context.test.stub.StubSupport;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.fast.IFastIterableSet;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.string.Strings;
import jakarta.inject.Named;

@Named
@ThreadSafe
public class ContextDirectoriesStub extends StubSupport {

    private static final IFastIterableSet<String> PROTECTED_DIRECTORIES = ILockCollectionFactory.getInstance(true)
            .newFastIterableLinkedSet();

    static {
        addProtectedDirectory(ContextProperties.TEMP_CLASSPATH_DIRECTORY);
        addProtectedDirectory(ContextProperties.TEMP_DIRECTORY_LOCK.getFile());
        addProtectedDirectory(ContextProperties.TEMP_DIRECTORY_JAVA);
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
    public void tearDownOnce(final ATest test, final TestContext ctx) {
        if (!ctx.isFinished()) {
            return;
        }
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
            final String[] protectedDirectories = PROTECTED_DIRECTORIES.asArray(Strings.EMPTY_ARRAY);
            for (final File f : dir.listFiles()) {
                boolean isProtectedDir = false;
                for (int i = 0; i < protectedDirectories.length; i++) {
                    final String protectedDirectory = protectedDirectories[i];
                    if (FilenameUtils.normalizeNoEndSeparator(f.getAbsolutePath()).startsWith(protectedDirectory)) {
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
