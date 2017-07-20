package com.carmusic.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.carmusic.entity.ThreadInfo;

public class ThreadInfoDao {
	private DBHpler dbHpler;

	public ThreadInfoDao(Context context){
		dbHpler=new DBHpler(context);
	}
	
	/**
	 * 查询某个下载任务的全部子线程
	 * */
	public List<ThreadInfo> searchByDownLoadInfoId(int downLoadInfoId){
		List<ThreadInfo> list=new ArrayList<ThreadInfo>();
		ThreadInfo info=null;
		SQLiteDatabase db=dbHpler.getReadableDatabase();
		Cursor cr=db.rawQuery("SELECT * FROM "+DBData.THREADINFO_TABLENAME+" WHERE "+DBData.THREADINFO_DOWNLOADINFOID+"="+downLoadInfoId, null);
		while(cr.moveToNext()){
			info=new ThreadInfo();
			info.setId(cr.getInt(cr.getColumnIndex(DBData.THREADINFO_ID)));
			info.setStartPosition(cr.getInt(cr.getColumnIndex(DBData.THREADINFO_STARTPOSITION)));
			info.setCompleteSize(cr.getInt(cr.getColumnIndex(DBData.THREADINFO_COMPLETESIZE)));
			info.setDownLoadInfoId(cr.getInt(cr.getColumnIndex(DBData.THREADINFO_DOWNLOADINFOID)));
			info.setEndPosition(cr.getInt(cr.getColumnIndex(DBData.THREADINFO_ENDPOSITION)));
			list.add(info);
		}
		cr.close();
		db.close();
		return list;
	}
	
	/**
	 * 添加
	 * */
	public int add(ThreadInfo threadInfo){
		SQLiteDatabase db=dbHpler.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(DBData.THREADINFO_STARTPOSITION, threadInfo.getStartPosition());
		values.put(DBData.THREADINFO_ENDPOSITION, threadInfo.getEndPosition());
		values.put(DBData.THREADINFO_DOWNLOADINFOID, threadInfo.getDownLoadInfoId());
		values.put(DBData.THREADINFO_COMPLETESIZE, threadInfo.getCompleteSize());
		int rs=(int)db.insert(DBData.THREADINFO_TABLENAME, DBData.THREADINFO_COMPLETESIZE, values);
		db.close();
		return rs;
	}
	
	/**
	 * 更新
	 * */
	public void update(List<ThreadInfo> threadInfos){
		SQLiteDatabase db=dbHpler.getWritableDatabase();
		db.beginTransaction();
		try {
			for (int i = 0,len=threadInfos.size(); i < len; i++) {
				db.execSQL("UPDATE "+DBData.THREADINFO_TABLENAME+" SET "+DBData.THREADINFO_COMPLETESIZE+"=? WHERE "+DBData.THREADINFO_ID+"=?",new Object[]{threadInfos.get(i).getCompleteSize(),threadInfos.get(i).getId()});
			}
			db.setTransactionSuccessful();//设置事务处理成功，不设置会自动回滚不提交
		} finally {
			db.endTransaction();
		}
		db.close();
	}
	
	/**
	 * 删除
	 * */
	public int delete(int id){
		SQLiteDatabase db=dbHpler.getWritableDatabase();
		int rs=db.delete(DBData.THREADINFO_TABLENAME, DBData.THREADINFO_ID+"=?", new String[]{String.valueOf(id)});
		db.close();
		return rs;
	}
	
	/**
	 * 根据下载任务Id删除
	 * */
	public int deleteByDownLoadInfoId(int id){
		SQLiteDatabase db=dbHpler.getWritableDatabase();
		int rs=db.delete(DBData.THREADINFO_TABLENAME, DBData.THREADINFO_DOWNLOADINFOID+"=?", new String[]{String.valueOf(id)});
		db.close();
		return rs;
	}
}
