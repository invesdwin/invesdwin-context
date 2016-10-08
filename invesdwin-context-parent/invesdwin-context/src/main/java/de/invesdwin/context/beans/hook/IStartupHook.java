package de.invesdwin.context.beans.hook;

/**
 * Gets called after the merged ApplicationContext is initialized.
 * 
 * Spring beans with this annotation are automatically registered. Else for normal classes StartupHookManager.register()
 * must be called manually.
 * 
 * This hook is non-blocking and can run endlessly.
 * 
 * @author subes
 * 
 */
public interface IStartupHook {

    void startup() throws Exception;
}
