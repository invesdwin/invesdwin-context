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
import de.invesdwin.util.collections.fast.concurrent.SynchronizedMap;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class SynchronizedProperties implements IProperties {

    private final IProperties delegate;
    private final Object lock;
    private SynchronizedMap<String, String> asMap;

    public SynchronizedProperties(final IProperties delegate) {
        this.delegate = delegate;
        this.lock = this;
    }

    public SynchronizedProperties(final IProperties delegate, final Object lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    public List<String> getKeys() {
        synchronized (lock) {
            return delegate.getKeys();
        }
    }

    @Override
    public void remove(final String key) {
        synchronized (lock) {
            delegate.remove(key);
        }
    }

    @Override
    public boolean containsKey(final String key) {
        synchronized (lock) {
            return delegate.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(final String key) {
        synchronized (lock) {
            return delegate.containsValue(key);
        }
    }

    @Override
    public Boolean getBoolean(final String key) {
        synchronized (lock) {
            return delegate.getBoolean(key);
        }
    }

    @Override
    public void setBoolean(final String key, final Boolean value) {
        synchronized (lock) {
            delegate.setBoolean(key, value);
        }
    }

    @Override
    public Byte getByte(final String key) {
        synchronized (lock) {
            return delegate.getByte(key);
        }
    }

    @Override
    public void setByte(final String key, final Byte value) {
        synchronized (lock) {
            delegate.setByte(key, value);
        }
    }

    @Override
    public Double getDouble(final String key) {
        synchronized (lock) {
            return delegate.getDouble(key);
        }
    }

    @Override
    public void setDouble(final String key, final Double value) {
        synchronized (lock) {
            delegate.setDouble(key, value);
        }
    }

    @Override
    public Float getFloat(final String key) {
        synchronized (lock) {
            return delegate.getFloat(key);
        }
    }

    @Override
    public void setFloat(final String key, final Float value) {
        synchronized (lock) {
            delegate.setFloat(key, value);
        }
    }

    @Override
    public Integer getInteger(final String key) {
        synchronized (lock) {
            return delegate.getInteger(key);
        }
    }

    @Override
    public void setInteger(final String key, final Integer value) {
        synchronized (lock) {
            delegate.setInteger(key, value);
        }
    }

    @Override
    public Long getLong(final String key) {
        synchronized (lock) {
            return delegate.getLong(key);
        }
    }

    @Override
    public void setLong(final String key, final Long value) {
        synchronized (lock) {
            delegate.setLong(key, value);
        }
    }

    @Override
    public Short getShort(final String key) {
        synchronized (lock) {
            return delegate.getShort(key);
        }
    }

    @Override
    public void setShort(final String key, final Short value) {
        synchronized (lock) {
            delegate.setShort(key, value);
        }
    }

    @Override
    public BigDecimal getBigDecimal(final String key) {
        synchronized (lock) {
            return delegate.getBigDecimal(key);
        }
    }

    @Override
    public void setBigDecimal(final String key, final BigDecimal value) {
        synchronized (lock) {
            delegate.setBigDecimal(key, value);
        }
    }

    @Override
    public BigInteger getBigInteger(final String key) {
        synchronized (lock) {
            return delegate.getBigInteger(key);
        }
    }

    @Override
    public void setBigInteger(final String key, final BigInteger value) {
        synchronized (lock) {
            delegate.setBigInteger(key, value);
        }
    }

    @Override
    public Decimal getDecimal(final String key) {
        synchronized (lock) {
            return delegate.getDecimal(key);
        }
    }

    @Override
    public void setDecimal(final String key, final Decimal value) {
        synchronized (lock) {
            delegate.setDecimal(key, value);
        }
    }

    @Override
    public String getString(final String key) {
        synchronized (lock) {
            return delegate.getString(key);
        }
    }

    @Override
    public void setString(final String key, final String value) {
        synchronized (lock) {
            delegate.setString(key, value);
        }
    }

    @Override
    public Object getProperty(final String key) {
        synchronized (lock) {
            return delegate.getProperty(key);
        }
    }

    @Override
    public String[] getStringArray(final String key) {
        synchronized (lock) {
            return delegate.getStringArray(key);
        }
    }

    @Override
    public List<String> getList(final String key) {
        synchronized (lock) {
            return delegate.getList(key);
        }
    }

    @Override
    public void setList(final String key, final List<String> value) {
        synchronized (lock) {
            delegate.setList(key, value);
        }
    }

    @Override
    public Set<String> getSet(final String key) {
        synchronized (lock) {
            return delegate.getSet(key);
        }
    }

    @Override
    public void setSet(final String key, final Set<String> value) {
        synchronized (lock) {
            delegate.setSet(key, value);
        }
    }

    @Override
    public FDate getDate(final String key) {
        synchronized (lock) {
            return delegate.getDate(key);
        }
    }

    @Override
    public void setDate(final String key, final FDate value) {
        synchronized (lock) {
            delegate.setDate(key, value);
        }
    }

    @Override
    public Duration getDuration(final String key) {
        synchronized (lock) {
            return delegate.getDuration(key);
        }
    }

    @Override
    public void setDuration(final String key, final Duration value) {
        synchronized (lock) {
            delegate.setDuration(key, value);
        }
    }

    @Override
    public URL getURL(final String key, final boolean validatePort) {
        synchronized (lock) {
            return delegate.getURL(key, validatePort);
        }
    }

    @Override
    public void setURL(final String key, final URL value) {
        synchronized (lock) {
            delegate.setURL(key, value);
        }
    }

    @Override
    public URI getURI(final String key, final boolean validatePort) {
        synchronized (lock) {
            return delegate.getURI(key, validatePort);
        }
    }

    @Override
    public void setURI(final String key, final URI value) {
        synchronized (lock) {
            delegate.setURI(key, value);
        }
    }

    @Override
    public Integer getPort(final String key, final boolean validatePort) {
        synchronized (lock) {
            return delegate.getPort(key, validatePort);
        }
    }

    @Override
    public InetAddress getInetAddress(final String key) {
        synchronized (lock) {
            return delegate.getInetAddress(key);
        }
    }

    @Override
    public InetSocketAddress getInetSocketAddress(final String key, final boolean validatePort) {
        synchronized (lock) {
            return delegate.getInetSocketAddress(key, validatePort);
        }
    }

    @Override
    public File getFile(final String key) {
        synchronized (lock) {
            return delegate.getFile(key);
        }
    }

    @Override
    public void setFile(final String key, final File value) {
        synchronized (lock) {
            delegate.setFile(key, value);
        }
    }

    @Override
    public <T extends Enum<T>> T getEnum(final Class<T> enumType, final String key) {
        synchronized (lock) {
            return delegate.getEnum(enumType, key);
        }
    }

    @Override
    public void setEnum(final String key, final Enum<?> value) {
        synchronized (lock) {
            delegate.setEnum(key, value);
        }
    }

    @Override
    public String getEnumFormat(final Class<? extends Enum<?>> enumType) {
        synchronized (lock) {
            return delegate.getEnumFormat(enumType);
        }
    }

    @Override
    public String getStringWithSecurityWarning(final String key, final String defaultValueWarning) {
        synchronized (lock) {
            return delegate.getStringWithSecurityWarning(key, defaultValueWarning);
        }
    }

    @Override
    public String getErrorMessage(final String key, final Object value, final Class<?> expectedType,
            final String message) {
        synchronized (lock) {
            return delegate.getErrorMessage(key, value, expectedType, message);
        }
    }

    @Override
    public void maybeLogSecurityWarning(final String key, final String actualValue, final String defaultValueWarning) {
        synchronized (lock) {
            delegate.maybeLogSecurityWarning(key, actualValue, defaultValueWarning);
        }
    }

    @Override
    public Map<String, String> asMap() {
        if (asMap == null) {
            synchronized (lock) {
                if (asMap == null) {
                    asMap = new SynchronizedMap<>(delegate.asMap(), lock);
                }
            }
        }
        return asMap;
    }

    @Override
    public int size() {
        synchronized (lock) {
            return delegate.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (lock) {
            return delegate.isEmpty();
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            delegate.clear();
        }
    }

}
