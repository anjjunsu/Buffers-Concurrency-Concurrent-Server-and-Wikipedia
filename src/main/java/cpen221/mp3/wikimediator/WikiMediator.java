package cpen221.mp3.wikimediator;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.fsftbuffer.ObjectDoesNotExistException;
import org.fastily.jwiki.core.Wiki;

import java.util.ArrayList;
import java.util.List;

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
    private int ID; // ID will be deleted later.

    /**
     * Create a WikiMediator cache with capacity and stalenessInterval
     *
     * @param capacity          the number of pages to be cached
     * @param stalenessInterval the number of seconds after which a page in the cache
     *                          will become stale
     */
    public WikiMediator(int capacity, int stalenessInterval) {
        ID = 0;
        cache = new FSFTBuffer<>(capacity, stalenessInterval);
        wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
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
        try {
            pageObject = cache.get(String.valueOf(ID));
        } catch (ObjectDoesNotExistException e) {
            pageObject = new Page(query, getPage(query), ID);
            cache.put(pageObject);
            return new ArrayList<>(wiki.search(query,limit));
        }
        // if the pageObject was already existing, touch it to renew it.
        cache.touch(pageObject.id());
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
            pageObject = cache.get(String.valueOf(ID));
        } catch (ObjectDoesNotExistException e) {
            pageObject = new Page(pageTitle, getPage(pageTitle), ID);
            cache.put(pageObject);
            return pageObject.content();
        }

        // if the pageObject was already existing, touch it to renew it.
        cache.touch(pageObject.id());
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
