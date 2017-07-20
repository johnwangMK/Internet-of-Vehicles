package cn.guet.haojiayou.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import cn.guet.haojiayou.R;
import cn.guet.haojiayou.R.id;
import cn.guet.haojiayou.R.layout;
import cn.guet.haojiayou.utils.BorderTextView;
//import cn.guet.haojiayou.adapter.SpinerAdapter.IOnItemSelectListener;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SelectOilPopupWindow extends PopupWindow {
	private Context mContext;
	private ListView mListView;
	private OilSpinerAdapter mAdapter;
	private TextView tv_stype,tv_sy,tv_sprice;
	private TextView tv_summoney;
	private BorderTextView Border6,Border7,Border8;
	public List<Map<String,Object>> list= new ArrayList<Map<String,Object>>();
	public double quanlity;
	
	public SelectOilPopupWindow(Context context, TextView tv_stype,TextView tv_sy,TextView tv_sprice,List<Map<String,Object>> list,
			BorderTextView Border6,BorderTextView Border7,BorderTextView Border8,TextView tv_summoney)
	{
		super(context);
		this.tv_stype = tv_stype;
		this.tv_sy = tv_sy;
		this.tv_sprice = tv_sprice;
		this.Border6 = Border6;
		this.Border7 = Border7;
		this.Border8 = Border8;
		mContext = context;
		this.list = list;
		this.tv_summoney = tv_summoney;
		init();
	}
	
		public SelectOilPopupWindow(Context context)
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
		mAdapter = new OilSpinerAdapter(mContext,list);	
		list = mAdapter.mObjects;
		mListView.setAdapter(mAdapter);	
		mListView.setOnItemClickListener(new ListViewItemClick());
	}
	
	
	public void refreshData(List<Map<String,Object>> list, int selIndex)
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
		Map<String,Object> map = list.get(pos);
		tv_stype.setText((String)map.get("type"));
        tv_sy.setText("￥");
        tv_sy.setTextColor(Color.RED);
        String price = (String)map.get("price");
		tv_sprice.setText(price);
		
		String sumcost = calcuMoney(quanlity,Double.valueOf(price));
		tv_summoney.setText(sumcost);
		
      	System.out.println("单价是"+(String)map.get("price"));
      	System.out.println("升是"+quanlity);
		System.out.println("总价是"+sumcost);
		
	}
		Border6.setClickable(true);
		Border7.setClickable(true);
		Border8.setClickable(true);
   }
		
 }
      /**
       * 按升计算总金额
       * @param quantity 升
       * @return 小数点后一位
       */
      private String calcuMoney(Double quantity,Double price){
     	
      	  DecimalFormat df=new DecimalFormat("#.0");
      	return df.format(quantity*price);
      }
}
