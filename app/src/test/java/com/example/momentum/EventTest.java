package com.example.momentum;

import com.example.momentum.habitEvents.Event;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * Test for Event class
 */
public class EventTest {
    /**
     * Helper method to instantiate Event
     * @return
     * an instance of Event
     */

    private Event mockEvents(){
        return new Event("Event Title","Event comment",0,0, "image uri");
    }

    /**
     * Getter for Event
     */
    @Test
    public void testGetter(){
        Event event = mockEvents();
        assertEquals("Event Title", event.getTitle());
        assertNotEquals("Not exercise", event.getTitle());

        assertEquals("Event comment", event.getComment());
        assertNotEquals(" comment", event.getComment());

    }


}
