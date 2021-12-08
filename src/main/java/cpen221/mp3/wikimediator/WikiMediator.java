package cpen221.mp3.wikimediator;

import com.google.gson.Gson;
import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.fsftbuffer.ObjectDoesNotExistException;
import org.fastily.jwiki.core.Wiki;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import java.io.FileWriter;
import java.io.IOException;

public class WikiMediator {

    /* Representation Invariants */
    // capacity > 0
    // stalenessInterval > 0 (unit : seconds)
    // cache, pageObject, zeitgeistMap, timerMap, timeRequestMap do not contain null
    // no duplicates in cache, pageObject, zeitgeistMap, timerMap, timeRequestMap
    // currentTime >= 0

    /* Abstraction Function */
    // WikiMediator is a cache that holds information of Wikipedia page, which breaks down into
    // pageTitle and content, with its capacity being number of Pages that can be added and
    // stalenessInterval being the amount of time each Page is going to last, in seconds.
    //

    /* Thread Safety */
    // used concurrentHashMap and synchronized methods so that shared objects are not accessed at
    // the same time, which secures thread safety

    public static final int MILLIE_SEC = 1;

    private FSFTBuffer<Page> cache;
    private Wiki wiki;
    private Page pageObject;
    // key: ID, value: number of times the object has been used by search or getPage
    private ConcurrentHashMap<String, Integer> zeitgeistMap;
    private ConcurrentHashMap<String, ArrayList<Double>> timerMap;
    private ConcurrentHashMap<Integer, Integer> timeRequestMap;
    //timer for trending and windowedPeakLoad
    private Timer cacheTimer;
    private double currentTime;

    private FileWriter cacheWriter;

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
        timeRequestMap = new ConcurrentHashMap<>();
        currentTime = 0;
        cacheTimer = new Timer();
        // Start at time = 0, unit of time flow = 1 second
        cacheTimer.schedule(new TimeHelper(), 0, MILLIE_SEC);
    }

    /**
     * Given a query, return up to limit page titles that match the query string (per Wikipedia's
     * search service).
     *
     * @param query page title requested
     * @param limit number of page titles to be returned
     * @return list of page titles from query up to limit page titles.
     */

    public synchronized List<String> search(String query, int limit) {
        addElementOnTimeRequestMap();
        addElementOnZeitgeistMap(query);
        addElementOnTimerMap(query);

        saveDataInLocal();

        return new ArrayList<>(wiki.search(query, limit));
    }

    /**
     * Return the context of given pageTitle.
     *
     * @param pageTitle page title of the content
     * @return content with the given page title
     */

    public synchronized String getPage(String pageTitle) {
        addElementOnTimeRequestMap();

        try {
            pageObject = cache.get(pageTitle);
        } catch (ObjectDoesNotExistException e) {
            addElementOnZeitgeistMap(pageTitle);
            addElementOnTimerMap(pageTitle);

            pageObject = new Page(pageTitle, wiki.getPageText(pageTitle));
            cache.put(pageObject);
            return pageObject.content();
        }

        addElementOnZeitgeistMap(pageTitle);
        addElementOnTimerMap(pageTitle);

        saveDataInLocal();

        return pageObject.content();
    }

    /**
     * add element on zeitgeistMap
     *
     * @param element element to add
     */

    private void addElementOnZeitgeistMap(String element) {
        if (zeitgeistMap.containsKey(element)) {
            zeitgeistMap.put(element, zeitgeistMap.get(element) + 1);
        } else {
            zeitgeistMap.put(element, 1);
        }
    }

    /**
     * add element on a timerMap
     *
     * @param element element to add
     */

    private void addElementOnTimerMap(String element) {
        if (timerMap.containsKey(element)) {
            timerMap.get(element).add(currentTime);
        } else {
            ArrayList<Double> timeList = new ArrayList<>();
            timeList.add(currentTime);
            timerMap.put(element, timeList);
        }
    }

    /**
     * Return the most common strings used in search and getPage requests, with items being sorted
     * in non-increasing count order up to the number specified.
     *
     * @param limit maximum number of items to be returned
     * @return list of strings that are used in search and getPage requests the most.
     */

    public synchronized List<String> zeitgeist(int limit) {
        // if server was restarted, get the stored data from local
        if (timeRequestMap.isEmpty()) {
            getDataFromLocal();
        }

        // sort the map in non-increasing order
        addElementOnTimeRequestMap();

        saveDataInLocal();

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

    public synchronized List<String> trending(int timeLimitInSeconds, int maxItems) {
        // if server was restarted, get the stored data from local
        if (timeRequestMap.isEmpty()) {
            getDataFromLocal();
        }

        addElementOnTimeRequestMap();
        LinkedHashMap<String, Integer> timeFilteredMap = new LinkedHashMap<>();

        // each index + 1 shows count
        timerMap.forEach((x, y) -> {
            AtomicInteger c = new AtomicInteger();
            y.forEach(z -> {
                if (currentTime - timeLimitInSeconds <= z && z < currentTime) {
                    c.getAndIncrement();
                    timeFilteredMap.put(x, Integer.valueOf(c.toString()));
                }
            });
        });

        saveDataInLocal();

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

        return resultList;
    }

    /**
     * Return the maximum number of requests done within given time window. The requests made in
     * current time are not included.
     *
     * @param timeWindowInSeconds time window length in which maximum number of
     *                            requests can be found
     * @return maximum number of requests within given time window
     */

    public synchronized int windowedPeakLoad(int timeWindowInSeconds) {
        // if server was restarted, get the stored data from local
        if (timeRequestMap.isEmpty()) {
            getDataFromLocal();
        }

        addElementOnTimeRequestMap();

        saveDataInLocal();

        return findMaxRequests(timeWindowInSeconds);
    }

    /**
     * Return the maximum number of requests done within 30 seconds. The requests made in current
     * time are not included. Overloaded version of windowedPeakLoad(int timeWindowInSeconds).
     *
     * @return maximum number of requests within 30 seconds
     */

    public synchronized int windowedPeakLoad() {
        // if server was restarted, get the stored data from local
        if (timeRequestMap.isEmpty()) {
            getDataFromLocal();
        }

        addElementOnTimeRequestMap();

        saveDataInLocal();

        return findMaxRequests(30);
    }

    /**
     * return maximum number of request from timeRequestMap within given time
     *
     * @param timeInSeconds time window length in which maximum number of requests found.
     * @return maximum number of requests within given time
     */

    private int findMaxRequests(int timeInSeconds) {
        if (timeInSeconds > currentTime) {
            AtomicInteger c = new AtomicInteger();
            timeRequestMap.forEach((x, y) -> {
                c.addAndGet(timeRequestMap.get(x));
            });
            return c.get();
        } else {
            int max = 0;
            double time = currentTime;
            for (int i = 0; i < time - timeInSeconds; i++) {
                int c = 0;
                for (int j = i; j < timeInSeconds + i; j++) {
                    if (timeRequestMap.containsKey(j)) {
                        c += timeRequestMap.get(j);
                    }
                }
                max = Math.max(c, max);
            }

            return max;
        }
    }

    /**
     * add element on timeRequestMap
     */

    private void addElementOnTimeRequestMap() {
        int currentTimeInt = (int) Math.floor(currentTime);
        if (timeRequestMap.containsKey(currentTimeInt)) {
            timeRequestMap.put(currentTimeInt, timeRequestMap.get(currentTimeInt) + 1);
        } else {
            timeRequestMap.put(currentTimeInt, 1);
        }
    }

    /**
     * save zeitgeistMap, timerMap, timeRequestMap on local file in string form
     */

    private void saveDataInLocal() {
        Data data = new Data(zeitgeistMap, timerMap, timeRequestMap);
        String jsonData = new Gson().toJson(data);

        try {
            cacheWriter = new FileWriter("local/data.txt");
            cacheWriter.write(jsonData);
            cacheWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred on FileWriter");
        }
    }

    /**
     * get data from local data file that were saved before the server was shut down.
     */

    private void getDataFromLocal() {
        StringBuilder dataFromLocal = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("local/data.txt"));
            for (String dataLine = reader.readLine();
                 dataLine != null;
                 dataLine = reader.readLine()) {
                dataFromLocal.append(dataLine);
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Problem reading data.");
        }

        Data readFromLocalData = new Gson().fromJson(dataFromLocal.toString(), Data.class);
        this.zeitgeistMap = readFromLocalData.getZeitgeistMap();
        this.timerMap = readFromLocalData.getTimerMap();
        this.timeRequestMap = readFromLocalData.getTimeRequestMap();
    }


    /**
     * Data is a class that contains zeitgeistMap, timerMap and timeRequestMap.
     */

    static class Data {
        private ConcurrentHashMap<String, Integer> zeitgeistMap;
        private ConcurrentHashMap<String, ArrayList<Double>> timerMap;
        private ConcurrentHashMap<Integer, Integer> timeRequestMap;

        public Data(ConcurrentHashMap<String, Integer> zMap,
                    ConcurrentHashMap<String, ArrayList<Double>> tMap,
                    ConcurrentHashMap<Integer, Integer> trMap) {
            zeitgeistMap = zMap;
            timerMap = tMap;
            timeRequestMap = trMap;
        }

        ConcurrentHashMap<String, Integer> getZeitgeistMap() {
            return zeitgeistMap;
        }

        ConcurrentHashMap<String, ArrayList<Double>> getTimerMap() {
            return timerMap;
        }

        ConcurrentHashMap<Integer, Integer> getTimeRequestMap() {
            return timeRequestMap;
        }

    }

    /**
     * tracks current time in the program
     */
    class TimeHelper extends TimerTask {
        @Override
        public synchronized void run() {
            currentTime += 0.001;
        }
    }

    /**
     * returns the shortest path between two Wikipedia pages. If the path does not exist, return
     * an empty list.
     *
     * @param pageTitle1 page title in which the path starts with
     * @param pageTitle2 page title in which the path ends with
     * @param timeout    duration - in seconds - that is permitted for this operation.
     *                   If the timeout is exceeded, TimeOutException is thrown.
     * @return shortest path between pageTitle1 and pageTitle2
     * @throws TimeoutException Exception which is thrown when timeout is exceeded.
     */
    public synchronized List<String> shortestPath(String pageTitle1, String pageTitle2, int timeout)
        throws
        TimeoutException {
        return new ArrayList<String>();
    }

}
