package de.invesdwin.context.integration.streams.encryption.cipher.pool;

import org.apache.commons.crypto.cipher.CryptoCipher;

public interface ICryptoCipherFactory {

    CryptoCipher newCryptoCipher();

}
