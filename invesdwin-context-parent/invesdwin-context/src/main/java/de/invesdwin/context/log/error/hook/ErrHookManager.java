package de.invesdwin.context.log.error.hook;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.invesdwin.context.log.error.LoggedRuntimeException;
import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
@Named
public final class ErrHookManager implements ApplicationContextAware, FactoryBean<ErrHookManager> {

    private static final ErrHookManager INSTANCE = new ErrHookManager();
    @GuardedBy("INSTANCE")
    private static final Set<IErrHook> REGISTRIERTE_HOOKS = new HashSet<IErrHook>();

    private ErrHookManager() {}

    public static void register(final IErrHook hook) {
        synchronized (INSTANCE) {
            Assertions.assertThat(REGISTRIERTE_HOOKS.add(hook))
                    .as("Hook [%s] has already been registered!", hook)
                    .isTrue();
        }
    }

    public static void unregister(final IErrHook hook) {
        synchronized (INSTANCE) {
            Assertions.assertThat(REGISTRIERTE_HOOKS.remove(hook))
                    .as("Hook [%s] has not been registered yet!", hook)
                    .isTrue();
        }
    }

    public static void loggedException(final LoggedRuntimeException e, final boolean uncaughtException) {
        synchronized (INSTANCE) {
            for (final IErrHook hook : REGISTRIERTE_HOOKS) {
                hook.loggedException(e, uncaughtException);
            }
        }
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        synchronized (INSTANCE) {
            for (final IErrHook hook : applicationContext.getBeansOfType(IErrHook.class).values()) {
                register(hook);
            }
        }
    }

    @Override
    public ErrHookManager getObject() throws Exception {
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
