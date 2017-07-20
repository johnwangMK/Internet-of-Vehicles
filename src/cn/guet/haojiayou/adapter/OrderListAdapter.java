package cn.guet.haojiayou.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.guet.haojiayou.R;
import cn.guet.haojiayou.bean.FillOilOrder;
import cn.guet.haojiayou.ui.EnsurePay;
import cn.guet.haojiayou.utils.NetService;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderListAdapter extends BaseAdapter {

	private List<FillOilOrder> list = null;
	private LayoutInflater layoutInflater;
	private Context context;
	
	
	public OrderListAdapter(List<FillOilOrder> list,Context context) {
		super();
		this.list = list;
		this.layoutInflater = LayoutInflater.from(context);
		this.context = context;
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

      
//	@Override
//	public View getView(final int position, View convertView, ViewGroup parent) {
//		// TODO Auto-generated method stub
//		ViewHolder viewHolder;
//		if(convertView == null){
//			viewHolder = new ViewHolder();
//			convertView = layoutInflater.inflate(R.layout.item_oderlist, null);
//			viewHolder.tv_oilname = (TextView) convertView.findViewById(R.id.tv_oilname);
//			viewHolder.order = (ImageView) convertView.findViewById(R.id.img_order);
//			viewHolder.orderstatus = (ImageView) convertView.findViewById(R.id.orderstatus);
//			viewHolder.tv_oiltype = (TextView) convertView.findViewById(R.id.tv_oiltype);
//			viewHolder.tv_oilprice = (TextView) convertView.findViewById(R.id.tv_oilprice);
//			viewHolder.tv_ordertime = (TextView) convertView.findViewById(R.id.tv_ordertime);
//			viewHolder.tv_tolalmoney = (TextView) convertView.findViewById(R.id.tv_tolalmoney);
//			viewHolder.delete = (ImageButton) convertView.findViewById(R.id.delete);
//			viewHolder.pay = (TextView) convertView.findViewById(R.id.pay);
//			convertView.setTag(viewHolder);
//		}else{
//			viewHolder = (ViewHolder) convertView.getTag();
//		}
//        System.out.println(list.get(position).oilBrandName);
//		   System.out.println("所有订单："+list.size()+list.toString());
//		if(list.get(position).oilBrandName.equals("中石化")){
//			viewHolder.order.setBackgroundResource(R.drawable.logo_for_station_detail_zhongshihua);
//		}else if(list.get(position).oilBrandName.equals("中石油")){
//			viewHolder.order.setBackgroundResource(R.drawable.logo_for_station_detail_zhongshiyou);
//		}else{
//			viewHolder.order.setBackgroundResource(R.drawable.logo_for_station_detail_daodaer);
//		}
//		
//		if (list.get(position).orderStatus.equals("0")) {//已完成
//			viewHolder.orderstatus.setBackgroundResource(R.drawable.corder);
//			viewHolder.delete.setVisibility(View.VISIBLE);
//			viewHolder.pay.setVisibility(View.GONE);
//		}else if (list.get(position).orderStatus.equals("1")) {//待支付
//			viewHolder.orderstatus.setBackgroundResource(R.drawable.zorder);
//			viewHolder.delete.setVisibility(View.GONE);
//			viewHolder.pay.setVisibility(View.VISIBLE);
//		}else if (list.get(position).orderStatus.equals("2")) {//待加油
//			viewHolder.orderstatus.setBackgroundResource(R.drawable.jorder);
//			viewHolder.delete.setVisibility(View.GONE);
//			viewHolder.pay.setVisibility(View.GONE);
//		}else {
//			viewHolder.orderstatus.setBackgroundResource(R.drawable.corder);
//		}
//		
//		viewHolder.tv_oilname.setText((String) list.get(position).gasStationName);
//		viewHolder.tv_oiltype.setText((String) list.get(position).oilType);
//		viewHolder.tv_oilprice.setText((String) list.get(position).oilPrice);
//		String ordertime = list.get(position).orderTime;
//		viewHolder.tv_ordertime.setText(ordertime.substring(0,ordertime.length()-2));
//		viewHolder.tv_tolalmoney.setText((String) list.get(position).totalMoney);
//		viewHolder.delete.setTag(position);  
//		viewHolder.delete.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				AlertDialog.Builder builder = new AlertDialog.Builder(context);
//				builder.setMessage("确定删除此订单吗？")
//						.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
//							
//							@Override
//							public void onClick(DialogInterface arg0, int arg1) {
//								// TODO Auto-generated method stub
//								 removeItem(position); 
//								 deleteOrder(position);
//							}
//						}).setNegativeButton("取消",new android.content.DialogInterface.OnClickListener() {
//							
//							@Override
//							public void onClick(DialogInterface arg0, int arg1) {
//								// TODO Auto-generated method stub
//								
//							}
//						} ).create().show();
//				 
//			}
//		});
//		
//		viewHolder.pay.setTag(position);
//		viewHolder.pay.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				System.out.println("更新的订单是："+list.get(position));
//				Intent intent = new Intent(context,EnsurePay.class);
//    			Bundle bundle = new Bundle();
//				bundle.putSerializable("orderlist", list.get(position));
//				intent.putExtras(bundle);
//				context.startActivity(intent);
//				
//				
//			}
//		});
//		return convertView;
//	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
			convertView = layoutInflater.inflate(R.layout.item_oderlist, null);
			TextView tv_oilname = (TextView) convertView.findViewById(R.id.tv_oilname);
			ImageView order = (ImageView) convertView.findViewById(R.id.img_order);
			ImageView orderstatus = (ImageView) convertView.findViewById(R.id.orderstatus);
			TextView tv_oiltype = (TextView) convertView.findViewById(R.id.tv_oiltype);
			TextView tv_oilprice = (TextView) convertView.findViewById(R.id.tv_oilprice);
			TextView tv_ordertime = (TextView) convertView.findViewById(R.id.tv_ordertime);
			TextView tv_tolalmoney = (TextView) convertView.findViewById(R.id.tv_tolalmoney);
			ImageButton delete = (ImageButton) convertView.findViewById(R.id.delete);
			TextView pay = (TextView) convertView.findViewById(R.id.pay);
        //System.out.println("所有订单："+list.size()+list.toString());
		
		if(list.get(position).oilBrandName.equals("中石化")){
			order.setBackgroundResource(R.drawable.logo_for_station_detail_zhongshihua);
		}else if(list.get(position).oilBrandName.equals("中石油")){
			order.setBackgroundResource(R.drawable.logo_for_station_detail_zhongshiyou);
		}else{
			order.setBackgroundResource(R.drawable.yzp);
		}
		
		if (list.get(position).orderStatus.equals("0")) {//已完成
			orderstatus.setBackgroundResource(R.drawable.corder);
			delete.setVisibility(View.VISIBLE);
			pay.setVisibility(View.GONE);
		}else if (list.get(position).orderStatus.equals("1")) {//待支付
			orderstatus.setBackgroundResource(R.drawable.zorder);
			delete.setVisibility(View.GONE);
			pay.setVisibility(View.VISIBLE);
		}else if (list.get(position).orderStatus.equals("2")) {//待加油
			orderstatus.setBackgroundResource(R.drawable.jorder);
			delete.setVisibility(View.GONE);
			pay.setVisibility(View.GONE);
		}else {
			orderstatus.setBackgroundResource(R.drawable.corder);
		}
		
		tv_oilname.setText((String) list.get(position).gasStationName);
		tv_oiltype.setText((String) list.get(position).oilType);
		tv_oilprice.setText((String) list.get(position).oilPrice);
		String ordertime = list.get(position).orderTime;
		tv_ordertime.setText(ordertime.substring(0,ordertime.length()-2));
		tv_tolalmoney.setText((String) list.get(position).totalMoney);
		
		//delete.setTag(position);  
		delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("确定删除此订单吗？")
						.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								 deleteOrder(position);
								 removeItem(position); 
							}
						}).setNegativeButton("取消",new android.content.DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								
							}
						} ).create().show();
				 
			}
		});
		
		//pay.setTag(position);
		pay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,EnsurePay.class);
    			Bundle bundle = new Bundle();
    			System.out.println("更新的订单是："+list.get(position));
				bundle.putSerializable("orderlist", list.get(position));
				intent.putExtras(bundle);
				((Activity) context).finish();
				((Activity) context).startActivity(intent);
				
			}
		});
		return convertView;
	}
	/**
	 * 删除listview项目
	 * @param position listitem位置
	 */
    public void removeItem(int position){  
        list.remove(position);  
        this.notifyDataSetChanged();  
    }  
    
   /**
    * 删除订单
    * @param position
    */
    public void deleteOrder(int position){  
    	final FillOilOrder  order =  list.get(position);
    	System.out.println("order"+order);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String path = "http://115.28.16.183:8080/HaoJiaYouOrder1/UpdateOilOrder";
				//String path = "http://202.193.75.94:8080/HaoJiaYouOrder1/UpdateOilOrder";
				Map<String, String> params = new HashMap<String, String>();
				System.out.println("删除order.orderID"+order.orderID);
				params.put("OrderID", order.orderID);
				params.put("Flag", "1");
				try {
					String msg = NetService.sendGetRequest(path,params, "utf-8");

					if (msg.toString().contains("success")) {
						System.out.println("服务器删除订单成功！");
					}

				} catch (Exception e) {
					// TODO Auto-generated catch
					// block
					e.printStackTrace();
				}
			}
		}).start();
		
    } 
    
	/**
	 * 更新listview项目
	 * @param position listitem位置
	 */
    public void updateItem(){  
    	list.add(null);
        this.notifyDataSetChanged();  
    } 
    /**
     * 更改订单状态
     * @param position
     */
    public void  updateOrderStatus(int position){  
    	final FillOilOrder  order =  list.get(position);
    	System.out.println("order"+order);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				//String path = "http://115.28.16.183:8080/HaoJiaYouOrder1/DeleteOilOrder";
				String path = "http://202.193.75.94:8080/HaoJiaYouOrder1/UpdateOilOrder";
				Map<String, String> params = new HashMap<String, String>();
				params.put("OrderID", order.orderID);
				params.put("OderStatus", order.orderStatus);
				params.put("Flag","2");
				try {
					String msg = NetService.sendGetRequest(path,params, "utf-8");

					if (msg.toString().contains("success")) {
						System.out.println("服务器更改订单状态成功！");
					}

				} catch (Exception e) {
					// TODO Auto-generated catch
					// block
					e.printStackTrace();
				}
			}
		}).start();
		
    } 
	 class ViewHolder{
		public ImageView order,orderstatus;
		public TextView tv_oilname,tv_oiltype,tv_oilprice,tv_ordertime,tv_tolalmoney,pay;
		public ImageButton delete;
	}
}
