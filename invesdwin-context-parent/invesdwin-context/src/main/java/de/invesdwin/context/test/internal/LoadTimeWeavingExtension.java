package de.invesdwin.context.test.internal;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.invesdwin.context.beans.init.PreMergedContext;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class LoadTimeWeavingExtension extends SpringExtension {

    static {
        Assertions.assertThat(PreMergedContext.getInstance()).isNotNull();
    }

}
