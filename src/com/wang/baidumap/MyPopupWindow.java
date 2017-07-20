package com.wang.baidumap;

import cn.guet.haojiayou.R;

import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.wang.juhe.Data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MyPopupWindow extends PopupWindow {

	private TextView gs_name;
	private TextView gs_detail;
	private ImageView gs_icon;
	private Button add_oil;
	private View view;
	//public MyPopupWindow(final Fragment context,final PoiDetailResult poiDetailResult){
	public MyPopupWindow(final Activity context,final PoiDetailResult poiDetailResult){
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.popupwindow, null);
		this.setContentView(view);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		gs_name = (TextView) view.findViewById(R.id.gs_name);
		gs_detail = (TextView) view.findViewById(R.id.gs_detail);
		gs_icon = (ImageView) view.findViewById(R.id.gs_icon);
		String name = poiDetailResult.getName();
		String detail = poiDetailResult.getAddress();
		gs_name.setText(name);
		gs_detail.setText(detail);
		add_oil = (Button) view.findViewById(R.id.add_oil);
		add_oil.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				Intent intent = new Intent(context, StationDetailActivity.class);
				Bundle bundle = new Bundle();
				String gs_name = poiDetailResult.getName();
				String gs_address = poiDetailResult.getAddress();
				bundle.putString("name", gs_name);
				bundle.putString("address", gs_address);
				intent.putExtras(bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		});
		this.setFocusable(true);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int height = view.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(y<height){
						dismiss();
					}
				}
				return true;
			}
		});
		
	}

	public MyPopupWindow(final Activity context, final Data[] data, final int i) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.popupwindow, null);
		this.setContentView(view);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		gs_name = (TextView) view.findViewById(R.id.gs_name);
		gs_detail = (TextView) view.findViewById(R.id.gs_detail);
		gs_icon = (ImageView) view.findViewById(R.id.gs_icon);
		String name = data[i].getName();
		String detail = data[i].getAddress();
		String brandname = data[i].getBrandname();
		//Toast.makeText(context, brandname, Toast.LENGTH_SHORT).show();
		if(brandname.equals("中石油")){
			gs_icon.setBackgroundResource(R.drawable.logo_for_station_detail_zhongshiyou);
		}else if(brandname.equals("中石化")){
			gs_icon.setBackgroundResource(R.drawable.logo_for_station_detail_zhongshihua);
		}else if(brandname.equals("道达尔")){
			gs_icon.setBackgroundResource(R.drawable.logo_for_station_detail_daodaer);
		}else if(brandname.equals("乐福")){
			gs_icon.setBackgroundResource(R.drawable.logo_for_station_detail_yuefu);
		}else{
			gs_icon.setBackgroundResource(R.drawable.yzp);
		}
		gs_name.setText(name);
		gs_detail.setText(detail);
		add_oil = (Button) view.findViewById(R.id.add_oil);
		add_oil.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				Intent intent = new Intent(context, StationDetailActivity.class);
				Bundle bundle = new Bundle();
				String gs_name = data[i].getName();
				String brandname = data[i].getBrandname();
				String gs_address = data[i].getAddress();
				String gastprice_90 = data[i].getGastprice().getP90();
				String gastprice_93 = data[i].getGastprice().getP93();
				String gastprice_97 = data[i].getGastprice().getP97();
				String gastprice_0 = data[i].getGastprice().getP0();
				double longitude = data[i].getLon();
				double latitude = data[i].getLat();
				//Toast.makeText(context, gastprice_90 + "," + gastprice_93 + "," + gastprice_97 + "," + gastprice_0, Toast.LENGTH_LONG).show();
				bundle.putString("gs_name", gs_name);
				bundle.putString("gs_address", gs_address);
				bundle.putString("brandname", brandname);
				bundle.putString("gastprice_90", gastprice_90);
				bundle.putString("gastprice_93", gastprice_93);
				bundle.putString("gastprice_97", gastprice_97);
				bundle.putString("gastprice_0", gastprice_0);
				bundle.putDouble("longitude",longitude);
				bundle.putDouble("latitude", latitude);
				intent.putExtras(bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				
			}
		});
		this.setFocusable(true);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int height = view.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(y<height){
						dismiss();
					}
				}
				return true;
			}
		});
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
			}
		});
	}
}
