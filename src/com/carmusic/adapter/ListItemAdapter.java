package com.carmusic.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.guet.haojiayou.R;



public class ListItemAdapter extends BaseAdapter {
	private List<String[]> data;
	private Context context;
	private int defaultIcon;
	
	public ListItemAdapter(Context context,List<String[]> data,int defaultIcon){
		this.context=context;
		this.data=data;
		this.defaultIcon=defaultIcon;
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

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.music_list_item, null);
			viewHolder.imageView=(ImageView)convertView.findViewById(R.id.iv_list_item_icon);
			viewHolder.textView=(TextView)convertView.findViewById(R.id.tv_list_item_title);
			viewHolder.imageView2=(ImageView)convertView.findViewById(R.id.iv_list_item_icon2);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		String pic=data.get(0)[2];
		if(TextUtils.isEmpty(pic)){
			viewHolder.imageView.setImageResource(defaultIcon);
		}else{
			Bitmap bitmap=BitmapFactory.decodeFile(pic);
			//判断SD图片是否存在
			if(bitmap!=null){
				viewHolder.imageView.setImageBitmap(bitmap);
			}else{
				viewHolder.imageView.setImageResource(defaultIcon);
			}
		}
		viewHolder.textView.setText(data.get(position)[1]);
		viewHolder.textView.setTag(data.get(position)[0]);//Id或者文件路径
		viewHolder.imageView2.setImageResource(R.drawable.player_playlist_sign);
		return convertView;
	}

	public class ViewHolder{
		public ImageView imageView;
		public TextView textView;
		public ImageView imageView2;
	}
}
