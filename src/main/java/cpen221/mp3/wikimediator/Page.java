package cpen221.mp3.wikimediator;

import cpen221.mp3.fsftbuffer.Bufferable;

/**
 * Page is a datatype to be stored in a cache (WikiMediator)
 */
public class Page implements Bufferable {
    private  String title;
    private  String content;
    private  String  id;

    public Page(String title, String content) {
        this.title = title;
        this.content = content;
        this.id = title;
    }

    public String title() {
        return title;
    }

    public String content() {
        return content;
    }

    @Override
    public String id() {
        return id;
    }
}
