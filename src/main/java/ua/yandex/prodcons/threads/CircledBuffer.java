package ua.yandex.prodcons.threads;

import java.util.ArrayList;

/**
 * @author Mykola Holovatsky
 */
public class CircledBuffer<E> {
    private int bufferSize;
    private ArrayList<E> container;
    private int theOldest;
    private int nextFreePosition = 0;
    private volatile Integer theFullness = 0;

    public CircledBuffer() {
        bufferSize = 10;
        container = new ArrayList<>(bufferSize);
    }

    public CircledBuffer(int size) {
        this.bufferSize = size;
        container = new ArrayList<>(size);
    }

    public synchronized E poll() {
        try {
            while (isEmpty()) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int theOldest = this.theOldest;
        this.theOldest = (this.theOldest + 1) % bufferSize;
        synchronized (theFullness) {
            theFullness--;
        }
        notifyAll();
        return container.get(theOldest);
    }

    public synchronized void put(E element) {
        try {
            while (isFull()) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (container.size() != bufferSize) {
            container.add(nextFreePosition, element);
        } else {
            container.set(nextFreePosition, element);
        }
        nextFreePosition = (nextFreePosition + 1) % bufferSize;
        synchronized (theFullness) {
            theFullness++;
        }
        notifyAll();
    }

    public boolean isEmpty() {
        return theFullness == 0;
    }

    public boolean isFull() {
        return theFullness == bufferSize;
    }

    public int getBufferSize() {
        return bufferSize;
    }
}
