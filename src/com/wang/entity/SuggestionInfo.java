package com.wang.entity;

import com.baidu.mapapi.model.LatLng;
/**
 * 建议查询结果实体类
 * @author Angel
 *
 */
public class SuggestionInfo {
	private String city;//联想词城市
	private String district;//联想结果所在行政区
	private String key;//联想词关键字
	private LatLng pt;//联想结果坐标
	private String uid;//联想结果uid
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public LatLng getPt() {
		return pt;
	}
	public void setPt(LatLng pt) {
		this.pt = pt;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
}
