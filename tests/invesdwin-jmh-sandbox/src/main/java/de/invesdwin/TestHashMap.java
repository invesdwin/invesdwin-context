/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.invesdwin;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jol.info.GraphLayout;

import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.google.common.collect.GuavaCompactHashMap;

import de.invesdwin.util.collections.eviction.LeastRecentlyAddedMap;
import de.invesdwin.util.collections.eviction.LeastRecentlyModifiedMap;
import de.invesdwin.util.collections.eviction.LeastRecentlyUsedMap;
import de.invesdwin.util.time.Instant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.Arrays.stream;

//HashMap: 59,2 bytes per element
//CompactHashMap: 38,5 bytes per element
//KolobokeMap: 38,5 bytes per element
//HPPC map: 38,5 bytes per element
//GuavaCompactHashMap: 42,8 bytes per element
//FastUtilHashMap: 38,5 bytes per element
//TroveHashMap: 48,1 bytes per element
//SmoothieMap: 44,1 bytes per element
//0. hashMap: PT0.341.753.360S
//1. hashMap: PT0.317.662.080S
//2. hashMap: PT0.303.456.258S
//3. hashMap: PT0.298.153.221S
//4. hashMap: PT0.302.554.888S
//5. hashMap: PT0.300.732.469S
//6. hashMap: PT0.402.692.082S
//7. hashMap: PT0.309.393.125S
//8. hashMap: PT0.305.966.869S
//9. hashMap: PT0.305.919.888S
//10. hashMap: PT0.306.562.014S
//11. hashMap: PT0.307.541.541S
//12. hashMap: PT0.300.297.675S
//13. hashMap: PT0.428.751.651S
//14. hashMap: PT0.344.841.951S
//15. hashMap: PT0.337.720.326S
//16. hashMap: PT0.351.775.751S
//17. hashMap: PT0.341.170.918S
//18. hashMap: PT0.302.268.897S
//19. hashMap: PT0.307.557.406S
//PT6.522.354.525S
//0. compactHashMap: PT0.382.983.073S
//1. compactHashMap: PT0.491.833.435S
//2. compactHashMap: PT0.373.248.304S
//3. compactHashMap: PT0.362.667.335S
//4. compactHashMap: PT0.359.849.313S
//5. compactHashMap: PT0.357.472.670S
//6. compactHashMap: PT0.473.713.124S
//7. compactHashMap: PT0.356.076.859S
//8. compactHashMap: PT0.358.602.859S
//9. compactHashMap: PT0.356.740.335S
//10. compactHashMap: PT0.355.605.893S
//11. compactHashMap: PT0.539.328.123S
//12. compactHashMap: PT0.363.637.524S
//13. compactHashMap: PT0.357.217.909S
//14. compactHashMap: PT0.354.004.035S
//15. compactHashMap: PT0.358.614.267S
//16. compactHashMap: PT0.366.389.601S
//17. compactHashMap: PT0.448.083.603S
//18. compactHashMap: PT0.359.527.747S
//19. compactHashMap: PT0.360.375.295S
//PT7.737.405.039S
//0. hppcMap: PT0.532.423.767S
//1. hppcMap: PT0.530.275.768S
//2. hppcMap: PT0.556.564.815S
//3. hppcMap: PT0.638.650.889S
//4. hppcMap: PT0.521.636.396S
//5. hppcMap: PT0.517.167.825S
//6. hppcMap: PT0.498.692.273S
//7. hppcMap: PT0.504.223.537S
//8. hppcMap: PT0.495.658.652S
//9. hppcMap: PT0.616.643.703S
//10. hppcMap: PT0.516.529.497S
//11. hppcMap: PT0.501.064.212S
//12. hppcMap: PT0.510.006.426S
//13. hppcMap: PT0.500.832.735S
//14. hppcMap: PT0.501.910.428S
//15. hppcMap: PT0.618.135.279S
//16. hppcMap: PT0.506.539.519S
//17. hppcMap: PT0.508.204.938S
//18. hppcMap: PT0.510.203.618S
//19. hppcMap: PT0.501.593.697S
//PT10.588.261.954S
//0. kolobokeMap: PT0.320.607.246S
//1. kolobokeMap: PT0.298.552.688S
//2. kolobokeMap: PT0.405.883.612S
//3. kolobokeMap: PT0.280.799.502S
//4. kolobokeMap: PT0.289.043.520S
//5. kolobokeMap: PT0.277.807.506S
//6. kolobokeMap: PT0.278.711.096S
//7. kolobokeMap: PT0.278.126.036S
//8. kolobokeMap: PT0.275.821.308S
//9. kolobokeMap: PT0.403.930.868S
//10. kolobokeMap: PT0.284.567.834S
//11. kolobokeMap: PT0.278.878.971S
//12. kolobokeMap: PT0.275.004.414S
//13. kolobokeMap: PT0.278.476.609S
//14. kolobokeMap: PT0.277.880.377S
//15. kolobokeMap: PT0.282.415.466S
//16. kolobokeMap: PT0.406.741.291S
//17. kolobokeMap: PT0.278.226.500S
//18. kolobokeMap: PT0.280.312.157S
//19. kolobokeMap: PT0.281.567.444S
//PT6.034.659.906S
//0. guavaCompactHashMap: PT0.295.482.046S
//1. guavaCompactHashMap: PT0.278.193.729S
//2. guavaCompactHashMap: PT0.260.437.172S
//3. guavaCompactHashMap: PT0.395.605.531S
//4. guavaCompactHashMap: PT0.270.166.639S
//5. guavaCompactHashMap: PT0.267.491.277S
//6. guavaCompactHashMap: PT0.260.434.841S
//7. guavaCompactHashMap: PT0.260.391.069S
//8. guavaCompactHashMap: PT0.263.007.216S
//9. guavaCompactHashMap: PT0.299.819.145S
//10. guavaCompactHashMap: PT0.263.257.498S
//11. guavaCompactHashMap: PT0.265.642.642S
//12. guavaCompactHashMap: PT0.260.476.612S
//13. guavaCompactHashMap: PT0.260.726.277S
//14. guavaCompactHashMap: PT0.266.819.654S
//15. guavaCompactHashMap: PT0.267.773.367S
//16. guavaCompactHashMap: PT0.327.270.262S
//17. guavaCompactHashMap: PT0.278.175.867S
//18. guavaCompactHashMap: PT0.266.952.075S
//19. guavaCompactHashMap: PT0.259.808.461S
//PT5.569.194.052S
//0. fastUtilHashMap: PT0.471.419.432S
//1. fastUtilHashMap: PT0.454.800.166S
//2. fastUtilHashMap: PT0.441.597.068S
//3. fastUtilHashMap: PT0.432.826.866S
//4. fastUtilHashMap: PT0.458.876.414S
//5. fastUtilHashMap: PT0.435.533.189S
//6. fastUtilHashMap: PT0.556.352.355S
//7. fastUtilHashMap: PT0.433.724.688S
//8. fastUtilHashMap: PT0.433.609.952S
//9. fastUtilHashMap: PT0.439.469.341S
//10. fastUtilHashMap: PT0.439.610.421S
//11. fastUtilHashMap: PT0.431.628.070S
//12. fastUtilHashMap: PT0.433.857.091S
//13. fastUtilHashMap: PT0.435.459.065S
//14. fastUtilHashMap: PT0.569.297.347S
//15. fastUtilHashMap: PT0.434.499.088S
//16. fastUtilHashMap: PT0.431.503.943S
//17. fastUtilHashMap: PT0.434.473.967S
//18. fastUtilHashMap: PT0.451.147.543S
//19. fastUtilHashMap: PT0.453.055.001S
//PT9.073.922.999S
//0. troveHashMap: PT0.700.039.845S
//1. troveHashMap: PT0.795.981.385S
//2. troveHashMap: PT0.638.501.293S
//3. troveHashMap: PT0.631.753.429S
//4. troveHashMap: PT0.774.383.920S
//5. troveHashMap: PT0.632.647.262S
//6. troveHashMap: PT0.635.147.283S
//7. troveHashMap: PT0.760.676.741S
//8. troveHashMap: PT0.640.351.514S
//9. troveHashMap: PT0.631.541.724S
//10. troveHashMap: PT0.631.182.493S
//11. troveHashMap: PT0.767.002.219S
//12. troveHashMap: PT0.726.639.115S
//13. troveHashMap: PT0.627.346.767S
//14. troveHashMap: PT0.645.176.081S
//15. troveHashMap: PT0.639.063.851S
//16. troveHashMap: PT0.627.949.103S
//17. troveHashMap: PT0.752.011.617S
//18. troveHashMap: PT0.632.011.723S
//19. troveHashMap: PT0.628.304.567S
//PT13.518.865.723S
//0. smoothieMap: PT0.306.387.970S
//1. smoothieMap: PT0.421.702.556S
//2. smoothieMap: PT0.280.296.724S
//3. smoothieMap: PT0.285.871.936S
//4. smoothieMap: PT0.276.332.142S
//5. smoothieMap: PT0.291.530.908S
//6. smoothieMap: PT0.309.406.356S
//7. smoothieMap: PT0.278.027.307S
//8. smoothieMap: PT0.276.525.323S
//9. smoothieMap: PT0.274.705.921S
//10. smoothieMap: PT0.280.078.741S
//11. smoothieMap: PT0.278.482.816S
//12. smoothieMap: PT0.290.004.397S
//13. smoothieMap: PT0.276.396.336S
//14. smoothieMap: PT0.272.998.412S
//15. smoothieMap: PT0.270.977.240S
//16. smoothieMap: PT0.274.663.289S
//17. smoothieMap: PT0.277.234.955S
//18. smoothieMap: PT0.275.832.819S
//19. smoothieMap: PT0.275.548.340S
//PT5.773.937.781S


//https://stackoverflow.com/questions/26365457/should-i-dump-java-util-hashset-in-favor-of-compacthashset/26369483#26369483
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@OperationsPerInvocation(TestHashMap.TIMES)
@Threads(1)
@Fork(1)
@State(Scope.Thread)
public class TestHashMap {
	public static final int TIMES = 10000;
	private static final int MAX = 5000000;
	private static double ELEMENTS_SIZE;

	static Long[] add = new Long[TIMES], lookup = new Long[TIMES], remove = new Long[TIMES];
	static {
		for (int ix = 0; ix < TIMES; ix++)
			add[ix] = new Long(Math.round(Math.random() * MAX));
		ELEMENTS_SIZE = stream(add).distinct().count() * GraphLayout.parseInstance(add[0]).totalSize();
		for (int ix = 0; ix < TIMES; ix++)
			lookup[ix] = new Long(Math.round(Math.random() * MAX));
		for (int ix = 0; ix < TIMES; ix++)
			remove[ix] = new Long(Math.round(Math.random() * MAX));
	}

	@Benchmark
	public int hashMap() {
		Map<Long, Double> map = new HashMap<Long, Double>();
		for (Long o : add) {
			map.put(o, o.doubleValue());
		}
		int r = 0;
		for (Long o : lookup) {
			r ^= map.get(o) != null ? 1 : 0;
		}
		for(int i = 0; i < TIMES; i++) {
			long il = (long)i;
			double id = (double)i;
			map.put(il, id);
			map.get(il);
			map.remove(il);
		}
		for (Long o : remove) {
			map.remove(o);
		}
		return r + map.size();
	}

	@Benchmark
	public int compactHashMap() {
		Map<Long, Double> map = new net.ontopia.utils.CompactHashMap<Long, Double>();
		for (Long o : add) {
			map.put(o, o.doubleValue());
		}
		int r = 0;
		for (Long o : lookup) {
			r ^= map.get(o) != null ? 1 : 0;
		}
		for(int i = 0; i < TIMES; i++) {
			long il = (long)i;
			double id = (double)i;
			map.put(il, id);
			map.get(il);
			map.remove(il);
		}
		for (Long o : remove) {
			map.remove(o);
		}
		return r + map.size();
	}

	@Benchmark
	public int hppcMap() {
		com.carrotsearch.hppc.ObjectObjectHashMap<Long, Double> map = new com.carrotsearch.hppc.ObjectObjectHashMap<Long, Double>();
		for (Long o : add) {
			map.put(o, o.doubleValue());
		}
		int r = 0;
		for (Long o : lookup) {
			r ^= map.get(o) != null ? 1 : 0;
		}
		for(int i = 0; i < TIMES; i++) {
			long il = (long)i;
			double id = (double)i;
			map.put(il, id);
			map.get(il);
			map.remove(il);
		}
		for (Long o : remove) {
			map.remove(o);
		}
		return r + map.size();
	}

	@Benchmark
	public int kolobokeMap() {
		Map<Long, Double> map = com.koloboke.collect.map.hash.HashObjObjMaps.newMutableMap();
		for (Long o : add) {
			map.put(o, o.doubleValue());
		}
		int r = 0;
		for (Long o : lookup) {
			r ^= map.get(o) != null ? 1 : 0;
		}
		for(int i = 0; i < TIMES; i++) {
			long il = (long)i;
			double id = (double)i;
			map.put(il, id);
			map.get(il);
			map.remove(il);
		}
		for (Long o : remove) {
			map.remove(o);
		}
		return r + map.size();
	}

	@Benchmark
	public int guavaCompactHashMap() {
		Map<Long, Double> map = new com.google.common.collect.GuavaCompactHashMap<>();
		for (Long o : add) {
			map.put(o, o.doubleValue());
		}
		int r = 0;
		for (Long o : lookup) {
			r ^= map.get(o) != null ? 1 : 0;
		}
		for(int i = 0; i < TIMES; i++) {
			long il = (long)i;
			double id = (double)i;
			map.put(il, id);
			map.get(il);
			map.remove(il);
		}
		for (Long o : remove) {
			map.remove(o);
		}
		return r + map.size();
	}

	@Benchmark
	public int fastUtilHashMap() {
		// fair comparison -- growing table
		Map<Long, Double> map = new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>();
		for (Long o : add) {
			map.put(o, o.doubleValue());
		}
		int r = 0;
		for (Long o : lookup) {
			r ^= map.get(o) != null ? 1 : 0;
		}
		for(int i = 0; i < TIMES; i++) {
			long il = (long)i;
			double id = (double)i;
			map.put(il, id);
			map.get(il);
			map.remove(il);
		}
		for (Long o : remove) {
			map.remove(o);
		}
		return r + map.size();
	}

	@Benchmark
	public int troveHashMap() {
		// fair comparison -- growing table
		Map<Long, Double> map = new gnu.trove.map.hash.THashMap<>();
		for (Long o : add) {
			map.put(o, o.doubleValue());
		}
		int r = 0;
		for (Long o : lookup) {
			r ^= map.get(o) != null ? 1 : 0;
		}
		for(int i = 0; i < TIMES; i++) {
			long il = (long)i;
			double id = (double)i;
			map.put(il, id);
			map.get(il);
			map.remove(il);
		}
		for (Long o : remove) {
			map.remove(o);
		}
		return r + map.size();
	}

	@Benchmark
	public int smoothieMap() {
		// fair comparison -- growing table
		Map<Long, Double> map = new net.openhft.smoothie.SmoothieMap<>();
		for (Long o : add) {
			map.put(o, o.doubleValue());
		}
		int r = 0;
		for (Long o : lookup) {
			r ^= map.get(o) != null ? 1 : 0;
		}
		for(int i = 0; i < TIMES; i++) {
			long il = (long)i;
			double id = (double)i;
			map.put(il, id);
			map.get(il);
			map.remove(il);
		}
		for (Long o : remove) {
			map.remove(o);
		}
		return r + map.size();
	}
	
	@Benchmark
	public int lruMap() {
		Map<Long, Double> map = new LeastRecentlyUsedMap<>(TIMES/10);
		for (Long o : add) {
			map.put(o, o.doubleValue());
		}
		int r = 0;
		for (Long o : lookup) {
			r ^= map.get(o) != null ? 1 : 0;
		}
		for(int i = 0; i < TIMES; i++) {
			long il = (long)i;
			double id = (double)i;
			map.put(il, id);
			map.get(il);
			map.remove(il);
		}
		for (Long o : remove) {
			map.remove(o);
		}
		return r + map.size();
	}
	
	@Benchmark
	public int lrmMap() {
		Map<Long, Double> map = new LeastRecentlyModifiedMap<>(TIMES/10);
		for (Long o : add) {
			map.put(o, o.doubleValue());
		}
		int r = 0;
		for (Long o : lookup) {
			r ^= map.get(o) != null ? 1 : 0;
		}
		for(int i = 0; i < TIMES; i++) {
			long il = (long)i;
			double id = (double)i;
			map.put(il, id);
			map.get(il);
			map.remove(il);
		}
		for (Long o : remove) {
			map.remove(o);
		}
		return r + map.size();
	}
	
	@Benchmark
	public int lraMap() {
		Map<Long, Double> map = new LeastRecentlyAddedMap<>(TIMES/10);
		for (Long o : add) {
			map.put(o, o.doubleValue());
		}
		int r = 0;
		for (Long o : lookup) {
			r ^= map.get(o) != null ? 1 : 0;
		}
		for(int i = 0; i < TIMES; i++) {
			long il = (long)i;
			double id = (double)i;
			map.put(il, id);
			map.get(il);
			map.remove(il);
		}
		for (Long o : remove) {
			map.remove(o);
		}
		return r + map.size();
	}
	
	@Benchmark
	public int javaLruMap() {
		Map<Long, Double> map = new LinkedHashMap<Long, Double>() {
			 @Override
			  protected boolean removeEldestEntry(final Entry<Long, Double> eldest) {
			    return size() >= TIMES/10;
			  }
		};
		for (Long o : add) {
			map.put(o, o.doubleValue());
		}
		int r = 0;
		for (Long o : lookup) {
			r ^= map.get(o) != null ? 1 : 0;
		}
		for(int i = 0; i < TIMES; i++) {
			long il = (long)i;
			double id = (double)i;
			map.put(il, id);
			map.get(il);
			map.remove(il);
		}
		for (Long o : remove) {
			map.remove(o);
		}
		return r + map.size();
	}

	public static void main(String[] argv) {
		testSize();
		testRuntime();
	}

	private static void testRuntime() {
		TestHashMap test = new TestHashMap();
		testRuntime("hashMap", test::hashMap);
		testRuntime("compactHashMap", test::compactHashMap);
		testRuntime("hppcMap", test::hppcMap);
		testRuntime("kolobokeMap", test::kolobokeMap);
		testRuntime("guavaCompactHashMap", test::guavaCompactHashMap);
		testRuntime("fastUtilHashMap", test::fastUtilHashMap);
		testRuntime("troveHashMap", test::troveHashMap);
		testRuntime("smoothieMap", test::smoothieMap);
		testRuntime("lruMap", test::lruMap);
		testRuntime("lrmMap", test::lrmMap);
		testRuntime("lraMap", test::lraMap);
		testRuntime("javaLruMap", test::javaLruMap);
	}

	private static void testRuntime(String string, Runnable object) {
		Instant overall = new Instant();
		for (int i = 0; i < 10000; i++) {
//			Instant instant = new Instant();
			object.run();
//			System.out.println(i + ". " + string + ": " + instant);
		}
		System.out.println(string+": "+overall);
	}

	private static void put(ObjectObjectHashMap hppcMap, Object t) {
		Long l = (Long) t;
		hppcMap.put(l, l.doubleValue());
	}

	private static void testSize() {
		HashMap hashMap = new HashMap();
		testSize("HashMap", hashMap);
		net.ontopia.utils.CompactHashMap compactHashMap = new net.ontopia.utils.CompactHashMap();
		testSize("CompactHashMap", compactHashMap);
		com.koloboke.collect.map.hash.HashObjObjMap<Object, Object> kolobokeMap = com.koloboke.collect.map.hash.HashObjObjMaps
				.newMutableMap();
		testSize("KolobokeMap", kolobokeMap);
		com.carrotsearch.hppc.ObjectObjectHashMap hppcMap = new com.carrotsearch.hppc.ObjectObjectHashMap();
		testSize("HPPC map", hppcMap);
		com.google.common.collect.GuavaCompactHashMap guavaCompactHashMap = new com.google.common.collect.GuavaCompactHashMap();
		testSize("GuavaCompactHashMap", guavaCompactHashMap);
		it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap fastUtilHashMap = new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap();
		testSize("FastUtilHashMap", fastUtilHashMap);
		gnu.trove.map.hash.THashMap troveHashMap = new gnu.trove.map.hash.THashMap();
		testSize("TroveHashMap", troveHashMap);
		net.openhft.smoothie.SmoothieMap smoothieMap = new net.openhft.smoothie.SmoothieMap();
		testSize("SmoothieMap", smoothieMap);
		LeastRecentlyUsedMap lruMap = new LeastRecentlyUsedMap<>(TIMES/10);
		testSize("lruMap", lruMap);
		LeastRecentlyModifiedMap lrmMap = new LeastRecentlyModifiedMap<>(TIMES/10);
		testSize("lrmMap", lrmMap);
		LeastRecentlyAddedMap lraMap = new LeastRecentlyAddedMap<>(TIMES/10);
		testSize("lraMap", lraMap);
		Map<Long, Double> javaLruMap = new LinkedHashMap<Long, Double>() {
			 @Override
			  protected boolean removeEldestEntry(final Entry<Long, Double> eldest) {
			    return size() >= TIMES/10;
			  }
		};
		testSize("javaLruMap", javaLruMap);
	}

	private static void testSize(String name, ObjectObjectHashMap map) {
		for (Long o : add) {
			map.put(o, o.doubleValue());
		}
		double size = map.size();
		double elementsSize = ELEMENTS_SIZE / TIMES * size;
		System.out.printf("%s: %.1f bytes per element\n", name,
				((GraphLayout.parseInstance(map).totalSize() - elementsSize) * 1.0 / size));
	}

	private static void testSize(String name, Map map) {
		for (Long o : add) {
			map.put(o, o.doubleValue());
		}
		double size = map.size();
		double elementsSize = ELEMENTS_SIZE / TIMES * size;
		System.out.printf("%s: %.1f bytes per element\n", name,
				((GraphLayout.parseInstance(map).totalSize() - elementsSize) * 1.0 / size));
	}
}