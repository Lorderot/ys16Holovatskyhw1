package ua.yandex.prodcons.utilconcurrent;

import ua.yandex.prodcons.CircledBuffer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Mykola Holovatsky
 */
public class UtilConcurrentCircledBuffer<E>  implements CircledBuffer<E> {
    private int bufferSize;
    private Object[] container;
    private volatile int theOldest = 0;
    private volatile int nextFreePosition = 0;
    private volatile boolean delayedIsEmpty = true;
    private volatile boolean delayedIsFull = false;
    private volatile boolean reallyIsEmpty = true;
    private volatile boolean reallyIsFull = false;
    private final ReentrantLock writing = new ReentrantLock();
    private final ReentrantLock reading = new ReentrantLock();
    private final Condition notEmpty = reading.newCondition();
    private final Condition notFull = writing.newCondition();


    public UtilConcurrentCircledBuffer() {
        bufferSize = 10;
        container = new Object[bufferSize];
    }

    public UtilConcurrentCircledBuffer(int size) {
        this.bufferSize = size;
        container = new Object[size];
    }

    public E poll() {
        E element;

        reading.lock();
        while (delayedIsEmpty) {
            try {
                notEmpty.await();
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

        reading.unlock();
        if (delayedIsFull) {

            writing.lock();
            if (!reallyIsFull) {
                delayedIsFull = false;
            }
            notFull.signalAll();
            writing.unlock();

        }
        return element;
    }

    public void put(E element) {

        writing.lock();
        try {
            while (delayedIsFull) {
                notFull.await();
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
        writing.unlock();

        if (delayedIsEmpty) {
            reading.lock();
            if (!reallyIsEmpty) {
                delayedIsEmpty = false;
            }
            notEmpty.signalAll();
            reading.unlock();
        }
    }

    public boolean isEmpty() {
        return reallyIsEmpty;
    }

    public boolean isFull() {
        return reallyIsFull;
    }

    public int getBufferSize() {
        return bufferSize;
    }
}
