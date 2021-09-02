package de.invesdwin.context.integration.streams.compressor.lz4.output;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Checksum;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.ByteBuffers;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.util.SafeUtils;

@NotThreadSafe
public class ReusableLZ4BlockOutputStream extends OutputStream {

    public static final byte[] MAGIC = new byte[] { 'L', 'Z', '4', 'B', 'l', 'o', 'c', 'k' };
    public static final int MAGIC_LENGTH = MAGIC.length;

    public static final int HEADER_LENGTH = MAGIC_LENGTH // magic bytes
            + 1 // token
            + 4 // compressed length
            + 4 // decompressed length
            + 4; // checksum

    public static final int COMPRESSION_LEVEL_BASE = 10;
    public static final int MIN_BLOCK_SIZE = 64;
    public static final int MAX_BLOCK_SIZE = 1 << (COMPRESSION_LEVEL_BASE + 0x0F);

    public static final int COMPRESSION_METHOD_RAW = 0x10;
    public static final int COMPRESSION_METHOD_LZ4 = 0x20;

    public static final int DEFAULT_SEED = 0x9747b28c;

    private final int blockSize;
    private final int compressionLevel;
    private final LZ4Compressor compressor;
    private final Checksum checksum;
    private final byte[] buffer;
    private final byte[] compressedBuffer;
    private boolean finished;
    private int o;
    private OutputStream out;

    /**
     * Creates a new {@link OutputStream} with configurable block size. Large blocks require more memory at compression
     * and decompression time but should improve the compression ratio.
     *
     * @param out
     *            the {@link OutputStream} to feed
     * @param blockSize
     *            the maximum number of bytes to try to compress at once, must be &gt;= 64 and &lt;= 32 M
     * @param compressor
     *            the {@link LZ4Compressor} instance to use to compress data
     * @param checksum
     *            the {@link Checksum} instance to use to check data for integrity.
     */
    public ReusableLZ4BlockOutputStream(final int blockSize, final LZ4Compressor compressor, final Checksum checksum) {
        this.blockSize = blockSize;
        this.compressor = compressor;
        this.checksum = checksum;
        this.compressionLevel = compressionLevel(blockSize);
        this.buffer = ByteBuffers.allocateByteArray(blockSize);
        final int compressedBlockSize = HEADER_LENGTH + compressor.maxCompressedLength(blockSize);
        this.compressedBuffer = ByteBuffers.allocateByteArray(compressedBlockSize);
        System.arraycopy(MAGIC, 0, compressedBuffer, 0, MAGIC_LENGTH);
    }

    public ReusableLZ4BlockOutputStream init(final OutputStream out) {
        this.out = out;
        o = 0;
        finished = false;
        return this;
    }

    private static int compressionLevel(final int blockSize) {
        if (blockSize < MIN_BLOCK_SIZE) {
            throw new IllegalArgumentException("blockSize must be >= " + MIN_BLOCK_SIZE + ", got " + blockSize);
        } else if (blockSize > MAX_BLOCK_SIZE) {
            throw new IllegalArgumentException("blockSize must be <= " + MAX_BLOCK_SIZE + ", got " + blockSize);
        }
        int compressionLevel = 32 - Integer.numberOfLeadingZeros(blockSize - 1); // ceil of log2
        assert (1 << compressionLevel) >= blockSize;
        assert blockSize * 2 > (1 << compressionLevel);
        compressionLevel = Math.max(0, compressionLevel - COMPRESSION_LEVEL_BASE);
        assert compressionLevel >= 0 && compressionLevel <= 0x0F;
        return compressionLevel;
    }

    private void ensureNotFinished() {
        if (finished) {
            throw new IllegalStateException("This stream is already closed");
        }
    }

    @Override
    public void write(final int b) throws IOException {
        ensureNotFinished();
        if (o == blockSize) {
            flushBufferedData();
        }
        buffer[o++] = (byte) b;
    }

    //CHECKSTYLE:OFF
    @Override
    public void write(final byte[] b, int off, int len) throws IOException {
        SafeUtils.checkRange(b, off, len);
        ensureNotFinished();

        while (o + len > blockSize) {
            final int l = blockSize - o;
            System.arraycopy(b, off, buffer, o, blockSize - o);
            o = blockSize;
            flushBufferedData();
            off += l;
            len -= l;
        }
        System.arraycopy(b, off, buffer, o, len);
        o += len;
    }
    //CHECKSTYLE:ON

    @Override
    public void write(final byte[] b) throws IOException {
        ensureNotFinished();
        write(b, 0, b.length);
    }

    @Override
    public void close() throws IOException {
        if (!finished) {
            finish();
        }
        if (out != null) {
            out.close();
            out = null;
        }
    }

    private void flushBufferedData() throws IOException {
        if (o == 0) {
            return;
        }
        checksum.reset();
        checksum.update(buffer, 0, o);
        final int check = (int) checksum.getValue();
        int compressedLength = compressor.compress(buffer, 0, o, compressedBuffer, HEADER_LENGTH);
        final int compressMethod;
        if (compressedLength >= o) {
            compressMethod = COMPRESSION_METHOD_RAW;
            compressedLength = o;
            System.arraycopy(buffer, 0, compressedBuffer, HEADER_LENGTH, o);
        } else {
            compressMethod = COMPRESSION_METHOD_LZ4;
        }

        compressedBuffer[MAGIC_LENGTH] = (byte) (compressMethod | compressionLevel);
        writeIntLE(compressedLength, compressedBuffer, MAGIC_LENGTH + 1);
        writeIntLE(o, compressedBuffer, MAGIC_LENGTH + 5);
        writeIntLE(check, compressedBuffer, MAGIC_LENGTH + 9);
        assert MAGIC_LENGTH + 13 == HEADER_LENGTH;
        out.write(compressedBuffer, 0, HEADER_LENGTH + compressedLength);
        o = 0;
    }

    /**
     * Flushes this compressed {@link OutputStream}.
     *
     * If the stream has been created with <code>syncFlush=true</code>, pending data will be compressed and appended to
     * the underlying {@link OutputStream} before calling {@link OutputStream#flush()} on the underlying stream.
     * Otherwise, this method just flushes the underlying stream, so pending data might not be available for reading
     * until {@link #finish()} or {@link #close()} is called.
     */
    @Override
    public void flush() throws IOException {
        if (out != null) {
            flushBufferedData();
            out.flush();
        }
    }

    /**
     * Same as {@link #close()} except that it doesn't close the underlying stream. This can be useful if you want to
     * keep on using the underlying stream.
     *
     * @throws IOException
     *             if an I/O error occurs.
     */
    public void finish() throws IOException {
        ensureNotFinished();
        flushBufferedData();
        compressedBuffer[MAGIC_LENGTH] = (byte) (COMPRESSION_METHOD_RAW | compressionLevel);
        writeIntLE(0, compressedBuffer, MAGIC_LENGTH + 1);
        writeIntLE(0, compressedBuffer, MAGIC_LENGTH + 5);
        writeIntLE(0, compressedBuffer, MAGIC_LENGTH + 9);
        assert MAGIC_LENGTH + 13 == HEADER_LENGTH;
        out.write(compressedBuffer, 0, HEADER_LENGTH);
        finished = true;
        out.flush();
    }

    //CHECKSTYLE:OFF
    private static void writeIntLE(final int i, final byte[] buf, int off) {
        buf[off++] = (byte) i;
        buf[off++] = (byte) (i >>> 8);
        buf[off++] = (byte) (i >>> 16);
        buf[off++] = (byte) (i >>> 24);
    }
    //CHECKSTYLE:ON

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(out=" + out + ", blockSize=" + blockSize + ", compressor=" + compressor
                + ", checksum=" + checksum + ")";
    }

}
