package com.wang.entity;

import com.baidu.mapapi.model.LatLng;
/**
 * �����ѯ���ʵ����
 * @author Angel
 *
 */
public class SuggestionInfo {
	private String city;//����ʳ���
	private String district;//����������������
	private String key;//����ʹؼ���
	private LatLng pt;//����������
	private String uid;//������uid
	
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
