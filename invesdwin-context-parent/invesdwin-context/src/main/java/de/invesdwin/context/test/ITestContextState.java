package de.invesdwin.context.test;

public interface ITestContextState {

    /**
     * Tells if all parallel running test classes that used this context have been through tearDownOnce and thus won't
     * use this context anymore.
     */
    boolean isFinishedContext();

    /**
     * Tells if there are no more parallel test classes active over all contexts.
     */
    boolean isFinishedGlobal();

}
