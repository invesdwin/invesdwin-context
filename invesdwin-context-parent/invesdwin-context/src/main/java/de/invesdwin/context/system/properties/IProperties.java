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

import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.streams.SocketUtils;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

public interface IProperties {

    String INVESDWIN_DEFAULT_PASSWORD = "invesdwin";

    List<String> getKeys();

    void remove(String key);

    boolean containsKey(String key);

    boolean containsValue(String key);

    Boolean getBoolean(String key);

    default Boolean getBooleanOptional(final String key) {
        return getBooleanOptional(key, null);
    }

    default Boolean getBooleanOptional(final String key, final Boolean defaultValue) {
        if (containsValue(key)) {
            return getBoolean(key);
        } else {
            return defaultValue;
        }
    }

    void setBoolean(String key, Boolean value);

    Byte getByte(String key);

    default Byte getByteOptional(final String key) {
        return getByteOptional(key, null);
    }

    default Byte getByteOptional(final String key, final Byte defaultValue) {
        if (containsValue(key)) {
            return getByte(key);
        } else {
            return defaultValue;
        }
    }

    void setByte(String key, Byte value);

    Double getDouble(String key);

    default Double getDoubleOptional(final String key) {
        return getDoubleOptional(key, null);
    }

    default Double getDoubleOptional(final String key, final Double defaultValue) {
        if (containsValue(key)) {
            return getDouble(key);
        } else {
            return defaultValue;
        }
    }

    void setDouble(String key, Double value);

    Float getFloat(String key);

    default Float getFloatOptional(final String key) {
        return getFloatOptional(key, null);
    }

    default Float getFloatOptional(final String key, final Float defaultValue) {
        if (containsValue(key)) {
            return getFloat(key);
        } else {
            return defaultValue;
        }
    }

    void setFloat(String key, Float value);

    Integer getInteger(String key);

    default Integer getIntegerOptional(final String key) {
        return getIntegerOptional(key, null);
    }

    default Integer getIntegerOptional(final String key, final Integer defaultValue) {
        if (containsValue(key)) {
            return getInteger(key);
        } else {
            return defaultValue;
        }
    }

    void setInteger(String key, Integer value);

    Long getLong(String key);

    default Long getLongOptional(final String key) {
        return getLongOptional(key, null);
    }

    default Long getLongOptional(final String key, final Long defaultValue) {
        if (containsValue(key)) {
            return getLong(key);
        } else {
            return defaultValue;
        }
    }

    void setLong(String key, Long value);

    Short getShort(String key);

    default Short getShortOptional(final String key) {
        return getShortOptional(key, null);
    }

    default Short getShortOptional(final String key, final Short defaultValue) {
        if (containsValue(key)) {
            return getShort(key);
        } else {
            return defaultValue;
        }
    }

    void setShort(String key, Short value);

    BigDecimal getBigDecimal(String key);

    default BigDecimal getBigDecimalOptional(final String key) {
        return getBigDecimalOptional(key, null);
    }

    default BigDecimal getBigDecimalOptional(final String key, final BigDecimal defaultValue) {
        if (containsValue(key)) {
            return getBigDecimal(key);
        } else {
            return defaultValue;
        }
    }

    void setBigDecimal(String key, BigDecimal value);

    BigInteger getBigInteger(String key);

    default BigInteger getBigIntegerOptional(final String key) {
        return getBigIntegerOptional(key, null);
    }

    default BigInteger getBigIntegerOptional(final String key, final BigInteger defaultValue) {
        if (containsValue(key)) {
            return getBigInteger(key);
        } else {
            return defaultValue;
        }
    }

    void setBigInteger(String key, BigInteger value);

    Decimal getDecimal(String key);

    default Decimal getDecimalOptional(final String key) {
        return getDecimalOptional(key, null);
    }

    default Decimal getDecimalOptional(final String key, final Decimal defaultValue) {
        if (containsValue(key)) {
            return getDecimal(key);
        } else {
            return defaultValue;
        }
    }

    void setDecimal(String key, Decimal value);

    String getString(String key);

    default String getStringOptional(final String key) {
        return getStringOptional(key, null);
    }

    default String getStringOptional(final String key, final String defaultValue) {
        if (containsValue(key)) {
            return getString(key);
        } else {
            return defaultValue;
        }
    }

    void setString(String key, String value);

    Object getProperty(String key);

    default Object getPropertyOptional(final String key) {
        return getPropertyOptional(key, null);
    }

    default Object getPropertyOptional(final String key, final Object defaultValue) {
        if (containsValue(key)) {
            return getProperty(key);
        } else {
            return defaultValue;
        }
    }

    String[] getStringArray(String key);

    default String[] getStringArrayOptional(final String key) {
        return getStringArrayOptional(key, null);
    }

    default String[] getStringArrayOptional(final String key, final String[] defaultValue) {
        if (containsValue(key)) {
            return getStringArray(key);
        } else {
            return defaultValue;
        }
    }

    List<String> getList(String key);

    default List<String> getListOptional(final String key) {
        return getListOptional(key, null);
    }

    default List<String> getListOptional(final String key, final List<String> defaultValue) {
        if (containsValue(key)) {
            return getList(key);
        } else {
            return defaultValue;
        }
    }

    void setList(String key, List<String> value);

    Set<String> getSet(String key);

    default Set<String> getSetOptional(final String key) {
        return getSetOptional(key, null);
    }

    default Set<String> getSetOptional(final String key, final Set<String> defaultValue) {
        if (containsValue(key)) {
            return getSet(key);
        } else {
            return defaultValue;
        }
    }

    void setSet(String key, Set<String> value);

    FDate getDate(String key);

    default FDate getDateOptional(final String key) {
        return getDateOptional(key, null);
    }

    default FDate getDateOptional(final String key, final FDate defaultValue) {
        if (containsValue(key)) {
            return getDate(key);
        } else {
            return defaultValue;
        }
    }

    void setDate(String key, FDate value);

    Duration getDuration(String key);

    default Duration getDurationOptional(final String key) {
        return getDurationOptional(key, null);
    }

    default Duration getDurationOptional(final String key, final Duration defaultValue) {
        if (containsValue(key)) {
            return getDuration(key);
        } else {
            return defaultValue;
        }
    }

    void setDuration(String key, Duration value);

    URL getURL(String key, boolean validatePort);

    default URL getURLOptional(final String key, final boolean validatePort) {
        return getURLOptional(key, validatePort, null);
    }

    default URL getURLOptional(final String key, final boolean validatePort, final URL defaultValue) {
        if (containsValue(key)) {
            return getURL(key, validatePort);
        } else {
            return defaultValue;
        }
    }

    void setURL(String key, URL value);

    URI getURI(String key, boolean validatePort);

    default URI getURIOptional(final String key, final boolean validatePort) {
        return getURIOptional(key, validatePort, null);
    }

    default URI getURIOptional(final String key, final boolean validatePort, final URI defaultValue) {
        if (containsValue(key)) {
            return getURI(key, validatePort);
        } else {
            return defaultValue;
        }
    }

    void setURI(String key, URI value);

    /**
     * if validatePort == true then a negative port will result in an exception.
     */
    Integer getPort(String key, boolean validatePort);

    /**
     * if validatePort == true then a negative port will result in null. If value does not exist, null is also returned
     * as default value.
     */
    default Integer getPortOptional(final String key, final boolean validatePort) {
        return getPortOptional(key, validatePort, null);
    }

    /**
     * if validatePort == true then a negative port will result in null regardless of the defaultValue. If value does
     * not exist, default value is returned.
     */
    default Integer getPortOptional(final String key, final boolean validatePort, final Integer defaultValue) {
        if (containsValue(key)) {
            final Integer port = getPort(key, false);
            if (validatePort) {
                return validatePort(key, port, false);
            } else {
                return port;
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * When port == 0 then a random available port is assigned. If port is negative then an exception is thrown or null
     * is returned depending on the negativeException flag.
     */
    default Integer validatePort(final String key, final Integer port, final boolean negativeException) {
        if (port == null) {
            return null;
        } else if (port == 0) {
            final int randomPort = SocketUtils.findAvailableTcpPort();
            //override property
            setInteger(key, randomPort);
            return randomPort;
        } else if (port < 0) {
            if (negativeException) {
                throw new IllegalArgumentException("Port [" + key + "=" + port + "] should not be negative");
            } else {
                return null;
            }
        } else {
            return port;
        }
    }

    InetAddress getInetAddress(String key);

    default InetAddress getInetAddressOptional(final String key) {
        return getInetAddressOptional(key, null);
    }

    default InetAddress getInetAddressOptional(final String key, final InetAddress defaultValue) {
        if (containsValue(key)) {
            return getInetAddress(key);
        } else {
            return defaultValue;
        }
    }

    InetSocketAddress getInetSocketAddress(String key, boolean validatePort);

    default InetSocketAddress getInetSocketAddressOptional(final String key, final boolean validatePort) {
        return getInetSocketAddressOptional(key, validatePort, null);
    }

    default InetSocketAddress getInetSocketAddressOptional(final String key, final boolean validatePort,
            final InetSocketAddress defaultValue) {
        if (containsValue(key)) {
            return getInetSocketAddress(key, validatePort);
        } else {
            return defaultValue;
        }
    }

    File getFile(String key);

    default File getFileOptional(final String key) {
        return getFileOptional(key, null);
    }

    default File getFileOptional(final String key, final File defaultValue) {
        if (containsValue(key)) {
            return getFile(key);
        } else {
            return defaultValue;
        }
    }

    void setFile(String key, File value);

    <T extends Enum<T>> T getEnum(Class<T> enumType, String key);

    default <T extends Enum<T>> T getEnumOptional(final Class<T> enumType, final String key) {
        return getEnumOptional(enumType, key, null);
    }

    default <T extends Enum<T>> T getEnumOptional(final Class<T> enumType, final String key, final T defaultValue) {
        if (containsValue(key)) {
            return getEnum(enumType, key);
        } else {
            return defaultValue;
        }
    }

    void setEnum(String key, Enum<?> value);

    String getEnumFormat(Class<? extends Enum<?>> enumType);

    String getStringWithSecurityWarning(String key, String defaultValueWarning);

    default String getStringWithSecurityWarningOptional(final String key, final String defaultValueWarning) {
        return getStringWithSecurityWarningOptional(key, defaultValueWarning, null);
    }

    default String getStringWithSecurityWarningOptional(final String key, final String defaultValueWarning,
            final String defaultValue) {
        if (containsValue(key)) {
            return getStringWithSecurityWarning(key, defaultValueWarning);
        } else {
            maybeLogSecurityWarning(key, defaultValue, defaultValueWarning);
            return defaultValue;
        }
    }

    String getErrorMessage(String key, Object value, Class<?> expectedType, String message);

    void maybeLogSecurityWarning(String key, String actualValue, String defaultValueWarning);

    Map<String, String> asMap();

    int size();

    boolean isEmpty();

    void clear();
}
