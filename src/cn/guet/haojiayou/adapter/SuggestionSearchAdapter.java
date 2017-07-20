package cn.guet.haojiayou.adapter;

import java.util.List;

import com.wang.entity.SuggestionInfo;

import cn.guet.haojiayou.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SuggestionSearchAdapter extends BaseAdapter {
	private List<SuggestionInfo> data = null;
	private Context context;
	
	public SuggestionSearchAdapter(List<SuggestionInfo> data, Context context) {
		super();
		this.data = data;
		this.context = context;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.sug_result_item, null);
			viewHolder.result_text = (TextView) convertView.findViewById(R.id.suggestion_result_listview_text);
			viewHolder.result_text_district = (TextView) convertView.findViewById(R.id.suggestion_result_listview_district);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.result_text.setText(data.get(position).getKey());
		viewHolder.result_text_district.setText(data.get(position).getDistrict());
		return convertView;
	}
	
	static class ViewHolder{
		public TextView result_text;
		public TextView result_text_district;
	}
	
	public void removeAll(){
		data.clear();
		notifyDataSetChanged();
	}
}
