package cpen221.mp3.fsftbuffer;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class FSFTBuffer<T extends Bufferable> {

    /* Representation Invariant */
    // capacity > 0
    // timeout > 0 (Unit : seconds)
    // Buffer, timeoutRecord, and lastUsedTimeRecord does not contain null
    // No duplicates in the buffer
    // No duplicates in the timeoutRecord
    // No duplicates in the lastUsedTimeRecord
    // Size of the buffer, timeoutRecord, and lastUsedTimeRecord do not exceed the capacity
    // If object is inserted in buffer,
    //      timeoutRecord and lastUsedTimeRecord must also contain
    //      that object's information and vice versa.

    /* Abstract Function */
    // FSFT buffer holds limited number of inserted object for a limited time
    // Buffer is the map that the key is the ID of bufferable object
    //      and the value is bufferable object
    // timeoutRecord is the map that the key is the ID of bufferable object in buffer
    //      and the values represents the putted, touched, or updated time.
    // lastUsedTimeRecord is the map that key is the ID of bufferable object in buffer
    //      and the values represent the most recent used time.

    /* Thread Safety */
    // Any method which access to the shared object (buffer, timeoutRecord, and lastUsedTimeRecord)
    //      are synchronized to secure thread safety.
    // Shared objects such as buffer, timeoutRecord, and lastUsedTimeRecord
    //      are ConcurrentHashMap to secure thread safety.

    public static final int ONE_SEC = 1000;
    /* the default buffer size is 32 objects */
    public static final int DSIZE = 32;

    /* the default timeout value is 3600s */
    public static final int DTIMEOUT = 3600;

    private Timer bufferTimer;

    private final int capacity;
    private final int timeout;
    private int currentTime;

    private ConcurrentHashMap<String, T> buffer;
    private ConcurrentHashMap<String, Integer> timeoutRecord;
    private ConcurrentHashMap<String, Integer> lastUsedTimeRecord;

    /**
     * Create a buffer with a fixed capacity and a timeout value.
     * Objects in the buffer that have not been refreshed within the
     * timeout period are removed from the cache.
     *
     * @param capacity the number of objects the buffer can hold
     * @param timeout  the duration, in seconds, an object should
     *                 be in the buffer before it times out
     */
    public FSFTBuffer(int capacity, int timeout) {

        this.timeout = timeout;
        this.capacity = capacity;
        currentTime = 0;
        bufferTimer = new Timer();
        buffer = new ConcurrentHashMap<>();
        timeoutRecord = new ConcurrentHashMap<>();
        lastUsedTimeRecord = new ConcurrentHashMap<>();
        // Start Timer.
        // Starting time = 0, unit of time flow = 1 second
        bufferTimer.schedule(new TimeHelper(), 0, ONE_SEC);
    }

    /**
     * Create a buffer with default capacity and timeout values.
     */
    public FSFTBuffer() {
        this(DSIZE, DTIMEOUT);
    }

    /**
     * Add a value to the buffer and that object is considered as 'used'.
     * If same object id is already exists in the buffer, it uses update method.
     *      it considered as 'used' and its timeout delays.
     * If the buffer is full then remove the least recently accessed
     *      object to make room for the new object.
     */
    public synchronized boolean put(T t) {
        if (buffer.containsKey(t.id())) {
            update(t);
            updateLastUsedTime(t.id());
        }

        if (buffer.size() >= capacity) {
            removeLeastUsed();
        }
        buffer.put(t.id(), t);
        timeoutRecord.put(t.id(), currentTime);
        lastUsedTimeRecord.put(t.id(), currentTime);

        return true;
    }

    /**
     * It removes one object in the buffer which is not most recently used.
     */
    private synchronized void removeLeastUsed() {
        String leastUsedID = Collections.min(lastUsedTimeRecord.entrySet(),
            Comparator.comparing(Map.Entry::getValue)).getKey();

        lastUsedTimeRecord.remove(leastUsedID);
        timeoutRecord.remove(leastUsedID);
        buffer.remove(leastUsedID);
    }

    /**
     * Retrieves the object in the buffer and that object is considered as 'used'.
     * @param id the identifier of the object to be retrieved
     * @return the object that matches the identifier from the buffer
     */
    public synchronized T get(String id) throws ObjectDoesNotExistException {
        if (!buffer.containsKey(id)) {
            throw new ObjectDoesNotExistException();
        }

        updateLastUsedTime(id);

        return buffer.get(id);
    }

    /**
     * Update the last refresh time for the object with the provided id.
     * This method is used to mark an object as "not stale" so that its
     * timeout is delayed.
     *
     * @param id the identifier of the object to "touch"
     * @return true if successful and false otherwise
     */
    public synchronized boolean touch(String id) {
        if (!buffer.containsKey(id)) {
            return false;
        }

        updateTimeout(id);

        return true;
    }

    /**
     * Update an object in the buffer.
     * This method updates an object and acts like a "touch" to
     * renew the object in the cache.
     *
     * @param t the object to update
     * @return true if successful and false otherwise
     */
    public synchronized boolean update(T t) {
        if (!buffer.containsKey(t.id())) {
            return false;
        }
        buffer.replace(t.id(), buffer.get(t.id()), t);
        updateTimeout(t.id());
        return true;
    }

    /**
     * When the object in the buffer is used, update the last used time to current time.
     *
     * @param id is the ID of the object we want to update the time
     */
    private synchronized void updateLastUsedTime(String id) {
        lastUsedTimeRecord.computeIfPresent(id, (object, time) -> time = currentTime);
    }

    /**
     * When the object in the buffer is touched, or updated,
     *      update time to be terminated from the buffer to currentTime + timeout.
     *
     * @param id is the ID of the object we want to extend the life to live in the buffer.
     */
    private synchronized void updateTimeout(String id) {
        timeoutRecord.computeIfPresent(id, (object, time) -> time = currentTime);
    }

    /**
     * TimerTask that increment currentTime by one for every second.
     * Check staled objects in the buffer and removes from the buffer if that is staled.
     */
    class TimeHelper extends TimerTask {

        @Override
        public synchronized void run() {
            currentTime++;

            // remove out-dated objects in every second
            for (String id : timeoutRecord.keySet()) {

                if (timeoutRecord.get(id) + timeout < currentTime) {
                    timeoutRecord.remove(id);
                    lastUsedTimeRecord.remove(id);
                    buffer.remove(id);
                }
            }
        }
    }
}
