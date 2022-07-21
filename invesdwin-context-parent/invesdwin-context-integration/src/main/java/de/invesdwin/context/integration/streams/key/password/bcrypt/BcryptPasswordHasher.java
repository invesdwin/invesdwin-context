package de.invesdwin.context.integration.streams.key.password.bcrypt;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.streams.key.password.IPasswordHasher;

@Immutable
public class BcryptPasswordHasher implements IPasswordHasher {

    public static final BcryptPasswordHasher INSTANCE = new BcryptPasswordHasher();

    @Override
    public byte[] hash(final byte[] salt, final byte[] password, final int length) {
        return null;
    }

}
