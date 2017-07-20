package com.carmusic.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.guet.haojiayou.R;

import com.carmusic.entity.ScanData;

public class ScanListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<ScanData> data;
	private String rs="";
	
	public ScanListAdapter(Context context,List<ScanData> data){
		inflater=LayoutInflater.from(context);
		this.data=data;
	}
	
	public int getCount() {
		return data.size();
	}
	
	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * 返回选中的文件路径
	 * */
	public String getCheckFilePath() {
		return rs;
	}

	/**
	 * 设置选中的文件路径
	 * */
	public void setCheckFilePath(String rs) {
		this.rs = rs;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		final String filePath=data.get(position).getFilePath();
		if(convertView==null){
			 viewHolder = new ViewHolder();
			 convertView =inflater.inflate(R.layout.music_scan_list_item, null);
			 viewHolder.iv_scan_item_icon = ((ImageView)convertView.findViewById(R.id.iv_scan_item_icon));
			 viewHolder.tv_scan_item_title = ((TextView)convertView.findViewById(R.id.tv_scan_item_title));
			 viewHolder.cb_scan_item= ((CheckBox)convertView.findViewById(R.id.cb_scan_item));
			 convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		viewHolder.iv_scan_item_icon.setImageResource(R.drawable.music_directory_icon);
		viewHolder.tv_scan_item_title.setText(filePath);
		
		viewHolder.cb_scan_item.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//添加，删除路径
				if(isChecked){
					if(rs.toString().indexOf("$"+filePath+"$")==-1){
						rs+="$"+filePath+"$";
					}
				}else{
					rs=rs.replace("$"+filePath+"$", "");
				}
			}
		});
		
		viewHolder.cb_scan_item.setChecked(data.get(position).isChecked());
		return convertView;
	}
	
	private class ViewHolder{
		public ImageView iv_scan_item_icon;
		public TextView tv_scan_item_title;
		public CheckBox cb_scan_item;
	}
}
