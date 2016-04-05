package ua.yandex.prodcons.threads;

import ua.yandex.prodcons.CircledBuffer;

/**
 * @author Mykola Holovatsky
 */
public class ThreadsCircledBuffer<E> implements CircledBuffer<E> {
    private int bufferSize;
    private Object[] container;
    private volatile int theOldest = 0;
    private volatile int nextFreePosition = 0;
    private volatile boolean delayedIsEmpty = true;
    private volatile boolean delayedIsFull = false;
    private volatile boolean reallyIsEmpty = true;
    private volatile boolean reallyIsFull = false;
    final Object writing = new Object();
    final Object reading = new Object();

    public ThreadsCircledBuffer() {
        bufferSize = 10;
        container = new Object[bufferSize];
    }

    public ThreadsCircledBuffer(int size) {
        this.bufferSize = size;
        container = new Object[size];
    }

    public E poll() {
        E element;
        synchronized (reading) {
            while (delayedIsEmpty) {
                try {
                    reading.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            element = (E) container[theOldest];
            if ((theOldest + 1) % bufferSize == nextFreePosition) {
                delayedIsEmpty = true;
                reallyIsEmpty = true;
            }
            theOldest = (theOldest + 1) % bufferSize;
            reallyIsFull = false;
        }
        if (delayedIsFull) {
            synchronized (writing) {
                if (!reallyIsFull) {
                    delayedIsFull = false;
                }
                writing.notifyAll();
            }
        }
        return element;
    }

    public void put(E element) {
        synchronized (writing) {
            try {
                while (delayedIsFull) {
                    writing.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            container[nextFreePosition] = element;
            if ((nextFreePosition + 1) % bufferSize == theOldest) {
                delayedIsFull = true;
                reallyIsFull = true;
            }
            nextFreePosition = (nextFreePosition + 1) % bufferSize;
            reallyIsEmpty = false;
        }
        if (delayedIsEmpty) {
            synchronized (reading) {
                if (!reallyIsEmpty) {
                    delayedIsEmpty = false;
                }
                reading.notifyAll();
            }
        }

    }

    public boolean isEmpty() {
        return reallyIsEmpty;
    }

    public boolean isFull() {
        return reallyIsFull;
    }

    public int getBufferSize() {
        if (delayedIsEmpty) {
            return 0;
        }
        return bufferSize - theOldest + nextFreePosition;
    }
}