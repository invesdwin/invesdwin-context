package de.invesdwin.context.integration.streams.derivation.provider.password;

public interface IPasswordEncoder {

    byte[] encode(byte[] salt, byte[] password, int length);

}
