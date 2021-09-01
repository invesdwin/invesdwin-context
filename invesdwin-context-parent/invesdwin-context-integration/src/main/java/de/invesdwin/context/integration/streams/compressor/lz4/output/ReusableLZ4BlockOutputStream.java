package de.invesdwin.context.integration.streams.compressor.lz4.output;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.kafka.common.compress.KafkaLZ4BlockOutputStream;
import org.apache.kafka.common.compress.KafkaLZ4BlockOutputStream.BD;
import org.apache.kafka.common.compress.KafkaLZ4BlockOutputStream.FLG;
import org.apache.kafka.common.utils.ByteUtils;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4FrameOutputStream.BLOCKSIZE;
import net.jpountz.xxhash.XXHash32;

@NotThreadSafe
public class ReusableLZ4BlockOutputStream extends OutputStream {

    private final LZ4Compressor compressor;
    private final XXHash32 checksum;
    private final FLG flg;
    private final BD bd;
    private final int maxBlockSize;
    private OutputStream out;
    private byte[] buffer;
    private byte[] compressedBuffer;
    private int bufferOffset;
    private boolean started;
    private boolean finished;

    public ReusableLZ4BlockOutputStream(final BLOCKSIZE blockSize, final LZ4Compressor compressor,
            final XXHash32 checksum) {
        this.compressor = compressor;
        this.checksum = checksum;
        this.bd = new BD(blockSize.getIndicator());
        this.flg = new FLG(true);
        this.maxBlockSize = bd.getBlockMaximumSize();
    }

    public ReusableLZ4BlockOutputStream init(final OutputStream out) {
        if (this.out != null) {
            throw new IllegalStateException("not closed!");
        }
        this.out = out;
        bufferOffset = 0;
        buffer = new byte[maxBlockSize];
        compressedBuffer = new byte[compressor.maxCompressedLength(maxBlockSize)];
        started = false;
        finished = false;
        return this;
    }

    /**
     * Writes the magic number and frame descriptor to the underlying {@link OutputStream}.
     *
     * @throws IOException
     */
    private void writeHeader() throws IOException {
        ByteUtils.writeUnsignedIntLE(buffer, 0, KafkaLZ4BlockOutputStream.MAGIC);
        bufferOffset = 4;
        buffer[bufferOffset++] = flg.toByte();
        buffer[bufferOffset++] = bd.toByte();
        // TODO write uncompressed content size, update flg.validate()

        // compute checksum on all descriptor fields
        final int offset = 4;
        final int len = bufferOffset - offset;
        final byte hash = (byte) ((checksum.hash(buffer, offset, len, 0) >> 8) & 0xFF);
        buffer[bufferOffset++] = hash;

        // write out frame descriptor
        out.write(buffer, 0, bufferOffset);
        bufferOffset = 0;
    }

    /**
     * Compresses buffered data, optionally computes an XXHash32 checksum, and writes the result to the underlying
     * {@link OutputStream}.
     *
     * @throws IOException
     */
    private void writeBlock() throws IOException {
        if (bufferOffset == 0) {
            return;
        }

        int compressedLength = compressor.compress(buffer, 0, bufferOffset, compressedBuffer, 0);
        byte[] bufferToWrite = compressedBuffer;
        int compressMethod = 0;

        // Store block uncompressed if compressed length is greater (incompressible)
        if (compressedLength >= bufferOffset) {
            bufferToWrite = buffer;
            compressedLength = bufferOffset;
            compressMethod = KafkaLZ4BlockOutputStream.LZ4_FRAME_INCOMPRESSIBLE_MASK;
        }

        // Write content
        ByteUtils.writeUnsignedIntLE(out, compressedLength | compressMethod);
        out.write(bufferToWrite, 0, compressedLength);

        // Calculate and write block checksum
        if (flg.isBlockChecksumSet()) {
            final int hash = checksum.hash(bufferToWrite, 0, compressedLength, 0);
            ByteUtils.writeUnsignedIntLE(out, hash);
        }
        bufferOffset = 0;
    }

    /**
     * Similar to the {@link #writeBlock()} method. Writes a 0-length block (without block checksum) to signal the end
     * of the block stream.
     *
     * @throws IOException
     */
    private void writeEndMark() throws IOException {
        ByteUtils.writeUnsignedIntLE(out, 0);
        // TODO implement content checksum, update flg.validate()
    }

    @Override
    public void write(final int b) throws IOException {
        ensureNotFinished();
        maybeStart();
        if (bufferOffset == maxBlockSize) {
            writeBlock();
        }
        buffer[bufferOffset++] = (byte) b;
    }

    //CHECKSTYLE:OFF
    @Override
    public void write(final byte[] b, int off, int len) throws IOException {
        net.jpountz.util.SafeUtils.checkRange(b, off, len);
        ensureNotFinished();
        maybeStart();

        int bufferRemainingLength = maxBlockSize - bufferOffset;
        // while b will fill the buffer
        while (len > bufferRemainingLength) {
            // fill remaining space in buffer
            System.arraycopy(b, off, buffer, bufferOffset, bufferRemainingLength);
            bufferOffset = maxBlockSize;
            writeBlock();
            // compute new offset and length
            off += bufferRemainingLength;
            len -= bufferRemainingLength;
            bufferRemainingLength = maxBlockSize;
        }

        System.arraycopy(b, off, buffer, bufferOffset, len);
        bufferOffset += len;
    }
    //CHECKSTYLE:ON

    @Override
    public void flush() throws IOException {
        if (!finished) {
            maybeStart();
            writeBlock();
        }
        if (out != null) {
            out.flush();
        }
    }

    private void maybeStart() throws IOException {
        if (started) {
            return;
        }
        started = true;
        writeHeader();
    }

    /**
     * A simple state check to ensure the stream is still open.
     */
    private void ensureNotFinished() {
        if (finished) {
            throw new IllegalStateException(KafkaLZ4BlockOutputStream.CLOSED_STREAM);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            if (!finished) {
                maybeStart();
                // basically flush the buffer writing the last block
                writeBlock();
                // write the end block
                writeEndMark();
            }
        } finally {
            try {
                if (out != null) {
                    try (OutputStream outStream = out) {
                        outStream.flush();
                    }
                }
            } finally {
                out = null;
                buffer = null;
                compressedBuffer = null;
                finished = true;
            }
        }
    }

}
