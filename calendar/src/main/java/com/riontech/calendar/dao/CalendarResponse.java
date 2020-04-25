package com.riontech.calendar.dao;

import java.util.List;

/**
 * Created by Dhaval Soneji on 13/5/16.
 */
public class CalendarResponse{

    private List<Event> monthdata;

    public List<Event> getMonthdata() {
        return monthdata;
    }

    public void setMonthdata(List<Event> monthdata) {
        this.monthdata = monthdata;
    }



}
