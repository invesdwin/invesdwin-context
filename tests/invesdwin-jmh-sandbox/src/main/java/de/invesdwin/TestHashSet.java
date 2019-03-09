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

import com.google.common.collect.GuavaCompactHashSet;

import de.invesdwin.util.time.Instant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.Arrays.stream;

//HashSet: 42,1 bytes per element
//CompactHashSet: 10,5 bytes per element
//KolobokeSet: 10,5 bytes per element
//HPPC set: 10,5 bytes per element
//GuavaCompactHashSet: 21,8 bytes per element
//FastUtilHashSet: 10,5 bytes per element
//TroveHashSet: 8,2 bytes per element
//hashSet: PT10.123.976.325S
//compactHashSet: PT11.226.095.028S
//hppcSet: PT9.041.564.226S
//kolobokeSet: PT7.304.953.777S
//guavaCompactHashSet: PT6.294.883.671S
//fastUtilHashSet: PT7.943.748.371S
//troveHashSet: PT13.024.372.126S

//https://stackoverflow.com/questions/26365457/should-i-dump-java-util-hashset-in-favor-of-compacthashset/26369483#26369483
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@OperationsPerInvocation(TestHashSet.TIMES)
@Threads(1)
@Fork(1)
@State(Scope.Thread)
public class TestHashSet {
    private static final int REPETITIONS = 1000;
	public static final int TIMES = 100000;
    private static final int MAX = 5000000;
    private static long ELEMENTS_SIZE;

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
    public int hashSet() {
        Set<Long> set = new HashSet<Long>();
        return test(set);
    }

	private int test(Set<Long> set) {
		for (Long o : add) {
            set.add(o);
        }
        int r = 0;
        for (Long o : lookup) {
            r ^= set.contains(o) ? 1 : 0;
        }
        for (Long o : set) {
            r += o.intValue();
        }
//        for (Long o : remove) {
//            set.remove(o);
//        }
        return r + set.size();
	}
	
	private int test(com.carrotsearch.hppc.ObjectHashSet<Long> set) {
		for (Long o : add) {
            set.add(o);
        }
        int r = 0;
        for (Long o : lookup) {
            r ^= set.contains(o) ? 1 : 0;
        }
        for (com.carrotsearch.hppc.cursors.ObjectCursor<Long> cur : set) {
            r += cur.value.intValue();
        }
//        for (Long o : remove) {
//            set.remove(o);
//        }
        return r + set.size();
	}

    @Benchmark
    public int compactHashSet() {
        Set<Long> set = new net.ontopia.utils.CompactHashSet<Long>();
        return test(set);
    }

    @Benchmark
    public int hppcSet() {
    	com.carrotsearch.hppc.ObjectHashSet<Long> set = new com.carrotsearch.hppc.ObjectHashSet<Long>();
        return test(set);
    }


    @Benchmark
    public int kolobokeSet() {
        Set<Long> set = com.koloboke.collect.set.hash.HashObjSets.newMutableSet();
        return test(set);
    }

    @Benchmark
    public int guavaCompactHashSet() {
        Set<Long> set = new com.google.common.collect.GuavaCompactHashSet<>();
        return test(set);
    }
    
    @Benchmark
    public int fastUtilHashSet() {
        // fair comparison -- growing table
        Set<Long> set = new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet<>();
        return test(set);
    }
    
    @Benchmark
    public int troveHashSet() {
        // fair comparison -- growing table
        Set<Long> set = new gnu.trove.set.hash.THashSet<>();
        return test(set);
    }

    public static void main(String[] argv) {
        testSize();
        testRuntime();
    }

	private static void testRuntime() {
		TestHashSet test = new TestHashSet();
		testRuntime("hashSet", test::hashSet);
		testRuntime("compactHashSet", test::compactHashSet);
		testRuntime("hppcSet", test::hppcSet);
		testRuntime("kolobokeSet", test::kolobokeSet);
		testRuntime("guavaCompactHashSet", test::guavaCompactHashSet);
		testRuntime("fastUtilHashSet", test::fastUtilHashSet);
		testRuntime("troveHashSet", test::troveHashSet);
	}

	private static void testRuntime(String string, Runnable object) {
		Instant overall = new Instant();
		for(int i = 0; i < REPETITIONS; i++) {
//			Instant instant = new Instant();
			object.run();
//			System.out.println(i+". "+string+": "+instant);
		}
		System.out.println(string+": "+overall);
	}

	private static void testSize() {
		HashSet hashSet = new HashSet();
        testSize("HashSet", hashSet, hashSet::add);
        net.ontopia.utils.CompactHashSet compactHashSet = new net.ontopia.utils.CompactHashSet();
        testSize("CompactHashSet", compactHashSet, compactHashSet::add);
        com.koloboke.collect.set.hash.HashObjSet<Object> kolobokeSet = com.koloboke.collect.set.hash.HashObjSets.newMutableSet();
        testSize("KolobokeSet", kolobokeSet, kolobokeSet::add);
        com.carrotsearch.hppc.ObjectHashSet hppcSet = new com.carrotsearch.hppc.ObjectHashSet();
        testSize("HPPC set", hppcSet, hppcSet::add);
        com.google.common.collect.GuavaCompactHashSet guavaCompactHashSet = new com.google.common.collect.GuavaCompactHashSet();
        testSize("GuavaCompactHashSet", guavaCompactHashSet, guavaCompactHashSet::add);
        it.unimi.dsi.fastutil.objects.ObjectOpenHashSet fastUtilHashSet = new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet();
        testSize("FastUtilHashSet", fastUtilHashSet, fastUtilHashSet::add);
        gnu.trove.set.hash.THashSet troveHashSet = new gnu.trove.set.hash.THashSet();
        testSize("TroveHashSet", troveHashSet, troveHashSet::add);
	}

    public static void testSize(String name, Object set, Consumer setAdd) {
        for (Long o : add) {
            setAdd.accept(o);
        }
        System.out.printf("%s: %.1f bytes per element\n", name,
                ((GraphLayout.parseInstance(set).totalSize() - ELEMENTS_SIZE) * 1.0 / TIMES));

    }
}