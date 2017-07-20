package cn.guet.haojiayou.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import cn.guet.haojiayou.R;

import com.wang.entity.SuggestionInfo;

public class AutoAdapter extends BaseAdapter implements Filterable {
	private List<SuggestionInfo> mList = null;
	private Context context;
	private ArrayFilter mFilter;
	private ArrayList<SuggestionInfo> mUnfilteredData;
	
	public AutoAdapter(List<SuggestionInfo> mList, Context context) {
		super();
		this.mList = mList;
		this.context = context;
	}

	public void clear() {
		mList.clear();
	}

	public void add(int arg0, SuggestionInfo arg1) {
		mList.add(arg0, arg1);
	}

	public boolean add(SuggestionInfo arg0) {
		return mList.add(arg0);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.auto_item, null);
			convertView = View.inflate(context, R.layout.auto_item, null);
			holder.auto_name = (TextView) convertView.findViewById(R.id.name);
			holder.auto_address = (TextView) convertView.findViewById(R.id.adress);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		SuggestionInfo si = mList.get(position);
		holder.auto_name.setText(si.getKey());
		holder.auto_address.setText(si.getDistrict());
		return convertView;
	}
	
	static class ViewHolder{
		public ImageView auto_image;
		public TextView auto_name;
		public TextView auto_address;
	}
	@Override
	public Filter getFilter() {
		if(mFilter == null){
			mFilter = new ArrayFilter();
		}
		return mFilter;
	}
	
	private class ArrayFilter extends Filter{

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();
			if(mUnfilteredData == null){
				mUnfilteredData = new ArrayList<SuggestionInfo>(mList);
			}
			if(prefix == null || prefix.length() == 0){
				results.values = mUnfilteredData;
				results.count = mUnfilteredData.size();
			}else{
				List<SuggestionInfo> retList = new ArrayList<SuggestionInfo>();
				String prefixString = prefix.toString();
				for(SuggestionInfo si : mUnfilteredData){
					if(si.getKey().contains(prefixString)){
						retList.add(si);
					}
				}
				results.values = retList;
				results.count = retList.size();
				
			}
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			mList = (List<SuggestionInfo>) results.values;
			if(results.count>0){
				notifyDataSetChanged();
			}else{
				notifyDataSetInvalidated();
			}
			
		}
		
	}

}
