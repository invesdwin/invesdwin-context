package de.invesdwin.context.beans.init;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.aspects.IConstructorFinishedHook;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
class ConstructorFinishedBean implements IConstructorFinishedHook {

    private boolean initialized;

    public void test() {
        Assertions.assertThat(initialized).isTrue();
    }

    @Override
    public void onConstructorFinished() {
        initialized = true;
    }

}
