package ua.yandex.prodcons.utilconcurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Mykola Holovatsky
 */
public class Consumer implements Runnable {
    public static volatile boolean work = true;
    public static AtomicLong result = new AtomicLong(0);
    static AtomicInteger tradesConsumed = new AtomicInteger(0);
    private CircledBuffer<Integer> buffer;

    public Consumer(CircledBuffer<Integer> buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while (work || !buffer.isEmpty()) {
            Integer element = pollTheElement();
            result.getAndUpdate(x -> x + element);
            tradesConsumed.getAndIncrement();
        }
    }

    public static Integer getTradesConsumed() {
        return tradesConsumed.get();
    }

    private Integer pollTheElement() {
        Integer element = buffer.poll();
        return element;
    }
}