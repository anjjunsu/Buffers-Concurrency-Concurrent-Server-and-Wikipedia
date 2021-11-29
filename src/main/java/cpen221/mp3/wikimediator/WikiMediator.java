package cpen221.mp3.wikimediator;

import cpen221.mp3.fsftbuffer.FSFTBuffer;

import java.util.ArrayList;
import java.util.List;

public class WikiMediator {

    /* TODO: Implement this datatype

        You must implement the methods with the exact signatures
        as provided in the statement for this mini-project.

        You must add method signatures even for the methods that you
        do not plan to implement. You should provide skeleton implementation
        for those methods, and the skeleton implementation could return
        values like null.

     */

    /**
     * Create a WikiMediator cache with capacity and stalenessInterval
     *
     * @param capacity           the number of pages to be cached
     * @param stalenessInterval  the number of seconds after which a page in the cache
     *                           will become stale
     */
    public WikiMediator(int capacity, int stalenessInterval) {
        FSFTBuffer<Page> buffer = new FSFTBuffer<>(capacity, stalenessInterval);
    }

    public List<String> search(String query, int limit) {
        return new ArrayList<>();
    }

    public String getPage(String pageTitle) {
        return "abc";
    }

    public List<String> zeitgeist(int limit) {
        return new ArrayList<>();
    }

    public List<String> trending(int timeLimitInSeconds, int maxItems) {
        return new ArrayList<>();
    }

    public int windowedPeakLoad(int timeWindowInSeconds) {
        return 0;
    }

    public int windowedPeakLoad() {
        return 0;
    }

}
