package com.riontech.calendar.dao;

import java.util.List;

/**
 * Created by Dhaval Soneji on 13/5/16.
 */
public class Event {
    private String date;
    private String count;
    private List<EventData> eventData;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<EventData> getEventData() {
        return eventData;
    }

    public void setEventData(List<EventData> eventData) {
        this.eventData = eventData;
    }
}
