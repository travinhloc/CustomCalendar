package com.riontech.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.riontech.calendar.adapter.ViewPagerAdapter;
import com.riontech.calendar.dao.Event;
import com.riontech.calendar.dao.EventData;
import com.riontech.calendar.utils.CalendarUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@SuppressLint("SimpleDateFormat")
public class CustomCalendar extends LinearLayout {
    private static final String TAG = CustomCalendar.class.getSimpleName();
    private String mStartMonth;
    private String mEndMonth;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private int duplicateTotalMonthCount;
    private int currentPosition;
    private List<Event> events;
    private boolean isValidAttr = true;

    private Context context;
    private AttributeSet attributeSet = null;
    private CalendarFragment.OnDateSelected onDateSelected;
    private OnDateCallBack callBack;

    public CustomCalendar(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.layout_viewpager_recyclerview, this);
        Calendar calendar = Calendar.getInstance();
        mStartMonth = "1, " + calendar.get(Calendar.YEAR);
        mEndMonth = "12, " + calendar.get(Calendar.YEAR);
        this.context = context;
        initViews();
    }

    public CustomCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_viewpager_recyclerview, this);
        this.context = context;
        attributeSet = attrs;
        initViews();
    }

    public CustomCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_viewpager_recyclerview, this);
        this.context = context;
        attributeSet = attrs;
        initViews();
    }

    private void initViews() {
        if (attributeSet != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attributeSet,
                    R.styleable.CustomCalendar, 0, 0);
            try {
                String startMonth = a.getString(R.styleable.CustomCalendar_startMonth);
                String startYear = a.getString(R.styleable.CustomCalendar_startYear);
                String endMonth = a.getString(R.styleable.CustomCalendar_endMonth);
                String endYear = a.getString(R.styleable.CustomCalendar_endYear);

                validateAttributes(startMonth, endMonth);

                mStartMonth = startMonth + ", " + startYear;
                mEndMonth = endMonth + ", " + endYear;
            } finally {
                a.recycle();
            }
        }

        onDateSelected = new CalendarFragment.OnDateSelected() {
            @Override
            public void onDateSelected(String date) {

                if (callBack!= null) {
                    callBack.onDateSelected(date);
                }

            }
        };
        viewPager = findViewById(R.id.viewPager);
        if (!isValidAttr) {
            invalidAttributes();
            return;
        }

        Singleton.getInstance().setMonth((GregorianCalendar) GregorianCalendar.getInstance());
        Singleton.getInstance().setCurrentDate(
                CalendarUtils.getCalendarDBFormat().format(Calendar.getInstance().getTime()));
        Singleton.getInstance().setTodayDate(
                CalendarUtils.getCalendarDBFormat().format(Calendar.getInstance().getTime()));

        events = new ArrayList<>();

        Singleton.getInstance().setStartMonth(mStartMonth);
        Singleton.getInstance().setEndMonth(mEndMonth);

        setupCalendar(Singleton.getInstance().getStartMonth(), Singleton.getInstance().getEndMonth());

        Singleton.getInstance().setEventManager(events);
    }

    public void setOnDateSelected(CalendarFragment.OnDateSelected onDateSelected) {
        this.onDateSelected = onDateSelected;
    }

    private void validateAttributes(String startMonth, String endMonth) {
        if (Integer.parseInt(startMonth) < 1 || Integer.parseInt(startMonth) > 12) {
            isValidAttr = false;
        }
        if (Integer.parseInt(endMonth) < 1 || Integer.parseInt(endMonth) > 12) {
            isValidAttr = false;
        }
    }

    private void invalidAttributes() {
        viewPager.setVisibility(GONE);
    }

    public void addAnEvent(String eventDate) {
        if (!isValidAttr) return;

        Event date = new Event();
        date.setDate(eventDate);
        date.setCount(String.valueOf(1));
        events.add(date);
        Singleton.getInstance().setEventManager(events);
    }

    public void addAnEvents(List<String> strings) {
        events.clear();
        for (int i = 0; i < strings.size(); i++) {
            Event date = new Event();
            date.setDate(strings.get(i));
            date.setCount(String.valueOf(1));
            events.add(date);
        }
        ((CalendarFragment) adapter.getRegisteredFragment(viewPager.getCurrentItem())).refreshCalendar(events);
    }

    private void setupCalendar(String startMonth, String endMonth) {
        String[] temp = endMonth.split(",");
        int a = Integer.parseInt(temp[0]);
        String b = temp[1];
        a = a + 1;
        mStartMonth = startMonth;
        mEndMonth = a + ", " + b;

        SimpleDateFormat sdf = new SimpleDateFormat("MM, yyyy");
        Calendar currentCalendar = Calendar.getInstance();
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        Date startDate;
        Date endDate;

        try {
            startDate = sdf.parse(mStartMonth);
            endDate = sdf.parse(mEndMonth);
            startCalendar.setTime(startDate);
            endCalendar.setTime(endDate);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int totalMonthCount = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
        duplicateTotalMonthCount = totalMonthCount;
        int diffCurrentYear = currentCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffCurrentMonth = diffCurrentYear * 12 + currentCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
        currentPosition = diffCurrentMonth;

        FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        adapter = new ViewPagerAdapter(fm, totalMonthCount, onDateSelected);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(diffCurrentMonth);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position <= duplicateTotalMonthCount && position >= 0) {
                    if (position > currentPosition) {
                        Singleton.getInstance().setIsSwipeViewPager(1);

                        ((CalendarFragment) adapter.getRegisteredFragment(position)).setNextMonth();
                        ((CalendarFragment) adapter.getRegisteredFragment(position)).refreshCalendar();

                    } else {
                        Singleton.getInstance().setIsSwipeViewPager(0);

                        ((CalendarFragment) adapter.getRegisteredFragment(position)).setPreviousMonth();
                        ((CalendarFragment) adapter.getRegisteredFragment(position)).refreshCalendar();
                    }
                    currentPosition = position;
                }
                ((CalendarFragment) adapter.getRegisteredFragment(position)).refreshCalendar(events);
            }
        });
    }

    public void setOnDateSelected(OnDateCallBack onDateSelected) {
        this.callBack = onDateSelected;
    }

    public interface OnDateCallBack{
        void onDateSelected(String date);
    }
}
