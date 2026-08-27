package de.invesdwin.context.system.properties.concurrent;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.util.collections.fast.concurrent.locked.readwrite.ReadWriteLockedMap;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class ReadWriteLockedProperties implements IProperties {

    private final IProperties delegate;
    private final IReadWriteLock lock;
    private Map<String, String> asMap;

    public ReadWriteLockedProperties(final IProperties delegate) {
        this.delegate = delegate;
        this.lock = Locks.newReentrantReadWriteLock(getClass().getSimpleName());
    }

    public ReadWriteLockedProperties(final IProperties delegate, final IReadWriteLock lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public List<String> getKeys() {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getKeys();
        }
    }

    @Override
    public void remove(final String key) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.remove(key);
        }
    }

    @Override
    public boolean containsKey(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.containsValue(key);
        }
    }

    @Override
    public Boolean getBoolean(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getBoolean(key);
        }
    }

    @Override
    public void setBoolean(final String key, final Boolean value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setBoolean(key, value);
        }
    }

    @Override
    public Byte getByte(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getByte(key);
        }
    }

    @Override
    public void setByte(final String key, final Byte value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setByte(key, value);
        }
    }

    @Override
    public Double getDouble(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getDouble(key);
        }
    }

    @Override
    public void setDouble(final String key, final Double value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setDouble(key, value);
        }
    }

    @Override
    public Float getFloat(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getFloat(key);
        }
    }

    @Override
    public void setFloat(final String key, final Float value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setFloat(key, value);
        }
    }

    @Override
    public Integer getInteger(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getInteger(key);
        }
    }

    @Override
    public void setInteger(final String key, final Integer value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setInteger(key, value);
        }
    }

    @Override
    public Long getLong(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getLong(key);
        }
    }

    @Override
    public void setLong(final String key, final Long value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setLong(key, value);
        }
    }

    @Override
    public Short getShort(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getShort(key);
        }
    }

    @Override
    public void setShort(final String key, final Short value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setShort(key, value);
        }
    }

    @Override
    public BigDecimal getBigDecimal(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getBigDecimal(key);
        }
    }

    @Override
    public void setBigDecimal(final String key, final BigDecimal value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setBigDecimal(key, value);
        }
    }

    @Override
    public BigInteger getBigInteger(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getBigInteger(key);
        }
    }

    @Override
    public void setBigInteger(final String key, final BigInteger value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setBigInteger(key, value);
        }
    }

    @Override
    public Decimal getDecimal(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getDecimal(key);
        }
    }

    @Override
    public void setDecimal(final String key, final Decimal value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setDecimal(key, value);
        }
    }

    @Override
    public String getString(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getString(key);
        }
    }

    @Override
    public void setString(final String key, final String value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setString(key, value);
        }
    }

    @Override
    public Object getProperty(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getProperty(key);
        }
    }

    @Override
    public String[] getStringArray(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getStringArray(key);
        }
    }

    @Override
    public List<String> getList(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getList(key);
        }
    }

    @Override
    public void setList(final String key, final List<String> value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setList(key, value);
        }
    }

    @Override
    public Set<String> getSet(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getSet(key);
        }
    }

    @Override
    public void setSet(final String key, final Set<String> value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setSet(key, value);
        }
    }

    @Override
    public FDate getDate(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getDate(key);
        }
    }

    @Override
    public void setDate(final String key, final FDate value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setDate(key, value);
        }
    }

    @Override
    public Duration getDuration(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getDuration(key);
        }
    }

    @Override
    public void setDuration(final String key, final Duration value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setDuration(key, value);
        }
    }

    @Override
    public URL getURL(final String key, final boolean validatePort) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getURL(key, validatePort);
        }
    }

    @Override
    public void setURL(final String key, final URL value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setURL(key, value);
        }
    }

    @Override
    public URI getURI(final String key, final boolean validatePort) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getURI(key, validatePort);
        }
    }

    @Override
    public void setURI(final String key, final URI value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setURI(key, value);
        }
    }

    @Override
    public Integer getPort(final String key, final boolean validatePort) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getPort(key, validatePort);
        }
    }

    @Override
    public InetAddress getInetAddress(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getInetAddress(key);
        }
    }

    @Override
    public InetSocketAddress getInetSocketAddress(final String key, final boolean validatePort) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getInetSocketAddress(key, validatePort);
        }
    }

    @Override
    public File getFile(final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getFile(key);
        }
    }

    @Override
    public void setFile(final String key, final File value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setFile(key, value);
        }
    }

    @Override
    public <T extends Enum<T>> T getEnum(final Class<T> enumType, final String key) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getEnum(enumType, key);
        }
    }

    @Override
    public void setEnum(final String key, final Enum<?> value) {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.setEnum(key, value);
        }
    }

    @Override
    public String getEnumFormat(final Class<? extends Enum<?>> enumType) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getEnumFormat(enumType);
        }
    }

    @Override
    public String getStringWithSecurityWarning(final String key, final String defaultValueWarning) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getStringWithSecurityWarning(key, defaultValueWarning);
        }
    }

    @Override
    public String getErrorMessage(final String key, final Object value, final Class<?> expectedType,
            final String message) {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.getErrorMessage(key, value, expectedType, message);
        }
    }

    @Override
    public void maybeLogSecurityWarning(final String key, final String actualValue, final String defaultValueWarning) {
        try (ICloseableLock locked = lock.readLocked()) {
            delegate.maybeLogSecurityWarning(key, actualValue, defaultValueWarning);
        }
    }

    @Override
    public Map<String, String> asMap() {
        if (asMap == null) {
            synchronized (this) {
                if (asMap == null) {
                    asMap = new ReadWriteLockedMap<>(delegate.asMap(), lock);
                }
            }
        }
        return asMap;
    }

    @Override
    public int size() {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.size();
        }
    }

    @Override
    public boolean isEmpty() {
        try (ICloseableLock locked = lock.readLocked()) {
            return delegate.isEmpty();
        }
    }

    @Override
    public void clear() {
        try (ICloseableLock locked = lock.writeLocked()) {
            delegate.clear();
        }
    }

}
