package de.invesdwin.context.integration.streams.key.password.scrypt;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.streams.key.password.IPasswordHasher;

@Immutable
public class ScryptPasswordHasher implements IPasswordHasher {

    public static final ScryptPasswordHasher INSTANCE = new ScryptPasswordHasher();

    @Override
    public byte[] hash(final byte[] salt, final byte[] password, final int length) {
        return null;
    }

}
