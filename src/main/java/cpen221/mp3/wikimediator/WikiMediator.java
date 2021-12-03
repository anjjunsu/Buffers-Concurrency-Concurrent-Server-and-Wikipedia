package cpen221.mp3.wikimediator;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import org.fastily.jwiki.core.Wiki;

import java.util.ArrayList;
import java.util.List;

public class WikiMediator {
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
        cache = new FSFTBuffer<>(capacity, stalenessInterval);
        wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
    }

    public String getPage(String pageTitle) {
        return wiki.getPageText(pageTitle);
    }

    public List<String> search(String query, int limit) {
        return new ArrayList<>();
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
