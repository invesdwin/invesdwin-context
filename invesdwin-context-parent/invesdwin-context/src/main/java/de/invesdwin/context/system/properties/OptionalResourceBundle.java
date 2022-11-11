package de.invesdwin.context.system.properties;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class OptionalResourceBundle extends ResourceBundle {

    private final ResourceBundle delegate;

    public OptionalResourceBundle(final ResourceBundle delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Object handleGetObject(final String key) {
        if (key == null) {
            return null;
        }
        if (delegate == null) {
            return key;
        }
        try {
            return delegate.getObject(key);
        } catch (final MissingResourceException e) {
            return key;
        }
    }

    @Override
    public Enumeration<String> getKeys() {
        return delegate.getKeys();
    }

}
