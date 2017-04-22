package de.invesdwin.context.integration.streams;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.streams.ADelegateInputStream;

/**
 * An InputStream that acts as a wrapper to commons-compress to decompress diverse archives and compressions. Examples
 * are ZIP, GZIP, BZIP2, JAR, TAR, etc. If the archive contains more than one file, only the first gets extracted.
 * 
 * If there is no archive behind it, the file gets downloaded as it is.
 * 
 * @author subes
 * 
 */
@NotThreadSafe
public abstract class ADecompressingInputStream extends ADelegateInputStream {

    private boolean oneArchiveEntryAlreadyExtraced = false;

    private static InputStream wrap(final InputStream in) {
        final InputStream markableIn;
        if (!in.markSupported()) {
            markableIn = new BufferedInputStream(in);
        } else {
            markableIn = in;
        }
        try {
            return new ArchiveStreamFactory().createArchiveInputStream(markableIn);
        } catch (final ArchiveException e) {
            try {
                return new CompressorStreamFactory().createCompressorInputStream(markableIn);
            } catch (final CompressorException e1) {
                //Maybe this is not an archive, thus we download it as it is...
                return in;
            }
        }
    }

    @Override
    protected final InputStream newDelegate() {
        return wrap(innerNewDelegate());
    }

    protected abstract InputStream innerNewDelegate();

    @Override
    public int read() throws IOException {
        int b = super.read();
        final InputStream delegate = getDelegate();
        if (b == -1 && delegate instanceof ArchiveInputStream) {
            final ArchiveInputStream archiveIn = (ArchiveInputStream) delegate;
            if (!oneArchiveEntryAlreadyExtraced) {
                ArchiveEntry entry = archiveIn.getNextEntry();
                while (entry != null && (entry.isDirectory() || Strings.isBlank(entry.getName()))) {
                    entry = archiveIn.getNextEntry();
                }
                if (entry != null) {
                    oneArchiveEntryAlreadyExtraced = true;
                    b = super.read();
                }
            }
        }
        return b;
    }

    @Override
    protected boolean shouldCloseOnMinus1Read() {
        return false;
    }
}
