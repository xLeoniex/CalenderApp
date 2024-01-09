package com.example.calenderapp.events;

import androidx.lifecycle.MutableLiveData;

import com.example.calenderapp.events.model.EventModel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HourEvent
{
    LocalTime time;
    List<EventModel> eventList;

    public HourEvent(LocalTime time, List<EventModel> eventList)
    {
        this.time = time;
        this.eventList = eventList;
    }

    public LocalTime getTime()
    {
        return time;
    }

    public void setTime(LocalTime time)
    {
        this.time = time;
    }

    public List<EventModel> getEvents()
    {
        return eventList;
    }

    public void setEvents(List<EventModel> eventList)
    {
        this.eventList = eventList;
    }
}
