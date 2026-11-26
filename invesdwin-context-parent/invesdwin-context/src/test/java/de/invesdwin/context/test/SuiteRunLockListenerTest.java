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
 * The listener is what turns "the suites of a module run serially" into something a second run can observe, so it has
 * to really hold a cross-process guard for the session and give it up again - a guard that silently always succeeds is
 * worse than no guard, because the next reader of a red suite run will trust it.
 * <p>
 * The reader/writer split is the point of these tests: a reference-CREATING run must be alone, while comparison-only
 * runs may overlap. Both directions have to refuse (writer while a reader is live, reader while the writer is live),
 * parallel readers must NOT refuse, and a marker left behind by a killed process must not keep refusing - a guard that
 * deadlocks the fleet after one crashed fork is worse than none.
 * <p>
 * One listener class plays both roles here, because the guard's identity is the suite family (the listener class) and
 * the role is only what this particular run does with reference files. A writer and a reader of two different classes
 * would have two guards to begin with, which {@link #testEachListenerHasAGuardOfItsOwn} covers separately.
 */
@NotThreadSafe
public class SuiteRunLockListenerTest {

    @Test
    public void testTheWriterSessionHoldsTheGuardNamedForTheListenerAndFreesItAgain() throws IOException {
        final File lockDir = newLockDir(RoleTestListener.class);
        final File writerFile = new File(lockDir, "writer");
        deleteDir(lockDir);

        //the session parameter is unused by the guard, so the two hooks are driven directly
        final RoleTestListener holder = newWriter();
        holder.launcherSessionOpened(null);
        Assertions.assertThat(holder.isSessionLockHeld()).isTrue();
        Assertions.assertThat(writerFile)
                .as("a writer session takes the writer guard, creating the folder it sits in")
                .isFile();
        Assertions.assertThat(lockDir.getName())
                .as("each suite family gets a folder of its own")
                .isEqualTo(RoleTestListener.class.getSimpleName());

        //what a second suite process would hit; inside one jvm the same mechanism reports an overlapping lock instead of
        //returning null, which is what FileChannelLock turns into OVERLAPPING_FILE_LOCK_BY_OTHER_THREAD.
        Assertions.assertThat(isOuterAttemptRefused(writerFile))
                .as("the live session holds an os level lock on that file")
                .isTrue();

        holder.launcherSessionClosed(null);
        Assertions.assertThat(writerFile)
                .as("the closing session frees it, and FileChannelLock unlinks the file")
                .doesNotExist();

        final RoleTestListener next = newWriter();
        next.launcherSessionOpened(null);
        Assertions.assertThat(next.isSessionLockHeld()).as("the next session gets the guard again").isTrue();
        Assertions.assertThat(next.getLastRefusalReason()).isNull();
        next.launcherSessionClosed(null);
    }

    /**
     * The default a listener gets when it does not say which role it runs: the destructive one. Too strict is
     * survivable here, too lenient silently corrupts reference files.
     */
    @Test
    public void testAListenerThatDoesNotSayItsRoleGuardsAsAWriter() {
        final File lockDir = newLockDir(DefaultRoleTestListener.class);
        deleteDir(lockDir);
        final DefaultRoleTestListener first = new DefaultRoleTestListener();
        final DefaultRoleTestListener second = new DefaultRoleTestListener();
        first.launcherSessionOpened(null);
        second.launcherSessionOpened(null);
        Assertions.assertThat(new File(lockDir, "writer")).isFile();
        Assertions.assertThat(second.getLastRefusalReason())
                .as("two unnamed sessions contend, which only a writer posture can detect")
                .contains("writer guard");
        second.launcherSessionClosed(null);
        first.launcherSessionClosed(null);
        deleteDir(lockDir);
    }

    @Test
    public void testAWriterIsRefusedWhileAReaderIsLive() {
        final File lockDir = newLockDir(RoleTestListener.class);
        deleteDir(lockDir);

        final RoleTestListener reader = newReader("one");
        reader.launcherSessionOpened(null);
        Assertions.assertThat(reader.getLastRefusalReason()).as("a lone reader is not refused").isNull();

        final RoleTestListener writer = newWriter();
        writer.launcherSessionOpened(null);
        Assertions.assertThat(writer.getLastRefusalReason())
                .as("the writer sees the live reader marker and says which")
                .contains("live reader sessions")
                .contains("reader_one");
        //the warn posture: it proceeds, and it keeps marking itself so later readers see it
        Assertions.assertThat(writer.isSessionLockHeld()).isTrue();

        writer.launcherSessionClosed(null);
        Assertions.assertThat(new File(lockDir, "writer")).doesNotExist();
        Assertions.assertThat(reader.isSessionLockHeld())
                .as("the reader's own marker is untouched by the writer")
                .isTrue();
        reader.launcherSessionClosed(null);
        Assertions.assertThat(new File(lockDir, "reader_one"))
                .as("and the reader unlinks its own on close")
                .doesNotExist();
    }

    @Test
    public void testAReaderIsRefusedWhileAWriterIsLive() {
        final File lockDir = newLockDir(RoleTestListener.class);
        deleteDir(lockDir);

        final RoleTestListener writer = newWriter();
        writer.launcherSessionOpened(null);
        Assertions.assertThat(writer.isSessionLockHeld()).isTrue();

        final RoleTestListener reader = newReader("one");
        reader.launcherSessionOpened(null);
        Assertions.assertThat(reader.getLastRefusalReason())
                .as("the reader probes the writer guard and names it")
                .contains("the writer guard");
        Assertions.assertThat(reader.isSessionLockHeld())
                .as("it still marks itself, since the posture is to proceed and be visible")
                .isTrue();

        reader.launcherSessionClosed(null);
        Assertions.assertThat(new File(lockDir, "writer"))
                .as("the reader's close removes nothing of the writer's")
                .isFile();
        writer.launcherSessionClosed(null);
        deleteDir(lockDir);
    }

    @Test
    public void testParallelReadersDoNotRefuseEachOtherAndAreBothSeenByAWriter() {
        final File lockDir = newLockDir(RoleTestListener.class);
        deleteDir(lockDir);

        final RoleTestListener first = newReader("one");
        final RoleTestListener second = newReader("two");
        first.launcherSessionOpened(null);
        second.launcherSessionOpened(null);

        Assertions.assertThat(first.getLastRefusalReason()).isNull();
        Assertions.assertThat(second.getLastRefusalReason())
                .as("a reader of another process holds a marker of its own, so it is not contended")
                .isNull();
        Assertions.assertThat(new File(lockDir, "reader_one")).isFile();
        Assertions.assertThat(new File(lockDir, "reader_two")).isFile();

        final RoleTestListener writer = newWriter();
        writer.launcherSessionOpened(null);
        Assertions.assertThat(writer.getLastRefusalReason())
                .as("the writer lists every live reader, so the log names all of them")
                .contains("reader_one")
                .contains("reader_two");
        Assertions.assertThat(new File(lockDir, "reader_one")).as("listing readers locks nothing away").isFile();

        writer.launcherSessionClosed(null);
        first.launcherSessionClosed(null);
        Assertions.assertThat(new File(lockDir, "reader_two"))
                .as("one reader's close removes only its own marker")
                .isFile();
        second.launcherSessionClosed(null);
        deleteDir(lockDir);
    }

    /**
     * The hazard of any file-marker guard: a killed fork leaves its marker behind, and a collision that never goes away
     * trains everyone to ignore the log line.
     */
    @Test
    public void testAMarkerLeftByADeadProcessIsNeitherACollisionNorLeftStanding() throws IOException {
        final File lockDir = newLockDir(RoleTestListener.class);
        deleteDir(lockDir);
        lockDir.mkdirs();
        final File deadMarker = new File(lockDir, "reader_1234_deadhost_deadbeef");
        Assertions.assertThat(deadMarker.createNewFile()).isTrue();

        final RoleTestListener writer = newWriter();
        writer.launcherSessionOpened(null);
        Assertions.assertThat(writer.getLastRefusalReason())
                .as("a marker nobody can lock any more belongs to a dead process")
                .isNull();
        Assertions.assertThat(deadMarker).as("and the probe that could lock it removes the residue").doesNotExist();
        writer.launcherSessionClosed(null);
        deleteDir(lockDir);
    }

    @Test
    public void testASecondReaderOfTheSameProcessIsRefusedAndRemovesNothing() {
        final File lockDir = newLockDir(RoleTestListener.class);
        deleteDir(lockDir);

        final RoleTestListener holder = newReader("one");
        holder.launcherSessionOpened(null);
        final File marker = new File(lockDir, "reader_one");

        //two launcher sessions in one fork share one owner marker: the second is refused, logs it and proceeds
        final RoleTestListener contender = newReader("one");
        contender.launcherSessionOpened(null);
        Assertions.assertThat(contender.getLastRefusalReason()).contains("already holds the reader marker");
        Assertions.assertThat(marker).as("a refused session takes nothing away from the live holder").isFile();
        contender.launcherSessionClosed(null);
        Assertions.assertThat(marker).as("and its close removes nothing it never held").isFile();
        Assertions.assertThat(holder.isSessionLockHeld()).isTrue();

        holder.launcherSessionClosed(null);
        Assertions.assertThat(marker).as("only the holder's close unlinks it").doesNotExist();
    }

    @Test
    public void testEachListenerHasAGuardOfItsOwn() {
        deleteDir(newLockDir(RoleTestListener.class));
        final File secondDir = newLockDir(SecondTestListener.class);
        deleteDir(secondDir);
        final File writerFile = new File(newLockDir(RoleTestListener.class), "writer");

        final RoleTestListener first = newWriter();
        final SecondTestListener second = new SecondTestListener();
        first.launcherSessionOpened(null);
        second.launcherSessionOpened(null);
        Assertions.assertThat(writerFile).as("the guard is named for the listener class").isFile();
        Assertions.assertThat(new File(secondDir, "writer"))
                .as("another listener has a folder of its own and takes its guard")
                .isFile();
        Assertions.assertThat(second.getLastRefusalReason()).as("so it is not refused by the first").isNull();
        first.launcherSessionClosed(null);
        second.launcherSessionClosed(null);
        deleteDir(newLockDir(RoleTestListener.class));
        deleteDir(secondDir);
    }

    /**
     * True if an attempt from outside the session's own lock object can not lock the file - which is how a second suite
     * process would be refused.
     */
    private static boolean isOuterAttemptRefused(final File file) throws IOException {
        try (FileChannel other = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            return other.tryLock() == null;
        } catch (final OverlappingFileLockException e) {
            return true;
        }
    }

    private static RoleTestListener newWriter() {
        return new RoleTestListener(true, null);
    }

    private static RoleTestListener newReader(final String owner) {
        return new RoleTestListener(false, owner);
    }

    private static File newLockDir(final Class<?> listenerClass) {
        //the guard names the folder after Reflections.getClassSimpleNameNonBlank of its own class
        return new File(new File(ContextProperties.getLogDirectory(), "locks"), listenerClass.getSimpleName());
    }

    private static void deleteDir(final File dir) {
        final File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                file.delete();
            }
        }
        dir.delete();
    }

    @NotThreadSafe
    private static class RoleTestListener extends ASuiteRunLockListener {

        private final String readerOwner;

        RoleTestListener(final boolean createReferenceFile, final String readerOwner) {
            super(createReferenceFile);
            this.readerOwner = readerOwner;
        }

        @Override
        protected String newReaderOwner() {
            //the production marker is per process (pid@host plus a uuid); a test names its pretend processes
            return readerOwner;
        }

    }

    @NotThreadSafe
    private static final class SecondTestListener extends ASuiteRunLockListener {
        //no-arg, so also a writer - in a folder of its own
    }

    @NotThreadSafe
    private static final class DefaultRoleTestListener extends ASuiteRunLockListener {
        //no-arg, so also a writer - the point of this test
    }

}
