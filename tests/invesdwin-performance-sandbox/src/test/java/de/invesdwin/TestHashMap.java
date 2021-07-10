package de.invesdwin;

import static java.util.Arrays.stream;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import com.carrotsearch.hppc.ObjectObjectHashMap;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.ImmutableHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.internal.IValuesMap;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericFDate;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FDate;

// Openj9 java 8 -Xgcpolicy:gencon
// hashMap: PT10.673.089.868S
// compactHashMap: PT31.682.672.559S
// hppcMap: PT16.570.689.086S
// guavaCompactHashMap: PT9.244.068.963S
// fastUtilHashMap: PT14.604.036.365S
// troveHashMap: PT13.882.609.951S

// Openj9 java 8 -Xgcpolicy:balanced
// hashMap: PT17.586.313.547S
// compactHashMap: PT41.379.089.692S
// hppcMap: PT25.596.774.880S
// guavaCompactHashMap: PT22.646.482.987S
// fastUtilHashMap: PT24.438.371.012S
// troveHashMap: PT21.945.963.103S

// Openj9 java 8 -Xgcpolicy:metronome
// hashMap: PT13.301.320.653S
// compactHashMap: PT36.532.957.555S
// hppcMap: PT20.934.546.699S
// guavaCompactHashMap: PT24.411.726.191S
// fastUtilHashMap: PT19.357.303.449S
// troveHashMap: PT17.859.125.003S

// Openj9 java 8 -Xgcpolicy:optavgpause
// hashMap: PT15.674.841.322S
// compactHashMap: PT35.587.337.904S
// hppcMap: PT19.828.710.327S
// guavaCompactHashMap: PT12.047.931.813S
// fastUtilHashMap: PT17.395.649.212S
// troveHashMap: PT16.604.839.397S

// Openj9 java 8 -Xgcpolicy:optthruput
// hashMap: PT13.619.991.479S
// compactHashMap: PT35.352.615.176S
// hppcMap: PT19.486.721.217S
// guavaCompactHashMap: PT11.752.282.974S
// fastUtilHashMap: PT18.566.279.722S
// troveHashMap: PT17.261.404.424S

// Openj9 java 11 -Xgcpolicy:gencon
// hashMap: PT11.469.006.103S
// compactHashMap: PT33.547.919.810S
// hppcMap: PT17.180.321.014S
// guavaCompactHashMap: PT9.720.283.446S
// fastUtilHashMap: PT15.558.054.552S
// troveHashMap: PT15.160.953.378S

// Openj9 java 11 -Xgcpolicy:balanced
// hashMap: PT13.968.364.439S
// compactHashMap: PT36.150.251.258S
// hppcMap: PT21.988.758.968S
// guavaCompactHashMap: PT12.147.315.991S
// fastUtilHashMap: PT20.839.989.782S
// troveHashMap: PT17.298.240.239S

// Openj9 java 11 -Xgcpolicy:metronome
// hashMap: PT13.985.609.723S
// compactHashMap: PT38.050.801.611S
// hppcMap: PT22.581.615.944S
// guavaCompactHashMap: PT24.993.480.549S
// fastUtilHashMap: PT22.010.727.253S
// troveHashMap: PT19.188.915.066S

// Openj9 java 11 -Xgcpolicy:optavgpause
// hashMap: PT14.810.054.694S
// compactHashMap: PT35.662.968.604S
// hppcMap: PT25.245.937.725S
// guavaCompactHashMap: PT12.559.163.593S
// fastUtilHashMap: PT17.711.671.131S
// troveHashMap: PT16.859.589.138S

// Openj9 java 11 -Xgcpolicy:optthruput
// hashMap: PT12.362.809.375S
// compactHashMap: PT34.113.324.432S
// hppcMap: PT19.570.270.592S
// guavaCompactHashMap: PT11.117.609.098S
// fastUtilHashMap: PT16.828.001.865S
// troveHashMap: PT15.385.554.122S

// Hotspot java 8 -XX:+UseSerialGC
// hashMap: PT11.173.708.773S
// compactHashMap: PT30.152.080.092S
// hppcMap: PT18.938.884.815S
// guavaCompactHashMap: PT9.359.868.062S
// fastUtilHashMap: PT16.400.995.860S
// troveHashMap: PT12.983.279.046S

// Hotspot java 8 -XX:+UseParallelGC
// hashMap: PT10.660.893.704S
// compactHashMap: PT30.389.739.942S
// hppcMap: PT20.196.656.942S
// guavaCompactHashMap: PT9.638.008.614S
// fastUtilHashMap: PT15.447.543.459S
// troveHashMap: PT12.315.306.130S

// Hotspot java 8 -XX:+UseConcMarkSweepGC
// hashMap: PT10.059.356.110S
// compactHashMap: PT30.060.590.340S
// hppcMap: PT19.748.543.779S
// guavaCompactHashMap: PT9.265.152.036S
// fastUtilHashMap: PT16.172.261.943S
// troveHashMap: PT12.643.560.707S

// Hotspot java 8 -XX:+UseG1GC
// hashMap: PT22.277.037.590S
// compactHashMap: PT42.252.736.339S
// hppcMap: PT35.776.199.261S
// guavaCompactHashMap: PT12.360.797.017S
// fastUtilHashMap: PT31.256.247.291S
// troveHashMap: PT15.636.118.622S

// Hotspot java 11 -XX:+UseSerialGC
// hashMap: PT10.399.517.139S
// compactHashMap: PT29.949.991.829S
// hppcMap: PT18.259.987.647S
// guavaCompactHashMap: PT9.076.489.170S
// fastUtilHashMap: PT15.462.131.872S
// troveHashMap: PT12.838.159.361S

// Hotspot java 11 -XX:+UseParallelGC
// hashMap: PT11.180.302.138S
// compactHashMap: PT29.950.683.183S
// hppcMap: PT19.635.354.795S
// guavaCompactHashMap: PT9.116.864.128S
// fastUtilHashMap: PT15.915.601.877S
// troveHashMap: PT12.933.871.414S

// Hotspot java 11 -XX:+UseConcMarkSweepGC
// hashMap: PT12.045.087.460S
// compactHashMap: PT31.893.287.419S
// hppcMap: PT23.397.116.620S
// guavaCompactHashMap: PT9.776.016.608S
// fastUtilHashMap: PT18.160.823.306S
// troveHashMap: PT13.697.234.275S

// Hotspot java 11 -XX:+UseG1GC
// hashMap: PT19.169.524.307S
// compactHashMap: PT40.557.505.254S
// hppcMap: PT31.373.068.214S
// guavaCompactHashMap: PT12.171.967.944S
// fastUtilHashMap: PT26.929.169.933S
// troveHashMap: PT16.152.498.349S

// Hotspot java 11 -XX:+UnlockExperimentalVMOptions -XX:+UseZGC
// hashMap: PT11.443.784.010S
// compactHashMap: PT30.986.300.520S
// hppcMap: PT22.917.488.291S
// guavaCompactHashMap: PT9.635.536.091S
// fastUtilHashMap: PT17.628.276.801S
// troveHashMap: PT12.724.979.248S

// Hotspot java 12 -XX:+UnlockExperimentalVMOptions -XX:+UseShenandoahGC
// hashMap: PT11.991.925.977S
// compactHashMap: PT33.862.227.384S
// hppcMap: PT28.150.080.801S
// guavaCompactHashMap: PT9.442.861.079S
// fastUtilHashMap: PT25.183.109.274S
// troveHashMap: PT16.216.778.572S

@NotThreadSafe
// CHECKSTYLE:OFF
public class TestHashMap extends AbstractPerformanceTest {

    private static final class TestHistoricalCache extends AHistoricalCache<Double> {

        @Override
        protected IEvaluateGenericFDate<Double> newLoadValue() {
            return key -> null;
        }

        @Override
        protected Integer getInitialMaximumSize() {
            return Integer.MAX_VALUE;
        }

        @Override
        public IValuesMap<Double> getValuesMap() {
            return super.getValuesMap();
        }
    }

    private static final int REPETITIONS = 10;
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

    private int test(final TestHistoricalCache map) {
        for (final Long o : add) {
            map.getValuesMap().put(FDate.valueOf(o), ImmutableHistoricalEntry.of(FDate.valueOf(o), o.doubleValue()));
        }
        int r = 0;
        final IHistoricalCacheQuery<Double> query = map.query();
        for (final Long o : lookup) {
            r ^= query.getValue(FDate.valueOf(o)) != null ? 1 : 0;
        }
        for (int i = 0; i < TIMES; i++) {
            final long il = i;
            final double id = i;
            map.getValuesMap().put(FDate.valueOf(il), ImmutableHistoricalEntry.of(FDate.valueOf(il), id));
            query.getValue(FDate.valueOf(il));
            if (removeEnabled) {
                map.remove(FDate.valueOf(il));
            }
        }
        if (removeEnabled) {
            for (final Long o : remove) {
                map.remove(FDate.valueOf(o));
            }
        }
        return r + map.getValuesMap().size();
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

    public int historicalCache() {
        final TestHistoricalCache map = new TestHistoricalCache();
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
        testRuntime("historicalCache", test::historicalCache);
        testRuntime("hashMap", test::hashMap);
        testRuntime("compactHashMap", test::compactHashMap);
        testRuntime("hppcMap", test::hppcMap);
        //        testRuntime("kolobokeMap", test::kolobokeMap);
        testRuntime("guavaCompactHashMap", test::guavaCompactHashMap);
        testRuntime("fastUtilHashMap", test::fastUtilHashMap);
        testRuntime("troveHashMap", test::troveHashMap);
        //        testRuntime("smoothieMap", test::smoothieMap);
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
        final TestHistoricalCache historicalCache = new TestHistoricalCache();
        testSize("HistoricalCache", historicalCache);
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
        //        final net.openhft.smoothie.SmoothieMap smoothieMap = new net.openhft.smoothie.SmoothieMap();
        //        testSize("SmoothieMap", smoothieMap);
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

    private static void testSize(final String name, final TestHistoricalCache map) {
        final double emptyMapSize = measureHeapSize(map);
        for (final Long o : add) {
            map.getValuesMap().put(FDate.valueOf(o), ImmutableHistoricalEntry.of(FDate.valueOf(o), o.doubleValue()));
        }
        final double size = map.getValuesMap().size();
        final double elementsSize = ELEMENTS_SIZE / TIMES * size;
        System.out.printf("%s: %.1f bytes per element\n", name,
                ((measureHeapSize(map) - elementsSize - emptyMapSize) * 1.0 / size));
    }
}