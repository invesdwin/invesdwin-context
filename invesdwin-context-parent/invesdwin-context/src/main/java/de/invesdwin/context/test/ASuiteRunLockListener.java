package de.invesdwin.context.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.log.Log;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.concurrent.lock.file.FileChannelLock;
import de.invesdwin.util.concurrent.lock.file.HeartbeatFileChannelLockRegistry;
import de.invesdwin.util.lang.reflection.Reflections;

/**
 * Makes "the suites of one module run serially" observable at the moment it is broken: a JUnit launcher session takes a
 * cross-process guard for its whole lifetime, so a second suite run logs a named collision instead of showing up later
 * as unrelated failures spread across the shared store directories. The build does not enforce that convention - the
 * parent pom pins forkCount=1 and leaves the threadCount block commented out, which serialises classes inside one fork
 * and says nothing about a second suite process - and a file guard covers every launcher (surefire, IDE, hand-run),
 * where a shell {@code flock} would not, since {@code flock(2)} and Java file locks do not observe each other.
 *
 * <p>
 * The guard is a reader/writer one, because only one kind of suite run is actually destructive: a run that RE-CREATES
 * reference files rewrites the expectations every other run compares against, so it must be alone; a run that only
 * COMPARES against them may run next to other such runs. The subclass says which it is by passing
 * {@code createReferenceFile} to this constructor - true is the writer, false the reader.
 * <p>
 * A writer holds an exclusive {@link FileChannelLock} on {@code <logDir>/locks/<simpleName of the listener>/writer}, so
 * two writers cannot both hold it, and it marks the run for readers for the whole session. A reader marks itself in a
 * file of its own, {@code .../reader_<owner>}, locked exclusively by its own process; readers never contend with each
 * other, since they hold different files. Each side then looks at the other through the directory: a writer lists the
 * {@code reader_*} markers and refuses while a live one exists, a reader probes the {@code writer} file and refuses
 * while it is locked.
 * <p>
 * A marker being PRESENT is not what makes it live - a killed fork leaves its file behind, and a guard that then blocks
 * every later run forever is worse than none. So liveness is decided by locking the marker: {@code FileChannelLock}
 * gets it only if the owner is gone (the kernel releases the locks of a dead process), in which case the probe deletes
 * the residue as it closes. That is also why every marker is an OS lock rather than a plain file.
 *
 * <p>
 * Markers sit in a folder of their own under {@code <logDir>/locks}, named for the listener, so the log line, the
 * folder and the files all say which suite family is held and by whom; the log directory is what the generated
 * {@code .gitignore} already excludes, so a guard never dirties a checkout. The folder is created here because
 * {@link FileChannelLock} does not create missing parents, and a path it cannot open refuses with an io reason that
 * would read like a collision.
 * <p>
 * There is no static state: the launcher instantiates one listener per launcher session, the object lives exactly as
 * long as its guard has to be held, and it owns exactly the one lock it took. Two launcher sessions in one jvm do still
 * contend - inside one process the JDK rejects every overlapping region regardless of mode (measured
 * {@code OverlappingFileLockException} for shared-onto-shared on JDK 17), and both readers share one owner-named
 * marker - which is fine here, because one fork runs one suite at a time and the guard's subject is parallel
 * processes.
 * <p>
 * A refusal is logged and the run proceeds, because suites do get run side by side for work that touches none of the
 * contended stores; {@link #isSessionLockHeld()} and {@link #getLastRefusalReason()} keep that decision observable.
 * <p>
 * A module registers a subclass as a service under {@code META-INF/services} next to its test sources (this codebase has
 * no resources folders), which is how the launcher discovers it in any harness.
 */
@NotThreadSafe
public abstract class ASuiteRunLockListener implements LauncherSessionListener {

    private static final String LOCK_SUBDIR = "locks";
    private static final String WRITER_FILE_NAME = "writer";
    private static final String READER_FILE_PREFIX = "reader_";

    //the log line names the concrete listener, so a collision reads as that suite family being held
    private final Log log = new Log(getClass());
    private final String lockName = Reflections.getClassSimpleNameNonBlank(getClass());
    private final boolean createReferenceFile;

    //the guard this session actually holds (the writer file, or this process's reader marker), for the session's lifetime
    private FileChannelLock heldLock;
    private String lastRefusalReason;

    /**
     * A listener that does not say which role it runs is treated as the destructive one, so the default can only ever be
     * too strict, never too lenient.
     */
    protected ASuiteRunLockListener() {
        this(true);
    }

    protected ASuiteRunLockListener(final boolean createReferenceFile) {
        this.createReferenceFile = createReferenceFile;
    }

    @Override
    public void launcherSessionOpened(final LauncherSession session) {
        newLockDir().mkdirs();
        if (createReferenceFile) {
            openWriterSession();
        } else {
            openReaderSession();
        }
    }

    @Override
    public void launcherSessionClosed(final LauncherSession session) {
        final FileChannelLock heldLockCopy = heldLock;
        if (heldLockCopy != null) {
            heldLockCopy.close();
            heldLock = null;
        }
    }

    /**
     * The writer takes the exclusive writer file, so writers are serialised by the OS lock itself, and then looks at the
     * reader markers. The writer lock is kept even when it refuses: the run proceeds (warn posture), and while it runs
     * later readers have to be able to see it.
     */
    private void openWriterSession() {
        final File writerFile = newWriterFile();
        final FileChannelLock writerLock = new FileChannelLock(writerFile);
        if (!writerLock.tryLock()) {
            lastRefusalReason = "the writer guard [" + writerFile + "] is locked by another process ("
                    + writerLock.getLastTryLockFailureReason() + ")";
            //closing a lock this session never held removes nothing - FileChannelLock only unlinks a file it acquired
            writerLock.close();
            log.error(newRefusalMessage());
            return;
        }
        heldLock = writerLock;
        final String liveReaders = listLiveReaderMarkers();
        if (liveReaders != null) {
            lastRefusalReason = "live reader sessions in [" + newLockDir() + "]: " + liveReaders;
            log.error(newRefusalMessage());
        }
    }

    /**
     * The reader marks this process and then looks for a writer. Markers are per owner, so parallel readers never see
     * each other; the writer probe is a tryLock, so a writer that is mid-start sees this marker already locked.
     */
    private void openReaderSession() {
        final File readerFile = newReaderFile();
        final FileChannelLock readerLock = new FileChannelLock(readerFile);
        if (!readerLock.tryLock()) {
            lastRefusalReason = "this process already holds the reader marker [" + readerFile + "] ("
                    + readerLock.getLastTryLockFailureReason() + ")";
            readerLock.close();
            log.error(newRefusalMessage());
            return;
        }
        heldLock = readerLock;
        if (isWriterSessionLive()) {
            lastRefusalReason = "the writer guard [" + newWriterFile() + "] is locked by another process";
            log.error(newRefusalMessage());
        }
    }

    /**
     * True while some process holds the writer file locked. Probing it is also what clears a writer marker left by a
     * killed process: the probe can lock it, and closing an acquired probe unlinks it.
     */
    private boolean isWriterSessionLive() {
        try (FileChannelLock probe = new FileChannelLock(newWriterFile())) {
            return !probe.tryLock();
        }
    }

    /**
     * The names of the reader markers whose owner process is still holding them, or null if there is none. A marker that
     * this probe can lock belongs to a dead process (the kernel dropped its locks), so closing the probe removes it and
     * it is not counted - which is what keeps a crashed suite run from blocking every later writer.
     */
    private String listLiveReaderMarkers() {
        final File[] markers = newLockDir().listFiles((dir, name) -> name.startsWith(READER_FILE_PREFIX));
        if (markers == null || markers.length == 0) {
            return null;
        }
        Arrays.sort(markers);
        final List<String> live = new ArrayList<>();
        for (final File marker : markers) {
            if (isMarkerLockedByOtherProcess(marker)) {
                live.add(marker.getName());
            }
        }
        return live.isEmpty() ? null : String.join(", ", live);
    }

    private boolean isMarkerLockedByOtherProcess(final File marker) {
        try (FileChannelLock probe = new FileChannelLock(marker)) {
            if (probe.tryLock()) {
                //acquired => the owner is gone; close() unlinks the residue
                return false;
            }
            return true;
        }
    }

    /**
     * A refusal as it gets logged, so no module has its own copy of the wording.
     */
    private String newRefusalMessage() {
        return "another suite run holds the " + (createReferenceFile ? "writer" : "reader") + " guard for suite family ["
                + lockName + "]: " + lastRefusalReason + "; the suite runs share store directories and reference files,"
                + " so overlapping runs produce unrelated-looking failures across them (missing run reports, truncated"
                + " cold reads, mutated reference membership). Run the reference-creating run alone.";
    }

    /**
     * Whether this session actually holds its guard. A refusal is only logged, so without this the decision would be
     * invisible to anything but the log.
     */
    public boolean isSessionLockHeld() {
        final FileChannelLock heldLockCopy = heldLock;
        return heldLockCopy != null && heldLockCopy.isLocked();
    }

    /**
     * Why the last {@link #launcherSessionOpened} refused, or null if it did not.
     */
    public String getLastRefusalReason() {
        return lastRefusalReason;
    }

    /**
     * The identity half of this process's reader marker. Distinct per process by construction (pid@host plus a UUID, so
     * equal pids in different containers still differ); overridable so a test can put two readers side by side in one
     * jvm, which is otherwise a JDK-internal impossibility rather than a property of the guard.
     */
    protected String newReaderOwner() {
        return HeartbeatFileChannelLockRegistry.HEARTBEAT_OWNER;
    }

    private File newLockDir() {
        return new File(ContextProperties.getLogDirectory(), LOCK_SUBDIR + File.separatorChar + lockName);
    }

    private File newWriterFile() {
        return new File(newLockDir(), WRITER_FILE_NAME);
    }

    private File newReaderFile() {
        return new File(newLockDir(), READER_FILE_PREFIX + newReaderOwner());
    }

}
