package de.invesdwin.context.integration.streams.authentication.hmac;

import java.security.Key;

import javax.annotation.concurrent.Immutable;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.HmacAlgorithms;

import de.invesdwin.context.integration.streams.authentication.pool.IMac;
import de.invesdwin.context.integration.streams.authentication.pool.IMacFactory;
import de.invesdwin.context.integration.streams.authentication.pool.JceMac;
import de.invesdwin.context.integration.streams.authentication.pool.MacObjectPool;

@Immutable
public enum HmacAlgorithm implements IMacFactory {
    HMAC_MD5(HmacAlgorithms.HMAC_MD5.getName()),
    HMAC_SHA_1(HmacAlgorithms.HMAC_SHA_1.getName()),
    HMAC_SHA_224(HmacAlgorithms.HMAC_SHA_224.getName()),
    HMAC_SHA_256(HmacAlgorithms.HMAC_SHA_256.getName()),
    HMAC_SHA_384(HmacAlgorithms.HMAC_SHA_384.getName()),
    HMAC_SHA_512(HmacAlgorithms.HMAC_SHA_512.getName());

    public static final HmacAlgorithm DEFAULT = HMAC_SHA_256;
    private final String algorithm;
    private final MacObjectPool macPool;

    HmacAlgorithm(final String algorithm) {
        this.algorithm = algorithm;
        this.macPool = new MacObjectPool(this);
    }

    @Override
    public String toString() {
        return algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public IMac newMac() {
        return new JceMac(getAlgorithm());
    }

    public Key wrapKey(final byte[] key) {
        return new SecretKeySpec(key, algorithm);
    }

    public MacObjectPool getMacPool() {
        return macPool;
    }

}
