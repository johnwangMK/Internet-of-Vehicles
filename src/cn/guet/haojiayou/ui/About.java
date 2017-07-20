package cn.guet.haojiayou.ui;

import cn.guet.haojiayou.R;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		ImageView goback = (ImageView) findViewById(R.id.back_about);
		LinearLayout LL_contact_us = (LinearLayout) findViewById(R.id.LL_contact_us);
		
		goback.setOnClickListener(new clickListener());
		LL_contact_us.setOnClickListener(new clickListener());
		
	}
      private class clickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back_about:
				About.this.finish();
				About.this.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
				break;
			case R.id.LL_contact_us:
				String phonenumber = "10086";
				Intent intent =new Intent(Intent.ACTION_DIAL);
				Uri data = Uri.parse("tel:"+phonenumber);
				intent.setData(data);
				startActivity(intent);
				break;
			default:
				break;
			}
		}
    	  
      }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

}
