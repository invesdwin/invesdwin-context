package de.invesdwin.context.beans.init;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
class ExtendedConfigurableBean extends ConfigurableBean {

    private final boolean constructorInitialized;
    private boolean extendedInitialized;

    ExtendedConfigurableBean() {
        this.constructorInitialized = true;
    }

    @Override
    public void test() {
        super.test();
        Assertions.assertThat(extendedInitialized).isTrue();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assertions.assertThat(constructorInitialized).isTrue();
        extendedInitialized = true;
    }

}
