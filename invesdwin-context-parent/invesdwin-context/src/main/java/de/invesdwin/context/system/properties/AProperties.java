package de.invesdwin.context.system.properties;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.configuration2.AbstractConfiguration;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.log.Log;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.uri.Addresses;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public abstract class AProperties implements IProperties {

    public static final char LIST_DELIMITER = ',';

    private final Log log = new Log(this);

    @GuardedBy("none")
    private AbstractConfiguration delegate;
    @GuardedBy("none")
    private PropertiesAsMap asMap;

    protected abstract AbstractConfiguration createDelegate();

    public AbstractConfiguration getDelegate() {
        if (delegate == null) {
            synchronized (this) {
                if (delegate == null) {
                    delegate = createDelegate();
                }
            }
        }
        return delegate;
    }

    @Override
    public List<String> getKeys() {
        return Lists.toListWithoutHasNext(getDelegate().getKeys());
    }

    @Override
    public void remove(final String key) {
        final String keyPath = prefix(key);
        getDelegate().clearProperty(keyPath);
    }

    private void setProperty(final String key, final String value) {
        getDelegate().setProperty(prefix(key), value);
    }

    @Override
    public boolean containsKey(final String key) {
        final String keyPath = prefix(key);
        return getDelegate().containsKey(keyPath);
    }

    @Override
    public boolean containsValue(final String key) {
        if (containsKey(key)) {
            final Object property = getDelegate().getProperty(prefix(key));
            return !Strings.isBlank(Strings.asString(property));
        } else {
            return false;
        }
    }

    @Override
    public Boolean getBoolean(final String key) {
        final String keyPath = prefix(key);
        return maybeThrowIfMissing(keyPath, getDelegate().getBoolean(keyPath, null));
    }

    @Override
    public void setBoolean(final String key, final Boolean value) {
        setProperty(key, Strings.asString(value));
    }

    @Override
    public Byte getByte(final String key) {
        final String keyPath = prefix(key);
        return maybeThrowIfMissing(keyPath, getDelegate().getByte(keyPath, null));
    }

    @Override
    public void setByte(final String key, final Byte value) {
        setProperty(key, Strings.asString(value));
    }

    @Override
    public Double getDouble(final String key) {
        final String keyPath = prefix(key);
        return maybeThrowIfMissing(keyPath, getDelegate().getDouble(keyPath, null));
    }

    @Override
    public void setDouble(final String key, final Double value) {
        setProperty(key, Strings.asString(value));
    }

    @Override
    public Float getFloat(final String key) {
        final String keyPath = prefix(key);
        return maybeThrowIfMissing(keyPath, getDelegate().getFloat(keyPath, null));
    }

    @Override
    public void setFloat(final String key, final Float value) {
        setProperty(key, Strings.asString(value));
    }

    @Override
    public Integer getInteger(final String key) {
        final String keyPath = prefix(key);
        return maybeThrowIfMissing(keyPath, getDelegate().getInteger(keyPath, null));
    }

    @Override
    public void setInteger(final String key, final Integer value) {
        final String keyPath = prefix(key);
        setProperty(keyPath, Strings.asString(value));
    }

    @Override
    public Long getLong(final String key) {
        final String keyPath = prefix(key);
        return maybeThrowIfMissing(keyPath, getDelegate().getLong(keyPath, null));
    }

    @Override
    public void setLong(final String key, final Long value) {
        setProperty(key, Strings.asString(value));
    }

    @Override
    public Short getShort(final String key) {
        final String keyPath = prefix(key);
        return maybeThrowIfMissing(keyPath, getDelegate().getShort(keyPath, null));
    }

    @Override
    public void setShort(final String key, final Short value) {
        setProperty(key, Strings.asString(value));
    }

    @Override
    public BigDecimal getBigDecimal(final String key) {
        final String keyPath = prefix(key);
        return maybeThrowIfMissing(keyPath, getDelegate().getBigDecimal(keyPath));
    }

    @Override
    public void setBigDecimal(final String key, final BigDecimal value) {
        setProperty(key, Strings.asString(value));
    }

    @Override
    public BigInteger getBigInteger(final String key) {
        final String keyPath = prefix(key);
        return maybeThrowIfMissing(keyPath, getDelegate().getBigInteger(keyPath));
    }

    @Override
    public void setBigInteger(final String key, final BigInteger value) {
        setProperty(key, Strings.asString(value));
    }

    @Override
    public Decimal getDecimal(final String key) {
        return Decimal.valueOf(getDouble(key));
    }

    @Override
    public void setDecimal(final String key, final Decimal value) {
        setDouble(key, Doubles.checkedCastObj(value));
    }

    @Override
    public String getString(final String key) {
        final String keyPath = prefix(key);
        return maybeThrowIfMissing(keyPath, getDelegate().getString(keyPath));
    }

    @Override
    public Object getProperty(final String key) {
        final String keyPath = prefix(key);
        return maybeThrowIfMissing(keyPath, getDelegate().getProperty(keyPath));
    }

    @Override
    public String getStringWithSecurityWarning(final String key, final String defaultValueWarning) {
        final String keyPath = prefix(key);
        final String actualValue = maybeThrowIfMissing(keyPath, getDelegate().getString(keyPath));
        maybeLogSecurityWarning(keyPath, actualValue, defaultValueWarning);
        return actualValue;
    }

    @Override
    public void maybeLogSecurityWarning(final String key, final String actualValue, final String defaultValueWarning) {
        if (!ContextProperties.IS_TEST_ENVIRONMENT && defaultValueWarning != null
                && defaultValueWarning.equals(actualValue)) {
            log.warn("Property [%s] is currently set to the default value [%s] in a production environment, "
                    + "please override this value because otherwise you might expose yourself to a security risk. "
                    + "See the invesdwin-context documentation for details on how to do this.", prefix(key),
                    defaultValueWarning);
        }
    }

    @Override
    public <T extends Enum<T>> T getEnum(final Class<T> enumType, final String key) {
        final String value = getString(key);
        if (value == null) {
            return null;
        }
        try {
            return Enum.valueOf(enumType, value);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    getErrorMessage(key, value, Duration.class, "Expected format: " + getEnumFormat(enumType)));
        }
    }

    @Override
    public void setEnum(final String key, final Enum<?> value) {
        setProperty(key, value.name());
    }

    @Override
    public void setString(final String key, final String value) {
        setProperty(key, value);
    }

    @Override
    public String[] getStringArray(final String key) {
        final String keyPath = prefix(key);
        //provoke exception if something is not set
        if (getString(keyPath) == null) {
            return null;
        }
        final String str = getDelegate().getString(prefix(keyPath));
        return Strings.splitPreserveAllTokens(str, LIST_DELIMITER);
    }

    @Override
    public List<String> getList(final String key) {
        return Strings.asList(getStringArray(key));
    }

    @Override
    public void setList(final String key, final List<String> value) {
        final String keyPath = prefix(key);
        final String valueStr = Strings.asString(value, LIST_DELIMITER);
        getDelegate().setProperty(keyPath, valueStr);
    }

    @Override
    public Set<String> getSet(final String key) {
        final List<String> list = getList(key);
        if (list == null) {
            return null;
        } else {
            return ILockCollectionFactory.getInstance(false).newLinkedSet(list);
        }
    }

    @Override
    public void setSet(final String key, final Set<String> value) {
        if (value == null) {
            setList(key, null);
        } else {
            setList(key, new ArrayList<String>(value));
        }
    }

    private String prefix(final String key) {
        return Strings.putPrefix(key, getPropertyPrefix());
    }

    protected String getPropertyPrefix() {
        return null;
    }

    @Override
    public FDate getDate(final String key) {
        final String value = getString(key);
        return FDate.valueOf(value, FDate.FORMAT_ISO_DATE_TIME_PS);
    }

    @Override
    public void setDate(final String key, final FDate value) {
        setProperty(key, FDates.toString(value, FDate.FORMAT_ISO_DATE_TIME_PS));
    }

    @Override
    public void setDuration(final String key, final Duration value) {
        setProperty(key, Duration.toStringValue(value));
    }

    @Override
    public Duration getDuration(final String key) {
        final String value = getString(key);
        if (value == null) {
            return null;
        }
        final Duration ret = Duration.valueOf(value);
        if (ret == null) {
            throw new IllegalArgumentException(getErrorMessage(key, value, Duration.class,
                    "Expected format: <NUMBER> " + getEnumFormat(FTimeUnit.class)));
        }
        return ret;

    }

    @Override
    public URL getURL(final String key, final boolean validatePort) {
        try {
            return getURI(key, validatePort).toURL();
        } catch (final Throwable t) {
            final String str = getString(key);
            throw new IllegalArgumentException(getErrorMessage(key, str, URL.class, t.getMessage()), t);
        }
    }

    @Override
    public void setURL(final String key, final URL value) {
        setProperty(key, Strings.asString(value));
    }

    @Override
    public URI getURI(final String key, final boolean validatePort) {
        final String str = getString(key);
        try {
            URI uri = URIs.asUri(str);
            if (uri.getHost() == null && uri.getScheme() == null) {
                //without a protocol, calls to getHost and getPort will fail, thus add a fake one!
                uri = URIs.asUri("p://" + str);
            }
            if (!uri.getScheme().equals("file")) {
                Assertions.assertThat(uri.getHost())
                        .as("Unable to get host from URI (maybe you forgot to add <protocol>:// or so?): " + uri)
                        .isNotNull();
            }
            if (validatePort) {
                final int port = uri.getPort();
                if (port == 0) {
                    final int randomPort = de.invesdwin.util.streams.SocketUtils.findAvailableTcpPort();
                    //override property
                    uri = URIs.setPort(uri, randomPort);
                    setProperty(key, uri.toString());
                } else {
                    Assertions.assertThat(port)
                            .as("Unable to get port from URI (maybe you forgot to add <protocol>:// or so?): " + uri)
                            .isGreaterThan(0);
                }
            }
            return uri;
        } catch (final Throwable t) {
            throw new IllegalArgumentException(getErrorMessage(key, str, URI.class, t.getMessage()), t);
        }
    }

    @Override
    public void setURI(final String key, final URI value) {
        setProperty(key, Strings.asString(value));
    }

    @Override
    public Integer getPort(final String key, final boolean validatePort) {
        final Integer port = getInteger(key);
        if (validatePort) {
            return validatePort(key, port, true);
        }
        return port;
    }

    @Override
    public InetAddress getInetAddress(final String key) {
        return Addresses.asAddress(getString(key));
    }

    @Override
    public InetSocketAddress getInetSocketAddress(final String key, final boolean validatePort) {
        final String value = getString(key);
        final String[] split = Strings.splitPreserveAllTokens(value, ":");
        Throwable cause = null;
        if (split.length == 2) {
            try {
                final String host = split[0];
                final int port = Integer.parseInt(split[1]);
                if (validatePort) {
                    if (port == 0) {
                        final int randomPort = de.invesdwin.util.streams.SocketUtils.findAvailableTcpPort();
                        //override property
                        setString(key, host + ":" + randomPort);
                        return Addresses.asAddress(host, randomPort);
                    } else {
                        Assertions.assertThat(port).as("Unable to determine port").isGreaterThan(0);
                    }
                }
                return Addresses.asAddress(host, port);
            } catch (final NumberFormatException e) {
                cause = e;
            }
        }
        throw new IllegalArgumentException(
                getErrorMessage(key, value, InetSocketAddress.class, "Expected format: <HOST>:<PORT>"), cause);
    }

    @Override
    public File getFile(final String key) {
        final String str = getString(key);
        if (str == null) {
            return null;
        } else {
            return new File(str);
        }
    }

    @Override
    public void setFile(final String key, final File value) {
        if (value == null) {
            setString(key, null);
        } else {
            setString(key, value.getAbsolutePath());
        }
    }

    @Override
    public String getEnumFormat(final Class<? extends Enum<?>> enumType) {
        return newEnumFormat(enumType);
    }

    public static String newEnumFormat(final Class<? extends Enum<?>> enumType) {
        final String delimiter = " | ";
        final StringBuilder sb = new StringBuilder("(");
        for (final Enum<?> value : enumType.getEnumConstants()) {
            sb.append(value.name());
            sb.append(delimiter);
        }
        return Strings.removeEnd(sb.toString(), delimiter) + ")";
    }

    @Override
    public String getErrorMessage(final String key, final Object value, final Class<?> expectedType,
            final String message) {
        return newErrorMessage(prefix(key), value, expectedType, message);
    }

    public static String newErrorMessage(final String key, final Object value, final Class<?> expectedType,
            final String message) {
        Assertions.checkNotNull(key);
        String error = "Property " + key;
        if (value != null) {
            error += " [" + value + "]";
        }
        if (expectedType != null) {
            error += " is not of type " + expectedType.getSimpleName();
        }
        error += ".";
        if (Strings.isNotBlank(message)) {
            error += Strings.putPrefix(message, " ");
        }
        return error;
    }

    private <T> T maybeThrowIfMissing(final String key, final T value) {
        if (getDelegate().isThrowExceptionOnMissing() && value == null) {
            //CHECKSTYLE:OFF we explicitly want the stacktrace here
            throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
            //CHECKSTYLE:ON
        } else {
            return value;
        }
    }

    @Override
    public int size() {
        return getDelegate().size();
    }

    @Override
    public boolean isEmpty() {
        return getDelegate().isEmpty();
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

    @Override
    public Map<String, String> asMap() {
        if (asMap == null) {
            synchronized (this) {
                if (asMap == null) {
                    asMap = new PropertiesAsMap(this);
                }
            }
        }
        return asMap;
    }

}
