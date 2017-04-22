package de.invesdwin.context.integration.network;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.FileUtils;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FDates;
import de.invesdwin.util.time.fdate.FTimeUnit;

@NotThreadSafe
public class DailyDownloadCache {

    private static final Duration DAILY_REFRESH_DURATION = new Duration(1, FTimeUnit.DAYS);
    private static final File FOLDER = new File(ContextProperties.getHomeDirectory(),
            DailyDownloadCache.class.getSimpleName());

    public String download(final String name, final Callable<String> request) throws Exception {
        try {
            final File file = newFile(name);
            if (!file.exists()
                    || !ContextProperties.IS_TEST_ENVIRONMENT && shouldUpdate(FDate.valueOf(file.lastModified()))) {
                final String content = request.call();
                FileUtils.writeStringToFile(file, content);
                return content;
            } else {
                return FileUtils.readFileToString(file);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File newFile(final String name) {
        return new File(FOLDER, name.replace(":", "_") + ".txt");
    }

    public static void delete(final String name) {
        final File file = newFile(name);
        FileUtils.deleteQuietly(file);
    }

    public boolean shouldUpdate(final FDate lastRequestTime) {
        return new Duration(lastRequestTime).isGreaterThan(DAILY_REFRESH_DURATION)
                || !FDates.isSameDay(lastRequestTime, new FDate());
    }
}
