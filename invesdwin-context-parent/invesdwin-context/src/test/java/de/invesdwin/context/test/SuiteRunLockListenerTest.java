package de.invesdwin.context.test;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.StandardOpenOption;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.util.assertions.Assertions;

/**
 * The listener is what turns "the suites of a module run serially" into something a second run can observe, so it has to
 * really hold a file lock for the session and give it up again - a guard that silently always succeeds is worse than no
 * guard, because the next reader of a red suite run will trust it.
 */
@NotThreadSafe
public class SuiteRunLockListenerTest {

    @Test
    public void testTheSessionHoldsTheLockNamedForTheListenerAndFreesItAgain() throws IOException {
        final File lockFile = newLockFile(FirstTestListener.class);
        lockFile.delete();

        //the session parameter is unused by the guard, so the two hooks are driven directly
        final FirstTestListener holder = new FirstTestListener();
        holder.launcherSessionOpened(null);
        Assertions.assertThat(lockFile)
                .as("an uncontended session takes the lock, creating the subfolder it sits in").isFile();
        Assertions.assertThat(lockFile.getParentFile().getName())
                .as("the lock gets a subfolder of its own, since FileChannelLock unlinks its file on unlock")
                .isEqualTo("locks");

        //what a second suite process would hit; inside one jvm the same mechanism reports an overlapping lock instead of
        //returning null, which is what FileChannelLock turns into OVERLAPPING_FILE_LOCK_BY_OTHER_THREAD.
        boolean refused = false;
        try (FileChannel other = FileChannel.open(lockFile.toPath(), StandardOpenOption.CREATE,
                StandardOpenOption.WRITE)) {
            other.tryLock();
        } catch (OverlappingFileLockException e) {
            refused = true;
        }
        Assertions.assertThat(refused).as("the live session holds an os level lock on that file").isTrue();

        holder.launcherSessionClosed(null);
        Assertions.assertThat(lockFile).as("the closing session frees it, and FileChannelLock unlinks the file")
                .doesNotExist();

        final FirstTestListener next = new FirstTestListener();
        next.launcherSessionOpened(null);
        Assertions.assertThat(lockFile).as("the next session gets a lock of its own again").isFile();
        next.launcherSessionClosed(null);
    }

    /**
     * The hazard the guard's javadoc warns about: a refused session that unlinked the file on close would silently
     * un-guard the run that does hold it.
     */
    @Test
    public void testARefusedSessionLeavesTheLiveHoldersLockUntouched() {
        final File lockFile = newLockFile(FirstTestListener.class);
        final FirstTestListener holder = new FirstTestListener();
        holder.launcherSessionOpened(null);

        //two launcher sessions in one fork: the second one is refused, logs it and proceeds anyway (warn posture)
        final FirstTestListener contender = new FirstTestListener();
        contender.launcherSessionOpened(null);
        Assertions.assertThat(lockFile).as("a refused session takes nothing away from the live holder").isFile();
        contender.launcherSessionClosed(null);
        Assertions.assertThat(lockFile).as("and its close removes nothing it never held").isFile();

        holder.launcherSessionClosed(null);
        Assertions.assertThat(lockFile).as("only the holder's close unlinks it").doesNotExist();
    }

    @Test
    public void testEachListenerHasALockOfItsOwn() {
        final File firstFile = newLockFile(FirstTestListener.class);
        final File secondFile = newLockFile(SecondTestListener.class);
        final FirstTestListener first = new FirstTestListener();
        final SecondTestListener second = new SecondTestListener();
        first.launcherSessionOpened(null);
        second.launcherSessionOpened(null);
        Assertions.assertThat(firstFile).as("the file is named for the listener class").isFile();
        Assertions.assertThat(secondFile).as("another listener has a file of its own and is not refused by it").isFile();
        first.launcherSessionClosed(null);
        second.launcherSessionClosed(null);
    }

    private static File newLockFile(final Class<?> listenerClass) {
        //the guard names the file after Reflections.getClassSimpleNameNonBlank of its own class
        return new File(new File(ContextProperties.getLogDirectory(), "locks"),
                listenerClass.getSimpleName() + ".lock");
    }

    @NotThreadSafe
    private static class FirstTestListener extends ASuiteRunLockListener {
    }

    @NotThreadSafe
    private static class SecondTestListener extends ASuiteRunLockListener {
    }

}
