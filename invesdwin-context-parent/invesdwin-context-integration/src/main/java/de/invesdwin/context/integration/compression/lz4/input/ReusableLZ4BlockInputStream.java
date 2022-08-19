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
package de.invesdwin.context.integration.compression.lz4.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Checksum;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.compression.lz4.output.ReusableLZ4BlockOutputStream;
import de.invesdwin.util.error.FastEOFException;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.pool.APooledInputStream;
import net.jpountz.lz4.LZ4Exception;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.util.SafeUtils;

@NotThreadSafe
public class ReusableLZ4BlockInputStream extends APooledInputStream {

    private final LZ4FastDecompressor decompressor;
    private final Checksum checksum;
    private byte[] buffer;
    private byte[] compressedBuffer;
    private int originalLen;
    private int o;
    private boolean finished;
    private InputStream in;

    /**
     * Creates a new LZ4 input stream to read from the specified underlying InputStream.
     *
     * @param in
     *            the {@link InputStream} to poll
     * @param decompressor
     *            the {@link LZ4FastDecompressor decompressor} instance to use
     * @param checksum
     *            the {@link Checksum} instance to use, must be equivalent to the instance which has been used to write
     *            the stream
     */
    public ReusableLZ4BlockInputStream(final LZ4FastDecompressor decompressor, final Checksum checksum) {
        this.decompressor = decompressor;
        this.checksum = checksum;
        this.buffer = Bytes.EMPTY_ARRAY;
        this.compressedBuffer = ByteBuffers.allocateByteArray(ReusableLZ4BlockOutputStream.HEADER_LENGTH);
    }

    public ReusableLZ4BlockInputStream init(final InputStream in) {
        if (!isClosed()) {
            throw new IllegalStateException("not closed");
        }
        this.in = in;
        o = 0;
        originalLen = 0;
        finished = false;
        return this;
    }

    @Override
    public int available() throws IOException {
        if (finished) {
            return 0;
        }
        if (o == originalLen) {
            refill();
        }
        final int buffered = originalLen - o;
        final int available = in.available();
        return buffered + available;
    }

    @Override
    public int read() throws IOException {
        if (finished) {
            return -1;
        }
        if (o == originalLen) {
            refill();
        }
        if (finished) {
            return -1;
        }
        return buffer[o++] & 0xFF;
    }

    @Override
    public int read(final byte[] b, final int off, final int pLen) throws IOException {
        SafeUtils.checkRange(b, off, pLen);
        if (finished) {
            return -1;
        }
        if (o == originalLen) {
            refill();
        }
        if (finished) {
            return -1;
        }
        final int len = Math.min(pLen, originalLen - o);
        System.arraycopy(buffer, o, b, off, len);
        o += len;
        return len;
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public long skip(final long n) throws IOException {
        if (n <= 0 || finished) {
            return 0;
        }
        if (o == originalLen) {
            refill();
        }
        if (finished) {
            return 0;
        }
        final int skipped = (int) Math.min(n, originalLen - o);
        o += skipped;
        return skipped;
    }

    //CHECKSTYLE:OFF
    private void refill() throws IOException {
        //CHECKSTYLE:ON
        if (!tryReadFully(compressedBuffer, ReusableLZ4BlockOutputStream.HEADER_LENGTH)) {
            throw FastEOFException.getInstance("Stream ended prematurely");
        }
        for (int i = 0; i < ReusableLZ4BlockOutputStream.MAGIC_LENGTH; ++i) {
            if (compressedBuffer[i] != ReusableLZ4BlockOutputStream.MAGIC[i]) {
                throw new IOException("Stream is corrupted");
            }
        }
        final int token = compressedBuffer[ReusableLZ4BlockOutputStream.MAGIC_LENGTH] & 0xFF;
        final int compressionMethod = token & 0xF0;
        final int compressionLevel = ReusableLZ4BlockOutputStream.COMPRESSION_LEVEL_BASE + (token & 0x0F);
        if (compressionMethod != ReusableLZ4BlockOutputStream.COMPRESSION_METHOD_RAW
                && compressionMethod != ReusableLZ4BlockOutputStream.COMPRESSION_METHOD_LZ4) {
            throw new IOException("Stream is corrupted");
        }
        final int compressedLen = SafeUtils.readIntLE(compressedBuffer, ReusableLZ4BlockOutputStream.MAGIC_LENGTH + 1);
        originalLen = SafeUtils.readIntLE(compressedBuffer, ReusableLZ4BlockOutputStream.MAGIC_LENGTH + 5);
        final int check = SafeUtils.readIntLE(compressedBuffer, ReusableLZ4BlockOutputStream.MAGIC_LENGTH + 9);
        assert ReusableLZ4BlockOutputStream.HEADER_LENGTH == ReusableLZ4BlockOutputStream.MAGIC_LENGTH + 13;
        //CHECKSTYLE:OFF
        if (originalLen > 1 << compressionLevel || originalLen < 0 || compressedLen < 0
                || (originalLen == 0 && compressedLen != 0) || (originalLen != 0 && compressedLen == 0)
                || (compressionMethod == ReusableLZ4BlockOutputStream.COMPRESSION_METHOD_RAW
                        && originalLen != compressedLen)) {
            //CHECKSTYLE:ON
            throw new IOException("Stream is corrupted");
        }
        if (originalLen == 0 && compressedLen == 0) {
            if (check != 0) {
                throw new IOException("Stream is corrupted");
            }
            finished = true;
            return;
        }
        if (buffer.length < originalLen) {
            buffer = ByteBuffers.allocateByteArray(Math.max(originalLen, buffer.length * 3 / 2));
        }
        switch (compressionMethod) {
        case ReusableLZ4BlockOutputStream.COMPRESSION_METHOD_RAW:
            readFully(buffer, originalLen);
            break;
        case ReusableLZ4BlockOutputStream.COMPRESSION_METHOD_LZ4:
            if (compressedBuffer.length < compressedLen) {
                compressedBuffer = ByteBuffers
                        .allocateByteArray(Math.max(compressedLen, compressedBuffer.length * 3 / 2));
            }
            readFully(compressedBuffer, compressedLen);
            try {
                final int compressedLen2 = decompressor.decompress(compressedBuffer, 0, buffer, 0, originalLen);
                if (compressedLen != compressedLen2) {
                    throw new IOException("Stream is corrupted");
                }
            } catch (final LZ4Exception e) {
                throw new IOException("Stream is corrupted", e);
            }
            break;
        default:
            throw new AssertionError();
        }
        checksum.reset();
        checksum.update(buffer, 0, originalLen);
        if ((int) checksum.getValue() != check) {
            throw new IOException("Stream is corrupted");
        }
        o = 0;
    }

    // Like readFully(), except it signals incomplete reads by returning
    // false instead of throwing EOFException.
    private boolean tryReadFully(final byte[] b, final int len) throws IOException {
        int read = 0;
        while (read < len) {
            final int r = in.read(b, read, len - read);
            if (r < 0) {
                return false;
            }
            read += r;
        }
        assert len == read;
        return true;
    }

    private void readFully(final byte[] b, final int len) throws IOException {
        if (!tryReadFully(b, len)) {
            throw FastEOFException.getInstance("Stream ended prematurely");
        }
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @SuppressWarnings("sync-override")
    @Override
    public void mark(final int readlimit) {
        // unsupported
    }

    @SuppressWarnings("sync-override")
    @Override
    public void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(in=" + in + ", decompressor=" + decompressor + ", checksum=" + checksum
                + ")";
    }

    /**
     * Closes this input stream and releases any system resources associated with the stream. This method simply
     * performs {@code in.close()}.
     *
     * @throws IOException
     *             if an I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    @Override
    public void close() throws IOException {
        if (!isClosed()) {
            in.close();
            in = null;
        }
    }

    public boolean isClosed() {
        return in == null;
    }

}
