package ua.yandex.ThreadPool;

import org.junit.Test;
import ua.yandex.threadpool.FixedThreadPool;
import static org.junit.Assert.assertEquals;


/**
 * @author Mykola Holovatsky
 */
public class TestFixedThreadPool {
    public static volatile int counter = 0;
    private final int numberOfTasks = 1000000;

    @Test
    public void testFixedThreadPool_TasksMoreThanThreads_OneThread() {
        int optimalNumberOfThreads = 1;
        FixedThreadPool pool = new FixedThreadPool(optimalNumberOfThreads);
        clear();
        submitAllTasks(pool);
        checkIfAllTasksHaveBeenRan(pool);
        assertEquals(numberOfTasks, counter);
    }

    @Test
    public void testFixedThreadPool_TasksMoreThanThreads_ManyThreads() {
        int optimalNumberOfThreads = 10000;
        FixedThreadPool pool = new FixedThreadPool(optimalNumberOfThreads);
        clear();
        submitAllTasks(pool);
        checkIfAllTasksHaveBeenRan(pool);
        assertEquals(numberOfTasks, counter);
    }

    private void submitAllTasks(FixedThreadPool pool) {
        for (int i = 0; i < numberOfTasks; i++) {
            pool.execute(() -> {
                synchronized (TestFixedThreadPool.class) {
                    counter++;
                }
            });
        }
        System.out.println("All tasks have been ran");
    }

    private void checkIfAllTasksHaveBeenRan(FixedThreadPool pool) {
        while (pool.hasAnyTask()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void clear() {
        counter = 0;
    }
}

