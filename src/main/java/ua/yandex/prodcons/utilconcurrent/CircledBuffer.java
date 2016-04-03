package ua.yandex.prodcons.utilconcurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
    private final ReentrantLock writing = new ReentrantLock();
    private final ReentrantLock reading = new ReentrantLock();
    private final Condition notEmpty = reading.newCondition();
    private final Condition notFull = writing.newCondition();


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
        reading.lock();
        while (isEmpty) {
            try {
                notEmpty.await();
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
        reading.unlock();
        if (!isFull) {
            writing.lock();
            notFull.signalAll();
            writing.unlock();
        }
        return element;
    }

    public void put(E element) {
        writing.lock();
        try {
            while (isFull) {
                notFull.await();
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
        writing.unlock();
        if (!isEmpty) {
            reading.lock();
            notEmpty.signalAll();
            reading.unlock();
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
