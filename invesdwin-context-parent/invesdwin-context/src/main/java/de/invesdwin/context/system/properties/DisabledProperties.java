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

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public final class DisabledProperties implements IProperties {

    public static final DisabledProperties INSTANCE = new DisabledProperties();

    private DisabledProperties() {}

    @Override
    public List<String> getKeys() {
        return Collections.emptyList();
    }

    @Override
    public void remove(final String key) {}

    @Override
    public boolean containsKey(final String key) {
        return false;
    }

    @Override
    public boolean containsValue(final String key) {
        return false;
    }

    @Override
    public Boolean getBoolean(final String key) {
        return null;
    }

    @Override
    public void setBoolean(final String key, final Boolean value) {}

    @Override
    public Byte getByte(final String key) {
        return null;
    }

    @Override
    public void setByte(final String key, final Byte value) {}

    @Override
    public Double getDouble(final String key) {
        return null;
    }

    @Override
    public void setDouble(final String key, final Double value) {}

    @Override
    public Float getFloat(final String key) {
        return null;
    }

    @Override
    public void setFloat(final String key, final Float value) {}

    @Override
    public Integer getInteger(final String key) {
        return null;
    }

    @Override
    public void setInteger(final String key, final Integer value) {}

    @Override
    public Long getLong(final String key) {
        return null;
    }

    @Override
    public void setLong(final String key, final Long value) {}

    @Override
    public Short getShort(final String key) {
        return null;
    }

    @Override
    public void setShort(final String key, final Short value) {}

    @Override
    public BigDecimal getBigDecimal(final String key) {
        return null;
    }

    @Override
    public void setBigDecimal(final String key, final BigDecimal value) {}

    @Override
    public BigInteger getBigInteger(final String key) {
        return null;
    }

    @Override
    public void setBigInteger(final String key, final BigInteger value) {}

    @Override
    public Decimal getDecimal(final String key) {
        return null;
    }

    @Override
    public void setDecimal(final String key, final Decimal value) {}

    @Override
    public String getString(final String key) {
        return null;
    }

    @Override
    public void setString(final String key, final String value) {}

    @Override
    public Object getProperty(final String key) {
        return null;
    }

    @Override
    public String[] getStringArray(final String key) {
        return null;
    }

    @Override
    public List<String> getList(final String key) {
        return null;
    }

    @Override
    public void setList(final String key, final List<String> value) {}

    @Override
    public Set<String> getSet(final String key) {
        return null;
    }

    @Override
    public void setSet(final String key, final Set<String> value) {}

    @Override
    public FDate getDate(final String key) {
        return null;
    }

    @Override
    public void setDate(final String key, final FDate value) {}

    @Override
    public Duration getDuration(final String key) {
        return null;
    }

    @Override
    public void setDuration(final String key, final Duration value) {}

    @Override
    public URL getURL(final String key, final boolean validatePort) {
        return null;
    }

    @Override
    public void setURL(final String key, final URL value) {}

    @Override
    public URI getURI(final String key, final boolean validatePort) {
        return null;
    }

    @Override
    public void setURI(final String key, final URI value) {}

    @Override
    public Integer getPort(final String key, final boolean validatePort) {
        return null;
    }

    @Override
    public InetAddress getInetAddress(final String key) {
        return null;
    }

    @Override
    public InetSocketAddress getInetSocketAddress(final String key, final boolean validatePort) {
        return null;
    }

    @Override
    public File getFile(final String key) {
        return null;
    }

    @Override
    public void setFile(final String key, final File value) {}

    @Override
    public <T extends Enum<T>> T getEnum(final Class<T> enumType, final String key) {
        return null;
    }

    @Override
    public void setEnum(final String key, final Enum<?> value) {}

    @Override
    public String getEnumFormat(final Class<? extends Enum<?>> enumType) {
        return null;
    }

    @Override
    public String getStringWithSecurityWarning(final String key, final String defaultValueWarning) {
        return null;
    }

    @Override
    public String getErrorMessage(final String key, final Object value, final Class<?> expectedType,
            final String message) {
        return AProperties.newErrorMessage(key, value, expectedType, message);
    }

    @Override
    public void maybeLogSecurityWarning(final String key, final String actualValue, final String defaultValueWarning) {}

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {}

    @Override
    public Map<String, String> asMap() {
        return Collections.emptyMap();
    }

}
