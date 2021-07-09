package de.invesdwin.context.integration.filechannel;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.retry.RetryDisabledRuntimeException;
import de.invesdwin.context.integration.retry.RetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.task.ARetryCallable;
import de.invesdwin.context.integration.retry.task.RetryOriginator;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.future.Futures;
import de.invesdwin.util.lang.description.TextDescription;
import de.invesdwin.util.streams.ADelegateInputStream;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class AsyncFileChannelDownload implements Callable<InputStream> {

    private static final int MAX_PARALLEL_DOWNLOADS = 50;

    private static final WrappedExecutorService EXECUTOR = Executors
            .newFixedThreadPool(AsyncFileChannelDownload.class.getSimpleName(), MAX_PARALLEL_DOWNLOADS);

    private final IFileChannel<?> channel;
    private final String channelFileName;
    private final Duration downloadTimeout;
    private Instant timeoutStart;
    private FDate lastFileModified;
    private Long lastFileSize;
    private int tries = 0;

    public AsyncFileChannelDownload(final IFileChannel<?> channel, final Duration downloadTimeout) {
        Assertions.checkNotNull(channel);
        this.channel = channel;
        this.channelFileName = channel.getFilename();
        Assertions.checkNotNull(channelFileName);
        this.downloadTimeout = downloadTimeout;
    }

    @Override
    public InputStream call() throws Exception {
        final Callable<InputStream> downloadAsync = new ARetryCallable<InputStream>(
                new RetryOriginator(AsyncFileChannelDownload.class, "call", channel)) {
            @Override
            protected InputStream callRetry() {
                try {
                    if (!channel.isConnected()) {
                        channel.connect();
                    }
                    timeoutStart = new Instant();
                    while (shouldWaitForFinishedFile()) {
                        FTimeUnit.SECONDS.sleep(1);
                        if (timeoutStart.isGreaterThan(downloadTimeout)) {
                            throw new TimeoutException(
                                    "Timeout of " + downloadTimeout + " exceeded while downloading: " + channel);
                        }
                    }
                    channel.setFilename(channelFileName);
                    final InputStream input = download();
                    return new ADelegateInputStream(new TextDescription("%s[%s]: call()",
                            AsyncFileChannelDownload.class.getSimpleName(), channel)) {

                        @Override
                        protected InputStream newDelegate() {
                            return input;
                        }

                        @Override
                        public void close() throws IOException {
                            super.close();
                            deleteChannelFileAutomatically();
                            closeChannelAutomatically();
                        }

                    };
                } catch (InterruptedException | TimeoutException e) {
                    try {
                        channel.close();
                    } catch (final IOException e1) {
                        //ignore
                    }
                    throw new RetryDisabledRuntimeException(e);
                } catch (final Throwable t) {
                    throw handleRetry(t);
                }
            }

        };
        EXECUTOR.awaitPendingCountFull();
        return Futures.submitAndGet(EXECUTOR, downloadAsync);
    }

    protected InputStream download() {
        return channel.downloadInputStream();
    }

    private RuntimeException handleRetry(final Throwable t) {
        try {
            channel.close();
        } catch (final IOException e) {
            //ignore
        }
        tries++;
        if (tries >= AsyncFileChannelUpload.MAX_TRIES) {
            return new RetryDisabledRuntimeException(
                    "Aborting upload retry after " + tries + " tries because: " + t.toString(), t);
        } else {
            return new RetryLaterRuntimeException(t);
        }
    }

    protected void deleteChannelFileAutomatically() {
        channel.setFilename(channelFileName + AsyncFileChannelUpload.FINISHED_FILENAME_SUFFIX);
        channel.delete();
        channel.setFilename(channelFileName);
        channel.delete();
    }

    protected void closeChannelAutomatically() {
        try {
            channel.close();
        } catch (final IOException e) {
            //ignore
        }
    }

    private boolean shouldWaitForFinishedFile() {
        channel.setFilename(channelFileName + AsyncFileChannelUpload.FINISHED_FILENAME_SUFFIX);
        if (channel.exists()) {
            return false;
        }

        channel.setFilename(channelFileName);
        if (channel.exists()) {
            final FDate fileModified = channel.modified();
            if (lastFileModified == null || lastFileModified.isBefore(fileModified)) {
                lastFileModified = fileModified;
                //reset timeout since upload is still in progress
                timeoutStart = new Instant();
                return true;
            }
            final long fileSize = channel.size();
            if (lastFileSize == null || lastFileSize != fileSize) {
                lastFileSize = fileSize;
                //reset timeout since upload is still in progress
                timeoutStart = new Instant();
                return true;
            }
        }

        return true;
    }

}
