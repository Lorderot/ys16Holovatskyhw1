package ua.yandex.prodcons.threads;

import org.junit.Before;
import org.junit.Test;
import ua.yandex.prodcons.TestCircledBuffer;

/**
 * @author Mykola Holovatsky
 */
public class TestThreadsCircledBuffer {
    private TestCircledBuffer test;

    @Before
    public void init() {
        test = new TestCircledBuffer(ThreadsCircledBuffer.class);
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
