package cpen221.mp3;

import cpen221.mp3.fsftbuffer.Bufferable;
import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.fsftbuffer.ObjectDoesNotExistException;
import org.junit.Assert;
import org.junit.Test;

import java.rmi.NoSuchObjectException;

import static org.junit.Assert.*;

public class Task2_Test {
    public static final int FIVE_CAPACITY = 5;
    public static final int TEN_CAPACITY = 10;
    public static final int THREE_CAPACITY = 3;
    public static final int FIVE_SEC_TIME_TO_LIVE = 5;
    public static final int TEN_SEC_TIME_TO_LIVE = 10;
    public static final int ONE_SEC = 1000;

    // Make the thread class implements Runnable that puts object in the buffer
    private class Put_Thread implements Runnable {
        private FSFTBuffer buffer;
        private Bufferable object;

        public Put_Thread(FSFTBuffer buffer, Bufferable object) {
            this.buffer = buffer;
            this.object = object;
        }

        @Override
        public void run() {
            buffer.put(object);
        }
    }

    // Make the thread class implements Runnable that get the object from the buffer
    private class Get_Thread implements Runnable {
        private FSFTBuffer buffer;
        private Bufferable object;

        public Get_Thread(FSFTBuffer buffer, Bufferable object) {
            this.buffer = buffer;
            this.object = object;
        }

        @Override
        public void run() {
            try {
                buffer.get(object.id());
            } catch (ObjectDoesNotExistException e) {
                System.out.println("Exception thrown when the thread trying to get an object" + object.id() + "from the buffer");
            }
        }
    }

    // Test two individual threads put the distinct object in the same buffer
    // Tread.join may throw InterruptedException
    @Test
    public void testTwoThreadsPut() throws InterruptedException {
        FSFTBuffer<Bufferable> sharedBuffer = new FSFTBuffer<>(THREE_CAPACITY, TEN_SEC_TIME_TO_LIVE);
        Bufferable_int_Testing firstObject = new Bufferable_int_Testing(1);
        Bufferable_int_Testing secondObject = new Bufferable_int_Testing(2);

        Thread t1 = new Thread(new Put_Thread(sharedBuffer, firstObject));
        Thread t2 = new Thread(new Put_Thread(sharedBuffer, secondObject));

        // Run the thread
        t1.start();
        t2.start();
        // Wait until all the threads finish their task
        t1.join();
        t2.join();

        // Check two objects were inserted in the buffer properly
        try {
            Assert.assertEquals(firstObject, sharedBuffer.get(firstObject.id()));
            Assert.assertEquals(secondObject, sharedBuffer.get(secondObject.id()));
        } catch (Exception e) {
            fail("[Task2_Test] Exception occured when trying to get an object from shared buffer");
        }
    }

    // Test Multiple threads put the object over the buffer's capacity and oldest one is removed properly
    @Test
    public void ThreadOverwelming() throws InterruptedException {
        FSFTBuffer<Bufferable> sharedBuffer = new FSFTBuffer<>(THREE_CAPACITY, TEN_SEC_TIME_TO_LIVE);
        Bufferable_int_Testing first = new Bufferable_int_Testing(1);
        Bufferable_int_Testing second = new Bufferable_int_Testing(2);
        Bufferable_int_Testing third = new Bufferable_int_Testing(3);
        Bufferable_int_Testing fourth = new Bufferable_int_Testing(4);
        Bufferable_int_Testing fifth = new Bufferable_int_Testing(5);

        Thread t1 = new Thread(new Put_Thread(sharedBuffer, first));
        Thread t2 = new Thread(new Put_Thread(sharedBuffer, second));
        Thread t3 = new Thread(new Put_Thread(sharedBuffer, third));
        Thread t4 = new Thread(new Put_Thread(sharedBuffer, fourth));
        Thread t5 = new Thread(new Put_Thread(sharedBuffer, fifth));

        t1.start();
        // Sleep for 2 sec after put first object in the buffer
        try {
            Thread.sleep(ONE_SEC);
        } catch (InterruptedException e) {
            fail("[Task2_Test] No exception expected when Thread.sleep()");
        }
        // Put the rest objects in the buffer
        t2.start();
        Thread.sleep(ONE_SEC);
        t3.start();
        t4.start();

        // Wait all the threads to finish their task
        t1.join();
        t2.join();
        t3.join();
        t4.join();

        // First object should have been removed
        try {
            sharedBuffer.get(first.id());
            fail("[Task2_Test] first inserted object should have removed");

        } catch (ObjectDoesNotExistException e) {
            // Test Passed
        }

        // Put one more extra object to the buffer
        t5.start();
        t5.join();

        // Second object should have been removed
        try {
            sharedBuffer.get(second.id());
            fail("[Task2_Test] second inserted object should have removed");
        } catch (ObjectDoesNotExistException e) {
            // Test Passed
        }
    }

//    // Test Get thread is trying to get an object from the buffer before the any object is inserted
//    @Test
//    public void testTooEarlyToGet() {
//        FSFTBuffer<Bufferable> sharedBuffer = new FSFTBuffer<>(THREE_CAPACITY, TEN_SEC_TIME_TO_LIVE);
//        Bufferable_text_testing testing = new Bufferable_text_testing("HELLO", "Please. Save me", 11);
//
//        Thread getThread = new Thread(new Get_Thread(sharedBuffer, testing));
//        Thread putThread = new Thread(new Put_Thread(sharedBuffer, testing));
//
//        Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread t, Throwable e) {
//                System.out.println("Thread throws an exception when trying to get an object from the buffer" + e);
//                try {
//                    throw new Exception();
//                } catch (Exception ex) {
//
//                }
//            }
//        };
//
//        getThread.setUncaughtExceptionHandler(handler);
//
//        try {
//            getThread.start();
//            Thread.sleep(2*ONE_SEC);
//            putThread.start();
//
//            getThread.join();
//            putThread.join();
//            fail("[Task2 testTooEarlyToGet] There should be an exception");
//        } catch (Exception e) {
//            // Exception expected
//            // Test passed
//        }
//
//    }
}
