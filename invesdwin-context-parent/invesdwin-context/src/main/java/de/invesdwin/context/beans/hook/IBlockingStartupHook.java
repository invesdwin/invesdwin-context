package de.invesdwin.context.beans.hook;

/**
 * The same as IStartupHook with the only difference being that it will block the bootstrap process until this hook is
 * finished.
 *
 */
public interface IBlockingStartupHook extends IStartupHook {

}
