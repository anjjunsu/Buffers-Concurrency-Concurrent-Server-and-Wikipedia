package cpen221.mp3;

import cpen221.mp3.fsftbuffer.Bufferable;

public class Bufferable_int_Testing implements Bufferable {
    private int content;

    public Bufferable_int_Testing(int content) {
        this.content = content;
    }

    @Override
    public String id() {
        return String.valueOf(content);
    }
}
