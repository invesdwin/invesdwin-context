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
import com.google.common.collect.GuavaCompactLinkedHashSet;

import de.invesdwin.util.time.Instant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.Arrays.stream;

//Benchmark                        Mode  Cnt    Score    Error  Units
//TestHashSet.compactHashSet       avgt    5  227,432 ± 10,418  ns/op
//TestHashSet.fastUtilHashSet      avgt    5  244,838 ± 10,770  ns/op
//TestHashSet.guavaCompactHashSet  avgt    5  160,115 ± 15,077  ns/op
//TestHashSet.hashSet              avgt    5  242,879 ± 10,291  ns/op
//TestHashSet.hppcSet              avgt    5  320,705 ± 35,359  ns/op
//TestHashSet.kolobokeSet          avgt    5  187,091 ±  9,167  ns/op
//TestHashSet.troveHashSet         avgt    5  357,688 ± 21,499  ns/op


//HashSet: 37,4 bytes per element
//CompactHashSet: 8,4 bytes per element
//KolobokeSet: 8,4 bytes per element
//HPPC set: 8,4 bytes per element
//GuavaCompactHashSet: 16,8 bytes per element
//FastUtilHashSet: 8,4 bytes per element
//TroveHashSet: 13,2 bytes per element
//0. hashSet: PT0.316.569.458S
//1. hashSet: PT0.271.302.700S
//2. hashSet: PT0.408.457.975S
//3. hashSet: PT0.271.719.835S
//4. hashSet: PT0.263.828.956S
//5. hashSet: PT0.264.971.202S
//6. hashSet: PT0.265.025.379S
//7. hashSet: PT0.264.450.542S
//8. hashSet: PT0.264.809.860S
//9. hashSet: PT0.261.461.573S
//10. hashSet: PT0.316.773.403S
//11. hashSet: PT0.256.571.414S
//12. hashSet: PT0.265.155.689S
//13. hashSet: PT0.256.692.056S
//14. hashSet: PT0.254.953.651S
//15. hashSet: PT0.256.487.194S
//16. hashSet: PT0.257.512.968S
//17. hashSet: PT0.255.409.044S
//18. hashSet: PT0.256.444.305S
//19. hashSet: PT0.263.599.620S
//PT5.497.807.971S
//0. compactHashSet: PT0.264.993.181S
//1. compactHashSet: PT0.249.159.552S
//2. compactHashSet: PT0.256.224.720S
//3. compactHashSet: PT0.236.254.187S
//4. compactHashSet: PT0.234.527.996S
//5. compactHashSet: PT0.237.103.087S
//6. compactHashSet: PT0.237.085.734S
//7. compactHashSet: PT0.252.312.892S
//8. compactHashSet: PT0.243.524.894S
//9. compactHashSet: PT0.236.686.391S
//10. compactHashSet: PT0.236.248.914S
//11. compactHashSet: PT0.240.324.420S
//12. compactHashSet: PT0.242.767.275S
//13. compactHashSet: PT0.237.282.603S
//14. compactHashSet: PT0.235.624.945S
//15. compactHashSet: PT0.236.131.729S
//16. compactHashSet: PT0.236.428.785S
//17. compactHashSet: PT0.235.695.583S
//18. compactHashSet: PT0.233.251.096S
//19. compactHashSet: PT0.340.338.235S
//PT4.923.391.485S
//0. hppcSet: PT0.363.121.067S
//1. hppcSet: PT0.343.943.327S
//2. hppcSet: PT0.339.662.842S
//3. hppcSet: PT0.338.908.009S
//4. hppcSet: PT0.337.180.409S
//5. hppcSet: PT0.337.519.148S
//6. hppcSet: PT0.334.234.812S
//7. hppcSet: PT0.337.761.927S
//8. hppcSet: PT0.338.795.789S
//9. hppcSet: PT0.344.628.202S
//10. hppcSet: PT0.338.013.514S
//11. hppcSet: PT0.340.424.632S
//12. hppcSet: PT0.337.491.879S
//13. hppcSet: PT0.335.684.870S
//14. hppcSet: PT0.338.230.287S
//15. hppcSet: PT0.362.901.905S
//16. hppcSet: PT0.341.927.592S
//17. hppcSet: PT0.339.301.859S
//18. hppcSet: PT0.340.301.583S
//19. hppcSet: PT0.343.250.080S
//PT6.834.672.144S
//0. kolobokeSet: PT0.214.566.836S
//1. kolobokeSet: PT0.197.576.754S
//2. kolobokeSet: PT0.194.820.968S
//3. kolobokeSet: PT0.196.022.522S
//4. kolobokeSet: PT0.196.809.043S
//5. kolobokeSet: PT0.193.961.624S
//6. kolobokeSet: PT0.202.011.598S
//7. kolobokeSet: PT0.194.407.531S
//8. kolobokeSet: PT0.191.796.084S
//9. kolobokeSet: PT0.193.803.090S
//10. kolobokeSet: PT0.193.899.356S
//11. kolobokeSet: PT0.192.881.923S
//12. kolobokeSet: PT0.192.821.997S
//13. kolobokeSet: PT0.194.822.434S
//14. kolobokeSet: PT0.194.536.566S
//15. kolobokeSet: PT0.193.343.377S
//16. kolobokeSet: PT0.194.498.203S
//17. kolobokeSet: PT0.194.535.135S
//18. kolobokeSet: PT0.194.269.622S
//19. kolobokeSet: PT0.192.763.830S
//PT3.915.501.385S
//0. guavaCompactHashSet: PT0.196.531.573S
//1. guavaCompactHashSet: PT0.177.601.562S
//2. guavaCompactHashSet: PT0.170.103.046S
//3. guavaCompactHashSet: PT0.172.840.538S
//4. guavaCompactHashSet: PT0.172.167.206S
//5. guavaCompactHashSet: PT0.171.142.024S
//6. guavaCompactHashSet: PT0.172.722.336S
//7. guavaCompactHashSet: PT0.168.059.702S
//8. guavaCompactHashSet: PT0.168.923.381S
//9. guavaCompactHashSet: PT0.168.838.621S
//10. guavaCompactHashSet: PT0.171.521.159S
//11. guavaCompactHashSet: PT0.169.889.327S
//12. guavaCompactHashSet: PT0.173.712.726S
//13. guavaCompactHashSet: PT0.176.899.828S
//14. guavaCompactHashSet: PT0.172.665.059S
//15. guavaCompactHashSet: PT0.170.397.399S
//16. guavaCompactHashSet: PT0.170.649.851S
//17. guavaCompactHashSet: PT0.173.175.365S
//18. guavaCompactHashSet: PT0.169.261.319S
//19. guavaCompactHashSet: PT0.174.505.981S
//PT3.462.835.702S
//0. fastUtilHashSet: PT0.298.848.355S
//1. fastUtilHashSet: PT0.286.905.364S
//2. fastUtilHashSet: PT0.284.382.155S
//3. fastUtilHashSet: PT0.277.404.519S
//4. fastUtilHashSet: PT0.284.595.722S
//5. fastUtilHashSet: PT0.294.769.202S
//6. fastUtilHashSet: PT0.283.204.965S
//7. fastUtilHashSet: PT0.276.533.410S
//8. fastUtilHashSet: PT0.277.732.753S
//9. fastUtilHashSet: PT0.282.697.066S
//10. fastUtilHashSet: PT0.281.960.692S
//11. fastUtilHashSet: PT0.281.474.076S
//12. fastUtilHashSet: PT0.280.582.176S
//13. fastUtilHashSet: PT0.290.414.379S
//14. fastUtilHashSet: PT0.281.304.090S
//15. fastUtilHashSet: PT0.284.346.617S
//16. fastUtilHashSet: PT0.289.030.447S
//17. fastUtilHashSet: PT0.285.424.915S
//18. fastUtilHashSet: PT0.289.799.994S
//19. fastUtilHashSet: PT0.296.285.986S
//PT5.708.846.422S
//0. troveHashSet: PT0.399.844.120S
//1. troveHashSet: PT0.389.276.046S
//2. troveHashSet: PT0.380.952.008S
//3. troveHashSet: PT0.381.234.120S
//4. troveHashSet: PT0.382.294.090S
//5. troveHashSet: PT0.399.834.192S
//6. troveHashSet: PT0.409.634.013S
//7. troveHashSet: PT0.396.658.946S
//8. troveHashSet: PT0.401.114.892S
//9. troveHashSet: PT0.391.224.584S
//10. troveHashSet: PT0.388.151.611S
//11. troveHashSet: PT0.378.898.448S
//12. troveHashSet: PT0.377.482.844S
//13. troveHashSet: PT0.378.287.257S
//14. troveHashSet: PT0.379.100.250S
//15. troveHashSet: PT0.403.362.806S
//16. troveHashSet: PT0.381.923.937S
//17. troveHashSet: PT0.376.762.316S
//18. troveHashSet: PT0.373.928.111S
//19. troveHashSet: PT0.375.383.329S
//PT7.746.430.282S

//https://stackoverflow.com/questions/26365457/should-i-dump-java-util-hashset-in-favor-of-compacthashset/26369483#26369483
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@OperationsPerInvocation(TestLinkedHashSet.TIMES)
@Threads(1)
@Fork(1)
@State(Scope.Thread)
public class TestLinkedHashSet {
    private static final int REPETITIONS = 1000;
	public static final int TIMES = 10000;
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
    public int linkedHashSet() {
        Set<Long> set = new LinkedHashSet<Long>();
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
        for (Long o : remove) {
            set.remove(o);
        }
        return r + set.size();
	}

    @Benchmark
    public int guavaCompactLinkedHashSet() {
        Set<Long> set = new com.google.common.collect.GuavaCompactLinkedHashSet<>();
        return test(set);
    }
    
    @Benchmark
    public int fastUtilLinkedHashSet() {
        // fair comparison -- growing table
        Set<Long> set = new it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet<>();
        return test(set);
    }
    
    public static void main(String[] argv) {
        testSize();
        testRuntime();
    }

	private static void testRuntime() {
		TestLinkedHashSet test = new TestLinkedHashSet();
		testRuntime("linkedHashSet", test::linkedHashSet);
		testRuntime("guavaCompactLinkedHashSet", test::guavaCompactLinkedHashSet);
		testRuntime("fastUtilLinkedHashSet", test::fastUtilLinkedHashSet);
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
		LinkedHashSet hashSet = new LinkedHashSet();
        testSize("LinkedHashSet", hashSet, hashSet::add);
       GuavaCompactLinkedHashSet guavaCompactLinkedHashSet = new GuavaCompactLinkedHashSet<>();
        testSize("GuavaCompactLinkedHashSet", guavaCompactLinkedHashSet, guavaCompactLinkedHashSet::add);
        it.unimi.dsi.fastutil.objects.ObjectOpenHashSet fastUtilHashSet = new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet();
        testSize("FastUtilLinkedHashSet", fastUtilHashSet, fastUtilHashSet::add);
	}

    public static void testSize(String name, Object set, Consumer setAdd) {
        for (Long o : add) {
            setAdd.accept(o);
        }
        System.out.printf("%s: %.1f bytes per element\n", name,
                ((GraphLayout.parseInstance(set).totalSize() - ELEMENTS_SIZE) * 1.0 / TIMES));

    }
}