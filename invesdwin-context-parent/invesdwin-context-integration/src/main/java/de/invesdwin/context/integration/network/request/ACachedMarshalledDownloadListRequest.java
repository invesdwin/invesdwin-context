package de.invesdwin.context.integration.network.request;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.concurrent.NotThreadSafe;

import com.fasterxml.jackson.core.type.TypeReference;

import de.invesdwin.context.integration.marshaller.MarshallerJsonJackson;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.concurrent.lock.FileChannelLock;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.string.Charsets;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public abstract class ACachedMarshalledDownloadListRequest<E> implements Callable<List<E>> {

    protected final TypeReference<List<E>> type;

    public ACachedMarshalledDownloadListRequest(final TypeReference<List<E>> type) {
        this.type = type;
    }

    @Override
    public List<E> call() {
        try {
            final File file = newFile();
            if (!file.exists() || isExpired(file)) {
                Files.forceMkdirParent(file);
                try (FileChannelLock lock = new FileChannelLock(new File(file.getAbsolutePath() + ".lock")) {
                    @Override
                    protected boolean isThreadLockEnabled() {
                        return true;
                    }
                }) {
                    if (!file.exists() || isExpired(file)) {
                        final List<E> downloaded = download();
                        if (downloaded.isEmpty()) {
                            Files.writeStringToFile(file, "", Charsets.DEFAULT);
                        } else {
                            final String json = toMarshalled(downloaded);
                            Files.writeStringToFile(file, json, Charsets.DEFAULT);
                        }
                        return downloaded;
                    }
                }
            }
            if (file.length() == 0) {
                return Collections.emptyList();
            } else {
                try {
                    final String json = Files.readFileToString(file, Charsets.DEFAULT);
                    return fromMarshalled(json);
                } catch (final Throwable t) {
                    Err.process(new RuntimeException("Resetting and retrying: " + t.toString(), t));
                    Files.delete(file);
                    return call();
                }
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retry empty downloads once a day (might be a permission problem)
     */
    protected boolean isExpired(final File file) {
        return file.length() == 0 && Duration.ONE_DAY.isLessThan(System.currentTimeMillis() - file.lastModified(),
                FTimeUnit.MILLISECONDS);
    }

    protected String toMarshalled(final List<E> downloaded) {
        return MarshallerJsonJackson.toJsonMultiline(downloaded);
    }

    protected List<E> fromMarshalled(final String json) {
        return MarshallerJsonJackson.fromJson(json, type);
    }

    protected abstract List<E> download();

    protected abstract File newFile();

}
