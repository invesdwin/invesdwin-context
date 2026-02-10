package de.invesdwin.context.integration.compression;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.lang.string.description.TextDescription;
import de.invesdwin.util.streams.closeable.Closeables;
import de.invesdwin.util.streams.delegate.ADelegateInputStream;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

@NotThreadSafe
public class SevenZipDecompressingInputStream extends ADelegateInputStream {

    private static final AtomicInteger NEXT_ID = new AtomicInteger();

    private final SevenZipDecompressingInputStreamFinalizer finalizer;

    public SevenZipDecompressingInputStream(final TextDescription name, final InputStream in) {
        super(name);
        this.finalizer = new SevenZipDecompressingInputStreamFinalizer();
        registerCloseable(finalizer);

        finalizer.copyInputStream = in;
        final File tempFolder = getTempFolder();
        final int id = NEXT_ID.incrementAndGet();
        finalizer.inFile = new File(tempFolder, "file_" + id + "_in");
        finalizer.deleteInFile = true;
        finalizer.outFile = new File(tempFolder, "file_" + id + "_out");
    }

    public SevenZipDecompressingInputStream(final TextDescription name, final File file) {
        super(name);
        this.finalizer = new SevenZipDecompressingInputStreamFinalizer();
        registerCloseable(finalizer);

        final File tempFolder = getTempFolder();
        final int id = NEXT_ID.incrementAndGet();
        finalizer.inFile = file;
        finalizer.deleteInFile = false;
        finalizer.outFile = new File(tempFolder, "file_" + id + "_out");
    }

    private File getTempFolder() {
        final File tempFolder = new File(ContextProperties.TEMP_DIRECTORY,
                SevenZipDecompressingInputStream.class.getSimpleName());
        try {
            Files.forceMkdir(tempFolder);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return tempFolder;
    }

    @Override
    protected final InputStream newDelegate() {
        try {
            if (finalizer.copyInputStream != null) {
                Files.copyInputStreamToFile(finalizer.copyInputStream, finalizer.inFile);
                Closeables.closeQuietly(finalizer.copyInputStream);
                finalizer.copyInputStream = null;
            }
            final RandomAccessFile inRaf = new RandomAccessFile(finalizer.inFile, "r");
            final RandomAccessFileInStream inRafStream = new RandomAccessFileInStream(inRaf);
            final IInArchive inArchive = SevenZip.openInArchive(null, inRafStream);

            final RandomAccessFile outRaf = new RandomAccessFile(finalizer.outFile, "rw");
            final RandomAccessFileOutStream outRafStream = new RandomAccessFileOutStream(outRaf);
            final ISimpleInArchive simpleInterface = inArchive.getSimpleInterface();
            for (int i = 0; i < simpleInterface.getNumberOfItems(); i++) {
                final ISimpleInArchiveItem archiveItem = simpleInterface.getArchiveItem(i);
                if (archiveItem.isFolder()) {
                    continue;
                }
                final ExtractOperationResult result = archiveItem.extractSlow(outRafStream);
                if (result != ExtractOperationResult.OK) {
                    throw new IllegalStateException("Invalid result: " + result);
                }
                return new FileInputStream(finalizer.outFile);
            }
            throw new IllegalStateException(
                    "No " + ISimpleInArchiveItem.class.getSimpleName() + " found to extract from: " + finalizer.inFile);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        finalizer.close();
    }

    private static final class SevenZipDecompressingInputStreamFinalizer extends AFinalizer {

        private InputStream copyInputStream;
        private File inFile;
        private boolean deleteInFile;
        private File outFile;

        private SevenZipDecompressingInputStreamFinalizer() {}

        @Override
        protected void clean() {
            if (copyInputStream != null) {
                Closeables.closeQuietly(copyInputStream);
                copyInputStream = null;
            }
            if (inFile != null && deleteInFile) {
                Files.deleteQuietly(inFile);
                inFile = null;
            }
            if (outFile != null) {
                Files.deleteQuietly(outFile);
                outFile = null;
            }
        }

        @Override
        protected boolean isCleaned() {
            return inFile == null;
        }

        @Override
        public boolean isThreadLocal() {
            return true;
        }

    }

}