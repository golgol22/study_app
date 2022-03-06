package com.example.hjy.myapplication;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

public class SimpleService extends Service {

	private KeyguardManager km = null; 
	private KeyguardManager.KeyguardLock keylock = null;


	private BroadcastReceiver mReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
			if(action.equals("android.intent.action.SCREEN_OFF")  ){
            	Intent i = new Intent(context, Lockscreendemo.class);
            	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	context.startActivity(i);
            }
			Toast.makeText(context, "잠금화면 실행중!", Toast.LENGTH_LONG).show();
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		km=(KeyguardManager) this.getSystemService(Activity.KEYGUARD_SERVICE);
        if(km!=null){
        	keylock = km.newKeyguardLock("test");
        	keylock.disableKeyguard();
        }
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Toast.makeText(this, "잠금화면 시작되었습니다.", Toast.LENGTH_LONG).show();
		IntentFilter filter = new IntentFilter("com.example.hjy.myapplication.isAlive");
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy(){
		if(keylock!=null){
				keylock.reenableKeyguard();
		}
		 
		if(mReceiver != null)
			unregisterReceiver(mReceiver);
		Toast.makeText(this, "잠금화면 종료", Toast.LENGTH_LONG).show();
	}

}
