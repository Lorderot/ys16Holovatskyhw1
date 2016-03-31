package ua.yandex.sumofseries.utilconcurrent;

import ua.yandex.sumofseries.threads.SeriesSummator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Mykola Holovatsky
 */
public class MultiThreadedSeriesSummator {
    private int threads;
    private SeriesSummator summator;
    private ExecutorService executorService;

    private class SummatorThread implements Callable<Double> {
        private Double lowerBound;
        private Double upperBound;

        public SummatorThread(Double lowerBound, Double upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        @Override
        public Double call() throws Exception {
            return summator.sum(lowerBound, upperBound);
        }
    }

    public MultiThreadedSeriesSummator(int threads, SeriesSummator summator) {
        this.threads = threads;
        this.summator = summator;
        executorService = Executors.newFixedThreadPool(threads);
    }

    public Double sum(Double lowerBound, Double upperBound)
            throws InterruptedException, ExecutionException {
        Double result = 0.0;
        Double step = (upperBound - lowerBound) / threads;
        ArrayList<SummatorThread> jobs = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            jobs.add(new SummatorThread(lowerBound + i * step,
                    lowerBound + (i + 1) * step));
        }
        List<Future<Double>> responses = executorService.invokeAll(jobs);
        for (Future<Double> i : responses) {
            result += i.get();
        }
        executorService.shutdown();
        return result;
    }
}
