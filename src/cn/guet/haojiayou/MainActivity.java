package cn.guet.haojiayou;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.carUserLogin.CheckLoginState;
import com.cheshouye.api.client.WeizhangIntentService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushManager;
import cn.guet.haojiayou.bean.CarInfo;
import cn.guet.haojiayou.db.CarInfoDao;
import cn.guet.haojiayou.ui.HomeFragment;
import cn.guet.haojiayou.ui.MessageFragment;
import cn.guet.haojiayou.ui.MineFragment;
import cn.guet.haojiayou.ui.NearByFragment;
import cn.guet.haojiayou.utils.NetService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends FragmentActivity implements
		View.OnClickListener, OnCheckedChangeListener{
	private RadioGroup rg;
	private RadioButton rb1, rb2, rb3, rb4;
	private long mExitTime;
	//FragmentManager fragmentManager;
	private Fragment homeFragment;
	private Fragment nearbyFragment;
	private Fragment messageFragment;
	public static MineFragment mineFragment;
	private static final String[] FRAGMENT_TAG = {"homeFrag","nearbyFrag","messageFrag","mineFrag"};
	private static final String PRV_SELINDEX = "PRV_SELINDEX";
	private int selindex = 0;
	public static String userno;//用户名
	String carsJson;//爱车列表json
	ArrayList<CarInfo> carlist;
	CarInfoDao carInfoDao;//操作本地数据库
	FragmentTransaction ft;

	/**
	 * 第三方应用Master Secret，修改为正确的值
	 */
	private static final String MASTERSECRET = "EYMwDBoWyn6V7BYRChvgU8";

	// SDK参数，会自动从Manifest文件中读取，第三方无需修改下列变量，请修改AndroidManifest.xml文件中相应的meta-data信息。
	// 修改方式参见个推SDK文档
	private String appkey = "";
	private String appsecret = "";
	private String appid = "";
    boolean hasNetwoke = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	SDKInitializer.initialize(getApplicationContext());	
		setContentView(R.layout.activity_main);
		FragmentManager fragmentManager =getSupportFragmentManager();
		if(savedInstanceState != null){
			selindex = savedInstanceState.getInt(PRV_SELINDEX, selindex);
			homeFragment = (HomeFragment)fragmentManager.findFragmentByTag(FRAGMENT_TAG[0]);
			nearbyFragment = (NearByFragment)fragmentManager.findFragmentByTag(FRAGMENT_TAG[1]);
			messageFragment = (MessageFragment)fragmentManager.findFragmentByTag(FRAGMENT_TAG[2]);
			mineFragment = (MineFragment)fragmentManager.findFragmentByTag(FRAGMENT_TAG[3]);
		}

		// 个推服务从AndroidManifest.xml的meta-data中读取SDK配置信息
		String packageName = getApplicationContext().getPackageName();
		try {
			ApplicationInfo appInfo = getPackageManager().getApplicationInfo(
					packageName, PackageManager.GET_META_DATA);
			if (appInfo.metaData != null) {
				appid = appInfo.metaData.getString("PUSH_APPID");
				appsecret = appInfo.metaData.getString("PUSH_APPSECRET");
				appkey = (appInfo.metaData.get("PUSH_APPKEY") != null) ? appInfo.metaData
						.get("PUSH_APPKEY").toString() : null;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		

		
		System.out.println("appid" + appid);
		System.out.println("appsecret" + appsecret);
		System.out.println("appkey" + appkey);
		// 初始化个推SDK
		Log.d("GetuiSdkDemo", "initializing sdk...");
		PushManager.getInstance().initialize(this.getApplicationContext());
		
;
//        /**
//         * 应用未启动, 个推 service已经被唤醒,显示该时间段内离线消息
//         */
//        if (PushDemoReceiver.payloadData != null) {
//            tLogView.append(PushDemoReceiver.payloadData);
//        }
//		
		// 车辆违章查询初始化
		Log.d("初始化违章服务代码", "");
		Intent weizhangIntent = new Intent(MainActivity.this,
				WeizhangIntentService.class);
		weizhangIntent.putExtra("appId", 1683);// appId
		weizhangIntent.putExtra("appKey", "5625dc861908214be074402275e6f711");// appKey
		startService(weizhangIntent); 
		
	 //Toast.makeText(MainActivity.this,"有网没？"+isNetworkConnected() , Toast.LENGTH_LONG).show();

		 if (isNetworkConnected()) hasNetwoke = true;
		 else hasNetwoke = false;
			 
		init();
		
		userno = CheckLoginState.username;
		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				 carlist = getMycars(userno);
//				 System.out.println("明天你好"+carlist);
//				 carInfoDao = new CarInfoDao(MainActivity.this);
//
//				 if (hasNetwoke){
//					 carInfoDao.delelteAll();
//					 }
//				 try {
//					
//					 carInfoDao.insertInfo(carlist);
//				} catch (Exception e) {
//					// TODO: handle exception
//					System.out.println("插入不成功！");
//				}
//			}
//		}).start();
		
	}
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//保存Tab选中的状态
		outState.putInt(PRV_SELINDEX, selindex);
		super.onSaveInstanceState(outState);
	}


	@Override
	public void onDestroy() {
		// Log.d("GetuiSdkDemo", "onDestroy()");
		PushDemoReceiver.payloadData.delete(0,
				PushDemoReceiver.payloadData.length());
		super.onDestroy();
	}

	@Override
	public void onStop() {
		// Log.d("GetuiSdkDemo", "onStop()");
		super.onStop();
	}

	private void init() {
		rg = (RadioGroup) findViewById(R.id.rg);
		rb1 = (RadioButton) findViewById(R.id.rb1);
		rb2 = (RadioButton) findViewById(R.id.rb2);
		rb3 = (RadioButton) findViewById(R.id.rb3);
		rb4 = (RadioButton) findViewById(R.id.rb4);
		rg.setOnCheckedChangeListener(this);
		rb2.setChecked(true);
	}
///**
// * 从服务器获取我的爱车列表
// * @return ArrayList<CarInfo>
// */
//	public  ArrayList<CarInfo> getMycars(String userno){
//    	String path = "http://115.28.16.183:8080/HaoJiaYouOrder1/GetCarInfo";
//
//		 Map<String, String> params = new HashMap<String,String>();
//		 params.put("UserNo", userno);
//		 
//		try {
//			carsJson = NetService.sendGetRequest(path, params, "utf-8");
//			 System.out.println("响应的值是："+carsJson  );		
//			 
//			 if(carsJson.toString().equals("404")||carsJson.toString().equals("500")) {
//				 carsJson = null;
//				}	
//            
//            
//			//Toast.makeText(MycarActivity.this, carsJson,Toast.LENGTH_LONG).show();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//             String msg = NetService.ExceptionCode(e);
//            System.out.println("网络异常："+msg);
//		}
//		
//		
//      Gson gson = new Gson();
//
//      if (null!=carsJson) {
//	
//      carlist = gson.fromJson(carsJson , 
//		     new TypeToken<ArrayList<CarInfo>>(){}.getType());  
//      return  carlist;
//    	
//    }
//      return null;  
//}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			// 判断两次点击的时间间隔（默认设置为2秒）
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();

				mExitTime = System.currentTimeMillis();
				System.out.println(mExitTime);
			} else {
				finish();
				System.exit(0);
				super.onBackPressed();
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

/*	private void changeFragment(Fragment targetFragment) {
		// resideMenu.clearIgnoredViewList();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_fragment, targetFragment, "fragment")
				.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit();
	}*/

	/**
	 * 页面改变
	 */
	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {

		if (rb1.getId() == checkedId) {
			showFragment(1);
			//changeFragment(new HomeFragment());
		} else if (rb2.getId() == checkedId) {
			showFragment(2);
			//changeFragment(new NearByFragment());
		} else if (rb3.getId() == checkedId) {
			showFragment(3);
			//changeFragment(new MessageFragment());
		} else if (rb4.getId() == checkedId) {
			showFragment(4);
			//changeFragment(new MineFragment());
		}
	}

	public boolean isNetworkConnected() {
		// 判断网络是否连接
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		return mNetworkInfo != null && mNetworkInfo.isAvailable();
	}
	
	public void showFragment(int index){
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		hideFragment(ft);
		switch(index){
			case 1:
				if(homeFragment != null){
					ft.show(homeFragment);
				}else{
					homeFragment = new HomeFragment();
					ft.add(R.id.main_fragment, homeFragment, FRAGMENT_TAG[0]);
				}
				break;
			case 2:
				if(nearbyFragment != null){
					ft.show(nearbyFragment);
				}else{
					nearbyFragment = new NearByFragment();
					ft.add(R.id.main_fragment, nearbyFragment, FRAGMENT_TAG[1]);
				}
				break;
			case 3:
				if(messageFragment != null){
					ft.show(messageFragment);
					if (CheckLoginState.check(MainActivity.this)) {
						((MessageFragment) messageFragment).initview();
					}else if (!CheckLoginState.check(MainActivity.this)){
						Toast.makeText(MainActivity.this, "您还没有登录，请先登录或注册！", Toast.LENGTH_SHORT).show();
						((MessageFragment) messageFragment).initvoid();
					}
				}else{
					messageFragment = new MessageFragment();
					ft.add(R.id.main_fragment, messageFragment, FRAGMENT_TAG[2]);
					
					if (CheckLoginState.check(MainActivity.this)) {
						
						((MessageFragment) messageFragment).initview();
					}else if (!CheckLoginState.check(MainActivity.this)){
						Toast.makeText(MainActivity.this, "您还没有登录，请先登录或注册！", Toast.LENGTH_SHORT).show();
						 ((MessageFragment) messageFragment).initvoid();
					}
				}
				break;
			case 4:
				if(mineFragment != null){
					ft.show(mineFragment);
				}else{
					mineFragment = new MineFragment();
					ft.add(R.id.main_fragment, mineFragment, FRAGMENT_TAG[3]);
				}	
		}
		ft.commit();
	}
	
	public void hideFragment(FragmentTransaction ft){
		if(homeFragment != null){
			ft.hide(homeFragment);
		}
		if(nearbyFragment != null){
			ft.hide(nearbyFragment);
		}
		if(messageFragment != null){
			ft.hide(messageFragment);
		}
		if(mineFragment != null){
			ft.hide(mineFragment);
		}
		
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		// TODO Auto-generated method stub
		super.onAttachFragment(fragment);
	}


	@Override
	protected void onResume() {
		super.onResume();
	}
	
}
