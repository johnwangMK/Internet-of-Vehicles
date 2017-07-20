package cn.guet.haojiayou.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.carUserLogin.CarUserLogin1;
import com.carUserLogin.CheckLoginState;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.guet.haojiayou.R;
import cn.guet.haojiayou.adapter.OrderListAdapter;
import cn.guet.haojiayou.bean.FillOilOrder;
import cn.guet.haojiayou.ui.QueryMycarFragment.myAnyTask;
import cn.guet.haojiayou.utils.NetService;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class OrderListActivity extends Activity {

	private ListView lv;
	private OrderListAdapter oAdaptert;
	private ArrayList<FillOilOrder> oderlist;
	private String userno;//用户名
	private String ordersJson;//订单列表json
	private TextView ordertitle;
	private ImageView back,imgnet;
	private String status = "100";
	private View popLoader,badnet_order;
	boolean hasNetwork;
	Handler cwjHandler;
	// 保持对Task的引用
	myAnyTask task;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_list);
		cwjHandler = new Handler();
		initeView();
	}

	private void initeView() {

		 hasNetwork = NetService.isNetworkConnected(this);
		 lv = (ListView) findViewById(R.id.lv_order);
		 ordertitle = (TextView) findViewById(R.id.ordertitle);
		 popLoader = findViewById(R.id.popLoader_order);
		 popLoader.setVisibility(View.VISIBLE);
		 badnet_order = findViewById(R.id.badnet_order);
		 imgnet =  (ImageView) findViewById(R.id.imgnet_order);
		 back = (ImageView) findViewById(R.id.back_order);
		 back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
		    	if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
		    	task.cancel(true); // 如果Task还在运行，则先取消它
		    	}
			}
		});
		 
		 imgnet .setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			    if (hasNetwork) {
			    	if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
				    	task.cancel(true); // 如果Task还在运行，则先取消它
				    	}
			    	task.execute();
				}
			}
		});
		 Bundle bundle = this.getIntent().getExtras();
		 if (null != bundle) {	
			 status = this.getIntent().getStringExtra("status");
		}
		 if (status.equals("1")) {//待支付
			 ordertitle.setText("待支付订单");
			 }else if (status.equals("2")) {//待加油
					ordertitle.setText("待加油订单");
			 }else if (status.equals("0")) {//已完成
					ordertitle.setText("已完成订单");
			 }
		
	    	if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
	    	task.cancel(true); // 如果Task还在运行，则先取消它
	    	}else task = new myAnyTask();
	    if (hasNetwork) {
	    	 badnet_order.setVisibility(View.GONE);
	    	 if (CheckLoginState.check(this)) {//判断是否登录
				userno = CheckLoginState.username;
				task.execute();
			}else {
				showToast("请先登录！");
      			//startActivity(new Intent(OrderListActivity.this,CarUserLogin1.class));
			}
	    	 
		}else{
			 popLoader.setVisibility(View.GONE);
			 showToast("网络不佳，请检查网络连接！");
			 badnet_order.setVisibility(View.VISIBLE);
		}
	}
	
    class myAnyTask extends AsyncTask<Void, Void, ArrayList<FillOilOrder>>{

    	@Override
    	protected ArrayList<FillOilOrder> doInBackground(Void... arg0) {
    		// TODO Auto-generated method stub
    		oderlist = getOderlistByUno();
    		System.out.println("status"+status);
    		if (null!= oderlist) {
    			
					if (status.equals("1")) {//待支付
						ArrayList<FillOilOrder> l_selectorder = new ArrayList<FillOilOrder>();
						for (FillOilOrder order : oderlist) {
							if (order.orderStatus.equals("1")) {
								l_selectorder .add(order);
							}
					    }
						oderlist = l_selectorder;
					return l_selectorder;
				}else if (status.equals("2")) {//待加油
					ArrayList<FillOilOrder> l_selectorder = new ArrayList<FillOilOrder>();
					for (FillOilOrder order : oderlist) {
						if (order.orderStatus.equals("2")) {
							l_selectorder .add(order);
						}
				    }
					oderlist = l_selectorder;
				return l_selectorder;
			}else if (status.equals("0")) {//已完成
				ArrayList<FillOilOrder> l_selectorder = new ArrayList<FillOilOrder>();
				for (FillOilOrder order : oderlist) {
					if (order.orderStatus.equals("0")) {
						l_selectorder .add(order);
					}
			     }
				oderlist = l_selectorder;
			return l_selectorder;
		}else if (status.equals("100")) return oderlist;//全部订单
    						
			}	
    		return null;  		
    	}

		@Override
		protected void onPostExecute(ArrayList<FillOilOrder> result) {
			// TODO Auto-generated method stub
			
            

			if(null== result|| result.isEmpty() ){
				if (status.equals("1")) {
					showToast("您暂无待支付订单哦！");
				 }else if (status.equals("2")) {
					showToast("您暂无待加油订单哦！");
				 }else if (status.equals("0")) {
					showToast("您暂无已完成订单哦！");
				 }else if (status.equals("100")) {
					showToast("您暂无预约订单哦！");
				 }
				
			    }else{	
				
				System.out.println("这是："+result.toString());				
				 oAdaptert = new OrderListAdapter(result, OrderListActivity.this);
				 lv.setAdapter(oAdaptert);
			     lv.setOnItemClickListener(new ItemClickListener());				
			
			}
		}
    	
    	
    }
	private ArrayList<FillOilOrder> getOderlistByUno() {

		//String path = "http://202.193.74.157:8080/HaoJiaYouOrder1/GetOilOrderInfo";
		String path = "http://115.28.16.183:8080/HaoJiaYouOrder1/GetOilOrderInfo";
		//userno = "13912200019";
		Map<String, String> params = new HashMap<String, String>();
		params.put("UserNo", userno);

		try {
			ordersJson = NetService.sendGetRequest(path, params, "utf-8");
			//System.out.println("响应的值是：" + ordersJson);

			if (ordersJson.toString().equals("404")|| ordersJson.toString().equals("500")) {
				ordersJson = null;
			}
			cwjHandler.post(mUpdateResults); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String emsg = NetService.ExceptionCode(e);
			System.out.println("网络异常：" + emsg);
			ordersJson = null;
			cwjHandler.post(mUpdateResults); // 告诉UI线程可以更新结果了
			Looper.prepare();
			Toast.makeText(OrderListActivity.this,"服务器异常，请联系我们！",Toast.LENGTH_LONG).show();
			Looper.loop();
		}
		Gson gson = new Gson();
		System.out.println("ordersJson1"+ordersJson);
		if (null != ordersJson) {

			oderlist = gson.fromJson(ordersJson,
					new TypeToken<ArrayList<FillOilOrder>>() {
					}.getType());
			System.out.println("oderlist"+oderlist.toString());
			return oderlist;
		}

		return null;
	}

    class ItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
    		if (null != oderlist) {
				
    			Intent intent = new Intent(OrderListActivity.this,OrderDetail.class);
    			Bundle bundle = new Bundle();
				bundle.putSerializable("orderlist", oderlist.get(position));
				
				intent.putExtras(bundle);
				finish();
				startActivity(intent);
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

			}
			//showToast("油品牌名字："+oderlist.get(position).oilBrandName);
			
		}
    	
    }
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			popLoader.setVisibility(View.GONE);
		}
	};
	private void showToast(String msg){
		Toast.makeText(OrderListActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
	public boolean isNetworkConnected() {
		// 判断网络是否连接
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		return mNetworkInfo != null && mNetworkInfo.isAvailable();
	}
}
