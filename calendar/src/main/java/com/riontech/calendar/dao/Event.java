package com.riontech.calendar.dao;

import java.util.List;

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
}
