package de.invesdwin;

import static java.util.Arrays.stream;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.Instant;

@NotThreadSafe
//CHECKSTYLE:OFF
public class TestHashSet extends AbstractPerformanceTest {
    private static final int REPETITIONS = 1000;
    private static final int TIMES = 100000;
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

    public int hashSet() {
        final Set<Long> set = new HashSet<Long>();
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
        //        for (Long o : remove) {
        //            set.remove(o);
        //        }
        return r + set.size();
    }

    private int test(final com.carrotsearch.hppc.ObjectHashSet<Long> set) {
        for (final Long o : add) {
            set.add(o);
        }
        int r = 0;
        for (final Long o : lookup) {
            r ^= set.contains(o) ? 1 : 0;
        }
        for (final com.carrotsearch.hppc.cursors.ObjectCursor<Long> cur : set) {
            r += cur.value.intValue();
        }
        //        for (Long o : remove) {
        //            set.remove(o);
        //        }
        return r + set.size();
    }

    public int compactHashSet() {
        final Set<Long> set = new net.ontopia.utils.CompactHashSet<Long>();
        return test(set);
    }

    public int hppcSet() {
        final com.carrotsearch.hppc.ObjectHashSet<Long> set = new com.carrotsearch.hppc.ObjectHashSet<Long>();
        return test(set);
    }

    public int kolobokeSet() {
        final Set<Long> set = com.koloboke.collect.set.hash.HashObjSets.newMutableSet();
        return test(set);
    }

    public int guavaCompactHashSet() {
        final Set<Long> set = new com.google.common.collect.GuavaCompactHashSet<>();
        return test(set);
    }

    public int fastUtilHashSet() {
        // fair comparison -- growing table
        final Set<Long> set = new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet<>();
        return test(set);
    }

    public int troveHashSet() {
        // fair comparison -- growing table
        final Set<Long> set = new gnu.trove.set.hash.THashSet<>();
        return test(set);
    }

    public static void main(final String[] argv) {
        testSize();
        testRuntime();
    }

    private static void testRuntime() {
        final TestHashSet test = new TestHashSet();
        testRuntime("hashSet", test::hashSet);
        testRuntime("compactHashSet", test::compactHashSet);
        testRuntime("hppcSet", test::hppcSet);
        testRuntime("kolobokeSet", test::kolobokeSet);
        testRuntime("guavaCompactHashSet", test::guavaCompactHashSet);
        testRuntime("fastUtilHashSet", test::fastUtilHashSet);
        testRuntime("troveHashSet", test::troveHashSet);
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
        final HashSet hashSet = new HashSet();
        testSize("HashSet", hashSet, hashSet::add);
        final net.ontopia.utils.CompactHashSet compactHashSet = new net.ontopia.utils.CompactHashSet();
        testSize("CompactHashSet", compactHashSet, compactHashSet::add);
        final com.koloboke.collect.set.hash.HashObjSet<Object> kolobokeSet = com.koloboke.collect.set.hash.HashObjSets
                .newMutableSet();
        testSize("KolobokeSet", kolobokeSet, kolobokeSet::add);
        final com.carrotsearch.hppc.ObjectHashSet hppcSet = new com.carrotsearch.hppc.ObjectHashSet();
        testSize("HPPC set", hppcSet, hppcSet::add);
        final com.google.common.collect.GuavaCompactHashSet guavaCompactHashSet = new com.google.common.collect.GuavaCompactHashSet();
        testSize("GuavaCompactHashSet", guavaCompactHashSet, guavaCompactHashSet::add);
        final it.unimi.dsi.fastutil.objects.ObjectOpenHashSet fastUtilHashSet = new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet();
        testSize("FastUtilHashSet", fastUtilHashSet, fastUtilHashSet::add);
        final gnu.trove.set.hash.THashSet troveHashSet = new gnu.trove.set.hash.THashSet();
        testSize("TroveHashSet", troveHashSet, troveHashSet::add);
    }

    public static void testSize(final String name, final Object set, final Consumer setAdd) {
        for (final Long o : add) {
            setAdd.accept(o);
        }
        System.out.printf("%s: %.1f bytes per element\n", name, ((measureHeapSize(set) - ELEMENTS_SIZE) * 1.0 / TIMES));

    }
}