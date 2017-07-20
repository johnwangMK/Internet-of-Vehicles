package cn.guet.haojiayou.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

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

public class MyCarWeizhangAdapter extends BaseAdapter{

	Context context;
	ArrayList<CarInfo> list;
	//private LayoutInflater mInflater;
	ArrayList<String>  weizhanglist;
	public MyCarWeizhangAdapter(Context context,
			ArrayList<CarInfo> list) {
		
		this.context = context;
		this.list = list;
		//this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		View view = View.inflate(context, R.layout.item_weizhangcarlist, null);
		TextView tv_brand = (TextView) view.findViewById(R.id.tv_brand);
		TextView tv_plateNo = (TextView) view.findViewById(R.id.tv_plateNo);
		ImageView img_car_logo = (ImageView) view.findViewById(R.id.img_car_logo);
		
		CarInfo carInfo = list.get(position);
        tv_brand.setText(carInfo.brand);
        tv_plateNo.setText(carInfo.plateNo);
        String	logopath = "carimages/"+list.get(position).logo;							
        Drawable  drawable = GetAssets.getImageFromAssetsFile(logopath, context); 
        img_car_logo.setImageDrawable(drawable);
        
		return view;
	}
	
	public void addDatas( ArrayList<String>  datas) {
		 
		weizhanglist = datas;	
		notifyDataSetChanged();
	}
}
