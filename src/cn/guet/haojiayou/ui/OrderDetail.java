package cn.guet.haojiayou.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import cn.guet.haojiayou.R;
import cn.guet.haojiayou.bean.FillOilOrder;
import cn.guet.haojiayou.utils.CreateQRCode;
import cn.guet.haojiayou.utils.NetService;
import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class OrderDetail extends Activity {

	private TextView orderID,ordertime,odername,phonenum,arrivetime,gas_name,type,price,plateNo,summoney;
	private TextView orderagain,orderconcel;
	private ImageButton back,la_long;
	private FillOilOrder order,newOrder;
	private ImageView order_QRcode ;
	private String QRgson;
	private Bitmap scanbitmap;
	private String orderstatus;
	private boolean isCheck;
	LinearLayout ll_selectdate, ll_selecttime;
	ImageButton bt_datedropdown, bt_timedropdown;
	TextView tv_date,	tv_time;
	Time time;	
	int flag = 0;
	String orderJson;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderdetail);
        initeData();
        initeView();
	}



	private void initeView() {
		back = (ImageButton) findViewById(R.id.back_order_lv);
		LinearLayout contact_gs =  (LinearLayout) findViewById(R.id.contact_gs);
//		contact_gs.setOnClickListener(new ClickListener());
		orderID = (TextView) findViewById(R.id.orderID);
		ordertime = (TextView) findViewById(R.id.ordertime);
		odername = (TextView) findViewById(R.id.ordername);
		phonenum = (TextView) findViewById(R.id.phonenum);
		arrivetime = (TextView) findViewById(R.id.arrivetime);
		gas_name = (TextView) findViewById(R.id.gas_name);
		type = (TextView) findViewById(R.id.dtype);
		price = (TextView) findViewById(R.id.dprice);
		plateNo = (TextView) findViewById(R.id.orderdetail_carinfo);
		summoney = (TextView) findViewById(R.id.order_total_price);
		orderagain = (TextView) findViewById(R.id.order_again);
		orderconcel = (TextView) findViewById(R.id.cancel);
		la_long = (ImageButton) findViewById(R.id.la_long);	
		
		orderstatus = order.orderStatus;
	     //Toast.makeText(this, "状态order.orderStatus"+order.orderStatus, Toast.LENGTH_LONG).show();
		if (orderstatus .equals("1")||orderstatus .equals("2")) {//待支付，待加油
			orderagain.setVisibility(View.GONE);
			orderconcel.setVisibility(View.VISIBLE);
		}else if (orderstatus .equals("0")) { //已完成
			orderconcel.setVisibility(View.GONE);
			orderagain.setVisibility(View.VISIBLE);
		}
		orderID.setText(order.orderID);
		ordertime.setText(order.orderTime.substring(0,order.orderTime.length()-2));
		odername.setText(order.carUserName);
		arrivetime.setText(order.arriveTime.substring(0,order.orderTime.length()-2));
		phonenum.setText(order.phoneNum);
		gas_name.setText(order.gasStationName);
		type.setText(order.oilType);
		price.setText(order.oilPrice);
		plateNo.setText(order.plateNo);
		summoney.setText(order.totalMoney);
		
		order_QRcode = (ImageView) findViewById(R.id.order_QRcode);
		order_QRcode.setOnClickListener(new ClickListener());
		back.setOnClickListener(new ClickListener());
		orderconcel.setOnClickListener(new ClickListener());
		orderagain.setOnClickListener(new ClickListener());
	}

	private void initeData() {
		time = new Time();  
		time.setToNow();
     	Bundle bundle = this.getIntent().getExtras();
     	if (null != bundle) {
     		order = (FillOilOrder) bundle.getSerializable("orderlist");
          //  System.out.println("order"+order);
            Gson gson = new Gson();
            QRgson = gson.toJson(order);
            scanbitmap =  CreateQRCode.createQRImage(QRgson);
		}
    	   	
	}


	
	class ClickListener implements OnClickListener{
		Intent intent;
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			
			case R.id.back_order_lv:
				intent = new Intent(OrderDetail.this,OrderListActivity.class);
				intent.putExtra("status", order.orderStatus);
                  finish();
                  startActivity(intent);
      			overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);	
      			break;	
      			
			case R.id.order_QRcode:
				View view = LayoutInflater.from(OrderDetail.this).inflate(R.layout.orderbitmapdialog, null);
				ImageView img = (ImageView) view.findViewById(R.id.order_bitmap);
				showQRdecode(img,view);
				break;
				
			case R.id.cancel:

				AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetail.this);
				builder.setMessage("您确定取消预约吗？")
						.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								deleteOrder();
								Intent intent = new Intent(OrderDetail.this,OrderListActivity.class);
								intent.putExtra("status", order.orderStatus);
								finish();
								startActivity(intent);
								overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
							}
						}).setNegativeButton("取消",new android.content.DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								
							}
						} ).create().show();
    			break;
    			
			case R.id.order_again:
				Dialog dialog = createDialog();
				dialog.show();
				break;
			
			case R.id.abt_datedropdown:// 选择日期
			case R.id.all_selectdate:
				onCreateDialog(R.id.all_selectdate).show();
				break;

			case R.id.abt_timedropdown:// 选择时间
			case R.id.all_selecttime:
				onCreateDialog(R.id.all_selecttime).show();
				break;
				
			default:
				break;
			}
		}
		
	}
	/**
	 * 重新选择预计到达加油时间
	 * @return
	 */
	private Dialog createDialog(){
		
		 View view =  getLayoutInflater().inflate(R.layout.activity_order_again, null);
		 RelativeLayout r = (RelativeLayout) view.findViewById(R.id.l_arrivetime);
		 ll_selectdate = (LinearLayout) view.findViewById(R.id.all_selectdate);
		 ll_selecttime = (LinearLayout) view.findViewById(R.id.all_selecttime);
		 bt_datedropdown = (ImageButton) view.findViewById(R.id.abt_datedropdown);
		 bt_timedropdown = (ImageButton) view.findViewById(R.id.abt_timedropdown);
		 tv_date = (TextView) view.findViewById(R.id.atv_date);//日期
		 tv_time = (TextView)view. findViewById(R.id.atv_time);//时间
	     ll_selectdate.setOnClickListener(new ClickListener());
	     ll_selecttime.setOnClickListener(new ClickListener());
		 bt_datedropdown.setOnClickListener(new ClickListener());
		 bt_timedropdown.setOnClickListener(new ClickListener());
		 
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);	
		  
		  builder.setTitle("请重新选择预计到达加油时间！").setView(r)
		  .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
		   
		   @Override
		   public void onClick(DialogInterface dialog, int which) {
		    //do upgrade work
			isCheck = checkArriveTime();
			if (isCheck) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetail.this);
				String msg = "您的新预约加油详情如下:\n 是否确认？";
				View view =  newOrder();	
				builder.setView(view)
				.setTitle(msg).setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						sendOrder();
					}
				}).setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
				}).create().show();
				
			}
		   }
		  });
		  builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
		   
		   @Override
		   public void onClick(DialogInterface dialog, int which) {

		   }
		  });
		  return builder.create();
		 }
	
	private boolean checkArriveTime(){
		 if (tv_date.getText().toString().trim().equals("")
				||tv_time.getText().toString().trim().equals("")) {
            Toast.makeText(OrderDetail.this, "请选择预计到达加油时间！", 2000).show(); 
			return false;
		}  else {
			return true;
		}
		
	}
	/**
	 * 再来一单
	 * @return
	 */
	private View newOrder(){
		
		View view =  getLayoutInflater().inflate(R.layout.orderdetail, null);
		RelativeLayout r_back =  (RelativeLayout) view.findViewById(R.id.r_back);
		RelativeLayout r_orderid =  (RelativeLayout) view.findViewById(R.id.r_orderid);
		RelativeLayout r_again_cancel =  (RelativeLayout) view.findViewById(R.id.r_again_cancel);
        LinearLayout contact_gs =  (LinearLayout) view.findViewById(R.id.contact_gs);
		LinearLayout l_ordertime =  (LinearLayout) view.findViewById(R.id.l_ordertime);
		
		TextView odername = (TextView)view. findViewById(R.id.ordername);
		TextView phonenum = (TextView) view.findViewById(R.id.phonenum);
		TextView arrivetime = (TextView) view.findViewById(R.id.arrivetime);
		TextView gas_name = (TextView) view.findViewById(R.id.gas_name);
		TextView type = (TextView) view.findViewById(R.id.dtype);
		TextView price = (TextView)view. findViewById(R.id.dprice);
		TextView plateNo = (TextView)view. findViewById(R.id.orderdetail_carinfo);
		TextView summoney = (TextView) view.findViewById(R.id.order_total_price);
		ImageButton la_long = (ImageButton)view. findViewById(R.id.la_long);	
		
		r_back.setVisibility(View.GONE);
		r_orderid.setVisibility(View.GONE);
		l_ordertime.setVisibility(View.GONE);
		la_long.setVisibility(View.GONE);
		contact_gs.setVisibility(View.GONE);
		r_again_cancel.setVisibility(View.GONE);
				
		odername.setText(order.carUserName);
		phonenum.setText(order.phoneNum);
		gas_name.setText(order.gasStationName);
		type.setText(order.oilType);
		price.setText(order.oilPrice);
		plateNo.setText(order.plateNo);
		summoney.setText(order.totalMoney);	
		String date = tv_date.getText().toString().trim();
		String time = tv_time.getText().toString().trim();
		arrivetime.setText(date+"  "+time);
		
		newOrder = new FillOilOrder(getNow(), order.carUserName, order.phoneNum, order.gasStationAddress, order.gasStationName, order.oilBrandName, order.oilType, order.oilPrice, date+"  "+time, order.totalMoney, order.plateNo, "1");
	    return view;
	}
	
	
	/**
	 * 发送新订单
	 */
		public void sendOrder() {
	 
			System.out.println("豌豆是：" + newOrder.toString());
			Gson gson = new Gson();
			orderJson = gson.toJson(newOrder);
			System.out.println(orderJson);
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					 String path = "http://115.28.16.183:8080/HaoJiaYouOrder1/FillOilInfo";
					//String path = "http://202.193.75.94:8080/HaoJiaYouOrder1/FillOilInfo";
					try {
						String msg = NetService.sendData(path, orderJson);// 向服务器传输数据
	                    System.out.println("插入订单服务器消息"+msg);
						if (msg.contains("success")) {
							Looper.prepare();
                            Toast.makeText(OrderDetail.this, "预约成功！", 2000).show(); 
							Looper.loop();
						} else{
							Looper.prepare();
                            Toast.makeText(OrderDetail.this, "预约失败！", 2000).show(); 
							Looper.loop();
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

					}

				}
			}).start();
		}
	/**
	 * 日期和时间选择对话框
	 */
	public Dialog onCreateDialog(int id) { 
	    Dialog dialog = null; 
	    
	    switch (id) { 
	    case R.id.all_selectdate: 
	         DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {  
	                @Override  
	                public void onDateSet(DatePicker datePicker,int year, int month, int dayOfMonth) {  

	                 if(year>getYear()){ //设置年大于当前年，直接设置，不用判断下面的 
	                   
//	                     time.set(dayOfMonth, month, year);	                     
//	                     tv_date.setText(time.format("%Y-%m-%d")); 
	                     tv_date.setText(year + "-"+(month+1)+"-"+dayOfMonth); 
	                     flag = 1; 
	                 }else if(year == getYear()){   //设置年等于当前年，则向下开始判断月 
	                     if((month+1) > getMonth()){ //设置月等于当前月，直接设置，不用判断下面的 
	                         flag = 1; 
//	                         time.set(dayOfMonth, month, year);		                     
//		                     tv_date.setText(time.format("%Y-%m-%d")); 
	                         tv_date.setText(year + "-"+(month+1)+"-"+dayOfMonth);  
	                     }else if((month+1) == getMonth()){     //设置月等于当前月，则向下开始判断日 
	                         if(dayOfMonth > getDay()){          //设置日大于当前月，直接设置，不用判断下面的 
	                             flag = 1; 
//	                             time.set(dayOfMonth, month, year);		                     
//			                     tv_date.setText(time.format("%Y-%m-%d")); 
	                             tv_date.setText(year + "-"+(month+1)+"-"+dayOfMonth);  
	                         }else if(dayOfMonth == getDay()){  //设置日等于当前日，则向下开始判断时 
	                             flag = 2; 
//	                             time.set(dayOfMonth, month, year);	                     
//			                     tv_date.setText(time.format("%Y-%m-%d")); 
	                             tv_date.setText(year + "-"+(month+1)+"-"+dayOfMonth);
	                         }else{     //设置日小于当前日，提示重新设置 
	                             flag = 3; 
	                             Toast.makeText(OrderDetail.this, "当前日不能小于今日,请重新设置", 2000).show(); 
	                         } 
	                     }else{         //设置月小于当前月，提示重新设置 
	                         flag = 3; 
	                         Toast.makeText(OrderDetail.this, "当前月不能小于今月，请重新设置", 2000).show(); 
	                     } 
	                 }else{             //设置年小于当前年，提示重新设置 
	                     flag = 3; 
	                     Toast.makeText(OrderDetail.this, "当前年不能小于今年，请重新设置", 2000).show(); 
	                 } 
	                  
	                 if(flag == 3){ 
	                     datePicker.init(getYear(), (getMonth()-1), getDay(), new DatePicker.OnDateChangedListener() { 
	                             
	                            @Override 
	                            public void onDateChanged(DatePicker view, int year, int monthOfYear, 
	                                    int dayOfMonth) { 
//	                             time.set(dayOfMonth, monthOfYear, year);	                     
//			                     tv_date.setText(time.format("%Y-%m-%d")); 
	                            	 tv_date.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth ); 
	                            } 
	                        }); 
	                 } 
	                }  
	            };  
	         dialog = new DatePickerDialog(this, dateListener, getYear(), (getMonth()-1), getDay());  
	            break; 
	            
	    case R.id.all_selecttime: 
	        TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() { 
	             
	            @Override 
	            public void onTimeSet(TimePicker view, int hourOfDay, int minute) { 
	            	
	                switch (flag) { 
	                case 1:         //设置日期在当前日期之后，直接设置时间，不用判断                     
                         if(minute<10){
                        	 tv_time.setText(hourOfDay+":"+"0"+minute); 
                         } else{
	                	      tv_time.setText(hourOfDay+":"+minute); }
	                    break; 
	                    
	                case 2:         //设置日期等于当前日期之后，判断时和分 
	                    if(hourOfDay>getHour()){ 
	                    	
	                         if(minute<10){
	                        	 tv_time.setText(hourOfDay+":"+"0"+minute); 
	                         } else{
		                	      tv_time.setText(hourOfDay+":"+minute); }
	                    }else if(hourOfDay == getHour()){ 
	                        if(minute>getMinute()){ 
	                        	
	                            if(minute<10){
	                           	 tv_time.setText(hourOfDay+":"+"0"+minute); 
	                            } else{
	   	                	      tv_time.setText(hourOfDay+":"+minute); }
	                            
	                        }else{ 
	                           
	                            if(minute<10){
		                           	 tv_time.setText(getHour()+":"+"0"+getMinute());
		                            } else{
			                            tv_time.setText(getHour()+":"+getMinute()); }
	                            
	                            Toast.makeText(OrderDetail.this, "请选择大于现在时刻的分钟", 2000).show(); 
	                        } 
	                    }else{ 
                            if(minute<10){
	                           	 tv_time.setText(getHour()+":"+"0"+getMinute());
	                            } else{
		                            tv_time.setText(getHour()+":"+getMinute()); } 
	                        Toast.makeText(OrderDetail.this, "请选择大于现在时刻的小时", 2000).show(); 
	                    } 
	                    break; 
	                    
	                case 3:         //设置日期等于当前日期之前，提示日期还未设置正确，不能设置时间 
                        if(minute<10){
                          	 tv_time.setText(getHour()+":"+"0"+getMinute());
                           } else{
	                            tv_time.setText(getHour()+":"+getMinute()); }
                        
	                    Toast.makeText(OrderDetail.this, "请先设置正确的日期", 2000).show(); 
	                    break; 
	                 
	                default: 
	                    break; 
	                } 
	            } 
	        }; 
	        dialog = new TimePickerDialog(this, timeListener, getHour(),getMinute(), true); 
	        break; 

	    default: 
	        break; 
	    } 
	    return dialog; 

	}
	  
	/**
	 * 获取系统当前时间 年-月-日 时:分:秒
	 * 
	 * @return
	 */
	public String getNow() {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		return df.format(new Date());
	}
	
	public int getYear(){
		time.setToNow();
		return time.year;
	}
	public int getMonth(){
		time.setToNow();
		return time.month+1;
	}
	
	public int getDay(){
		time.setToNow();
		return time.monthDay;
	}
	public int getHour(){
		time.setToNow();
		return time.hour;
	}
	public int getMinute(){
		time.setToNow();
		return time.minute;
	}
	   /**
	    * 取消订单
	    * @param position
	    */
	    public void deleteOrder(){  

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
							System.out.println("服务器取消订单成功！");
						}

					} catch (Exception e) {
						// TODO Auto-generated catch
						// block
						e.printStackTrace();
						String msg = NetService.ExceptionCode(e);
						System.out.println("网络异常：" + msg);						
						Looper.prepare();
						Toast.makeText(OrderDetail.this,"服务器异常，请联系我们！",Toast.LENGTH_LONG).show();
						Looper.loop();
					}
				}
			}).start();
			
	    } 
	private void showQRdecode(ImageView img,View view) {
		
		img.setImageBitmap(scanbitmap);
		
		new AlertDialog.Builder(OrderDetail.this)
			//.setTitle("订单二维码")
			.setView(view).show();

	}
}
