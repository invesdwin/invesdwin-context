package de.invesdwin.context.test;

import java.io.File;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.log.Log;
import de.invesdwin.util.concurrent.lock.file.FileChannelLock;
import de.invesdwin.util.lang.reflection.Reflections;

/**
 * Makes "the suites of one module run serially" observable at the moment it is broken: a JUnit launcher session takes a
 * {@link FileChannelLock} for its whole lifetime, so a second suite run logs a named collision instead of showing up
 * later as unrelated failures spread across the shared store directories. The build does not enforce that convention -
 * the parent pom pins forkCount=1 and leaves the threadCount block commented out, which serialises classes inside one
 * fork and says nothing about a second suite process - and a file lock covers every launcher (surefire, IDE, hand-run),
 * where a shell {@code flock} would not, since {@code flock(2)} and Java file locks do not observe each other.
 *
 * <p>
 * The lock file is {@code <logDir>/locks/<simpleName of the listener>.lock}. It is named for the listener, so the log
 * line and the file both say which suite family is held, and it sits in a subfolder of its own because
 * {@link FileChannelLock} unlinks its own file on unlock and has to live where a suite run's own cleanup does not sweep
 * it (the cache directory was rejected for exactly that reason: a lock removed under a live holder silently un-guards
 * the run). The subfolder is created here because FileChannelLock does not create missing parents, and a path it cannot
 * open refuses with an io reason that would read like a collision. The log directory is what the generated
 * {@code .gitignore} already excludes, so a lock never dirties a checkout. A refusal is logged and the run proceeds,
 * because suites do get run side by side for work that touches none of the contended stores.
 *
 * <p>
 * One listener object owns exactly one lock and there is no static state: the launcher instantiates one listener per
 * launcher session, so the object lives exactly as long as the lock has to be held, and two sessions in one jvm get two
 * objects that really do contend (measured refusal reason {@code OVERLAPPING_FILE_LOCK_BY_OTHER_THREAD}).
 *
 * <p>
 * A module registers a subclass as a service under {@code META-INF/services} next to its test sources (this codebase has
 * no resources folders), which is how the launcher discovers it in any harness.
 */
@NotThreadSafe
public abstract class ASuiteRunLockListener implements LauncherSessionListener {

    private static final String LOCK_SUBDIR = "locks";
    private static final String LOCK_FILE_SUFFIX = ".lock";

    //the log line names the concrete listener, so a collision reads as that suite family being held
    private final Log log = new Log(getClass());
    private final String lockName = Reflections.getClassSimpleNameNonBlank(getClass());

    //one lock per launcher session, which is exactly how long the guard has to be held
    private FileChannelLock lock;

    @Override
    public void launcherSessionOpened(final LauncherSession session) {
        lock = newSuiteRunLock();
        if (lock.tryLock()) {
            return;
        }
        log.error(newRefusalMessage());
    }

    @Override
    public void launcherSessionClosed(final LauncherSession session) {
        lock.close();
    }

    /**
     * A refusal as it gets logged, so no module has its own copy of the wording.
     */
    private String newRefusalMessage() {
        return "another suite run holds the suite lock [" + newLockFile() + "] ("
                + lock.getLastTryLockFailureReason() + "); the suite runs share store directories and reference files,"
                + " so overlapping runs produce unrelated-looking failures across them (missing run reports, truncated"
                + " cold reads, mutated reference membership). Run them serially.";
    }

    private FileChannelLock newSuiteRunLock() {
        final File lockFile = newLockFile();
        //the locks subfolder appears the first time a suite run needs it
        lockFile.getParentFile().mkdirs();
        return new FileChannelLock(lockFile);
    }

    private File newLockFile() {
        return new File(new File(ContextProperties.getLogDirectory(), LOCK_SUBDIR), lockName + LOCK_FILE_SUFFIX);
    }

}
