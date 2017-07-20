package com.carmusic.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.carmusic.service.MediaPlayerManager;

/**
 * 耳机状态广播接收器，拔下耳机时，暂停音乐
 * */
public class EarphoneStateRecevier extends BroadcastReceiver {

	/**
	 * 提示应用程序音频信号由于音频输出的变化将变得“嘈杂”。
	 * 例如，当拔出一个有线耳机，或断开一个支持A2DP的音频接收器，
	 * 这个intent就会被发送，且音频系统将自动切换音频线路到扬声器。
	 * 收到这个intent后，控制音频流的应用程序会考虑暂停，减小音量或其他措施
	 * ，以免扬声器的声音使用户惊奇。
	 * */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
			context.startService(new Intent(MediaPlayerManager.SERVICE_ACTION)
			.putExtra("flag", MediaPlayerManager.SERVICE_MUSIC_PAUSE));
        }
	}

}
