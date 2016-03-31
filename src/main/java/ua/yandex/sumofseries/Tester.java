package ua.yandex.sumofseries;

import ua.yandex.sumofseries.utilconcurrent.MultiThreadedSeriesSummator;
import ua.yandex.sumofseries.threads.SeriesSummator;


/**
 * @author Mykola Holovatsky
 */
public class Tester {
    private static final int N = 30;
    private static final double lowerBound = -100;
    private static final double upperBound = 100;
    private SeriesSummator summator;

    public Tester(SeriesSummator summator) {
        this.summator = summator;
    }

    public int theBestAmountOfThreads() throws Exception {
        double[] timeForExecution = new double[N];
        for (int i = 0; i < N; i++) {
            long start = System.nanoTime();
            new MultiThreadedSeriesSummator(i + 1, summator)
                    .sum(lowerBound, upperBound);
            long end = System.nanoTime();
            timeForExecution[i] = end - start;
        }

        double min = timeForExecution[0];
        int threads = 1;
        for (int i = 1; i < N; i++) {
            if (timeForExecution[i] < min) {
                min = timeForExecution[i];
                threads = i + 1;
            }
        }
        return threads;
    }

    public static void main(String[] args) throws Exception {
        Tester tester = new Tester(
                new SeriesSummator(x -> Math.sin(x) * Math.cos(x)));
        System.out.println(tester.theBestAmountOfThreads());
    }
}
