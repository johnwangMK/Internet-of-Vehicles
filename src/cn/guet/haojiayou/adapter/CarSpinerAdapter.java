package cn.guet.haojiayou.adapter;

import java.util.ArrayList;
import java.util.List;
import cn.guet.haojiayou.R;
import cn.guet.haojiayou.bean.CarInfo;
import cn.guet.haojiayou.utils.GetAssets;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public  class CarSpinerAdapter extends BaseAdapter {
	
	 private Context mContext;   
	 public List<CarInfo> mObjects = new ArrayList<CarInfo>();
	 private int mSelectItem = 0;    
	 private LayoutInflater mInflater;
	 
	 public  CarSpinerAdapter(Context context,List<CarInfo> mObjects){
		 this.mObjects = mObjects;
		 mContext = context;
		 mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 }
	 	 
	 public void refreshData(List<CarInfo> objects, int selIndex){
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
	    	 convertView = mInflater.inflate(R.layout.spiner_car_item, null);
	         viewHolder = new ViewHolder();
	         viewHolder.tv_brand = (TextView) convertView.findViewById(R.id.tv_ibrand);
	         viewHolder.tv_plateNo = (TextView) convertView.findViewById(R.id.tv_iplateNo);
	         viewHolder.img_car_logo = (ImageView) convertView.findViewById(R.id.img_icar_logo);
	         convertView.setTag(viewHolder);
	     } else {
	         viewHolder = (ViewHolder) convertView.getTag();
	     }
	     
	     CarInfo carInfo = (CarInfo) getItem(pos);
	     viewHolder.tv_brand.setText(carInfo.brand);
	     viewHolder.tv_plateNo.setText(carInfo.plateNo);
	     String	logopath = "carimages/"+carInfo.logo;							
	     Drawable  drawable = GetAssets.getImageFromAssetsFile(logopath, mContext); 
	     viewHolder.img_car_logo.setImageDrawable(drawable);
	     
	     return convertView;
	}

	public static class ViewHolder
	{
	    public TextView tv_brand,tv_plateNo;
	    public ImageView   img_car_logo;
    }


}
