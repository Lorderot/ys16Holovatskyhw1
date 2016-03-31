package ua.yandex.sumofseries.threads;

/**
 * @author Mykola Holovatsky
 */
public class MultiThreadedSeriesSummator {
    private int threads;
    private SeriesSummator summator;

    private class SummatorThread extends Thread {
        private Double result = 0.0;
        private Double lowerBound;
        private Double upperBound;

        public SummatorThread(Double lowerBound, Double upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        @Override
        public void run() {
            result = summator.sum(lowerBound, upperBound);
            super.run();
        }

        public Double getResult() {
            return result;
        }
    }


    public MultiThreadedSeriesSummator(int threads, SeriesSummator summator) {
        this.threads = threads;
        this.summator = summator;
    }

    public Double sum(Double lowerBound, Double upperBound)
            throws InterruptedException {
        SummatorThread[] threadPool = new SummatorThread[threads];
        Double step = (upperBound - lowerBound) / threads;
        for (int i = 0; i < threads; i++) {
            threadPool[i] = new SummatorThread(lowerBound + i * step,
                    lowerBound + (i + 1) * step);
            threadPool[i].start();
        }
        for (int i = 0; i < threads; i++) {
            threadPool[i].join();
        }

        Double result = 0.0;
        for (int i = 0; i < threads; i++) {
            result += threadPool[i].getResult();
        }
        return result;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        if (threads <= 0) {
            throw new IllegalArgumentException();
        }
        this.threads = threads;
    }
}
