package ua.yandex.lockfree;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import ua.yandex.prodcons.CircledBuffer;
import ua.yandex.prodcons.threads.ThreadsCircledBuffer;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mykola Holovatsky
 */
public class TestSequenceOfPowers {
    private SequenceOfPowers sequence = new SequenceOfPowers();
    private Set<String> setOfPowers = new HashSet<>();

    @Test
    public void testSequenceOfPowers_SingleThreadExecution() {
        int amountOfPowers = 10000;
        CircledBuffer<BigInteger> buffer = new ThreadsCircledBuffer<>(amountOfPowers);
        initializing(amountOfPowers);
        for (int i = 0; i < amountOfPowers; i++) {
            BigInteger element = sequence.next();
            buffer.put(element);
        }
        System.out.println("All numbers have been put");
        boolean expect = true;
        boolean result = pollResultsAndCheckForMissingValuesAndCorrectness(amountOfPowers, buffer);
        assertEquals(expect, result);

    }

    @Test
    public void testSequenceOfPowers_multiThreadsExecution() {
        int amountOfPowers = 20000;
        CircledBuffer<BigInteger> buffer = new ThreadsCircledBuffer<>(amountOfPowers);
        initializing(amountOfPowers);
        startTheThreads(buffer, amountOfPowers);
        boolean expected = true;
        boolean result = pollResultsAndCheckForMissingValuesAndCorrectness(
                amountOfPowers, buffer);
        assertEquals(expected, result);
    }

    private void startTheThreads(CircledBuffer<BigInteger> buffer,
                                 int amountOfPowers) {
        for (int i = 0; i < amountOfPowers; i++) {
            new Thread(() -> {
                BigInteger element = sequence.next();
                buffer.put(element);
            }).start();
        }
        System.out.println("All threads have been started");
    }

    private void initializing(int amountOfPowers) {
        setOfPowers = new HashSet<>();
        for (int i = 0; i < amountOfPowers; i++) {
            BigInteger elem = new BigInteger(new byte[]{0, 2}).pow(i);
            setOfPowers.add(elem.toString());
        }
    }

    private boolean pollResultsAndCheckForMissingValuesAndCorrectness(
            int amountOfPowers, CircledBuffer<BigInteger> buffer) {
        try {
            for (int i = 0; i < amountOfPowers; i++) {
                BigInteger element = buffer.poll();
                assertEquals(true, setOfPowers.contains(element.toString()));
                setOfPowers.remove(element.toString());
            }
            assertEquals(0, setOfPowers.size());
        } catch (AssertionError e) {
            return false;
        }
        return true;
    }
}
