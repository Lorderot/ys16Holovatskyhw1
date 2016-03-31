package ua.yandex.sumofseries.threads;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Mykola Holovatsky
 */
public class TestSeriesSummator {
    private SeriesSummator summator =
            new SeriesSummator(x -> Math.sin(x) * Math.cos(x));
    @Test
    public void testSum_checkTheCorrectness() {
        Double result = summator.sum(-10.0, 10.0);
        Double expected = 0.0;
        assertEquals(result, expected, 0.0001);
    }
}
