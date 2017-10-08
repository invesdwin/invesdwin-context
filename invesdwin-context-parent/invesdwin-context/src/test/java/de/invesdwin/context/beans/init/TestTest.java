package de.invesdwin.context.beans.init;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;

import org.junit.AfterClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.InitializingBean;

import de.invesdwin.context.beans.init.duplicate.IBeanWithSameClassName;
import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class TestTest extends ATest implements InitializingBean {

    @GuardedBy("TestTest.class")
    private static int afterPropertiesSetCalled;
    @GuardedBy("TestTest.class")
    private static int setUpOnceCalled;
    @GuardedBy("TestTest.class")
    private static int tearDownOnceCalled;

    @Inject
    private IBeanWithSameClassName[] duplicates;

    @Test
    public void provokeSetUpOnce() {
        Assertions.assertThat(true).isTrue(); //just provoke a junit reinit
    }

    @Test
    public void provokeAfterPropertiesSet() {
        Assertions.assertThat(true).isTrue(); //just provoke a junit reinit
    }

    @AfterClass
    public static synchronized void testCalls() {
        Assertions.assertThat(TestTest.setUpOnceCalled).isEqualTo(1);
        Assertions.assertThat(TestTest.afterPropertiesSetCalled).isEqualTo(3);
    }

    @Test
    public void testDependencyInjectionInTC() {
        new ConfigurableBean().test();
        Assertions.assertThat(duplicates.length).isEqualTo(2);
    }

    @Override
    public void setUpOnce() throws Exception {
        super.setUpOnce();
        synchronized (TestTest.class) {
            TestTest.setUpOnceCalled++;
        }
    }

    @Override
    public void tearDownOnce() throws Exception {
        super.tearDownOnce();
        synchronized (TestTest.class) {
            Assertions.assertThat(TestTest.tearDownOnceCalled).isZero();
            TestTest.tearDownOnceCalled++;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        synchronized (TestTest.class) {
            TestTest.afterPropertiesSetCalled++;
        }
    }

}
