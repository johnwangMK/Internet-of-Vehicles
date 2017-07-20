package cn.guet.haojiayou.ui;


import cn.guet.haojiayou.R;
import cn.guet.haojiayou.R.id;
import cn.guet.haojiayou.R.layout;
import cn.guet.haojiayou.bean.CarInfo;
import cn.guet.haojiayou.utils.GetAssets;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CarDetailedInfo extends Activity {

	TextView brand,model,plateNo,engineNo,carDoor,carSeat,mileage,oilConsumption,vehicleIDNo,
    enginePerformance,variatorPerformance,carLamp;
    ImageView logo;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car_detailed_info);
		
		Intent intent = this.getIntent();
		CarInfo carInfo = (CarInfo) intent.getSerializableExtra("carInfo");
		
		brand = (TextView) findViewById(R.id.brand);
		model = (TextView) findViewById(R.id.model);
		vehicleIDNo = (TextView) findViewById(R.id.vehicleIDNo);
		plateNo = (TextView) findViewById(R.id.plateNo);
		carDoor = (TextView) findViewById(R.id.carDoor);
		carSeat = (TextView) findViewById(R.id.carSeat);
		oilConsumption = (TextView) findViewById(R.id.oilConsumption);
		engineNo = (TextView) findViewById(R.id.engineNo);
		mileage = (TextView) findViewById(R.id.mileage);
		enginePerformance = (TextView) findViewById(R.id.enginePerformance);
		variatorPerformance = (TextView) findViewById(R.id.variatorPerformance);
		carLamp = (TextView) findViewById(R.id.carLamp);	
		logo = (ImageView) findViewById(R.id.logo);
		
		brand.setText(carInfo.brand);
		model.setText(carInfo.model);
		plateNo.setText(carInfo.plateNo);
		vehicleIDNo.setText(carInfo.vehicleIDNo);
		carDoor.setText(carInfo.carDoor+"门");
		carSeat.setText(carInfo.carSeat+"座");
		oilConsumption.setText(carInfo.oilConsumption);
		engineNo.setText(carInfo.engineNo);
		mileage.setText(carInfo.mileage+"公里");
		enginePerformance .setText(carInfo.enginePerformance );
		variatorPerformance.setText(carInfo.variatorPerformance);
		carLamp.setText(carInfo.carLamp);		
		/**
		 * 设置汽车logo图片
		 */
		String logopath = "carimages/"+carInfo.logo;
		Drawable drawable = GetAssets.getImageFromAssetsFile(logopath, this);
		logo.setImageDrawable(drawable);
		
		ImageView back = (ImageView) findViewById(R.id.back_mycarlistview);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
			}
		});
	}

	
}
