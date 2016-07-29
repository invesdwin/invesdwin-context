package de.invesdwin.context.beans.init;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;

import de.invesdwin.util.assertions.Assertions;

@Configurable
@NotThreadSafe
class ConfigurableBean implements InitializingBean {

    @Inject
    private TestBean bean;
    private boolean initialized;

    public void test() {
        Assertions.assertThat(bean).isNotNull();
        Assertions.assertThat(initialized).isTrue();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initialized = true;
    }

}
