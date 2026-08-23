package de.invesdwin.context.integration.filechannel;

import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.util.error.InterruptedRuntimeException;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.millis.FDateMillis;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public final class FileChannels {

    private FileChannels() {
        // utility class
    }

    public static <T> T downloadTimeout(final IFileChannel channel, final Supplier<T> downloadF)
            throws TimeoutException {
        return downloadTimeout(channel, downloadF, ContextProperties.DEFAULT_NETWORK_TIMEOUT);
    }

    public static <T> T downloadTimeout(final IFileChannel channel, final Supplier<T> downloadF,
            final Duration timeout) throws TimeoutException {
        final long startTime = FDateMillis.nowMillis();
        while (true) {
            if (channel.exists()) {
                return downloadF.get();
            }
            if (timeout != null) {
                if (timeout.isLessThanMillis(FDateMillis.nowMillis() - startTime)) {
                    throw new TimeoutException("File does not exist after waiting for " + timeout);
                }
            }
            try {
                FTimeUnit.MILLISECONDS.sleep(250);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InterruptedRuntimeException("Thread interrupted while waiting for file to exist", e);
            }
        }
    };

}
