package com.carmusic.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.carmusic.entity.DownLoadInfo;
import com.carmusic.service.DownLoadManager;

public class DownLoadInfoDao {
	private DBHpler dbHpler;

	public DownLoadInfoDao(Context context){
		dbHpler=new DBHpler(context);
	}
	
	/**
	 * 查询所有下载任务
	 * */
	public List<DownLoadInfo> searchAll(){
		List<DownLoadInfo> list=new ArrayList<DownLoadInfo>();
		DownLoadInfo downLoadInfo=null;
		SQLiteDatabase db=dbHpler.getReadableDatabase();
		Cursor cr=db.rawQuery("SELECT * FROM "+DBData.DOWNLOADINFO_TABLENAME+" ORDER BY "+DBData.DOWNLOADINFO_ID, null);
		while(cr.moveToNext()){
			downLoadInfo=new DownLoadInfo();
			downLoadInfo.setId(cr.getInt(cr.getColumnIndex(DBData.DOWNLOADINFO_ID)));
			downLoadInfo.setThreadInfos(null);
			downLoadInfo.setFileSize(cr.getInt(cr.getColumnIndex(DBData.DOWNLOADINFO_FILESIZE)));
			downLoadInfo.setUrl(cr.getString(cr.getColumnIndex(DBData.DOWNLOADINFO_URL)));
			downLoadInfo.setAlbum(cr.getString(cr.getColumnIndex(DBData.DOWNLOADINFO_ALBUM)));
			downLoadInfo.setArtist(cr.getString(cr.getColumnIndex(DBData.DOWNLOADINFO_ARTIST)));
			downLoadInfo.setDisplayName(cr.getString(cr.getColumnIndex(DBData.DOWNLOADINFO_DISPLAYNAME)));
			downLoadInfo.setDurationTime(cr.getInt(cr.getColumnIndex(DBData.DOWNLOADINFO_DURATIONTIME)));
			downLoadInfo.setCompleteSize(cr.getInt(cr.getColumnIndex(DBData.DOWNLOADINFO_COMPLETESIZE)));
			downLoadInfo.setFilePath(cr.getString(cr.getColumnIndex(DBData.DOWNLOADINFO_FILEPATH)));
			downLoadInfo.setMimeType(cr.getString(cr.getColumnIndex(DBData.DOWNLOADINFO_MIMETYPE)));
			downLoadInfo.setName(cr.getString(cr.getColumnIndex(DBData.DOWNLOADINFO_NAME)));
			downLoadInfo.setState(DownLoadManager.STATE_PAUSE);
			downLoadInfo.setThreadCount(0);
			list.add(downLoadInfo);
		}
		cr.close();
		db.close();
		return list;
	}
	
	/**
	 * 添加
	 * */
	public int add(DownLoadInfo downLoadInfo){
		SQLiteDatabase db=dbHpler.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(DBData.DOWNLOADINFO_FILESIZE, downLoadInfo.getFileSize());
		values.put(DBData.DOWNLOADINFO_URL, downLoadInfo.getUrl());
		values.put(DBData.DOWNLOADINFO_ALBUM, downLoadInfo.getAlbum());
		values.put(DBData.DOWNLOADINFO_ARTIST, downLoadInfo.getArtist());
		values.put(DBData.DOWNLOADINFO_DISPLAYNAME, downLoadInfo.getDisplayName());
		values.put(DBData.DOWNLOADINFO_DURATIONTIME, downLoadInfo.getDurationTime());
		values.put(DBData.DOWNLOADINFO_FILEPATH, downLoadInfo.getFilePath());
		values.put(DBData.DOWNLOADINFO_COMPLETESIZE, 0);
		values.put(DBData.DOWNLOADINFO_MIMETYPE, downLoadInfo.getMimeType());
		values.put(DBData.DOWNLOADINFO_NAME, downLoadInfo.getName());
		
		int rs=(int)db.insert(DBData.DOWNLOADINFO_TABLENAME, DBData.DOWNLOADINFO_URL, values);
		db.close();
		return rs;
	}
	
	/**
	 * 更新下载进度
	 * */
	public  void update(int id,int completeSize){
		SQLiteDatabase db=dbHpler.getWritableDatabase();
		db.execSQL("UPDATE "+DBData.DOWNLOADINFO_TABLENAME+" SET "+DBData.DOWNLOADINFO_COMPLETESIZE+"=? WHERE "+DBData.DOWNLOADINFO_ID+"=?",new Object[]{completeSize,id});
		db.close();
	}
	
	/**
	 * 删除
	 * */
	public int delete(int id){
		SQLiteDatabase db=dbHpler.getWritableDatabase();
		int rs=db.delete(DBData.DOWNLOADINFO_TABLENAME, DBData.DOWNLOADINFO_ID+"=?", new String[]{String.valueOf(id)});
		db.close();
		return rs;
	}
	
	/**
	 * 判断下载任务是否存在
	 * */
	public boolean isExist(String url){
		int rs=-1;
		SQLiteDatabase db=dbHpler.getReadableDatabase();
		Cursor cr=db.rawQuery("SELECT COUNT(*) FROM "+DBData.DOWNLOADINFO_TABLENAME+" WHERE "+DBData.DOWNLOADINFO_URL+"=?", new String[]{url});
		while(cr.moveToNext()){
			rs=cr.getInt(0);
		}
		cr.close();
		db.close();
		return rs>0;
	}
}
