package ua.yandex.prodcons.utilconcurrent;

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
        try {
            new Thread(new Consumer(buffer)).start();
            Thread.sleep(100);
            buffer.put(100);
            Thread.sleep(1000);
            assertEquals(100, Consumer.result.get());
            assertEquals(new Integer(1), Consumer.getTradesConsumed());
            assertEquals(true, buffer.isEmpty());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCircledBuffer_CheckNotifyingWritingThreads() {
        CircledBuffer<Integer> buffer = new CircledBuffer<>(2);
        try {
            buffer.put(100);
            buffer.put(100);
            buffer.poll();
            assertEquals(false, buffer.isFull());
            new Thread(new Producer(buffer)).start();
            Thread.sleep(1000);
            assertNotEquals(0, Producer.expectedSum.get());
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
            startOperationsBetweenProducersAndConsumers(randomTests * 20, buffer);
        }
    }

    private void startOperationsBetweenProducersAndConsumers(int N, CircledBuffer<Integer> buffer) {
        for (int i = 0; i < N; i++) {
            new Thread(new Producer(buffer)).start();
            new Thread(new Consumer(buffer)).start();
        }
        try {
            Thread.sleep(1000);
            Producer.work = false;
            Consumer.work = false;
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(Producer.expectedSum.get(), Consumer.result.get());
        assertEquals(Producer.getTradesProduced(), Consumer.getTradesConsumed());
        assertEquals(true, buffer.isEmpty());
        System.out.println("Test with " + N + " Consumers and Producers has been done");
    }
}
