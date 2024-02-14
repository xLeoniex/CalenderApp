/*
 * *************************************************
 *   Author :           Seika Leonie
 *   SubAuthor :        None
 *   Beschreibung :     Main Class for the Hour Events
 *   Letzte Änderung :  14/02/2024
 * *************************************************
 */
package com.example.calenderapp.events;

import com.example.calenderapp.events.model.EventModel;

import java.time.LocalTime;
import java.util.ArrayList;
public class HourEvent
{
    LocalTime time;
    ArrayList<EventModel> events;

    public HourEvent(LocalTime time, ArrayList<EventModel> events)
    {
        this.time = time;
        this.events = events;
    }

    public LocalTime getTime()
    {
        return time;
    }

    public void setTime(LocalTime time)
    {
        this.time = time;
    }

    public ArrayList<EventModel> getEvents()
    {
        return events;
    }

    public void setEvents(ArrayList<EventModel> events)
    {
        this.events = events;
    }
}