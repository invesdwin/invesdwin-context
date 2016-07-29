package de.invesdwin.context.beans.hook;

/**
 * Gets called before the spring contexts are collected from the classpath.
 * 
 * Spring beans with this annotation are automatically registered. Else for normal classes
 * PreStartupHookManager.register() must be called manually.
 */
public interface IPreStartupHook {

    void preStartup() throws Exception;

}
