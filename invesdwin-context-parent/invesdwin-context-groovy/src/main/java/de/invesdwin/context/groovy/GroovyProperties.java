package de.invesdwin.context.groovy;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.system.properties.SystemProperties;

@Immutable
public final class GroovyProperties {

    public static final boolean COMPILE_STATIC;
    public static final boolean TYPE_CHECKED;

    static {
        final SystemProperties systemProperties = new SystemProperties(GroovyProperties.class);
        COMPILE_STATIC = systemProperties.getBoolean("COMPILE_STATIC");
        TYPE_CHECKED = systemProperties.getBoolean("TYPE_CHECKED");
    }

    private GroovyProperties() {
    }

}
