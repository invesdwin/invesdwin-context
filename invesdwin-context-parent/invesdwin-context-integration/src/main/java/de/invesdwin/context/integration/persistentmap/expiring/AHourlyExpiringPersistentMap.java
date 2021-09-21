package de.invesdwin.context.integration.persistentmap.expiring;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.persistentmap.APersistentMap;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;

@ThreadSafe
public abstract class AHourlyExpiringPersistentMap<K, V> extends APersistentMap<K, V> {

    public AHourlyExpiringPersistentMap(final String name) {
        super(name);
    }

    @Override
    protected boolean shouldPurgeTable() {
        final FDate tableCreationTime = getTableCreationTime();
        return tableCreationTime != null && !FDates.isSameJulianHour(tableCreationTime, new FDate());
    }

}
