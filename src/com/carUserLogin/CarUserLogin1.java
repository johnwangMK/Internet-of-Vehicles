package com.carUserLogin;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.guet.haojiayou.MainActivity;
import cn.guet.haojiayou.R;

public class CarUserLogin1 extends Activity {

	final int PROGRESS_DIALOG = 0;
	ProgressDialog pd;

	private EditText userNo;
	private EditText userPassword;

	private Button btn_login;
	private Button btn_register;
	private Button btn_forgetpassword;

	String userNoString;
	String userPwdString;

	SharedPreferences sharedPreferences;
	Editor editor;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.car_user_login1);
		
		userNo = (EditText) findViewById(R.id.user_cellphoneNo1);
		userPassword = (EditText) findViewById(R.id.user_password1);

		btn_register = (Button) findViewById(R.id.user_register_btn1);
		btn_login = (Button) findViewById(R.id.user_login_btn1);
		btn_forgetpassword = (Button) findViewById(R.id.forget_password1);
		btn_register.setOnClickListener(new OnClickListener() { // 创建监听事件
					@Override
					public void onClick(View source) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(CarUserLogin1.this,
								CarUserRegister.class);
						startActivityForResult(intent, 0);
					}
				});
		
		btn_forgetpassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CarUserLogin1.this,
						CarUserResetPassword.class);
				startActivityForResult(intent, 0);
			}
		});

		sharedPreferences = getSharedPreferences("carUserLoginData",
				MODE_PRIVATE);
		//boolean b = false;
		// 向新建的文档中读取数据
/*		b = sharedPreferences.getBoolean("cheackBox", false);
		if (b == true) {
			userNo.setText(sharedPreferences.getString("userphoneNo", ""));
			userPassword.setText(sharedPreferences.getString("upassword", ""));
			checkBox.setChecked(true);
//		    showDialog(PROGRESS_DIALOG); //自动登录
		}*/
/*		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null){
			String userNo_r = bundle.getString("UserNo");
			String password_r = bundle.getString("Password");
			userNo.setText(userNo_r);
			userPassword.setText(password_r);
		}*/
		btn_login.setOnClickListener(new OnClickListener() {

			@SuppressWarnings({ "deprecation", "static-access", "unused" })
			public void onClick(View v) {
				// TODO Auto-generated method stub
				userNoString = userNo.getText().toString().trim();
				userPwdString = userPassword.getText().toString().trim();
				if (userNoString.equals("")) {
					showDialog("请检查手机号是否为空！\n");
				}else if(userPwdString.equals("")) {
					showDialog("请检查密码是否为空！\n");
				}else {					
					//Toast.makeText(CarUserLogin1.this,"用户名：" + userNoString + "\n密码：" + userPwdString,Toast.LENGTH_SHORT).show();
					HttpPost httpRequest = new
					HttpPost("http://115.28.16.183:8080/HaoJiaYouOrder1/Login");
//					HttpPost httpRequest = new HttpPost(
//							"http://202.193.75.94:8080/HaoJiaYouOrder1/Login");
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("UserNo", userNoString));
					params.add(new BasicNameValuePair("Password", userPwdString));

					try {
						
						if (true) {
							httpRequest.setEntity(new UrlEncodedFormEntity(params,
									HTTP.UTF_8));// 设置请求参数项
							HttpClient httpClient = new DefaultHttpClient();
							HttpResponse httpResponse = httpClient
									.execute(httpRequest);// 执行请求返回响应
							//Toast.makeText(CarUserLogin1.this, "hjhjkfhskjf",Toast.LENGTH_SHORT).show();	
							if (httpResponse.getStatusLine().getStatusCode() == 200) {// 判断是否请求成功
								String msg = EntityUtils.toString(httpResponse
										.getEntity());
								
							String msg1 = null;
							if (msg.contains("success")) {
								msg1 = "success";
							} else if (msg.contains("failure")) {
								msg1 = "failure";
							}
							//Toast.makeText(CarUserLogin1.this, msg1,Toast.LENGTH_SHORT).show();
							if (msg1.equals("success")) {
								editor = sharedPreferences.edit();
								editor.clear();
								editor.putString("userphoneNo",	userNoString);
								editor.putString("upassword", userPwdString);
							/*if (checkBox.isChecked()) {
									// 向新建的文档中写入数据
									editor.putString("userphoneNo",	userNoString);
									editor.putString("upassword", userPwdString);
									editor.putBoolean("cheackBox", true);
								} else {
									// 清除新建文档中的数据
									editor.clear();
									
								}*/
								// 执行对新建文档的操作
								editor.commit();
								if(MainActivity.mineFragment != null)  {
									MainActivity.mineFragment.setLonginlogo();
								}
								showDialog(PROGRESS_DIALOG);

							} else if (msg1.equals("failure")) {
								showDialog("手机号或者密码错误，请检查后重新登录！\n");
								// userNo.setText("");
								// userPassword.setText("");
							}
							}

						} else {
							// throw new RuntimeException("请求url失败");
						}
					} catch (Exception e) {
						// Toast.makeText(Login.this, e.getMessage(),
						// Toast.LENGTH_SHORT).show();
						Toast.makeText(CarUserLogin1.this, "请求超时",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case PROGRESS_DIALOG:
			pd = new ProgressDialog(this);
			pd.setMax(100);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setTitle("正在登录，请稍候...");
			pd.setCancelable(false);
			break;
		}
		return pd;
	}

	@Override
	@Deprecated
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case PROGRESS_DIALOG:
			pd.incrementProgressBy(-pd.getProgress());
			new Thread() {
				public void run() {
					while (true) {
						// hd.sendEmptyMessage(INCREASE);
						pd.incrementProgressBy(2);
						if (pd.getProgress() >= 100) {
							pd.dismiss();
							int resultCode = 1;
							Intent intent = new Intent();
							CarUserLogin1.this.setResult(resultCode);
							finish();
							break;
						}
						try {
							Thread.sleep(40);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
			break;
		}
	}

	private void showDialog(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("友情提醒");//设置标题
		builder.setIcon(R.drawable.ic_launcher);//设置图标 
		builder.setMessage(msg).setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.car_user_login, menu);
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(resultCode){
		case 1:
			String userNo_r = data.getExtras().getString("UserNo","");
			String password_r = data.getExtras().getString("Password", "");
			userNo.setText(userNo_r);
			userPassword.setText(password_r);
			break;
		case 0:
			break;
		default:
			break;
		}
	}
	
	

}