package com.example.hjy.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dhfl9 on 2018-06-12.
 */

public class DB extends SQLiteOpenHelper {

    public DB(Context context, String name, SQLiteDatabase.CursorFactory  factory,int version){
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table calList(sdate char(50), list char(50), checked char(10) default 'false', per char(10) default '0', stime char(20) );";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists calList;");
        onCreate(db);
    }
}
