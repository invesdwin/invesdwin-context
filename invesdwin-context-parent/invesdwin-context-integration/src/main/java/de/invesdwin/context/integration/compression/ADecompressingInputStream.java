package de.invesdwin.context.integration.compression;

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
import de.invesdwin.util.lang.description.TextDescription;
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

    public ADecompressingInputStream(final TextDescription name) {
        super(name);
    }

    private static InputStream wrap(final InputStream in) {
        final InputStream markableIn;
        if (!in.markSupported()) {
            markableIn = new BufferedInputStream(in);
        } else {
            markableIn = in;
        }
        try {
            final ArchiveInputStream archiveInputStream = new ArchiveStreamFactory()
                    .createArchiveInputStream(markableIn);
            return archiveInputStream;
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
        if (b == -1 && getDelegate() instanceof ArchiveInputStream) {
            final ArchiveInputStream archiveIn = (ArchiveInputStream) getDelegate();
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
    public int read(final byte[] b) throws IOException {
        int read = super.read(b);
        if (read <= 0 && b.length > 0 && getDelegate() instanceof ArchiveInputStream) {
            final ArchiveInputStream archiveIn = (ArchiveInputStream) getDelegate();
            if (!oneArchiveEntryAlreadyExtraced) {
                ArchiveEntry entry = archiveIn.getNextEntry();
                while (entry != null && (entry.isDirectory() || Strings.isBlank(entry.getName()))) {
                    entry = archiveIn.getNextEntry();
                }
                if (entry != null) {
                    oneArchiveEntryAlreadyExtraced = true;
                    read = super.read(b);
                }
            }
        }
        return read;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        int read = super.read(b, off, len);
        if (read <= 0 && off < b.length && len > 0 && getDelegate() instanceof ArchiveInputStream) {
            final ArchiveInputStream archiveIn = (ArchiveInputStream) getDelegate();
            if (!oneArchiveEntryAlreadyExtraced) {
                ArchiveEntry entry = archiveIn.getNextEntry();
                while (entry != null && (entry.isDirectory() || Strings.isBlank(entry.getName()))) {
                    entry = archiveIn.getNextEntry();
                }
                if (entry != null) {
                    oneArchiveEntryAlreadyExtraced = true;
                    read = super.read(b, off, len);
                }
            }
        }
        return read;
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        byte[] readAllBytes = super.readAllBytes();
        if ((readAllBytes == null || readAllBytes.length == 0) && getDelegate() instanceof ArchiveInputStream) {
            final ArchiveInputStream archiveIn = (ArchiveInputStream) getDelegate();
            if (!oneArchiveEntryAlreadyExtraced) {
                ArchiveEntry entry = archiveIn.getNextEntry();
                while (entry != null && (entry.isDirectory() || Strings.isBlank(entry.getName()))) {
                    entry = archiveIn.getNextEntry();
                }
                if (entry != null) {
                    oneArchiveEntryAlreadyExtraced = true;
                    readAllBytes = super.readAllBytes();
                }
            }
        }
        return readAllBytes;
    }

    @Override
    public int readNBytes(final byte[] b, final int off, final int len) throws IOException {
        int readNBytes = super.readNBytes(b, off, len);
        if (readNBytes <= 0 && off < b.length && len > 0 && getDelegate() instanceof ArchiveInputStream) {
            final ArchiveInputStream archiveIn = (ArchiveInputStream) getDelegate();
            if (!oneArchiveEntryAlreadyExtraced) {
                ArchiveEntry entry = archiveIn.getNextEntry();
                while (entry != null && (entry.isDirectory() || Strings.isBlank(entry.getName()))) {
                    entry = archiveIn.getNextEntry();
                }
                if (entry != null) {
                    oneArchiveEntryAlreadyExtraced = true;
                    readNBytes = super.readNBytes(b, off, len);
                }
            }
        }
        return readNBytes;
    }

    @Override
    public byte[] readNBytes(final int len) throws IOException {
        byte[] readNBytes = super.readNBytes(len);
        if ((readNBytes == null || readNBytes.length == 0) && len > 0 && getDelegate() instanceof ArchiveInputStream) {
            final ArchiveInputStream archiveIn = (ArchiveInputStream) getDelegate();
            if (!oneArchiveEntryAlreadyExtraced) {
                ArchiveEntry entry = archiveIn.getNextEntry();
                while (entry != null && (entry.isDirectory() || Strings.isBlank(entry.getName()))) {
                    entry = archiveIn.getNextEntry();
                }
                if (entry != null) {
                    oneArchiveEntryAlreadyExtraced = true;
                    readNBytes = super.readNBytes(len);
                }
            }
        }
        return readNBytes;
    }

    @Override
    protected boolean shouldCloseOnMinus1Read() {
        return false;
    }
}
