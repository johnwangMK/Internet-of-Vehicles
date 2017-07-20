package cn.guet.haojiayou.ui;

import java.util.ArrayList;
import java.util.List;

import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.CarInfo;
import com.cheshouye.api.client.json.CityInfoJson;
import com.cheshouye.api.client.json.InputConfigJson;
import com.cheshouye.api.client.json.WeizhangResponseHistoryJson;
import com.cheshouye.api.client.json.WeizhangResponseJson;

import cn.guet.haojiayou.R;
import cn.guet.haojiayou.R.layout;
import cn.guet.haojiayou.R.menu;
import cn.guet.haojiayou.adapter.WeizhangResponseAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MycarWeizhangResulet extends Activity {
	
	final Handler cwjHandler = new Handler();
	WeizhangResponseJson info = null;
	private TextView query_city;
	private View btn_cpsz;
	private Button btn_query;
	private View popLoader;
	Intent intent ;
	ArrayList<String> weizhangcarlist;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mycar_result);
		
		// 标题
		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtTitle.setText("违章查询结果");

		// 返回按钮
		Button btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent();
				if(info != null){
				//传值
				intent.putExtra("条数", info.getCount()+"");
				intent.putExtra("罚款", info.getTotal_money()+"");
				intent.putExtra("分数", info.getTotal_score()+"");
				}else{
					intent.putExtra("条数", 0+"");
					intent.putExtra("罚款", 0+"");
					intent.putExtra("分数", 0+"");
				}
				
				setResult(50, intent);				
				finish();			

				
			}
		});
		
		// 选择省份缩写
		query_city = (TextView) findViewById(R.id.query_my_city);
		btn_query = (Button) findViewById(R.id.btn_query_my);
		
		popLoader = (View) findViewById(R.id.popLoader2);
	//	popLoader.setVisibility(View.VISIBLE);
		
		 intent = this.getIntent();
		 weizhangcarlist = intent.getStringArrayListExtra("carinfo");
		 
			TextView query_chepai = (TextView) findViewById(R.id.query_my_chepai);//车牌
			query_chepai.setText( weizhangcarlist.get(0));
	//	CarInfo car = (CarInfo) intent.getSerializableExtra("carInfo");
		
		//选择城市
		query_city.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MycarWeizhangResulet.this, ProvinceList.class);
				//intent.setClass(getActivity(), Setting.class);
				startActivityForResult(intent, 1);
			}
		});
		
		//查询违章信息
		btn_query.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				

				// 获取违章信息
				CarInfo car = new CarInfo();
				String quertCityStr = null;
				String quertCityIdStr = null;

//				final String shortNameStr = short_name.getText().toString()
//						.trim();
				final String chepaiNumberStr = weizhangcarlist.get(0);

				if (query_city.getText() != null
						&& !query_city.getText().equals("")) {
					quertCityStr = query_city.getText().toString().trim();

				}

				if (query_city.getTag() != null
						&& !query_city.getTag().equals("")) {
					quertCityIdStr = query_city.getTag().toString().trim();
					car.setCity_id(Integer.parseInt(quertCityIdStr));
				}
				final String chejiaNumberStr = weizhangcarlist.get(1);
				final String engineNumberStr = weizhangcarlist.get(2);

				Intent intent = new Intent();

				car.setChepai_no(chepaiNumberStr);
				car.setChejia_no(chejiaNumberStr);
				car.setEngine_no(engineNumberStr);

//				Bundle bundle = new Bundle();
//				bundle.putSerializable("carInfo", car);
//				intent.putExtras(bundle);

				boolean result = checkQueryItem(car);
//				boolean result = true;
				if (result) {
					popLoader.setVisibility(View.VISIBLE);
//					intent.setClass(MycarWeizhangResulet.this, WeizhangResult.class);
//					startActivity(intent);
					step4(car);
				}
				
				
			}
		});
		

	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {//按下返回键时传值

			Intent intent = new Intent();
			if(info != null){
			//传值
			intent.putExtra("条数", info.getCount()+"");
			intent.putExtra("罚款", info.getTotal_money()+"");
			intent.putExtra("分数", info.getTotal_score()+"");
			}else{
				intent.putExtra("条数", 0+"");
				intent.putExtra("罚款", 0+"");
				intent.putExtra("分数", 0+"");
			}
			
			setResult(50, intent);	
				finish();
				super.onBackPressed();		
				
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void step4(final CarInfo car) {
		// 声明一个子线程
		new Thread() {
			@Override
			public void run() {
				try {
					// 这里写入子线程需要做的工作
					info = WeizhangClient.getWeizhang(car);//获取违章信息
					cwjHandler.post(mUpdateResults); // 告诉UI线程可以更新结果了
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}
	
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			updateUI();
		}
	};

	private void updateUI() {
		TextView result_null = (TextView) findViewById(R.id.result_my_null);
		TextView result_title = (TextView) findViewById(R.id.result_my_title);
		ListView result_list = (ListView) findViewById(R.id.result_my_list);

		popLoader.setVisibility(View.GONE);

		Log.d("返回数据", info.toJson());
		
		// 直接将信息限制在 Activity中
		if (info.getStatus() == 2001) {
			result_null.setVisibility(View.GONE);
			result_title.setVisibility(View.VISIBLE);
			result_list.setVisibility(View.VISIBLE);

			result_title.setText("共违章" + info.getCount() + "次, 计"
					+ info.getTotal_score() + "分, 罚款 " + info.getTotal_money()
					+ "元");

			WeizhangResponseAdapter mAdapter = new WeizhangResponseAdapter(
					this, getData());
			result_list.setAdapter(mAdapter);

		} else {
			// 没有查到为章记录

			if (info.getStatus() == 5000) {
				result_null.setText("请求超时，请稍后重试");
			} else if (info.getStatus() == 5001) {
				result_null.setText("交管局系统连线忙碌中，请稍后再试");
			} else if (info.getStatus() == 5002) {
				result_null.setText("恭喜，当前城市交管局暂无您的违章记录");
			} else if (info.getStatus() == 5003) {
				result_null.setText("数据异常，请重新查询");
			} else if (info.getStatus() == 5004) {
				result_null.setText("系统错误，请稍后重试");
			} else if (info.getStatus() == 5005) {
				result_null.setText("车辆查询数量超过限制");
			} else if (info.getStatus() == 5006) {
				result_null.setText("你访问的速度过快, 请后再试");
			} else if (info.getStatus() == 5008) {
				result_null.setText("输入的车辆信息有误，请查证后重新输入");
			} else {
				result_null.setText("恭喜, 没有查到违章记录！");
			}

			result_title.setVisibility(View.GONE);
			result_list.setVisibility(View.GONE);
			result_null.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;

		switch (requestCode) {
//		case 0:
//			Bundle bundle = data.getExtras();
//			String ShortName = bundle.getString("short_name");
//			short_name.setText(ShortName);
//			break;
		case 1:
			Bundle bundle1 = data.getExtras();
			// String cityName = bundle1.getString("city_name");
			String cityId = bundle1.getString("city_id");
			// query_city.setText(cityName);
			// query_city.setTag(cityId);
			// InputConfigJson inputConfig =
			// WeizhangClient.getInputConfig(Integer.parseInt(cityId));
			// System.out.println(inputConfig.toJson());
			setQueryItem(Integer.parseInt(cityId));

			break;
		}
	}
	// 根据城市的配置设置查询项目
	private void setQueryItem(int cityId ) {

		InputConfigJson cityConfig = WeizhangClient.getInputConfig(cityId);

		// 没有初始化完成的时候;
		if (cityConfig != null) {
			CityInfoJson city = WeizhangClient.getCity(cityId);

			query_city.setText(city.getCity_name());
			query_city.setTag(cityId);
			
			
		}
	}
	// 提交表单检测
	private boolean checkQueryItem(CarInfo car) {
		if (car.getCity_id() == 0) {
			Toast.makeText(MycarWeizhangResulet.this, "请选择查询地", 0).show();
			return false;
		}
		return true;
   }

	/**
	 * title:填值
	 * 
	 * @return
	 */
	private List getData() {
		List<WeizhangResponseHistoryJson> list = new ArrayList();

		for (WeizhangResponseHistoryJson weizhangResponseHistoryJson : info
				.getHistorys()) {
			WeizhangResponseHistoryJson json = new WeizhangResponseHistoryJson();
			json.setFen(weizhangResponseHistoryJson.getFen());
			json.setMoney(weizhangResponseHistoryJson.getMoney());
			json.setOccur_date(weizhangResponseHistoryJson.getOccur_date());
			json.setOccur_area(weizhangResponseHistoryJson.getOccur_area());
			json.setInfo(weizhangResponseHistoryJson.getInfo());
			list.add(json);
		}

		return list;
	}

}
	
	
