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

import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;

public interface IProperties {

    String INVESDWIN_DEFAULT_PASSWORD = "invesdwin";

    boolean containsKey(String key);

    boolean containsValue(String key);

    Boolean getBoolean(String key);

    void setBoolean(String key, Boolean value);

    Byte getByte(String key);

    void setByte(String key, Byte value);

    Double getDouble(String key);

    void setDouble(String key, Double value);

    Float getFloat(String key);

    void setFloat(String key, Float value);

    Integer getInteger(String key);

    void setInteger(String key, Integer value);

    Long getLong(String key);

    void setLong(String key, Long value);

    Short getShort(String key);

    void setShort(String key, Short value);

    BigDecimal getBigDecimal(String key);

    void setBigDecimal(String key, BigDecimal value);

    BigInteger getBigInteger(String key);

    void setBigInteger(String key, BigInteger value);

    Decimal getDecimal(String key);

    void setDecimal(String key, Decimal value);

    String getString(String key);

    void setString(String key, String value);

    String[] getStringArray(String key);

    List<String> getList(String key);

    void setList(String key, List<String> value);

    Set<String> getSet(String key);

    void setSet(String key, Set<String> value);

    FDate getDate(String key);

    void setDate(String key, FDate value);

    Duration getDuration(String key);

    void setDuration(String key, Duration value);

    URL getURL(String key, boolean validatePort);

    void setURL(String key, URL value);

    URI getURI(String key, boolean validatePort);

    void setURI(String key, URI value);

    Integer getPort(String key, boolean validatePort);

    InetAddress getInetAddress(String key);

    InetSocketAddress getInetSocketAddress(String key, boolean validatePort);

    File getFile(String key);

    void setFile(String key, File value);

    String getErrorMessage(String key, Object value, Class<?> expectedType, String message);

    <T extends Enum<T>> T getEnum(Class<T> enumType, String key);

    void setEnum(String key, Enum<?> value);

    String getEnumFormat(Class<? extends Enum<?>> enumType);

    String getStringWithSecurityWarning(String key, String defaultValueWarning);

}
