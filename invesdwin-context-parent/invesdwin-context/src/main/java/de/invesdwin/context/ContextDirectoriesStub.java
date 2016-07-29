package de.invesdwin.context;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;

import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.TestContext;
import de.invesdwin.context.test.stub.StubSupport;
import de.invesdwin.util.assertions.Assertions;

@Named
@NotThreadSafe
public class ContextDirectoriesStub extends StubSupport {

    private static final Set<File> PROTECTED_DIRECTORIES = new HashSet<File>();

    static {
        addProtectedDirectory(ContextProperties.TEMP_CLASSPATH_DIRECTORY);
    }

    @Override
    public void setUpOnce(final ATest test, final TestContext ctx) {
        for (final File dir : new File[] { ContextProperties.getCacheDirectory(), ContextProperties.TEMP_DIRECTORY,
                ContextProperties.getHomeDirectory() }) {
            if (dir != null && dir.exists()) {
                for (final File f : dir.listFiles()) {
                    boolean isProtectedDir = false;
                    for (final File protectedDir : PROTECTED_DIRECTORIES) {
                        if (f.getAbsolutePath().startsWith(protectedDir.getAbsolutePath())) {
                            isProtectedDir = true;
                            break;
                        }
                    }
                    if (!isProtectedDir) {
                        FileUtils.deleteQuietly(f);
                    }
                }
                Assertions.assertThat(dir.exists());
            }
        }
    }

    public static void addProtectedDirectory(final File protectedDirectory) {
        PROTECTED_DIRECTORIES.add(protectedDirectory);
    }

}
