package de.invesdwin.context.integration.streams.key.derivation;

import de.invesdwin.context.integration.streams.authentication.mac.IMacAlgorithm;

public interface IDerivationFactory {

    IMacAlgorithm getAlgorithm();

    byte[] extract(byte[] salt, byte[] keyMaterial);

    byte[] expand(byte[] key, byte[] info, int length);

    /**
     * https://datatracker.ietf.org/doc/html/draft-ietf-tls-tls13-28
     */
    byte[] expandLabel(byte[] key, String label, byte[] context, int length);

}
