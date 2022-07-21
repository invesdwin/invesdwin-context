package de.invesdwin.context.integration.streams.derivation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.streams.authentication.mac.IMacAlgorithm;
import de.invesdwin.context.integration.streams.authentication.mac.hmac.HmacAlgorithm;
import de.invesdwin.context.integration.streams.authentication.mac.pool.IMac;

/**
 * Adapted from: https://github.com/NetRiceCake/HKDF/blob/master/src/main/java/com/github/netricecake/hkdf/HKDF.java
 */
@Immutable
public class HkdfDerivationFactory implements IDerivationFactory {

    public static final HkdfDerivationFactory INSTANCE = new HkdfDerivationFactory(HmacAlgorithm.DEFAULT);

    private final IMacAlgorithm algorithm;

    public HkdfDerivationFactory(final IMacAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public IMacAlgorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public byte[] extract(final byte[] salt, final byte[] keyMaterial) {
        if (keyMaterial != null && keyMaterial.length > 0) {
            final IMac mac = algorithm.getMacPool().borrowObject();
            try {
                mac.init(algorithm.wrapKey(salt));
                return mac.doFinal(keyMaterial);
            } finally {
                algorithm.getMacPool().returnObject(mac);
            }
        } else {
            return null;
        }
    }

    @Override
    public byte[] expand(final byte[] key, final byte[] pInfo, final int length) {
        final IMac mac = algorithm.getMacPool().borrowObject();
        try {
            mac.init(algorithm.wrapKey(key));
            final byte[] info;
            if (pInfo == null) {
                info = new byte[0];
            } else {
                info = pInfo;
            }

            byte[] hashRound = new byte[0];
            final java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(length);

            for (int i = 0; i < (int) Math.ceil((double) length / (double) mac.getMacLength()); i++) {
                mac.update(hashRound);
                mac.update(info);
                mac.update((byte) (i + 1));
                hashRound = mac.doFinal();
                final int size = Math.min(length, hashRound.length);
                buffer.put(hashRound, 0, size);
            }

            return buffer.array();
        } finally {
            algorithm.getMacPool().returnObject(mac);
        }
    }

    @Override
    public byte[] expandLabel(final byte[] key, final String label, final byte[] context, final int length) {
        final byte[] hexLabel = ("tls13 " + label).getBytes();
        final byte[] info = new byte[hexLabel.length + context.length + 4];

        final byte[] hexLength = new byte[2];
        hexLength[0] = (byte) (length >> 8);
        hexLength[1] = (byte) (length);

        System.arraycopy(hexLength, 0, info, 0, 2);
        info[2] = (byte) hexLabel.length;
        System.arraycopy(hexLabel, 0, info, 3, hexLabel.length);
        info[hexLabel.length + 3] = (byte) context.length;
        System.arraycopy(context, 0, info, hexLabel.length + 4, context.length);

        return expand(key, info, length);
    }

}