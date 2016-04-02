package ua.yandex.prodcons.utilconcurrent;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Mykola Holovatsky
 */
public class Producer implements Runnable {
    public static AtomicLong expectedSum = new AtomicLong(0);
    public static volatile boolean work = true;
    private static AtomicInteger tradesProduced = new AtomicInteger(0);
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
        return tradesProduced.get();
    }

    @Override
    public void run() {
        while (work) {
            Integer element = produceTheElement();
            putToTheBuffer(element);
            expectedSum.getAndUpdate(x -> x + element);
            tradesProduced.getAndIncrement();
        }
    }
}
