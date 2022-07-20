package de.invesdwin.context.integration.streams.authentication.mac;

import java.security.Key;

import de.invesdwin.context.integration.streams.authentication.mac.pool.IMacFactory;
import de.invesdwin.context.integration.streams.authentication.mac.pool.MacObjectPool;

public interface IMacAlgorithm extends IMacFactory {

    String getAlgorithm();

    int getMacLength();

    Key wrapKey(byte[] key);

    MacObjectPool getMacPool();

}
