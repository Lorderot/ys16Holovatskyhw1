package ua.yandex.mergesort.threads;

import java.lang.reflect.Array;

/**
 * @author Mykola Holovatsky
 */
public class Algorithms {
    private static int optimalNumberOfThreads = 4;
    private static volatile int updateCache = 0;
    public static <E extends Comparable<E>> E[] parallelMergeSort(E[] a, Class<E> clazz) {
        return parallelMergeSort(clazz, a, 0, a.length - 1);
    }

    public static <E extends Comparable<E>> E[] parallelMergeSort(Class<E> clazz,
                                                                  E[] a, int fromIndex, int toIndex) {
        E[] result = (E[]) Array.newInstance(clazz, a.length);
        parallelMergeSort(clazz, a, fromIndex, toIndex, result, 0);
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <E extends Comparable<E>> void parallelMergeSort(Class<E> clazz,
                                                                    E[] a, int fromIndex, int toIndex, E[] storage, int start) {
        int numberOfElements = toIndex - fromIndex + 1;
        if (numberOfElements == 1) {
            storage[start] = a[fromIndex];
        } else {
            E[] newStorage = (E[]) Array.newInstance(clazz, numberOfElements);
            int median = (fromIndex + toIndex) / 2;
            int amountOfElementsBeforeMedian = median - fromIndex + 1;
            Thread thread = null;
            if (Thread.activeCount() > optimalNumberOfThreads) {
                parallelMergeSort(clazz, a, fromIndex, median, newStorage, 0);
            } else {
                thread = new Thread(() -> parallelMergeSort(clazz, a,
                        fromIndex, median, newStorage, 0));
                thread.start();
            }
            parallelMergeSort(clazz, a, median + 1, toIndex, newStorage,
                    amountOfElementsBeforeMedian);
            try {
                if (thread != null) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateCache = 0;
            parallelMerge(newStorage, 0, amountOfElementsBeforeMedian - 1,
                    amountOfElementsBeforeMedian,
                    numberOfElements - 1, storage, start);
        }
    }

    private static <E extends Comparable<E>> void parallelMerge (
            E[] source, int start1, int end1,
            int start2, int end2, E[] destination, int start3) {
        int n1 = end1 - start1 + 1;
        int n2 = end2 - start2 + 1;
        if (n1 < n2) {
            int swapper = n1;
            n1 = n2;
            n2 = swapper;
            swapper = end1;
            end1 = end2;
            end2 = swapper;
            swapper = start1;
            start1 = start2;
            start2 = swapper;
        }
        if (n1 == 0) {
            return;
        } else {
            int median1 = (start1 + end1) / 2;
            int median2 = medianBinarySearch(
                    source[median1], source, start2, end2);
            int medianIndexInDestination = start3 +
                    (median1 - start1) + (median2 - start2);
            destination[medianIndexInDestination] = source[median1];
            final int s1 = start1;
            final int s2 = start2;
            Thread thread = null;
            if (Thread.activeCount() > optimalNumberOfThreads) {
                parallelMerge(source, s1, median1 - 1,
                        s2, median2 - 1, destination, start3);
            } else {
                thread = new Thread(() ->
                        parallelMerge(source, s1, median1 - 1,
                                s2, median2 - 1, destination, start3));
                thread.start();
            }
            parallelMerge(source, median1 + 1, end1, median2, end2,
                    destination, medianIndexInDestination + 1);
            try {
                if (thread != null) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static <E extends Comparable<E>> int medianBinarySearch (
            E element, E[] a, int left, int right) {
        int leftEdge = left;
        int rightEdge = right + 1;
        while (leftEdge < rightEdge) {
            int middle = (leftEdge + rightEdge) / 2;
            if (element.compareTo(a[middle]) <= 0) {
                rightEdge = middle;
            } else {
                leftEdge = middle + 1;
            }
        }
        return rightEdge;
    }
}
