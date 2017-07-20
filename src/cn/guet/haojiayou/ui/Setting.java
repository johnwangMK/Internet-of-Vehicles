package cn.guet.haojiayou.ui;

import com.carUserLogin.CarUserResetPassword;
import com.carUserLogin.CheckLoginState;

import cn.guet.haojiayou.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Setting extends Activity implements OnClickListener{
	
	private Button exit;
	private LinearLayout ll_feedback;
	private LinearLayout ll_ckeckupdate;
	private LinearLayout ll_resetpassword;
	SharedPreferences sharedPreferences;
	Editor editor;
	private boolean loginState;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		loginState = CheckLoginState.check(Setting.this);
		ImageView goback = (ImageView) findViewById(R.id.back_setting);
		ll_feedback = (LinearLayout) findViewById(R.id.LL_feedback);
		ll_ckeckupdate = (LinearLayout) findViewById(R.id.LL_checkupdate);
		ll_resetpassword = (LinearLayout) findViewById(R.id.LL_resetpassword);
		exit = (Button) findViewById(R.id.login_exit);
		exit.setOnClickListener(this);
		goback.setOnClickListener(this);
		ll_feedback.setOnClickListener(this);
		ll_ckeckupdate.setOnClickListener(this);
		ll_resetpassword.setOnClickListener(this);
		if(loginState == false){
			exit.setVisibility(View.GONE);
			ll_resetpassword.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_setting:
			Setting.this.setResult(2);
			finish();
			overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
			break;
		case R.id.login_exit:
			sharedPreferences = getSharedPreferences("carUserLoginData",MODE_PRIVATE);
			editor = sharedPreferences.edit();
			editor.clear();
			editor.commit();
			finish();
			Setting.this.setResult(0);
			break;
		case R.id.LL_feedback:
			startActivity(new Intent(this, Feedback.class));
			break;
		case R.id.LL_checkupdate:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("已是最新版本，无需更新");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			});
			builder.create().show();
			break;
		case R.id.LL_resetpassword:
			Intent intent = new Intent(this, CarUserResetPassword.class);
			startActivityForResult(intent, 0);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Setting.this.setResult(2);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}	
	
}
