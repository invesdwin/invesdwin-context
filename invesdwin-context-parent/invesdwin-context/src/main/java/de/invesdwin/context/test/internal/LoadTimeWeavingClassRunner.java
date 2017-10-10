package de.invesdwin.context.test.internal;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.invesdwin.context.beans.init.PreMergedContext;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class LoadTimeWeavingClassRunner extends SpringJUnit4ClassRunner {

    static {
        Assertions.assertThat(PreMergedContext.getInstance()).isNotNull();
    }

    public LoadTimeWeavingClassRunner(final Class<?> clazz) throws InitializationError {
        super(clazz);
    }

}
