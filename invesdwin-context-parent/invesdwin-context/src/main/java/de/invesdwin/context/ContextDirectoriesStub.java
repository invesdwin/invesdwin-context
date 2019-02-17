package de.invesdwin.context;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.TestContext;
import de.invesdwin.context.test.stub.StubSupport;

@Named
@NotThreadSafe
public class ContextDirectoriesStub extends StubSupport {

    private static final Set<String> PROTECTED_DIRECTORIES = new HashSet<String>();

    static {
        addProtectedDirectory(ContextProperties.TEMP_CLASSPATH_DIRECTORY);
        if (PlatformInitializerProperties.isKeepSystemHomeDuringTests()) {
            addProtectedDirectory(ContextProperties.getHomeDirectory());
        }
    }

    @Override
    public void setUpOnce(final ATest test, final TestContext ctx) {
        for (final File dir : new File[] { ContextProperties.getCacheDirectory(), ContextProperties.TEMP_DIRECTORY,
                ContextProperties.getHomeDirectory() }) {
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
                        FileUtils.deleteQuietly(f);
                    }
                }
            }
        }
    }

    public static void addProtectedDirectory(final File protectedDirectory) {
        PROTECTED_DIRECTORIES.add(FilenameUtils.normalizeNoEndSeparator(protectedDirectory.getAbsolutePath()));
    }

}
