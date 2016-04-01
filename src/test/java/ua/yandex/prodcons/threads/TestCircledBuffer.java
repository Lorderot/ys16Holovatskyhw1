package ua.yandex.prodcons.threads;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
/**
 * @author Mykola Holovatsky
 */
public class TestCircledBuffer {
    private static final int SIZE = 20;
    private CircledBuffer<Integer> buffer = new CircledBuffer<>(SIZE);
    private Random generator = new Random(System.nanoTime());
    private int limit = 1000;

    @Test
    public void testCircledBuffer_SingleThreadCorrectness() {
        int numberOfTrades = SIZE;
        int expectedSum = 0;
        for (int i = 0; i < numberOfTrades; i++) {
            Integer element = generator.nextInt(limit);
            buffer.put(element);
            expectedSum += element;
        }
        int result = 0;
        for (int i = 0; i < numberOfTrades; i++) {
            result += buffer.poll();
        }
        assertEquals(expectedSum, result);
    }

    @Test
    public void testCircledBuffer_Blocking() {
        int numberOfTrades = 100;
        Integer[] produced = new Integer[numberOfTrades];
        int expectedSum = 0;
        for (int i = 0; i < numberOfTrades; i++) {
            Integer element = generator.nextInt(limit);
            produced[i] = element;
            expectedSum += element;
        }
        for (int i = 0; i < numberOfTrades; i++) {
            final int j = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    buffer.put(produced[j]);
                }
            }).start();
        }
        int result = 0;
        for (int i = 0; i < numberOfTrades; i++) {
            while (buffer.isEmpty()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            result += buffer.poll();
        }
        assertEquals(expectedSum, result);
    }

    @Test
    public void testCircledBuffer_ProducerConsumerPatternTest() {
        int numberOfTests = 10;
        for (int randomTests = 1; randomTests <= numberOfTests; randomTests++) {
            startOperationsBetweenProducersAndConsumers(randomTests * 20);
        }
    }

    private void startOperationsBetweenProducersAndConsumers(int N) {
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
        assertEquals(Producer.expectedSum, Consumer.result);
        assertEquals(Producer.getTradesProduced(), Consumer.getTradesConsumed());
        assertEquals(true, buffer.isEmpty());
    }
}
