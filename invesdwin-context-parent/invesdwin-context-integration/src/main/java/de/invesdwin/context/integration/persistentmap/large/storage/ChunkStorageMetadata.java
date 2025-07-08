package de.invesdwin.context.integration.persistentmap.large.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummary;
import de.invesdwin.util.time.date.FDate;

@NotThreadSafe
public class ChunkStorageMetadata {

    private final File logFile;

    public ChunkStorageMetadata(final File dataDirectory) {
        this.logFile = new File(dataDirectory, "memory.log");
    }

    public void setSummary(final ChunkSummary summary) {
        final StringBuilder logEntry = new StringBuilder();
        logEntry.append("\n");
        logEntry.append(ChunkSummary.class.getSimpleName());
        logEntry.append("\nREAL_TIME=");
        logEntry.append(new FDate());
        logEntry.append("\nMEMORY_RESOURCE_URI=");
        logEntry.append(summary.getMemoryResourceUri());
        logEntry.append("\nPRECEDING_MEMORY_OFFSET=");
        logEntry.append(summary.getPrecedingMemoryOffset());
        logEntry.append("\nMEMORY_OFFSET=");
        logEntry.append(summary.getMemoryOffset());
        logEntry.append("\nMEMORY_LENGTH=");
        logEntry.append(summary.getMemoryLength());
        logEntry.append("\nHASH_CODE=");
        logEntry.append(summary.hashCode());
        logEntry.append("\n");
        try (FileOutputStream out = new FileOutputStream(logFile, true)) {
            out.write(logEntry.toString().getBytes());
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
