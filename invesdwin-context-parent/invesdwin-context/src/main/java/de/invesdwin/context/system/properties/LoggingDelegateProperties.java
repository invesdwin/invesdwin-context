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

import de.invesdwin.context.log.Log;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public class LoggingDelegateProperties implements IProperties {

    private final Log log;
    private final IProperties delegate;
    private final String prefix;

    public LoggingDelegateProperties(final Log log, final String prefix, final IProperties delegate) {
        this.log = log;
        if (Strings.isNotBlank(prefix)) {
            this.prefix = prefix + ": ";
        } else {
            this.prefix = "";
        }
        this.delegate = delegate;
    }

    @Override
    public List<String> getKeys() {
        return delegate.getKeys();
    }

    @Override
    public void remove(final String key) {
        logModification(key, null);
        delegate.remove(key);
    }

    private <T> void logModification(final String key, final T newValue) {
        final String oldValueStr;
        if (delegate.containsValue(key)) {
            oldValueStr = Strings.asString(delegate.getProperty(key));
        } else {
            oldValueStr = null;
        }
        final String newValueStr = Strings.asString(newValue);
        if (!Objects.equals(oldValueStr, newValueStr)) {
            log.info("%s%s = %s -> %s", prefix, key, oldValueStr, newValueStr);
        }
    }

    @Override
    public boolean containsKey(final String key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(final String key) {
        return delegate.containsValue(key);
    }

    @Override
    public Boolean getBoolean(final String key) {
        return delegate.getBoolean(key);
    }

    @Override
    public void setBoolean(final String key, final Boolean value) {
        logModification(key, value);
        delegate.setBoolean(key, value);
    }

    @Override
    public Byte getByte(final String key) {
        return delegate.getByte(key);
    }

    @Override
    public void setByte(final String key, final Byte value) {
        logModification(key, value);
        delegate.setByte(key, value);
    }

    @Override
    public Double getDouble(final String key) {
        return delegate.getDouble(key);
    }

    @Override
    public void setDouble(final String key, final Double value) {
        logModification(key, value);
        delegate.setDouble(key, value);
    }

    @Override
    public Float getFloat(final String key) {
        return delegate.getFloat(key);
    }

    @Override
    public void setFloat(final String key, final Float value) {
        logModification(key, value);
        delegate.setFloat(key, value);
    }

    @Override
    public Integer getInteger(final String key) {
        return delegate.getInteger(key);
    }

    @Override
    public void setInteger(final String key, final Integer value) {
        logModification(key, value);
        delegate.setInteger(key, value);
    }

    @Override
    public Long getLong(final String key) {
        return delegate.getLong(key);
    }

    @Override
    public void setLong(final String key, final Long value) {
        logModification(key, value);
        delegate.setLong(key, value);
    }

    @Override
    public Short getShort(final String key) {
        return delegate.getShort(key);
    }

    @Override
    public void setShort(final String key, final Short value) {
        logModification(key, value);
        delegate.setShort(key, value);
    }

    @Override
    public BigDecimal getBigDecimal(final String key) {
        return delegate.getBigDecimal(key);
    }

    @Override
    public void setBigDecimal(final String key, final BigDecimal value) {
        logModification(key, value);
        delegate.setBigDecimal(key, value);
    }

    @Override
    public BigInteger getBigInteger(final String key) {
        return delegate.getBigInteger(key);
    }

    @Override
    public void setBigInteger(final String key, final BigInteger value) {
        logModification(key, value);
        delegate.setBigInteger(key, value);
    }

    @Override
    public Decimal getDecimal(final String key) {
        return delegate.getDecimal(key);
    }

    @Override
    public void setDecimal(final String key, final Decimal value) {
        logModification(key, value);
        delegate.setDecimal(key, value);
    }

    @Override
    public String getString(final String key) {
        return delegate.getString(key);
    }

    @Override
    public Object getProperty(final String key) {
        return delegate.getProperty(key);
    }

    @Override
    public void setString(final String key, final String value) {
        logModification(key, value);
        delegate.setString(key, value);
    }

    @Override
    public String[] getStringArray(final String key) {
        return delegate.getStringArray(key);
    }

    @Override
    public List<String> getList(final String key) {
        return delegate.getList(key);
    }

    @Override
    public void setList(final String key, final List<String> value) {
        logModification(key, value);
        delegate.setList(key, value);
    }

    @Override
    public Set<String> getSet(final String key) {
        return delegate.getSet(key);
    }

    @Override
    public void setSet(final String key, final Set<String> value) {
        logModification(key, value);
        delegate.setSet(key, value);
    }

    @Override
    public FDate getDate(final String key) {
        return delegate.getDate(key);
    }

    @Override
    public void setDate(final String key, final FDate value) {
        logModification(key, value);
        delegate.setDate(key, value);
    }

    @Override
    public Duration getDuration(final String key) {
        return delegate.getDuration(key);
    }

    @Override
    public void setDuration(final String key, final Duration value) {
        logModification(key, value);
        delegate.setDuration(key, value);
    }

    @Override
    public URL getURL(final String key, final boolean validatePort) {
        return delegate.getURL(key, validatePort);
    }

    @Override
    public void setURL(final String key, final URL value) {
        logModification(key, value);
        delegate.setURL(key, value);
    }

    @Override
    public URI getURI(final String key, final boolean validatePort) {
        return delegate.getURI(key, validatePort);
    }

    @Override
    public void setURI(final String key, final URI value) {
        logModification(key, value);
        delegate.setURI(key, value);
    }

    @Override
    public Integer getPort(final String key, final boolean validatePort) {
        return delegate.getPort(key, validatePort);
    }

    @Override
    public InetAddress getInetAddress(final String key) {
        return delegate.getInetAddress(key);
    }

    @Override
    public InetSocketAddress getInetSocketAddress(final String key, final boolean validatePort) {
        return delegate.getInetSocketAddress(key, validatePort);
    }

    @Override
    public File getFile(final String key) {
        return delegate.getFile(key);
    }

    @Override
    public void setFile(final String key, final File value) {
        logModification(key, value);
        delegate.setFile(key, value);
    }

    @Override
    public String getErrorMessage(final String key, final Object value, final Class<?> expectedType,
            final String message) {
        return delegate.getErrorMessage(key, value, expectedType, message);
    }

    @Override
    public <T extends Enum<T>> T getEnum(final Class<T> enumType, final String key) {
        return delegate.getEnum(enumType, key);
    }

    @Override
    public void setEnum(final String key, final Enum<?> value) {
        logModification(key, value);
        delegate.setEnum(key, value);
    }

    @Override
    public String getEnumFormat(final Class<? extends Enum<?>> enumType) {
        return delegate.getEnumFormat(enumType);
    }

    @Override
    public String getStringWithSecurityWarning(final String key, final String defaultValueWarning) {
        return delegate.getStringWithSecurityWarning(key, defaultValueWarning);
    }

}
