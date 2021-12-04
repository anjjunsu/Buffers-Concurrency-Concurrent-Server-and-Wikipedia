package cpen221.mp3.wikimediator;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.fsftbuffer.ObjectDoesNotExistException;
import org.fastily.jwiki.core.Wiki;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WikiMediator {

    /* Representation Invariants */
    // fill this out

    /* Abstract Function */
    // fill this out

    /* Thread Safety */
    // fill this out


    private FSFTBuffer<Page> cache;
    private Wiki wiki;
    private Page pageObject;
    // key: ID, value: number of times the object has been used by search or getPage
    private Map<String, Integer> zeitgeistMap;

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
        zeitgeistMap = new LinkedHashMap<>();
    }

    /**
     * Given a query, return up to limit page titles that match the query string (per Wikipedia's
     * search service).
     *
     * @param query page title requested
     * @param limit number of page titles to be returned
     * @return list of page titles from query up to limit page titles.
     */

    public List<String> search(String query, int limit) {
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
            pageObject = new Page(pageTitle, getPage(pageTitle));
            cache.put(pageObject);
            zeitgeistMap.put(pageTitle, 1);
            return pageObject.content();
        }

        // count up the number in zeigeistMap
        zeitgeistMap.put(pageTitle, zeitgeistMap.get(pageTitle) + 1);
        return pageObject.content();
    }

    /**
     * Return the most common strings used in search and getPage requests, with items being sorted
     * in non-increasing count order up to limit number of items.
     *
     * @param limit maximum number of items to be returned
     * @return list of strings that are used in search and getPage requests the most.
     */

    public List<String> zeitgeist(int limit) {
        List<String> mostCommonStringsUsed = new ArrayList<>();

        // sort the map in non-increasing order
        zeitgeistMap.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .forEachOrdered(x -> zeitgeistMap.put(x.getKey(), x.getValue()));

        zeitgeistMap.keySet().forEach(x -> {
            if (mostCommonStringsUsed.size() < limit) {
                mostCommonStringsUsed.add(x);
            }
        });

        return mostCommonStringsUsed;
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
