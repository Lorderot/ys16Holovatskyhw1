package ua.yandex.prodcons.threads;

/**
 * @author Mykola Holovatsky
 */
public class CircledBuffer<E> {
    private int bufferSize;
    private Object[] container;
    private volatile int theOldest = 0;
    private volatile int nextFreePosition = 0;
    private volatile Flag isEmpty = new Flag(true);
    private volatile Flag isFull = new Flag(false);

    private class Flag {
        public volatile boolean value;

        public Flag(boolean value) {
            this.value = value;
        }
    }

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
        synchronized (isEmpty) {
            try {
                while (isEmpty.value || container[theOldest] == null) {
                    isEmpty.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            element = (E) container[theOldest];
            container[theOldest] = null;
            this.theOldest = (this.theOldest + 1) % bufferSize;
            if (this.theOldest == nextFreePosition) {
                isEmpty.value = true;
            }
        }
            if (isFull.value) {
                synchronized (isFull) {
                    isFull.notifyAll();
                    isFull.value = false;
                }
            }
            return element;
    }

    public void put(E element) {
        synchronized (isFull) {
            try {
                while (isFull.value || container[nextFreePosition] != null) {
                    isFull.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            container[nextFreePosition] = element;
            nextFreePosition = (nextFreePosition + 1) % bufferSize;
            if (nextFreePosition == theOldest) {
                isFull.value = true;
            }
        }
        if (isEmpty.value) {
            synchronized (isEmpty) {
                isEmpty.notifyAll();
                isEmpty.value = false;
            }
        }
    }

    public boolean isEmpty() {
        return  isEmpty.value;
    }

    public boolean isFull() {
        return isFull.value;
    }

    public int getBufferSize() {
        return bufferSize;
    }
}
