package com.example.hjy.myapplication;

import android.app.Activity;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by dhfl9 on 2018-06-10.
 */

public class CalendarItem {
    private String mYear, mMonth, mDay="" ,mDate;
    private boolean mIsTake;
    private int mYoil=0;

    CalendarItem(String y, String m, boolean isTake)
    {
        mYear = y;
        mMonth = m;
        mIsTake = isTake;
    }

    CalendarItem(String y, String m, String d, int yoil, boolean isTake)
    {
        mYear = y;
        mMonth = m;
        mDay = d;
        mYoil = yoil;
        mIsTake = isTake;
        mDate = "y"+"m"+"d";
    }

    public String year()
    {
        return mYear;
    }

    public String month()
    {
        return mMonth;
    }

    public String day()
    {
        return mDay;
    }
    public  String mDate(){
        return  mDate;
    }

    public Boolean isTake()
    {
        return mIsTake;
    }

    public int yoil()
    {
        return mYoil;
    }

}

