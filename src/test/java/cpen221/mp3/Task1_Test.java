package cpen221.mp3;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class Task1_Test {
    public static final int CAPACITY = 5;
    public static final int FIVE_SEC_TIME_TO_LIVE = 5;
    public static final int TEN_SEC_TIME_TO_LIVE = 10;
    public static final int ONE_SEC = 1000;


    // Test buffer put and get (in very simple case)
    @Test
    public void testPut_Get() {
        FSFTBuffer<Bufferable_int_Testing> bufferPutGet = new FSFTBuffer<>(CAPACITY, FIVE_SEC_TIME_TO_LIVE);
        Bufferable_int_Testing bufferaleInt = new Bufferable_int_Testing(10);
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
        FSFTBuffer<Bufferable_int_Testing> timeOutBuffer = new FSFTBuffer<>(CAPACITY, FIVE_SEC_TIME_TO_LIVE);
        Bufferable_int_Testing ten = new Bufferable_int_Testing(10);

        timeOutBuffer.put(ten);

        try {
            // Sleep two times of timeout seconds
            Thread.sleep(ONE_SEC * (2 * FIVE_SEC_TIME_TO_LIVE));
        } catch (InterruptedException e) {
            fail("[FSFT testTimeOut] : should not have Interrupted Exception");
        }

        try {
            timeOutBuffer.get(ten.id());
            fail("[FSFT testTImeOut : staled object did not removed in the buffer");
        } catch (Exception e) {
            // TEST PASSED
        }
    }

    // Test the whether the buffer preserve the object in timeout
    @Test
    public void testGetBeforeTimeout() {
        FSFTBuffer<Bufferable_text_testing> notYetTimeoutBuffer =
            new FSFTBuffer<>(CAPACITY, FIVE_SEC_TIME_TO_LIVE);
        Bufferable_text_testing testing_text =
            new Bufferable_text_testing("This is Title", "I am content of the text", 97);

        notYetTimeoutBuffer.put(testing_text);

        try {
            // Sleep for half of timeout seconds
            Thread.sleep((FIVE_SEC_TIME_TO_LIVE / 2) * ONE_SEC);
        } catch (InterruptedException e) {
            fail("[FSFT testGetBeforeTimeout] : should not have Interrupted Exception");
        }

        try {
            Assert.assertEquals(testing_text, notYetTimeoutBuffer.get(testing_text.id()));
        } catch (Exception e) {
            fail(
                "[FSFT testGetBeforeTimeout : Object in the buffer should be alive. Why you kill that object too early? Savage");
        }
    }

    // Test touch method works
    // https://www.youtube.com/watch?v=Q33SoblaZbU
    @Test
    public void SISTAR__TOUCH_MY_BODY() throws InterruptedException {
        FSFTBuffer<Bufferable_int_Testing> touch_buffer = new FSFTBuffer<>(CAPACITY, FIVE_SEC_TIME_TO_LIVE);
        Bufferable_int_Testing testing_int = new Bufferable_int_Testing(5);

        touch_buffer.put(testing_int);

        // Sleep 3 sec
        Thread.sleep(3 * ONE_SEC);

        // touch the obejct.
        touch_buffer.touch(testing_int.id());

        // Sleep 4 sec more
        Thread.sleep(4 * ONE_SEC);

        // get the touched value
        try {
            Assert.assertEquals(testing_int, touch_buffer.get(testing_int.id()));
        } catch (Exception e) {
            fail("Fail: [FSFT touch test] touch did not extend the life of the object");
        }
    }
    // Test update method works

    // Test Buffer capacity works?

    // Inserting order maintained?
    @Test
    public void testInsertingOrder() {
        FSFTBuffer<Bufferable_int_Testing> bufferUnderTest = new FSFTBuffer<>(10, 3);
        List<Bufferable_int_Testing> contents = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            Bufferable_int_Testing content = new Bufferable_int_Testing(i);
            bufferUnderTest.put(content);
            contents.add(content);
        }

        try {
            for (Bufferable_int_Testing b : contents) {
                Assert.assertEquals(b, bufferUnderTest.get(b.id()));
            }
        } catch (Exception e) {
            fail("[FSFT Buffer : testInsertOrder] TEST FAIL : No Exception Expected");
        }
    }
}


