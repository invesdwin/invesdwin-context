package de.invesdwin.context.system;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Ignore;
import org.junit.Test;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.Decimal;

@ThreadSafe
public class SerializingSoftReferenceTest extends ATest {

    @Test
    public void testSerialization() {
        final SerializingSoftReference<Decimal> ref = new SerializingSoftReference<Decimal>(new Decimal("100"));
        Assertions.assertThat(ref.get()).isNotNull();
        ref.clear();
        Assertions.assertThat(ref.get()).isNotNull();
        ref.close();
        Assertions.assertThat(ref.get()).isNull();
    }

    @Test
    @Ignore("manual test")
    public void testOutOfMemory() {
        final Set<SerializingSoftReference<Decimal>> refs = new LinkedHashSet<SerializingSoftReference<Decimal>>();
        Decimal curValue = Decimal.ZERO;
        while (true) {
            curValue = curValue.add(Decimal.ONE);
            final SerializingSoftReference<Decimal> ref = new SerializingSoftReference<Decimal>(curValue);
            refs.add(ref);
            Assertions.assertThat(ref.get()).isNotNull();
        }
    }

}
