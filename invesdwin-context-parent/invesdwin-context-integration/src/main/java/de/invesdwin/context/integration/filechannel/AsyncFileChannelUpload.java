package de.invesdwin.context.integration.filechannel;

import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.retry.RetryDisabledRuntimeException;
import de.invesdwin.context.integration.retry.RetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.task.ARetryingRunnable;
import de.invesdwin.context.integration.retry.task.RetryOriginator;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;

@NotThreadSafe
public class AsyncFileChannelUpload implements Runnable {

    public static final String FINISHED_FILENAME_SUFFIX = ".finished";
    public static final int MAX_TRIES = 3;
    public static final int MAX_PARALLEL_UPLOADS = 2;

    private static final WrappedExecutorService EXECUTOR = Executors
            .newFixedThreadPool(AsyncFileChannelUpload.class.getSimpleName(), MAX_PARALLEL_UPLOADS);

    private final IFileChannel<?> channel;
    private final String channelFileName;
    private final File localTempFile;
    private int tries = 0;

    public AsyncFileChannelUpload(final IFileChannel<?> channel, final File localTempFile) {
        Assertions.checkNotNull(channel);
        this.channel = channel;
        this.localTempFile = localTempFile;
        this.channelFileName = channel.getFilename();
        Assertions.checkNotNull(channelFileName);
    }

    @Override
    public void run() {
        final Runnable retry = new ARetryingRunnable(
                new RetryOriginator(AsyncFileChannelUpload.class, "run", channel, localTempFile)) {
            @Override
            protected void runRetryable() throws Exception {
                cleanupForUpload();
                try {
                    EXECUTOR.awaitPendingCount(MAX_PARALLEL_UPLOADS);
                } catch (final InterruptedException e) {
                    channel.close();
                    throw new RuntimeException(e);
                } catch (final Throwable t) {
                    throw handleRetry(t);
                }
                uploadAsync();
            }

        };
        retry.run();
    }

    private void cleanupForUpload() {
        if (!channel.isConnected()) {
            channel.connect();
        }
        channel.setFilename(channelFileName + FINISHED_FILENAME_SUFFIX);
        channel.delete();
        channel.setFilename(channelFileName);
        channel.delete();
    }

    private void uploadAsync() {
        EXECUTOR.execute(new ARetryingRunnable(
                new RetryOriginator(AsyncFileChannelUpload.class, "upload", channel, localTempFile)) {
            @Override
            protected void runRetryable() throws Exception {
                try {
                    cleanupForUpload();
                    upload();
                    deleteInputFileAutomatically();
                    closeChannelAutomatically();
                } catch (final Throwable t) {
                    throw handleRetry(t);
                }
            }

        });
    }

    protected void upload() {
        channel.setFilename(channelFileName);
        channel.upload(localTempFile);
        channel.setFilename(channelFileName + FINISHED_FILENAME_SUFFIX);
        channel.upload(channel.getEmptyFileContent());
        channel.setFilename(channelFileName);
    }

    private RuntimeException handleRetry(final Throwable t) {
        try {
            channel.close();
        } catch (final IOException e) {
            //ignore
        }
        tries++;
        if (tries >= MAX_TRIES) {
            return new RetryDisabledRuntimeException(
                    "Aborting upload retry after " + tries + " tries because: " + t.toString(), t);
        } else {
            return new RetryLaterRuntimeException(t);
        }
    }

    protected void deleteInputFileAutomatically() {
        localTempFile.delete();
    }

    protected void closeChannelAutomatically() {
        try {
            channel.close();
        } catch (final IOException e) {
            //ignore
        }
    }

}
