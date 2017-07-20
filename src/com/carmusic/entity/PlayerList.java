package com.carmusic.entity;

import java.io.Serializable;

/**
 * 播放列表[0:默认列表]
 * */
public class PlayerList  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;//id
	private String name;//播放列表名称
	private long date;//添加日期 
	
	public PlayerList(){
	}

	public PlayerList(int id,String name){
		this.id=id;
		this.name=name;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}
}
