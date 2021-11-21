package cpen221.mp3;

import cpen221.mp3.fsftbuffer.Bufferable;

public class Bufferable_text_testing implements Bufferable {
    private String title;
    private String content;
    private int index;

    public Bufferable_text_testing(String title, String content, int index) {
        this.title = title;
        this.content = content;
        this.index = index;
    }

    @Override
    public String id() {
        return title;
    }
}
