package de.invesdwin.context.integration.streams.authentication.mac.hmac;

import java.security.Key;

import javax.annotation.concurrent.Immutable;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.HmacAlgorithms;

import de.invesdwin.context.integration.streams.authentication.mac.IMacAlgorithm;
import de.invesdwin.context.integration.streams.authentication.mac.pool.IMac;
import de.invesdwin.context.integration.streams.authentication.mac.pool.JceMac;
import de.invesdwin.context.integration.streams.authentication.mac.pool.MacObjectPool;

@Immutable
public enum HmacAlgorithm implements IMacAlgorithm {
    HMAC_MD5(HmacAlgorithms.HMAC_MD5.getName(), 16),
    HMAC_SHA_1(HmacAlgorithms.HMAC_SHA_1.getName(), 20),
    HMAC_SHA_224(HmacAlgorithms.HMAC_SHA_224.getName(), 28),
    HMAC_SHA_256(HmacAlgorithms.HMAC_SHA_256.getName(), 32),
    HMAC_SHA_384(HmacAlgorithms.HMAC_SHA_384.getName(), 48),
    HMAC_SHA_512(HmacAlgorithms.HMAC_SHA_512.getName(), 64);

    public static final HmacAlgorithm DEFAULT = HMAC_SHA_256;
    private final String algorithm;
    private final MacObjectPool macPool;
    private int macLength;

    HmacAlgorithm(final String algorithm, final int macLength) {
        this.algorithm = algorithm;
        this.macPool = new MacObjectPool(this);
        this.macLength = macLength;
    }

    @Override
    public String toString() {
        return algorithm;
    }

    @Override
    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public int getMacLength() {
        return macLength;
    }

    @Override
    public IMac newMac() {
        return new JceMac(getAlgorithm());
    }

    @Override
    public Key wrapKey(final byte[] key) {
        return new SecretKeySpec(key, algorithm);
    }

    @Override
    public MacObjectPool getMacPool() {
        return macPool;
    }

}
