package de.invesdwin.context.integration.streams.authentication.pool;

import java.security.Key;

public interface IMac {

    String getAlgorithm();

    int getMacLength();

    void init(Key key);

    void update(java.nio.ByteBuffer input);

    void update(byte input);

    void update(byte[] input);

    void update(byte[] input, int inputOffset, int inputLen);

    byte[] doFinal();

    void doFinal(byte[] output, int offset);

}
