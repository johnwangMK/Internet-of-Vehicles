package com.carmusic.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import cn.guet.haojiayou.R;

import com.carmusic.entity.Song;
import com.carmusic.service.MediaPlayerManager;

public class DownLoadListAdapter extends BaseAdapter{
	private Context context;
	private List<Song> data;
	private int[] playerInfo=new int[2];//0:playerId,1:playerstate
	private ItemListener mItemListener;
	
	public DownLoadListAdapter(Context context,List<Song> data,int[] playerInfo){
		this.context=context;
		this.data=data;
		this.playerInfo=playerInfo;
	}
	
	public DownLoadListAdapter setItemListener(ItemListener mItemListener){
		this.mItemListener=mItemListener;
		return this;
	}
	
	public void setPlayerInfo(int[] playerInfo){
		this.playerInfo=playerInfo;
		notifyDataSetChanged();
	}
	
	public void setPlayerState(int playerState){
		this.playerInfo[1]=playerState;
		notifyDataSetChanged();
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

	public void deleteItem(int position){
		data.remove(position);
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.music_download_list_item, null);
			viewHolder.tv_download_list_item_bottom=(TextView)convertView.findViewById(R.id.tv_download_list_item_bottom);
			viewHolder.tv_download_list_item_number=(TextView)convertView.findViewById(R.id.tv_download_list_item_number);
			viewHolder.tv_download_list_item_top=(TextView)convertView.findViewById(R.id.tv_download_list_item_top);
			viewHolder.ibtn_download_list_item_menu=(Button)convertView.findViewById(R.id.ibtn_download_list_item_menu);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		
		final Song song=data.get(position);
		//是否与播放歌曲是同一首
		if(song.getId()==playerInfo[0]){
			viewHolder.tv_download_list_item_number.setText("");
			//暂停
			if(playerInfo[1]==MediaPlayerManager.STATE_PAUSE){
				viewHolder.tv_download_list_item_number.setBackgroundResource(R.drawable.music_list_item_pause);
			}else if(playerInfo[1]==MediaPlayerManager.STATE_PLAYER||playerInfo[1]==MediaPlayerManager.STATE_PREPARE){//播放
				viewHolder.tv_download_list_item_number.setBackgroundResource(R.drawable.music_list_item_player);
			}else if(playerInfo[1]==MediaPlayerManager.STATE_OVER){
				viewHolder.tv_download_list_item_number.setText((position+1)+"");
				viewHolder.tv_download_list_item_number.setBackgroundResource(0);
			}
		}else{
			viewHolder.tv_download_list_item_number.setText((position+1)+"");
			viewHolder.tv_download_list_item_number.setBackgroundResource(0);
		}
		
		viewHolder.tv_download_list_item_top.setText(song.getName());
		viewHolder.tv_download_list_item_top.setTag(song.getFilePath());
		viewHolder.tv_download_list_item_bottom.setText(song.getArtist().getName());
		viewHolder.ibtn_download_list_item_menu.setTag(song.getId());
		
		viewHolder.ibtn_download_list_item_menu.setFocusable(false);
		viewHolder.ibtn_download_list_item_menu.setFocusableInTouchMode(false);
		
		viewHolder.ibtn_download_list_item_menu.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mItemListener.onDelete(song.getId(), song.getFilePath(), position);
			}
		});
		return convertView;
	}
	
	public interface ItemListener{
		void onDelete(int id,String path,int position);
	}
	
	public class ViewHolder{
		public TextView tv_download_list_item_number;
		public TextView tv_download_list_item_top;
		public TextView tv_download_list_item_bottom;
		public Button ibtn_download_list_item_menu;
	}
}
