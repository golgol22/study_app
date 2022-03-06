package com.example.hjy.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TabActivity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends TabActivity {
    // public class Main extends TabActivity {

    LinearLayout linear1, linear2, linear3, linearLayout;
    Button time;
    Switch sw;
    DB database;
    SQLiteDatabase mydb;

    View linearDialog;
    TimePicker picker1, picker2;
    TextView settingstart, settingend, breakTime;

    TabHost tabHost;
    Button start, end;
    Integer num = 0, num1 = 0, num2 = 0, num3 = 0, num4 = 0, num5 = 0, num6 = 0, time2 = 0, time1 = 0;
    String h1 = "0", m1 = "0", h2 = "0", m2 = "0";
    String sh = "", eh = "";
    String t1, t2, t3, t4;
    CalendarAdapter adt;
    GridView grid;
    Calendar cal;
    TextView date;
    ArrayList<CalendarItem> mItem = new ArrayList<CalendarItem>();

    int sYoil;

    DevicePolicyManager mDPM;
    ComponentName mDeviceAdmin;

    SharedPreferences prof;
    SharedPreferences.Editor editor;

    ListView listViewMP3;
    ImageButton imgPre, imgPlay, imgNext;
    TextView txtSong;

    ArrayList<String> mp3List;
    String selectedMP3;

    MediaPlayer mPlayer;
    boolean PAUSED = false;
    int position;

    final Integer songs[] = {R.raw.song1, R.raw.song10, R.raw.song2, R.raw.song3, R.raw.song4, R.raw.song5, R.raw.song5, R.raw.song6, R.raw.song7, R.raw.song8,
            R.raw.song9};

    String[] sdate1;
    String[] sdate2;
    String[] sdate3;
    String[] per;
    String[] stime;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  getActionBar().setDisplayShowHomeEnabled(true);
        //  getActionBar().setIcon(R.mipmap.ic1);
        //액션바 아이콘 변경
        database = new DB(this, "calList.db", null, 1);

        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);

        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdmin = new ComponentName(MainActivity.this, Lockscreendemo.class);

        prof = getSharedPreferences("settime", 0);
        editor = prof.edit();


        linear1 = (LinearLayout) findViewById(R.id.linear1);
        linear2 = (LinearLayout) findViewById(R.id.linear2);
        linear3 = (LinearLayout) findViewById(R.id.linear3);
        sw = (Switch) findViewById(R.id.sw);

        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);

        time = (Button) findViewById(R.id.time);
        settingstart = (TextView) findViewById(R.id.settingstart);
        settingend = (TextView) findViewById(R.id.settingend);
        breakTime = (TextView) findViewById(R.id.breaktime);

/////////////////////////////////
        adt = new CalendarAdapter(this);
        grid = (GridView) findViewById(R.id.grid);
        date = (TextView) findViewById(R.id.date);

        cal = Calendar.getInstance();
        final int y = cal.get(Calendar.YEAR);
        final int m = cal.get(Calendar.MONTH) + 1;
        cal.set(y, m - 1, 1);
////////////////////////////////////

        tabHost = getTabHost();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("TAB1").setIndicator("시간");
        tab1.setContent(R.id.linear1);
        tabHost.addTab(tab1);

        TabHost.TabSpec tab2 = tabHost.newTabSpec("TAB2").setIndicator("캘린더");
        tab2.setContent(R.id.linear2);
        tabHost.addTab(tab2);

        TabHost.TabSpec tab3 = tabHost.newTabSpec("TAB3").setIndicator("음악");
        tab3.setContent(R.id.linear3);
        tabHost.addTab(tab3);

        tabHost.setCurrentTab(0);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.trim().equals("TAB2") == true) {
                    grid.setVisibility(View.VISIBLE);
                } else {
                    grid.setVisibility(View.INVISIBLE);
                }
            }
        });
        doShow();


        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (sw.isChecked()) {
                    linearLayout.setVisibility(View.VISIBLE);
                    Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
                    startActivity(i);
                } else {
                    linearLayout.setVisibility(View.INVISIBLE);
                    mDPM.removeActiveAdmin(mDeviceAdmin);
                    //stopService(new Intent(MainActivity.this, SimpleService.class));
                    //이것을 끄면 지정한 핸들러 해제
                }
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //시간 설정

                LayoutInflater li = getLayoutInflater();
                linearDialog = li.inflate(R.layout.timepicker, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);

                start = (Button) linearDialog.findViewById(R.id.start);
                end = (Button) linearDialog.findViewById(R.id.end);
                picker1 = (TimePicker) linearDialog.findViewById(R.id.picker1);
                picker2 = (TimePicker) linearDialog.findViewById(R.id.picker2);


                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        start.setBackgroundColor(Color.parseColor("#007fac"));
                        end.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        picker1.setVisibility(View.VISIBLE);
                        picker2.setVisibility(View.INVISIBLE);

                    }
                });

                end.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        start.setBackgroundColor(Color.parseColor("#ffffff"));
                        end.setBackgroundColor(Color.parseColor("#007fac"));
                        picker1.setVisibility(View.INVISIBLE);
                        picker2.setVisibility(View.VISIBLE);
                    }
                });

                picker1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {  //시작
                    @Override
                    public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                        h1 = Integer.toString(i);
                        m1 = Integer.toString(i1);
                    }
                });

                picker2.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {  //종료
                    @Override
                    public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                        h2 = Integer.toString(i);
                        m2 = Integer.toString(i1);
                    }
                });

                dlg.setView(linearDialog);
                dlg.setNegativeButton("취소", null);
                dlg.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        num1 = Integer.parseInt(h1); //시작시간
                        num2 = Integer.parseInt(h2);  //종료시간
                        num3 = Integer.parseInt(m1);  //시작분
                        num4 = Integer.parseInt(m2);   //종료분

                        if (num1 >= 12) {  //오후 (시작)
                            num5 = num1 - 12;

                            if (num5 < 10)
                                t1 = "0" + Integer.toString(num5);
                            else
                                t1 = Integer.toString(num5);
                            if (num3 < 10)
                                t3 = "0" + Integer.toString(num3);
                            else
                                t3 = m1;

                            sh = "PM " + t1 + ":" + t3;

                        } else {  //오전

                            if (num1 < 10)
                                t1 = "0" + Integer.toString(num1);
                            else
                                t1 = Integer.toString(num1);
                            if (num3 < 10)
                                t3 = "0" + Integer.toString(num3);
                            else
                                t3 = m1;

                            sh = "AM " + t1 + ":" + t3;
                        }
                        ///////////////////////////////////////

                        if (num2 >= 12) {  //오후 (종료)
                            num6 = num2 - 12;

                            if (num6 < 10)
                                t2 = "0" + Integer.toString(num6);
                            else
                                t2 = Integer.toString(num6);
                            if (num4 < 10)
                                t4 = "0" + Integer.toString(num4);
                            else
                                t4 = m2;

                            eh = "PM " + t2 + ":" + t4;

                        } else {  //오전

                            if (num2 < 10)
                                t2 = "0" + Integer.toString(num2);
                            else
                                t2 = Integer.toString(num2);
                            if (num4 < 10)
                                t4 = "0" + Integer.toString(num4);
                            else
                                t4 = m2;

                            eh = "AM " + t2 + ":" + t4;
                        }

                        //쉬는 시간 수 구하기
                        if (num4 - num3 >= 0)
                            if (num2 >= num1)
                                num = num2 - num1;
                            else
                                num = 24 - (num1 - num2);
                        else if (num2 >= num1)
                            num = num2 - num1 - 1;
                        else
                            num = 24 - (num1 - num2) - 1;

                        //시작시간-현재시간(단위: 초)
                        GregorianCalendar toDay = new GregorianCalendar();
                        final int hour = toDay.get(toDay.HOUR);
                        final int minute = toDay.get(toDay.MINUTE);
                        final int second = toDay.get(toDay.SECOND);

                        if (num1 >= hour) {
                            time2 = ((num1 * 3600 + num3 * 60) - (hour * 3600 + minute * 60 + second)) * 1000;
                        } else {
                            time2 = (24 * 3600 - (num1 * 3600 + num3 * 60) + (hour * 3600 + minute * 60 + second)) * 1000;
                        }


                        //종료시간-시작시간(단위: 초)
                        if (num2 >= num1) {
                            time1 = ((num2 * 3600 + num4 * 60) - (num1 * 3600 + num3 * 60 + second)) * 1000;
                        } else {
                            time1 = (24 * 3600 - (num2 * 3600 +
                                    num4 * 60) + (num1 * 3600 + num3 * 60 + second)) * 1000;
                        }


                        settingstart.setText("시작: " + sh);
                        settingend.setText("종료: " + eh);
                        breakTime.setText("쉬는 시간은 " + Integer.toString(num) + "번 주어집니다.");
                        breakTime.setTextColor(Color.parseColor("#f49000")); //f49000

                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                editor.putString("start", sh);
                                editor.putString("end", eh);
                                editor.putInt("breaktime", num);
                                editor.putBoolean("save", true);
                                editor.commit();


                                if (mDPM.isAdminActive(mDeviceAdmin) == true) {
                                    mDPM.lockNow();
                                }
                                startService(new Intent(MainActivity.this, SimpleService.class)); //서비스 시작
                                Intent intent = new Intent(getApplicationContext(), Lockscreendemo.class);
                                startActivity(intent);
                            }
                        }, time2);  //(시작시간-현재시간)


                        Handler h1 = new Handler();
                        h1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                                PowerManager.WakeLock mWakelock = pm.newWakeLock(
                                        PowerManager.FULL_WAKE_LOCK |
                                                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                                                PowerManager.ON_AFTER_RELEASE, "LODU");
                                mWakelock.acquire();
                                //휴대폰 깨우기 (굳이 뭐...)

                                stopService(new Intent(MainActivity.this, SimpleService.class));//서비스 종료
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                editor.putBoolean("save", false);
                                editor.commit();
                            }
                        }, time1);  //(종료시간-시작시간)

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/mm/dd", Locale.KOREA);
                        Date currentTime = new Date();
                        final String todayDate = simpleDateFormat.format(currentTime);

                        String args = sh + " - " + eh;
                        mydb = database.getWritableDatabase();
                        Cursor c;
                        String s = "select * from calList where sdate ='" + todayDate + "'";
                        c = mydb.rawQuery(s, null);
                        if (c.getCount() > 0) {
                            String sql = "update calList set stime = '" + args + "' where sdate='" + todayDate + "';";
                            mydb.execSQL(sql);
                        } else {
                            String sql = "insert into calList(sdate, stime) values ('" + todayDate + "', '" + args + "');";
                            mydb.execSQL(sql);
                        }
                        mydb.close();

                    }
                });
                dlg.show();


            }
        });


        tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#007fac"));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.WHITE);
                }  //클릭X
                tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#007fac"));  //클릭O
            }
        });


        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String d = "";

                // Toast.makeText(MainActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
                int a = position - sYoil + 2;

                if (a < 10) {
                    d = "0" + Integer.toString(a);
                } else {
                    d = Integer.toString(a);
                }
                String day = date.getText().toString() + "/" + d;

                Intent intent = new Intent(getApplicationContext(), List.class);
                intent.putExtra("day", day);
                startActivity(intent);
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
        mp3List = new ArrayList<String>();

        final Field[] fields = R.raw.class.getFields();
        for (int count = 0; count < fields.length; count++) {
            int snum = count + 1;
            mp3List.add("노래" + snum);
        }


        listViewMP3 = (ListView) findViewById(R.id.listViewMP3);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mp3List);
        listViewMP3.setAdapter(adapter);
        listViewMP3.setItemChecked(0, true);

/*
        listViewMP3.setSelector(position);
        listViewMP3.setSelector(new ColorDrawable(Color.parseColor("#FACC2E")));
*/

        listViewMP3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                selectedMP3 = mp3List.get(arg2);
                position = arg2;


                mPlayer.reset();
                try {
                    mPlayer = MediaPlayer.create(MainActivity.this, songs[position]);
                    mPlayer.start();
                    imgPlay.setImageResource(R.drawable.pause);
                    txtSong.setText(selectedMP3);
                    PAUSED = true;
                    mPlayer.setLooping(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        selectedMP3 = mp3List.get(0);

        imgPre = (ImageButton) findViewById(R.id.imgPre);
        imgPlay = (ImageButton) findViewById(R.id.imgPlay);
        imgNext = (ImageButton) findViewById(R.id.imgNext);
        txtSong = (TextView) findViewById(R.id.txtSong);

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mPlayer != null) {
                    imgPlay.setImageResource(R.drawable.play);
                    mPlayer.pause();
                }

                if (PAUSED == false) {
                    mPlayer = MediaPlayer.create(MainActivity.this, songs[position]);
                    mPlayer.start();
                    txtSong.setText(selectedMP3);
                    imgPlay.setImageResource(R.drawable.pause);
                    //listViewMP3.setSelector(new ColorDrawable(Color.parseColor("#FACC2E")));
                    PAUSED = true;
                    mPlayer.setLooping(false);
                    repeat();
                } else {
                    mPlayer.pause();
                    imgPlay.setImageResource(R.drawable.play);
                    PAUSED = false;
                }
            }
        });

        imgPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == 0) {
                    position = mp3List.size() - 1;
                    selectedMP3 = mp3List.get(position);
                } else {
                    position -= 1;
                    selectedMP3 = mp3List.get(position);
                }
                mPlayer.reset();

                try {
                    mPlayer = MediaPlayer.create(MainActivity.this, songs[position]);
                    mPlayer.start();
                    imgPlay.setImageResource(R.drawable.pause);
                    txtSong.setText(selectedMP3);
                    PAUSED = true;
                    mPlayer.setLooping(false);
                    repeat();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == mp3List.size() - 1) {
                    selectedMP3 = mp3List.get(0);
                } else {
                    position += 1;
                    selectedMP3 = mp3List.get(position);
                }
                mPlayer.reset();

                try {
                    mPlayer = MediaPlayer.create(MainActivity.this, songs[position]);
                    mPlayer.start();
                    imgPlay.setImageResource(R.drawable.pause);
                    txtSong.setText(selectedMP3);
                    PAUSED = true;
                    mPlayer.setLooping(false);
                    repeat();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void doShow() {
        String d1 = "";
        adt.clear();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;

        if (m < 10) {
            d1 = "0" + Integer.toString(m);
        } else {
            d1 = Integer.toString(m);
        }

        date.setText(y + "/" + d1);

        // 1일의 요일
        sYoil = cal.get(Calendar.DAY_OF_WEEK);

//        mydb = database.getWritableDatabase();
//        Cursor c;
//        String s = "select substr(sdate, 1, 4), substr(6,2), substr(8,2), per, stime from calList group by sdate;";
//        c = mydb.rawQuery(s, null);
//        final int count = c.getCount();
//        sdate1 = new String[count];
//        sdate2 = new String[count];
//        sdate3 = new String[count];
//        per = new String[count];
//        stime = new String[count];
//
//        for (int i = 0; i < count; i++) {
//            c.moveToNext();
//            sdate1[i] = c.getString(0);
//            sdate2[i] = c.getString(1);
//            sdate3[i] = c.getString(2);
//            per[i] = c.getString(3);
//            stime[i] = c.getString(4);
//        }
//        mydb.close();

//
//        for (int i = 1; i < sYoil; i++) {
//            cal.set(Integer.parseInt(sdate1[i]), Integer.parseInt(sdate2[i]), Integer.parseInt(sdate3[i]));
//        }
//

        // 빈 날짜 넣기
        for (int i = 1; i < sYoil; i++) {
            boolean isTake = false;
            CalendarItem item = new CalendarItem(Integer.toString(y), Integer.toString(m), isTake);
            adt.add(item);
        }



        // 이번 달 마지막 날
        int lDay = getLastDay(y, m);

        for (int i = 1; i <= lDay; i++) {
            boolean isTake = false;
            if (i % 2 == 0)
                isTake = true;

            cal.set(y, cal.get(Calendar.MONTH), i);
            int yoil = cal.get(Calendar.DAY_OF_WEEK);

            CalendarItem item = new CalendarItem(Integer.toString(y), Integer.toString(m), Integer.toString(i), yoil, isTake);
            adt.add(item);
        }

        grid.setAdapter(adt);
    }

    // 이전 달
    public void doPrev(View v) {
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) - 1;
        cal.set(y, m, 1);

        doShow();
    }

    // 다음 달
    public void doNext(View v) {
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        cal.set(y, m, 1);

        doShow();
    }

    // 특정월의 마지막 날짜
    private int getLastDay(int year, int month) {
        Date d = new Date(year, month, 1);
        d.setHours(d.getDay() - 1 * 24);
        SimpleDateFormat f = new SimpleDateFormat("dd");
        return Integer.parseInt(f.format(d));
    }


    public void repeat() {
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (position < mp3List.size()) {
                    if (position == mp3List.size() - 1) {
                        selectedMP3 = mp3List.get(0);
                    } else {
                        position += 1;
                        selectedMP3 = mp3List.get(position);
                    }
                    mPlayer.reset();
                    try {
                        mPlayer = MediaPlayer.create(MainActivity.this, songs[position]);
                        mPlayer.start();
                        txtSong.setText(selectedMP3);
                        PAUSED = true;
                        mPlayer.setLooping(false);
                        repeat();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    public class CalendarAdapter extends BaseAdapter {
        ArrayList<CalendarItem> mItem = new ArrayList<CalendarItem>();
        LayoutInflater mInflater;
        Context mContext;


        CalendarAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mContext = context;
        }

        public void add(CalendarItem item) {
            mItem.add(item);
        }

        public void clear() {
            mItem.clear();
        }

        @Override
        public int getCount() {
            return mItem.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            int ItemId = position;
            return ItemId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            com.example.hjy.myapplication.CalendarAdapter.DayViewHolder dayViewHolder;
            View v;
            if (convertView == null) {
                v = mInflater.inflate(R.layout.view_calender, null);
                if (position % 7 == 6) {
                    v.setLayoutParams(new GridView.LayoutParams(getCellWidthDP() + getRestCellWidthDP(), getCellHeightDP()));
                } else {
                    v.setLayoutParams(new GridView.LayoutParams(getCellWidthDP(), getCellHeightDP()));
                }
            } else {
                v = convertView;
            }

            LinearLayout background = (LinearLayout) v.findViewById(R.id.background);
            TextView day = (TextView) v.findViewById(R.id.day);
            TextView take = (TextView) v.findViewById(R.id.take);
            // take.setText(mItem.get(position).);
            day.setText(mItem.get(position).day());
//
//            Cursor c;
//            String s = "select * from calList;";
//            c = mydb.rawQuery(s, null);
//            if (c.getCount() > 0) {
//                background.setBackgroundColor(Color.RED);
//            }





            int yoil = mItem.get(position).yoil();
            if (yoil == 1)
                day.setTextColor(mContext.getResources().getColor(R.color.red));
            else if (yoil == 7)
                day.setTextColor(mContext.getResources().getColor(R.color.blue));

            day.setVisibility(View.VISIBLE);

            return v;
        }


        public class DayViewHolder {
            public LinearLayout llBackground;
            public TextView tvDay;
        }

        private int getCellWidthDP() {
            int cellWidth = 1440 / 7;
            return cellWidth;
        }

        private int getRestCellWidthDP() {
            int cellWidth = 1440 % 7;
            return cellWidth;
        }

        private int getCellHeightDP() {
            int cellHeight = 1600 / 6;
            return cellHeight;
        }


    }
}
