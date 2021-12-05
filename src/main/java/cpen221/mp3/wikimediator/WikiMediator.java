package cpen221.mp3.wikimediator;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.fsftbuffer.ObjectDoesNotExistException;
import org.fastily.jwiki.core.Wiki;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class WikiMediator {

    /* Representation Invariants */
    // fill this out

    /* Abstract Function */
    // fill this out

    /* Thread Safety */
    // fill this out

    public static final int ONE_SEC = 1000; // for timer

    private FSFTBuffer<Page> cache;
    private Wiki wiki;
    private Page pageObject;
    // key: ID, value: number of times the object has been used by search or getPage
    private ConcurrentHashMap<String, Integer> zeitgeistMap;
    private ConcurrentHashMap<String, Integer> timerMap;
    //timer for trending and windowedPeakLoad
    private Timer cacheTimer;
    private int currentTime;

    /**
     * Create a WikiMediator cache with capacity and stalenessInterval
     *
     * @param capacity          the number of pages to be cached
     * @param stalenessInterval the number of seconds after which a page in the cache
     *                          will become stale
     */
    public WikiMediator(int capacity, int stalenessInterval) {
        cache = new FSFTBuffer<>(capacity, stalenessInterval);
        wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
        zeitgeistMap = new ConcurrentHashMap<>();
        timerMap = new ConcurrentHashMap<>();
        currentTime = 0;
        cacheTimer = new Timer();
        // Start at time = 0, unit of time flow = 1 second
        cacheTimer.schedule(new TimeHelper(), 0, ONE_SEC);
    }

    /**
     * Given a query, return up to limit page titles that match the query string (per Wikipedia's
     * search service).
     *
     * @param query page title requested
     * @param limit number of page titles to be returned
     * @return list of page titles from query up to limit page titles.
     */

    public  List<String> search(String query, int limit) {
        if (zeitgeistMap.containsKey(query)) {
            zeitgeistMap.put(query, zeitgeistMap.get(query) + 1);
        } else {
            zeitgeistMap.put(query, 1);
        }
        return new ArrayList<>(wiki.search(query, limit));
    }

    /**
     * Return the context of given pageTitle.
     *
     * @param pageTitle page title of the content
     * @return content with the given page title
     */

    public String getPage(String pageTitle) {
        try {
            pageObject = cache.get(pageTitle);
        } catch (ObjectDoesNotExistException e) {
            pageObject = new Page(pageTitle, wiki.getPageText(pageTitle));
            cache.put(pageObject);
            zeitgeistMap.put(pageTitle, 1);
            timerMap.put(pageTitle, currentTime);
            return pageObject.content();
        }

        // count up the number in zeigeistMap
        zeitgeistMap.put(pageTitle, zeitgeistMap.get(pageTitle) + 1);
        timerMap.put(pageTitle, currentTime);
        return pageObject.content();
    }

    /**
     * Return the most common strings used in search and getPage requests, with items being sorted
     * in non-increasing count order up to the number specified.
     *
     * @param limit maximum number of items to be returned
     * @return list of strings that are used in search and getPage requests the most.
     */

    public List<String> zeitgeist(int limit) {
        // sort the map in non-increasing order
        return getOrderedList(zeitgeistMap, limit);
    }

    /**
     * Return the most common strings used in search and getPage requests, within the time limit.
     * The items are sorted in non-increasing count order up to the number specified.
     *
     * @param timeLimitInSeconds time in which the requests are valid for this method
     * @param maxItems           max number of items to be returned
     * @return list of strings that are used in search and getPage requests
     * the most within the timeLimitInSeconds.
     */

    public List<String> trending(int timeLimitInSeconds, int maxItems) {
        LinkedHashMap<String, Integer> timeFilteredMap = new LinkedHashMap<>();

        timerMap.forEach((x, y) -> {
            if (y <= timeLimitInSeconds) {
                timeFilteredMap.put(x, zeitgeistMap.get(x));
            }
        });

        return getOrderedList(timeFilteredMap, maxItems);
    }

    /**
     * return list of strings that have been used the most in non-increasing order.
     *
     * @param mapToSort map to sort in non-increasing order regarding
     *                  the number of times the element is used
     * @param max       maximum number of items to be returned
     * @return list of strings that have been used the most, in non-increasing order.
     */

    private List<String> getOrderedList(Map<String, Integer> mapToSort, int max) {
        List<String> resultList = new ArrayList<>();
        LinkedHashMap<String, Integer> LmapToSort = new LinkedHashMap<>(mapToSort);
        LinkedHashMap<String, Integer> mapSorted = new LinkedHashMap<>();

        LmapToSort.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .forEach(x -> mapSorted.put(x.getKey(), x.getValue()));

        mapSorted.keySet().forEach(x -> {
            if (resultList.size() < max) {
                resultList.add(x);
            }
        });
        System.out.println(mapSorted);

        return resultList;
    }

    /**
     *
     * @param timeWindowInSeconds
     * @return
     */

    public int windowedPeakLoad(int timeWindowInSeconds) {

        return 0;
    }

    public int windowedPeakLoad() {
        return 0;
    }

    class TimeHelper extends TimerTask {

        @Override
        public synchronized void run() {
            currentTime++;
        }
    }

}
