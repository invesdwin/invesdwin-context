package de.invesdwin.context.beans.init;

import javax.annotation.concurrent.NotThreadSafe;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.springframework.beans.factory.InitializingBean;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
@Named
class TestBean implements ITestBean, InitializingBean {

    private boolean initialized;
    @Inject
    private TestBeanToo too;

    public void test() throws InterruptedException {
        Assertions.assertThat(initialized).isTrue();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assertions.assertThat(too).isNotNull();
        initialized = true;
    }

    public TestBeanToo getToo() {
        return too;
    }

}
