package ua.yandex.prodcons.threads;

/**
 * @author Mykola Holovatsky
 */
public class Consumer implements Runnable {
    public static volatile boolean work = true;
    public static volatile Long result = new Long(0);
    private static final Object sumLocker = new Object();
    private static volatile Integer tradesConsumed = 0;
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
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
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