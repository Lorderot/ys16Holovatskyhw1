package ua.yandex.prodcons.threads;

import java.util.Random;

/**
 * @author Mykola Holovatsky
 */
public class Producer implements Runnable {
    public static volatile Long expectedSum = new Long(0);
    public static volatile boolean work = true;
    private static final Object sumLocker = new Object();
    private static volatile Integer tradesProduced = 0;
    private int limit = 100000;
    private CircledBuffer<Integer> buffer;
    private Random generator = new Random(System.nanoTime());

    public Producer(CircledBuffer<Integer> buffer) {
        this.buffer = buffer;
    }

    public void putToTheBuffer(Integer element) {
        buffer.put(element);
    }

    Integer produceTheElement() {
        return generator.nextInt(limit);
    }

    public static Integer getTradesProduced() {
        return tradesProduced;
    }

    @Override
    public void run() {
        while (work) {
            Integer element = produceTheElement();
            putToTheBuffer(element);
            synchronized (sumLocker) {
                expectedSum += element;
                tradesProduced++;
            }
        }
    }
}
