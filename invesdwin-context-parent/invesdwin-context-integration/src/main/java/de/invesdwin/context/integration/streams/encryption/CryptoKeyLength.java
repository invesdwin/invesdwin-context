package de.invesdwin.context.integration.streams.encryption;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum CryptoKeyLength {
    _128(16),
    _196(24),
    _256(32);

    private int bytes;
    private int bits;

    CryptoKeyLength(final int bytes) {
        this.bytes = bytes;
        this.bits = bytes * Byte.SIZE;
    }

    public int getBytes() {
        return bytes;
    }

    public int getBits() {
        return bits;
    }

}
