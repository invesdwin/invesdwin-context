package de.invesdwin.context.beans.init.platform;

import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Files;

/**
 * The shutdown hook's delete of the process temp tree is bounded, and the residue is the designed case rather than a
 * leak: the sweep the same initializer starts on the next launch treats a sibling temp directory whose lock file is
 * missing or unheld as an incomplete delete on shutdown, which is exactly what a killed JVM always left.
 * <p>
 * Without the bound, measured on the production feed box during the 2026-11-17 roll: a 4 h 22 m derived-store session
 * left a scratch tree of roughly a million entries, and deleting it in the hook held the JVM 203 s into a restart whose
 * application stop had finished in about one second - the SIGTERM handler sits blocked behind the SIGHUP-driven hook
 * chain for all of it, so the process looks deaf to termination.
 */
@NotThreadSafe
public class DefaultPlatformInitializerTempDeleteTest {

    @Test
    public void aTreeInsideTheBudgetIsDeletedCompletely() throws IOException {
        final File root = newTree(3, 4);
        try {
            //root + 3 directories + 12 files
            Assertions.assertThat(DefaultPlatformInitializer.deleteTempDirectoryWithinBudget(root,
                    Integer.MAX_VALUE)).isEqualTo(16);
            Assertions.assertThat(root).as("an unbounded delete still removes the tree, directory included")
                    .doesNotExist();
        } finally {
            Files.deleteQuietly(root);
        }
    }

    @Test
    public void aTreeBeyondTheBudgetStopsAndLeavesTheRestForTheNextStart() throws IOException {
        final File root = newTree(3, 4);
        try {
            //five entries are spent inside the first subtree (the directory plus its four files), which is where the
            //cap fires; the other two subtrees are never entered
            Assertions.assertThat(DefaultPlatformInitializer.deleteTempDirectoryWithinBudget(root, 5))
                    .as("the caller tests this return value to decide whether to report a truncation")
                    .isGreaterThan(5);
            Assertions.assertThat(root).as("the residue must still be there for the sweep to reap").exists();
            Assertions.assertThat(countFiles(root)).as("one subtree deleted, two left untouched").isEqualTo(8);
        } finally {
            Files.deleteQuietly(root);
        }
    }

    private File newTree(final int dirs, final int filesPerDir) throws IOException {
        final File root = new File(Files.getTempDirectory(),
                DefaultPlatformInitializerTempDeleteTest.class.getSimpleName());
        Files.deleteQuietly(root);
        Assertions.assertThat(root.mkdir()).isTrue();
        for (int d = 0; d < dirs; d++) {
            final File dir = new File(root, "sub" + d);
            Assertions.assertThat(dir.mkdir()).isTrue();
            for (int f = 0; f < filesPerDir; f++) {
                Assertions.assertThat(new File(dir, "file" + f).createNewFile()).isTrue();
            }
        }
        return root;
    }

    private int countFiles(final File dir) {
        int count = 0;
        final File[] children = dir.listFiles();
        if (children != null) {
            for (final File child : children) {
                count += child.isDirectory() ? countFiles(child) : 1;
            }
        }
        return count;
    }

}
