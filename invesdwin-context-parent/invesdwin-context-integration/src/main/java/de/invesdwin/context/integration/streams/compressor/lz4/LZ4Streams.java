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
import de.invesdwin.context.integration.streams.compressor.lz4.input.pool.PooledLZ4BlockInputStreamObjectPool;
import de.invesdwin.context.integration.streams.compressor.lz4.output.pool.PooledLZ4BlockOutputStreamObjectPool;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.scaled.ByteSize;
import de.invesdwin.util.math.decimal.scaled.ByteSizeScale;
import io.netty.util.concurrent.FastThreadLocal;
import net.jpountz.lz4.LZ4Compressor;
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

    //normally compressors/decompressors should not be nested in one thread
    private static final int MAX_POOL_SIZE = 2;

    private static final FastThreadLocal<PooledLZ4BlockInputStreamObjectPool> INPUT_POOL_HOLDER = new FastThreadLocal<PooledLZ4BlockInputStreamObjectPool>() {
        @Override
        protected PooledLZ4BlockInputStreamObjectPool initialValue() throws Exception {
            return new PooledLZ4BlockInputStreamObjectPool(newDefaultLZ4Decompressor(), newDefaultChecksum(),
                    MAX_POOL_SIZE);
        }
    };

    private static final FastThreadLocal<PooledLZ4BlockOutputStreamObjectPool> FAST_OUTPUT_POOL_HOLDER = new FastThreadLocal<PooledLZ4BlockOutputStreamObjectPool>() {
        @Override
        protected PooledLZ4BlockOutputStreamObjectPool initialValue() throws Exception {
            return new PooledLZ4BlockOutputStreamObjectPool(DEFAULT_BLOCK_SIZE_BYTES, newFastLZ4Compressor(),
                    newDefaultChecksum(), MAX_POOL_SIZE);
        }
    };
    private static final FastThreadLocal<PooledLZ4BlockOutputStreamObjectPool> LARGE_FAST_OUTPUT_POOL_HOLDER = new FastThreadLocal<PooledLZ4BlockOutputStreamObjectPool>() {
        @Override
        protected PooledLZ4BlockOutputStreamObjectPool initialValue() throws Exception {
            return new PooledLZ4BlockOutputStreamObjectPool(LARGE_BLOCK_SIZE_BYTES, newFastLZ4Compressor(),
                    newDefaultChecksum(), MAX_POOL_SIZE);
        }
    };
    private static final FastThreadLocal<PooledLZ4BlockOutputStreamObjectPool> HIGH_OUTPUT_POOL_HOLDER = new FastThreadLocal<PooledLZ4BlockOutputStreamObjectPool>() {
        @Override
        protected PooledLZ4BlockOutputStreamObjectPool initialValue() throws Exception {
            return new PooledLZ4BlockOutputStreamObjectPool(DEFAULT_BLOCK_SIZE_BYTES, newHighLZ4Compressor(),
                    newDefaultChecksum(), MAX_POOL_SIZE);
        }
    };
    private static final FastThreadLocal<PooledLZ4BlockOutputStreamObjectPool> LARGE_HIGH_OUTPUT_POOL_HOLDER = new FastThreadLocal<PooledLZ4BlockOutputStreamObjectPool>() {
        @Override
        protected PooledLZ4BlockOutputStreamObjectPool initialValue() throws Exception {
            return new PooledLZ4BlockOutputStreamObjectPool(LARGE_BLOCK_SIZE_BYTES, newHighLZ4Compressor(),
                    newDefaultChecksum(), MAX_POOL_SIZE);
        }
    };

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
            return HIGH_OUTPUT_POOL_HOLDER.get().borrowObject().init(out);
            //            return new LZ4BlockOutputStream(out, DEFAULT_BLOCK_SIZE_BYTES, newHighLZ4Compressor(), newDefaultChecksum(),
            //                    true);
        }
    }

    public static OutputStream newLargeHighLZ4OutputStream(final OutputStream out) {
        if (IntegrationProperties.FAST_COMPRESSION_ALWAYS) {
            return newLargeFastLZ4OutputStream(out);
        } else {
            return LARGE_HIGH_OUTPUT_POOL_HOLDER.get().borrowObject().init(out);
            //            return new LZ4BlockOutputStream(out, LARGE_BLOCK_SIZE_BYTES, newHighLZ4Compressor(), newDefaultChecksum(),
            //                    true);
        }
    }

    public static OutputStream newLargeFastLZ4OutputStream(final OutputStream out) {
        return LARGE_FAST_OUTPUT_POOL_HOLDER.get().borrowObject().init(out);
        //        return new LZ4BlockOutputStream(out, LARGE_BLOCK_SIZE_BYTES, newFastLZ4Compressor(), newDefaultChecksum(),
        //                true);
    }

    public static OutputStream newFastLZ4OutputStream(final OutputStream out) {
        return FAST_OUTPUT_POOL_HOLDER.get().borrowObject().init(out);
        //        return new LZ4BlockOutputStream(out, DEFAULT_BLOCK_SIZE_BYTES, newFastLZ4Compressor(), newDefaultChecksum(),
        //                true);
    }

    public static InputStream newDefaultLZ4InputStream(final InputStream in) {
        return INPUT_POOL_HOLDER.get().borrowObject().init(in);
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
        return LZ4Factory.fastestInstance().highCompressor(compressionLevel);
    }

    public static LZ4Compressor newFastLZ4Compressor() {
        return LZ4Factory.fastestInstance().fastCompressor();
    }

}
