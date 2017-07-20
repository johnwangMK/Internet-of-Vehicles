package com.carmusic.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.carmusic.service.MediaPlayerManager;

/**
 * 电话状态广播接收器，来电时暂停音乐
 * */
public class PhoneStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		if(telephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE){//不是空闲状态
			context.startService(new Intent(MediaPlayerManager.SERVICE_ACTION)
			.putExtra("flag", MediaPlayerManager.SERVICE_MUSIC_PAUSE));
		}
	}

}
