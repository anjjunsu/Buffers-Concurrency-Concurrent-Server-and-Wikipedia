package cpen221.mp3;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.fsftbuffer.ObjectDoesNotExistException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class Task1_Test {
    public static final int FIVE_CAPACITY = 5;
    public static final int TEN_CAPACITY = 10;
    public static final int FIVE_SEC_TIME_TO_LIVE = 5;
    public static final int TEN_SEC_TIME_TO_LIVE = 10;
    public static final int ONE_SEC = 1000;


    // Test buffer put and get (in very simple case)
    @Test
    public void testPut_Get() {
        FSFTBuffer<Bufferable_int_Testing> bufferPutGet =
            new FSFTBuffer<>(FIVE_CAPACITY, FIVE_SEC_TIME_TO_LIVE);
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
        FSFTBuffer<Bufferable_int_Testing> timeOutBuffer =
            new FSFTBuffer<>(FIVE_CAPACITY, FIVE_SEC_TIME_TO_LIVE);
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
            new FSFTBuffer<>(FIVE_CAPACITY, FIVE_SEC_TIME_TO_LIVE);
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
        FSFTBuffer<Bufferable_int_Testing> touch_buffer =
            new FSFTBuffer<>(FIVE_CAPACITY, FIVE_SEC_TIME_TO_LIVE);
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
    @Test
    public void testUpdate() {
        FSFTBuffer<Bufferable_text_testing> updateBuffer =
            new FSFTBuffer<>(FIVE_CAPACITY, FIVE_SEC_TIME_TO_LIVE);
        Bufferable_text_testing original =
            new Bufferable_text_testing("I am title", "I am original text", 8);
        Bufferable_text_testing updatedText =
            new Bufferable_text_testing("I am title", "I am updated text", 9);

        updateBuffer.put(original);

        updateBuffer.update(updatedText);

        try {
            Assert.assertEquals(updatedText, updateBuffer.get(original.id()));
        } catch (ObjectDoesNotExistException e) {
            System.out.println(
                "[FSFT TEST FAIL : testUpdate] || object in the buffer did not updated");
        }
    }

    // Test update method also 'touches' the object
    @Test
    public void testUpdateAlsoTouch() {
        FSFTBuffer<Bufferable_text_testing> updateBuffer2 =
            new FSFTBuffer<>(FIVE_CAPACITY, FIVE_SEC_TIME_TO_LIVE);
        Bufferable_text_testing original2 =
            new Bufferable_text_testing("I am title", "I am original text", 8);
        Bufferable_text_testing updatedText2 =
            new Bufferable_text_testing("I am title", "I am updated text", 9);

        updateBuffer2.put(original2);

        // Sleep for 3 seconds
        try {
            Thread.sleep(3 * ONE_SEC);
        } catch (InterruptedException e) {
            fail("InterruptedException while attempt to sleep");
        }

        updateBuffer2.update(updatedText2);

        // Sleep for extra 5 seconds
        try {
            Thread.sleep(5 * ONE_SEC);
        } catch (InterruptedException e) {
            fail("InterruptedException while attempt to sleep");
        }

        try {
            Assert.assertEquals(updatedText2, updateBuffer2.get(original2.id()));
        } catch (ObjectDoesNotExistException e) {
            fail("[FSFT TEST FAIL : testUpdateAlsoTouch] Update did not extend the object's life");
        }
    }

    @Test
    public void testCapacity() {
        FSFTBuffer<Bufferable_int_Testing> bufferUnderTest = new FSFTBuffer<>(TEN_CAPACITY, 3);
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

    // Test buffer properly remove the oldest object when buffer is full and insert one more
    @Test
    public void testByeByeOld() throws InterruptedException {
        FSFTBuffer<Bufferable_int_Testing> byebyeOld =
            new FSFTBuffer<>(FIVE_CAPACITY, TEN_SEC_TIME_TO_LIVE);

        Bufferable_int_Testing one = new Bufferable_int_Testing(1);
        Bufferable_int_Testing two = new Bufferable_int_Testing(2);
        Bufferable_int_Testing three = new Bufferable_int_Testing(3);
        Bufferable_int_Testing four = new Bufferable_int_Testing(4);
        Bufferable_int_Testing five = new Bufferable_int_Testing(6);
        Bufferable_int_Testing six = new Bufferable_int_Testing(7);
        Bufferable_int_Testing seven = new Bufferable_int_Testing(8);

        // Add first five objects to full the capacity of buffer
        // And sleep 1 seconds between put
        byebyeOld.put(one);
        Thread.sleep(ONE_SEC);
        byebyeOld.put(two);
        Thread.sleep(ONE_SEC);
        byebyeOld.put(three);
        Thread.sleep(ONE_SEC);
        byebyeOld.put(four);
        Thread.sleep(ONE_SEC);
        byebyeOld.put(five);
        Thread.sleep(ONE_SEC);

        // Add 6th object
        byebyeOld.put(six);
        Thread.sleep(ONE_SEC);

        // Test first one is removed
        try {
            byebyeOld.get(one.id());
            fail(
                "[FSFT TEST FAIL : ByeByeOld] The oldest one is not removed. When capacity is full");
        } catch (ObjectDoesNotExistException e) {
            // PASS. The oldest one is removed.
        }

        // six should be inserted properly
        try {
            Assert.assertEquals(six, byebyeOld.get(six.id()));
        } catch (ObjectDoesNotExistException e) {
            fail("[FSFT TEST FAIL : ByeByeOld] The sixth object should be inserted to buffer");
        }

        // And test second one is still alibe
        try {
            Assert.assertEquals(two, byebyeOld.get(two.id()));
        } catch (ObjectDoesNotExistException e) {
            fail("[FSFT TEST FAIL : ByeByeOld] The second object should still alive");
        }

        // And add one more; 7th
        byebyeOld.put(seven);
        Thread.sleep(ONE_SEC);

        // Now, second object should be removed
        try {
            byebyeOld.get(two.id());
            fail(
                "[FSFT TEST FAIL : ByeByeOld] The oldest one is not removed. When capacity is full");
        } catch (ObjectDoesNotExistException e) {
            // PASS. The oldest object is removed.
        }

        // 7th object should be inserted properly
        try {
            Assert.assertEquals(six, byebyeOld.get(six.id()));
        } catch (ObjectDoesNotExistException e) {
            fail("[FSFT TEST FAIL : ByeByeOld] The second object should still alive");
        }
    }
}


