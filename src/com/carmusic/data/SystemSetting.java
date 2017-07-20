package com.carmusic.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SystemSetting {
	/**
	 * 系统设置的保存的文件名
	 * */
	public static final String PREFERENCE_NAME = "com.carmusic.system";
	/**
	 * SD卡下载歌曲目录
	 * */
	public static final String DOWNLOAD_MUSIC_DIRECTORY="/CarMusic/download_music/";
	/**
	 * SD卡下载歌词目录
	 * */
	public static final String DOWNLOAD_LYRIC_DIRECTORY="/CarMusic/download_lyric/";
	/**
	 * SD卡下载专辑图片目录
	 * */
	public static final String DOWNLOAD_ALBUM_DIRECTORY="/CarMusic/download_album/";
	/**
	 * SD卡下载歌手图片目录
	 * */
	public static final String DOWNLOAD_ARTIST_DIRECTORY="/CarMusic/download_artist/";
	
	public static final String KEY_PLAYER_ID="player_id";//歌曲Id
	public static final String KEY_PLAYER_CURRENTDURATION="player_currentduration";//已经播放时长
	public static final String KEY_PLAYER_MODE="player_mode";//播放模式
	public static final String KEY_PLAYER_FLAG="player_flag";//播放列表Flag
	public static final String KEY_PLAYER_PARAMETER="player_parameter";//播放列表查询参数
	
	public static final String KEY_PLAYER_LATELY="player_lately";//最近播放(保存本地的，用','分割)
	
	public static final String KEY_ISSTARTUP="isStartup";//是否是刚启动
	
	public static final String KEY_ISSCANNERTIP="isScannerTip";//是否显示要扫描提示
	
	private SharedPreferences settingPreference;
	
	public SystemSetting(Context context,boolean isWrite) {
		settingPreference = context.getSharedPreferences(PREFERENCE_NAME,
				isWrite?Context.MODE_WORLD_READABLE:Context.MODE_WORLD_WRITEABLE);
	}
	
	/**
	 * 获取数据
	 * */
	public String getValue(String key){
		return settingPreference.getString(key, null);
	}

	/**
	 * 保存播放信息[0:歌曲Id 1:已经播放时长 2:播放模式3:播放列表Flag4:播放列表查询参数 5:最近播放的]
	 * */
	public void setPlayerInfo(String[] playerInfos){
		Editor it = settingPreference.edit();
		it.putString(KEY_PLAYER_ID, playerInfos[0]);
		it.putString(KEY_PLAYER_CURRENTDURATION, playerInfos[1]);
		it.putString(KEY_PLAYER_MODE, playerInfos[2]);
		it.putString(KEY_PLAYER_FLAG, playerInfos[3]);
		it.putString(KEY_PLAYER_PARAMETER, playerInfos[4]);
		it.putString(KEY_PLAYER_LATELY, playerInfos[5]);
		it.commit();
	}
	
	/**
	 * 设置键值
	 * */
	public void setValue(String key,String value){
		Editor it = settingPreference.edit();
		it.putString(key, value);
		it.commit();
	}
}
