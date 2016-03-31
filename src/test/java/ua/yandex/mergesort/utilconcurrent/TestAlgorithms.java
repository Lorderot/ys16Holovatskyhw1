package ua.yandex.mergesort.utilconcurrent;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Mykola Holovatsky
 */
public class TestAlgorithms {
    @Test
    public void testParallelMergeSort_checkTheCorrectness() {
        int N = 1000;
        Integer[] tests = new Integer[N];
        for (int i = 0; i < N; i++) {
            tests[i] = N - i - 1;
        }
        tests = Algorithms.parallelMergeSort(tests, Integer.class);
        for (int i = 0; i < N; i++) {
            int expected = i;
            int result = tests[i];
            assertEquals(expected, result);
        }

    }
}
