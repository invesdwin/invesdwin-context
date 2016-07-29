package de.invesdwin.context.beans.hook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;

/**
 * Registers hooks for the application start. The hooks are called only once after the merged ApplicationContext has
 * been initialized.
 * 
 * These hooks are started in their own seperate threads to speedup the startup process and allow multiple background
 * processes while the application runs.
 * 
 * @author subes
 * 
 */
@ThreadSafe
@Named
public final class StartupHookManager implements ApplicationContextAware, FactoryBean<StartupHookManager> {

    private static final StartupHookManager INSTANCE = new StartupHookManager();
    @GuardedBy("INSTANCE")
    private static final Set<IStartupHook> REGISTERED_HOOKS = new HashSet<IStartupHook>();
    @GuardedBy("INSTANCE")
    private static boolean alreadyStarted;
    @GuardedBy("INSTANCE")
    private static List<IStartupHook> queuedHooksForContextReinitialization;

    private StartupHookManager() {}

    public static void registerOrCall(final IStartupHook hook) {
        synchronized (INSTANCE) {
            if (alreadyStarted) {
                if (queuedHooksForContextReinitialization != null) {
                    queuedHooksForContextReinitialization.add(hook);
                } else {
                    try {
                        hook.startup();
                    } catch (final Exception e) {
                        throw Err.process(e);
                    }
                }
            } else {
                register(hook);
            }
        }
    }

    public static void reinitializationStarted() {
        synchronized (INSTANCE) {
            if (alreadyStarted) {
                Assertions.assertThat(queuedHooksForContextReinitialization).isNull();
                queuedHooksForContextReinitialization = new ArrayList<IStartupHook>();
            }
        }
    }

    public static void reinitializationFinished() {
        synchronized (INSTANCE) {
            if (queuedHooksForContextReinitialization != null) {
                for (final IStartupHook hook : queuedHooksForContextReinitialization) {
                    try {
                        hook.startup();
                    } catch (final Exception e) {
                        throw Err.process(e);
                    }
                }
                queuedHooksForContextReinitialization = null;
            }
        }
    }

    public static void reinitializationFailed() {
        synchronized (INSTANCE) {
            queuedHooksForContextReinitialization = null;
        }
    }

    public static void register(final IStartupHook hook) {
        synchronized (INSTANCE) {
            Assertions.assertThat(alreadyStarted)
            .as("%s cannot be registered after application start!", IStartupHook.class.getSimpleName())
            .isFalse();
            Assertions.assertThat(REGISTERED_HOOKS.add(hook)).as("Hook [%s] has already been registered!", hook).isTrue();
        }
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        synchronized (INSTANCE) {
            for (final IStartupHook hook : applicationContext.getBeansOfType(IStartupHook.class).values()) {
                if (!alreadyStarted) {
                    register(hook);
                }
            }
        }
    }

    public void start() {
        synchronized (INSTANCE) {
            Assertions.assertThat(alreadyStarted)
            .as("%s may only be started once initially!", IStartupHook.class.getSimpleName())
            .isFalse();
            alreadyStarted = true;
            final WrappedExecutorService executor = Executors.newFixedThreadPool(getClass().getSimpleName(),
                    Math.max(10, Executors.getCpuThreadPoolCount() * 2));
            for (final IStartupHook hook : REGISTERED_HOOKS) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            hook.startup();
                        } catch (final Exception e) {
                            Err.process(e);
                        }
                    };
                });
            }
            executor.shutdown();
        }
    }

    @Override
    public StartupHookManager getObject() throws Exception {
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