package de.invesdwin.context.beans.init;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
class ExtendedConstructorFinishedBean extends ConstructorFinishedBean {

    private final boolean constructorInitialized;
    private boolean extendedInitialized;

    ExtendedConstructorFinishedBean() {
        this.constructorInitialized = true;
    }

    @Override
    public void test() {
        super.test();
        Assertions.assertThat(extendedInitialized).isTrue();
    }

    @Override
    public void onConstructorFinished() {
        super.onConstructorFinished();
        Assertions.assertThat(constructorInitialized).isTrue();
        extendedInitialized = true;
    }

}
