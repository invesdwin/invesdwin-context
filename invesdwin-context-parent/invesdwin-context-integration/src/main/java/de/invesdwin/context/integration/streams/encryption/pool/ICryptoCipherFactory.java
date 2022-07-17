package de.invesdwin.context.integration.streams.encryption.pool;

import org.apache.commons.crypto.cipher.CryptoCipher;

public interface ICryptoCipherFactory {

    CryptoCipher newCryptoCipher();

}
