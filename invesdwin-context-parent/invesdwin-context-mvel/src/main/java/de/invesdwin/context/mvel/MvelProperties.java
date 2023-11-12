package de.invesdwin.context.mvel;

import javax.annotation.concurrent.ThreadSafe;

import io.netty.util.concurrent.FastThreadLocal;

@ThreadSafe
public final class MvelProperties {

    private static final FastThreadLocal<Boolean> STRICT_OVERRIDE = new FastThreadLocal<>();
    private static volatile boolean defaultStrict = false;

    private MvelProperties() {}

    public static boolean isDefaultStrict() {
        return defaultStrict;
    }

    public static void setDefaultStrict(final boolean defaultStrict) {
        MvelProperties.defaultStrict = defaultStrict;
    }

    public static boolean isStrict() {
        final Boolean strictOverride = getStrictOverride();
        if (strictOverride != null) {
            return strictOverride.booleanValue();
        } else {
            return MvelProperties.defaultStrict;
        }
    }

    public static Boolean getStrictOverride() {
        return STRICT_OVERRIDE.get();
    }

    public static Boolean setStrictOverride(final Boolean strict) {
        final Boolean strictOverrideBefore = getStrictOverride();
        if (strict == null) {
            STRICT_OVERRIDE.remove();
        } else {
            STRICT_OVERRIDE.set(strict);
        }
        return strictOverrideBefore;
    }

}
