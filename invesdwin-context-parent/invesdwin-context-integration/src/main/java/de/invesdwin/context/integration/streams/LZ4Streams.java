package de.invesdwin.context.integration.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.ByteSize;
import de.invesdwin.util.math.decimal.scaled.ByteSizeScale;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.xxhash.XXHashFactory;

@Immutable
public final class LZ4Streams {

    public static final int DEFAULT_COMPRESSION_LEVEL = 99;
    public static final int LARGE_BLOCK_SIZE = new ByteSize(new Decimal("1"), ByteSizeScale.MEGABYTES)
            .getValue(ByteSizeScale.BYTES)
            .intValue();
    /*
     * 64KB is default in LZ4OutputStream (1 << 16) though 128K is almost the same speed with a bit better compression
     * on fast compressor
     * 
     * http://java-performance.info/performance-general-compression/
     */
    public static final int DEFAULT_BLOCK_SIZE = new ByteSize(new Decimal("64"), ByteSizeScale.KILOBYTES)
            .getValue(ByteSizeScale.BYTES)
            .intValue();
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

    private LZ4Streams() {}

    public static LZ4BlockOutputStream newDefaultLZ4OutputStream(final OutputStream out) {
        return newHighLZ4OutputStream(out, DEFAULT_BLOCK_SIZE, DEFAULT_COMPRESSION_LEVEL);
    }

    public static LZ4BlockOutputStream newLargeLZ4OutputStream(final OutputStream out) {
        return newHighLZ4OutputStream(out, LARGE_BLOCK_SIZE, DEFAULT_COMPRESSION_LEVEL);
    }

    public static LZ4BlockOutputStream newHighLZ4OutputStream(final OutputStream out, final int blockSize,
            final int compressionLevel) {
        return new LZ4BlockOutputStream(out, blockSize, LZ4Factory.fastestInstance().highCompressor(compressionLevel),
                XXHashFactory.fastestInstance().newStreamingHash32(DEFAULT_SEED).asChecksum(), true);
    }

    public static LZ4BlockOutputStream newFastLZ4OutputStream(final OutputStream out) {
        return newFastLZ4OutputStream(out, DEFAULT_BLOCK_SIZE);
    }

    public static LZ4BlockOutputStream newFastLZ4OutputStream(final OutputStream out, final int blockSize) {
        return new LZ4BlockOutputStream(out, blockSize, LZ4Factory.fastestInstance().fastCompressor(),
                XXHashFactory.fastestInstance().newStreamingHash32(DEFAULT_SEED).asChecksum(), true);
    }

    public static LZ4BlockInputStream newDefaultLZ4InputStream(final InputStream in) {
        return new LZ4BlockInputStream(in, LZ4Factory.fastestInstance().fastDecompressor());
    }

}
