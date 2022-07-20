package de.invesdwin.context.integration.streams.authentication.mac.pool;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.annotation.concurrent.NotThreadSafe;
import javax.crypto.Mac;

@NotThreadSafe
public class JceMac implements IMac {

    private final Mac mac;

    public JceMac(final String algorithm) {
        this(getJceMacInstance(algorithm));
    }

    public JceMac(final Mac mac) {
        this.mac = mac;
    }

    public static Mac getJceMacInstance(final String algorithm) {
        try {
            return Mac.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAlgorithm() {
        return mac.getAlgorithm();
    }

    @Override
    public int getMacLength() {
        return mac.getMacLength();
    }

    @Override
    public void init(final Key key) {
        try {
            mac.init(key);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(final byte input) {
        mac.update(input);
    }

    @Override
    public void update(final java.nio.ByteBuffer input) {
        mac.update(input);
    }

    @Override
    public void update(final byte[] input) {
        mac.update(input);
    }

    @Override
    public void update(final byte[] input, final int inputOffset, final int inputLen) {
        mac.update(input, inputOffset, inputLen);
    }

    @Override
    public byte[] doFinal() {
        return mac.doFinal();
    }

    @Override
    public byte[] doFinal(final byte[] input) {
        return mac.doFinal(input);
    }

    @Override
    public void doFinal(final byte[] output, final int offset) {
        try {
            mac.doFinal(output, offset);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
