package cpen221.mp3.wikimediator;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
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


    private FSFTBuffer cache;
    private Wiki wiki;

    /**
     * Create a WikiMediator cache with capacity and stalenessInterval
     *
     * @param capacity           the number of pages to be cached
     * @param stalenessInterval  the number of seconds after which a page in the cache
     *                           will become stale
     */
    public WikiMediator(int capacity, int stalenessInterval) {
        cache = new FSFTBuffer<Page>(capacity, stalenessInterval);
        wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
    }

    /**
     * Given a query, return up to limit page titles that match the query string (per Wikipedia's
     * search service).
     *
     * @param query     page title requested
     * @param limit     number of page titles to be returned
     * @return          list of page titles from query up to limit page titles.
     */

    public List<String> search(String query, int limit) {
        return new ArrayList<>(wiki.search(query, limit));
    }

    public String getPage(String pageTitle) {
        return wiki.getPageText(pageTitle);
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
