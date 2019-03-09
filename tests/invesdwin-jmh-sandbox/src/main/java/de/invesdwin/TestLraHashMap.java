package de.invesdwin;

import static java.util.Arrays.stream;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jol.info.GraphLayout;

import com.carrotsearch.hppc.ObjectObjectHashMap;

import de.invesdwin.util.collections.eviction.ArrayLeastRecentlyAddedMap;
import de.invesdwin.util.collections.eviction.CommonsLeastRecentlyAddedMap;
import de.invesdwin.util.collections.eviction.CommonsLeastRecentlyModifiedMap;
import de.invesdwin.util.collections.eviction.CommonsLeastRecentlyUsedMap;
import de.invesdwin.util.collections.eviction.JavaLeastRecentlyUsedMap;
import de.invesdwin.util.time.Instant;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@OperationsPerInvocation(TestLraHashMap.TIMES)
@Threads(1)
@Fork(1)
@State(Scope.Thread)
public class TestLraHashMap {
	private static final int REPETITIONS = 1000;
	public static final int TIMES = 10000;
	private static final int MAXIMUM_SIZE = TIMES / 10;
	private static final int MAX = 5000000;
	private static double ELEMENTS_SIZE;
	private boolean removeEnabled = true;

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
	public int lruMap() {
		Map<Long, Double> map = new CommonsLeastRecentlyUsedMap<>(MAXIMUM_SIZE);
		return test(map);
	}

	private int test(Map<Long, Double> map) {
		for (Long o : add) {
			map.put(o, o.doubleValue());
		}
		int r = 0;
		for (Long o : lookup) {
			r ^= map.get(o) != null ? 1 : 0;
		}
		for (int i = 0; i < TIMES; i++) {
			long il = (long) i;
			double id = (double) i;
			map.put(il, id);
			map.get(il);
		}
		if (removeEnabled) {
			for (Long o : remove) {
				map.remove(o);
			}
		}
		return r + map.size();
	}

	@Benchmark
	public int lrmMap() {
		Map<Long, Double> map = new CommonsLeastRecentlyModifiedMap<>(MAXIMUM_SIZE);
		return test(map);
	}

	@Benchmark
	public int lraMap() {
		Map<Long, Double> map = new CommonsLeastRecentlyAddedMap<>(MAXIMUM_SIZE);
		return test(map);
	}

	@Benchmark
	public int javaLruMap() {
		Map<Long, Double> map = new JavaLeastRecentlyUsedMap<Long, Double>(MAXIMUM_SIZE);
		return test(map);
	}

	public int arrayListLraMap() {
		ArrayListLraMap<Long, Double> map = new ArrayListLraMap<>(MAXIMUM_SIZE);
		return test(map);
	}

	public int arrayCustomLraMap() {
		ArrayCustomLraMap<Long, Double> map = new ArrayCustomLraMap<>(MAXIMUM_SIZE);
		return test(map);
	}
	
	public int arrayLraMap() {
		ArrayLeastRecentlyAddedMap<Long, Double> map = new ArrayLeastRecentlyAddedMap<>(MAXIMUM_SIZE);
		return test(map);
	}

	public int linkedListLraMap() {
		LinkedListLraMap<Long, Double> map = new LinkedListLraMap<>(MAXIMUM_SIZE);
		return test(map);
	}

	public int linkedHashSetLraMap() {
		LinkedHashSetLraMap<Long, Double> map = new LinkedHashSetLraMap<>(MAXIMUM_SIZE);
		return test(map);
	}

	public static void main(String[] argv) {
		testSize();
		testRuntime();
	}

	private static void testRuntime() {
		TestLraHashMap test = new TestLraHashMap();
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

	private static void testRuntime(String string, Runnable object) {
		Instant overall = new Instant();
		for (int i = 0; i < REPETITIONS; i++) {
//			Instant instant = new Instant();
			object.run();
//			System.out.println(i + ". " + string + ": " + instant);
		}
		System.out.println(string + ": " + overall);
	}

	private static void put(ObjectObjectHashMap hppcMap, Object t) {
		Long l = (Long) t;
		hppcMap.put(l, l.doubleValue());
	}

	private static void testSize() {
		CommonsLeastRecentlyUsedMap lruMap = new CommonsLeastRecentlyUsedMap<>(MAXIMUM_SIZE);
		testSize("lruMap", lruMap);
		CommonsLeastRecentlyModifiedMap lrmMap = new CommonsLeastRecentlyModifiedMap<>(MAXIMUM_SIZE);
		testSize("lrmMap", lrmMap);
		CommonsLeastRecentlyAddedMap lraMap = new CommonsLeastRecentlyAddedMap<>(MAXIMUM_SIZE);
		testSize("lraMap", lraMap);
		JavaLeastRecentlyUsedMap<Long, Double> javaLruMap = new JavaLeastRecentlyUsedMap(MAXIMUM_SIZE);
		testSize("javaLruMap", javaLruMap);
		ArrayListLraMap<Long, Double> arrayListLraMap = new ArrayListLraMap<>(MAXIMUM_SIZE);
		testSize("arrayListLraMap", arrayListLraMap);
		ArrayCustomLraMap<Long, Double> arrayCustomLraMap = new ArrayCustomLraMap<>(MAXIMUM_SIZE);
		testSize("arrayCustomLraMap", arrayCustomLraMap);
		ArrayLeastRecentlyAddedMap<Long, Double> arrayLraMap = new ArrayLeastRecentlyAddedMap<>(MAXIMUM_SIZE);
		testSize("arrayLraMap", arrayLraMap);
		LinkedListLraMap<Long, Double> linkedListLraMap = new LinkedListLraMap<>(MAXIMUM_SIZE);
		testSize("linkedListLraMap", linkedListLraMap);
		LinkedHashSetLraMap<Long, Double> linkedHashSetLraMap = new LinkedHashSetLraMap<>(MAXIMUM_SIZE);
		testSize("linkedHashSetLraMap", linkedHashSetLraMap);
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