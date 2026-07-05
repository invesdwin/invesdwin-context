package de.invesdwin.context.beans.init.platform.util.internal;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.invesdwin.context.IBasePackageDefinition;
import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.lang.string.Strings;

@ThreadSafe
public final class BasePackagesConfigurer {

    private static final Logger LOG = LogManager.getLogger(BasePackagesConfigurer.class);
    @GuardedBy("BasePackagesConfigurer.class")
    private static Set<String> basePackages;
    private static String[] basePackagesArray;

    private BasePackagesConfigurer() {}

    public static synchronized Set<String> getBasePackages() {
        if (basePackages == null) {
            try {
                basePackages = ILockCollectionFactory.getInstance(false).newLinkedSet();
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

                    LOG.info("Loading " + basePackages.size() + " " + basePackageSingularPlural + " " + basePackages);
                }
            } catch (final Throwable t) {
                //webstart safety for access control
                PlatformInitializerProperties.logInitializationFailedIsIgnored(t);
                basePackages = ILockCollectionFactory.getInstance(false).newLinkedSet(Arrays.asList("de.invesdwin"));
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
