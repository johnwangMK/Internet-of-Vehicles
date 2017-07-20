package com.carmusic.entity;

/**
 * 扫描数据
 * */
public class ScanData {
	private String filePath;
	private boolean isChecked;

	public ScanData() {
	}

	public ScanData(String filePath, boolean isChecked) {
		this.filePath = filePath;
		this.isChecked = isChecked;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
}
