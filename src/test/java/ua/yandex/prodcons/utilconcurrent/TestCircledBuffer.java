package ua.yandex.prodcons.utilconcurrent;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
/**
 * @author Mykola Holovatsky
 */
public class TestCircledBuffer {
    private CircledBuffer<Integer> buffer = new CircledBuffer<>(1);
    private int numberOfTests = 100;
    @Test
    public void testCircledBuffer_BlockingWhenFullOrEmpty() {
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
