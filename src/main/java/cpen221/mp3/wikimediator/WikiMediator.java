package cpen221.mp3.wikimediator;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import org.fastily.jwiki.core.Wiki;

public class WikiMediator {
    private FSFTBuffer cache;
    private Wiki wiki;
    /* TODO: Implement this datatype

        You must implement the methods with the exact signatures
        as provided in the statement for this mini-project.

        You must add method signatures even for the methods that you
        do not plan to implement. You should provide skeleton implementation
        for those methods, and the skeleton implementation could return
        values like null.

     */
    public WikiMediator(int capacity, int stalenessInterval) {
        cache = new FSFTBuffer(capacity, stalenessInterval);
        wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();

    }

    public String getPage(String pageTitle) {
        return wiki.getPageText(pageTitle);
    }

}
