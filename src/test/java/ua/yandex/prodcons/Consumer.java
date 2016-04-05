package ua.yandex.prodcons;

/**
 * @author Mykola Holovatsky
 */
public class Consumer implements Runnable {
    public static volatile boolean work = true;
    public static volatile Long result = new Long(0);
    static volatile Integer tradesConsumed = 0;
    private static final Object sumLocker = new Object();
    private CircledBuffer<Integer> buffer;

    public Consumer(CircledBuffer<Integer> buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while (work || !buffer.isEmpty()) {
            Integer element = pollTheElement();
            synchronized (sumLocker) {
                result += element;
                tradesConsumed++;
            }
        }
    }

    public static Integer getTradesConsumed() {
        return tradesConsumed;
    }

    private Integer pollTheElement() {
        Integer element = buffer.poll();
        return element;
    }
}