package com.carmusic.entity;

import java.util.List;

/**
 * 线程下载信息
 * */
public class DownLoadInfo {

	private int id;// id
	private List<ThreadInfo> threadInfos;// 多线程信息
	private String url;// 下载路径
	private int fileSize;// 文件大小
	private String name;//歌曲名称
	private String artist;//歌手
	private String album;//专辑
	private String displayName;//文件名
	private String mimeType;//mime
	private int durationTime;//播放时长
	private int completeSize;//总下载进度
	private String filePath;//保存文件的路径
	
	private int state;// 下载状态
	private int threadCount;//运行时活动线程的数量
	
	public DownLoadInfo() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<ThreadInfo> getThreadInfos() {
		return threadInfos;
	}

	public void setThreadInfos(List<ThreadInfo> threadInfos) {
		this.threadInfos = threadInfos;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public int getDurationTime() {
		return durationTime;
	}

	public void setDurationTime(int durationTime) {
		this.durationTime = durationTime;
	}

	public int getCompleteSize() {
		return completeSize;
	}

	public void setCompleteSize(int completeSize) {
		this.completeSize = completeSize;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
}
