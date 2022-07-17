package de.invesdwin.context.integration.serde.reference;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.streams.compression.ICompressionFactory;
import de.invesdwin.util.concurrent.reference.persistent.ACompressingWeakReference;
import de.invesdwin.util.marshallers.serde.ISerde;

/**
 * Behaves just like a WeakReference, with the distinction that the value is not discarded, but instead serialized until
 * it is requested again.
 * 
 * Thus this reference will never return null if the referent was not null in the first place.
 * 
 * <a href="http://stackoverflow.com/questions/10878012/using-referencequeue-and-WeakReference">Source</a>
 */
@ThreadSafe
public class SerdeCompressingWeakReference<T> extends ACompressingWeakReference<T, byte[]> {

    private final ISerde<T> compressingSerde;

    public SerdeCompressingWeakReference(final T referent, final ISerde<T> serde,
            final ICompressionFactory compressionFactory) {
        super(referent);
        this.compressingSerde = compressionFactory.maybeWrap(serde);
    }

    @Override
    protected T fromCompressed(final byte[] compressed) throws Exception {
        return compressingSerde.fromBytes(compressed);
    }

    @Override
    protected byte[] toCompressed(final T referent) throws Exception {
        return compressingSerde.toBytes(referent);
    }

}
