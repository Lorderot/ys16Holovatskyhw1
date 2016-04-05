package ua.yandex.prodcons.utilconcurrent;

import org.junit.Before;
import org.junit.Test;
import ua.yandex.prodcons.TestCircledBuffer;

/**
 * @author Mykola Holovatsky
 */
public class TestUtilConcurrentCircledBuffer {
    private TestCircledBuffer test;

    @Before
    public void init() {
        test = new TestCircledBuffer(UtilConcurrentCircledBuffer.class);
    }

    @Test
    public void testCircledBuffer_CheckNotifyingReadingThreads() {
        try {
            test.testCircledBuffer_CheckNotifyingReadingThreads();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCircledBuffer_CheckNotifyingWritingThreads() {
        try {
            test.testCircledBuffer_CheckNotifyingWritingThreads();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCircledBuffer_BlockingWhenFullOrEmpty() {
        try {
            test.testCircledBuffer_BlockingWhenFullOrEmpty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCircledBuffer_HugeCapacity() {
        try {
            test.testCircledBuffer_HugeCapacity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
