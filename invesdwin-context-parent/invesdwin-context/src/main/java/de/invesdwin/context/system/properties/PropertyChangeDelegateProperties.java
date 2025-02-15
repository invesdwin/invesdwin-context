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
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.APropertyChangeSupportedBase;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public class PropertyChangeDelegateProperties extends APropertyChangeSupportedBase implements IProperties {

    private final IProperties delegate;
    @GuardedBy("none")
    private PropertiesAsMap asMap;

    public PropertyChangeDelegateProperties(final IProperties delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<String> getKeys() {
        return delegate.getKeys();
    }

    @Override
    public void remove(final String key) {
        final Object oldValue = getOldValue(key);
        delegate.remove(key);
        firePropertyChange(key, oldValue, null);
    }

    private Object getOldValue(final String key) {
        return delegate.containsValue(key) ? delegate.getProperty(key) : null;
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
        final Object oldValue = getOldValue(key);
        delegate.setBoolean(key, value);
        firePropertyChange(key, oldValue, value);
    }

    @Override
    public Byte getByte(final String key) {
        return delegate.getByte(key);
    }

    @Override
    public void setByte(final String key, final Byte value) {
        final Object oldValue = getOldValue(key);
        delegate.setByte(key, value);
        firePropertyChange(key, oldValue, value);
    }

    @Override
    public Double getDouble(final String key) {
        return delegate.getDouble(key);
    }

    @Override
    public void setDouble(final String key, final Double value) {
        final Object oldValue = getOldValue(key);
        delegate.setDouble(key, value);
        firePropertyChange(key, oldValue, value);
    }

    @Override
    public Float getFloat(final String key) {
        return delegate.getFloat(key);
    }

    @Override
    public void setFloat(final String key, final Float value) {
        final Object oldValue = getOldValue(key);
        delegate.setFloat(key, value);
        firePropertyChange(key, oldValue, value);
    }

    @Override
    public Integer getInteger(final String key) {
        return delegate.getInteger(key);
    }

    @Override
    public void setInteger(final String key, final Integer value) {
        final Object oldValue = getOldValue(key);
        delegate.setInteger(key, value);
        firePropertyChange(key, oldValue, value);
    }

    @Override
    public Long getLong(final String key) {
        return delegate.getLong(key);
    }

    @Override
    public void setLong(final String key, final Long value) {
        final Object oldValue = getOldValue(key);
        delegate.setLong(key, value);
        firePropertyChange(key, oldValue, value);
    }

    @Override
    public Short getShort(final String key) {
        return delegate.getShort(key);
    }

    @Override
    public void setShort(final String key, final Short value) {
        final Object oldValue = getOldValue(key);
        delegate.setShort(key, value);
        firePropertyChange(key, oldValue, value);
    }

    @Override
    public BigDecimal getBigDecimal(final String key) {
        return delegate.getBigDecimal(key);
    }

    @Override
    public void setBigDecimal(final String key, final BigDecimal value) {
        final Object oldValue = getOldValue(key);
        delegate.setBigDecimal(key, value);
        firePropertyChange(key, oldValue, value);
    }

    @Override
    public BigInteger getBigInteger(final String key) {
        return delegate.getBigInteger(key);
    }

    @Override
    public void setBigInteger(final String key, final BigInteger value) {
        final Object oldValue = getOldValue(key);
        delegate.setBigInteger(key, value);
        firePropertyChange(key, oldValue, value);
    }

    @Override
    public Decimal getDecimal(final String key) {
        return delegate.getDecimal(key);
    }

    @Override
    public void setDecimal(final String key, final Decimal value) {
        final Object oldValue = getOldValue(key);
        delegate.setDecimal(key, value);
        firePropertyChange(key, oldValue, value);
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
        final Object oldValue = getOldValue(key);
        delegate.setString(key, value);
        firePropertyChange(key, oldValue, value);
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
        final Object oldValue = getOldValue(key);
        delegate.setList(key, value);
        firePropertyChange(key, oldValue, value);
    }

    @Override
    public Set<String> getSet(final String key) {
        return delegate.getSet(key);
    }

    @Override
    public void setSet(final String key, final Set<String> value) {
        final Object oldValue = getOldValue(key);
        delegate.setSet(key, value);
        firePropertyChange(key, oldValue, value);
    }

    @Override
    public FDate getDate(final String key) {
        return delegate.getDate(key);
    }

    @Override
    public void setDate(final String key, final FDate value) {
        final Object oldValue = getOldValue(key);
        delegate.setDate(key, value);
        firePropertyChange(key, oldValue, value);
    }

    @Override
    public Duration getDuration(final String key) {
        return delegate.getDuration(key);
    }

    @Override
    public void setDuration(final String key, final Duration value) {
        final Object oldValue = getOldValue(key);
        delegate.setDuration(key, value);
        firePropertyChange(key, oldValue, value);
    }

    @Override
    public URL getURL(final String key, final boolean validatePort) {
        return delegate.getURL(key, validatePort);
    }

    @Override
    public void setURL(final String key, final URL value) {
        final Object oldValue = getOldValue(key);
        delegate.setURL(key, value);
        firePropertyChange(key, oldValue, value);
    }

    @Override
    public URI getURI(final String key, final boolean validatePort) {
        return delegate.getURI(key, validatePort);
    }

    @Override
    public void setURI(final String key, final URI value) {
        final Object oldValue = getOldValue(key);
        delegate.setURI(key, value);
        firePropertyChange(key, oldValue, value);
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
        final Object oldValue = getOldValue(key);
        delegate.setFile(key, value);
        firePropertyChange(key, oldValue, value);
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
        final Object oldValue = getOldValue(key);
        delegate.setEnum(key, value);
        firePropertyChange(key, oldValue, value);
    }

    @Override
    public String getEnumFormat(final Class<? extends Enum<?>> enumType) {
        return delegate.getEnumFormat(enumType);
    }

    @Override
    public String getStringWithSecurityWarning(final String key, final String defaultValueWarning) {
        return delegate.getStringWithSecurityWarning(key, defaultValueWarning);
    }

    @Override
    public void maybeLogSecurityWarning(final String key, final String actualValue, final String defaultValueWarning) {
        delegate.maybeLogSecurityWarning(key, actualValue, defaultValueWarning);
    }

    @Override
    public Map<String, String> asMap() {
        if (asMap == null) {
            asMap = new PropertiesAsMap(this);
        }
        return asMap;
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

}
