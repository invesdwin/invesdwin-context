package de.invesdwin.context.beans.init;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;
import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterAll;
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

    @AfterAll
    public static synchronized void testCalls() {
        Assertions.assertThat(TestTest.setUpOnceCalled).isEqualTo(1);
        Assertions.assertThat(TestTest.afterPropertiesSetCalled).isEqualTo(4);
    }

    @Test
    public void testDependencyInjectionInTC() {
        new ExtendedConfigurableBean().test();
        Assertions.assertThat(duplicates.length).isEqualTo(2);
    }

    @Test
    public void testConstructorFinishedHook() {
        new ExtendedConstructorFinishedBean().test();
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
