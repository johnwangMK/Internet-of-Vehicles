package cn.guet.haojiayou.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import cn.guet.haojiayou.MainActivity;
import cn.guet.haojiayou.R;
import cn.guet.haojiayou.bean.CarInfo;
import cn.guet.haojiayou.db.CarInfoDao;
import cn.guet.haojiayou.utils.GetAssets;
import cn.guet.haojiayou.utils.NetService;

import com.carUserLogin.CarUserLogin1;
import com.carUserLogin.CheckLoginState;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MycarActivity extends Activity {

	TextView brand, model, plateNo, engineNo, carDoor, carSeat, mileage,
			oilConsumption, vehicleIDNo, enginePerformance,
			variatorPerformance, carLamp;
	ImageView logo;
	LinearLayout ll_addcar;
	ImageView add_car;
	ImageView img_back_mycar,imgnet;
	ListView lv_mycar;
	TextView tv1;
	CarInfo carInfo;// 得到扫描后的汽车信息
	String carJson;// 插入汽车信息json
	String carsJson;// 爱车列表json
	ArrayList<CarInfo> carlist;
	String userno;// 用户名
	CarInfoDao carInfoDao;
	SimpleAdapter sa;
	Handler cwjHandler;
	private View popLoader,badnet;
	boolean hasNetwork;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycar);

		add_car = (ImageView) findViewById(R.id.img_add_car);
		img_back_mycar = (ImageView) findViewById(R.id.img_back_mycar);
		lv_mycar = (ListView) findViewById(R.id.lv_mycar);
		ll_addcar = (LinearLayout) findViewById(R.id.ll_addcar);
		 popLoader = findViewById(R.id.popLoader_car);
		 popLoader.setVisibility(View.VISIBLE);
		 badnet = findViewById(R.id.badnet_car);
		 imgnet =  (ImageView) findViewById(R.id.imgnet_car);
		 
		add_car.setOnClickListener(new ClickListener());
		ll_addcar.setOnClickListener(new ClickListener());
		img_back_mycar.setOnClickListener(new ClickListener());
		carInfoDao = new CarInfoDao(this);
		cwjHandler = new Handler();
		initeview();

	}

	public void initeview() {
		
		 hasNetwork = NetService.isNetworkConnected(this);
	    if (hasNetwork) {
	    	 badnet.setVisibility(View.GONE);
					userno = CheckLoginState.username;
//	    	 if (CheckLoginState.check(MycarActivity.this)) {//判断是否登录
//					
//					if(MainActivity.mineFragment != null)  {
//						MainActivity.mineFragment.setLonginlogo();
//					}
//					new myAnyTask().execute();
//					
//		    	 }else {
//						Toast.makeText(MycarActivity.this, "请先登录！", Toast.LENGTH_LONG).show();
//						startActivity(new Intent(MycarActivity.this,CarUserLogin1.class));
//						finish();
//					}
				new myAnyTask().execute();
				
	    	 }else{
			 popLoader.setVisibility(View.GONE);
				Toast.makeText(MycarActivity.this, "网络不佳，请检查网络连接！",Toast.LENGTH_LONG).show();
			 badnet.setVisibility(View.VISIBLE);
		}

	}

	class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.ll_addcar:
			case R.id.img_add_car:
						startActivityForResult(new Intent(MycarActivity.this,
								CaptureActivity.class), 100);// 跳转到扫描界面

				// displayDialog();
				break;

			case R.id.img_back_mycar:
				finish();
				break;

			default:
				break;
			}
		}

	}

	class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			if (null != carlist) {

				Intent intent = new Intent(MycarActivity.this,
						CarDetailedInfo.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("carInfo", carlist.get(position));
				intent.putExtras(bundle);
				startActivity(intent);

			}

		}

	}

	// 长按删除
	class ItemLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int positon, long id) {
			// TODO Auto-generated method stub

			final String plateNo = carlist.get(positon).plateNo;

			AlertDialog.Builder dialog = new AlertDialog.Builder(
					MycarActivity.this);
			dialog.setTitle("删除此辆汽车的信息吗？")
					.setPositiveButton(
							"确定",
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									new Thread(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											String path = "http://115.28.16.183:8080/HaoJiaYouOrder1/DeleteCarInfo";
											Map<String, String> params = new HashMap<String, String>();
											params.put("plateNo", plateNo);
											try {
												String msg = NetService
														.sendGetRequest(path,
																params, "utf-8");

												if (msg.toString().contains(
														"success")) {
													carInfoDao
															.delelteCarByPlateNo(plateNo);
													initeview();
													System.out.println("删除刷新"
															+ carlist
																	.toString());
													// sa.notifyDataSetChanged();//通知刷新listview
												}

											} catch (Exception e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}
									}).start();

								}

							})
					.setNegativeButton(
							"取消",
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
								}
							});

			dialog.create().show();

			return false;
		}

	}

	class myAnyTask extends
			AsyncTask<Void, Void, ArrayList<HashMap<String, Object>>> {

		@Override
		protected ArrayList<HashMap<String, Object>> doInBackground(
				Void... arg0) {
			// TODO Auto-generated method stub
			// ArrayList<CarInfo> carlist = new ArrayList<CarInfo>();
			carlist = getCars();// 汽车列表

			// 解析的汽车品牌和logo图片
			ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
			if (null != carlist) {

				for (int i = 0; i < carlist.size(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("品牌", carlist.get(i).brand);
					map.put("车牌", carlist.get(i).plateNo);
					String logopath = "carimages/" + carlist.get(i).logo;
					// String logopath = "carimages/aa.jpg";
					Drawable drawable = GetAssets.getImageFromAssetsFile(
							logopath, MycarActivity.this);
					map.put("图片", drawable);
					list.add(map);
				}

				return list;
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, Object>> result) {
			// TODO Auto-generated method stub

			if (null == result|| result.isEmpty()) {
				Toast.makeText(MycarActivity.this, "暂无爱车信息，请扫码添加车辆！",
						Toast.LENGTH_LONG).show();
			} else {

				System.out.println("这是：" + result.toString());
				sa = new SimpleAdapter(MycarActivity.this, result,
						R.layout.item_carlist,
						new String[] { "品牌", "图片", "车牌" }, new int[] {
								R.id.tv_brand, R.id.img_car_logo,
								R.id.tv_plateNo });

				// 如果是要在listView中实现图片的显示，那么listview必须使用自定义适配器（自定义一个适配器，
				// 重新布局listview，给图片显示一个容器，比如ImageView之类的.这段代码是现实自定义位图所必需的
				sa.setViewBinder(new ViewBinder() {
					public boolean setViewValue(View view, Object data,
							String textRepresentation) {
						if (view instanceof ImageView
								&& data instanceof Drawable) {
							ImageView iv = (ImageView) view;
							iv.setImageDrawable((Drawable) data);
							return true;
						} else
							return false;
					}
				});

				lv_mycar.setAdapter(sa);
				lv_mycar.setOnItemClickListener(new ItemClickListener());
				lv_mycar.setOnItemLongClickListener(new ItemLongClickListener());

			}
		}

	}

	/**
	 * 获取我的爱车列表
	 */
	public ArrayList<CarInfo> getCars() {

		String path = "http://115.28.16.183:8080/HaoJiaYouOrder1/GetCarInfo";
	//	 String path = "http://202.193.75.94:8080/HaoJiaYouOrder1/GetCarInfo";
		//userno = "13912200019";
		Map<String, String> params = new HashMap<String, String>();
		params.put("UserNo", userno);

		try {
			carsJson = NetService.sendGetRequest(path, params, "utf-8");
			System.out.println("响应的值是：" + carsJson);

			if (carsJson.toString().equals("404")
					|| carsJson.toString().equals("500")) {
				carsJson = null;
			}

			cwjHandler.post(mUpdateResults); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String msg = NetService.ExceptionCode(e);
			System.out.println("网络异常：" + msg);
			carsJson = null;
			cwjHandler.post(mUpdateResults); // 告诉UI线程可以更新结果了
			Looper.prepare();
			Toast.makeText(MycarActivity.this,"服务器异常，请联系我们！",Toast.LENGTH_LONG).show();
			Looper.loop();
		}

		Gson gson = new Gson();

		if (null != carsJson) {

			carlist = gson.fromJson(carsJson,
					new TypeToken<ArrayList<CarInfo>>() {
					}.getType());
			return carlist;
		}

		return null;
	}
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			popLoader.setVisibility(View.GONE);
		}
	};
	/**
	 * 
	 * 自定义汽车信息确认框
	 * 
	 */
	public void displayDialog(final CarInfo newcarInfo) {
		LayoutInflater inflater = getLayoutInflater();
		LinearLayout carinfoForm = (LinearLayout) inflater.inflate(
				R.layout.carinfo_table_dialog, null);

		brand = (TextView) carinfoForm.findViewById(R.id.brand);
		model = (TextView) carinfoForm.findViewById(R.id.model);
		vehicleIDNo = (TextView) carinfoForm.findViewById(R.id.vehicleIDNo);
		plateNo = (TextView) carinfoForm.findViewById(R.id.plateNo);
		carDoor = (TextView) carinfoForm.findViewById(R.id.carDoor);
		carSeat = (TextView) carinfoForm.findViewById(R.id.carSeat);
		oilConsumption = (TextView) carinfoForm
				.findViewById(R.id.oilConsumption);
		engineNo = (TextView) carinfoForm.findViewById(R.id.engineNo);
		mileage = (TextView) carinfoForm.findViewById(R.id.mileage);
		enginePerformance = (TextView) carinfoForm
				.findViewById(R.id.enginePerformance);
		variatorPerformance = (TextView) carinfoForm
				.findViewById(R.id.variatorPerformance);
		carLamp = (TextView) carinfoForm.findViewById(R.id.carLamp);

		logo = (ImageView) carinfoForm.findViewById(R.id.logo);

		brand.setText(carInfo.brand);
		model.setText(carInfo.model);
		plateNo.setText(carInfo.plateNo);
		vehicleIDNo.setText(carInfo.vehicleIDNo);
		carDoor.setText(carInfo.carDoor + "门");
		carSeat.setText(carInfo.carSeat + "座");
		oilConsumption.setText(carInfo.oilConsumption);
		engineNo.setText(carInfo.engineNo);
		mileage.setText(carInfo.mileage + "公里");
		enginePerformance.setText(carInfo.enginePerformance);
		variatorPerformance.setText(carInfo.variatorPerformance);
		carLamp.setText(carInfo.carLamp);

		/**
		 * 设置汽车logo图片
		 */
		String logopath = "carimages/" + carInfo.logo;
		Drawable drawable = GetAssets.getImageFromAssetsFile(logopath, this);
		logo.setImageDrawable(drawable);

		AlertDialog.Builder dialog = new AlertDialog.Builder(MycarActivity.this);

		dialog.setView(carinfoForm)
				.setPositiveButton("确定",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// 向服务器传递汽车json数据
								new Thread(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										String path = "http://115.28.16.183:8080/HaoJiaYouOrder1/AddCarInfo";
									//	 String path = "http://202.193.75.94:8080/HaoJiaYouOrder1/AddCarInfo";
										String str = null;
										try {
											
											Gson gson = new Gson();
											String newCarJson = gson.toJson(newcarInfo);											
											str = NetService.sendData(path,
													newCarJson);// 向服务器传输数据
											
											if (str.contains("success")) {
												Looper.prepare();
												Toast.makeText(
														MycarActivity.this,
														"添加车辆信息成功！",
														Toast.LENGTH_LONG)
														.show();
												Looper.loop();
											} else if(str.contains("failure")) {
												Looper.prepare();
												Toast.makeText(
														MycarActivity.this,
														"车辆信息已存在，不能添加！",
														Toast.LENGTH_LONG)
														.show();
												Looper.loop();
											} else {
												Looper.prepare();
												Toast.makeText(
														MycarActivity.this,
														"添加车辆信息异常！",
														Toast.LENGTH_LONG)
														.show();
												Looper.loop();
											}

										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();

										}
									}
								}).start();
								initeview();// 初始化界面
							}
						})
				.setNegativeButton("取消",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub

							}
						});

		// Window win = MycarActivity.this.getWindow();
		// LayoutParams params = new LayoutParams(80,60);
		// win.setAttributes(params);
		// // selectDialog.setCanceledOnTouchOutside(true);
		dialog.create().show();

	}

	// 返回结果处理
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == 20) {
			carJson = data.getExtras().getString("result");
			//tv1 = (TextView) MycarActivity.this.findViewById(R.id.tv1);
			//tv1.setText(carJson);
			
			String substr = carJson.substring(0, 4);
			Gson gson = new Gson();
			// 有问题
			
			if (substr.equals("http")) {

				WebView web = new WebView(MycarActivity.this);
				web.loadUrl(carJson);
				setContentView(web);
				Toast.makeText(MycarActivity.this, "success!", 0).show();
			} else {
				
	            try {
	    			carInfo = gson.fromJson(carJson, CarInfo.class);// 把汽车json转换成汽车信息实体
	    			carInfo.userNo = userno;
	    			displayDialog(carInfo);
	    			
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
            
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
