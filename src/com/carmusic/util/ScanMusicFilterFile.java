package com.carmusic.util;

import java.io.File;
import java.io.FileFilter;

/**
 * 文件过滤器
 * */
public class ScanMusicFilterFile implements FileFilter{

	public boolean accept(File pathname) {
		if(pathname.isDirectory()&&pathname.canRead()){
			return pathname.list().length>0;
		}
		return false;
	}
}
