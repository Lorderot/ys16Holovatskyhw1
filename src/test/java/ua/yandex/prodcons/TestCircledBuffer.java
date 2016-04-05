package ua.yandex.prodcons;

import ua.yandex.prodcons.threads.ThreadsCircledBuffer;

import java.lang.reflect.Constructor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
/**
 * @author Mykola Holovatsky
 */
public class TestCircledBuffer {
    private Class<? extends CircledBuffer> bufferType;

    public TestCircledBuffer(Class<? extends CircledBuffer> bufferType) {
        this.bufferType = bufferType;
    }

    public void testCircledBuffer_CheckNotifyingReadingThreads()
            throws Exception {
        CircledBuffer<Integer> buffer = createBuffer(2);
        clear();
        try {
            new Thread(new Consumer(buffer)).start();
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

    public void testCircledBuffer_CheckNotifyingWritingThreads()
            throws Exception {
        clear();
        CircledBuffer<Integer> buffer = createBuffer(2);
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

    public void testCircledBuffer_BlockingWhenFullOrEmpty()
            throws Exception {
        int numberOfTests = 10;
        CircledBuffer<Integer> buffer = createBuffer(1);
        for (int randomTests = 1; randomTests <= numberOfTests; randomTests++) {
            startOperationsBetweenProducersAndConsumers(randomTests * 1000,
                    buffer, 1000);
            System.out.println();
        }
    }

    public void testCircledBuffer_HugeCapacity() throws Exception {
        CircledBuffer<Integer> buffer = createBuffer(1000000);
        startOperationsBetweenProducersAndConsumers(20000, buffer, 5000);
    }

    private void startOperationsBetweenProducersAndConsumers(
            int N, CircledBuffer<Integer> buffer, int workTime) {
        clear();
        Thread[] threads = startTheConsumersAndProducersThreads(N, buffer);
        try {
            Thread.sleep(workTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Producer.work = false;
        waitProducersToFinishTheWork(N, threads);
        waitConsumersToEmptyTheBuffer(buffer);
        waitConsumersToFinishCalculations(N, threads);
        assertEquals(Producer.getTradesProduced(), Consumer.getTradesConsumed());
        assertEquals(Producer.expectedSum, Consumer.result);
        assertEquals(true, buffer.isEmpty());
        System.out.println("Trades produced: " + Producer.tradesProduced
                + " Trades consumed: " + Consumer.tradesConsumed);
        System.out.println("Test with " + N + " Consumers and Producers has been done");
    }

    private Thread[] startTheConsumersAndProducersThreads(int N, CircledBuffer buffer) {
        Thread[] threads = new Thread[2 * N];
        for (int i = 0; i < N; i++) {
            threads[2 * i] = new Thread(new Producer(buffer), "Producer");
            threads[2 * i + 1] = new Thread(new Consumer(buffer), "Consumer");
            threads[2 * i].start();
            threads[2 * i + 1].start();
        }
        System.out.println("All threads have been started!");
        return threads;
    }

    private void waitProducersToFinishTheWork(int N, Thread[] threads) {
        for (int i = 0; i < N; i++) {
            String status = threads[2 * i].getState().toString();
            while(!status.equals("TERMINATED")) {
                try {
                    status = threads[2 * i].getState().toString();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("All producers have finished the work!");
    }

    private void waitConsumersToEmptyTheBuffer(CircledBuffer buffer) {
        while(!buffer.isEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Buffer is empty!");
    }

    private void waitConsumersToFinishCalculations(int N, Thread[] threads) {
        for (int i = 0; i < N; i++) {
            String status = threads[2 * i + 1].getState().toString();
            while(!status.equals("WAITING")) {
                try {
                    status = threads[2 * i + 1].getState().toString();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("All consumers have finished their calculations!");
    }

    private CircledBuffer<Integer> createBuffer(int size)
            throws Exception {
        Constructor constructorWithSizeParameter = null;
        CircledBuffer<Integer> buffer;
        try {
            constructorWithSizeParameter = bufferType
                    .getConstructor(int.class);
        } catch (NoSuchMethodException noIntParameter) {
            try {
                constructorWithSizeParameter = bufferType
                        .getConstructor(Integer.class);
            } catch (NoSuchMethodException noIntegerParameter) {
                noIntParameter.printStackTrace();
                noIntegerParameter.printStackTrace();
            }
        }
        buffer = (CircledBuffer<Integer>)
                constructorWithSizeParameter.newInstance(size);
        return buffer;
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
