package de.invesdwin.context.integration.serde.reference;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.serde.CompressingDelegateSerde;
import de.invesdwin.context.integration.serde.ISerde;
import de.invesdwin.context.integration.streams.LZ4Streams;
import de.invesdwin.util.concurrent.reference.persistent.ACompressingSoftReference;

/**
 * Behaves just like a SoftReference, with the distinction that the value is not discarded, but instead serialized until
 * it is requested again.
 * 
 * Thus this reference will never return null if the referent was not null in the first place.
 * 
 * <a href="http://stackoverflow.com/questions/10878012/using-referencequeue-and-SoftReference">Source</a>
 */
@ThreadSafe
public class SerdeCompressingSoftReference<T> extends ACompressingSoftReference<T, byte[]> {

    private final ISerde<T> compressingSerde;

    public SerdeCompressingSoftReference(final T referent, final ISerde<T> serde) {
        super(referent);
        this.compressingSerde = new CompressingDelegateSerde<T>(serde) {
            @Override
            protected OutputStream newCompressor(final OutputStream out) {
                return SerdeCompressingSoftReference.this.newCompressor(out);
            }

            @Override
            protected InputStream newDecompressor(final ByteArrayInputStream bis) {
                return SerdeCompressingSoftReference.this.newDecompressor(bis);
            }
        };
    }

    @Override
    protected T fromCompressed(final byte[] compressed) throws Exception {
        return compressingSerde.fromBytes(compressed);
    }

    @Override
    protected byte[] toCompressed(final T referent) throws Exception {
        return compressingSerde.toBytes(referent);
    }

    protected OutputStream newCompressor(final OutputStream out) {
        return LZ4Streams.newDefaultLZ4OutputStream(out);
    }

    protected InputStream newDecompressor(final ByteArrayInputStream bis) {
        return LZ4Streams.newDefaultLZ4InputStream(bis);
    }

}
