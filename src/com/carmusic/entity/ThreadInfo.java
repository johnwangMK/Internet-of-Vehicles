package com.carmusic.entity;


/**
 * 多线程下载-子线程的信息
 * */
public class ThreadInfo {
	
	private int id;//id
	private int downLoadInfoId;//DOWNLOADINFO id
	private int startPosition;//开始下载的大小
	private int endPosition;//结束下载的大小
	private int completeSize;//已下载的大小
	
	public ThreadInfo(){
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDownLoadInfoId() {
		return downLoadInfoId;
	}

	public void setDownLoadInfoId(int downLoadInfoId) {
		this.downLoadInfoId = downLoadInfoId;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public int getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}

	public int getCompleteSize() {
		return completeSize;
	}

	public void setCompleteSize(int completeSize) {
		this.completeSize = completeSize;
	}
	
}
