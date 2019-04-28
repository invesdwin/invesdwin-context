package de.invesdwin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.time.Instant;

// CHECKSTYLE:OFF
@NotThreadSafe
public class TestArrayListClear {

    private static final int REPEATS = 10000;
    private static final int SIZE = 100000;

    @Test
    public void testClear() {
        final Instant start = new Instant();
        final List<Double> list = new ArrayList<Double>();
        for (int r = 0; r < REPEATS; r++) {
            for (int i = 0; i < SIZE; i++) {
                list.add((double) i);
            }
            list.clear();
        }
        System.out.println("testClear: " + start);
    }

    @Test
    public void testNew() {
        final Instant start = new Instant();
        List<Double> list = new ArrayList<Double>();
        for (int r = 0; r < REPEATS; r++) {
            for (int i = 0; i < SIZE; i++) {
                list.add((double) i);
            }
            list = new ArrayList<Double>();
        }
        System.out.println("testNew: " + start);
    }

    @Test
    public void testNewKeepSize() {
        final Instant start = new Instant();
        List<Double> list = new ArrayList<Double>();
        for (int r = 0; r < REPEATS; r++) {
            for (int i = 0; i < SIZE; i++) {
                list.add((double) i);
            }
            list = new ArrayList<Double>(list.size());
        }
        System.out.println("testNewKeepSize: " + start);
    }

}
