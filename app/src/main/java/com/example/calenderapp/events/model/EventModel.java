package com.example.calenderapp.events.model;

import com.example.calenderapp.events.Event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class EventModel {

    private String eventDate;
    private String eventName;
    private String startingTime;
    private String endingTime;
    private String eventType;
    private String recurringEventType;
    private String eventDescription;
    private String eventWeight;

    private LocalTime time;

    public EventModel(String eventDate,
                      String eventName,
                      String startingTime,
                      String endingTime,
                      String eventType,
                      String recurringEventType,
                      String eventDescription,
                      String eventWeight) {
        this.eventDate = eventDate;
        this.eventName = eventName;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.eventType = eventType;
        this.recurringEventType = recurringEventType;
        this.eventDescription = eventDescription;
        this.eventWeight = eventWeight;
    }

    public EventModel(String eventName) {
        this.eventName = eventName;
    }

    public EventModel() {

    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(String endingTime) {
        this.endingTime = endingTime;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getRecurringEventType() {
        return recurringEventType;
    }

    public void setRecurringEventType(String recurringEventType) {
        this.recurringEventType = recurringEventType;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventWeight() {
        return eventWeight;
    }

    public void setEventWeight(String eventWeight) {
        this.eventWeight = eventWeight;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public static ArrayList<EventModel> eventsList = new ArrayList<>();

    public static ArrayList<EventModel> eventsForDate(LocalDate date)
    {
        ArrayList<EventModel> events = new ArrayList<>();

        for(EventModel event : eventsList)
        {
            if(event.getEventDate().equals(date))
                events.add(event);
        }

        return events;
    }
    public static ArrayList<EventModel> eventsForDateAndTime(LocalDate date, LocalTime time)
    {
        ArrayList<EventModel> events = new ArrayList<>();

        for(EventModel event : eventsList)
        {
            int eventHour = event.time.getHour();
            int cellHour = time.getHour();
            if(event.getEventDate().equals(date) && eventHour == cellHour)
                events.add(event);
        }

        return events;
    }
    public LocalTime getTime()
    {
        return time;
    } //LocalTime

    public void setTime(LocalTime time)
    {
        this.time = time;
    }

    public String getName() {
        return eventName;
    }
}
