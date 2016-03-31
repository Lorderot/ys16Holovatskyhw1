package ua.yandex.prodcons.threads;

import java.util.Random;

/**
 * @author Mykola Holovatsky
 */
public class Producer implements Runnable {
    public static Integer expected = 0;
    public static Locker locker = new Locker();
    public static volatile boolean work = true;
    private int limit = 100000;
    private CircledBuffer<Integer> buffer;
    private Random generator = new Random(System.nanoTime());

    private static class Locker {
        public synchronized void increment(Integer element) {
            expected += element;
        }
    }

    public Producer(CircledBuffer<Integer> buffer) {
        this.buffer = buffer;
    }

    public void putToTheBuffer(Integer element) {
        buffer.put(element);
    }

    Integer produceTheElement() {
        return generator.nextInt(limit);
    }

    @Override
    public void run() {
        while (work) {
            Integer element = produceTheElement();
            putToTheBuffer(element);
            locker.increment(element);
        }
    }
}
