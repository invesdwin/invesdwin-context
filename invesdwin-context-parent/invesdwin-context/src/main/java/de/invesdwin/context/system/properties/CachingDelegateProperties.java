package de.invesdwin.context.system.properties;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.Caffeine;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.Optionals;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public final class CachingDelegateProperties implements IProperties {

    private final IProperties delegate;

    @GuardedBy("this")
    private final Map<String, Optional<?>> cache;
    @GuardedBy("none")
    private PropertiesAsMap asMap;

    public CachingDelegateProperties(final IProperties delegate) {
        Assertions.assertThat(delegate).isNotInstanceOf(getClass());
        this.cache = newCache();
        this.delegate = delegate;
    }

    public IProperties getDelegate() {
        return delegate;
    }

    @Override
    public List<String> getKeys() {
        return delegate.getKeys();
    }

    @Override
    public synchronized void remove(final String key) {
        delegate.remove(key);
        cache.remove(key);
    }

    public synchronized Object removeFromCache(final String key) {
        final Optional<?> value = cache.remove(key);
        if (value == null) {
            return null;
        } else {
            return value.orElse(null);
        }
    }

    public synchronized Object putIntoCache(final String key, final Object value) {
        final Optional<?> oldValue = cache.put(key, Optional.ofNullable(value));
        if (oldValue == null) {
            return null;
        } else {
            return oldValue.orElse(null);
        }
    }

    protected Map<String, Optional<?>> newCache() {
        return Caffeine.newBuilder().maximumSize(1000).<String, Optional<?>> build().asMap();
    }

    @Override
    public synchronized boolean containsKey(final String key) {
        if (cache.containsKey(key)) {
            return true;
        } else {
            return delegate.containsKey(key);
        }
    }

    @Override
    public synchronized boolean containsValue(final String key) {
        if (cache.get(key) != null) {
            return true;
        } else {
            return delegate.containsValue(key);
        }
    }

    @Override
    public Boolean getBoolean(final String key) {
        return getOrLoad(key, new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return delegate.getBoolean(key);
            }
        });
    }

    @Override
    public void setBoolean(final String key, final Boolean value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setBoolean(key, value);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T getOrLoad(final String key, final Callable<T> getter) {
        final Optional<T> existingValue = (Optional<T>) cache.get(key);
        if (existingValue != null) {
            return existingValue.orElse(null);
        } else {
            try {
                final T newValue = getter.call();
                Assertions.assertThat(cache.put(key, Optional.ofNullable(newValue))).isNull();
                return newValue;
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private synchronized <T> void set(final String key, final T value, final Runnable setter) {
        final Optional<T> next = Optional.ofNullable(value);
        final Optional<?> prev = cache.put(key, next);
        if (!Objects.equals(Optionals.orNull(prev), Optionals.orNull(next))) {
            setter.run();
        }
    }

    @Override
    public Byte getByte(final String key) {
        return getOrLoad(key, new Callable<Byte>() {
            @Override
            public Byte call() {
                return delegate.getByte(key);
            }
        });
    }

    @Override
    public void setByte(final String key, final Byte value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setByte(key, value);
            }
        });
    }

    @Override
    public Double getDouble(final String key) {
        return getOrLoad(key, new Callable<Double>() {
            @Override
            public Double call() {
                return delegate.getDouble(key);
            }
        });
    }

    @Override
    public void setDouble(final String key, final Double value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setDouble(key, value);
            }
        });
    }

    @Override
    public Float getFloat(final String key) {
        return getOrLoad(key, new Callable<Float>() {
            @Override
            public Float call() {
                return delegate.getFloat(key);
            }
        });
    }

    @Override
    public void setFloat(final String key, final Float value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setFloat(key, value);
            }
        });
    }

    @Override
    public Integer getInteger(final String key) {
        return getOrLoad(key, new Callable<Integer>() {
            @Override
            public Integer call() {
                return delegate.getInteger(key);
            }
        });
    }

    @Override
    public void setInteger(final String key, final Integer value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setInteger(key, value);
            }
        });
    }

    @Override
    public Long getLong(final String key) {
        return getOrLoad(key, new Callable<Long>() {
            @Override
            public Long call() {
                return delegate.getLong(key);
            }
        });
    }

    @Override
    public void setLong(final String key, final Long value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setLong(key, value);
            }
        });
    }

    @Override
    public Short getShort(final String key) {
        return getOrLoad(key, new Callable<Short>() {
            @Override
            public Short call() {
                return delegate.getShort(key);
            }
        });
    }

    @Override
    public void setShort(final String key, final Short value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setShort(key, value);
            }
        });
    }

    @Override
    public BigDecimal getBigDecimal(final String key) {
        return getOrLoad(key, new Callable<BigDecimal>() {
            @Override
            public BigDecimal call() {
                return delegate.getBigDecimal(key);
            }
        });
    }

    @Override
    public void setBigDecimal(final String key, final BigDecimal value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setBigDecimal(key, value);
            }
        });
    }

    @Override
    public BigInteger getBigInteger(final String key) {
        return getOrLoad(key, new Callable<BigInteger>() {
            @Override
            public BigInteger call() {
                return delegate.getBigInteger(key);
            }
        });
    }

    @Override
    public void setBigInteger(final String key, final BigInteger value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setBigInteger(key, value);
            }
        });
    }

    @Override
    public Decimal getDecimal(final String key) {
        return getOrLoad(key, new Callable<Decimal>() {
            @Override
            public Decimal call() {
                return delegate.getDecimal(key);
            }
        });
    }

    @Override
    public void setDecimal(final String key, final Decimal value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setDecimal(key, value);
            }
        });
    }

    @Override
    public String getString(final String key) {
        return getOrLoad(key, new Callable<String>() {
            @Override
            public String call() {
                return delegate.getString(key);
            }
        });
    }

    @Override
    public Object getProperty(final String key) {
        //don't cache this
        return delegate.getProperty(key);
    }

    @Override
    public String getStringWithSecurityWarning(final String key, final String defaultPasswordWarning) {
        return getOrLoad(key, new Callable<String>() {
            @Override
            public String call() {
                return delegate.getStringWithSecurityWarning(key, defaultPasswordWarning);
            }
        });
    }

    @Override
    public <T extends Enum<T>> T getEnum(final Class<T> enumType, final String key) {
        return getOrLoad(key, new Callable<T>() {
            @Override
            public T call() {
                return delegate.getEnum(enumType, key);
            }
        });
    }

    @Override
    public void setEnum(final String key, final Enum<?> value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setEnum(key, value);
            }
        });
    }

    @Override
    public void setString(final String key, final String value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setString(key, value);
            }
        });
    }

    @Override
    public String[] getStringArray(final String key) {
        return getOrLoad(key, new Callable<String[]>() {
            @Override
            public String[] call() {
                return delegate.getStringArray(key);
            }
        });
    }

    @Override
    public List<String> getList(final String key) {
        return getOrLoad(key, new Callable<List<String>>() {
            @Override
            public List<String> call() {
                return delegate.getList(key);
            }
        });
    }

    @Override
    public void setList(final String key, final List<String> value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setList(key, value);
            }
        });
    }

    @Override
    public Set<String> getSet(final String key) {
        return getOrLoad(key, new Callable<Set<String>>() {
            @Override
            public Set<String> call() {
                return delegate.getSet(key);
            }
        });
    }

    @Override
    public void setSet(final String key, final Set<String> value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setSet(key, value);
            }
        });
    }

    @Override
    public FDate getDate(final String key) {
        return getOrLoad(key, new Callable<FDate>() {
            @Override
            public FDate call() {
                return delegate.getDate(key);
            }
        });
    }

    @Override
    public void setDate(final String key, final FDate value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setDate(key, value);
            }
        });
    }

    @Override
    public Duration getDuration(final String key) {
        return getOrLoad(key, new Callable<Duration>() {
            @Override
            public Duration call() {
                return delegate.getDuration(key);
            }
        });
    }

    @Override
    public void setDuration(final String key, final Duration value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setDuration(key, value);
            }
        });
    }

    @Override
    public URL getURL(final String key, final boolean validatePort) {
        return getOrLoad(key, new Callable<URL>() {
            @Override
            public URL call() {
                return delegate.getURL(key, validatePort);
            }
        });
    }

    @Override
    public void setURL(final String key, final URL value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setURL(key, value);
            }
        });
    }

    @Override
    public URI getURI(final String key, final boolean validatePort) {
        return getOrLoad(key, new Callable<URI>() {
            @Override
            public URI call() {
                return delegate.getURI(key, validatePort);
            }
        });
    }

    @Override
    public void setURI(final String key, final URI value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setURI(key, value);
            }
        });
    }

    @Override
    public InetAddress getInetAddress(final String key) {
        return getOrLoad(key, new Callable<InetAddress>() {
            @Override
            public InetAddress call() {
                return delegate.getInetAddress(key);
            }
        });
    }

    @Override
    public Integer getPort(final String key, final boolean validatePort) {
        return getOrLoad(key, new Callable<Integer>() {
            @Override
            public Integer call() {
                return delegate.getPort(key, validatePort);
            }
        });
    }

    @Override
    public InetSocketAddress getInetSocketAddress(final String key, final boolean validatePort) {
        return getOrLoad(key, new Callable<InetSocketAddress>() {
            @Override
            public InetSocketAddress call() {
                return delegate.getInetSocketAddress(key, validatePort);
            }
        });
    }

    @Override
    public File getFile(final String key) {
        return getOrLoad(key, new Callable<File>() {
            @Override
            public File call() {
                return delegate.getFile(key);
            }
        });
    }

    @Override
    public void setFile(final String key, final File value) {
        set(key, value, new Runnable() {
            @Override
            public void run() {
                delegate.setFile(key, value);
            }
        });
    }

    @Override
    public String getEnumFormat(final Class<? extends Enum<?>> enumType) {
        return delegate.getEnumFormat(enumType);
    }

    @Override
    public String getErrorMessage(final String key, final Object value, final Class<?> expectedType,
            final String message) {
        return delegate.getErrorMessage(key, value, expectedType, message);
    }

    @Override
    public void maybeLogSecurityWarning(final String key, final String actualValue, final String defaultValueWarning) {
        delegate.maybeLogSecurityWarning(key, actualValue, defaultValueWarning);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Map<String, String> asMap() {
        if (asMap == null) {
            asMap = new PropertiesAsMap(this);
        }
        return asMap;
    }

}
