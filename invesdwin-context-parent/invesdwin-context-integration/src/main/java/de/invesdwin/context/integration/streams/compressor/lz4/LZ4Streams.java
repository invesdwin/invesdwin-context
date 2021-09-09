package de.invesdwin.context.integration.streams.compressor.lz4;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.zip.Checksum;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import de.invesdwin.context.integration.IntegrationProperties;
import de.invesdwin.context.integration.streams.compressor.ICompressionFactory;
import de.invesdwin.context.integration.streams.compressor.lz4.input.pool.PooledLZ4BlockInputStream;
import de.invesdwin.context.integration.streams.compressor.lz4.input.pool.PooledLZ4BlockInputStreamObjectPool;
import de.invesdwin.context.integration.streams.compressor.lz4.output.pool.PooledLZ4BlockOutputStream;
import de.invesdwin.context.integration.streams.compressor.lz4.output.pool.PooledLZ4BlockOutputStreamObjectPool;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.concurrent.pool.AgronaObjectPool;
import de.invesdwin.util.concurrent.pool.IObjectPool;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.scaled.ByteSize;
import de.invesdwin.util.math.decimal.scaled.ByteSizeScale;
import de.invesdwin.util.streams.buffer.IByteBuffer;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Exception;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.lz4.LZ4FrameOutputStream.BLOCKSIZE;
import net.jpountz.xxhash.XXHashFactory;

@Immutable
public final class LZ4Streams {

    /**
     * Compression level 99 is too high in JNI with LZ4HC, making it very slow:
     * https://github.com/lz4/lz4-java/issues/142
     */
    public static final int DEFAULT_COMPRESSION_LEVEL = 9;

    public static final BLOCKSIZE LARGE_BLOCK_SIZE = BLOCKSIZE.SIZE_1MB;
    public static final int LARGE_BLOCK_SIZE_BYTES = Integers
            .checkedCast(new ByteSize(1D, ByteSizeScale.MEGABYTES).getValue(ByteSizeScale.BYTES));
    /*
     * 64KB is default in LZ4OutputStream (1 << 16) though 128K is almost the same speed with a bit better compression
     * on fast compressor
     * 
     * http://java-performance.info/performance-general-compression/
     */
    public static final BLOCKSIZE DEFAULT_BLOCK_SIZE = BLOCKSIZE.SIZE_64KB;
    public static final int DEFAULT_BLOCK_SIZE_BYTES = Integers
            .checkedCast(new ByteSize(64D, ByteSizeScale.KILOBYTES).getValue(ByteSizeScale.BYTES));

    public static final int DEFAULT_SEED = 0x9747b28c;

    public static final byte[] COMPRESSED_EMPTY_VALUE;

    private static final int MAX_POOL_SIZE = AgronaObjectPool.DEFAULT_MAX_POOL_SIZE;

    private static final IObjectPool<PooledLZ4BlockInputStream> INPUT_POOL = new PooledLZ4BlockInputStreamObjectPool(
            newDefaultLZ4Decompressor(), LZ4Streams::newDefaultChecksum, MAX_POOL_SIZE);

    private static final IObjectPool<PooledLZ4BlockOutputStream> FAST_OUTPUT_POOL = new PooledLZ4BlockOutputStreamObjectPool(
            DEFAULT_BLOCK_SIZE_BYTES, newFastLZ4Compressor(), LZ4Streams::newDefaultChecksum, MAX_POOL_SIZE);
    private static final IObjectPool<PooledLZ4BlockOutputStream> LARGE_FAST_OUTPUT_POOL = new PooledLZ4BlockOutputStreamObjectPool(
            LARGE_BLOCK_SIZE_BYTES, newFastLZ4Compressor(), LZ4Streams::newDefaultChecksum, MAX_POOL_SIZE);
    private static final IObjectPool<PooledLZ4BlockOutputStream> HIGH_OUTPUT_POOL = new PooledLZ4BlockOutputStreamObjectPool(
            DEFAULT_BLOCK_SIZE_BYTES, newHighLZ4Compressor(), LZ4Streams::newDefaultChecksum, MAX_POOL_SIZE);
    private static final IObjectPool<PooledLZ4BlockOutputStream> LARGE_HIGH_OUTPUT_POOL = new PooledLZ4BlockOutputStreamObjectPool(
            LARGE_BLOCK_SIZE_BYTES, newHighLZ4Compressor(), LZ4Streams::newDefaultChecksum, MAX_POOL_SIZE);

    private static final int ORIGSIZE_INDEX = 0;
    private static final int ORIGSIZE_SIZE = Integer.BYTES;

    private static final int RAW_INDEX = ORIGSIZE_INDEX + ORIGSIZE_SIZE;
    private static final int RAW_SIZE = Booleans.BYTES;

    private static final int VALUE_INDEX = RAW_INDEX + RAW_SIZE;

    static {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (OutputStream lz4Out = newDefaultLZ4OutputStream(out)) {
            IOUtils.write("", lz4Out, Charset.defaultCharset());
            lz4Out.close();
            out.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        COMPRESSED_EMPTY_VALUE = out.toByteArray();
    }

    private LZ4Streams() {
    }

    public static OutputStream newDefaultLZ4OutputStream(final OutputStream out) {
        return newHighLZ4OutputStream(out);
    }

    public static OutputStream newHighLZ4OutputStream(final OutputStream out) {
        if (IntegrationProperties.FAST_COMPRESSION_ALWAYS) {
            return newFastLZ4OutputStream(out);
        } else {
            return HIGH_OUTPUT_POOL.borrowObject().init(out);
            //            return new LZ4BlockOutputStream(out, DEFAULT_BLOCK_SIZE_BYTES, newHighLZ4Compressor(), newDefaultChecksum(),
            //                    true);
        }
    }

    public static OutputStream newLargeHighLZ4OutputStream(final OutputStream out) {
        if (IntegrationProperties.FAST_COMPRESSION_ALWAYS) {
            return newLargeFastLZ4OutputStream(out);
        } else {
            return LARGE_HIGH_OUTPUT_POOL.borrowObject().init(out);
            //            return new LZ4BlockOutputStream(out, LARGE_BLOCK_SIZE_BYTES, newHighLZ4Compressor(), newDefaultChecksum(),
            //                    true);
        }
    }

    public static OutputStream newLargeFastLZ4OutputStream(final OutputStream out) {
        return LARGE_FAST_OUTPUT_POOL.borrowObject().init(out);
        //        return new LZ4BlockOutputStream(out, LARGE_BLOCK_SIZE_BYTES, newFastLZ4Compressor(), newDefaultChecksum(),
        //                true);
    }

    public static OutputStream newFastLZ4OutputStream(final OutputStream out) {
        return FAST_OUTPUT_POOL.borrowObject().init(out);
        //        return new LZ4BlockOutputStream(out, DEFAULT_BLOCK_SIZE_BYTES, newFastLZ4Compressor(), newDefaultChecksum(),
        //                true);
    }

    public static InputStream newDefaultLZ4InputStream(final InputStream in) {
        return INPUT_POOL.borrowObject().init(in);
        //        return new LZ4BlockInputStream(in, newDefaultLZ4Decompressor(), newDefaultChecksum(), true);
    }

    public static LZ4FastDecompressor newDefaultLZ4Decompressor() {
        return LZ4Factory.fastestInstance().fastDecompressor();
    }

    public static Checksum newDefaultChecksum() {
        return XXHashFactory.fastestInstance().newStreamingHash32(DEFAULT_SEED).asChecksum();
    }

    public static LZ4Compressor newDefaultLZ4Compressor() {
        return newHighLZ4Compressor();
    }

    public static LZ4Compressor newHighLZ4Compressor() {
        return newHighLZ4Compressor(DEFAULT_COMPRESSION_LEVEL);
    }

    public static LZ4Compressor newHighLZ4Compressor(final int compressionLevel) {
        if (IntegrationProperties.FAST_COMPRESSION_ALWAYS) {
            return newFastLZ4Compressor();
        } else {
            return LZ4Factory.fastestInstance().highCompressor(compressionLevel);
        }
    }

    public static LZ4Compressor newFastLZ4Compressor() {
        return LZ4Factory.fastestInstance().fastCompressor();
    }

    public static int compress(final LZ4Compressor compressor, final IByteBuffer src, final IByteBuffer dest) {
        //if compression fails we will have at maximum uncompressed size
        final int origLength = src.capacity();
        dest.ensureCapacity(origLength + VALUE_INDEX);
        final java.nio.ByteBuffer srcbb = src.asByteBuffer();
        final java.nio.ByteBuffer destbb = dest.asByteBuffer();
        final int destLength = destbb.capacity() - VALUE_INDEX;
        final int compressedLength = tryCompress(compressor, origLength, srcbb, destbb, destLength);
        dest.putInt(ORIGSIZE_INDEX, origLength);
        if (compressedLength < origLength) {
            dest.putBoolean(RAW_INDEX, false);
            return compressedLength + VALUE_INDEX;
        } else {
            dest.putBoolean(RAW_INDEX, true);
            dest.putBytesTo(VALUE_INDEX, src, origLength);
            return origLength + VALUE_INDEX;
        }
    }

    private static int tryCompress(final LZ4Compressor compressor, final int origLength,
            final java.nio.ByteBuffer srcbb, final java.nio.ByteBuffer destbb, final int destLength) {
        if (destLength <= 8) {
            return Integer.MAX_VALUE;
        }
        try {
            return compressor.compress(srcbb, 0, origLength, destbb, VALUE_INDEX, destLength);
        } catch (final LZ4Exception e) {
            Err.process(new RuntimeException("dest length is too small: " + origLength + " -> " + destLength, e));
            //destLength is too small
            return Integer.MAX_VALUE;
        }
    }

    public static int decompress(final IByteBuffer src, final IByteBuffer dest) {
        final int origLength = src.getInt(ORIGSIZE_INDEX);
        final boolean raw = src.getBoolean(RAW_INDEX);
        dest.ensureCapacity(origLength);
        if (raw) {
            dest.putBytes(0, src, VALUE_INDEX, origLength);
            return origLength;
        } else {
            return LZ4Streams.newDefaultLZ4Decompressor()
                    .decompress(src.asByteBuffer(), VALUE_INDEX, dest.asByteBuffer(), 0, origLength);
        }
    }

    public static ICompressionFactory getDefaultCompressionFactory() {
        if (IntegrationProperties.FAST_COMPRESSION_ALWAYS) {
            return FastLZ4CompressionFactory.INSTANCE;
        } else {
            return HighLZ4CompressionFactory.INSTANCE;
        }
    }

}
