package de.invesdwin;

import static java.util.Arrays.stream;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import com.carrotsearch.hppc.ObjectObjectHashMap;

import de.invesdwin.util.time.Instant;

@NotThreadSafe
//CHECKSTYLE:OFF
public class TestHashMap extends AbstractPerformanceTest {

    private static final int REPETITIONS = 1000;
    private static final int TIMES = 10000;
    private static final int MAX = 5000000;
    private static final double ELEMENTS_SIZE;

    private static Long[] add = new Long[TIMES], lookup = new Long[TIMES], remove = new Long[TIMES];
    private final boolean removeEnabled = false;
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
            if (removeEnabled) {
                map.remove(il);
            }
        }
        if (removeEnabled) {
            for (final Long o : remove) {
                map.remove(o);
            }
        }
        return r + map.size();
    }

    private int test(final com.carrotsearch.hppc.ObjectObjectHashMap<Long, Double> map) {
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
            if (removeEnabled) {
                map.remove(il);
            }
        }
        if (removeEnabled) {
            for (final Long o : remove) {
                map.remove(o);
            }
        }
        return r + map.size();
    }

    public int hashMap() {
        final Map<Long, Double> map = new HashMap<Long, Double>();
        return test(map);
    }

    public int compactHashMap() {
        final Map<Long, Double> map = new net.ontopia.utils.CompactHashMap<Long, Double>();
        return test(map);
    }

    public int hppcMap() {
        final com.carrotsearch.hppc.ObjectObjectHashMap<Long, Double> map = new com.carrotsearch.hppc.ObjectObjectHashMap<Long, Double>();
        return test(map);
    }

    public int kolobokeMap() {
        final Map<Long, Double> map = com.koloboke.collect.map.hash.HashObjObjMaps.newMutableMap();
        return test(map);
    }

    public int guavaCompactHashMap() {
        final Map<Long, Double> map = new com.google.common.collect.GuavaCompactHashMap<>();
        return test(map);
    }

    public int fastUtilHashMap() {
        // fair comparison -- growing table
        final Map<Long, Double> map = new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>();
        return test(map);
    }

    public int troveHashMap() {
        // fair comparison -- growing table
        final Map<Long, Double> map = new gnu.trove.map.hash.THashMap<>();
        return test(map);
    }

    public int smoothieMap() {
        // fair comparison -- growing table
        final Map<Long, Double> map = new net.openhft.smoothie.SmoothieMap<>();
        return test(map);
    }

    public static void main(final String[] argv) {
        testSize();
        testRuntime();
    }

    private static void testRuntime() {
        final TestHashMap test = new TestHashMap();
        testRuntime("hashMap", test::hashMap);
        testRuntime("compactHashMap", test::compactHashMap);
        testRuntime("hppcMap", test::hppcMap);
        testRuntime("kolobokeMap", test::kolobokeMap);
        testRuntime("guavaCompactHashMap", test::guavaCompactHashMap);
        testRuntime("fastUtilHashMap", test::fastUtilHashMap);
        testRuntime("troveHashMap", test::troveHashMap);
        testRuntime("smoothieMap", test::smoothieMap);
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
        final HashMap hashMap = new HashMap();
        testSize("HashMap", hashMap);
        final net.ontopia.utils.CompactHashMap compactHashMap = new net.ontopia.utils.CompactHashMap();
        testSize("CompactHashMap", compactHashMap);
        final com.koloboke.collect.map.hash.HashObjObjMap<Object, Object> kolobokeMap = com.koloboke.collect.map.hash.HashObjObjMaps
                .newMutableMap();
        testSize("KolobokeMap", kolobokeMap);
        final com.carrotsearch.hppc.ObjectObjectHashMap hppcMap = new com.carrotsearch.hppc.ObjectObjectHashMap();
        testSize("HPPC map", hppcMap);
        final com.google.common.collect.GuavaCompactHashMap guavaCompactHashMap = new com.google.common.collect.GuavaCompactHashMap();
        testSize("GuavaCompactHashMap", guavaCompactHashMap);
        final it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap fastUtilHashMap = new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap();
        testSize("FastUtilHashMap", fastUtilHashMap);
        final gnu.trove.map.hash.THashMap troveHashMap = new gnu.trove.map.hash.THashMap();
        testSize("TroveHashMap", troveHashMap);
        final net.openhft.smoothie.SmoothieMap smoothieMap = new net.openhft.smoothie.SmoothieMap();
        testSize("SmoothieMap", smoothieMap);
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