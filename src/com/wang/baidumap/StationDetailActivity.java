package com.wang.baidumap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.carUserLogin.CarUserLogin1;
import com.carUserLogin.CheckLoginState;

import cn.guet.haojiayou.R;
import cn.guet.haojiayou.adapter.GastpriceAdapter;
import cn.guet.haojiayou.ui.Appointment;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StationDetailActivity extends Activity {
	//�ؼ�
	private TextView gs_name;
	private TextView gs_address;
	private ListView gs_price_list;
	private Button goto_button;
	private ImageView back_btn;
	//���
	private String name;
	private String brandname;
	private String address;
	private String gastprice_90;
	private String gastprice_93;
	private String gastprice_97;
	private String gastprice_0;
	private double longitude;
	private double latitude;
	
	private String[] type;
	private String[] price;
	private List<Map<String, Object>> gastprice;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.station_detail);
		gs_name = (TextView) findViewById(R.id.station_detail_name);
		gs_address = (TextView) findViewById(R.id.station_detail_address);
		gs_price_list = (ListView) findViewById(R.id.station_detail_gas);
		goto_button = (Button) findViewById(R.id.goto_button);
		back_btn = (ImageView) findViewById(R.id.back_detail_station);
		initData();
		gs_name.setText(name);
		gs_address.setText(address);
		gs_price_list.setAdapter(new GastpriceAdapter(gastprice, StationDetailActivity.this));
		//Toast.makeText(StationDetailActivity.this, gastprice_93, Toast.LENGTH_LONG).show();
		//����վ��ַTextView����¼�������Ժ���ʾ�滮·��
		gs_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(StationDetailActivity.this,RouteActivity.class);
				Bundle bundle = new Bundle();
				bundle.putDouble("longitude", longitude);
				bundle.putDouble("latitude", latitude);
				//bundle.putInt("tag", 0);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		//前往预约加油
		goto_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (CheckLoginState.check(StationDetailActivity.this)) {
					Intent intent = new Intent(StationDetailActivity.this,Appointment.class);
					Bundle bundle = new Bundle();
					bundle.putString("address", address);
					bundle.putString("gs_name", name);
					bundle.putStringArray("type", type);
					bundle.putStringArray("price", price);
					bundle.putString("brandname", brandname);
					intent.putExtras(bundle);
					startActivity(intent);

				}else{
					Toast.makeText(StationDetailActivity.this, "登录后才能预约加油！", Toast.LENGTH_SHORT).show();
				     startActivity(new Intent(StationDetailActivity.this,CarUserLogin1.class));
				}	
			}
		});
		
		back_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				StationDetailActivity.this.finish();
			}
		});
	}
	public void initData(){
		Bundle bundle = this.getIntent().getExtras();
		name = bundle.getString("gs_name");
		address = bundle.getString("gs_address");
		brandname = bundle.getString("brandname");
		gastprice_90 = bundle.getString("gastprice_90");
		gastprice_93 = bundle.getString("gastprice_93");
		gastprice_97 = bundle.getString("gastprice_97");
		gastprice_0 = bundle.getString("gastprice_0");
		longitude = bundle.getDouble("longitude");
		latitude = bundle.getDouble("latitude");
		type = new String[]{"90#","93#","97#","0#车柴"};
		price = new String[]{gastprice_90, gastprice_93, gastprice_97, gastprice_0};
		gastprice = new ArrayList<Map<String,Object>>();
		for(int i=0;i<type.length;i++){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("type", type[i]);
			map.put("price", price[i]);
			gastprice.add(map);
		}
	}
}
