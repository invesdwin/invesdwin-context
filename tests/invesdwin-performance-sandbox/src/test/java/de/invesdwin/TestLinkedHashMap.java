package de.invesdwin;

import static java.util.Arrays.stream;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import com.carrotsearch.hppc.ObjectObjectHashMap;

import de.invesdwin.util.time.Instant;

@NotThreadSafe
//CHECKSTYLE:OFF
public class TestLinkedHashMap extends AbstractPerformanceTest {
    private static final int REPETITIONS = 1000;
    private static final int TIMES = 10000;
    private static final int MAX = 5000000;
    private static final double ELEMENTS_SIZE;

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

    public int linkedHashMap() {
        final Map<Long, Double> map = new LinkedHashMap<Long, Double>();
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
            map.remove(il);
        }
        for (final Long o : remove) {
            map.remove(o);
        }
        return r + map.size();
    }

    public int guavaCompactLinkedHashMap() {
        final Map<Long, Double> map = new com.google.common.collect.GuavaCompactLinkedHashMap<>();
        return test(map);
    }

    public int fastUtilLinkedHashMap() {
        // fair comparison -- growing table
        final Map<Long, Double> map = new it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap<>();
        return test(map);
    }

    public static void main(final String[] argv) {
        testSize();
        testRuntime();
    }

    private static void testRuntime() {
        final TestLinkedHashMap test = new TestLinkedHashMap();
        testRuntime("linkedHashMap", test::linkedHashMap);
        testRuntime("guavaCompactLinkedHashMap", test::guavaCompactLinkedHashMap);
        testRuntime("fastUtilLinkedHashMap", test::fastUtilLinkedHashMap);
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
        final LinkedHashMap hashMap = new LinkedHashMap();
        testSize("LinkedHashMap", hashMap);
        final com.google.common.collect.GuavaCompactLinkedHashMap guavaCompactHashMap = new com.google.common.collect.GuavaCompactLinkedHashMap();
        testSize("GuavaCompactLinkedHashMap", guavaCompactHashMap);
        final it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap fastUtilHashMap = new it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap();
        testSize("FastUtilLinkedHashMap", fastUtilHashMap);
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