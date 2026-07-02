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
import de.invesdwin.util.collections.fast.concurrent.locked.LockedMap;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class LockedProperties implements IProperties {

    private final IProperties delegate;
    private final ILock lock;
    private Map<String, String> asMap;

    public LockedProperties(final IProperties delegate) {
        this.delegate = delegate;
        this.lock = Locks.newReentrantLock(getClass().getSimpleName());
    }

    public LockedProperties(final IProperties delegate, final ILock lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public List<String> getKeys() {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getKeys();
        }
    }

    @Override
    public void remove(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.remove(key);
        }
    }

    @Override
    public boolean containsKey(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.containsValue(key);
        }
    }

    @Override
    public Boolean getBoolean(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getBoolean(key);
        }
    }

    @Override
    public void setBoolean(final String key, final Boolean value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setBoolean(key, value);
        }
    }

    @Override
    public Byte getByte(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getByte(key);
        }
    }

    @Override
    public void setByte(final String key, final Byte value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setByte(key, value);
        }
    }

    @Override
    public Double getDouble(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getDouble(key);
        }
    }

    @Override
    public void setDouble(final String key, final Double value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setDouble(key, value);
        }
    }

    @Override
    public Float getFloat(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getFloat(key);
        }
    }

    @Override
    public void setFloat(final String key, final Float value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setFloat(key, value);
        }
    }

    @Override
    public Integer getInteger(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getInteger(key);
        }
    }

    @Override
    public void setInteger(final String key, final Integer value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setInteger(key, value);
        }
    }

    @Override
    public Long getLong(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getLong(key);
        }
    }

    @Override
    public void setLong(final String key, final Long value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setLong(key, value);
        }
    }

    @Override
    public Short getShort(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getShort(key);
        }
    }

    @Override
    public void setShort(final String key, final Short value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setShort(key, value);
        }
    }

    @Override
    public BigDecimal getBigDecimal(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getBigDecimal(key);
        }
    }

    @Override
    public void setBigDecimal(final String key, final BigDecimal value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setBigDecimal(key, value);
        }
    }

    @Override
    public BigInteger getBigInteger(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getBigInteger(key);
        }
    }

    @Override
    public void setBigInteger(final String key, final BigInteger value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setBigInteger(key, value);
        }
    }

    @Override
    public Decimal getDecimal(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getDecimal(key);
        }
    }

    @Override
    public void setDecimal(final String key, final Decimal value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setDecimal(key, value);
        }
    }

    @Override
    public String getString(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getString(key);
        }
    }

    @Override
    public void setString(final String key, final String value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setString(key, value);
        }
    }

    @Override
    public Object getProperty(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getProperty(key);
        }
    }

    @Override
    public String[] getStringArray(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getStringArray(key);
        }
    }

    @Override
    public List<String> getList(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getList(key);
        }
    }

    @Override
    public void setList(final String key, final List<String> value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setList(key, value);
        }
    }

    @Override
    public Set<String> getSet(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getSet(key);
        }
    }

    @Override
    public void setSet(final String key, final Set<String> value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setSet(key, value);
        }
    }

    @Override
    public FDate getDate(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getDate(key);
        }
    }

    @Override
    public void setDate(final String key, final FDate value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setDate(key, value);
        }
    }

    @Override
    public Duration getDuration(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getDuration(key);
        }
    }

    @Override
    public void setDuration(final String key, final Duration value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setDuration(key, value);
        }
    }

    @Override
    public URL getURL(final String key, final boolean validatePort) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getURL(key, validatePort);
        }
    }

    @Override
    public void setURL(final String key, final URL value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setURL(key, value);
        }
    }

    @Override
    public URI getURI(final String key, final boolean validatePort) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getURI(key, validatePort);
        }
    }

    @Override
    public void setURI(final String key, final URI value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setURI(key, value);
        }
    }

    @Override
    public Integer getPort(final String key, final boolean validatePort) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getPort(key, validatePort);
        }
    }

    @Override
    public InetAddress getInetAddress(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getInetAddress(key);
        }
    }

    @Override
    public InetSocketAddress getInetSocketAddress(final String key, final boolean validatePort) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getInetSocketAddress(key, validatePort);
        }
    }

    @Override
    public File getFile(final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getFile(key);
        }
    }

    @Override
    public void setFile(final String key, final File value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setFile(key, value);
        }
    }

    @Override
    public <T extends Enum<T>> T getEnum(final Class<T> enumType, final String key) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getEnum(enumType, key);
        }
    }

    @Override
    public void setEnum(final String key, final Enum<?> value) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.setEnum(key, value);
        }
    }

    @Override
    public String getEnumFormat(final Class<? extends Enum<?>> enumType) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getEnumFormat(enumType);
        }
    }

    @Override
    public String getStringWithSecurityWarning(final String key, final String defaultValueWarning) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getStringWithSecurityWarning(key, defaultValueWarning);
        }
    }

    @Override
    public String getErrorMessage(final String key, final Object value, final Class<?> expectedType,
            final String message) {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.getErrorMessage(key, value, expectedType, message);
        }
    }

    @Override
    public void maybeLogSecurityWarning(final String key, final String actualValue, final String defaultValueWarning) {
        try (ICloseableLock locked = lock.locked()) {
            delegate.maybeLogSecurityWarning(key, actualValue, defaultValueWarning);
        }
    }

    @Override
    public Map<String, String> asMap() {
        if (asMap == null) {
            synchronized (this) {
                if (asMap == null) {
                    asMap = new LockedMap<>(delegate.asMap(), lock);
                }
            }
        }
        return asMap;
    }

    @Override
    public int size() {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.size();
        }
    }

    @Override
    public boolean isEmpty() {
        try (ICloseableLock locked = lock.locked()) {
            return delegate.isEmpty();
        }
    }

    @Override
    public void clear() {
        try (ICloseableLock locked = lock.locked()) {
            delegate.clear();
        }
    }

}
