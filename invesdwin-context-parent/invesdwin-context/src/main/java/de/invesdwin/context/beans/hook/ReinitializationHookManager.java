package de.invesdwin.context.beans.hook;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import jakarta.inject.Named;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
@Named
public final class ReinitializationHookManager
        implements ApplicationContextAware, FactoryBean<ReinitializationHookManager> {

    private static final ReinitializationHookManager INSTANCE = new ReinitializationHookManager();
    @GuardedBy("INSTANCE")
    private static final Set<IReinitializationHook> REGISTERED_HOOKS = new HashSet<IReinitializationHook>();

    private ReinitializationHookManager() {}

    public static void reinitializationStarted() {
        synchronized (INSTANCE) {
            if (StartupHookManager.isAlreadyStarted()) {
                for (final IReinitializationHook hook : REGISTERED_HOOKS) {
                    hook.reinitializationStarted();
                }
            }
        }
    }

    public static void reinitializationFinished() {
        synchronized (INSTANCE) {
            if (StartupHookManager.isAlreadyStarted()) {
                for (final IReinitializationHook hook : REGISTERED_HOOKS) {
                    hook.reinitializationFinished();
                }
            }
        }
    }

    public static void reinitializationFailed() {
        synchronized (INSTANCE) {
            if (StartupHookManager.isAlreadyStarted()) {
                for (final IReinitializationHook hook : REGISTERED_HOOKS) {
                    hook.reinitializationFailed();
                }
            }
        }
    }

    public static void register(final IReinitializationHook hook) {
        synchronized (INSTANCE) {
            Assertions.assertThat(REGISTERED_HOOKS.add(hook))
                    .as("Hook [%s] has already been registered!", hook)
                    .isTrue();
        }
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        synchronized (INSTANCE) {
            if (!StartupHookManager.isAlreadyStarted()) {
                for (final IReinitializationHook hook : applicationContext.getBeansOfType(IReinitializationHook.class)
                        .values()) {
                    register(hook);
                }
            }
        }
    }

    @Override
    public ReinitializationHookManager getObject() throws Exception {
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