package com.carmusic.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.carmusic.entity.Album;

/**
 * 专辑DAO
 * */
public class AlbumDao {
	private DBHpler dbHpler;
	
	public AlbumDao(Context context){
		dbHpler=new DBHpler(context);
	}
	
	/**
	 * 全部查询
	 * */
	public List<String[]> searchAll(){
		List<String[]> list=new ArrayList<String[]>();
		SQLiteDatabase db=dbHpler.getReadableDatabase();
		Cursor cr=db.rawQuery("SELECT * FROM "+DBData.ALBUM_TABLENAME+" ORDER BY "+DBData.ALBUM_NAME+" DESC",null);
		while(cr.moveToNext()){
			String[] s=new String[3];
			s[0]=String.valueOf(cr.getInt(cr.getColumnIndex(DBData.ALBUM_ID)));
			s[1]=cr.getString(cr.getColumnIndex(DBData.ALBUM_NAME));
			s[2]=cr.getString(cr.getColumnIndex(DBData.ALBUM_PICPATH));
			list.add(s);
		}
		cr.close();
		db.close();
		return list;
	}
	
	/**
	 * 判断专辑是否存在，存在返回id
	 * */
	public int isExist(String name){
		int id=-1;
		SQLiteDatabase db=dbHpler.getReadableDatabase();
		Cursor cr=db.rawQuery("SELECT "+DBData.ALBUM_ID+" FROM "+DBData.ALBUM_TABLENAME+" WHERE "+DBData.ALBUM_NAME+"=?", new String[]{name});
		if(cr.moveToNext()){
			id=cr.getInt(0);
		}
		cr.close();
		db.close();
		return id;
	}
	
	/**
	 * 获取记录总数
	 * */
	public int getCount(){
		int count=0;
		SQLiteDatabase db=dbHpler.getReadableDatabase();
		Cursor cr=db.rawQuery("SELECT COUNT(*) FROM "+DBData.ALBUM_TABLENAME, null);
		if(cr.moveToNext()){
			count=cr.getInt(0);
		}
		cr.close();
		db.close();
		return count;
	}
	
	/**
	 * 添加
	 * */
	public long add(Album album){
		SQLiteDatabase db=dbHpler.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(DBData.ALBUM_NAME, album.getName());
		values.put(DBData.ALBUM_PICPATH, album.getPicPath());
		long rs=db.insert(DBData.ALBUM_TABLENAME, DBData.ALBUM_NAME, values);
		db.close();
		return rs;
	}
	
	/**
	 * 删除
	 * */
	public int delete(int id){
		SQLiteDatabase db=dbHpler.getWritableDatabase();
		int rs=db.delete(DBData.ALBUM_TABLENAME, DBData.ALBUM_ID+"=?",new String[]{String.valueOf(id)});
		db.close();
		return rs;
	}
	
	/**
	 * 更新
	 * */
	public int update(Album album){
		SQLiteDatabase db=dbHpler.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(DBData.ALBUM_NAME, album.getName());
		values.put(DBData.ALBUM_PICPATH, album.getPicPath());
		int rs=db.update(DBData.ALBUM_TABLENAME, values, DBData.ALBUM_ID+"=?", new String[]{String.valueOf(album.getId())});
		db.close();
		return rs;
	}

}
