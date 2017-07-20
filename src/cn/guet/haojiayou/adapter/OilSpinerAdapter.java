package cn.guet.haojiayou.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.guet.haojiayou.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public  class OilSpinerAdapter extends BaseAdapter {
	
	 private Context mContext;   
	 public List<Map<String,Object>> mObjects = new ArrayList<Map<String,Object>>();
	 private int mSelectItem = 0;    
	 private LayoutInflater mInflater;
	 
	 public  OilSpinerAdapter(Context context,List<Map<String,Object>> mObjects){
		 this.mObjects = mObjects;
		 mContext = context;
		 mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 }
	 	 
	 public void refreshData(List<Map<String,Object>> objects, int selIndex){
		 mObjects = objects;
		 if (selIndex < 0){
			 selIndex = 0;
		 }
		 if (selIndex >= mObjects.size()){
			 selIndex = mObjects.size() - 1;
		 }
		 
		 mSelectItem = selIndex;
	 }
	    	    
	@Override
	public int getCount() {

		return mObjects.size();
	}

	@Override
	public Object getItem(int pos) {
		return mObjects.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) {
		 ViewHolder viewHolder;
    	 
	     if (convertView == null) {
	    	 convertView = mInflater.inflate(R.layout.spiner_oil_item, null);
	         viewHolder = new ViewHolder();
	         viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
	         viewHolder.tv_sy = (TextView) convertView.findViewById(R.id.tv_y);
	         viewHolder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
	         convertView.setTag(viewHolder);
	     } else {
	         viewHolder = (ViewHolder) convertView.getTag();
	     }

	     
	     Map<String,Object> map = (Map<String, Object>) getItem(pos);
		 viewHolder.tv_type.setText((String)map.get("type"));
		 viewHolder.tv_price.setText((String)map.get("price"));
	     return convertView;
	}

	public static class ViewHolder
	{
	    public TextView tv_type,tv_sy,tv_price;
    }


}
