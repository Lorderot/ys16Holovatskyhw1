package ua.yandex.lockfree;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mykola Holovatsky
 */
public class SequenceOfPowers {
    private BigInteger basis = new BigInteger(new byte[]{0,2});
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    public BigInteger next() {
        int index = currentIndex.get();
        while (!currentIndex.compareAndSet(index, index + 1)) {
            index = currentIndex.get();
        }
        return basis.pow(index);
    }

}
