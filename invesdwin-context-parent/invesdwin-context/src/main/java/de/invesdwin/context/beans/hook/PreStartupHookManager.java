package de.invesdwin.context.beans.hook;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.assertions.Assertions;

/**
 * Registers hooks for the application start. The hooks are called only once before the merged ApplicationContext has
 * been initialized.
 * 
 * To avoid deadlocks with static initializers, all hooks are called by the same thread sequencially.
 * 
 * @author subes
 * 
 */
@ThreadSafe
@Named
public final class PreStartupHookManager implements ApplicationContextAware, FactoryBean<PreStartupHookManager> {

    private static final PreStartupHookManager INSTANCE = new PreStartupHookManager();
    @GuardedBy("INSTANCE")
    private static final Set<IPreStartupHook> REGISTERED_HOOKS = new HashSet<IPreStartupHook>();
    @GuardedBy("INSTANCE")
    private static boolean alreadyStarted;

    private PreStartupHookManager() {}

    public static void register(final IPreStartupHook hook) {
        synchronized (INSTANCE) {
            Assertions.assertThat(alreadyStarted)
                    .as("%s cannot be registered after application start!", IPreStartupHook.class.getSimpleName())
                    .isFalse();
            Assertions.assertThat(REGISTERED_HOOKS.add(hook))
                    .as("Hook [%s] has already been registered!", hook)
                    .isTrue();
        }
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        synchronized (INSTANCE) {
            for (final IPreStartupHook hook : applicationContext.getBeansOfType(IPreStartupHook.class).values()) {
                if (!alreadyStarted) {
                    register(hook);
                }
            }
        }
    }

    public void start() {
        synchronized (INSTANCE) {
            Assertions.assertThat(alreadyStarted)
                    .as("%s may only be started once initially!", IPreStartupHook.class.getSimpleName())
                    .isFalse();
            PreStartupHookManager.alreadyStarted = true;
            for (final IPreStartupHook hook : REGISTERED_HOOKS) {
                try {
                    hook.preStartup();
                } catch (final Exception e) {
                    Err.process(e);
                }
            }
        }
    }

    @Override
    public PreStartupHookManager getObject() throws Exception {
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