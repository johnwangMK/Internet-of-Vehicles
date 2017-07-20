package com.carmusic.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.carmusic.activity.BaseActivity;
import com.carmusic.data.SystemSetting;
import com.carmusic.service.DownLoadManager;
import com.carmusic.service.MediaPlayerManager;

public class AutoShutdownRecevier extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {		
		//关闭程序
		context.sendBroadcast(new Intent(BaseActivity.BROADCASTRECEVIER_ACTON));
		//停止音乐
		context.startService(new Intent(MediaPlayerManager.SERVICE_ACTION).putExtra("flag", MediaPlayerManager.SERVICE_MUSIC_STOP));
		//停止下载
		context.startService(new Intent(DownLoadManager.SERVICE_ACTION).putExtra("flag", DownLoadManager.SERVICE_DOWNLOAD_STOP));
	}

}
