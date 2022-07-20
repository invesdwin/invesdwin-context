package de.invesdwin.context.integration.streams.encryption.crypto.pool;

import org.apache.commons.crypto.cipher.CryptoCipher;

public interface ICryptoCipherFactory {

    CryptoCipher newCryptoCipher();

}
