package cn.guet.haojiayou.ui;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;
import android.widget.Toast;
import cn.guet.haojiayou.R;
import cn.guet.haojiayou.adapter.MyCarWeizhangAdapter;
import cn.guet.haojiayou.adapter.SelectCarPopupWindow;
import cn.guet.haojiayou.adapter.SelectOilPopupWindow;
import cn.guet.haojiayou.bean.CarInfo;
import cn.guet.haojiayou.bean.FillOilOrder;
import cn.guet.haojiayou.db.CarInfoDao;
import cn.guet.haojiayou.ui.QueryMycarFragment.ListItemClickListener;
import cn.guet.haojiayou.utils.BorderTextView;
import cn.guet.haojiayou.utils.NetService;

import com.carUserLogin.CheckLoginState;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class Appointment extends Activity implements OnClickListener,
		OnEditorActionListener {

	Button enteryuan, entersheng;
	private TextView tv_selectoil;
	private ImageButton bt_cardropdown, bt_oildropdown,bt_datedropdown,bt_timedropdown;
	private SelectCarPopupWindow carSpinerPopWindow;
	private SelectOilPopupWindow oilSpinerPopWindow;
	private TextView tv_brand, tv_plateNo;
	private ImageView img_car_logo,back_btn;
	private TextView tv_stype, tv_sy, tv_sprice, tv_gasStationName;
	private LinearLayout ll_selectcar, ll_selectoil,ll_selectdate,ll_selecttime;
//	private CarInfoDao carInfoDao;// 操作本地数据库
	private String userno;// 用户名
	private ArrayList<CarInfo> carlist;
	private ArrayList<Map<String, Object>> gaslist;
	private String address, gasStationName, oilBrandName, name, phonenum,
			plateNo, oilType, oilprice, orderTime,arriveTime, brandname;
	private String[] type;
	private String[] price;
	private BorderTextView Border, Border2, Border3, Border4, Border5, Border6,
			Border7, Border8;
	private LinearLayout ll_money, ll_quantity;
	private EditText input_name, input_phonenum, input_money, input_quantity;// 姓名，手机号，输入金额，输入升数
	private TextView tv_summoney;// 总金额
	private String selectMoney, selectQuantity;
	private String totalmoney;
	private Button btn_order;// 确认预约
	private boolean bymoney = true; // 按金额
	private boolean byquantity = false;// 按油量
	private boolean isCheck;
	private TextView tv_date,tv_time;
	String orderJson,carsJson;
	Time time;	
	int flag = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appointment);
		initeData();
		initeView();
	}

	private void initeView() {

		enteryuan = (Button) findViewById(R.id.enteryuan);
		entersheng = (Button) findViewById(R.id.entersheng);
		enteryuan.setOnClickListener(this);
		entersheng.setOnClickListener(this);

		tv_gasStationName = (TextView) findViewById(R.id.gasStationName);
		tv_gasStationName.setText(gasStationName);
		ll_selectcar = (LinearLayout) findViewById(R.id.ll_selectcar);
//		tv_brand = (TextView) findViewById(R.id.tv_sbrand);
//		tv_plateNo = (TextView) findViewById(R.id.tv_splateNo);
//		img_car_logo = (ImageView) findViewById(R.id.img_scar_logo);
		bt_cardropdown = (ImageButton) findViewById(R.id.bt_cardropdown);
		back_btn = (ImageView) findViewById(R.id.back_appointment);
		ll_selectoil = (LinearLayout) findViewById(R.id.ll_selectoil);
		tv_stype = (TextView) findViewById(R.id.tv_stype);
		tv_sy = (TextView) findViewById(R.id.tv_sy);
		tv_sprice = (TextView) findViewById(R.id.tv_sprice);
		bt_oildropdown = (ImageButton) findViewById(R.id.bt_oildropdown);

		ll_selectcar.setOnClickListener(this);
		bt_cardropdown.setOnClickListener(this);
		ll_selectoil.setOnClickListener(this);
		bt_oildropdown.setOnClickListener(this);
		back_btn.setOnClickListener(this);

		ll_selectdate = (LinearLayout) findViewById(R.id.ll_selectdate);
		ll_selecttime = (LinearLayout) findViewById(R.id.ll_selecttime);
		bt_datedropdown = (ImageButton) findViewById(R.id.bt_datedropdown);
		bt_timedropdown = (ImageButton) findViewById(R.id.bt_timedropdown);
		tv_date = (TextView) findViewById(R.id.tv_date);//日期
		tv_time = (TextView) findViewById(R.id.tv_time);//时间
		ll_selectdate.setOnClickListener(this);
		ll_selecttime.setOnClickListener(this);
		bt_datedropdown.setOnClickListener(this);
		bt_timedropdown.setOnClickListener(this);
		
		tv_summoney = (TextView) findViewById(R.id.tv_summoney);// 总金额
		input_name = (EditText) findViewById(R.id.input_name);
		input_phonenum = (EditText) findViewById(R.id.input_phonenum);
		ll_money = (LinearLayout) findViewById(R.id.ll_money);// 金额布局
		ll_quantity = (LinearLayout) findViewById(R.id.ll_quantity);// 升布局
		Border = (BorderTextView) findViewById(R.id.Border);// 按金额加油
		Border2 = (BorderTextView) findViewById(R.id.Border2);// 按升加油
		Border3 = (BorderTextView) findViewById(R.id.Border3);// 50元
		Border4 = (BorderTextView) findViewById(R.id.Border4);// 100元
		Border5 = (BorderTextView) findViewById(R.id.Border5);// 200元
		Border6 = (BorderTextView) findViewById(R.id.Border6);// 20升
		Border7 = (BorderTextView) findViewById(R.id.Border7);// 30升
		Border8 = (BorderTextView) findViewById(R.id.Border8);// 40升

//		carSpinerPopWindow = new SelectCarPopupWindow(this, tv_brand,
//				tv_plateNo, img_car_logo, carlist);
		oilSpinerPopWindow = new SelectOilPopupWindow(this, tv_stype, tv_sy,
				tv_sprice, gaslist, Border6, Border7, Border8, tv_summoney);

		Border.setOnClickListener(this);
		Border2.setOnClickListener(this);
		Border3.setOnClickListener(this);
		Border4.setOnClickListener(this);
		Border5.setOnClickListener(this);
		Border6.setOnClickListener(this);
		Border7.setOnClickListener(this);
		Border8.setOnClickListener(this);

		input_money = (EditText) findViewById(R.id.input_money);
		input_quantity = (EditText) findViewById(R.id.input_quantity);
		input_money.setOnClickListener(this);
		input_quantity.setOnClickListener(this);
		input_money.setOnFocusChangeListener(new FocusChangeListener());
		input_money.setOnEditorActionListener(this);
		input_quantity.setOnEditorActionListener(this);
		input_quantity.setOnFocusChangeListener(new FocusChangeListener());

		input_money.addTextChangedListener(new EditChangedListener(input_money,
				30, 500));
		input_quantity.addTextChangedListener(new EditChangedListener(
				input_quantity, 5, 100));

		btn_order = (Button) findViewById(R.id.btn_order);
		btn_order.setOnClickListener(this);
	}

	private void initeData() {
		
		time = new Time();  
		time.setToNow();
		  
		userno = CheckLoginState.username;
//		carInfoDao = new CarInfoDao(this);
//		carlist = carInfoDao.findCarsByUno(userno);
        new myAnyTask().execute();
        
		Bundle bundle = this.getIntent().getExtras();
		if (null != bundle) {
			address = bundle.getString("address");
			gasStationName = bundle.getString("gs_name");
			type = bundle.getStringArray("type");
			price = bundle.getStringArray("price");
			oilBrandName = bundle.getString("brandname");
		}
		// type = new String[]{"90#","93#","97#","0#车柴"};
		ArrayList<Map<String, Object>> gastprice = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < type.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", type[i]);
			map.put("price", price[i]);
			gastprice.add(map);
		}
		System.out.println("删之前" + gastprice.toString() + "大小"
				+ gastprice.size());
		gaslist = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> map : gastprice) {
			if (null != map.get("price"))
				gaslist.add(map);
		}
		System.out.println("删之后" + gaslist.toString() + "大小" + gaslist.size());

	}

	class myAnyTask extends AsyncTask<Void, Void, ArrayList<CarInfo>>{

    	@Override
    	protected ArrayList<CarInfo> doInBackground(Void... arg0) {
    		// TODO Auto-generated method stub
    		 carlist = getCars();//汽车列表
    		
    		if (null!=carlist) {				  			
    			return carlist;
			}

    		return null;
    	}

		@Override
		protected void onPostExecute(ArrayList<CarInfo> result) {
			// TODO Auto-generated method stub
						
			tv_brand = (TextView) findViewById(R.id.tv_sbrand);
			tv_plateNo = (TextView) findViewById(R.id.tv_splateNo);
			img_car_logo = (ImageView) findViewById(R.id.img_scar_logo);
			carSpinerPopWindow = new SelectCarPopupWindow(Appointment.this, tv_brand,
					tv_plateNo, img_car_logo, result);
				
				if(null== result || result.isEmpty()){
					Toast.makeText(Appointment.this, "暂无爱车信息，请添加！", Toast.LENGTH_LONG).show();
					}else{		
						
					}
				
		    }
			
		}

    	
	    /**
	     * 获取我的爱车列表
	     */
	    public ArrayList<CarInfo> getCars(){
	    	

					String path = "http://115.28.16.183:8080/HaoJiaYouOrder1/GetCarInfo";
					//userno = "13912200019";
					 Map<String, String> params = new HashMap<String,String>();
					 params.put("UserNo", userno);
					 
					try {
						carsJson = NetService.sendGetRequest(path, params, "utf-8");
			            System.out.println("响应的值是："+carsJson  );
						 if(carsJson.toString().equals("404")||carsJson.toString().equals("500")) {
							 carsJson = null;
							}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						carsJson = null;
//						cwjHandler.post(mUpdateResults); // 告诉UI线程可以更新结果了
						Looper.prepare();
						Toast.makeText(Appointment.this,"服务器异常，请联系我们！",Toast.LENGTH_LONG).show();
						Looper.loop();
					}
			Gson gson = new Gson();
			
			if (null!=carsJson) {
				
			carlist = gson.fromJson(carsJson , 
	        		new TypeToken<ArrayList<CarInfo>>(){}.getType());  		
			   return  carlist;
			}
			
			return null;
	    }  
	@Override
	public void onClick(View view) {
		switch (view.getId()) {

		case R.id.enteryuan:// 确认元输入框
			String money = "0.00";
			System.out.println("按金额" + bymoney + "按油量" + byquantity);
			if (bymoney) {				
				money = input_money.getText().toString().trim();
				if (!money.equals("")){
				  if (Integer.parseInt(money) < 30) {
					showToast("输入的数小于30，请重新输入！");
				  	input_money.setText(30 + "");
				   } else {
					tv_summoney.setText(money + "");
				 }
				}
			}
			break;

		case R.id.entersheng:// 确认升输入框
			String quantity = "0.00";
			System.out.println("按金额" + bymoney + "按油量" + byquantity);
			if (byquantity) {
				quantity = input_quantity.getText().toString().trim();
				if (!quantity.equals("")){
				if (Integer.parseInt(quantity) < 5) {
					showToast("输入的数小于5，请重新输入！");
					input_quantity.setText(5 + "");
				} else {
					String sumcost = calcuMoney(Double.parseDouble(quantity));
					tv_summoney.setText(sumcost + "");
				}
			  }
			}
			break;

		case R.id.back_station:// 返回
			finish();
			break;

		case R.id.bt_cardropdown:// 选择汽车
		case R.id.ll_selectcar:
			carSpinerPopWindow.setWidth(ll_selectcar.getWidth());
			carSpinerPopWindow.showAsDropDown(ll_selectcar);
			break;
			
		case R.id.bt_oildropdown:// 选择升数
		case R.id.ll_selectoil:
			oilSpinerPopWindow.setWidth(ll_selectoil.getWidth());
			oilSpinerPopWindow.showAsDropDown(ll_selectoil);
			break;
			
		case R.id.bt_datedropdown:// 选择日期
		case R.id.ll_selectdate:
			Dialog dialog = onCreateDialog(R.id.ll_selectdate);
			dialog.show();
			break;

		case R.id.bt_timedropdown:// 选择时间
		case R.id.ll_selecttime:
			onCreateDialog(R.id.ll_selecttime).show();
			break;
			
		case R.id.Border:// 按金额加油
			bymoney = true;
			byquantity = false;
			Border.setBackgroundColor(getResources().getColor(R.color.blue));
			Border2.setBackgroundColor(getResources().getColor(R.color.white));
			ll_quantity.setVisibility(View.GONE);
			ll_money.setVisibility(View.VISIBLE);
			break;
		case R.id.back_appointment:
			Appointment.this.finish();
			break;

		case R.id.Border2:// 按升加油
			byquantity = true;
			bymoney = false;
			if (!enterSelectOil()) {
				Border6.setClickable(false);
				Border7.setClickable(false);
				Border8.setClickable(false);
			} else {
				Border6.setClickable(true);
				Border7.setClickable(true);
				Border8.setClickable(true);
			}
			Border2.setBackgroundColor(getResources().getColor(R.color.blue));
			Border.setBackgroundColor(getResources().getColor(R.color.white));
			ll_money.setVisibility(View.GONE);
			ll_quantity.setVisibility(View.VISIBLE);
			break;

		case R.id.Border3:// 50元
			Border3.setBackgroundColor(getResources().getColor(R.color.blue));
			Border4.setBackgroundColor(getResources().getColor(R.color.white));
			Border5.setBackgroundColor(getResources().getColor(R.color.white));
			Border6.setBackgroundColor(getResources().getColor(R.color.white));
			Border7.setBackgroundColor(getResources().getColor(R.color.white));
			Border8.setBackgroundColor(getResources().getColor(R.color.white));
			tv_summoney.setText(50 + "");
			break;

		case R.id.Border4:// 100元
			Border4.setBackgroundColor(getResources().getColor(R.color.blue));
			Border3.setBackgroundColor(getResources().getColor(R.color.white));
			Border5.setBackgroundColor(getResources().getColor(R.color.white));
			Border6.setBackgroundColor(getResources().getColor(R.color.white));
			Border7.setBackgroundColor(getResources().getColor(R.color.white));
			Border8.setBackgroundColor(getResources().getColor(R.color.white));
			tv_summoney.setText(100 + "");
			break;

		case R.id.Border5:// 200元
			Border5.setBackgroundColor(getResources().getColor(R.color.blue));
			Border3.setBackgroundColor(getResources().getColor(R.color.white));
			Border4.setBackgroundColor(getResources().getColor(R.color.white));
			Border6.setBackgroundColor(getResources().getColor(R.color.white));
			Border7.setBackgroundColor(getResources().getColor(R.color.white));
			Border8.setBackgroundColor(getResources().getColor(R.color.white));
			tv_summoney.setText(200 + "");
			break;

		case R.id.Border6:// 20升
			Border6.setBackgroundColor(getResources().getColor(R.color.blue));
			Border7.setBackgroundColor(getResources().getColor(R.color.white));
			Border8.setBackgroundColor(getResources().getColor(R.color.white));
			Border3.setBackgroundColor(getResources().getColor(R.color.white));
			Border4.setBackgroundColor(getResources().getColor(R.color.white));
			Border5.setBackgroundColor(getResources().getColor(R.color.white));
			String sumcost = calcuMoney(20.0);
			tv_summoney.setText(sumcost + "");
			oilSpinerPopWindow.quanlity = 20.0;
			break;

		case R.id.Border7:// 30升
			Border7.setBackgroundColor(getResources().getColor(R.color.blue));
			Border6.setBackgroundColor(getResources().getColor(R.color.white));
			Border8.setBackgroundColor(getResources().getColor(R.color.white));
			Border3.setBackgroundColor(getResources().getColor(R.color.white));
			Border4.setBackgroundColor(getResources().getColor(R.color.white));
			Border5.setBackgroundColor(getResources().getColor(R.color.white));
			String sumcost2 = calcuMoney(30.0);
			tv_summoney.setText(sumcost2 + "");
			oilSpinerPopWindow.quanlity = 30.0;
			break;

		case R.id.Border8:// 40升
			Border8.setBackgroundColor(getResources().getColor(R.color.blue));
			Border6.setBackgroundColor(getResources().getColor(R.color.white));
			Border7.setBackgroundColor(getResources().getColor(R.color.white));
			Border3.setBackgroundColor(getResources().getColor(R.color.white));
			Border4.setBackgroundColor(getResources().getColor(R.color.white));
			Border5.setBackgroundColor(getResources().getColor(R.color.white));
			String sumcost3 = calcuMoney(40.0);
			tv_summoney.setText(sumcost3 + "");
			oilSpinerPopWindow.quanlity = 40.0;
			break;

		case R.id.input_money:
			Border3.setBackgroundColor(getResources().getColor(R.color.white));
			Border4.setBackgroundColor(getResources().getColor(R.color.white));
			Border5.setBackgroundColor(getResources().getColor(R.color.white));

			break;
		case R.id.input_quantity:
			Border6.setBackgroundColor(getResources().getColor(R.color.white));
			Border7.setBackgroundColor(getResources().getColor(R.color.white));
			Border8.setBackgroundColor(getResources().getColor(R.color.white));

			break;

		case R.id.btn_order:// 确认预约
			isCheck = checkOrder();
			System.out.println("好订单" + isCheck);
			if (isCheck) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(Appointment.this);
				String msg = "您的预约订单总金额为：￥"+tv_summoney.getText().toString()+" 是否确认？";
				
				builder.setMessage(msg).setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
					
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
			break;

		default:
			break;
		}

	}

	/**
	 * 输入框获得焦点
	 * 
	 * @author Zouwei
	 * 
	 */
	class FocusChangeListener implements OnFocusChangeListener {

		@Override
		public void onFocusChange(View view, boolean hasFocus) {
			// TODO Auto-generated method stub
			switch (view.getId()) {

			case R.id.input_money:
				if (hasFocus) {
					Border3.setBackgroundColor(getResources().getColor(
							R.color.white));
					Border4.setBackgroundColor(getResources().getColor(
							R.color.white));
					Border5.setBackgroundColor(getResources().getColor(
							R.color.white));

				}
				break;

			case R.id.input_quantity:
				if (hasFocus) {
					Border6.setBackgroundColor(getResources().getColor(
							R.color.white));
					Border7.setBackgroundColor(getResources().getColor(
							R.color.white));
					Border8.setBackgroundColor(getResources().getColor(
							R.color.white));
				}
				break;

			default:
				break;
			}
		}

	}

	/**
	 * 监听输入框内容的改变
	 * 
	 * @author Zouwei
	 * 
	 */
	class EditChangedListener implements TextWatcher {

		private int MIN_MARK;
		private int MAX_MARK;
		private EditText et;

		public EditChangedListener(EditText et, int mIN_MARK, int mAX_MARK) {
			super();
			MIN_MARK = mIN_MARK;
			MAX_MARK = mAX_MARK;
			this.et = et;
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if (s != null && !s.equals("")) {
				if (MIN_MARK != -1 && MAX_MARK != -1) {
					int markVal = 0;
					try {
						markVal = Integer.parseInt(s.toString());
					} catch (NumberFormatException e) {
						markVal = 0;
					}
					if (markVal > MAX_MARK) {
						Toast.makeText(getBaseContext(), "输入不能超过" + MAX_MARK,
								Toast.LENGTH_SHORT).show();
						et.setText(String.valueOf(MAX_MARK));
					}
					return;
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int end, int count) {
			// TODO Auto-generated method stub

			Border3.setBackgroundColor(getResources().getColor(R.color.white));
			Border4.setBackgroundColor(getResources().getColor(R.color.white));
			Border5.setBackgroundColor(getResources().getColor(R.color.white));
			if (start > 1) {
				if (MIN_MARK != -1 && MAX_MARK != -1) {
					int num = Integer.parseInt(s.toString());
					if (num > MAX_MARK) {
						s = String.valueOf(MAX_MARK);
						et.setText(s);
					} else if (num < MIN_MARK)
						s = String.valueOf(MIN_MARK);
					et.setText(s);
					return;
				}
			}

		}

	}

	/**
	 * 按升计算总金额
	 * 
	 * @param quantity
	 *            升
	 * @return 小数点后一位
	 */
	public String calcuMoney(Double quantity) {

		Double price = Double.parseDouble(tv_sprice.getText().toString());
		DecimalFormat df = new DecimalFormat("#.0");
		return df.format(quantity * price);
	}

	/**
	 * 判断手机号码是否合法
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	/**
	 * 是否已经选择了加油类型
	 * 
	 * @return
	 */
	private boolean enterSelectOil() {
		if (tv_stype.getText().toString().equals("  ")) {
			showToast("您还没有选择加油类型！");
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 检查订单填写完整与正确性
	 * 
	 * @return
	 */
	private boolean checkOrder() {
		System.out.println("手机号正确吗？"
				+ isPhoneNumberValid(input_phonenum.getText().toString()));
		if (input_name.getText().toString().equals("")
				|| input_phonenum.getText().toString().equals("")) {
			showToast("请输入姓名和手机号！");
			return false;
		} else if (tv_brand.getText().toString().equals("")) {
			showToast("请选择一辆汽车！若无爱车请先添加！");
			return false;
		}else if (tv_date.getText().toString().trim().equals("")
				||tv_time.getText().toString().trim().equals("")) {
			showToast("请选择预计到达加油时间！");
			return false;
		}  else if (!isPhoneNumberValid(input_phonenum.getText().toString())) {
			showToast("请检查手机号码是否输入正确！");
			return false;
		} else if (tv_stype.getText().toString().equals("  ")) {
			showToast("请选择加油类型！");
			return false;
		} else if (tv_summoney.getText().toString().trim().equals("")
				|| tv_summoney.getText().toString().trim().equals("0.00")
				|| tv_summoney.getText().toString().trim().equals("0.0")) {
			showToast("请确认加油量或者金额！");
			return false;
		} else {
			return true;
		}
	}
	
/**
 * 发送订单
 */
	public void sendOrder() {

		name = input_name.getText().toString();
		phonenum = input_phonenum.getText().toString();
		plateNo = tv_plateNo.getText().toString();
		oilType = tv_stype.getText().toString();
		oilprice = tv_sprice.getText().toString();
		totalmoney = tv_summoney.getText().toString();
		String date = tv_date.getText().toString();
		String time = tv_time.getText().toString();
		arriveTime = new String(date+" "+time);
		FillOilOrder order = new FillOilOrder(getNow(), name, phonenum,
				address, gasStationName, oilBrandName, oilType, oilprice,
				arriveTime,totalmoney, plateNo,"1");
    	System.out.println("豌豆是：" + order.toString());
		Gson gson = new Gson();
		orderJson = gson.toJson(order);
		System.out.println(orderJson);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String path = "http://115.28.16.183:8080/HaoJiaYouOrder1/FillOilInfo";
				//String path = "http://202.193.74.157:8080/HaoJiaYouOrder1/FillOilInfo";
				try {
					String msg = NetService.sendData(path, orderJson);// 向服务器传输数据
					//String msg = NetService.sendData(path, "");// 向服务器传输数据
                    System.out.println("插入订单服务器消息"+msg);
					if (msg.contains("success")) {
						Looper.prepare();
						showToast("预约成功！");
						Appointment.this.finish();
						startActivity(new Intent(Appointment.this,OrderListActivity.class));
						Looper.loop();
					} else{
						Looper.prepare();
						showToast("预约失败！");
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
	 * 获取系统当前时间 年-月-日 时:分:秒
	 * 
	 * @return
	 */
	public String getNow() {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date());
	}

	private void showToast(String msg) {
		Toast.makeText(Appointment.this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			input_money.clearFocus();
			input_quantity.clearFocus();
			return true;
		}
		return false;
	}

	/**
	 * 日期和时间选择对话框
	 */
	public Dialog onCreateDialog(int id) { 
	    Dialog dialog = null; 
	    
	    switch (id) { 
	    case R.id.ll_selectdate: 
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
	                             Toast.makeText(Appointment.this, "当前日不能小于今日,请重新设置", 2000).show(); 
	                         } 
	                     }else{         //设置月小于当前月，提示重新设置 
	                         flag = 3; 
	                         Toast.makeText(Appointment.this, "当前月不能小于今月，请重新设置", 2000).show(); 
	                     } 
	                 }else{             //设置年小于当前年，提示重新设置 
	                     flag = 3; 
	                     Toast.makeText(Appointment.this, "当前年不能小于今年，请重新设置", 2000).show(); 
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
	            
	    case R.id.ll_selecttime: 
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
	                            
	                            Toast.makeText(Appointment.this, "请选择大于现在时刻的分钟", 2000).show(); 
	                        } 
	                    }else{ 
                            if(minute<10){
	                           	 tv_time.setText(getHour()+":"+"0"+getMinute());
	                            } else{
		                            tv_time.setText(getHour()+":"+getMinute()); } 
	                        Toast.makeText(Appointment.this, "请选择大于现在时刻的小时", 2000).show(); 
	                    } 
	                    break; 
	                    
	                case 3:         //设置日期等于当前日期之前，提示日期还未设置正确，不能设置时间 
                        if(minute<10){
                          	 tv_time.setText(getHour()+":"+"0"+getMinute());
                           } else{
	                            tv_time.setText(getHour()+":"+getMinute()); }
                        
	                    Toast.makeText(Appointment.this, "请先设置正确的日期", 2000).show(); 
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
  
}
