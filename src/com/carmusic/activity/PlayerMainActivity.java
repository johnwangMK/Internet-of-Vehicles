package com.carmusic.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.guet.haojiayou.R;

import com.carmusic.custom.CarMDialog;
import com.carmusic.custom.FlingGalleryView;
import com.carmusic.custom.FlingGalleryView.OnCustomTouchListener;
import com.carmusic.service.MediaPlayerManager;
import com.carmusic.service.MediaPlayerManager.ServiceConnectionListener;
import com.carmusic.util.Common;
import com.carmusic.util.ImageUtil;

public class PlayerMainActivity extends BaseActivity {
	private RelativeLayout ll_player_voice;

	//控制播放
	private ImageButton ibtn_player_voice;
	private ImageButton ibtn_player_list;
	private ImageButton ibtn_player_control_mode;
	private ImageButton ibtn_player_control_pre;
	private ImageButton ibtn_player_control_play;
	private ImageButton ibtn_player_control_next;
	
	//播放信息
	private TextView tv_player_playing_time;
	private TextView tv_player_playering_duration;
	private TextView tv_player_song_info;

	//调节音量
	private SeekBar sb_player_voice;
	//调节播放进度
	private SeekBar sb_player_playprogress;

	//专辑
	private ViewGroup player_main_album;
	private ImageView iv_player_ablum;
	private ImageView iv_player_ablum_reflection;

	private FlingGalleryView fgv_player_main;

	// 音量面板显示和隐藏动画
	private Animation showVoicePanelAnimation;
	private Animation hiddenVoicePanelAnimation;

	// 播放模式
	private String[] player_modeStr;

	//播放模式的Drawable Id
	private static final int[] MODE_DRAWABLE_ID=new int[]{
		R.drawable.player_btn_player_mode_circlelist,
		R.drawable.player_btn_player_mode_random,
		R.drawable.player_btn_player_mode_circleone,
		R.drawable.player_btn_player_mode_sequence
	};
	
	private Toast toastMsg;
	private MediaPlayerManager mediaPlayerManager;
	private MediaPlayerBroadcastReceiver mediaPlayerBroadcastReceiver;
	
	private AudioManager audioManager;// 获取系统音频对象

	private boolean isSeekDrag=false;//进度是否在拖动
	
	//歌词显示部分
	private ViewGroup player_main_lyric;
	private TextView tv_player_lyric_info;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_player_main);
		
		ll_player_voice = (RelativeLayout) this
				.findViewById(R.id.ll_player_voice);

		ibtn_player_voice = (ImageButton) this
				.findViewById(R.id.ibtn_player_voice);
		ibtn_player_list = (ImageButton) this
				.findViewById(R.id.ibtn_player_list);
		ibtn_player_control_mode = (ImageButton) this
				.findViewById(R.id.ibtn_player_control_mode);
		ibtn_player_control_pre = (ImageButton) this
				.findViewById(R.id.ibtn_player_control_pre);
		ibtn_player_control_play = (ImageButton) this
				.findViewById(R.id.ibtn_player_control_play);
		ibtn_player_control_next = (ImageButton) this
				.findViewById(R.id.ibtn_player_control_next);

		ibtn_player_voice.setOnClickListener(listener);
		ibtn_player_list.setOnClickListener(listener);
		ibtn_player_control_mode.setOnClickListener(listener);
		ibtn_player_control_pre.setOnClickListener(listener);
		ibtn_player_control_play.setOnClickListener(listener);
		ibtn_player_control_next.setOnClickListener(listener);

		tv_player_playing_time = (TextView) this
				.findViewById(R.id.tv_player_playing_time);
		tv_player_playering_duration = (TextView) this
				.findViewById(R.id.tv_player_playering_duration);
		tv_player_song_info = (TextView) this
				.findViewById(R.id.tv_player_song_info);

		sb_player_voice = (SeekBar) this.findViewById(R.id.sb_player_voice);
		sb_player_playprogress = (SeekBar) this
				.findViewById(R.id.sb_player_playprogress);

		sb_player_voice.setOnSeekBarChangeListener(seekBarChangeListener);
		sb_player_playprogress
				.setOnSeekBarChangeListener(seekBarChangeListener);
		sb_player_playprogress.setMax(100);

		player_main_album = (ViewGroup) this
				.findViewById(R.id.player_main_album);
		iv_player_ablum = (ImageView) player_main_album
				.findViewById(R.id.iv_player_ablum);
		iv_player_ablum_reflection = (ImageView) player_main_album
				.findViewById(R.id.iv_player_ablum_reflection);
		setAlbum(R.drawable.music_default_album);
		
		fgv_player_main = (FlingGalleryView) this
				.findViewById(R.id.fgv_player_main);
		fgv_player_main.setOnCustomTouchListener(customTouchListener);
		
		showVoicePanelAnimation = AnimationUtils.loadAnimation(
				PlayerMainActivity.this, R.anim.music_push_up_in);
		hiddenVoicePanelAnimation = AnimationUtils.loadAnimation(
				PlayerMainActivity.this, R.anim.music_push_up_out);

		player_modeStr = getResources().getStringArray(R.array.player_mode);

		player_main_lyric=(ViewGroup)this.findViewById(R.id.player_main_lyric);
		tv_player_lyric_info=(TextView)player_main_lyric.findViewById(R.id.tv_player_lyric_info);
		tv_player_lyric_info.setText("暂时无歌词");
		
		//注册播放器-广播接收器
		mediaPlayerBroadcastReceiver=new MediaPlayerBroadcastReceiver();
		registerReceiver(mediaPlayerBroadcastReceiver, new IntentFilter(MediaPlayerManager.BROADCASTRECEVIER_ACTON));
		//播放器管理
		mediaPlayerManager=new MediaPlayerManager(this);
		mediaPlayerManager.setConnectionListener(mConnectionListener);
		mediaPlayerManager.startAndBindService();
		
		// 获取系统音乐音量
		audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		// 获取系统音乐当前音量
		int currentVolume = audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		sb_player_voice.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		sb_player_voice.setProgress(currentVolume);
	}
	
	private ServiceConnectionListener mConnectionListener=new ServiceConnectionListener() {
		@Override
		public void onServiceDisconnected() {
		}
		@Override
		public void onServiceConnected() {			
			mediaPlayerManager.initPlayerMain_SongInfo();
		}
	};
	
	/**
	 * 播放器-广播接收器
	 * */
	private class MediaPlayerBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			int flag=intent.getIntExtra("flag", -1);
			if(flag==MediaPlayerManager.FLAG_CHANGED){
				if(!isSeekDrag){
					int currentPosition=intent.getIntExtra("currentPosition", 0);
					int duration=intent.getIntExtra("duration", 0);
					tv_player_playing_time.setText(Common.formatSecondTime(currentPosition));
					tv_player_playering_duration.setText(Common.formatSecondTime(duration));
					sb_player_playprogress.setProgress(currentPosition);
					sb_player_playprogress.setMax(duration);
				}
			}else if(flag==MediaPlayerManager.FLAG_PREPARE){
				String albumPic=intent.getStringExtra("albumPic");
				tv_player_song_info.setText(intent.getStringExtra("title"));
				if(TextUtils.isEmpty(albumPic)){
					setAlbum(R.drawable.music_default_album);
				}else{
					Bitmap bitmap=BitmapFactory.decodeFile(albumPic);
					//判断SD图片是否存在
					if(bitmap!=null){
						setAlbum(bitmap);
					}else{
						setAlbum(R.drawable.music_default_album);
					}
				}
				int duration=intent.getIntExtra("duration", 0);
				int currentPosition=intent.getIntExtra("currentPosition", 0);
				tv_player_playing_time.setText(Common.formatSecondTime(currentPosition));
				tv_player_playering_duration.setText(Common.formatSecondTime(duration));
				sb_player_playprogress.setMax(duration);
				sb_player_playprogress.setProgress(currentPosition);
				sb_player_playprogress.setSecondaryProgress(0);
			}else if(flag==MediaPlayerManager.FLAG_INIT){//初始化播放信息
				int currentPosition=intent.getIntExtra("currentPosition", 0);
				int duration=intent.getIntExtra("duration", 0);
				int playerMode=intent.getIntExtra("playerMode", 0);
				int playerState=intent.getIntExtra("playerState", 0);
				
				if(playerState==MediaPlayerManager.STATE_PLAYER||playerState==MediaPlayerManager.STATE_PREPARE){//播放
					ibtn_player_control_play.setBackgroundResource(R.drawable.player_btn_player_pause);
				}else{
					ibtn_player_control_play.setBackgroundResource(R.drawable.player_btn_player_play);
				}
				
				ibtn_player_control_mode
				.setBackgroundResource(MODE_DRAWABLE_ID[playerMode]);
				
				sb_player_playprogress.setMax(duration);
				sb_player_playprogress.setProgress(currentPosition);
				tv_player_playing_time.setText(Common.formatSecondTime(currentPosition));
				tv_player_playering_duration.setText(Common.formatSecondTime(duration));
				tv_player_song_info.setText(intent.getStringExtra("title"));
				String albumPic=intent.getStringExtra("albumPic");
				if(TextUtils.isEmpty(albumPic)){
					setAlbum(R.drawable.music_default_album);
				}else{
					Bitmap bitmap=BitmapFactory.decodeFile(albumPic);
					//判断SD图片是否存在
					if(bitmap!=null){
						setAlbum(bitmap);
					}else{
						setAlbum(R.drawable.music_default_album);
					}
				}
			}else if(flag==MediaPlayerManager.FLAG_BUFFERING){
				int percent=intent.getIntExtra("percent", 0);
				percent=(int)(sb_player_playprogress.getMax()/100f)*percent;
				sb_player_playprogress.setSecondaryProgress(percent);
			}else if(flag==MediaPlayerManager.FLAG_LIST){
				int state=mediaPlayerManager.getPlayerState();
				if(state==MediaPlayerManager.STATE_PLAYER||state==MediaPlayerManager.STATE_PREPARE){//播放
					ibtn_player_control_play.setBackgroundResource(R.drawable.player_btn_player_pause);
				}else{
					ibtn_player_control_play.setBackgroundResource(R.drawable.player_btn_player_play);
				}
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		mediaPlayerManager.unbindService();
		unregisterReceiver(mediaPlayerBroadcastReceiver);
		super.onDestroy();
	}
	
	// 记录FlingGalleryView 上次Touch事件的Position
	private float lastX = 0;
	private float lastY = 0;
	private OnCustomTouchListener customTouchListener = new OnCustomTouchListener() {

		public void operation(MotionEvent event) {
			final int action = event.getAction();
			if (action == MotionEvent.ACTION_DOWN) {
				lastX = event.getX();
				lastY = event.getY();
			} else if (action == MotionEvent.ACTION_UP) {
				if (lastX == event.getX() && lastY == event.getY()) {
					voicePanelAnimation();
				}
			}
		}

	};

	// 设置专辑封面-默认图片
	private void setAlbum(int rid) {
		iv_player_ablum.setImageResource(rid);
		iv_player_ablum_reflection.setImageBitmap(ImageUtil
				.createReflectionBitmapForSingle(BitmapFactory.decodeResource(
						getResources(), rid)));
	}
	
	// 设置专辑封面-SD路径
	private void setAlbum(Bitmap bitmap){
		iv_player_ablum.setImageBitmap(bitmap);
		iv_player_ablum_reflection.setImageBitmap(ImageUtil
				.createReflectionBitmapForSingle(bitmap));
	}
	
	// 音量面板显示和隐藏
	private void voicePanelAnimation() {
		if (ll_player_voice.getVisibility() == View.GONE) {
			ll_player_voice.startAnimation(showVoicePanelAnimation);
			ll_player_voice.setVisibility(View.VISIBLE);
		} else {
			ll_player_voice.startAnimation(hiddenVoicePanelAnimation);
			ll_player_voice.setVisibility(View.GONE);
		}
	}

	private OnClickListener listener = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ibtn_player_voice:
				voicePanelAnimation();
				break;
			case R.id.ibtn_player_list:
				finish();
				break;
			case R.id.ibtn_player_control_mode:
				int player_mode=mediaPlayerManager.getPlayerMode();
				if (player_mode ==MediaPlayerManager.MODE_SEQUENCE) {
					player_mode = MediaPlayerManager.MODE_CIRCLELIST;
				} else {
					player_mode++;
				}
				mediaPlayerManager.setPlayerMode(player_mode);
				ibtn_player_control_mode
						.setBackgroundResource(MODE_DRAWABLE_ID[player_mode]);
				toastMsg = Common.showMessage(toastMsg,
						PlayerMainActivity.this, player_modeStr[player_mode]);
				break;
			case R.id.ibtn_player_control_pre:
				ibtn_player_control_play.setBackgroundResource(R.drawable.player_btn_player_pause);
				mediaPlayerManager.previousPlayer();
				break;
			case R.id.ibtn_player_control_play:
				if(mediaPlayerManager.getPlayerState()==MediaPlayerManager.STATE_NULL){
					toastMsg=Common.showMessage(toastMsg, PlayerMainActivity.this, "请先添加歌曲...");
					return;
				}
				//顺序列表播放结束
				if(mediaPlayerManager.getPlayerState()==MediaPlayerManager.STATE_OVER){
					toastMsg=Common.showMessage(toastMsg, PlayerMainActivity.this, "播放列表已经按顺序播放完毕！");
					return;
				}
				mediaPlayerManager.pauseOrPlayer();
				final int state=mediaPlayerManager.getPlayerState();
				if(state==MediaPlayerManager.STATE_PLAYER||state==MediaPlayerManager.STATE_PREPARE){//播放
					ibtn_player_control_play.setBackgroundResource(R.drawable.player_btn_player_pause);
				}else if(state==MediaPlayerManager.STATE_PAUSE){//暂停
					ibtn_player_control_play.setBackgroundResource(R.drawable.player_btn_player_play);
				}
				break;
			case R.id.ibtn_player_control_next:
				ibtn_player_control_play.setBackgroundResource(R.drawable.player_btn_player_pause);
				mediaPlayerManager.nextPlayer();
				break;
			default:
				break;
			}
		}
	};
	
	private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

		public void onStopTrackingTouch(SeekBar seekBar) {
			if (seekBar.getId() == R.id.sb_player_voice) {

			} else if (seekBar.getId() == R.id.sb_player_playprogress) {
				isSeekDrag=false;
				mediaPlayerManager.seekTo(seekBar.getProgress());
			}
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			if(seekBar.getId() == R.id.sb_player_playprogress) {
				 isSeekDrag=true;
				 tv_player_playing_time.setText(Common.formatSecondTime(seekBar.getProgress()));
			}
		}

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (seekBar.getId() == R.id.sb_player_voice) {
				// 设置音量
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, 0);
			}else if(seekBar.getId() == R.id.sb_player_playprogress) {
				if(isSeekDrag){
					tv_player_playing_time.setText(Common.formatSecondTime(progress));
				}
			}
		}
	};
}