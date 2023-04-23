package de.invesdwin.context.beans.init.platform.util;

import java.io.File;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.instrument.DynamicInstrumentationProperties;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.FileChannelLock;
import de.invesdwin.util.lang.Files;

@Immutable
public final class TempDirectoryLockConfigurerer {

    public static final String TEMP_DIRECTORY_LOCK_FILE_NAME = "running.lock";

    private TempDirectoryLockConfigurerer() {}

    public static void deleteObsoleteTempDirectories(final File tempDirectory) {
        final File tempDirectoryParent = tempDirectory.getParentFile();
        if (!tempDirectoryParent.getName().equals(DynamicInstrumentationProperties.USER_TEMP_DIRECTORY_NAME)) {
            //failsafe so that we don't delete the wrong directory siblings
            return;
        }
        final WrappedExecutorService deleteObsoleteTempFilesExecutor = Executors
                .newFixedThreadPool("deleteObsoleteTempFilesExecutor", 1);
        deleteObsoleteTempFilesExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final File[] children = tempDirectoryParent.listFiles();
                if (children != null) {
                    for (int i = 0; i < children.length; i++) {
                        final File child = children[i];
                        if (isObsoleteTempDirectory(child)) {
                            Files.deleteNative(child);
                        }
                    }
                }
            }

            private boolean isObsoleteTempDirectory(final File child) {
                if (!child.isDirectory()) {
                    //delete any files there, which should actually not be there
                    return true;
                }
                if (child.getName().equals(tempDirectory.getName())) {
                    //this is the current process's temp folder, don't delete that
                    return false;
                }
                final File childLockFile = new File(child, TEMP_DIRECTORY_LOCK_FILE_NAME);
                if (!childLockFile.exists()) {
                    //lock file does not exist, thus this must be an incomplete delete on shutdown
                    return true;
                }
                @SuppressWarnings("resource")
                final FileChannelLock childLock = new FileChannelLock(childLockFile);
                if (childLock.tryLock()) {
                    childLock.unlock();
                    //process is not holding the lock anymore, thus we can delete it
                    return true;
                } else {
                    //process is still running, don't touch this folder
                    return false;
                }
            }
        });
        deleteObsoleteTempFilesExecutor.shutdown();
    }

}
