package de.invesdwin.context.system.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.concurrent.Immutable;

import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;

@Immutable
public final class ResourceBundles {

    private ResourceBundles() {}

    public static ResourceBundle getResourceBundle(final Class<?> clazz) {
        return getResourceBundle(clazz, null);
    }

    public static ResourceBundle getResourceBundle(final Class<?> clazz, final Locale locale) {
        final List<String> bundleNames = new ArrayList<String>();
        Class<?> parent = clazz;
        while (parent != null) {
            bundleNames.add(parent.getName().replace(".", "/"));
            parent = parent.getSuperclass();
        }
        final AggregateResourceBundleLocator locator = new AggregateResourceBundleLocator(bundleNames);
        final Locale actualLocale;
        if (locale != null) {
            actualLocale = locale;
        } else {
            actualLocale = Locale.getDefault();
        }
        return new OptionalResourceBundle(locator.getResourceBundle(actualLocale));
    }
}
