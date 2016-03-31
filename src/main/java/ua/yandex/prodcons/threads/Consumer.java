package ua.yandex.prodcons.threads;

/**
 * @author Mykola Holovatsky
 */
public class Consumer implements Runnable {
    public static volatile boolean work = true;
    public static Integer result = 0;
    public static Locker locker = new Locker();
    private static class Locker {
        public synchronized void increment(Integer element) {
            result += element;
        }
    }
    private CircledBuffer<Integer> buffer;

    public Consumer(CircledBuffer<Integer> buffer) {
        this.buffer = buffer;
    }

    private Integer pollTheElement() {
        Integer element = buffer.poll();
        return element;
    }

    @Override
    public void run() {
        while (work || !buffer.isEmpty()) {
            Integer element = pollTheElement();
            locker.increment(element);
        }
    }
}