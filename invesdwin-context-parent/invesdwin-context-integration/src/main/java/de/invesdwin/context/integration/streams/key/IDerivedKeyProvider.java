package de.invesdwin.context.integration.streams.key;

public interface IDerivedKeyProvider {

    byte[] newDerivedKey(byte[] info, int length);

}
