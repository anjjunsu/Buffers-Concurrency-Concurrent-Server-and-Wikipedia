package cpen221.mp3;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class Tests {
    // Test buffer put

    // Test buffer get

    // Test buffer remove element when time-out

    // Test touch method works

    // Test update method works

    // Test Buffer capacity works?

    // Inserting order maintained?
    @Test
    public void testInsertingOrder() {
        FSFTBuffer<Bufferable_Testing> bufferUnderTest = new FSFTBuffer<>(10, 3);
        List<Bufferable_Testing> contents = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            Bufferable_Testing content = new Bufferable_Testing(i);
            bufferUnderTest.put(content);
            contents.add(content);
        }

        for (Bufferable_Testing b : contents) {
            Assert.assertEquals(b, bufferUnderTest.get(b.id()));
        }
    }

}
