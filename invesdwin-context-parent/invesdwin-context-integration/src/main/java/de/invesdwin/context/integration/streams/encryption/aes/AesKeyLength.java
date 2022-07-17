package de.invesdwin.context.integration.streams.encryption.aes;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

@Immutable
public enum AesKeyLength {
    _128(16),
    _196(24),
    _256(32);

    private int bytes;
    private int bits;

    AesKeyLength(final int bytes) {
        this.bytes = bytes;
        this.bits = bytes * Byte.SIZE;
    }

    public int getBytes() {
        return bytes;
    }

    public int getBits() {
        return bits;
    }

    public byte[] newRandomBytes(final RandomGenerator random) {
        final byte[] byteArray = ByteBuffers.allocateByteArray(bytes);
        random.nextBytes(byteArray);
        return byteArray;
    }

}
