package de.invesdwin.context.integration.streams.encryption.pool;

import javax.annotation.concurrent.ThreadSafe;

import org.agrona.concurrent.ManyToManyConcurrentArrayQueue;

import de.invesdwin.util.concurrent.pool.AQueueObjectPool;
import de.invesdwin.util.concurrent.pool.AgronaObjectPool;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

@ThreadSafe
public class MutableIvParameterSpecObjectPool extends AQueueObjectPool<MutableIvParameterSpec> {

    private final int ivBytes;

    public MutableIvParameterSpecObjectPool(final int ivBytes) {
        super(new ManyToManyConcurrentArrayQueue<>(AgronaObjectPool.DEFAULT_MAX_POOL_SIZE));
        this.ivBytes = ivBytes;
    }

    @Override
    protected MutableIvParameterSpec newObject() {
        return new MutableIvParameterSpec(ByteBuffers.allocateByteArray(ivBytes));
    }

}
