package com.carmusic.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.guet.haojiayou.R;

import com.carmusic.adapter.DownLoadListAdapter;
import com.carmusic.adapter.DownLoadingListAdapter;
import com.carmusic.adapter.ListItemAdapter;
import com.carmusic.adapter.MenuAdapter;
import com.carmusic.adapter.SongItemAdapter;
import com.carmusic.adapter.SongItemWebAdapter;
import com.carmusic.custom.CarMDialog;
import com.carmusic.custom.CarMMenu;
import com.carmusic.custom.FlingGalleryView;
import com.carmusic.custom.FlingGalleryView.OnScrollToScreenListener;
import com.carmusic.dao.AlbumDao;
import com.carmusic.dao.ArtistDao;
import com.carmusic.dao.DownLoadInfoDao;
import com.carmusic.dao.PlayerListDao;
import com.carmusic.dao.SongDao;
import com.carmusic.data.SystemSetting;
import com.carmusic.entity.PlayerList;
import com.carmusic.entity.Song;
import com.carmusic.recevier.AutoShutdownRecevier;
import com.carmusic.service.DownLoadManager;
import com.carmusic.service.MediaPlayerManager;
import com.carmusic.service.MediaPlayerManager.ServiceConnectionListener;
import com.carmusic.util.Common;
import com.carmusic.util.XmlUtil;

public class ListMainActivity extends BaseActivity {
	
	//导航栏选项卡布局数组
	private ViewGroup[] vg_list_tab_item = new ViewGroup[3];
	private FlingGalleryView fgv_list_main;

	//当前屏幕的下标
	private int screenIndex = 0;
	//导航栏的内容
	private String[] list_item_items;
	//导航栏的icon
	private int[] list_item_icons = new int[] { R.drawable.music_list_music_icon,
			R.drawable.music_list_web_icon, R.drawable.music_list_download_icon };
	
	//本地列表
	private ViewGroup list_main_music;
	//网络音乐
	private ViewGroup list_main_web;
	//下载管理
	private ViewGroup list_main_download;

	//主屏幕内容布局
	private ViewGroup rl_list_main_content;
	//切换内容布局
	private ViewGroup rl_list_content;
	
	//本地音乐和下载管理的二三级布局
	private ImageButton ibtn_list_content_icon;//左边图标
	private ImageButton ibtn_list_content_do_icon;//右边图标
	private TextView tv_list_content_title;//标题
	private ListView lv_list_change_content;//替换ListView
	private Button btn_list_random_music2;//随机播放
	
	//本地音乐随机播放
	private Button btn_list_random_music_local;
	//网络音乐随机播放
	private Button btn_list_random_music_web;
	
	//网络音乐播放列表
	private ListView lv_list_web;
	
	//底部工具栏
	private ImageButton ibtn_player_albumart;//专辑封面
	private ImageButton ibtn_player_control;//播放/暂停
	private TextView tv_player_title;//播放歌曲 歌手-标题
	private ProgressBar pb_player_progress;//播放进度条
	private TextView tv_player_currentPosition;//当前播放的进度
	private TextView tv_player_duration;//歌曲播放时长
	
	/**
	 * 默认页：0
	 * 1.全部歌曲 2.歌手 3.专辑 4.文件夹 5.播放列表 6.我最爱听 7.最近播放 8.正在下载 9.下载完成
	 * 22.歌手二级 33.专辑二级 44.文件夹二级 55.播放列表二级 
	 * */
	private int pageNumber=0;
	
	private SongDao songDao;
	private ArtistDao artistDao;
	private AlbumDao albumDao;
	private PlayerListDao playerListDao;
	private Toast toast;
	private LayoutParams params;
	private LayoutInflater inflater;
	private DownLoadInfoDao downLoadInfoDao;
	
	private DownLoadManager downLoadManager;
	private DownLoadBroadcastRecevier downLoadBroadcastRecevier;
	
	private MediaPlayerManager mediaPlayerManager;
	private MediaPlayerBroadcastReceiver mediaPlayerBroadcastReceiver;
	
	private CarMMenu carmMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_list_main);		
		SystemSetting setting = new SystemSetting(this, false);
		checkScannerTip(setting);
		
		songDao=new SongDao(this);
		artistDao=new ArtistDao(this);
		albumDao=new AlbumDao(this);
		playerListDao=new PlayerListDao(this);
		downLoadInfoDao=new DownLoadInfoDao(this);
		params=new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		
		//导航栏选项卡数组 实例化
		vg_list_tab_item[0] = (ViewGroup) this
				.findViewById(R.id.list_tab_item_music);
		vg_list_tab_item[1] = (ViewGroup) this
				.findViewById(R.id.list_tab_item_web);
		vg_list_tab_item[2] = (ViewGroup) this
				.findViewById(R.id.list_tab_item_download);

		//主屏幕内容布局选项 实例化
		list_main_music = (ViewGroup) this.findViewById(R.id.list_main_music);
		list_main_web = (ViewGroup) this.findViewById(R.id.list_main_web);
		list_main_download = (ViewGroup) this.findViewById(R.id.list_main_download);
		
		//随机播放音乐
		btn_list_random_music_local=(Button)list_main_music.findViewById(R.id.btn_list_random_music);
		btn_list_random_music_web=(Button)list_main_web.findViewById(R.id.btn_list_random_web);
		btn_list_random_music_local.setOnClickListener(btn_randomPlayerListener);
		btn_list_random_music_web.setOnClickListener(btn_randomPlayerListener);
		
		//主屏幕内容布局和切换内容布局 实例化
		rl_list_main_content=(ViewGroup)this.findViewById(R.id.rl_list_main_content);
		rl_list_content=(ViewGroup)this.findViewById(R.id.rl_list_content);
		
		//本地音乐和下载管理的二三级布局-公共 标题和内容区域
		ibtn_list_content_icon=(ImageButton)rl_list_content.findViewById(R.id.ibtn_list_content_icon);
		ibtn_list_content_do_icon=(ImageButton)rl_list_content.findViewById(R.id.ibtn_list_content_do_icon);
		tv_list_content_title=(TextView)rl_list_content.findViewById(R.id.tv_list_content_title);
		lv_list_change_content=(ListView)rl_list_content.findViewById(R.id.lv_list_change_content);
		ibtn_list_content_icon.setOnClickListener(imageButton_listener);
		ibtn_list_content_do_icon.setOnClickListener(imageButton_listener);
		lv_list_change_content.setOnItemClickListener(list_change_content_listener);
		lv_list_change_content.setOnItemLongClickListener(list_change_content_looglistener);
		btn_list_random_music2=(Button)rl_list_content.findViewById(R.id.btn_list_random_music2);
		btn_list_random_music2.setOnClickListener(btn_randomPlayerListener);
		
		//底部工具栏
		ibtn_player_albumart=(ImageButton)this.findViewById(R.id.ibtn_player_albumart);
		ibtn_player_control=(ImageButton)this.findViewById(R.id.ibtn_player_control);
		tv_player_title=(TextView)this.findViewById(R.id.tv_player_title);
		pb_player_progress=(ProgressBar)this.findViewById(R.id.pb_player_progress);
		tv_player_currentPosition=(TextView)this.findViewById(R.id.tv_player_currentPosition);
		tv_player_duration=(TextView)this.findViewById(R.id.tv_player_duration);
		
		ibtn_player_albumart.setOnClickListener(imageButton_listener);
		ibtn_player_control.setOnClickListener(imageButton_listener);
		
		//切换主屏幕内容容器
		fgv_list_main = (FlingGalleryView) rl_list_main_content
				.findViewById(R.id.fgv_list_main);
		fgv_list_main.setDefaultScreen(screenIndex);
		fgv_list_main.setOnScrollToScreenListener(scrollToScreenListener);

		//从资源文件中获取导航栏选项卡标题
		list_item_items = getResources().getStringArray(R.array.list_tab_items);
		//初始化导航栏
		initTabItem();
		//初始化本地音乐内容区域
		initListMusicItem();
		//初始化网络音乐
		lv_list_web=(ListView)list_main_web.findViewById(R.id.lv_list_web);
		lv_list_web.setAdapter(new SongItemWebAdapter(this, XmlUtil.parseWebSongList(this)).setItemListener(mItemListener));
		lv_list_web.setOnItemClickListener(webItemClickListener);
		
		//初始化下载管理
		initDownLoad();
		
		//下载管理
		downLoadManager=new DownLoadManager(this);
		downLoadManager.startAndBindService();
		
		//播放器管理
		mediaPlayerManager=new MediaPlayerManager(this);
		mediaPlayerManager.setConnectionListener(mConnectionListener);
		
		createMenu();
	}
	
	/**
	 * 检查显示扫描歌曲提示(第一次进入软件界面)
	 * */
	private void checkScannerTip(SystemSetting setting){
		if(setting.getValue(SystemSetting.KEY_ISSCANNERTIP)==null){
			new CarMDialog.Builder(this).setTitle("扫描提示").setMessage("是否要扫描本地歌曲入库？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					dialog.dismiss();
					Intent it=new Intent(ListMainActivity.this,ScanMusicActivity.class);
					startActivityForResult(it, 1);
				}
			}).setNegativeButton("取消", null).create().show();
			setting.setValue(SystemSetting.KEY_ISSCANNERTIP, "OK");
		}
	}
	
	/**
	 * 随机播放
	 * */
	private OnClickListener btn_randomPlayerListener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mediaPlayerManager.getPlayerFlag()==MediaPlayerManager.PLAYERFLAG_WEB){
				int[] playerInfo=new int[]{-1,-1};
				((SongItemWebAdapter)lv_list_web.getAdapter()).setPlayerInfo(playerInfo);
			}
			if(v.getId()==R.id.btn_list_random_music){
				mediaPlayerManager.randomPlayer(MediaPlayerManager.PLAYERFLAG_ALL, "");
			}else if(v.getId()==R.id.btn_list_random_web){
				if(!Common.getNetIsAvailable(ListMainActivity.this)){
					toast=Common.showMessage(toast, ListMainActivity.this, "当前网络不可用");
					return;
				}
				mediaPlayerManager.randomPlayer(MediaPlayerManager.PLAYERFLAG_WEB, "");
			}else if(v.getId()==R.id.btn_list_random_music2){
				if(Integer.valueOf(v.getTag().toString())==0){
					return;
				}
				if(pageNumber==1){
					mediaPlayerManager.randomPlayer(MediaPlayerManager.PLAYERFLAG_ALL, "");
				}else if(pageNumber==6){
					mediaPlayerManager.randomPlayer(MediaPlayerManager.PLAYERFLAG_LIKE, "");
				}else if(pageNumber==7){
					mediaPlayerManager.randomPlayer(MediaPlayerManager.PLAYERFLAG_LATELY, "");
				}else if(pageNumber==9){
					mediaPlayerManager.randomPlayer(MediaPlayerManager.PLAYERFLAG_DOWNLOAD, "");
				}else if(pageNumber==22){
					mediaPlayerManager.randomPlayer(MediaPlayerManager.PLAYERFLAG_ARTIST, condition);
				}else if(pageNumber==33){
					mediaPlayerManager.randomPlayer(MediaPlayerManager.PLAYERFLAG_ALBUM, condition);
				}else if(pageNumber==44){
					mediaPlayerManager.randomPlayer(MediaPlayerManager.PLAYERFLAG_FOLDER, condition);
				}else if(pageNumber==55){
					mediaPlayerManager.randomPlayer(MediaPlayerManager.PLAYERFLAG_PLAYERLIST, condition);
				}
			}
		}
	};
	
	//网络音乐，点击播放/暂停
	private OnItemClickListener webItemClickListener=new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(!Common.getNetIsAvailable(ListMainActivity.this)){
				toast=Common.showMessage(toast, ListMainActivity.this, "当前网络不可用");
				return;
			}
			int songId=Integer.valueOf(((SongItemWebAdapter.ViewHolder)view.getTag()).tv_web_list_item_top.getTag().toString());
			if(songId==mediaPlayerManager.getSongId()){
				PlayerOrPause(view);
			}else {
				ibtn_player_control.setBackgroundResource(R.drawable.player_btn_mini_pause);
				mediaPlayerManager.player(songId,MediaPlayerManager.PLAYERFLAG_WEB, null);
				int[] playerInfo=new int[]{songId,mediaPlayerManager.getPlayerState()};
				((SongItemWebAdapter)lv_list_web.getAdapter()).setPlayerInfo(playerInfo);
			}
		}
	};

	private ServiceConnectionListener mConnectionListener=new ServiceConnectionListener() {
		@Override
		public void onServiceDisconnected() {
		}
		@Override
		public void onServiceConnected() {
			//每次进入activity时都要重新拿数据
			mediaPlayerManager.initPlayerMain_SongInfo();
			updateSongItemList();
		}
	};
	
	@Override
	protected void onStart() {
		super.onStart();
		//注册播放器-广播接收器
		mediaPlayerBroadcastReceiver=new MediaPlayerBroadcastReceiver();
		registerReceiver(mediaPlayerBroadcastReceiver, new IntentFilter(MediaPlayerManager.BROADCASTRECEVIER_ACTON));
		//注册下载任务-广播接收器
		downLoadBroadcastRecevier=new DownLoadBroadcastRecevier();
		registerReceiver(downLoadBroadcastRecevier, new IntentFilter(DownLoadManager.BROADCASTRECEVIER_ACTON));
		mediaPlayerManager.startAndBindService();
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(mediaPlayerBroadcastReceiver);
		unregisterReceiver(downLoadBroadcastRecevier);
		mediaPlayerManager.unbindService();
	}

	@Override
	protected void onDestroy() {
		downLoadManager.unbindService();
		super.onDestroy();
	}
	
	
	/**
	 * 播放器-广播接收器
	 * */
	private class MediaPlayerBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			int flag=intent.getIntExtra("flag", -1);
			if(flag==MediaPlayerManager.FLAG_CHANGED){
				int currentPosition=intent.getIntExtra("currentPosition", 0);
				int duration=intent.getIntExtra("duration", 0);
				tv_player_currentPosition.setText(Common.formatSecondTime(currentPosition));
				tv_player_duration.setText(Common.formatSecondTime(duration));
				pb_player_progress.setProgress(currentPosition);
				pb_player_progress.setMax(duration);
				
			}else if(flag==MediaPlayerManager.FLAG_PREPARE){
				String albumPic=intent.getStringExtra("albumPic");
				tv_player_title.setText(intent.getStringExtra("title"));
				if(TextUtils.isEmpty(albumPic)){
					ibtn_player_albumart.setImageResource(R.drawable.music_min_default_album);
				}else{
					Bitmap bitmap=BitmapFactory.decodeFile(albumPic);
					//判断SD图片是否存在
					if(bitmap!=null){
						ibtn_player_albumart.setImageBitmap(bitmap);
					}else{
						ibtn_player_albumart.setImageResource(R.drawable.music_min_default_album);
					}
				}
				int duration=intent.getIntExtra("duration", 0);
				int currentPosition=intent.getIntExtra("currentPosition", 0);
				tv_player_currentPosition.setText(Common.formatSecondTime(currentPosition));
				tv_player_duration.setText(Common.formatSecondTime(duration));
				pb_player_progress.setMax(duration);
				pb_player_progress.setProgress(currentPosition);
				pb_player_progress.setSecondaryProgress(0);
				
				//更新播放列表状态
				updateSongItemList();
			}else if(flag==MediaPlayerManager.FLAG_INIT){//初始化播放信息
				int currentPosition=intent.getIntExtra("currentPosition", 0);
				int duration=intent.getIntExtra("duration", 0);
				pb_player_progress.setMax(duration);
				pb_player_progress.setProgress(currentPosition);
				tv_player_currentPosition.setText(Common.formatSecondTime(currentPosition));
				tv_player_duration.setText(Common.formatSecondTime(duration));
				tv_player_title.setText(intent.getStringExtra("title"));
				String albumPic=intent.getStringExtra("albumPic");
				if(TextUtils.isEmpty(albumPic)){
					ibtn_player_albumart.setImageResource(R.drawable.music_min_default_album);
				}else{
					Bitmap bitmap=BitmapFactory.decodeFile(albumPic);
					//判断SD卡图片是否存在
					if(bitmap!=null){
						ibtn_player_albumart.setImageBitmap(bitmap);
					}else{
						ibtn_player_albumart.setImageResource(R.drawable.music_min_default_album);
					}
				}
				int playerState=intent.getIntExtra("playerState", 0);
				if(playerState==MediaPlayerManager.STATE_PLAYER||playerState==MediaPlayerManager.STATE_PREPARE){//播放
					ibtn_player_control.setBackgroundResource(R.drawable.player_btn_mini_pause);
				}else{
					ibtn_player_control.setBackgroundResource(R.drawable.player_btn_mini_player);
				}

				if(mediaPlayerManager.getPlayerState()==MediaPlayerManager.STATE_OVER){
					if(pageNumber==1||pageNumber==6||pageNumber==7||pageNumber==22||pageNumber==33||pageNumber==44||pageNumber==55){
						((SongItemAdapter)lv_list_change_content.getAdapter()).setPlayerState(mediaPlayerManager.getPlayerState());
					}
					if(pageNumber==9){
						((DownLoadListAdapter)lv_list_change_content.getAdapter()).setPlayerState(mediaPlayerManager.getPlayerState());
					}
				}
			}else if(flag==MediaPlayerManager.FLAG_LIST){
				//自动切歌播放，更新前台歌曲列表
				updateSongItemList();
			}else if(flag==MediaPlayerManager.FLAG_BUFFERING){
				int percent=intent.getIntExtra("percent", 0);
				percent=(int)(pb_player_progress.getMax()/100f)*percent;
				pb_player_progress.setSecondaryProgress(percent);
			}
		}
	}
	
	//自动切歌播放，更新前台歌曲列表
	private void updateSongItemList(){
		int[] playerInfo=new int[]{mediaPlayerManager.getSongId(),mediaPlayerManager.getPlayerState()};
		if(mediaPlayerManager.getPlayerFlag()==MediaPlayerManager.PLAYERFLAG_WEB){
			((SongItemWebAdapter)lv_list_web.getAdapter()).setPlayerInfo(playerInfo);
		}else{
			if(pageNumber==1||pageNumber==6||pageNumber==7||pageNumber==22||pageNumber==33||pageNumber==44||pageNumber==55){
				((SongItemAdapter)lv_list_change_content.getAdapter()).setPlayerInfo(playerInfo);
			}
			if(pageNumber==9){
				((DownLoadListAdapter)lv_list_change_content.getAdapter()).setPlayerInfo(playerInfo);
			}
		}
		int state=mediaPlayerManager.getPlayerState();
		if(state==MediaPlayerManager.STATE_PLAYER||state==MediaPlayerManager.STATE_PREPARE){//播放
			ibtn_player_control.setBackgroundResource(R.drawable.player_btn_mini_pause);
		}else if(state==MediaPlayerManager.STATE_PAUSE){//暂停
			ibtn_player_control.setBackgroundResource(R.drawable.player_btn_mini_player);
		}
	}
	
	/**
	 * 初始化下载管理信息
	 * */
	private void initDownLoad(){
		List<HashMap<String, Object>> data=Common.getListDownLoadData();
		SimpleAdapter download_adapter = new SimpleAdapter(this, data,
				R.layout.music_list_item, new String[] { "icon", "title", "icon2" },
				new int[] { R.id.iv_list_item_icon, R.id.tv_list_item_title,
						R.id.iv_list_item_icon2 });

		ListView lv_list_download = (ListView) list_main_download
				.findViewById(R.id.lv_list_download);
		lv_list_download.setAdapter(download_adapter);
		lv_list_download.setOnItemClickListener(list_download_listener);
	}
	
	//网络音乐项事件
	private SongItemWebAdapter.ItemListener mItemListener=new SongItemWebAdapter.ItemListener() {
		@Override
		public void onDownLoad(Song	song) {
			if(!Common.getNetIsAvailable(ListMainActivity.this)){
				toast=Common.showMessage(toast, ListMainActivity.this, "当前网络不可用");
				return;
			}
			if(!Common.isExistSdCard()){
				toast=Common.showMessage(toast, ListMainActivity.this, "请先插入SD卡");
				return;
			}
			//判断是否在下载列表中
			if(downLoadInfoDao.isExist(song.getNetUrl())){
				toast=Common.showMessage(toast, ListMainActivity.this, "此歌曲已经在下载列表中");
				return;
			}
			//判断是否已经下载过
			if(songDao.isExist(song.getNetUrl())){
				toast=Common.showMessage(toast, ListMainActivity.this, "此歌曲已经在下载过了");
				return;
			}
			//添加到下载列表中
			downLoadManager.add(song);
		}
		
	};
	
	//ImageButton click
	private OnClickListener imageButton_listener=new OnClickListener() {
		
		public void onClick(View v) {
			if(v.getId()==R.id.ibtn_list_content_icon){
				rl_list_content.setVisibility(View.GONE);
				rl_list_main_content.setVisibility(View.VISIBLE);
				pageNumber=0;
			}else if(v.getId()==R.id.ibtn_list_content_do_icon){
				if(pageNumber==5){//播放列表时，弹出菜单
					doPlayList(0,0,null);
				}
			}else if(v.getId()==R.id.ibtn_player_control){
				PlayerOrPause(null);
			}else if(v.getId()==R.id.ibtn_player_albumart){
				startActivity(new Intent(ListMainActivity.this,PlayerMainActivity.class));
			}
		}
	};
	
	/**
	 * 播放或暂停歌曲
	 * */
	private void PlayerOrPause(View v){
		if(mediaPlayerManager.getPlayerState()==MediaPlayerManager.STATE_NULL){
			toast=Common.showMessage(toast, ListMainActivity.this, "请先添加歌曲...");
			return;
		}
		if(v==null){
			//当前列表播放结束
			if(mediaPlayerManager.getPlayerState()==MediaPlayerManager.STATE_OVER){
				toast=Common.showMessage(toast, ListMainActivity.this, "当前列表已经播放完毕！");
				return;
			}
		}
		mediaPlayerManager.pauseOrPlayer();
		final int state=mediaPlayerManager.getPlayerState();
		int itemRsId=0;
		if(state==MediaPlayerManager.STATE_PLAYER||state==MediaPlayerManager.STATE_PREPARE){//播放
			ibtn_player_control.setBackgroundResource(R.drawable.player_btn_mini_pause);
			itemRsId=R.drawable.music_list_item_player;
		}else if(state==MediaPlayerManager.STATE_PAUSE){//暂停
			ibtn_player_control.setBackgroundResource(R.drawable.player_btn_mini_player);
			itemRsId=R.drawable.music_list_item_pause;
		}
		if(mediaPlayerManager.getPlayerFlag()==MediaPlayerManager.PLAYERFLAG_WEB){
			if(v==null){
				((SongItemWebAdapter)lv_list_web.getAdapter()).setPlayerState(mediaPlayerManager.getPlayerState());
			}else{
				((SongItemWebAdapter.ViewHolder)v.getTag()).tv_web_list_item_number.setBackgroundResource(itemRsId);
			}
		}else{
			if(pageNumber==1||pageNumber==6||pageNumber==7||pageNumber==22||pageNumber==33||pageNumber==44||pageNumber==55){
				if(v==null){
					((SongItemAdapter)lv_list_change_content.getAdapter()).setPlayerState(mediaPlayerManager.getPlayerState());
				}else{
					((SongItemAdapter.ViewHolder)v.getTag()).tv_song_list_item_number.setBackgroundResource(itemRsId);
				}
			}
			if(pageNumber==9){
				if(v==null){
					((DownLoadListAdapter)lv_list_change_content.getAdapter()).setPlayerState(mediaPlayerManager.getPlayerState());
				}else{
					((DownLoadListAdapter.ViewHolder)v.getTag()).tv_download_list_item_number.setBackgroundResource(itemRsId);
				}
			}
		}
	}
	
	/**
	 * 添加或更新播放列表
	 * */
	private void doPlayList(final int flag,final int id,String text){
		String actionmsg = null;
		final EditText et_newPlayList=new EditText(ListMainActivity.this);
		et_newPlayList.setLayoutParams(params);
		et_newPlayList.setTextSize(15);
		if(flag==0){//新建
			actionmsg="创建";
			et_newPlayList.setHint("请输入播放列表名称");
		}else if(flag==1){//更新
			actionmsg="更新";
			et_newPlayList.setText(text);
			et_newPlayList.selectAll();
		}
		final String actionmsg2=actionmsg;

		new CarMDialog.Builder(ListMainActivity.this).setTitle(actionmsg+"播放列表")
		.setView(et_newPlayList,5,10,5,10).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				String text=et_newPlayList.getText().toString().trim();
				if(!TextUtils.isEmpty(text)){
					if(playerListDao.isExists(text)){
						toast=Common.showMessage(toast, ListMainActivity.this, "此名称已经存在！");
					}else{
						PlayerList playerList=new PlayerList();
						playerList.setName(text);
						
						int rowId=-1;
						if(flag==0){//创建播放列表
							rowId=(int) playerListDao.add(playerList);
						}else if(flag==1){//更新播放列表
							playerList.setId(id);
							rowId=playerListDao.update(playerList);
						}
						if(rowId>0){//判断是否成功
							toast=Common.showMessage(toast, ListMainActivity.this, actionmsg2+"成功！");
							lv_list_change_content.setAdapter(new ListItemAdapter(ListMainActivity.this, 
									playerListDao.searchAll(),R.drawable.music_local_custom));
							dialog.cancel();
							dialog.dismiss();
						}else{
							toast=Common.showMessage(toast, ListMainActivity.this, actionmsg2+"失败！");
						}
					}
				}
			}
		}).setNegativeButton("取消",null).create().show();
	}

	/**
	 * 创建底部菜单
	 * */
	private void createMenu(){
		carmMenu=new CarMMenu(ListMainActivity.this);
		
		List<int[]> data1=new ArrayList<int[]>();
		data1.add(new int[]{R.drawable.music_btn_menu_scanner,R.string.scan_title});
		carmMenu.addItem("常用", data1, new MenuAdapter.ItemListener() {
			@Override
			public void onClickListener(int position, View view) {
				carmMenu.cancel();
				if(position==0){
					Intent it=new Intent(ListMainActivity.this,ScanMusicActivity.class);
					startActivityForResult(it, 1);
				}else if(position==2){
					cancelAutoShutdown();
					mediaPlayerManager.stop();
					downLoadManager.stop();
					finish();
				}
			}
		});
		carmMenu.create();
	}
	
	/**
	 * 取消定时关闭
	 * */
	private void cancelAutoShutdown(){
		AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
		PendingIntent operation=PendingIntent.getBroadcast(ListMainActivity.this, 0, new Intent(ListMainActivity.this,AutoShutdownRecevier.class), PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(operation);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1&&resultCode==1){
			mediaPlayerManager.initScanner_SongInfo();
			updateListAdapterData();
		}
	}

	//重写返回键事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(pageNumber==0){
				int state=mediaPlayerManager.getPlayerState();
				if(state==MediaPlayerManager.STATE_NULL||state==MediaPlayerManager.STATE_OVER||state==MediaPlayerManager.STATE_PAUSE){
					cancelAutoShutdown();
					mediaPlayerManager.stop();
					downLoadManager.stop();
				}
				finish();
				return true;
			}
			return backPage();
		}else if(keyCode==KeyEvent.KEYCODE_MENU&&!carmMenu.isShowing()){
			carmMenu.showAtLocation(findViewById(R.id.rl_parent_cotent), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 返回键事件
	 * */
	private boolean backPage(){
		if(pageNumber<10){
			rl_list_content.setVisibility(View.GONE);
			rl_list_main_content.setVisibility(View.VISIBLE);
			pageNumber=0;
			return true;
		}else{
			if(pageNumber==22){
				jumpPage(1, 2,null);
			}else if(pageNumber==33){
				jumpPage(1, 3,null);
			}else if(pageNumber==44){
				jumpPage(1, 4,null);
			}else if(pageNumber==55){
				jumpPage(1, 5,null);
			}
		}
		return false;
	}
	
	//初始化本地音乐
	private void initListMusicItem() {
		List<HashMap<String, Object>> data=Common.getListMusicData();
		SimpleAdapter music_adapter = new SimpleAdapter(this, data,
				R.layout.music_list_item, new String[] { "icon", "title", "icon2" },
				new int[] { R.id.iv_list_item_icon, R.id.tv_list_item_title,
						R.id.iv_list_item_icon2 });

		ListView lv_list_music = (ListView) list_main_music
				.findViewById(R.id.lv_list_music);
		lv_list_music.setAdapter(music_adapter);
		lv_list_music.setOnItemClickListener(list_music_listener);
	}
	
	//音乐列表项的点击事件
	private OnItemClickListener list_music_listener=new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			jumpPage(1, position+1,null);
		}
	};
	
	//下载管理列表项的点击事件
	private OnItemClickListener list_download_listener=new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			jumpPage(1, position+8,null);
		}
	};
		
	/**
	 * 当前歌曲列表的播放列表的查询条件
	 * */
	private String condition=null;
	
	/**
	 * 跳转某页面事件
	 * */
	private void jumpPage(int classIndex,int flag,Object obj){
		int[] playerInfo=new int[]{mediaPlayerManager.getSongId(),mediaPlayerManager.getPlayerState()};
		if(classIndex==1){
			rl_list_main_content.setVisibility(View.GONE);
			rl_list_content.setVisibility(View.VISIBLE);
			ibtn_list_content_icon.setBackgroundResource(R.drawable.player_btn_list);
			MarginLayoutParams margin = new MarginLayoutParams(ibtn_list_content_icon.getLayoutParams());
			margin.setMargins(30, 30, 0, 0);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
			layoutParams.height = 85;//设置图片的高度         
			layoutParams.width = 85; //设置图片的宽度   
			layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
			ibtn_list_content_icon.setLayoutParams(layoutParams);
			
			btn_list_random_music2.setVisibility(View.GONE);
			ibtn_list_content_do_icon.setVisibility(View.GONE);

			if(flag==1){//全部歌曲
				tv_list_content_title.setText("全部歌曲");
				List<String[]> data=songDao.searchByAll();
				lv_list_change_content.setAdapter(new SongItemAdapter(ListMainActivity.this, data,playerInfo).setItemListener(songItemListener));
				btn_list_random_music2.setVisibility(View.VISIBLE);
				btn_list_random_music2.setText("(共"+data.size()+"首)随机播放");
				btn_list_random_music2.setTag(data.size());
			}else if(flag==2){//歌手
				tv_list_content_title.setText("歌手");
				lv_list_change_content.setAdapter(new ListItemAdapter(ListMainActivity.this, 
						artistDao.searchAll(),R.drawable.music_default_list_singer));
			}else if(flag==3){//专辑
				tv_list_content_title.setText("专辑");
				lv_list_change_content.setAdapter(new ListItemAdapter(ListMainActivity.this, 
						albumDao.searchAll(),R.drawable.music_default_list_album));
			}else if(flag==4){//文件夹
				tv_list_content_title.setText("文件夹");
				lv_list_change_content.setAdapter(new ListItemAdapter(ListMainActivity.this,
						songDao.searchByDirectory(), R.drawable.music_local_file));
			}else if(flag==5){//播放列表
				tv_list_content_title.setText("播放列表");
				lv_list_change_content.setAdapter(new ListItemAdapter(ListMainActivity.this, 
						playerListDao.searchAll(),R.drawable.music_local_custom));
				ibtn_list_content_do_icon.setVisibility(View.VISIBLE);
			}else if(flag==6){//我最爱听
				tv_list_content_title.setText("我最爱听");
				btn_list_random_music2.setVisibility(View.VISIBLE);
				List<String[]> data=songDao.searchByIsLike();
				lv_list_change_content.setAdapter(new SongItemAdapter(ListMainActivity.this, data,playerInfo).setItemListener(songItemListener));
				btn_list_random_music2.setVisibility(View.VISIBLE);
				btn_list_random_music2.setText("(共"+data.size()+"首)随机播放");
				btn_list_random_music2.setTag(data.size());
			}else if(flag==7){//最近播放
				tv_list_content_title.setText("最近播放");
				btn_list_random_music2.setVisibility(View.VISIBLE);
				List<String[]> data=songDao.searchByLately(mediaPlayerManager.getLatelyStr());
				lv_list_change_content.setAdapter(new SongItemAdapter(ListMainActivity.this, data,playerInfo).setItemListener(songItemListener));
				btn_list_random_music2.setVisibility(View.VISIBLE);
				btn_list_random_music2.setText("(共"+data.size()+"首)随机播放");
				btn_list_random_music2.setTag(data.size());
			}else if(flag==8){//正在下载
				tv_list_content_title.setText("正在下载");
				lv_list_change_content.setAdapter(new DownLoadingListAdapter(ListMainActivity.this, 
						downLoadManager.getDownLoadData()).setItemListener(downLoadingListItemListener));
			}else if(flag==9){//下载完成
				tv_list_content_title.setText("下载完成");
				List<Song> data=songDao.searchByDownLoad();
				lv_list_change_content.setAdapter(new DownLoadListAdapter(ListMainActivity.this, 
						data,playerInfo).setItemListener(downLoadListItemListener));
				btn_list_random_music2.setVisibility(View.VISIBLE);
				btn_list_random_music2.setText("(共"+data.size()+"首)随机播放");
				btn_list_random_music2.setTag(data.size());
			}
			pageNumber=flag;
		}else if(classIndex==2){
			btn_list_random_music2.setVisibility(View.VISIBLE);
			TextView textView=((ListItemAdapter.ViewHolder)obj).textView;
			condition=textView.getTag().toString().trim();
			tv_list_content_title.setText(textView.getText().toString());
			List<String[]> data=null;
			if(flag==22){//某歌手歌曲
				data=songDao.searchByArtist(condition);
			}else if(flag==33){//某专辑歌曲
				data=songDao.searchByAlbum(condition);
			}else if(flag==44){//某文件夹歌曲
				data=songDao.searchByDirectory(condition);
			}else if(flag==55){//某播放列表的歌曲
				data=songDao.searchByPlayerList("$"+condition+"$");
			}
			lv_list_change_content.setAdapter(new SongItemAdapter(ListMainActivity.this, data,playerInfo).setItemListener(songItemListener));
			btn_list_random_music2.setText("(共"+data.size()+"首)随机播放");
			btn_list_random_music2.setTag(data.size());
			pageNumber=flag;
		}
	}
	
	/**
	 * 更新本地列表的数据展示（扫描后）
	 * */
	private void updateListAdapterData(){
		int[] playerInfo=new int[]{mediaPlayerManager.getSongId(),mediaPlayerManager.getPlayerState()};
		if(pageNumber==1){
			List<String[]> data=songDao.searchByAll();
			lv_list_change_content.setAdapter(new SongItemAdapter(ListMainActivity.this, data,playerInfo).setItemListener(songItemListener));
			btn_list_random_music2.setText("(共"+data.size()+"首)随机播放");
			btn_list_random_music2.setTag(data.size());
		}else if(pageNumber==22||pageNumber==33||pageNumber==44||pageNumber==55){
			List<String[]> data=null;
			if(pageNumber==22){
				data=songDao.searchByArtist(condition);
			}else if(pageNumber==33){
				data=songDao.searchByAlbum(condition);
			}else if(pageNumber==44){
				data=songDao.searchByDirectory(condition);
			}else if(pageNumber==55){
				data=songDao.searchByPlayerList("$"+condition+"$");
			}
			lv_list_change_content.setAdapter(new SongItemAdapter(ListMainActivity.this, data,playerInfo).setItemListener(songItemListener));
			btn_list_random_music2.setText("(共"+data.size()+"首)随机播放");
			btn_list_random_music2.setTag(data.size());
		}
	}
	
	/**
	 * 下载任务-广播接收器
	 * */
	private class DownLoadBroadcastRecevier extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			int flag=intent.getIntExtra("flag", -1);
			if(flag==DownLoadManager.FLAG_CHANGED){
				if(pageNumber==8){
					((DownLoadingListAdapter)lv_list_change_content.getAdapter()).setData(downLoadManager.getDownLoadData());
				}
			}else if(flag==DownLoadManager.FLAG_WAIT){
				toast=Common.showMessage(toast, ListMainActivity.this,"\""+intent.getStringExtra("displayname")+"\""+"添加到了下载列表中!");
			}else if(flag==DownLoadManager.FLAG_COMPLETED){
				if(pageNumber==8){
					((DownLoadingListAdapter)lv_list_change_content.getAdapter()).setData(downLoadManager.getDownLoadData());
				}
				toast=Common.showMessage(toast, ListMainActivity.this,"\""+intent.getStringExtra("displayname")+"\""+"下载完成!");
			}else if(flag==DownLoadManager.FLAG_FAILED){
				if(pageNumber==8){
					((DownLoadingListAdapter)lv_list_change_content.getAdapter()).setData(downLoadManager.getDownLoadData());
				}
				toast=Common.showMessage(toast, ListMainActivity.this,"\""+intent.getStringExtra("displayname")+"\""+"服务器找不到文件!");
			}else if(flag==DownLoadManager.FLAG_TIMEOUT){
				if(pageNumber==8){
					((DownLoadingListAdapter)lv_list_change_content.getAdapter()).setData(downLoadManager.getDownLoadData());
				}
				toast=Common.showMessage(toast, ListMainActivity.this,"\""+intent.getStringExtra("displayname")+"\""+"连接已经超时!");
			}else if(flag==DownLoadManager.FLAG_ERROR){
				if(pageNumber==8){
					((DownLoadingListAdapter)lv_list_change_content.getAdapter()).setData(downLoadManager.getDownLoadData());
				}
				toast=Common.showMessage(toast, ListMainActivity.this,"\""+intent.getStringExtra("displayname")+"\""+"发生错误!");
			}else if(flag==DownLoadManager.FLAG_COMMON){
				if(pageNumber==8){
					((DownLoadingListAdapter)lv_list_change_content.getAdapter()).setData(downLoadManager.getDownLoadData());
				}
			}
		}
		
	}
	
	/**
	 * 删除歌曲，重置播放列表
	 * */
	private void deleteForResetPlayerList(int songId,int flag,String parameter){
		final int state=mediaPlayerManager.getPlayerState();
		if(state==MediaPlayerManager.STATE_NULL||state==MediaPlayerManager.STATE_OVER){
			return;
		}
		if(mediaPlayerManager.getPlayerFlag()==MediaPlayerManager.PLAYERFLAG_WEB){
			return;
		}
		String t_parameter=mediaPlayerManager.getParameter();
		if(t_parameter==null) t_parameter="";
		if(flag==MediaPlayerManager.PLAYERFLAG_ALL||(mediaPlayerManager.getPlayerFlag()==flag&&parameter.equals(t_parameter))){
			//删除'播放列表'，就播放全部歌曲
			if(songId==-1){
				mediaPlayerManager.delete(-1);
				return;
			}else{
				//如果是当前播放歌曲，就要切换下一首
				if(songId==mediaPlayerManager.getSongId()){
					mediaPlayerManager.delete(songId);
				}
			}
			mediaPlayerManager.resetPlayerList();
		}
	}
	
	/**
	 * 正在下载-开始下载/暂停下载，删除
	 * */
	private DownLoadingListAdapter.ItemListener downLoadingListItemListener=new DownLoadingListAdapter.ItemListener() {
		
		@Override
		public void onDelete(String url) {
			downLoadManager.delete(url);
		}

		@Override
		public void onPause(String url,int state) {
			if(state==DownLoadManager.STATE_PAUSE||state==DownLoadManager.STATE_ERROR||state==DownLoadManager.STATE_FAILED){
				downLoadManager.start(url);
			}else if(state==DownLoadManager.STATE_DOWNLOADING||state==DownLoadManager.STATE_CONNECTION||state==DownLoadManager.STATE_WAIT){
				downLoadManager.pause(url);
			}
		}
	};
	
	/**
	 * 下载完成-删除
	 * */
	private DownLoadListAdapter.ItemListener downLoadListItemListener=new DownLoadListAdapter.ItemListener(){
		@Override
		public void onDelete(int id, String path, int position) {
			createDeleteSongDialog(id, path, position,false);
		}
	};
	
	//本地音乐和下载管理的二三级布局-替换的ListView ItemClick事件
	private OnItemClickListener list_change_content_listener=new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(pageNumber==2){//歌手种类列表
				jumpPage(2, 22,view.getTag());
			}else if(pageNumber==3){//专辑种类列表
				jumpPage(2, 33,view.getTag());
			}else if(pageNumber==4){//文件夹种类列表
				jumpPage(2, 44,view.getTag());
			}else if(pageNumber==5){//播放种类列表
				ibtn_list_content_do_icon.setVisibility(View.GONE);
				jumpPage(2, 55,view.getTag());
			}else if(pageNumber==8){//正在下载列表
				DownLoadingListAdapter.ViewHolder viewHolder=(DownLoadingListAdapter.ViewHolder)view.getTag();
				int state=Integer.valueOf(viewHolder.tv_download_list_item_top.getTag().toString());
				String url=viewHolder.tv_download_list_item_number.getTag().toString();
				downLoadingListItemListener.onPause(url, state);
			}else if(pageNumber==9){//下载完成列表
				if(mediaPlayerManager.getPlayerFlag()==MediaPlayerManager.PLAYERFLAG_WEB){
					int[] playerInfo=new int[]{-1,-1};
					((SongItemWebAdapter)lv_list_web.getAdapter()).setPlayerInfo(playerInfo);
				}
				int songId=Integer.valueOf(((DownLoadListAdapter.ViewHolder)view.getTag()).ibtn_download_list_item_menu.getTag().toString());
				if(songId==mediaPlayerManager.getSongId()){
					PlayerOrPause(view);
				}else {
					ibtn_player_control.setBackgroundResource(R.drawable.player_btn_mini_pause);
					mediaPlayerManager.player(songId,MediaPlayerManager.PLAYERFLAG_DOWNLOAD, null);
					int[] playerInfo=new int[]{songId,mediaPlayerManager.getPlayerState()};
					((DownLoadListAdapter)lv_list_change_content.getAdapter()).setPlayerInfo(playerInfo);
				}
			}else if(pageNumber==1){//全部歌曲列表
				playerMusicByItem(view,MediaPlayerManager.PLAYERFLAG_ALL,null);
			}else if(pageNumber==6){//我最爱听列表
				playerMusicByItem(view,MediaPlayerManager.PLAYERFLAG_LIKE,null);
			}else if(pageNumber==7){//最近播放列表
				playerMusicByItem(view,MediaPlayerManager.PLAYERFLAG_LATELY,null);			
			}else if(pageNumber==22){//某歌手歌曲列表
				playerMusicByItem(view,MediaPlayerManager.PLAYERFLAG_ARTIST,condition);
			}else if(pageNumber==33){//某专辑歌曲列表
				playerMusicByItem(view,MediaPlayerManager.PLAYERFLAG_ALBUM,condition);
			}else if(pageNumber==44){//某文件夹歌曲列表
				playerMusicByItem(view,MediaPlayerManager.PLAYERFLAG_FOLDER,condition);
			}else if(pageNumber==55){//某播放列表歌曲列表
				playerMusicByItem(view,MediaPlayerManager.PLAYERFLAG_PLAYERLIST,condition);
			}
		}
	};
	
	private void playerMusicByItem(View view,int flag,String condition){
		if(mediaPlayerManager.getPlayerFlag()==MediaPlayerManager.PLAYERFLAG_WEB){
			int[] playerInfo=new int[]{-1,-1};
			((SongItemWebAdapter)lv_list_web.getAdapter()).setPlayerInfo(playerInfo);
		}
		int songId=Integer.valueOf(((SongItemAdapter.ViewHolder)view.getTag()).tv_song_list_item_bottom.getTag().toString());
		if(songId==mediaPlayerManager.getSongId()){
			PlayerOrPause(view);
		}else {
			ibtn_player_control.setBackgroundResource(R.drawable.player_btn_mini_pause);
			mediaPlayerManager.player(songId,flag, condition);
			int[] playerInfo=new int[]{songId,mediaPlayerManager.getPlayerState()};
			((SongItemAdapter)lv_list_change_content.getAdapter()).setPlayerInfo(playerInfo);
		}
	}
	
	//本地音乐和下载管理的二三级布局-替换的ListView ItemLoogClick事件
	private OnItemLongClickListener list_change_content_looglistener=new OnItemLongClickListener() {
		
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			if(pageNumber==5){//播放列表时，弹出菜单
				if(position!=0){
					doPlayListLoogItemDialog(view);
					return true;
				}
			}else{
				if(!(pageNumber==2||pageNumber==3||pageNumber==4||pageNumber==5||pageNumber==8||pageNumber==9)){
					final SongItemAdapter.ViewHolder viewHolder=(SongItemAdapter.ViewHolder)view.getTag();
					final String path=viewHolder.tv_song_list_item_top.getTag().toString();//歌曲路径
					final int sid=Integer.parseInt(viewHolder.tv_song_list_item_bottom.getTag().toString());//歌曲id
					final String text=viewHolder.tv_song_list_item_top.getText().toString();
					
					doListSongLoogItemDialog(sid,text,path,position);
				}
			}
			return false;
		}
		
	};
	
	//歌曲列表项事件
	private SongItemAdapter.ItemListener songItemListener=new SongItemAdapter.ItemListener() {
		@Override
		public void onLikeClick(int id, View view, int position) {
			//排除我最爱听歌曲列表
			if(pageNumber==6){
				songDao.updateByLike(id, 0);
				//更新歌曲列表
				((SongItemAdapter)lv_list_change_content.getAdapter()).deleteItem(position);
				btn_list_random_music2.setText("(共"+lv_list_change_content.getCount()+"首)随机播放");
				btn_list_random_music2.setTag(lv_list_change_content.getCount());
				deleteForResetPlayerList(id,MediaPlayerManager.PLAYERFLAG_LIKE,"");
				return;
			}
			if(view.getTag().equals("1")){
				view.setTag("0");
				view.setBackgroundResource(R.drawable.music_dislike);
				songDao.updateByLike(id, 0);
			}else{
				view.setTag("1");
				view.setBackgroundResource(R.drawable.music_like);
				songDao.updateByLike(id, 1);
			}
		}
		@Override
		public void onMenuClick(int id, String text, String path, int position) {
			doListSongLoogItemDialog(id,text,path,position);
		}
	};
	
	/**
	 * 创建歌曲列表菜单对话框
	 * */
	private void doListSongLoogItemDialog(final int sid,String text,final String path,final int parentposition){
		String delete_title="移除歌曲";
		if(pageNumber==9){
			delete_title="清除任务";
		}
		
		String[] menustring=new String[]{"添加到列表","设为铃声",delete_title,"查看详情"};
		ListView menulist=new ListView(ListMainActivity.this);
		menulist.setCacheColorHint(Color.TRANSPARENT);
		menulist.setDividerHeight(1);
		menulist.setAdapter(new ArrayAdapter<String>(ListMainActivity.this, R.layout.music_dialog_menu_item, R.id.text1, menustring));
		menulist.setLayoutParams(new LayoutParams(Common.getScreen(ListMainActivity.this)[0]/2, LayoutParams.WRAP_CONTENT));
		
		final CarMDialog carmdialog=new CarMDialog.Builder(ListMainActivity.this).setTitle(text).setView(menulist).create();
		carmdialog.show();
		
		menulist.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				carmdialog.cancel();
				carmdialog.dismiss();
				if(position==0){//添加到列表
					createPlayerListDialog(sid);
				}else if(position==1){//设为铃声
					createRingDialog(path);
				}else if(position==2){//移除歌曲
					createDeleteSongDialog(sid,path,parentposition,true);
				}else if(position==3){//查看详情
					createSongDetailDialog(sid);
				}
			}
		});
	}
	
	/**
	 * 歌曲详细对话框
	 * */
	private void createSongDetailDialog(int id){
		Song song=songDao.searchById(id);
		File file=new File(song.getFilePath());
		//歌曲不存在
		if(!file.exists()){
			toast=Common.showMessage(toast, ListMainActivity.this, "歌曲已经不存在，请删除歌曲！");
			return;
		}
		if(song.getSize()==-1){
			song.setSize((int)file.length());
			songDao.updateBySize(id, song.getSize());
		}
		//表示当时扫描时，是在媒体库中不存在的歌曲
		int duration=song.getDurationTime();
		if(duration==-1){
			//获取播放时长
			MediaPlayer t_MediaPlayer=new MediaPlayer();	
			try {
				t_MediaPlayer.setDataSource(song.getFilePath());
				t_MediaPlayer.prepare();
				duration=t_MediaPlayer.getDuration();
			} catch (IllegalArgumentException e) {
			} catch (IllegalStateException e) {
			} catch (IOException e) {
			}finally{
				t_MediaPlayer.release();
				t_MediaPlayer=null;
			}
			if(duration!=-1){
				song.setDurationTime(duration);
				//更新数据库
				songDao.updateByDuration(id,duration);
			}
		}
		
		View view=inflater.inflate(R.layout.music_song_detail, null);
		view.setLayoutParams(new LayoutParams(Common.getScreen(ListMainActivity.this)[0]/2, LayoutParams.WRAP_CONTENT));
		
		((TextView)view.findViewById(R.id.tv_song_title)).setText(song.getName());
		((TextView)view.findViewById(R.id.tv_song_album)).setText(song.getAlbum().getName());
		((TextView)view.findViewById(R.id.tv_song_artist)).setText(song.getArtist().getName());
		((TextView)view.findViewById(R.id.tv_song_duration)).setText(Common.formatSecondTime(duration));
		((TextView)view.findViewById(R.id.tv_song_filepath)).setText(song.getFilePath());
		((TextView)view.findViewById(R.id.tv_song_format)).setText(Common.getSuffix(song.getDisplayName()));
		((TextView)view.findViewById(R.id.tv_song_size)).setText(Common.formatByteToMB(song.getSize())+"MB");
		
		new CarMDialog.Builder(ListMainActivity.this).setTitle("歌曲详细信息").setNeutralButton("确定", null).setView(view).create().show();
	}
	
	/**
	 * 添加到列表对话框
	 * */
	private void createPlayerListDialog(final int id){
		List<String[]> pList=playerListDao.searchAll();
		
		RadioGroup rg_pl=new RadioGroup(ListMainActivity.this);
		rg_pl.setLayoutParams(params);
		final List<RadioButton> rbtns=new ArrayList<RadioButton>();
		
		for (int i = 0; i < pList.size();i++) {
			String[] str_temp=pList.get(i);
			RadioButton rbtn_temp=new RadioButton(ListMainActivity.this);
			rbtn_temp.setText(str_temp[1]);
			rbtn_temp.setTag(str_temp[0]);
			rg_pl.addView(rbtn_temp, params);
			rbtns.add(rbtn_temp);
		}
		
		new CarMDialog.Builder(ListMainActivity.this).setTitle("播放列表").setView(rg_pl).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				dialog.dismiss();
				int selectedIndex=-1;
				for (int i = 0; i < rbtns.size(); i++) {
					if(rbtns.get(i).isChecked()){
						selectedIndex=i;
						break;
					}
				}
				if(selectedIndex!=-1){
					songDao.updateByPlayerList(id,Integer.valueOf(rbtns.get(selectedIndex).getTag().toString()));
					toast=Common.showMessage(toast, ListMainActivity.this, "添加成功");
				}else{
					toast=Common.showMessage(toast, ListMainActivity.this, "请选择要添加到的播放列表");
				}
			}
		}).setNegativeButton("取消", null).create().show();
	}
	
	/**
	 * 移除歌曲对话框:flag是否是本地歌曲列表删除
	 * */
	private void createDeleteSongDialog(final int sid,final String filepath,final int position,final boolean flag){
		String t_title="移除歌曲";
		if(pageNumber==9){
			t_title="清除任务";
		}
		final String title=t_title;
		final CheckBox cb_deletesong=new CheckBox(ListMainActivity.this);
		cb_deletesong.setLayoutParams(params);
		cb_deletesong.setText("同时删除本地文件");

		CarMDialog.Builder builder=new CarMDialog.Builder(ListMainActivity.this).setView(cb_deletesong)
		.setTitle(title).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(cb_deletesong.isChecked()){
					Common.deleteFile(ListMainActivity.this, filepath);
				}
				int rs=0;
				//从播放列表中移除
				if(!cb_deletesong.isChecked()&&pageNumber==55){
					rs=songDao.deleteByPlayerList(sid, Integer.valueOf(condition));
				}else{
					//没有选中并且是下载完成删除
					if(!cb_deletesong.isChecked()&&!flag){
						rs=songDao.updateByDownLoadState(sid);
					}else{
						rs=songDao.delete(sid);
					}
				}
				if(rs>0){
					toast=Common.showMessage(toast, ListMainActivity.this, title+"成功");
					dialog.cancel();
					dialog.dismiss();
					
					//更新歌曲列表
					if(flag){
						((SongItemAdapter)lv_list_change_content.getAdapter()).deleteItem(position);
					}else{
						((DownLoadListAdapter)lv_list_change_content.getAdapter()).deleteItem(position);
					}
					if(pageNumber==1){
						deleteForResetPlayerList(sid,MediaPlayerManager.PLAYERFLAG_ALL,"");
					}else if(pageNumber==6){
						deleteForResetPlayerList(sid,MediaPlayerManager.PLAYERFLAG_LIKE,"");
					}else if(pageNumber==7){
						deleteForResetPlayerList(sid,MediaPlayerManager.PLAYERFLAG_LATELY,"");
					}else if(pageNumber==9){
						deleteForResetPlayerList(sid,MediaPlayerManager.PLAYERFLAG_DOWNLOAD,"");
					}else if(pageNumber==22){
						deleteForResetPlayerList(sid,MediaPlayerManager.PLAYERFLAG_ARTIST,condition);
					}else if(pageNumber==33){
						deleteForResetPlayerList(sid,MediaPlayerManager.PLAYERFLAG_ALBUM,condition);
					}else if(pageNumber==44){
						deleteForResetPlayerList(sid,MediaPlayerManager.PLAYERFLAG_FOLDER,condition);
					}else if(pageNumber==55){
						deleteForResetPlayerList(sid,MediaPlayerManager.PLAYERFLAG_PLAYERLIST,condition);
					}
					btn_list_random_music2.setText("(共"+lv_list_change_content.getCount()+"首)随机播放");
					btn_list_random_music2.setTag(lv_list_change_content.getCount());
				}else{
					toast=Common.showMessage(toast, ListMainActivity.this, title+"失败");
				}
			}
		}).setNegativeButton("取消", null);

		builder.create().show();
	}
	
	/**
	 * 设置铃声对话框
	 * */
	private void createRingDialog(final String filepath){
		RadioGroup rg_ring=new RadioGroup(ListMainActivity.this);
		rg_ring.setLayoutParams(params);
		final RadioButton rbtn_ringtones=new RadioButton(ListMainActivity.this);
		rbtn_ringtones.setText("来电铃声");
		rg_ring.addView(rbtn_ringtones, params);
		final RadioButton rbtn_alarms=new RadioButton(ListMainActivity.this);
		rbtn_alarms.setText("闹铃铃声");
		rg_ring.addView(rbtn_alarms, params);
		final RadioButton rbtn_notifications=new RadioButton(ListMainActivity.this);
		rbtn_notifications.setText("通知铃声");
		rg_ring.addView(rbtn_notifications, params);
		final RadioButton rbtn_all=new RadioButton(ListMainActivity.this);
		rbtn_all.setText("全部铃声");
		rg_ring.addView(rbtn_all, params);
		
		new CarMDialog.Builder(ListMainActivity.this).setTitle("设置铃声").setView(rg_ring).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				ContentValues cv=new ContentValues();
				int type=-1;
				if(rbtn_ringtones.isChecked()){
					type=RingtoneManager.TYPE_RINGTONE;
					cv.put(MediaStore.Audio.Media.IS_RINGTONE, true);
				}else if(rbtn_alarms.isChecked()){
					type=RingtoneManager.TYPE_ALARM;
					cv.put(MediaStore.Audio.Media.IS_ALARM, true);
				}else if(rbtn_notifications.isChecked()){
					type=RingtoneManager.TYPE_NOTIFICATION;
					cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
				}else if(rbtn_all.isChecked()){
					type=RingtoneManager.TYPE_ALL;
					cv.put(MediaStore.Audio.Media.IS_RINGTONE, true);
					cv.put(MediaStore.Audio.Media.IS_ALARM, true);
					cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
				}
				if(type==-1){
					toast=Common.showMessage(toast, ListMainActivity.this, "请选择铃声类型");
				}else{
					Uri uri = MediaStore.Audio.Media.getContentUriForPath(filepath);
					Uri ringtoneUri =null;
					Cursor cursor = ListMainActivity.this.getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[] { filepath },null);
					//查询媒体库中存在的
					if (cursor.getCount() > 0&&cursor.moveToFirst()) {
						String _id = cursor.getString(0);
						  //更新媒体库
						  getContentResolver().update(uri, cv, MediaStore.MediaColumns.DATA + "=?",new String[] { filepath }); 
						  ringtoneUri= Uri.withAppendedPath(uri, _id);
					}else{//不存在就添加
						 cv.put(MediaStore.MediaColumns.DATA,filepath);
						 ringtoneUri= ListMainActivity.this.getContentResolver().insert(uri, cv);
					}
					try {
						RingtoneManager.setActualDefaultRingtoneUri(ListMainActivity.this, type, ringtoneUri);
						toast=Common.showMessage(toast, ListMainActivity.this, "铃声设置成功");
					} catch (Exception e) {
						toast=Common.showMessage(toast, ListMainActivity.this, "铃声设置失败");
					}
					dialog.cancel();
					dialog.dismiss();
				}
			}
		}).setNegativeButton("取消", null).show();
	}
	
	/**
	 * 创建播放列表菜单对话框
	 * */
	private void doPlayListLoogItemDialog(View view){
		final TextView textView=((ListItemAdapter.ViewHolder)view.getTag()).textView;
		final String text=textView.getText().toString();//播放列表名称
		final int plid=Integer.parseInt(textView.getTag().toString());//播放列表id
		
		String[] menustring=new String[]{"重命名","删除"};
		ListView menulist=new ListView(ListMainActivity.this);
		menulist.setCacheColorHint(Color.TRANSPARENT);
		menulist.setDividerHeight(1);
		menulist.setAdapter(new ArrayAdapter<String>(ListMainActivity.this, R.layout.music_dialog_menu_item, R.id.text1, menustring));
		menulist.setLayoutParams(new LayoutParams(Common.getScreen(ListMainActivity.this)[0]/2, LayoutParams.WRAP_CONTENT));
		
		final CarMDialog carmdialog=new CarMDialog.Builder(ListMainActivity.this).setTitle(text).setView(menulist).create();
		carmdialog.show();
		
		menulist.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position==0){//重命名
					carmdialog.cancel();
					carmdialog.dismiss();
					doPlayList(1, plid,text);
				}else if(position==1){//删除
					carmdialog.cancel();
					carmdialog.dismiss();
					new CarMDialog.Builder(ListMainActivity.this).setTitle("删除提示").setMessage("是否要删除这个播放列表？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if(playerListDao.delete(plid)>0){
								toast=Common.showMessage(toast, ListMainActivity.this, "删除成功！");
								lv_list_change_content.setAdapter(new ListItemAdapter(ListMainActivity.this, 
										playerListDao.searchAll(),R.drawable.music_local_custom));
								
								//更新正在播放列表
								deleteForResetPlayerList(-1, MediaPlayerManager.PLAYERFLAG_PLAYERLIST, String.valueOf(plid));
							}else{
								toast=Common.showMessage(toast, ListMainActivity.this, "删除失败！");
							}
							dialog.cancel();
							dialog.dismiss();
						}
					}).setNegativeButton("取消",null).create().show();
				}
			}
			
		});
		
	}
	
	/**
	 * 初始化导航栏
	 * */
	private void initTabItem() {
		for (int i = 0; i < vg_list_tab_item.length; i++) {
			vg_list_tab_item[i].setOnClickListener(tabClickListener);
			if (screenIndex == i) {
				vg_list_tab_item[0]
						.setBackgroundResource(R.drawable.music_list_top_press);
			}
			((ImageView) vg_list_tab_item[i]
					.findViewById(R.id.iv_list_item_icon))
					.setImageResource(list_item_icons[i]);
			((TextView) vg_list_tab_item[i]
					.findViewById(R.id.tv_list_item_text))
					.setText(list_item_items[i]);
		}
	}

	//主屏幕左右滑动事件
	private OnScrollToScreenListener scrollToScreenListener = new OnScrollToScreenListener() {

		public void operation(int currentScreen, int screenCount) {
			vg_list_tab_item[screenIndex].setBackgroundResource(0);
			screenIndex = currentScreen;
			vg_list_tab_item[screenIndex]
					.setBackgroundResource(R.drawable.music_list_top_press);
		}
	};

	//导航栏选项卡切换事件
	private OnClickListener tabClickListener = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.list_tab_item_music:
				if (screenIndex == 0) {
					return;
				}
				vg_list_tab_item[screenIndex].setBackgroundResource(0);
				screenIndex = 0;
				break;
			case R.id.list_tab_item_web:
				if (screenIndex == 1) {
					return;
				}
				vg_list_tab_item[screenIndex].setBackgroundResource(0);
				screenIndex = 1;
				break;
			case R.id.list_tab_item_download:
				if (screenIndex == 2) {
					return;
				}
				vg_list_tab_item[screenIndex].setBackgroundResource(0);
				screenIndex = 2;
				break;
			default:
				break;
			}
			vg_list_tab_item[screenIndex]
					.setBackgroundResource(R.drawable.music_list_top_press);
			fgv_list_main.setToScreen(screenIndex, true);
		}
	};
}
