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
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.scaled.ByteSize;
import de.invesdwin.util.math.decimal.scaled.ByteSizeScale;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.xxhash.XXHashFactory;

@Immutable
public final class LZ4Streams {

    /**
     * Compression level 99 is too high in JNI with LZ4HC, making it very slow:
     * https://github.com/lz4/lz4-java/issues/142
     */
    public static final int DEFAULT_COMPRESSION_LEVEL = 9;
    public static final int LARGE_BLOCK_SIZE = Integers
            .checkedCast(new ByteSize(1D, ByteSizeScale.MEGABYTES).getValue(ByteSizeScale.BYTES));
    /*
     * 64KB is default in LZ4OutputStream (1 << 16) though 128K is almost the same speed with a bit better compression
     * on fast compressor
     * 
     * http://java-performance.info/performance-general-compression/
     */
    public static final int DEFAULT_BLOCK_SIZE = Integers
            .checkedCast(new ByteSize(64D, ByteSizeScale.KILOBYTES).getValue(ByteSizeScale.BYTES));
    public static final int DEFAULT_SEED = 0x9747b28c;

    public static final byte[] COMPRESSED_EMPTY_VALUE;

    static {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final LZ4BlockOutputStream lz4Out = newDefaultLZ4OutputStream(out);
        try {
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

    public static LZ4BlockOutputStream newDefaultLZ4OutputStream(final OutputStream out) {
        return newHighLZ4OutputStream(out);
    }

    public static LZ4BlockOutputStream newHighLZ4OutputStream(final OutputStream out) {
        return newHighLZ4OutputStream(out, DEFAULT_BLOCK_SIZE, DEFAULT_COMPRESSION_LEVEL);
    }

    public static LZ4BlockOutputStream newLargeHighLZ4OutputStream(final OutputStream out) {
        return newHighLZ4OutputStream(out, LARGE_BLOCK_SIZE, DEFAULT_COMPRESSION_LEVEL);
    }

    public static LZ4BlockOutputStream newHighLZ4OutputStream(final OutputStream out, final int blockSize,
            final int compressionLevel) {
        if (IntegrationProperties.FAST_COMPRESSION_ALWAYS) {
            return newFastLZ4OutputStream(out, blockSize);
        } else {
            return new LZ4BlockOutputStream(out, blockSize,
                    //fastestInstance picks jni which flushes too slow
                    newHighLZ4Compressor(compressionLevel), newDefaultChecksum(), true);
        }
    }

    public static LZ4BlockOutputStream newLargeFastLZ4OutputStream(final OutputStream out) {
        return newFastLZ4OutputStream(out, LARGE_BLOCK_SIZE);
    }

    public static LZ4BlockOutputStream newFastLZ4OutputStream(final OutputStream out) {
        return newFastLZ4OutputStream(out, DEFAULT_BLOCK_SIZE);
    }

    public static LZ4BlockOutputStream newFastLZ4OutputStream(final OutputStream out, final int blockSize) {
        return new LZ4BlockOutputStream(out, blockSize, newFastLZ4Compressor(), newDefaultChecksum(), true);
    }

    public static LZ4BlockInputStream newDefaultLZ4InputStream(final InputStream in) {
        return new LZ4BlockInputStream(in, newDefaultLZ4Decompressor());
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
