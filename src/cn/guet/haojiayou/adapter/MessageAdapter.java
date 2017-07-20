package cn.guet.haojiayou.adapter;

import java.util.ArrayList;

import cn.guet.haojiayou.R;
import cn.guet.haojiayou.bean.PushInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MessageAdapter extends BaseAdapter{

	private ArrayList<PushInfo> list = null;
	private LayoutInflater layoutInflater;
	private Context context;
	
	public MessageAdapter (ArrayList<PushInfo> list, Context context) {
		this.list = list;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return null==list?0:list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null==list?null:list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return null==list?0:position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.item_message, null);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.pushtime = (TextView) convertView.findViewById(R.id.pushtime);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		if (null!=list) {
			
			holder.content.setText(list.get(position).content);
			String time = list.get(position).pushTime;
			holder.pushtime.setText(time.substring(0,time.length()-2));
		}
		return convertView;
	}

	class ViewHolder{
		TextView content,pushtime;
	}
	
}
