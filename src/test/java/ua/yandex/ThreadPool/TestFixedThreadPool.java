package ua.yandex.ThreadPool;

import org.junit.Test;
import ua.yandex.threadpool.FixedThreadPool;
import static org.junit.Assert.assertEquals;


/**
 * @author Mykola Holovatsky
 */
public class TestFixedThreadPool {
    public static volatile int counter = 0;
    private final int optimalNumberOfThreads = 4;
    private FixedThreadPool pool = new FixedThreadPool(optimalNumberOfThreads);
    @Test
    public void testFixedThreadPool_TasksMoreThanThreads() {
        int numberOfTasks = 100000;
        for (int i = 0; i < numberOfTasks; i++) {
            pool.execute(() -> {
                synchronized (TestFixedThreadPool.class) {
                    counter++;
                    System.out.println(counter);
                }
            });
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(numberOfTasks, counter);
    }

}
