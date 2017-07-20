package com.carmusic.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;



public class BaseActivity extends Activity {
	public static final String BROADCASTRECEVIER_ACTON="com.carmusic.recevier.commonrecevier";
	private CommonRecevier commonRecevier;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 竖屏幕
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		
		commonRecevier=new CommonRecevier();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(commonRecevier, new IntentFilter(BROADCASTRECEVIER_ACTON));
	}



	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(commonRecevier);
	}



	public class CommonRecevier extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	}
}
