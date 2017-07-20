package com.carmusic.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.guet.haojiayou.R;

import com.carmusic.entity.DownLoadInfo;
import com.carmusic.service.DownLoadManager;
import com.carmusic.util.Common;

public class DownLoadingListAdapter extends BaseAdapter {
	private Context context;
	private ItemListener mItemListener;
	private List<DownLoadInfo> data;
	
	public DownLoadingListAdapter(Context context,List<DownLoadInfo> data){
		this.context=context;
		this.data=data;
	}
	
	public void setData(List<DownLoadInfo> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	public DownLoadingListAdapter setItemListener(ItemListener mItemListener){
		this.mItemListener=mItemListener;
		return this;
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.music_downloading_list_item, null);
			viewHolder.tv_download_list_item_number=(TextView)convertView.findViewById(R.id.tv_download_list_item_number);
			viewHolder.tv_download_list_item_top=(TextView)convertView.findViewById(R.id.tv_download_list_item_top);
			viewHolder.tv_download_list_item_bottom=(TextView)convertView.findViewById(R.id.tv_download_list_item_bottom);
			viewHolder.pb_download_list_item=(ProgressBar)convertView.findViewById(R.id.pb_download_list_item);
			viewHolder.btn_download_list_item_pause=(Button)convertView.findViewById(R.id.btn_download_list_item_pause);
			viewHolder.btn_download_list_item_delete=(Button)convertView.findViewById(R.id.btn_download_list_item_delete);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		final DownLoadInfo downLoadInfo=data.get(position);
		viewHolder.tv_download_list_item_number.setTextSize(10);
		//存放数据
		viewHolder.tv_download_list_item_number.setTag(downLoadInfo.getUrl());
		viewHolder.tv_download_list_item_top.setTag(downLoadInfo.getState());
		
		viewHolder.tv_download_list_item_top.setText(downLoadInfo.getArtist()+"-"+downLoadInfo.getName());
		viewHolder.pb_download_list_item.setMax(downLoadInfo.getFileSize());
		viewHolder.pb_download_list_item.setProgress(downLoadInfo.getCompleteSize());
		viewHolder.tv_download_list_item_number.setBackgroundResource(0);
		viewHolder.btn_download_list_item_pause.setEnabled(true);
		viewHolder.btn_download_list_item_delete.setEnabled(true);
		
		if(downLoadInfo.getState()==DownLoadManager.STATE_CONNECTION){
			viewHolder.tv_download_list_item_number.setText(Common.getPercent(downLoadInfo.getCompleteSize(),downLoadInfo.getFileSize())+"%");
			viewHolder.tv_download_list_item_bottom.setText("正在连接...");
			viewHolder.btn_download_list_item_pause.setBackgroundResource(R.drawable.music_btn_download_pause);
		}else if(downLoadInfo.getState()==DownLoadManager.STATE_DOWNLOADING){
			viewHolder.tv_download_list_item_number.setText(Common.getPercent(downLoadInfo.getCompleteSize(),downLoadInfo.getFileSize())+"%");
			viewHolder.tv_download_list_item_bottom.setText("正在下载："+Common.formatByteToKB(downLoadInfo.getCompleteSize())+"kb/"+Common.formatByteToKB(downLoadInfo.getFileSize())+"kb");
			viewHolder.btn_download_list_item_pause.setBackgroundResource(R.drawable.music_btn_download_pause);
		}else if(downLoadInfo.getState()==DownLoadManager.STATE_PAUSE){
			viewHolder.tv_download_list_item_number.setTextSize(15);
			viewHolder.tv_download_list_item_number.setText(""+(position+1));
			viewHolder.tv_download_list_item_bottom.setText("已完成任务"+Common.getPercent(downLoadInfo.getCompleteSize(),downLoadInfo.getFileSize())+"%");
			viewHolder.btn_download_list_item_pause.setBackgroundResource(R.drawable.music_btn_download_start);
		}else if(downLoadInfo.getState()==DownLoadManager.STATE_WAIT){
			viewHolder.tv_download_list_item_number.setText("");
			viewHolder.tv_download_list_item_number.setBackgroundResource(R.drawable.music_download_wait);
			viewHolder.tv_download_list_item_bottom.setText("等待下载...");
			viewHolder.btn_download_list_item_pause.setBackgroundResource(R.drawable.music_btn_download_pause);
		}else if(downLoadInfo.getState()==DownLoadManager.STATE_DELETE){
			viewHolder.tv_download_list_item_number.setText("");
			viewHolder.tv_download_list_item_number.setBackgroundResource(R.drawable.music_delete_icon_normal);
			viewHolder.tv_download_list_item_bottom.setText("正在删除...");
			viewHolder.btn_download_list_item_pause.setBackgroundResource(R.drawable.music_btn_download_pause);
			viewHolder.btn_download_list_item_pause.setEnabled(false);
			viewHolder.btn_download_list_item_delete.setEnabled(false);
		}else if(downLoadInfo.getState()==DownLoadManager.STATE_PAUSEING){
			viewHolder.tv_download_list_item_number.setText(Common.getPercent(downLoadInfo.getCompleteSize(),downLoadInfo.getFileSize())+"%");
			viewHolder.tv_download_list_item_bottom.setText("正在暂停...");
			viewHolder.btn_download_list_item_pause.setBackgroundResource(R.drawable.music_btn_download_pause);
			viewHolder.btn_download_list_item_pause.setEnabled(false);
			viewHolder.btn_download_list_item_delete.setEnabled(false);
		}else if(downLoadInfo.getState()==DownLoadManager.STATE_ERROR){
			viewHolder.tv_download_list_item_number.setText(Common.getPercent(downLoadInfo.getCompleteSize(),downLoadInfo.getFileSize())+"%");
			viewHolder.tv_download_list_item_bottom.setText("发生错误");
			viewHolder.btn_download_list_item_pause.setBackgroundResource(R.drawable.music_btn_download_start);
		}else if(downLoadInfo.getState()==DownLoadManager.STATE_FAILED){
			viewHolder.tv_download_list_item_number.setTextSize(15);
			viewHolder.tv_download_list_item_number.setText(""+(position+1));
			viewHolder.tv_download_list_item_bottom.setText("服务器找不到文件");
			viewHolder.btn_download_list_item_pause.setBackgroundResource(R.drawable.music_btn_download_start);
		}
		viewHolder.btn_download_list_item_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mItemListener!=null){
					mItemListener.onDelete(downLoadInfo.getUrl());
				}
			}
		});
		
		viewHolder.btn_download_list_item_pause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mItemListener!=null){
					mItemListener.onPause(downLoadInfo.getUrl(), downLoadInfo.getState());
				}
			}
		});
		
		viewHolder.btn_download_list_item_delete.setFocusable(false);
		viewHolder.btn_download_list_item_delete.setFocusableInTouchMode(false);
		
		viewHolder.btn_download_list_item_pause.setFocusable(false);
		viewHolder.btn_download_list_item_pause.setFocusableInTouchMode(false);
		
		return convertView;
	}
	
	public class ViewHolder{
		public TextView tv_download_list_item_number;
		public TextView tv_download_list_item_top;
		public TextView tv_download_list_item_bottom;
		public ProgressBar pb_download_list_item;
		public Button btn_download_list_item_pause;
		public Button btn_download_list_item_delete;
	}
	
	public interface ItemListener{
		void onDelete(String url);
		void onPause(String url,int state);
	}
}
