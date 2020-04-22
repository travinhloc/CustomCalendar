package com.riontech.calendar.dao;

import java.util.List;

/**
 * Created by Dhaval Soneji on 13/5/16.
 */
public class EventData {
    private String section;
    private List<dataAboutDate> data;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public List<dataAboutDate> getData() {
        return data;
    }

    public void setData(List<dataAboutDate> data) {
        this.data = data;
    }
}
