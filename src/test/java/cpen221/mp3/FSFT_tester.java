package cpen221.mp3;

import cpen221.mp3.fsftbuffer.Bufferable;
import cpen221.mp3.fsftbuffer.FSFTBuffer;

public class FSFT_tester implements Runnable{
    private FSFTBuffer buffer;
    private Bufferable object;

    FSFT_tester(FSFTBuffer buffer, Bufferable object) {
        this.buffer = buffer;
        this.object = object;
    }
    public void run() {
        buffer.put(object);
    }

}
