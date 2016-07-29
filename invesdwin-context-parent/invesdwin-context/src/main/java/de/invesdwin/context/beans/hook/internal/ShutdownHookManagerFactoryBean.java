package de.invesdwin.context.beans.hook.internal;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.invesdwin.util.shutdown.IShutdownHook;
import de.invesdwin.util.shutdown.ShutdownHookManager;

/**
 * Registers an internal Thread as a ShutdownHook in the JVM that runs the given callback on shutdown.
 * 
 * @author subes
 */
@ThreadSafe
@Named
public final class ShutdownHookManagerFactoryBean implements ApplicationContextAware, FactoryBean<ShutdownHookManager> {

    private static final ShutdownHookManager INSTANCE = ShutdownHookManager.INSTANCE;

    private ShutdownHookManagerFactoryBean() {}

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        for (final IShutdownHook hook : applicationContext.getBeansOfType(IShutdownHook.class).values()) {
            ShutdownHookManager.register(hook);
        }
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public Class<?> getObjectType() {
        return INSTANCE.getClass();
    }

    @Override
    public ShutdownHookManager getObject() throws Exception {
        return INSTANCE;
    }

}
