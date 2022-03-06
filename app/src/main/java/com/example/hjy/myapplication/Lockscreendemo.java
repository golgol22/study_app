package com.example.hjy.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class Lockscreendemo extends Activity {

	TextView date, settingTime,song;
	Button breaktimebtn;
	String start="", end="";
	Integer shour=0, sminute=0, ehour=0, eminute=0 ;
	Integer breaktime = 0;
	ImageButton iv1,iv2,iv3;

	ArrayList<String> mp3List;
	String selectedMP3;
	MediaPlayer mPlayer;
	boolean PAUSED = false;
	int position;
	final Integer songs[] = {R.raw.song1,R.raw.song10,R.raw.song2,R.raw.song3,R.raw.song4,R.raw.song5,R.raw.song5,R.raw.song6,R.raw.song7,R.raw.song8,
			R.raw.song9};

	SharedPreferences prof;
	SharedPreferences.Editor editor;

	protected void onDestroy() {
		super.onDestroy();

		mPlayer.pause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screendemo);

		prof=getSharedPreferences("settime", 0);
		editor= prof.edit();

		int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
		int newUiOptions = uiOptions;
		boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
		if (isImmersiveModeEnabled) {
			Log.i(TAG, "Turning immersive mode mode off.");
		} else {
			Log.i(TAG, "Turning immersive mode mode on.");
		}
		newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
		newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		getWindow().getDecorView().setSystemUiVisibility(newUiOptions);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |  WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		date=(TextView)findViewById(R.id.date);
		settingTime=(TextView)findViewById(R.id.settingtime);
		breaktimebtn =(Button)findViewById(R.id.breaktimebtn);
		iv1=(ImageButton) findViewById(R.id.iv1);
		iv2=(ImageButton)findViewById(R.id.iv2);
		iv3=(ImageButton)findViewById(R.id.iv3);
		song =(TextView)findViewById(R.id.song);

		if(prof.getBoolean("save", false)) {  //불러올때
			start=prof.getString("start", "");
			end=prof.getString("end", "");
//            shour = prof.getInt("shour", 0);
//            sminute=prof.getInt("sminute", 0);
//            ehour=prof.getInt("ehour", 0);
//            eminute=prof.getInt("eminute", 0);
			breaktime=prof.getInt("breaktime", 0);
		}

		GregorianCalendar toDay = new GregorianCalendar();
		final Integer hour = toDay.get(toDay.HOUR);
		final Integer minute = toDay.get(toDay.MINUTE);

		if((shour.equals(hour) && minute.compareTo(sminute)>=0)|| (shour<hour && hour<ehour) || (shour.equals(hour)&&minute.compareTo(eminute)<0))
			editor.putBoolean("SetupTime", true);
		else
			editor.putBoolean("SetupTime", false);
		editor.commit();



		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
		Date currentTime = new Date();
		final String todayDate = simpleDateFormat.format(currentTime);

		int dayofweek1= toDay.get(toDay.DAY_OF_WEEK);
		String dayofweek2="";
		switch (dayofweek1){

			case 1: dayofweek2="일";
				break;
			case 2: dayofweek2="월";
				break;
			case 3: dayofweek2="화";
				break;
			case 4: dayofweek2="수";
				break;
			case 5: dayofweek2="목";
				break;
			case 6: dayofweek2="금";
				break;
			case 7: dayofweek2="토";
				break;
		}
		date.setText(todayDate+" "+dayofweek2+"요일");
		settingTime.setText(start+" - "+end);
		breaktimebtn.setText("쉬는 시간 사용하기" + "(" + Integer.toString(breaktime) + "번 남음)");

		breaktimebtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {  //쉬는 시간 버튼 클릭
				mPlayer.pause();

				if (breaktime <= 0) {
					Toast.makeText(Lockscreendemo.this, "쉬는시간이 없어요ㅠㅠ", Toast.LENGTH_SHORT).show();
					//breaktimebtn.setEnabled(false);
				}else{
					stopService(new Intent(Lockscreendemo.this, SimpleService.class));
					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(intent);

					Handler h2 = new Handler();
					h2.postDelayed(new Runnable() {
						@Override
						public void run() {
							startService(new Intent(Lockscreendemo.this, SimpleService.class));
							Intent intent = new Intent(getApplicationContext(), Lockscreendemo.class);
							startActivity(intent);
							breaktime = breaktime-1;
							breaktimebtn.setText("쉬는 시간 사용하기" + "(" + Integer.toString(breaktime) + "번 남음)");
							editor.putInt("breaktime", breaktime);
							editor.putBoolean("save", true);
							editor.commit();
						}
					}, 10000);  //10분(10초)

				}
			}
		});



		ActivityCompat.requestPermissions(this, new String[] {
				Manifest.permission.WRITE_EXTERNAL_STORAGE},MODE_PRIVATE);
		position = songs[position];
		position = 0;

		iv2.setOnClickListener(new  View.OnClickListener()  {
			@Override
			public  void  onClick(View  arg0)  {
				if( mPlayer!=null ) {
					iv2.setImageResource(R.drawable.stop);
					mPlayer.pause();
				}

				if(PAUSED==false) {
					mPlayer = MediaPlayer.create(Lockscreendemo.this, songs[position]);
					mPlayer.start();
					selectedMP3 = "노래"+(position+1);
					song.setText(selectedMP3);
					iv2.setImageResource(R.drawable.start);
					PAUSED = true;
					mPlayer.setLooping(false);
					repeat();
				}
				else{
					mPlayer.pause();
					iv2.setImageResource(R.drawable.stop);
					PAUSED = false;
				}
			}
		});

		iv1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(position == 0) {
					position= songs.length-1;
					selectedMP3 = "노래10";
				}else {
					position -= 1;
					selectedMP3 = "노래"+(position+1);
				}
				mPlayer.reset();

				try  {
					mPlayer = MediaPlayer.create(Lockscreendemo.this, songs[ position ]);
					mPlayer.start();
					iv2.setImageResource(R.drawable.start);
					song.setText(selectedMP3);
					PAUSED = true;
					mPlayer.setLooping(false);
					repeat();
				}
				catch  (Exception  e)  {
					e.printStackTrace();
				}
			}
		});

		iv3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(position == song.length()-1) {
					selectedMP3 = "노래1";
				}else {
					position += 1;
					selectedMP3 = "노래"+(position+1);
				}
				mPlayer.reset();

				try  {
					mPlayer = MediaPlayer.create(Lockscreendemo.this, songs[ position ]);
					mPlayer.start();
					iv2.setImageResource(R.drawable.start);
					song.setText(selectedMP3);
					PAUSED = true;
					mPlayer.setLooping(false);
					repeat();
				}
				catch  (Exception  e)  {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onBackPressed() { }  //뒤로가기 키 제어


//	@Override
//	public void onAttachedToWindow() {
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
//		super.onAttachedToWindow();
//	}
//
//	public void onUserLeaveHint() {
////
////		if(a!=1)
////		{
////			m_vibrator.vibrate(5000);
////			mSound.play();
//			Context context = getApplicationContext(); // 자동으로 화면이동
//			AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//			//Intent Intent = new Intent("a.a");
//			Intent Intent = new Intent(Lockscreendemo.this, Lockscreendemo.class );
//			Intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
//			PendingIntent pIntent = PendingIntent.getActivity(context, 0, Intent, 0);
//			//mPool.play(mDdok, 1, 1, 0, 0, 1);
//			alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 0, pIntent);
////		}
//	}


	public void repeat() {
		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
				if (position < songs.length) {
					if (position == songs.length - 1) {
						selectedMP3 = "노래1";
					} else {
						position += 1;
						selectedMP3 = "노래"+(position+1);
					}
					mPlayer.reset();
					try {
						mPlayer = MediaPlayer.create(Lockscreendemo.this, songs[position]);
						mPlayer.start();
						song.setText(selectedMP3);
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


}
