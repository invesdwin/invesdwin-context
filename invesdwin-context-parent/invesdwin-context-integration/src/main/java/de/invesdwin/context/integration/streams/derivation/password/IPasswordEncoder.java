package de.invesdwin.context.integration.streams.derivation.password;

public interface IPasswordEncoder {

    byte[] encode(byte[] salt, byte[] password, int length);

}
