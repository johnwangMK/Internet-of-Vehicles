package cn.guet.haojiayou.adapter;

import java.util.ArrayList;
import java.util.List;


import cn.guet.haojiayou.R;
import cn.guet.haojiayou.R.id;
import cn.guet.haojiayou.R.layout;
import cn.guet.haojiayou.bean.CarInfo;
import cn.guet.haojiayou.utils.GetAssets;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SelectCarPopupWindow extends PopupWindow {
	private Context mContext;
	private ListView mListView;
	private CarSpinerAdapter mAdapter;
	   private TextView tv_brand,tv_plateNo;
	  private ImageView   img_car_logo;
	 public List<CarInfo> list = new ArrayList<CarInfo>();
	
	public SelectCarPopupWindow(Context context, TextView tv_brand,TextView tv_plateNo,ImageView img_car_logo,List<CarInfo> list)
	{
		super(context);
		this.tv_brand = tv_brand;
		this.tv_plateNo = tv_plateNo;
		this.img_car_logo = img_car_logo;
		mContext = context;
		this.list = list;
		init();
	}
	
		public SelectCarPopupWindow(Context context)
		{
			super(context);
			mContext = context;
			init();
		}	
		
	
	private void init()
	{
		View view = LayoutInflater.from(mContext).inflate(R.layout.selectcarpopupwindow, null);
		setContentView(view);		
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		
		setFocusable(true);	
		
		mListView = (ListView) view.findViewById(R.id.listview);		
		mAdapter = new CarSpinerAdapter(mContext,list);	
		list = mAdapter.mObjects;
		mListView.setAdapter(mAdapter);	
		mListView.setOnItemClickListener(new ListViewItemClick());
	}
	
	
	public void refreshData(List<CarInfo> list, int selIndex)
	{
		if (list != null && selIndex  != -1)
		{
			mAdapter.refreshData(list, selIndex);
		}
	}

      class ListViewItemClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			// TODO Auto-generated method stub
			dismiss();				
		if (pos >= 0 && pos <= list.size()){
//		String value = list.get(pos);
		CarInfo carInfo = list.get(pos);
		System.out.println("选择汽车"+list.get(pos));
			tv_brand.setText(carInfo.brand);
			tv_plateNo.setText(carInfo.plateNo);
		     String	logopath = "carimages/"+carInfo.logo;							
		     Drawable  drawable = GetAssets.getImageFromAssetsFile(logopath, mContext); 
             img_car_logo.setImageDrawable(drawable);

	}
		
   }
		
 }

}
