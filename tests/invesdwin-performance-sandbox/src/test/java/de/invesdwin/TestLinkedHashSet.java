package de.invesdwin;

import static java.util.Arrays.stream;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.collect.GuavaCompactLinkedHashSet;

import de.invesdwin.util.time.Instant;

@NotThreadSafe
//CHECKSTYLE:OFF
public class TestLinkedHashSet extends AbstractPerformanceTest {
    private static final int REPETITIONS = 1000;
    private static final int TIMES = 10000;
    private static final int MAX = 5000000;
    private static final long ELEMENTS_SIZE;

    private static Long[] add = new Long[TIMES], lookup = new Long[TIMES], remove = new Long[TIMES];
    static {
        for (int ix = 0; ix < TIMES; ix++) {
            add[ix] = new Long(Math.round(Math.random() * MAX));
        }
        ELEMENTS_SIZE = stream(add).distinct().count() * measureHeapSize(add[0]);
        for (int ix = 0; ix < TIMES; ix++) {
            lookup[ix] = new Long(Math.round(Math.random() * MAX));
        }
        for (int ix = 0; ix < TIMES; ix++) {
            remove[ix] = new Long(Math.round(Math.random() * MAX));
        }
    }

    public int linkedHashSet() {
        final Set<Long> set = new LinkedHashSet<Long>();
        return test(set);
    }

    private int test(final Set<Long> set) {
        for (final Long o : add) {
            set.add(o);
        }
        int r = 0;
        for (final Long o : lookup) {
            r ^= set.contains(o) ? 1 : 0;
        }
        for (final Long o : set) {
            r += o.intValue();
        }
        for (final Long o : remove) {
            set.remove(o);
        }
        return r + set.size();
    }

    public int guavaCompactLinkedHashSet() {
        final Set<Long> set = new com.google.common.collect.GuavaCompactLinkedHashSet<>();
        return test(set);
    }

    public int fastUtilLinkedHashSet() {
        // fair comparison -- growing table
        final Set<Long> set = new it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet<>();
        return test(set);
    }

    public static void main(final String[] argv) {
        testSize();
        testRuntime();
    }

    private static void testRuntime() {
        final TestLinkedHashSet test = new TestLinkedHashSet();
        testRuntime("linkedHashSet", test::linkedHashSet);
        testRuntime("guavaCompactLinkedHashSet", test::guavaCompactLinkedHashSet);
        testRuntime("fastUtilLinkedHashSet", test::fastUtilLinkedHashSet);
    }

    private static void testRuntime(final String string, final Runnable object) {
        final Instant overall = new Instant();
        for (int i = 0; i < REPETITIONS; i++) {
            //Instant instant = new Instant();
            object.run();
            //System.out.println(i+". "+string+": "+instant);
        }
        System.out.println(string + ": " + overall);
    }

    private static void testSize() {
        final LinkedHashSet hashSet = new LinkedHashSet();
        testSize("LinkedHashSet", hashSet, hashSet::add);
        final GuavaCompactLinkedHashSet guavaCompactLinkedHashSet = new GuavaCompactLinkedHashSet<>();
        testSize("GuavaCompactLinkedHashSet", guavaCompactLinkedHashSet, guavaCompactLinkedHashSet::add);
        final it.unimi.dsi.fastutil.objects.ObjectOpenHashSet fastUtilHashSet = new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet();
        testSize("FastUtilLinkedHashSet", fastUtilHashSet, fastUtilHashSet::add);
    }

    public static void testSize(final String name, final Object set, final Consumer setAdd) {
        for (final Long o : add) {
            setAdd.accept(o);
        }
        System.out.printf("%s: %.1f bytes per element\n", name, ((measureHeapSize(set) - ELEMENTS_SIZE) * 1.0 / TIMES));

    }
}