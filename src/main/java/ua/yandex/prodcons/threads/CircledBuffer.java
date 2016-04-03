package ua.yandex.prodcons.threads;

/**
 * @author Mykola Holovatsky
 */
public class CircledBuffer<E> {
    private int bufferSize;
    private Object[] container;
    private volatile int theOldest = 0;
    private volatile int nextFreePosition = 0;
    private volatile boolean isEmpty = true;
    private volatile boolean isFull = false;
    private final Object writing = new Object();
    private final Object reading = new Object();

    public CircledBuffer() {
        bufferSize = 10;
        container = new Object[bufferSize];
    }

    public CircledBuffer(int size) {
        this.bufferSize = size;
        container = new Object[size];
    }

    public E poll() {
        E element;
        synchronized (reading) {
            while (isEmpty) {
                try {
                    reading.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            element = (E) container[theOldest];
            theOldest = (theOldest + 1) % bufferSize;
            if (theOldest == nextFreePosition) {
                isEmpty = true;
            }
            isFull = false;
        }
        if (!isFull) {
            synchronized (writing) {
                writing.notifyAll();
            }
        }
        return element;
    }

    public void put(E element) {
        synchronized (writing) {
            try {
                while (isFull) {
                    writing.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            container[nextFreePosition] = element;
            nextFreePosition = (nextFreePosition + 1) % bufferSize;
            if (nextFreePosition == theOldest) {
                isFull = true;
            }
            isEmpty = false;
        }
        if (!isEmpty) {
            synchronized (reading) {
                reading.notifyAll();
            }
        }

    }

    public boolean isEmpty() {
        return  isEmpty;
    }

    public boolean isFull() {
        return isFull;
    }

    public int getBufferSize() {
        return bufferSize;
    }
}