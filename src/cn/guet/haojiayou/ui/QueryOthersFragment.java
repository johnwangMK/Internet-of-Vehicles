package cn.guet.haojiayou.ui;

import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.WeizhangIntentService;
import com.cheshouye.api.client.json.CarInfo;
import com.cheshouye.api.client.json.CityInfoJson;
import com.cheshouye.api.client.json.InputConfigJson;
import cn.guet.haojiayou.R;
import cn.guet.haojiayou.R.layout;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class QueryOthersFragment extends Fragment {
	private String defaultChepai = "粤"; // 粤=广东

	private TextView short_name;
	private TextView query_city;
	private View btn_cpsz;
	private Button btn_query;

	private EditText chepai_number;
	private EditText chejia_number;
	private EditText engine_number;
	View row_chejia; 
	View row_engine;

	// 行驶证图示
	private View popXSZ;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.query_others_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		// 标题
//		TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
//		txtTitle.setText("车辆违章查询");

		// ********************************************************
//		Log.d("初始化服务代码","");
//		Intent weizhangIntent = new Intent(getActivity(), WeizhangIntentService.class);
//		weizhangIntent.putExtra("appId",1683);// 您的appId
//		weizhangIntent.putExtra("appKey", "5625dc861908214be074402275e6f711");// 您的appKey
//		getActivity().startService(weizhangIntent);
		// ********************************************************

		// 选择省份缩写
		query_city = (TextView) view.findViewById(R.id.cx_city);
		chepai_number = (EditText) view.findViewById(R.id.chepai_number);
		chejia_number = (EditText) view.findViewById(R.id.chejia_number);
		engine_number = (EditText) view.findViewById(R.id.engine_number);
		short_name = (TextView) view.findViewById(R.id.chepai_sz);

		// ----------------------------------------------
		row_chejia = (View) view.findViewById(R.id.row_chejia);
		row_engine = (View) view.findViewById(R.id.row_engine);
		btn_cpsz = (View) view.findViewById(R.id.btn_cpsz);
		btn_query = (Button) view.findViewById(R.id.btn_query);

		btn_cpsz.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), ShortNameList.class);
				intent.putExtra("select_short_name", short_name.getText());
				startActivityForResult(intent, 0);
			}
		});

		query_city.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), ProvinceList.class);
				//intent.setClass(getActivity(), Setting.class);
				startActivityForResult(intent, 1);
			}
		});

		btn_query.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 获取违章信息
				CarInfo car = new CarInfo();
				String quertCityStr = null;
				String quertCityIdStr = null;

				final String shortNameStr = short_name.getText().toString()
						.trim();
				final String chepaiNumberStr = chepai_number.getText()
						.toString().trim();
				if (query_city.getText() != null
						&& !query_city.getText().equals("")) {
					quertCityStr = query_city.getText().toString().trim();

				}

				if (query_city.getTag() != null
						&& !query_city.getTag().equals("")) {
					quertCityIdStr = query_city.getTag().toString().trim();
					car.setCity_id(Integer.parseInt(quertCityIdStr));
				}
				final String chejiaNumberStr = chejia_number.getText()
						.toString().trim();
				final String engineNumberStr = engine_number.getText()
						.toString().trim();

				Intent intent = new Intent();

				car.setChejia_no(chejiaNumberStr);
				car.setChepai_no(shortNameStr + chepaiNumberStr);

				car.setEngine_no(engineNumberStr);

				Bundle bundle = new Bundle();
				bundle.putSerializable("carInfo", car);
				intent.putExtras(bundle);

				boolean result = checkQueryItem(car);

				if (result) {
					intent.setClass(getActivity(), WeizhangResult.class);
					startActivity(intent);

				}
			}
		});

		// 根据默认查询地城市id, 初始化查询项目
		// setQueryItem(defaultCityId, defaultCityName);
		short_name.setText(defaultChepai);

		// 显示隐藏行驶证图示
		popXSZ = (View) view.findViewById(R.id.popXSZ);
		popXSZ.setOnTouchListener(new popOnTouchListener());
		hideShowXSZ(view);		
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;

		switch (requestCode) {
		case 0:
			Bundle bundle = data.getExtras();
			String ShortName = bundle.getString("short_name");
			short_name.setText(ShortName);
			break;
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

			int len_chejia = cityConfig.getClassno();
			int len_engine = cityConfig.getEngineno();

			// 车架号
			if (len_chejia == 0) {
				row_chejia.setVisibility(View.GONE);
			} else {
				row_chejia.setVisibility(View.VISIBLE);
				setMaxlength(chejia_number, len_chejia);
				if (len_chejia == -1) {
					chejia_number.setHint("请输入完整车架号");
				} else if (len_chejia > 0) {
					chejia_number.setHint("请输入车架号后" + len_chejia + "位");
				}
			}

			// 发动机号
			if (len_engine == 0) {
				row_engine.setVisibility(View.GONE);
			} else {
				row_engine.setVisibility(View.VISIBLE);
				setMaxlength(engine_number, len_engine);
				if (len_engine == -1) {
					engine_number.setHint("请输入完整车发动机号");
				} else if (len_engine > 0) {
					engine_number.setHint("请输入发动机后" + len_engine + "位");
				}
			}
		}
	}

	// 提交表单检测
	private boolean checkQueryItem(CarInfo car) {
		if (car.getCity_id() == 0) {
			Toast.makeText(getActivity(), "请选择查询地", 0).show();
			return false;
		}

		if (car.getChepai_no().length() != 7) {
			Toast.makeText(getActivity(), "您输入的车牌号有误", 0).show();
			return false;
		}

		if (car.getCity_id() > 0) {
			InputConfigJson inputConfig = WeizhangClient.getInputConfig(car
					.getCity_id());
			int engineno = inputConfig.getEngineno();
			int registno = inputConfig.getRegistno();
			int classno = inputConfig.getClassno();

			// 车架号
			if (classno > 0) {
				if (car.getChejia_no().equals("")) {
					Toast.makeText(getActivity(), "输入车架号不为空", 0).show();
					return false;
				}

				if (car.getChejia_no().length() != classno) {
					Toast.makeText(getActivity(), "输入车架号后" + classno + "位",
							0).show();
					return false;
				}
			} else if (classno < 0) {
				if (car.getChejia_no().length() == 0) {
					Toast.makeText(getActivity(), "输入全部车架号", 0).show();
					return false;
				}
			}

			//发动机
			if (engineno > 0) {
				if (car.getEngine_no().equals("")) {
					Toast.makeText(getActivity(), "输入发动机号不为空", 0).show();
					return false;
				}

				if (car.getEngine_no().length() != engineno) {
					Toast.makeText(getActivity(),
							"输入发动机号后" + engineno + "位", 0).show();
					return false;
				}
			} else if (engineno < 0) {
				if (car.getEngine_no().length() == 0) {
					Toast.makeText(getActivity(), "输入全部发动机号", 0).show();
					return false;
				}
			}

			// 注册证书编号
			if (registno > 0) {
				if (car.getRegister_no().equals("")) {
					Toast.makeText(getActivity(), "输入证书编号不为空", 0).show();
					return false;
				}
				
				if (car.getRegister_no().length() != registno) {
					Toast.makeText(getActivity(),
							"输入证书编号后" + registno + "位", 0).show();
					return false;
				}
			} else if (registno < 0) {
				if (car.getRegister_no().length() == 0) {
					Toast.makeText(getActivity(), "输入全部证书编号", 0).show();
					return false;
				}
			}
			return true;
		}
		return false;

	}

	// 设置/取消最大长度限制
	private void setMaxlength(EditText et, int maxLength) {
		if (maxLength > 0) {
			et.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					maxLength) });
		} else { // 不限制
			et.setFilters(new InputFilter[] {});
		}
	}

	// 显示隐藏行驶证图示
	private void hideShowXSZ(View view) {
		View btn_help1 = (View) view.findViewById(R.id.ico_chejia);
		View btn_help2 = (View) view.findViewById(R.id.ico_engine);
		Button btn_closeXSZ = (Button) view.findViewById(R.id.btn_closeXSZ);

		btn_help1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popXSZ.setVisibility(View.VISIBLE);
			}
		});
		btn_help2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popXSZ.setVisibility(View.VISIBLE);
			}
		});
		btn_closeXSZ.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popXSZ.setVisibility(View.GONE);
			}
		});
	}

	// 避免穿透导致表单元素取得焦点
	private class popOnTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			popXSZ.setVisibility(View.GONE);
			return true;
		}
	}

}
