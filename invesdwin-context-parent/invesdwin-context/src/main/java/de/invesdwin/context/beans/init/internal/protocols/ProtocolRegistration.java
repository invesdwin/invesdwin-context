package de.invesdwin.context.beans.init.internal.protocols;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.system.properties.SystemProperties;

@Immutable
public final class ProtocolRegistration {

    private ProtocolRegistration() {}

    public static void register() {
        final SystemProperties systemProperties = new SystemProperties();
        final String key = "java.protocol.handler.pkgs";
        String newValue = ProtocolRegistration.class.getPackage().getName();
        if (systemProperties.containsKey(key)) {
            final String previousValue = systemProperties.getString(key);
            newValue += "|" + previousValue;
        }
        systemProperties.setString(key, newValue);
    }

}
