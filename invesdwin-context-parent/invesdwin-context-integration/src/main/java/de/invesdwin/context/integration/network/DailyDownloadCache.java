package de.invesdwin.context.integration.network;

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
import de.invesdwin.context.integration.streams.LZ4Streams;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FDates;
import de.invesdwin.util.time.fdate.FTimeUnit;

@NotThreadSafe
public class DailyDownloadCache {

    private static final Duration DAILY_REFRESH_DURATION = new Duration(1, FTimeUnit.DAYS);
    private static final File FOLDER = new File(ContextProperties.getHomeDirectory(),
            DailyDownloadCache.class.getSimpleName());

    public String downloadString(final String name, final Callable<String> request) throws Exception {
        try {
            final File file = newFile(name);
            if (shouldUpdate(file)) {
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
        try {
            final File file = newFile(name);
            if (shouldUpdate(file)) {
                try (OutputStream fos = LZ4Streams.newLargeHighLZ4OutputStream(new FileOutputStream(file))) {
                    request.accept(fos);
                }
            }
            return LZ4Streams.newDefaultLZ4InputStream(new FileInputStream(file));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean shouldUpdate(final String name) {
        return shouldUpdate(newFile(name));
    }

    private boolean shouldUpdate(final File file) {
        return !file.exists()
                || !ContextProperties.IS_TEST_ENVIRONMENT && shouldUpdate(FDate.valueOf(file.lastModified()));
    }

    public static File newFile(final String name) {
        return new File(FOLDER, Files.normalizePath(name));
    }

    public static void delete(final String name) {
        final File file = newFile(name);
        Files.deleteQuietly(file);
    }

    public boolean shouldUpdate(final FDate lastRequestTime) {
        return new Duration(lastRequestTime).isGreaterThan(DAILY_REFRESH_DURATION)
                || !FDates.isSameJulianDay(lastRequestTime, new FDate());
    }
}
