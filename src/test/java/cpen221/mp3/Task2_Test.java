package cpen221.mp3;

import cpen221.mp3.fsftbuffer.Bufferable;
import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.fsftbuffer.ObjectDoesNotExistException;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class Task2_Test {
    public static final int FIVE_CAPACITY = 5;
    public static final int TEN_CAPACITY = 10;
    public static final int THREE_CAPACITY = 3;
    public static final int FIVE_SEC_TIME_TO_LIVE = 5;
    public static final int TEN_SEC_TIME_TO_LIVE = 10;
    public static final int ONE_SEC = 1000;

    private volatile static Throwable exc_tooEarlyToGet = null;

    // Test two individual threads put the distinct object in the same buffer
    // Tread.join may throw InterruptedException
    @Test
    public void testTwoThreadsPut() throws InterruptedException {
        FSFTBuffer<Bufferable> sharedBuffer =
            new FSFTBuffer<>(THREE_CAPACITY, TEN_SEC_TIME_TO_LIVE);
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
        FSFTBuffer<Bufferable> sharedBuffer =
            new FSFTBuffer<>(THREE_CAPACITY, TEN_SEC_TIME_TO_LIVE);
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

    // Test Get thread is trying to get an object from the buffer before the any object is inserted
    @Test
    public void testTooEarlyToGet() {
        FSFTBuffer<Bufferable> sharedBuffer =
            new FSFTBuffer<>(THREE_CAPACITY, TEN_SEC_TIME_TO_LIVE);
        Bufferable_text_testing testing =
            new Bufferable_text_testing("HELLO", "Please. Save me", 11);

        Thread getThread = new Thread(new Get_Thread(sharedBuffer, testing));
        Thread putThread = new Thread(new Put_Thread(sharedBuffer, testing));

        // expecting an exception from the getThread
        try {
            getThread.start();
            Thread.sleep(2 * ONE_SEC);
            putThread.start();

            getThread.join();
            putThread.join();

            if (exc_tooEarlyToGet == null) {
                fail(
                    "[Task2 testTooEarlyToGet] expected an exception when getThread trying to get an object form the buffer");
            }
        } catch (Exception e) {
            // Exception expected
            // Test passed
        }
    }

    // Test if time record is updated properly when many threads are trying to access the same buffer
    // Also, check update method works fine when thread uses update method
    @Test
    public void chaosFromThreads() throws InterruptedException {
        FSFTBuffer<Bufferable_text_testing> sharedBuffer =
            new FSFTBuffer<>(THREE_CAPACITY, FIVE_SEC_TIME_TO_LIVE);
        Bufferable_text_testing JunsuAn =
            new Bufferable_text_testing("Junsu An", "I love(...)", 25);
        Bufferable_text_testing updatedJunsu = new Bufferable_text_testing("Junsu An", "I win at last", 25);
        Bufferable_text_testing TaeyangBaek =
            new Bufferable_text_testing("John Baek", "I am superman", 22);
        Bufferable_text_testing updatedTaeyang =
            new Bufferable_text_testing("John Baek", "Now, I am sexy", 23);
        Bufferable_text_testing JonghaLee =
            new Bufferable_text_testing("Jake Lee", "I am smart", 20);
        Bufferable_text_testing coffee =
            new Bufferable_text_testing("Coffee", "Best coffee is Iced-Americano", 11);
        Bufferable_text_testing soju = new Bufferable_text_testing("Soju", "Jinro is best", 17);

        // First, put maximum capacity of object to the buffer
        Thread t1 = new Thread(new Put_Thread(sharedBuffer, JunsuAn));
        Thread t2 = new Thread(new Put_Thread(sharedBuffer, TaeyangBaek));
        Thread t3 = new Thread(new Put_Thread(sharedBuffer, JonghaLee));

        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();

        // Sleep for 3 sec (Buffer's timeout is 5 sec)
        Thread.sleep(3 * ONE_SEC);

        // Then, another thread is trying to update Taeyang object.
        // It will extend Taeyang object's life
        Thread updateTaeyang = new Thread(new Update_Thread(sharedBuffer, updatedTaeyang));
        updateTaeyang.start();
        updateTaeyang.join();

        // Sleep one more sec
        Thread.sleep(ONE_SEC);

        // Then, update Junsu object
        // It will update Junsu object's life
        Thread updateJunsu = new Thread(new Update_Thread(sharedBuffer, updatedJunsu));
        updateJunsu.start();
        updateJunsu.join();

        // Sleep two sec
        // Buffer will remove Jongha object due to time out
        Thread.sleep(3 * ONE_SEC);

        // Check Jongha object is removed properly
        try {
            sharedBuffer.get(JonghaLee.id());
            fail("[Task2_chaosFromThread] Jongha object supposed to be removed due to time out");
        } catch (ObjectDoesNotExistException e) {
            // Test Passed
        }

        // Check Taeyang object and Junsu object still alive
        try {
            Assert.assertEquals(updatedTaeyang, sharedBuffer.get(TaeyangBaek.id()));
            Assert.assertEquals(updatedJunsu, sharedBuffer.get(JunsuAn.id()));
        } catch (ObjectDoesNotExistException e) {
            fail("[Task2_chaosFromThread] Taeyang object supposed to be alive since it is updated");
        }

        // After that, if we put two more object in the buffer,
        // Taeyang object supposed to be removed since it's the least used.
        Thread putCoffee = new Thread(new Put_Thread(sharedBuffer, coffee));
        Thread putSoju = new Thread(new Put_Thread(sharedBuffer, coffee));
        putCoffee.start();
        putSoju.start();
        putCoffee.join();
        putSoju.join();

        try {
            sharedBuffer.get(TaeyangBaek.id());
            fail("[Task2_chaosFromThread] Taeyang object supposed to be removed");
        } catch (ObjectDoesNotExistException e) {
            // Test passed
        }

        // Check Junsu object still alive
        try {
            Assert.assertEquals(updatedJunsu, sharedBuffer.get(JunsuAn.id()));
        } catch (ObjectDoesNotExistException e) {
            fail("[Task2_chaosFromThread] Junsu object supposed to be alive");
        }
    }

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
                exc_tooEarlyToGet = e;
            }
        }
    }

    // Make thread that updates the object
    private class Update_Thread implements Runnable {
        private FSFTBuffer buffer;
        private Bufferable object;

        public Update_Thread(FSFTBuffer buffer, Bufferable object) {
            this.buffer = buffer;
            this.object = object;
        }

        @Override
        public void run() {
            buffer.update(object);
        }
    }
}
