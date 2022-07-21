package de.invesdwin.context.integration.streams.encryption.cipher.pool;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

@ThreadSafe
public class MutableIvParameterSpecObjectPool extends AAgronaObjectPool<MutableIvParameterSpec> {

    private final int ivBytes;

    public MutableIvParameterSpecObjectPool(final int ivBytes) {
        this.ivBytes = ivBytes;
    }

    @Override
    protected MutableIvParameterSpec newObject() {
        return new MutableIvParameterSpec(ByteBuffers.allocateByteArray(ivBytes));
    }

}
