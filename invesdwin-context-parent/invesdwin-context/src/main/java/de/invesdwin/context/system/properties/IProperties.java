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

    Double getDouble(String key);

    Float getFloat(String key);

    Integer getInteger(String key);

    void setInteger(String key, Integer value);

    Long getLong(String key);

    Short getShort(String key);

    BigDecimal getBigDecimal(String key);

    BigInteger getBigInteger(String key);

    Decimal getDecimal(String key);

    String getString(String key);

    <T extends Enum<T>> T getEnum(Class<T> enumType, String key);

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

    URI getURI(String key, boolean validatePort);

    Integer getPort(String key, boolean validatePort);

    InetAddress getInetAddress(String key);

    InetSocketAddress getInetSocketAddress(String key, boolean validatePort);

    File getFile(String key);

    String getEnumFormat(Class<? extends Enum<?>> enumType);

    String getErrorMessage(String key, Object value, Class<?> expectedType, String message);

    void setEnum(String key, Enum<?> value);

    String getStringWithSecurityWarning(String key, String defaultValueWarning);

}
