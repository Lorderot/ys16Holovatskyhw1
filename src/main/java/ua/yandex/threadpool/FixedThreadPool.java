package ua.yandex.threadpool;

import ua.yandex.prodcons.threads.CircledBuffer;

/**
 * @author Mykola Holovatsky
 */
public class FixedThreadPool {
    public static final int MAX_QUEUE_SIZE = 100000000;
    public static final int DEFAULT_QUEUE_SIZE = 10000;
    private final int poolSize;
    private final int queueSize;
    private Thread[] pool;
    private final CircledBuffer<Runnable> taskQueue;

    public FixedThreadPool(int size) {
        this(size, DEFAULT_QUEUE_SIZE);
    }

    public FixedThreadPool(int size, int queueSize) {
        poolSize = size;
        if (queueSize < MAX_QUEUE_SIZE) {
            this.queueSize = queueSize;
        } else {
            this.queueSize = MAX_QUEUE_SIZE;
        }
        taskQueue = new CircledBuffer<>(queueSize);
        pool = new Thread[poolSize];
        for (int i = 0; i < poolSize; i++) {
            pool[i] = new Thread(() -> {
                while (true) {
                    if (taskQueue.isEmpty()) {
                        synchronized (taskQueue) {
                            while (taskQueue.isEmpty()) {
                                try {
                                    taskQueue.wait();
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
        if (taskQueue.isEmpty()) {
            synchronized (taskQueue) {
                taskQueue.notifyAll();
                taskQueue.put(task);
            }
        } else {
            taskQueue.put(task);
        }
    }
}
