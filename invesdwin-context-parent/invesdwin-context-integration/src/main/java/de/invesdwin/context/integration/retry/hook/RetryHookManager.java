package de.invesdwin.context.integration.retry.hook;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.invesdwin.context.integration.retry.hook.internal.BroadcastingRetryHook;
import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
@Named
public final class RetryHookManager implements ApplicationContextAware, FactoryBean<RetryHookManager> {

    private static final RetryHookManager INSTANCE = new RetryHookManager();
    private static final BroadcastingRetryHook REGISTERED_HOOKS = new BroadcastingRetryHook();

    static {
        register(new LoggingRetryHook());
    }

    private RetryHookManager() {}

    public static void register(final IRetryHook hook) {
        Assertions.assertThat(REGISTERED_HOOKS.add(hook)).as("Hook [%s] has already been registered!", hook).isTrue();
    }

    public static void unregister(final IRetryHook hook) {
        Assertions.assertThat(REGISTERED_HOOKS.remove(hook)).as("Hook [%s] was never registered!", hook).isTrue();
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        for (final IRetryHook hook : applicationContext.getBeansOfType(IRetryHook.class).values()) {
            register(hook);
        }
    }

    public static IRetryHook getEventTrigger() {
        return REGISTERED_HOOKS;
    }

    @Override
    public RetryHookManager getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return INSTANCE.getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}