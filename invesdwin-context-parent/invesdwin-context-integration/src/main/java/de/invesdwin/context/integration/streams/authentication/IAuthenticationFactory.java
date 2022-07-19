package de.invesdwin.context.integration.streams.authentication;

import java.io.InputStream;
import java.io.OutputStream;

import de.invesdwin.context.integration.streams.authentication.stream.LayeredMacInputStream;
import de.invesdwin.context.integration.streams.authentication.stream.LayeredMacOutputStream;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

public interface IAuthenticationFactory {

    LayeredMacOutputStream newMacOutputStream(OutputStream out);

    LayeredMacInputStream newMacInputStream(InputStream in);

    byte[] mac(IByteBuffer src);

    int sign(IByteBuffer src, IByteBuffer dest);

    int verify(IByteBuffer src, IByteBuffer dest);

    <T> ISerde<T> maybeWrap(ISerde<T> delegate);

}
