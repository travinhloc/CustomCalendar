package com.riontech.calendar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.riontech.calendar.R;
import com.riontech.calendar.Singleton;
import com.riontech.calendar.dao.CalendarDecoratorDao;
import com.riontech.calendar.fragment.CalendarFragment;

import java.util.List;
import java.util.GregorianCalendar;

public class CalendarGridviewAdapter extends BaseAdapter {
    private static final String TAG = CalendarGridviewAdapter.class.getSimpleName();
    private Context mContext;
    public static int firstDay;

    private List<CalendarDecoratorDao> mEventList;
    private View mPreviousView;

    public CalendarGridviewAdapter(Context c, List<CalendarDecoratorDao> items, GregorianCalendar month) {
        this.mEventList = items;
        mContext = c;

        firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
    }

    public int getCount() {
        return mEventList.size();
    }

    public CalendarDecoratorDao getItem(int position) {
        return mEventList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        CalendarGridViewHolder holder;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.calendar_item, null);
            holder = new CalendarGridViewHolder(convertView);
            int dimen = mContext.getResources().getDimensionPixelSize(R.dimen.common_40_dp);
            GridView.LayoutParams pParams = new GridView.LayoutParams(dimen, dimen);
            convertView.setLayoutParams(pParams);
            convertView.setTag(holder);
        } else {
            holder = (CalendarGridViewHolder) convertView.getTag();
        }

        CalendarDecoratorDao content = getItem(position);
        content.setPosition(position);
        holder.setDay(convertView, content);
        holder.setSelectedView(convertView, content);
        holder.bindDate(content, convertView);

        return convertView;
    }

    public View setSelected(View view, String selectedGridDate) {
        ImageView img1 = view.findViewById(R.id.date_icon);

        if (mPreviousView != null) {
            mPreviousView.findViewById(R.id.llCalendarItem);
            mPreviousView.setBackgroundResource(R.drawable.list_item_background);

            TextView txt = mPreviousView.findViewById(R.id.tv_date);
            txt.setTextColor(Color.WHITE);

        }

        mPreviousView = view;

        TextView txt = view.findViewById(R.id.tv_date);
        txt.setTextColor(Color.WHITE);

        img1.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.circle_shape_selected));
        img1.setVisibility(View.VISIBLE);

        Singleton.getInstance().setCurrentDate(selectedGridDate);
        CalendarFragment.currentDateSelected = selectedGridDate;
        return view;
    }

    class CalendarGridViewHolder {
        TextView tvDay;
        ImageView ivBackground;

        public CalendarGridViewHolder(View v) {
            setLayoutParam(v);
            tvDay = v.findViewById(R.id.tv_date);
            ivBackground = v.findViewById(R.id.date_icon);
        }

        private void setLayoutParam(View view) {
            int dimen = mContext.getResources().getDimensionPixelSize(R.dimen.common_40_dp);
            GridView.LayoutParams pParams = new GridView.LayoutParams(dimen, dimen);
            view.setLayoutParams(pParams);
        }

        private void setDay(View view, CalendarDecoratorDao content) {
            String day = content.getDay();
            view.setVisibility(View.VISIBLE);
            if ((Integer.parseInt(day) > 1) && (content.getPosition() < firstDay)) {
                view.setVisibility(View.INVISIBLE);
            } else if ((Integer.parseInt(day) < 7) && (content.getPosition() > 28)) {
                view.setVisibility(View.INVISIBLE);
            } else {
                tvDay.setTextColor(Color.BLACK);
            }
        }

        public void setSelectedView(View v, CalendarDecoratorDao content) {
            String day = content.getDay();
            if (content.getDate().equals(Singleton.getInstance().getCurrentDate())) {
                setSelected(v, Singleton.getInstance().getCurrentDate());
                mPreviousView = v;
            } else {
                v.setBackgroundResource(R.drawable.list_item_background);
                if (content.getDate().equals(Singleton.getInstance().getTodayDate())) {
                    TextView txtTodayDate = v.findViewById(R.id.tv_date);
                    txtTodayDate.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimary));
                }
            }
            tvDay.setText(day);
        }

        public void bindDate(CalendarDecoratorDao content, View view) {
            String date = content.getDate();
            if (date.length() == 1) {
                date = "0" + date;
            }
            setDecoratorVisibility(date, content, view);
        }

        private void setDecoratorVisibility(String date, CalendarDecoratorDao content, View view) {
            if (date.length() > 0 && content.getCount() > 0) {
                ivBackground.setVisibility(View.VISIBLE);
                tvDay.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                ivBackground.setVisibility(View.INVISIBLE);
                tvDay.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
        }
    }
}