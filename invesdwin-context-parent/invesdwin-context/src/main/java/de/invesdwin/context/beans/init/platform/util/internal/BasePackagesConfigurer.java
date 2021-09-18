package de.invesdwin.context.beans.init.platform.util.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.IBasePackageDefinition;
import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.util.lang.Strings;

@ThreadSafe
public final class BasePackagesConfigurer {

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(BasePackagesConfigurer.class);
    @GuardedBy("BasePackagesConfigurer.class")
    private static Set<String> basePackages;
    private static String[] basePackagesArray;

    private BasePackagesConfigurer() {
    }

    public static synchronized Set<String> getBasePackages() {
        if (basePackages == null) {
            try {
                basePackages = new HashSet<String>();
                final Iterator<IBasePackageDefinition> basePackageDefinitions = ServiceLoader
                        .load(IBasePackageDefinition.class)
                        .iterator();
                while (basePackageDefinitions.hasNext()) {
                    final IBasePackageDefinition basePackageDefinition = basePackageDefinitions.next();
                    basePackages.add(basePackageDefinition.getBasePackage());
                }

                if (LOG.isInfoEnabled() && basePackages.size() > 0) {
                    String basePackageSingularPlural = "base package";
                    if (basePackages.size() != 1) {
                        basePackageSingularPlural += "s";
                    }

                    //CHECKSTYLE:OFF
                    LOG.info("Loading {} {} {}", basePackages.size(), basePackageSingularPlural, basePackages);
                    //CHECKSTYLE:ON
                }
            } catch (final Throwable t) {
                //webstart safety for access control
                PlatformInitializerProperties.logInitializationFailedIsIgnored(t);
                basePackages = new HashSet<String>(Arrays.asList("de.invesdwin"));
            }
        }
        return basePackages;
    }

    public static String[] getBasePackagesArray() {
        if (basePackagesArray == null) {
            basePackagesArray = getBasePackages().toArray(Strings.EMPTY_ARRAY);
        }
        return basePackagesArray;
    }

}
