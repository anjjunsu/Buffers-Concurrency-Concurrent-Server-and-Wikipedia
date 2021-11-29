package cpen221.mp3.wikimediator;

import cpen221.mp3.fsftbuffer.Bufferable;

/**
 * Page is a datatype to be stored in a cache (WikiMediator)
 */
public class Page implements Bufferable {
    private  String title;
    private  String content;
    private final int id;

    public Page(String title, String content, int id) {
        this.title = title;
        this.content = content;
        this.id = id;
    }

    @Override
    public String id() {
        return String.valueOf(id);
    }
}
