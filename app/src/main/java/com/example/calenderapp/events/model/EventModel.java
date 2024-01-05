package com.example.calenderapp.events.model;

public class EventModel {
    private String eventName;
    private String startingTime;
    private String endingTime;
    private String eventType;
    private String recurringEventType;
    private String eventDescription;
    private String eventWeight;


    public EventModel(String eventName
            , String startingTime
            , String endingTime
            , String eventType
            , String recurringEventType
            , String eventDescription
            , String eventWeight) {
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
}
