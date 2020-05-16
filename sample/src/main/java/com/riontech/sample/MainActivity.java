package com.riontech.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import com.riontech.calendar.CalendarFragment;
import com.riontech.calendar.CustomCalendar;
import com.riontech.calendar.dao.EventData;
import com.riontech.calendar.dao.dataAboutDate;
import com.riontech.calendar.utils.CalendarUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private CustomCalendar customCalendar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customCalendar = (CustomCalendar) findViewById(R.id.customCalendar);
        customCalendar.setOnDateSelected(new CustomCalendar.OnDateCallBack() {
            @Override
            public void onDateSelected(String date) {
                Toast.makeText(MainActivity.this, date, Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.add_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] arr = {"2020-03-01","2020-04-21","2020-04-22","2020-04-23","2020-04-24", "2020-05-01"};
                List<String> strings = new ArrayList<>(Arrays.asList(arr));
                customCalendar.addAnEvents(strings);
            }
        });


    }
}
