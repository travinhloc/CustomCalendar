package com.riontech.calendar.dao;

import java.util.List;

/**
 * Created by Dhaval Soneji on 13/5/16.
 */
public class CalendarResponse{
    private String startmon;
    private String endmon;
    private List<Event> monthdata;
    private List<EventData> currentDateData;

    public List<EventData> getCurrentDateData() {
        return currentDateData;
    }

    public void setCurrentDateData(List<EventData> currentDateData) {
        this.currentDateData = currentDateData;
    }

    public List<Event> getMonthdata() {
        return monthdata;
    }

    public void setMonthdata(List<Event> monthdata) {
        this.monthdata = monthdata;
    }

    public String getStartmonth() {
        return startmon;
    }

    public void setStartmonth(String startmonth) {
        this.startmon = startmonth;
    }

    public String getEndmonth() {
        return endmon;
    }

    public void setEndmonth(String endmonth) {
        this.endmon = endmonth;
    }

}
