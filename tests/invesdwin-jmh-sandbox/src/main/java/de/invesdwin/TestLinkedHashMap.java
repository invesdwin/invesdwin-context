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

import de.invesdwin.util.collections.eviction.CommonsLeastRecentlyAddedMap;
import de.invesdwin.util.collections.eviction.CommonsLeastRecentlyModifiedMap;
import de.invesdwin.util.collections.eviction.CommonsLeastRecentlyUsedMap;
import de.invesdwin.util.time.Instant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.Arrays.stream;




//https://stackoverflow.com/questions/26365457/should-i-dump-java-util-hashset-in-favor-of-compacthashset/26369483#26369483
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@OperationsPerInvocation(TestLinkedHashMap.TIMES)
@Threads(1)
@Fork(1)
@State(Scope.Thread)
public class TestLinkedHashMap {
	private static final int REPETITIONS = 1000;
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
	public int linkedHashMap() {
		Map<Long, Double> map = new LinkedHashMap<Long, Double>();
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
	public int guavaCompactLinkedHashMap() {
		Map<Long, Double> map = new com.google.common.collect.GuavaCompactLinkedHashMap<>();
		return test(map);
	}

	@Benchmark
	public int fastUtilLinkedHashMap() {
		// fair comparison -- growing table
		Map<Long, Double> map = new it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap<>();
		return test(map);
	}

	public static void main(String[] argv) {
		testSize();
		testRuntime();
	}

	private static void testRuntime() {
		TestLinkedHashMap test = new TestLinkedHashMap();
		testRuntime("linkedHashMap", test::linkedHashMap);
		testRuntime("guavaCompactLinkedHashMap", test::guavaCompactLinkedHashMap);
		testRuntime("fastUtilLinkedHashMap", test::fastUtilLinkedHashMap);
	}

	private static void testRuntime(String string, Runnable object) {
		Instant overall = new Instant();
		for (int i = 0; i < REPETITIONS; i++) {
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
		LinkedHashMap hashMap = new LinkedHashMap();
		testSize("LinkedHashMap", hashMap);
		com.google.common.collect.GuavaCompactLinkedHashMap guavaCompactHashMap = new com.google.common.collect.GuavaCompactLinkedHashMap();
		testSize("GuavaCompactLinkedHashMap", guavaCompactHashMap);
		it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap fastUtilHashMap = new it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap();
		testSize("FastUtilLinkedHashMap", fastUtilHashMap);
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