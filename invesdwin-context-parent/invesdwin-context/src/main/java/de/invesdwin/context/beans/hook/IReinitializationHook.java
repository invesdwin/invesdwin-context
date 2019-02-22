package de.invesdwin.context.beans.hook;

/**
 * Gets called while the MergedContext gets reinitialized, e.g. during test case context reinitialization.
 *
 */
public interface IReinitializationHook {

    void reinitializationStarted();

    void reinitializationFinished();

    void reinitializationFailed();

}
