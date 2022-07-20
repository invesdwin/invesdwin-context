package de.invesdwin.context.integration.streams.derivation.provider;

public interface IDerivedKeyProvider {

    byte[] newDerivedKey(byte[] info, int length);

}
