package com.riontech.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.riontech.calendar.adapter.CalendarGridViewAdapter;
import com.riontech.calendar.dao.CalendarDecoratorDao;
import com.riontech.calendar.dao.CalendarResponse;
import com.riontech.calendar.dao.Event;
import com.riontech.calendar.utils.CalendarUtils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarFragment extends Fragment {

    private static final String TAG = CalendarFragment.class.getSimpleName();

    private GridView mGridview;
    private LinearLayout mLlDayList;
    private RelativeLayout mRlHeader;
    private GregorianCalendar month;
    private CalendarGridViewAdapter adapter;
    private boolean flagMaxMin = false;
    public static String currentDateSelected;
    private Calendar mCalendar;
    private DateFormat mDateFormat;
    private GregorianCalendar mPMonth;
    private int mMonthLength;
    private GregorianCalendar mPMonthMaxSet;
    private List<CalendarDecoratorDao> days = new ArrayList<>();
    private ViewGroup rootView;
    private OnDateSelected onDateSelected;

    public static CalendarFragment newInstance(OnDateSelected onDateSelected) {
        CalendarFragment fragment = new CalendarFragment();
        fragment.setOnDateSelected(onDateSelected);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragement, container, false);

        initCurrentMonthInGridview();
        if (Singleton.getInstance().getIsSwipeViewPager() == 2) {
            refreshDays();
        }
        return rootView;
    }

    private void initCurrentMonthInGridview() {
        mLlDayList = rootView.findViewById(R.id.llDayList);
        mRlHeader = rootView.findViewById(R.id.rlMonthTitle);
        month = Singleton.getInstance().getMonth();
        adapter = new CalendarGridViewAdapter(getActivity(), days, month);

        mCalendar = month;
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
        mDateFormat = CalendarUtils.getCalendarDBFormat();
        mGridview = rootView.findViewById(R.id.gvCurrentMonthDayList);
        mGridview.setAdapter(adapter);
        TextView title = rootView.findViewById(R.id.title);
        title.setText(android.text.format.DateFormat.format(CalendarUtils.getCalendarMonthTitleFormat(), month));
        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String selectedDate = days.get(position).getDate();
                ((CalendarGridViewAdapter) parent.getAdapter()).setSelected(v, selectedDate);
                if (onDateSelected!= null) {
                    onDateSelected.onDateSelected(selectedDate);
                }
            }
        });
    }

    public void refreshCalendar() {
        if (rootView != null) {
            TextView title = rootView.findViewById(R.id.title);
            refreshDays();
            title.setText(android.text.format.DateFormat.format(CalendarUtils.getCalendarMonthTitleFormat(), month));
        }
    }

    public void refreshCalendar(List<Event> events){
        if (days != null && days.size() > 0) {
            for (CalendarDecoratorDao decoratorDao : days) {
                for (Event event : events) {
                    if (event.getDate().equals(decoratorDao.getDate())) {
                        decoratorDao.setCount(1);
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void refreshDays() {
        days.clear();
        mPMonth = (GregorianCalendar) mCalendar.clone();
        CalendarGridViewAdapter.firstDay = mCalendar.get(GregorianCalendar.DAY_OF_WEEK);

        int mMaxWeekNumber = mCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH);

        mMonthLength = mMaxWeekNumber * 7;
        int mMaxP = getmMaxP();
        int mCalMaxP = mMaxP - (CalendarGridViewAdapter.firstDay - 1);

        mPMonthMaxSet = (GregorianCalendar) mPMonth.clone();

        mPMonthMaxSet.set(GregorianCalendar.DAY_OF_MONTH, mCalMaxP + 1);

        setData(getCalendarData());
    }

    private CalendarResponse getCalendarData() {
        CalendarResponse calendarResponse = new CalendarResponse();
        calendarResponse.setMonthdata(Singleton.getInstance().getEventManager());
        return calendarResponse;
    }

    private void setData(CalendarResponse calendarResponse) {

        mLlDayList.setVisibility(View.VISIBLE);
        mRlHeader.setVisibility(View.VISIBLE);
        mGridview.setVisibility(View.VISIBLE);

        if (calendarResponse.getMonthdata() != null) {
            List<Event> monthDataList = calendarResponse.getMonthdata();
            int m = 0;
            for (int n = 0; n < mMonthLength; n++) {
                String mItemValue = mDateFormat.format(mPMonthMaxSet.getTime());
                mPMonthMaxSet.add(GregorianCalendar.DATE, 1);
                if (m < monthDataList.size()) {
                    if (mItemValue.equalsIgnoreCase(monthDataList.get(m).getDate())) {
                        CalendarDecoratorDao eventDao = new CalendarDecoratorDao(
                                monthDataList.get(m).getDate(),
                                Integer.parseInt(monthDataList.get(m).getCount()));
                        days.add(eventDao);
                        m++;
                    } else {
                        CalendarDecoratorDao eventDao = new CalendarDecoratorDao(mItemValue, 0);
                        days.add(eventDao);
                    }
                } else {
                    CalendarDecoratorDao eventDao = new CalendarDecoratorDao(mItemValue, 0);
                    days.add(eventDao);
                }
            }

            adapter.notifyDataSetChanged();

            if (!flagMaxMin) {
                flagMaxMin = true;
            }
        }
    }

    private void updateSelectMonth(List<Event> events){
    }

    public void setNextMonth() {
        if (month.get(GregorianCalendar.MONTH) == month.getActualMaximum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) + 1), month.getActualMinimum(GregorianCalendar.MONTH), 1);
            Singleton.getInstance().setMonth(month);
        } else {
            month.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) + 1);
            Singleton.getInstance().setMonth(month);
        }
    }

    public void setPreviousMonth() {
        if (month.get(GregorianCalendar.MONTH) == month.getActualMinimum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) - 1), month.getActualMaximum(GregorianCalendar.MONTH), 1);
            Singleton.getInstance().setMonth(month);
        } else {
            month.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) - 1);
            Singleton.getInstance().setMonth(month);
        }
    }

    private int getmMaxP() {
        int maxP;
        if (mCalendar.get(GregorianCalendar.MONTH) == mCalendar
                .getActualMinimum(GregorianCalendar.MONTH)) {
            mPMonth.set((mCalendar.get(GregorianCalendar.YEAR) - 1),
                    mCalendar.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            mPMonth.set(GregorianCalendar.MONTH,
                    mCalendar.get(GregorianCalendar.MONTH) - 1);
        }
        maxP = mPMonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        return maxP;
    }

    public void setOnDateSelected(OnDateSelected onDateSelected) {
        this.onDateSelected = onDateSelected;
    }

    public interface OnDateSelected{

        void onDateSelected(String date);
    }
}
