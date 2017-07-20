package com.carmusic.service;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.carmusic.entity.DownLoadInfo;
import com.carmusic.entity.Song;

public class DownLoadManager {
	// 下载状态
	public static final int STATE_DOWNLOADING = 0;// 下载中
	public static final int STATE_PAUSE = 1;// 暂停
	public static final int STATE_WAIT = 2;// 等待
	public static final int STATE_DELETE = 3;// 删除
	public static final int STATE_FAILED = 4;//连接失败
	public static final int STATE_PAUSEING = 5;// 正在暂停
	public static final int STATE_CONNECTION = 6;//连接中
	public static final int STATE_ERROR = 7;//错误
	
	//下载任务-广播动作类型
	public static final String BROADCASTRECEVIER_ACTON="com.carmusic.download.brocast";
	
	public static final int FLAG_CHANGED=0;//更新前台
	public static final int FLAG_COMPLETED=1;//下载完成
	public static final int FLAG_FAILED=2;//失败
	public static final int FLAG_WAIT=3;//等待下载
	public static final int FLAG_TIMEOUT=4;//下载超时
	public static final int FLAG_ERROR=5;//发生错误
	public static final int FLAG_COMMON=6;//删除
	
	//DownLoadService action
	public static final String SERVICE_ACTION="com.carmusic.service.download";
	
	//MediaPlayerService onStart flag
	public static final int SERVICE_DOWNLOAD_STOP=0;//停止下载
	
	private DownLoadService mDownLoadService;
	private ContextWrapper mContextWrapper;
	
	public DownLoadManager(ContextWrapper cw) {
		mContextWrapper = cw;
	}
	
	/**
	 * 启动某个下载任务
	 * */
	public void start(String url){
		if(mDownLoadService!=null){
			mDownLoadService.start(url);
		}
	}
	
	/**
	 * 添加某个下载任务
	 * */
	public void add(Song song){
		if(mDownLoadService!=null){
			mDownLoadService.add(song);
		}
	}
	
	/**
	 * 删除某个下载任务
	 * */
	public void delete(String url){
		if(mDownLoadService!=null){
			mDownLoadService.delete(url);
		}
	}
	
	/**
	 * 暂停某个下载任务
	 * */
	public void pause(String url){
		if(mDownLoadService!=null){
			mDownLoadService.pause(url);
		}
	}
	
	/**
	 * 获取下载数据
	 * */
	public List<DownLoadInfo> getDownLoadData(){
		if(mDownLoadService!=null){
			return mDownLoadService.getDownLoadData();
		}
		return null;
	}

	private ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mDownLoadService = ((DownLoadService.DownLoadBinder) service)
					.getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			mDownLoadService = null;
		}
	};
	
	/**
	 * 停止下载
	 * */
	public void stop(){
		if(mDownLoadService != null){
			mDownLoadService.stop();
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
	 * 取消绑定
	 * */
	public void unbindService(){
        if(mDownLoadService != null){
            mContextWrapper.unbindService(mServiceConnection);
        }
    }
}