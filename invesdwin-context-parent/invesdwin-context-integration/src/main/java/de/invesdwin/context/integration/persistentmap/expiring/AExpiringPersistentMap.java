package de.invesdwin.context.integration.persistentmap.expiring;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.persistentmap.APersistentMap;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public abstract class AExpiringPersistentMap<K, V> extends APersistentMap<K, V> {

    private final Duration duration;

    public AExpiringPersistentMap(final String name, final Duration duration) {
        super(name);
        this.duration = duration;
    }

    @Override
    protected boolean shouldPurgeTable() {
        final FDate tableCreationTime = getTableCreationTime();
        return tableCreationTime != null && new Duration(tableCreationTime).isGreaterThan(duration);
    }

}
