package ua.yandex.prodcons.threads;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
/**
 * @author Mykola Holovatsky
 */
public class TestCircledBuffer {
    @Test
    public void testCircledBuffer_CheckNotifyingReadingThreads() {
        CircledBuffer<Integer> buffer = new CircledBuffer<>(2);
        clear();
        try {
            Thread a = new Thread(new Consumer(buffer));
            a.start();
            Thread.sleep(100);
            buffer.put(100);
            Thread.sleep(1000);
            assertEquals(new Long(100), Consumer.result);
            assertEquals(new Integer(1), Consumer.getTradesConsumed());
            assertEquals(true, buffer.isEmpty());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCircledBuffer_CheckNotifyingWritingThreads() {
        clear();
        CircledBuffer<Integer> buffer = new CircledBuffer<>(2);
        try {
            buffer.put(100);
            buffer.put(100);
            buffer.poll();
            assertEquals(false, buffer.isFull());
            new Thread(new Producer(buffer)).start();
            Thread.sleep(1000);
            assertNotEquals(new Long(0), Producer.expectedSum);
            assertNotEquals(new Integer(0), Producer.getTradesProduced());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCircledBuffer_BlockingWhenFullOrEmpty() {
        int numberOfTests = 100;
        CircledBuffer<Integer> buffer = new CircledBuffer<>(1);
        for (int randomTests = 1; randomTests <= numberOfTests; randomTests++) {
            clear();
            startOperationsBetweenProducersAndConsumers(randomTests * 20,
                    buffer, 1000, 1000);
        }
    }

    @Test
    public void testCircledBuffer_HugeCapacity() {
        clear();
        CircledBuffer<Integer> buffer = new CircledBuffer<>(100000000);
        startOperationsBetweenProducersAndConsumers(10000, buffer, 1000, 10000);
    }

    private void startOperationsBetweenProducersAndConsumers(
            int N, CircledBuffer<Integer> buffer,
            int workTime, int timeToComplete) {
        for (int i = 0; i < N; i++) {
            new Thread(new Producer(buffer)).start();
            new Thread(new Consumer(buffer)).start();
        }
        try {
            Thread.sleep(workTime);
            Producer.work = false;
            Consumer.work = false;
            Thread.sleep(timeToComplete);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(Producer.expectedSum, Consumer.result);
        assertEquals(Producer.getTradesProduced(), Consumer.getTradesConsumed());
        assertEquals(true, buffer.isEmpty());
        System.out.println("Test with " + N + " Consumers and Producers has been done");
    }

    private void clear() {
        Producer.tradesProduced = 0;
        Consumer.tradesConsumed = 0;
        Producer.work = true;
        Consumer.work = true;
        Producer.expectedSum = new Long(0);
        Consumer.result = new Long(0);
    }
}
