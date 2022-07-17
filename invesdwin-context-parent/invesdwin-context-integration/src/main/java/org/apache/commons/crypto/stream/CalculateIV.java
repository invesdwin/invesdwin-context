package org.apache.commons.crypto.stream;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class CalculateIV {

    private CalculateIV() {
    }

    public static void calculateIV(final byte[] initIV, final long counter, final byte[] iv) {
        CtrCryptoInputStream.calculateIV(initIV, counter, iv);
    }

}
