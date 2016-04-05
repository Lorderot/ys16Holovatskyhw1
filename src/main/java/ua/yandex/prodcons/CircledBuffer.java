package ua.yandex.prodcons;

/**
 * @author Mykola Holovatsky
 */
public interface CircledBuffer<E> {
    E poll();

    void put(E element);

    boolean isEmpty();

    boolean isFull();
}
