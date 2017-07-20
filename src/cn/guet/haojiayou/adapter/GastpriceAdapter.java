package cn.guet.haojiayou.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.guet.haojiayou.R;

public class GastpriceAdapter extends BaseAdapter {
	
	private List<Map<String,Object>> data = null;
	private LayoutInflater layoutInflater;
	private Context context;
	
	public GastpriceAdapter(List<Map<String, Object>> data, Context context) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.station_detail_item, null);
			viewHolder.gas_kind = (TextView) convertView.findViewById(R.id.station_detail_item_gas_kind);
			viewHolder.gas_price = (TextView) convertView.findViewById(R.id.station_detail_item_price);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.gas_kind.setText((String) data.get(position).get("type"));
		if(data.get(position).get("price")!=null){
			viewHolder.gas_price.setText((String) data.get(position).get("price"));
		}else{
			viewHolder.gas_price.setText("无售");
		}
		return convertView;
	}
	
	static class ViewHolder{
		public TextView gas_price;
		public TextView gas_kind;
	}
}
