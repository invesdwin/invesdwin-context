package de.invesdwin.context.integration.network.request;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.integration.streams.compressor.lz4.LZ4Streams;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.streams.pool.APooledOutputStream;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class DailyDownloadCache {

    private static final File FOLDER = new File(ContextProperties.getHomeDataDirectory(),
            DailyDownloadCache.class.getSimpleName());
    private final DailyDownloadInterval updateInterval;

    public DailyDownloadCache() {
        this.updateInterval = DailyDownloadInterval.DAILY;
    }

    public DailyDownloadCache(final DailyDownloadInterval updateInterval) {
        this.updateInterval = updateInterval;
    }

    public String downloadString(final String name, final Callable<String> request) throws Exception {
        return downloadString(name, request, new FDate());
    }

    public String downloadString(final String name, final Callable<String> request, final FDate now) throws Exception {
        try {
            final File file = newFile(name);
            if (shouldUpdate(file, now)) {
                final String content = request.call();
                Files.writeStringToFile(file, content, Charset.defaultCharset());
                return content;
            } else {
                return Files.readFileToString(file, Charset.defaultCharset());
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream downloadStream(final String name, final Consumer<OutputStream> request) throws Exception {
        return downloadStream(name, request, new FDate());
    }

    public InputStream downloadStream(final String name, final Consumer<OutputStream> request, final FDate now)
            throws Exception {
        try {
            final File file = newFile(name);
            if (shouldUpdate(file, now)) {
                Files.forceMkdirParent(file);
                final File tmpFile = new File(file.getAbsolutePath() + ".tmp");
                try (APooledOutputStream fos = LZ4Streams.newLargeHighLZ4OutputStream(new FileOutputStream(tmpFile))) {
                    request.accept(fos.asNonClosing());
                }
                Files.moveFile(tmpFile, file);
            }
            return LZ4Streams.newDefaultLZ4InputStream(new FileInputStream(file));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean shouldUpdate(final String name, final FDate now) {
        return shouldUpdate(newFile(name), now);
    }

    private boolean shouldUpdate(final File file, final FDate now) {
        if (!file.exists()) {
            return true;
        }
        return !ContextProperties.IS_TEST_ENVIRONMENT && shouldUpdate(FDate.valueOf(file.lastModified()), now);
    }

    public static File newFile(final String name) {
        return new File(FOLDER, Files.normalizePath(name));
    }

    public static void delete(final String name) {
        final File file = newFile(name);
        Files.deleteQuietly(file);
    }

    public boolean shouldUpdate(final FDate lastRequestTime, final FDate now) {
        if (lastRequestTime.isAfterOrEqualToNotNullSafe(now)) {
            return false;
        }
        return new Duration(lastRequestTime, now).isGreaterThan(updateInterval.getDuration())
                || !updateInterval.isSameInterval(lastRequestTime, now);
    }
}
