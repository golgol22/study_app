package com.example.hjy.myapplication;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter
{
    ArrayList<CalendarItem>mItem = new ArrayList<CalendarItem>();
    LayoutInflater mInflater;
    Context mContext;


//    DB database = new DB(this,"calList.db",null,1);;
//    SQLiteDatabase mydb=database.getWritableDatabase();;


    CalendarAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public void add(CalendarItem item)
    {
        mItem.add(item);
    }

    public void clear()
    {
        mItem.clear();
    }

    @Override
    public int getCount()
    {
        return mItem.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        int ItemId = position;
        return ItemId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        DayViewHolder dayViewHolder;
        View v;
        if (convertView == null)
        {
            v = mInflater.inflate(R.layout.view_calender, null);
            if(position % 7 == 6){
                v.setLayoutParams(new GridView.LayoutParams(getCellWidthDP()+getRestCellWidthDP(),getCellHeightDP()));
            }else {
                v.setLayoutParams(new GridView.LayoutParams(getCellWidthDP(),getCellHeightDP()));
            }
        }
        else
        {
            v = convertView;
        }

        LinearLayout background = (LinearLayout)v.findViewById(R.id.background);
        TextView day = (TextView)v.findViewById(R.id.day);
        TextView take = (TextView)v.findViewById(R.id.take);
       // take.setText("ㅏㅏ");
        day.setText(mItem.get(position).day());



       //if()
            background.setBackgroundColor(Color.RED);

        int yoil = mItem.get(position).yoil();
        if (yoil == 1)
            day.setTextColor(mContext.getResources().getColor(R.color.red));
        else if (yoil == 7)
            day.setTextColor(mContext.getResources().getColor(R.color.blue));

        day.setVisibility(View.VISIBLE);

        return v;
    }



    public  class DayViewHolder{
        public LinearLayout llBackground;
        public TextView tvDay;
    }
    private  int getCellWidthDP() {
        int cellWidth = 1440/7;
        return  cellWidth;
    }
    private  int getRestCellWidthDP(){
        int cellWidth = 1440%7;
        return  cellWidth;
    }
    private  int getCellHeightDP(){
        int cellHeight = 1600/6;
        return  cellHeight;
    }


}
