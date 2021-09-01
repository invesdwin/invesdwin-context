/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package de.invesdwin.context.integration.streams.compressor.lz4.input;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.kafka.common.compress.KafkaLZ4BlockInputStream;
import org.apache.kafka.common.compress.KafkaLZ4BlockOutputStream;
import org.apache.kafka.common.compress.KafkaLZ4BlockOutputStream.BD;
import org.apache.kafka.common.compress.KafkaLZ4BlockOutputStream.FLG;
import org.apache.kafka.common.utils.BufferSupplier;
import org.apache.kafka.common.utils.BufferSupplier.GrowableBufferSupplier;

import de.invesdwin.util.lang.Closeables;
import de.invesdwin.util.lang.description.TextDescription;
import de.invesdwin.util.streams.buffer.ByteBuffers;
import de.invesdwin.util.streams.buffer.IByteBuffer;
import net.jpountz.lz4.LZ4Exception;
import net.jpountz.lz4.LZ4SafeDecompressor;
import net.jpountz.xxhash.XXHash32;

@NotThreadSafe
public class ReusableLZ4BlockInputStream extends InputStream {

    private final IByteBuffer buffer = ByteBuffers.allocateExpandable();
    private final BufferSupplier bufferSupplier = new GrowableBufferSupplier();

    private final LZ4SafeDecompressor decompressor;
    private final XXHash32 checksum;

    private InputStream in;
    private java.nio.ByteBuffer inBuffer;
    private java.nio.ByteBuffer decompressionBuffer;
    // `flg` and `maxBlockSize` are effectively final, they are initialised in the `readHeader` method that is only
    // invoked from the constructor
    private FLG flg;
    private int maxBlockSize;

    // If a block is compressed, this is the same as `decompressionBuffer`. If a block is not compressed, this is
    // a slice of `in` to avoid unnecessary copies.
    private java.nio.ByteBuffer decompressedBuffer;
    private boolean started;
    private boolean finished;

    public ReusableLZ4BlockInputStream(final LZ4SafeDecompressor decompressor, final XXHash32 checksum) {
        this.decompressor = decompressor;
        this.checksum = checksum;
    }

    public ReusableLZ4BlockInputStream init(final InputStream in) {
        if (this.inBuffer != null) {
            throw new IllegalStateException("inBuffer should be null, thus close() was not called after use");
        }
        if (this.in != null) {
            throw new IllegalStateException("in should be null, thus close() was not called after use");
        }
        this.in = in;
        finished = false;
        started = false;
        return this;
    }

    private void maybeStart() throws IOException {
        if (started) {
            return;
        }
        started = true;
        final int length = ByteBuffers.readExpandable(in, buffer, 0);
        this.inBuffer = buffer.asByteBufferTo(length).order(ByteOrder.LITTLE_ENDIAN);
        in.close();
        in = null;

        readHeader();
        decompressionBuffer = bufferSupplier.get(maxBlockSize);
    }

    /**
     * Reads the magic number and frame descriptor from input buffer.
     *
     * @throws IOException
     */
    private void readHeader() throws IOException {
        // read first 6 bytes into buffer to check magic and FLG/BD descriptor flags
        if (inBuffer.remaining() < 6) {
            throw new IOException(KafkaLZ4BlockInputStream.PREMATURE_EOS);
        }

        if (KafkaLZ4BlockOutputStream.MAGIC != inBuffer.getInt()) {
            throw new IOException(KafkaLZ4BlockInputStream.NOT_SUPPORTED);
        }
        // mark start of data to checksum
        inBuffer.mark();

        flg = FLG.fromByte(inBuffer.get());
        maxBlockSize = BD.fromByte(inBuffer.get()).getBlockMaximumSize();

        if (flg.isContentSizeSet()) {
            if (inBuffer.remaining() < 8) {
                throw new IOException(KafkaLZ4BlockInputStream.PREMATURE_EOS);
            }
            inBuffer.position(inBuffer.position() + 8);
        }

        // Final byte of Frame Descriptor is HC checksum

        final int len = inBuffer.position() - inBuffer.reset().position();

        final int hash = checksum.hash(inBuffer, inBuffer.position(), len, 0);
        inBuffer.position(inBuffer.position() + len);
        if (inBuffer.get() != (byte) ((hash >> 8) & 0xFF)) {
            throw new IOException(KafkaLZ4BlockInputStream.DESCRIPTOR_HASH_MISMATCH);
        }
    }

    /**
     * Decompresses (if necessary) buffered data, optionally computes and validates a XXHash32 checksum, and writes the
     * result to a buffer.
     *
     * @throws IOException
     */
    //CHECKSTYLE:OFF
    private void readBlock() throws IOException {
        //CHECKSTYLE:ON
        maybeStart();
        if (inBuffer.remaining() < 4) {
            throw new IOException(KafkaLZ4BlockInputStream.PREMATURE_EOS);
        }

        int blockSize = inBuffer.getInt();
        final boolean compressed = (blockSize & KafkaLZ4BlockOutputStream.LZ4_FRAME_INCOMPRESSIBLE_MASK) == 0;
        blockSize &= ~KafkaLZ4BlockOutputStream.LZ4_FRAME_INCOMPRESSIBLE_MASK;

        // Check for EndMark
        if (blockSize == 0) {
            finished = true;
            if (flg.isContentChecksumSet()) {
                inBuffer.getInt(); // TODO: verify this content checksum
            }
            return;
        } else if (blockSize > maxBlockSize) {
            throw new IOException(TextDescription.format("Block size %s exceeded max: %s", blockSize, maxBlockSize));
        }

        if (inBuffer.remaining() < blockSize) {
            throw new IOException(KafkaLZ4BlockInputStream.PREMATURE_EOS);
        }

        if (compressed) {
            try {
                final int bufferSize = decompressor.decompress(inBuffer, inBuffer.position(), blockSize,
                        decompressionBuffer, 0, maxBlockSize);
                decompressionBuffer.position(0);
                decompressionBuffer.limit(bufferSize);
                decompressedBuffer = decompressionBuffer;
            } catch (final LZ4Exception e) {
                throw new IOException(e);
            }
        } else {
            decompressedBuffer = inBuffer.slice();
            decompressedBuffer.limit(blockSize);
        }

        // verify checksum
        if (flg.isBlockChecksumSet()) {
            final int hash = checksum.hash(inBuffer, inBuffer.position(), blockSize, 0);
            inBuffer.position(inBuffer.position() + blockSize);
            if (hash != inBuffer.getInt()) {
                throw new IOException(KafkaLZ4BlockInputStream.BLOCK_HASH_MISMATCH);
            }
        } else {
            inBuffer.position(inBuffer.position() + blockSize);
        }
    }

    @Override
    public int read() throws IOException {
        if (finished) {
            return -1;
        }
        if (available() == 0) {
            readBlock();
        }
        if (finished) {
            return -1;
        }

        return decompressedBuffer.get() & 0xFF;
    }

    //CHECKSTYLE:OFF
    @Override
    public int read(final byte[] b, final int off, int len) throws IOException {
        net.jpountz.util.SafeUtils.checkRange(b, off, len);
        if (finished) {
            return -1;
        }
        if (available() == 0) {
            readBlock();
        }
        if (finished) {
            return -1;
        }
        len = Math.min(len, available());

        decompressedBuffer.get(b, off, len);
        return len;
    }
    //CHECKSTYLE:ON

    @Override
    public long skip(final long n) throws IOException {
        if (finished) {
            return 0;
        }
        if (available() == 0) {
            readBlock();
        }
        if (finished) {
            return 0;
        }
        final int skipped = (int) Math.min(n, available());
        decompressedBuffer.position(decompressedBuffer.position() + skipped);
        return skipped;
    }

    @Override
    public int available() {
        return decompressedBuffer == null ? 0 : decompressedBuffer.remaining();
    }

    @Override
    public void close() {
        bufferSupplier.release(decompressionBuffer);
        inBuffer = null;
        decompressedBuffer = null;
        if (in != null) {
            Closeables.closeQuietly(in);
            in = null;
        }
        finished = true;
    }

    @Override
    public void mark(final int readlimit) {
        throw new RuntimeException("mark not supported");
    }

    @Override
    public void reset() {
        throw new RuntimeException("reset not supported");
    }

    @Override
    public boolean markSupported() {
        return false;
    }
}
