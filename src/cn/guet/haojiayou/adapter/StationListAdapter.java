package cn.guet.haojiayou.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import cn.guet.haojiayou.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StationListAdapter extends BaseAdapter{
	
	private List<Map<String,Object>> data = null;
	private LayoutInflater layoutInflater;
	private Context context;
	
	public StationListAdapter(List<Map<String, Object>> data, Context context) {
		super();
		this.data = data;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
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
		ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.station_item, null);
			//convertView = View.inflate(context, R.layout.station_item, null);
			viewHolder.station_logo = (ImageView) convertView.findViewById(R.id.station_logo);
			viewHolder.station_name = (TextView) convertView.findViewById(R.id.station_name);
			viewHolder.station_adress = (TextView) convertView.findViewById(R.id.station_address);
			viewHolder.station_distance = (TextView) convertView.findViewById(R.id.station_distance);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		//�����
		if(data.get(position).get("brandname").equals("中石化")){
			viewHolder.station_logo.setBackgroundResource(R.drawable.logo_for_station_detail_zhongshihua);
		}else if(data.get(position).get("brandname").equals("中石油")){
			viewHolder.station_logo.setBackgroundResource(R.drawable.logo_for_station_detail_zhongshiyou);
		}else{
			viewHolder.station_logo.setBackgroundResource(R.drawable.yzp);
		}
		viewHolder.station_name.setText((String) data.get(position).get("name"));
		/*float distance = (Integer.parseInt((String) data.get(position).get("distance")) )/1000;
		DecimalFormat df = new DecimalFormat("0.00");
		String distance1 = df.format(distance);*/
		viewHolder.station_distance.setText((String) data.get(position).get("distance") + "米");
		viewHolder.station_adress.setText((String) data.get(position).get("address"));
		return convertView;
	}
	
	static class ViewHolder{
		public ImageView station_logo;
		public TextView station_name;
		public TextView station_adress;
		public TextView station_distance;
	}

}
