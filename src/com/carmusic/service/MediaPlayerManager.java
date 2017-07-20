package com.carmusic.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MediaPlayerManager {
	
	private MediaPlayerService mMediaPlayerService;
	private ContextWrapper mContextWrapper;
	
	//播放模式
	/**
	 * 顺序播放 0
	 * */
	public static final int MODE_CIRCLELIST=0;
	/**
	 * 随机播放 1
	 * */
	public static final int MODE_RANDOM=1;
	/**
	 * 单曲循环 2
	 * */
	public static final int MODE_CIRCLEONE=2;
	/**
	 * 列表循环 3
	 * */
	public static final int MODE_SEQUENCE=3;
	
	//播放状态
	public static final int STATE_NULL=0;//空闲
	public static final int STATE_BUFFER=1;//缓冲
	public static final int STATE_PAUSE=2;//暂停
	public static final int STATE_PLAYER=3;//播放
	public static final int STATE_PREPARE=4;//准备
	public static final int STATE_OVER=5;//播放结束
	public static final int STATE_STOP=6;//停止
	
	//播放Flag
	public static final int PLAYERFLAG_WEB=0;//网络
	public static final int PLAYERFLAG_ALL=1;//全部
	public static final int PLAYERFLAG_ARTIST=2;//歌手
	public static final int PLAYERFLAG_ALBUM=3;//专辑
	public static final int PLAYERFLAG_FOLDER=4;//文件夹
	public static final int PLAYERFLAG_PLAYERLIST=5;//播放列表
	public static final int PLAYERFLAG_LIKE=6;//我最爱听
	public static final int PLAYERFLAG_LATELY=7;//最近播放
	public static final int PLAYERFLAG_DOWNLOAD=9;//下载完成
	
	//播放歌曲-广播动作类型
	public static final String BROADCASTRECEVIER_ACTON="com.carmusic.player.brocast";

	public static final int FLAG_CHANGED=0;//更新前台
	public static final int FLAG_PREPARE=1;//准备状态
	public static final int FLAG_INIT=2;//初始化数据
	public static final int FLAG_LIST=3;//自动播放时，更新前台列表状态
	public static final int FLAG_BUFFERING=4;//网络音乐-缓冲数据
	
	public static final int FLAG_AUTOSHUTDOWN=5;//定时关机
	
	//MediaPlayerService action
	public static final String SERVICE_ACTION="com.carmusic.service.meidaplayer";
		
	//MediaPlayerService onStart flag
	public static final int SERVICE_RESET_PLAYLIST=0;//更新播放列表
	public static final int SERVICE_MUSIC_PAUSE=1;//暂停
	public static final int SERVICE_MUSIC_PLAYERORPAUSE=2;//播放/暂停
	public static final int SERVICE_MUSIC_PREV=3;//上一首
	public static final int SERVICE_MUSIC_NEXT=4;//下一首
	public static final int SERVICE_MUSIC_STOP=5;//停止播放
	public static final int SERVICE_MUSIC_INIT=6;//添加widget时，初始化widget信息
	
	private ServiceConnectionListener mConnectionListener;
	
	public MediaPlayerManager(ContextWrapper cw) {
		mContextWrapper = cw;
	}
	
	public interface ServiceConnectionListener{
    	public void onServiceConnected();
    	public void onServiceDisconnected();
	}
	
	public void setConnectionListener(ServiceConnectionListener listener){
    	mConnectionListener = listener;
    }
	
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mMediaPlayerService = ((MediaPlayerService.MediaPlayerBinder) service)
					.getService();
			if(mConnectionListener!=null){
				mConnectionListener.onServiceConnected();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			mMediaPlayerService = null;
			if(mConnectionListener!=null){
				mConnectionListener.onServiceDisconnected();
			}
		}
	};
	
	/**
	 * 初始化歌曲信息-播放界面进入时
	 * */
	public void initPlayerMain_SongInfo(){
		if(mMediaPlayerService!=null){
			mMediaPlayerService.initPlayerMain_SongInfo();
		}
	}
	
	/**
	 * 开始服务并绑定服务
	 * */
	public void startAndBindService(){ 
        mContextWrapper.startService(new Intent(SERVICE_ACTION)); 
        mContextWrapper.bindService(new Intent(SERVICE_ACTION), mServiceConnection, Context.BIND_AUTO_CREATE);
    }
	
	/**
	 * 停止播放
	 * */
	public void stop(){
        if(mMediaPlayerService != null){
        	mMediaPlayerService.stop();
        }
    }
	
	/**
	 * 取消绑定
	 * */
	public void unbindService(){
        if(mMediaPlayerService != null){
            mContextWrapper.unbindService(mServiceConnection);
        }
    }
	
	/**
	 * 设置播放模式
	 * */
	public void setPlayerMode(int playerMode){
		if(mMediaPlayerService!=null){
			mMediaPlayerService.setPlayerMode(playerMode);
		}
	}
	
	/**
	 * 获取专辑图片
	 * */
	public String getAlbumPic(){
		if(mMediaPlayerService!=null){
			mMediaPlayerService.getAlbumPic();
		}
		return null;
	}
	
	/**
	 * 获取当前播放歌曲的Id
	 * */
	public int getSongId(){
		if(mMediaPlayerService!=null){
			return mMediaPlayerService.getSongId();
		}
		return -1;
	}
	
	/**
	 * 获取当前播放的Flag
	 * */
	public int getPlayerFlag(){
		if(mMediaPlayerService!=null){
			return mMediaPlayerService.getPlayerFlag();
		}
		return -1;
	}
	
	/**
	 * 获取当前播放状态
	 * */
	public int getPlayerState(){
		if(mMediaPlayerService!=null){
			return mMediaPlayerService.getPlayerState();
		}
		return -1;
	}
	
	/**
	 * 指定位置播放
	 * */
	public void seekTo(int msec){
		if(mMediaPlayerService!=null){
			mMediaPlayerService.seekTo(msec);
		}
	}
	
	/**
	 * 获取当前播放歌曲标题
	 * */
	public String getTitle(){
		if(mMediaPlayerService!=null){
			return mMediaPlayerService.getTitle();
		}
		return null;
	}
	
	/**
	 * 获取当前播放歌曲的进度
	 * */
	public int getPlayerProgress(){
		if(mMediaPlayerService!=null){
			return mMediaPlayerService.getPlayerProgress();
		}
		return -1;
	}
	
	/**
	 * 获取当前播放歌曲的时长
	 * */
	public int getPlayerDuration(){
		if(mMediaPlayerService!=null){
			return mMediaPlayerService.getPlayerDuration();
		}
		return -1;
	}
	
	/**
	 * 播放下一首
	 * */
	public void nextPlayer(){
		if(mMediaPlayerService!=null){
			mMediaPlayerService.nextPlayer();
		}
	}
	
	/**
	 * 播放上一首
	 * */
	public void previousPlayer(){
		if(mMediaPlayerService!=null){
			mMediaPlayerService.previousPlayer();
		}
	}
	
	/**
	 * 播放/暂停
	 * */
	public void pauseOrPlayer(){
		if(mMediaPlayerService!=null){
			mMediaPlayerService.pauseOrPlayer();
		}
	}

	/**
	 * 根据指定条件播放
	 * */
	public void player(int id,int playerFlag,String parameter){
		if(mMediaPlayerService!=null){
			mMediaPlayerService.player(id,playerFlag,parameter);
		}
	}
	
	/**
	 * 重置播放歌曲列表
	 * */
	public void resetPlayerList(){
		if(mMediaPlayerService!=null){
			mMediaPlayerService.resetPlayerList();
		}
	}
	
	/**
	 * 获取当前播放模式
	 * */
	public int getPlayerMode(){
		if(mMediaPlayerService!=null){
			return mMediaPlayerService.getPlayerMode();
		}
		return -1;
	}
	
	/**
	 * 初始化歌曲信息-扫描之后
	 * */
	public void initScanner_SongInfo(){
		if(mMediaPlayerService!=null){
			mMediaPlayerService.initScanner_SongInfo();
		}
	}
	
	/**
	/**
	 * 获取当前查询列表条件
	 * */
	public String getParameter(){
		if(mMediaPlayerService!=null){
			return mMediaPlayerService.getParameter();
		}
		return null;
	}
	
	
	/**
	 * 获取最近播放的
	 * */
	public String getLatelyStr(){
		if(mMediaPlayerService!=null){
			return mMediaPlayerService.getLatelyStr();
		}
		return null;
	}
	
	/**
	 * 随机播放
	 * */
	public void randomPlayer(int flag,String parameter){
		if(mMediaPlayerService!=null){
			mMediaPlayerService.randomPlayer(flag,parameter);
		}
	}
	/**
	 * 删除歌曲时
	 * */
	public void delete(int songId){
		if(mMediaPlayerService!=null){
			mMediaPlayerService.delete(songId);
		}
	}
}
