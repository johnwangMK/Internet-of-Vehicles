package cn.guet.haojiayou.ui;

import cn.guet.haojiayou.MainActivity;
import cn.guet.haojiayou.R;
import cn.guet.haojiayou.R.layout;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;

public class Welcome extends Activity {
	private final int SPLASH_DISPLAY_LENGTH = 3000;
	private Handler mHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent intent = new Intent(Welcome.this,MainActivity.class);
				startActivity(intent);
				Welcome.this.finish();
				
			}
		}, SPLASH_DISPLAY_LENGTH);
	}
	
}
