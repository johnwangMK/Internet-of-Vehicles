package com.wang.juhe;

public class Result {
	private Data[] data;
	private Pageinfo pageinfo;
	public Data[] getData() {
		return data;
	}
	public void setData(Data[] data) {
		this.data = data;
	}
	public Pageinfo getPageinfo() {
		return pageinfo;
	}
	public void setPageinfo(Pageinfo pageinfo) {
		this.pageinfo = pageinfo;
	}
	public Result(Data[] data, Pageinfo pageinfo) {
		super();
		this.data = data;
		this.pageinfo = pageinfo;
	}
	
}
