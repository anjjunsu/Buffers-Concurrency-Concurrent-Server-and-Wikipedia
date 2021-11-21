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
    // buffer and objectTimeRecord does not contain null
    // no duplicates in the buffer
    // no duplicates in the objectTimeRecord
    // size of the buffer and objectTimeRecord do not exceed the capacity
    // If object is inserted in buffer, objectTimeRecord must also contains that object's information and vice versa

    /* Abstract Function */
    // FSFT buffer holds limited number of inserted object for a limited time
    // buffer is the map that the key is the ID of bufferable object and the value is bufferable object
    // objectTimeRecord is the map that the key is the ID of bufferable object in buffer and the values represents the inserted time

    public static final int ONE_SEC = 1000;
    /* the default buffer size is 32 objects */
    public static final int DSIZE = 32;

    /* the default timeout value is 3600s */
    public static final int DTIMEOUT = 3600;

    private Timer bufferTimer;

    private int capacity;
    private int timeout;
    private int currentTime;

    private ConcurrentHashMap<String, T> buffer;
    private ConcurrentHashMap<String, Integer> objectTimeRecord;

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
        objectTimeRecord = new ConcurrentHashMap<>();

        // start timer
        bufferTimer.schedule(new TimeHelper(), 0, ONE_SEC);
    }

    /**
     * Create a buffer with default capacity and timeout values.
     */
    public FSFTBuffer() {
        this(DSIZE, DTIMEOUT);
    }

    /**
     * Add a value to the buffer.
     * If the buffer is full then remove the least recently accessed
     * object to make room for the new object.
     */
    public synchronized boolean put(T t) {
        if (buffer.size() >= capacity) {
            removeLeastUsed();
        }
        buffer.put(t.id(), t);
        objectTimeRecord.put(t.id(), currentTime);

        return true;
    }

    /**
     *
     */
    private synchronized void removeLeastUsed() {
        String leastUsedID = Collections.min(objectTimeRecord.entrySet(), Comparator.comparing(
            Map.Entry::getValue)).getKey();

        objectTimeRecord.remove(leastUsedID);
        buffer.remove(leastUsedID);
    }

    /**
     * @param id the identifier of the object to be retrieved
     * @return the object that matches the identifier from the
     * buffer
     */
    public synchronized T get(String id) throws ObjectDoesNotExistException {
        if (!buffer.containsKey(id)) {
            throw new ObjectDoesNotExistException();
        }

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

        objectTimeRecord.computeIfPresent(id, (k, v) -> v = currentTime);

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
        objectTimeRecord.computeIfPresent(t.id(), (k, v) -> v = currentTime);

        return true;
    }

    class TimeHelper extends TimerTask {

        @Override
        public synchronized void run() {
            currentTime++;

            // remove out-dated objects in every second
            for (String id : objectTimeRecord.keySet()) {

                // remove
                int time_debugging = objectTimeRecord.get(id) + timeout;

                if (objectTimeRecord.get(id) + timeout < currentTime) {
                    objectTimeRecord.remove(id);
                    buffer.remove(id);
                }
            }
        }
    }
}
