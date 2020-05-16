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
import com.riontech.calendar.CalendarFragment;

import java.util.List;
import java.util.GregorianCalendar;

public class CalendarGridViewAdapter extends BaseAdapter {
    private Context context;
    public static int firstDay;

    private List<CalendarDecoratorDao> days;

    public CalendarGridViewAdapter(Context c, List<CalendarDecoratorDao> items, GregorianCalendar month) {
        this.days = items;
        context = c;
        firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
    }

    public int getCount() {
        return days.size();
    }

    public CalendarDecoratorDao getItem(int position) {
        return days.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        CalendarGridViewHolder holder;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.calendar_item, null);
            holder = new CalendarGridViewHolder(convertView);
            int dimen = context.getResources().getDimensionPixelSize(R.dimen.common_40_dp);
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
        holder.bindDate(content);

        return convertView;
    }

    public void setSelected(View view, String selectedGridDate) {
        ImageView img1 = view.findViewById(R.id.date_icon);
        TextView txt = view.findViewById(R.id.tv_date);
        txt.setTextColor(Color.WHITE);

        img1.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.circle_shape_selected));
        img1.setVisibility(View.VISIBLE);

        Singleton.getInstance().setCurrentDate(selectedGridDate);
        CalendarFragment.currentDateSelected = selectedGridDate;
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
            int dimen = context.getResources().getDimensionPixelSize(R.dimen.common_40_dp);
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

        public void setSelectedView(View groupView, CalendarDecoratorDao decoratorDao) {
            String day = decoratorDao.getDay();
            if (decoratorDao.getDate().equals(Singleton.getInstance().getCurrentDate())) {
                setSelected(groupView, Singleton.getInstance().getCurrentDate());
            } else {
                groupView.setBackgroundResource(R.drawable.list_item_background);
                if (decoratorDao.getDate().equals(Singleton.getInstance().getTodayDate())) {
                    TextView txtTodayDate = groupView.findViewById(R.id.tv_date);
                    txtTodayDate.setTextColor(ContextCompat.getColor(groupView.getContext(), R.color.colorPrimary));
                }
            }
            tvDay.setText(day);
        }

        public void bindDate(CalendarDecoratorDao content) {
            String date = content.getDate();
            if (date.length() == 1) {
                date = "0" + date;
            }
            setDecoratorVisibility(date, content);
        }

        private void setDecoratorVisibility(String date, CalendarDecoratorDao content) {
            if (date.length() > 0 && content.getCount() > 0) {
                ivBackground.setVisibility(View.VISIBLE);
                tvDay.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                ivBackground.setVisibility(View.INVISIBLE);
                tvDay.setTextColor(ContextCompat.getColor(context, R.color.black));
            }
        }
    }
}