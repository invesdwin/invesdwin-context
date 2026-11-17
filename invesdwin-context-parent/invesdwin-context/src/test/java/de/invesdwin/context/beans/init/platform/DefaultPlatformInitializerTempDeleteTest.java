package de.invesdwin.context.beans.init.platform;

import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

/**
 * The shutdown hook's delete of the process temp tree is bounded by how long it may keep the JVM waiting, not by how
 * much of the tree it may remove: the delete runs on its own process and outlives the shutdown it was started from.
 * <p>
 * Without the bound, measured on the production feed box during the 2026-11-17 roll: a 4 h 22 m derived-store session
 * left a scratch tree of roughly a million entries, and deleting it held the JVM 203 s into a restart whose application
 * stop had finished in about one second - the SIGHUP-driven hook chain owns the shutdown monitor and the SIGTERM
 * handler blocks behind it, so the process reads as deaf to termination for all of it.
 * <p>
 * A tree left behind is not a leak: the sweep the same initializer starts on the next launch treats a sibling temp
 * directory whose lock file is missing or unheld as an incomplete delete on shutdown, which is the path a killed JVM
 * has always relied on.
 */
@NotThreadSafe
public class DefaultPlatformInitializerTempDeleteTest {

    private static final Duration GENEROUS_BUDGET = new Duration(30, FTimeUnit.SECONDS);

    @Test
    public void aTreeThatFitsTheBudgetIsDeletedBeforeTheShutdownContinues() throws IOException {
        final File root = newTree("small", 3, 4);
        try {
            Assertions.assertThat(DefaultPlatformInitializer.deleteTempDirectoryBounded(root, GENEROUS_BUDGET))
                    .as("a tree that fits the wait is gone before the hook returns")
                    .isTrue();
            Assertions.assertThat(root).doesNotExist();
        } finally {
            Files.deleteQuietly(root);
        }
    }

    @Test
    public void aTreeThatDoesNotFitTheBudgetDoesNotHoldTheShutdownOpen() throws IOException {
        //fifty thousand entries: no box deletes that inside one millisecond, so the budget always expires first
        final File root = newTree("large", 200, 250);
        final Instant start = new Instant();
        try {
            Assertions.assertThat(DefaultPlatformInitializer.deleteTempDirectoryBounded(root, Duration.ONE_MILLISECOND))
                    .as("a delete that outlives the budget is reported as unfinished")
                    .isFalse();
            Assertions.assertThat(new Duration(start, new Instant()).longValue(FTimeUnit.MILLISECONDS))
                    .as("the hook itself returned; the delete continues on a process of its own")
                    .isLessThan(5000);
        } finally {
            //the detached process may still be walking the tree - java's own delete finishes the teardown
            Files.deleteQuietly(root);
        }
    }

    private File newTree(final String name, final int dirs, final int filesPerDir) throws IOException {
        final File root = new File(Files.getTempDirectory(),
                DefaultPlatformInitializerTempDeleteTest.class.getSimpleName() + "-" + name);
        Files.deleteQuietly(root);
        Files.forceMkdir(root);
        for (int d = 0; d < dirs; d++) {
            final File dir = new File(root, "sub" + d);
            Files.forceMkdir(dir);
            for (int f = 0; f < filesPerDir; f++) {
                Assertions.assertThat(new File(dir, "file" + f).createNewFile()).isTrue();
            }
        }
        return root;
    }

}
