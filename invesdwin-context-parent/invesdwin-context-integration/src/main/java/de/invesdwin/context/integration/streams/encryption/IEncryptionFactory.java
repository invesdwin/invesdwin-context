package de.invesdwin.context.integration.streams.encryption;

import java.io.InputStream;
import java.io.OutputStream;

import de.invesdwin.context.integration.streams.encryption.cipher.ICipherAlgorithm;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

public interface IEncryptionFactory {

    ICipherAlgorithm getAlgorithm();

    OutputStream newEncryptor(OutputStream out);

    InputStream newDecryptor(InputStream in);

    int encrypt(IByteBuffer src, IByteBuffer dest);

    int decrypt(IByteBuffer src, IByteBuffer dest);

    <T> ISerde<T> maybeWrap(ISerde<T> delegate);

}
