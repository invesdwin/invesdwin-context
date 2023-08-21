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
public class PrefixedDelegateProperties implements IProperties {

    private final IProperties delegate;
    private final String prefix;

    public PrefixedDelegateProperties(final IProperties delegate, final String prefix) {
        this.delegate = delegate;
        this.prefix = prefix;
        Assertions.checkNotBlank(prefix);
    }

    @Override
    public List<String> getKeys() {
        return delegate.getKeys();
    }

    @Override
    public void remove(final String key) {
        delegate.remove(prefix + key);
    }

    @Override
    public boolean containsKey(final String key) {
        return delegate.containsKey(prefix + key);
    }

    @Override
    public boolean containsValue(final String key) {
        return delegate.containsValue(prefix + key);
    }

    @Override
    public Boolean getBoolean(final String key) {
        return delegate.getBoolean(prefix + key);
    }

    @Override
    public void setBoolean(final String key, final Boolean value) {
        delegate.setBoolean(prefix + key, value);
    }

    @Override
    public Byte getByte(final String key) {
        return delegate.getByte(prefix + key);
    }

    @Override
    public void setByte(final String key, final Byte value) {
        delegate.setByte(prefix + key, value);
    }

    @Override
    public Double getDouble(final String key) {
        return delegate.getDouble(prefix + key);
    }

    @Override
    public void setDouble(final String key, final Double value) {
        delegate.setDouble(prefix + key, value);
    }

    @Override
    public Float getFloat(final String key) {
        return delegate.getFloat(prefix + key);
    }

    @Override
    public void setFloat(final String key, final Float value) {
        delegate.setFloat(prefix + key, value);
    }

    @Override
    public Integer getInteger(final String key) {
        return delegate.getInteger(prefix + key);
    }

    @Override
    public void setInteger(final String key, final Integer value) {
        delegate.setInteger(prefix + key, value);
    }

    @Override
    public Long getLong(final String key) {
        return delegate.getLong(prefix + key);
    }

    @Override
    public void setLong(final String key, final Long value) {
        delegate.setLong(prefix + key, value);
    }

    @Override
    public Short getShort(final String key) {
        return delegate.getShort(prefix + key);
    }

    @Override
    public void setShort(final String key, final Short value) {
        delegate.setShort(prefix + key, value);
    }

    @Override
    public BigDecimal getBigDecimal(final String key) {
        return delegate.getBigDecimal(prefix + key);
    }

    @Override
    public void setBigDecimal(final String key, final BigDecimal value) {
        delegate.setBigDecimal(prefix + key, value);
    }

    @Override
    public BigInteger getBigInteger(final String key) {
        return delegate.getBigInteger(prefix + key);
    }

    @Override
    public void setBigInteger(final String key, final BigInteger value) {
        delegate.setBigInteger(prefix + key, value);
    }

    @Override
    public Decimal getDecimal(final String key) {
        return delegate.getDecimal(prefix + key);
    }

    @Override
    public void setDecimal(final String key, final Decimal value) {
        delegate.setDecimal(prefix + key, value);
    }

    @Override
    public String getString(final String key) {
        return delegate.getString(prefix + key);
    }

    @Override
    public void setString(final String key, final String value) {
        delegate.setString(prefix + key, value);
    }

    @Override
    public Object getProperty(final String key) {
        return delegate.getProperty(prefix + key);
    }

    @Override
    public String[] getStringArray(final String key) {
        return delegate.getStringArray(prefix + key);
    }

    @Override
    public List<String> getList(final String key) {
        return delegate.getList(prefix + key);
    }

    @Override
    public void setList(final String key, final List<String> value) {
        delegate.setList(prefix + key, value);
    }

    @Override
    public Set<String> getSet(final String key) {
        return delegate.getSet(prefix + key);
    }

    @Override
    public void setSet(final String key, final Set<String> value) {
        delegate.setSet(prefix + key, value);
    }

    @Override
    public FDate getDate(final String key) {
        return delegate.getDate(prefix + key);
    }

    @Override
    public void setDate(final String key, final FDate value) {
        delegate.setDate(prefix + key, value);
    }

    @Override
    public Duration getDuration(final String key) {
        return delegate.getDuration(prefix + key);
    }

    @Override
    public void setDuration(final String key, final Duration value) {
        delegate.setDuration(prefix + key, value);
    }

    @Override
    public URL getURL(final String key, final boolean validatePort) {
        return delegate.getURL(prefix + key, validatePort);
    }

    @Override
    public void setURL(final String key, final URL value) {
        delegate.setURL(prefix + key, value);
    }

    @Override
    public URI getURI(final String key, final boolean validatePort) {
        return delegate.getURI(prefix + key, validatePort);
    }

    @Override
    public void setURI(final String key, final URI value) {
        delegate.setURI(prefix + key, value);
    }

    @Override
    public Integer getPort(final String key, final boolean validatePort) {
        return delegate.getPort(prefix + key, validatePort);
    }

    @Override
    public InetAddress getInetAddress(final String key) {
        return delegate.getInetAddress(prefix + key);
    }

    @Override
    public InetSocketAddress getInetSocketAddress(final String key, final boolean validatePort) {
        return delegate.getInetSocketAddress(prefix + key, validatePort);
    }

    @Override
    public File getFile(final String key) {
        return delegate.getFile(prefix + key);
    }

    @Override
    public void setFile(final String key, final File value) {
        delegate.setFile(prefix + key, value);
    }

    @Override
    public <T extends Enum<T>> T getEnum(final Class<T> enumType, final String key) {
        return delegate.getEnum(enumType, prefix + key);
    }

    @Override
    public void setEnum(final String key, final Enum<?> value) {
        delegate.setEnum(prefix + key, value);
    }

    @Override
    public String getEnumFormat(final Class<? extends Enum<?>> enumType) {
        return delegate.getEnumFormat(enumType);
    }

    @Override
    public String getStringWithSecurityWarning(final String key, final String defaultValueWarning) {
        return delegate.getStringWithSecurityWarning(prefix + key, defaultValueWarning);
    }

    @Override
    public String getErrorMessage(final String key, final Object value, final Class<?> expectedType,
            final String message) {
        return delegate.getErrorMessage(prefix + key, value, expectedType, message);
    }

    @Override
    public void maybeLogSecurityWarning(final String key, final String actualValue, final String defaultValueWarning) {
        delegate.maybeLogSecurityWarning(prefix + key, actualValue, defaultValueWarning);
    }

}
