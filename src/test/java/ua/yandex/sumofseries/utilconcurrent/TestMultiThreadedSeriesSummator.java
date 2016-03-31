package ua.yandex.sumofseries.utilconcurrent;

import org.junit.Test;
import ua.yandex.sumofseries.threads.SeriesSummator;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

/**
 * @author Mykola Holovatsky
 */
public class TestMultiThreadedSeriesSummator {
    private MultiThreadedSeriesSummator summator =
            new MultiThreadedSeriesSummator(10,
                    new SeriesSummator(x -> Math.sin(x) * Math.cos(x)));
    @Test
    public void testSum_checkTheCorrectness()
            throws InterruptedException, ExecutionException {
        Double result = summator.sum(-10.0, 10.0);
        Double expected = 0.0;
        assertEquals(result, expected, 0.0001);
    }

}
