package com.example.calenderapp.events.utils;

import com.example.calenderapp.events.model.EventModel;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventSorting {

    public static void sortEventsByStartingTime(List<EventModel> eventModels)
    {
        Collections.sort(eventModels, new Comparator<EventModel>() {
            @Override
            public int compare(EventModel event1, EventModel event2) {
                String startingTime1 = event1.getStartingTime();
                String startingTime2 = event2.getStartingTime();
                String endingTime1 = event1.getEndingTime();
                String endingTime2 = event2.getEndingTime();

                if (startingTime1.equals(startingTime2)) {
                    // If starting times are equal, compare based on ending time
                    return endingTime1.compareTo(endingTime2);
                } else {
                    // Compare based on starting time
                    return startingTime1.compareTo(startingTime2);
                }
            }
        });
    }
}
