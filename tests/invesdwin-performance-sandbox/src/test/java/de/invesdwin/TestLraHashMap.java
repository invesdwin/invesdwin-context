package de.invesdwin;

import static java.util.Arrays.stream;

import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import com.carrotsearch.hppc.ObjectObjectHashMap;

import de.invesdwin.util.collections.eviction.ArrayLeastRecentlyAddedMap;
import de.invesdwin.util.collections.eviction.CommonsLeastRecentlyAddedMap;
import de.invesdwin.util.collections.eviction.CommonsLeastRecentlyModifiedMap;
import de.invesdwin.util.collections.eviction.CommonsLeastRecentlyUsedMap;
import de.invesdwin.util.collections.eviction.JavaLeastRecentlyUsedMap;
import de.invesdwin.util.time.Instant;

@NotThreadSafe
//CHECKSTYLE:OFF
public class TestLraHashMap extends AbstractPerformanceTest {
    private static final int REPETITIONS = 1000;
    private static final int TIMES = 10000;
    private static final int MAXIMUM_SIZE = TIMES / 10;
    private static final int MAX = 5000000;
    private static final double ELEMENTS_SIZE;

    private static Long[] add = new Long[TIMES], lookup = new Long[TIMES], remove = new Long[TIMES];
    private final boolean removeEnabled = true;
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

    public int lruMap() {
        final Map<Long, Double> map = new CommonsLeastRecentlyUsedMap<>(MAXIMUM_SIZE);
        return test(map);
    }

    private int test(final Map<Long, Double> map) {
        for (final Long o : add) {
            map.put(o, o.doubleValue());
        }
        int r = 0;
        for (final Long o : lookup) {
            r ^= map.get(o) != null ? 1 : 0;
        }
        for (int i = 0; i < TIMES; i++) {
            final long il = i;
            final double id = i;
            map.put(il, id);
            map.get(il);
        }
        if (removeEnabled) {
            for (final Long o : remove) {
                map.remove(o);
            }
        }
        return r + map.size();
    }

    public int lrmMap() {
        final Map<Long, Double> map = new CommonsLeastRecentlyModifiedMap<>(MAXIMUM_SIZE);
        return test(map);
    }

    public int lraMap() {
        final Map<Long, Double> map = new CommonsLeastRecentlyAddedMap<>(MAXIMUM_SIZE);
        return test(map);
    }

    public int javaLruMap() {
        final Map<Long, Double> map = new JavaLeastRecentlyUsedMap<Long, Double>(MAXIMUM_SIZE);
        return test(map);
    }

    public int arrayListLraMap() {
        final ArrayListLraMap<Long, Double> map = new ArrayListLraMap<>(MAXIMUM_SIZE);
        return test(map);
    }

    public int arrayCustomLraMap() {
        final ArrayCustomLraMap<Long, Double> map = new ArrayCustomLraMap<>(MAXIMUM_SIZE);
        return test(map);
    }

    public int arrayLraMap() {
        final ArrayLeastRecentlyAddedMap<Long, Double> map = new ArrayLeastRecentlyAddedMap<>(MAXIMUM_SIZE);
        return test(map);
    }

    public int linkedListLraMap() {
        final LinkedListLraMap<Long, Double> map = new LinkedListLraMap<>(MAXIMUM_SIZE);
        return test(map);
    }

    public int linkedHashSetLraMap() {
        final LinkedHashSetLraMap<Long, Double> map = new LinkedHashSetLraMap<>(MAXIMUM_SIZE);
        return test(map);
    }

    public static void main(final String[] argv) {
        testSize();
        testRuntime();
    }

    private static void testRuntime() {
        final TestLraHashMap test = new TestLraHashMap();
        testRuntime("lruMap", test::lruMap);
        testRuntime("lrmMap", test::lrmMap);
        testRuntime("lraMap", test::lraMap);
        testRuntime("javaLruMap", test::javaLruMap);
        testRuntime("arrayListLraMap", test::arrayListLraMap);
        testRuntime("arrayLraMap", test::arrayLraMap);
        testRuntime("arrayCustomLraMap", test::arrayCustomLraMap);
        testRuntime("linkedListLraMap", test::linkedListLraMap);
        testRuntime("linkedHashSetLraMap", test::linkedHashSetLraMap);
    }

    private static void testRuntime(final String string, final Runnable object) {
        final Instant overall = new Instant();
        for (int i = 0; i < REPETITIONS; i++) {
            //Instant instant = new Instant();
            object.run();
            //System.out.println(i + ". " + string + ": " + instant);
        }
        System.out.println(string + ": " + overall);
    }

    private static void testSize() {
        final CommonsLeastRecentlyUsedMap lruMap = new CommonsLeastRecentlyUsedMap<>(MAXIMUM_SIZE);
        testSize("lruMap", lruMap);
        final CommonsLeastRecentlyModifiedMap lrmMap = new CommonsLeastRecentlyModifiedMap<>(MAXIMUM_SIZE);
        testSize("lrmMap", lrmMap);
        final CommonsLeastRecentlyAddedMap lraMap = new CommonsLeastRecentlyAddedMap<>(MAXIMUM_SIZE);
        testSize("lraMap", lraMap);
        final JavaLeastRecentlyUsedMap<Long, Double> javaLruMap = new JavaLeastRecentlyUsedMap(MAXIMUM_SIZE);
        testSize("javaLruMap", javaLruMap);
        final ArrayListLraMap<Long, Double> arrayListLraMap = new ArrayListLraMap<>(MAXIMUM_SIZE);
        testSize("arrayListLraMap", arrayListLraMap);
        final ArrayCustomLraMap<Long, Double> arrayCustomLraMap = new ArrayCustomLraMap<>(MAXIMUM_SIZE);
        testSize("arrayCustomLraMap", arrayCustomLraMap);
        final ArrayLeastRecentlyAddedMap<Long, Double> arrayLraMap = new ArrayLeastRecentlyAddedMap<>(MAXIMUM_SIZE);
        testSize("arrayLraMap", arrayLraMap);
        final LinkedListLraMap<Long, Double> linkedListLraMap = new LinkedListLraMap<>(MAXIMUM_SIZE);
        testSize("linkedListLraMap", linkedListLraMap);
        final LinkedHashSetLraMap<Long, Double> linkedHashSetLraMap = new LinkedHashSetLraMap<>(MAXIMUM_SIZE);
        testSize("linkedHashSetLraMap", linkedHashSetLraMap);
    }

    private static void testSize(final String name, final ObjectObjectHashMap map) {
        for (final Long o : add) {
            map.put(o, o.doubleValue());
        }
        final double size = map.size();
        final double elementsSize = ELEMENTS_SIZE / TIMES * size;
        System.out.printf("%s: %.1f bytes per element\n", name, ((measureHeapSize(map) - elementsSize) * 1.0 / size));
    }

    private static void testSize(final String name, final Map map) {
        for (final Long o : add) {
            map.put(o, o.doubleValue());
        }
        final double size = map.size();
        final double elementsSize = ELEMENTS_SIZE / TIMES * size;
        System.out.printf("%s: %.1f bytes per element\n", name, ((measureHeapSize(map) - elementsSize) * 1.0 / size));
    }
}