package de.invesdwin.context.integration.streams.encryption.pool;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.crypto.cipher.CryptoCipher;

import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.lang.Closeables;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public final class CryptoCipherObjectPool extends ATimeoutObjectPool<CryptoCipher> {

    private final ICryptoCipherFactory factory;

    public CryptoCipherObjectPool(final ICryptoCipherFactory factory) {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
        this.factory = factory;
    }

    @Override
    protected CryptoCipher newObject() {
        return factory.newCryptoCipher();
    }

    @Override
    public void invalidateObject(final CryptoCipher obj) {
        Closeables.closeQuietly(obj);
    }

    @Override
    protected void passivateObject(final CryptoCipher element) {
        //noop
    }

}
