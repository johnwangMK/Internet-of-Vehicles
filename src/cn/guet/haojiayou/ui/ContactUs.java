package cn.guet.haojiayou.ui;

import cn.guet.haojiayou.R;
import cn.guet.haojiayou.R.layout;
import cn.guet.haojiayou.R.menu;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactUs extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_us);
    ImageView goback = (ImageView) findViewById(R.id.back_contact_us);	
	LinearLayout LL_call = (LinearLayout) findViewById(R.id.LL_call);
	
	goback.setOnClickListener(new clickListener());
	LL_call.setOnClickListener(new clickListener());
}
  private class clickListener implements OnClickListener{

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_contact_us:
			finish();
			overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
			break;
		case R.id.LL_contact_us:
			TextView phoneText = (TextView) findViewById(R.id.tv_phone);
			String phoneno=phoneText.getText().toString();			
			Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phoneno));			
			startActivity(intent);		
			break;
		default:
			break;
		}
	}
	  
  }


}
