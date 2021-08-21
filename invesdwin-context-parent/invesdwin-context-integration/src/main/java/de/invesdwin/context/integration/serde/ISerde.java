package de.invesdwin.context.integration.serde;

public interface ISerde<O> {
    O fromBytes(byte[] bytes);

    byte[] toBytes(O obj);
}