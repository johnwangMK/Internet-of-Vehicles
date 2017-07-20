package com.carmusic.entity;


public class Sentence {

	private long fromTime;// 开始毫秒数
	private long toTime;// 结束毫秒数
	private String content;// 此句内容

	public Sentence(String content, long fromTime, long toTime) {
		this.content = content;
		this.fromTime = fromTime;
		this.toTime = toTime;
	}

	public Sentence(String content, long fromTime) {
		this(content, fromTime, 0);
	}

	public Sentence(String content) {
		this(content, 0, 0);
	}

	public long getFromTime() {
		return fromTime;
	}

	public void setFromTime(long fromTime) {
		this.fromTime = fromTime;
	}

	public long getToTime() {
		return toTime;
	}

	public void setToTime(long toTime) {
		this.toTime = toTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	// 检查某个时间是否包含在某句中间
	public boolean isInTime(long time) {
		return time >= fromTime && time <= toTime;
	}

	// 得到这个句子的时间长度,毫秒为单位
	public long getDuring() {
		return toTime - fromTime;
	}
}
