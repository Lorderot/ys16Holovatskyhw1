package ua.yandex.threadpool;

import ua.yandex.prodcons.threads.ThreadsCircledBuffer;

/**
 * @author Mykola Holovatsky
 */
public class FixedThreadPool {
    public static final int MAX_QUEUE_SIZE = 100000000;
    public static final int DEFAULT_QUEUE_SIZE = 10000;
    private final int poolSize;
    private static volatile int waiters = 0;
    private Thread[] pool;
    private final ThreadsCircledBuffer<Runnable> taskQueue;

    public FixedThreadPool(int size) {
        this(size, DEFAULT_QUEUE_SIZE);
    }

    public FixedThreadPool(int size, int queueSize) {
        poolSize = size;
        if (queueSize > MAX_QUEUE_SIZE) {
            queueSize = MAX_QUEUE_SIZE;
        }
        taskQueue = new ThreadsCircledBuffer<>(queueSize);
        pool = new Thread[poolSize];
        for (int i = 0; i < poolSize; i++) {
            pool[i] = new Thread(() -> {
                while (true) {
                    if (taskQueue.isEmpty()) {
                        synchronized (taskQueue) {
                            while (taskQueue.isEmpty()) {
                                try {
                                    waiters++;
                                    taskQueue.wait();
                                    waiters--;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    taskQueue.poll().run();
                }
            });
            pool[i].start();
        }
    }

    public void execute(Runnable task) {
        if (waiters > 0) {
            synchronized (taskQueue) {
                taskQueue.notifyAll();
            }
        }
        taskQueue.put(task);
    }

    public boolean hasAnyTask() {
        return !taskQueue.isEmpty();
    }
}
