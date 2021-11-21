package cpen221.mp3;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class Tests {
    public static final int CAPACITY = 5;
    public static final int TIME_TO_LIVE = 2;
    public static final int ONE_SEC = 1000;

    // Test buffer put and get (in very simple case)
    @Test
    public void testPut_Get() {
        FSFTBuffer<Bufferable_Testing> bufferPutGet = new FSFTBuffer<>(CAPACITY, TIME_TO_LIVE);
        Bufferable_Testing bufferaleInt = new Bufferable_Testing(10);
        bufferPutGet.put(bufferaleInt);
        try {
            Assert.assertEquals("[FSFT Buffer testPut_Get] : inserted object does not match",
                bufferaleInt, bufferPutGet.get("10"));
        } catch (Exception e) {
            fail("[FSFTBuffer testPut_Get]: No exception expected");
        }
    }

    // Test whether buffer remove element when time-out
    @Test
    public void testTimeOut() {
        FSFTBuffer<Bufferable_Testing> timeOutBuffer = new FSFTBuffer<>(CAPACITY, TIME_TO_LIVE);
        Bufferable_Testing ten = new Bufferable_Testing(10);

        timeOutBuffer.put(ten);

        try {
            Thread.sleep(ONE_SEC * (2 * TIME_TO_LIVE));
        } catch (InterruptedException e) {
            fail("[FSFT testTimeOut] : should not have Interrupted Exception");
        }

        try {
            timeOutBuffer.get(ten.id());
            fail("[FSFT testTImeOut : object in the buffer didn't timed-out");
        } catch (Exception e) {
            // TEST PASSED
        }


    }
    // Test touch method works

    // Test update method works

    // Test Buffer capacity works?

    // Inserting order maintained?
    @Test
    public void testInsertingOrder() {
        FSFTBuffer<Bufferable_Testing> bufferUnderTest = new FSFTBuffer<>(10, 3);
        List<Bufferable_Testing> contents = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            Bufferable_Testing content = new Bufferable_Testing(i);
            bufferUnderTest.put(content);
            contents.add(content);
        }

        try {
            for (Bufferable_Testing b : contents) {
                Assert.assertEquals(b, bufferUnderTest.get(b.id()));
            }
        } catch (Exception e) {
            fail("[FSFT Buffer : testInsertOrder] TEST FAIL : No Exception Expected");
        }
    }
}


