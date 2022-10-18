package de.invesdwin.context.system.properties;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public final class IgnoreExceptionsDelegateProperties implements IProperties {

    private final IProperties delegate;

    public IgnoreExceptionsDelegateProperties(final IProperties delegate) {
        Assertions.assertThat(delegate).isNotInstanceOf(getClass());
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
    public void remove(final String key) {
        try {
            delegate.remove(key);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public boolean containsKey(final String key) {
        try {
            return delegate.containsKey(key);
        } catch (final Throwable t) {
            return false;
        }
    }

    @Override
    public boolean containsValue(final String key) {
        try {
            return delegate.containsValue(key);
        } catch (final Throwable t) {
            return false;
        }
    }

    @Override
    public Boolean getBoolean(final String key) {
        try {
            return delegate.getBoolean(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setBoolean(final String key, final Boolean value) {
        try {
            delegate.setBoolean(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public Byte getByte(final String key) {
        try {
            return delegate.getByte(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setByte(final String key, final Byte value) {
        try {
            delegate.setByte(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public Double getDouble(final String key) {
        try {
            return delegate.getDouble(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setDouble(final String key, final Double value) {
        try {
            delegate.setDouble(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public Float getFloat(final String key) {
        try {
            return delegate.getFloat(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setFloat(final String key, final Float value) {
        try {
            delegate.setFloat(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public Integer getInteger(final String key) {
        try {
            return delegate.getInteger(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setInteger(final String key, final Integer value) {
        try {
            delegate.setInteger(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public Long getLong(final String key) {
        try {
            return delegate.getLong(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setLong(final String key, final Long value) {
        try {
            delegate.setLong(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public Short getShort(final String key) {
        try {
            return delegate.getShort(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setShort(final String key, final Short value) {
        try {
            delegate.setShort(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public BigDecimal getBigDecimal(final String key) {
        try {
            return delegate.getBigDecimal(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setBigDecimal(final String key, final BigDecimal value) {
        try {
            delegate.setBigDecimal(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public BigInteger getBigInteger(final String key) {
        try {
            return delegate.getBigInteger(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setBigInteger(final String key, final BigInteger value) {
        try {
            delegate.setBigInteger(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public Decimal getDecimal(final String key) {
        try {
            return delegate.getDecimal(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setDecimal(final String key, final Decimal value) {
        try {
            delegate.setDecimal(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public String getString(final String key) {
        try {
            return delegate.getString(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public Object getProperty(final String key) {
        try {
            return delegate.getProperty(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public String getStringWithSecurityWarning(final String key, final String defaultPasswordWarning) {
        try {
            return delegate.getStringWithSecurityWarning(key, defaultPasswordWarning);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public <T extends Enum<T>> T getEnum(final Class<T> enumType, final String key) {
        try {
            return delegate.getEnum(enumType, key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setEnum(final String key, final Enum<?> value) {
        try {
            delegate.setEnum(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public void setString(final String key, final String value) {
        try {
            delegate.setString(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public String[] getStringArray(final String key) {
        try {
            return delegate.getStringArray(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public List<String> getList(final String key) {
        try {
            return delegate.getList(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setList(final String key, final List<String> value) {
        try {
            delegate.setList(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public Set<String> getSet(final String key) {
        try {
            return delegate.getSet(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setSet(final String key, final Set<String> value) {
        try {
            delegate.setSet(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public FDate getDate(final String key) {
        try {
            return delegate.getDate(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setDate(final String key, final FDate value) {
        try {
            delegate.setDate(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public Duration getDuration(final String key) {
        try {
            return delegate.getDuration(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setDuration(final String key, final Duration value) {
        try {
            delegate.setDuration(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public URL getURL(final String key, final boolean validatePort) {
        try {
            return delegate.getURL(key, validatePort);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setURL(final String key, final URL value) {
        try {
            delegate.setURL(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public URI getURI(final String key, final boolean validatePort) {
        try {
            return delegate.getURI(key, validatePort);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setURI(final String key, final URI value) {
        try {
            delegate.setURI(key, value);
        } catch (final Throwable t) {
            //noop
        }
    }

    @Override
    public InetAddress getInetAddress(final String key) {
        try {
            return delegate.getInetAddress(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public Integer getPort(final String key, final boolean validatePort) {
        try {
            return delegate.getPort(key, validatePort);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public InetSocketAddress getInetSocketAddress(final String key, final boolean validatePort) {
        try {
            return delegate.getInetSocketAddress(key, validatePort);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public File getFile(final String key) {
        try {
            return delegate.getFile(key);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public void setFile(final String key, final File value) {
        try {
            delegate.setFile(key, value);
        } catch (final Throwable t) {
            //noop
        }
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

}
