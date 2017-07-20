package com.carUserLogin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.guet.haojiayou.R;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class CarUserResetPassword extends Activity {
	final int PROGRESS_DIALOG = 0;
	ProgressDialog pd;
	String resetPhoneNo1;
	int i = 30;
	
	private EditText inputPhoneNo;
	private EditText mEditText; // 验证码输入框
	private EditText newPassword1;
	private EditText newPassword2;

	private TextView mTextView; // 显示验证码输入状态信息

	private Button requestCodeBtn; // 获取验证码按钮
	private Button resetPassword;
	private Button resetSubmit;

	private LinearLayout resetLayout1;
	private LinearLayout resetLayout2;

	SharedPreferences sharedPreferences;
	Editor editor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.car_user_reset_password);

		inputPhoneNo = (EditText) findViewById(R.id.user_reset_cellphoneNo);
		mEditText = (EditText) findViewById(R.id.user_reset_yanzma);
		mTextView = (TextView) findViewById(R.id.tv_codestatus);

		resetPassword = (Button) findViewById(R.id.btn_user_reset_password);
		resetSubmit = (Button) findViewById(R.id.btn_user_submit);
		requestCodeBtn = (Button) findViewById(R.id.btn_reset_get_yanzma);

		resetLayout1 = (LinearLayout) findViewById(R.id.resetPassword1);
		resetLayout2 = (LinearLayout) findViewById(R.id.resetPassword2);

		sharedPreferences = getSharedPreferences("carUserResetPasswordData",
				MODE_PRIVATE);
		editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
		mEditText.addTextChangedListener(new EditChangedListener());

		// 启动短信验证sdk
		SMSSDK.initSDK(this, "1318e188ea3fd",
				"0f0fd827c5d21e622971d107684f5f27"); // 自己申请的

		// SMSSDK.initSDK(this, "11e440d09d630",
		// "46c4a6a718f7204860ad768d5a13c5e1"); //下载的

		// SMSSDK.initSDK(this, "110ee66f30b40",
		// "85ec67aed1b89e3ec73f37b8b89f5142");

		EventHandler eventHandler = new EventHandler() {
			@Override
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};

		// 注册回调监听接口
		SMSSDK.registerEventHandler(eventHandler);
		requestCodeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				resetPhoneNo1 = inputPhoneNo.getText().toString().trim();
				if (resetPhoneNo1.equals("")) {
					showDialog("请输入手机号码！");
					return;
				} else if (!isPhoneNumberValid(resetPhoneNo1)) {
					showDialog("手机号码输入有误！");
					return;
				}

				// 2. 通过sdk发送短信验证
				getContentResolver().registerContentObserver(
						Uri.parse("content://sms"), true,
						new SmsObserver(new Handler()));
				SMSSDK.getVerificationCode("86", resetPhoneNo1);

				// 3. 重新发送验证码，把按钮变成不可点击，并且显示倒计时（正在获取）
				requestCodeBtn.setClickable(false);
				requestCodeBtn.setText("重新发送(" + i + ")");
				new Thread(new Runnable() {
					@Override
					public void run() {
						for (; i > 0; i--) {
							handler.sendEmptyMessage(-9);
							if (i <= 0) {
								break;
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						handler.sendEmptyMessage(-8);
					}
				}).start();

			}
		});

		resetPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String Code2 = sharedPreferences.getString("receivedCode", "");
				String inputCode2 = mEditText.getText().toString().trim();
				String resetPhoneNo2 = inputPhoneNo.getText().toString().trim();
				SMSSDK.submitVerificationCode("86", resetPhoneNo1, inputCode2);
				// Toast.makeText(CarUserResetPassword.this,
				// "inputCode2:"+inputCode2, Toast.LENGTH_SHORT).show();
				if (resetPhoneNo2.equals("")) {
					showDialog("请输入手机号码！\n");
				} else if (!isPhoneNumberValid(resetPhoneNo2)) {
					showDialog("手机号码输入有误！\n");
				} else if (inputCode2.equals("")) {
					resetLayout1.setVisibility(View.INVISIBLE);
					resetLayout2.setVisibility(View.INVISIBLE);
					resetSubmit.setVisibility(View.INVISIBLE);
					showDialog("请输入短信验证码");
				} else if (inputCode2.equals(Code2)) {
					SMSSDK.submitVerificationCode("86", resetPhoneNo1,
							inputCode2);
					resetLayout1.setVisibility(View.VISIBLE);
					resetLayout2.setVisibility(View.VISIBLE);
					resetSubmit.setVisibility(View.VISIBLE);
				} else {
					resetLayout1.setVisibility(View.INVISIBLE);
					resetLayout2.setVisibility(View.INVISIBLE);
					resetSubmit.setVisibility(View.INVISIBLE);
					mTextView.setVisibility(View.VISIBLE);
					mTextView.setTextColor(Color.parseColor("#F33507"));
					mTextView.setText("输入错误！");
				}
			}
		});

		resetSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				newPassword1 = (EditText) findViewById(R.id.user_reset_password1);
				newPassword2 = (EditText) findViewById(R.id.user_reset_password2);
				String cellphoneNO = inputPhoneNo.getText().toString().trim();
				String newPasswordStr1 = newPassword1.getText().toString().trim();
				String newPasswordStr2 = newPassword2.getText().toString().trim();
				if (newPasswordStr1.equals("")) {
					showDialog("密码不能为空!");
				} else if (!validatePwd(newPasswordStr1)) {
					showDialog("请按规定的格式输入密码!");
				} else if (newPasswordStr2.equals("")) {
					showDialog("确认密码不能为空!");
				} else if (!(newPasswordStr1.equals(newPasswordStr2))) {
					showDialog("两次输入的密码不一致!");
				} else {
					showDialog("手机号" + resetPhoneNo1 + "的新密码是：" + newPasswordStr1 + "请牢记！");
					// Toast.makeText(CarUserResetPassword.this,
					// "手机号" + resetPhoneNo1 + "的新密码是：" + newPasswordStr1 +
					// "请牢记！",
					// Toast.LENGTH_SHORT).show();
					HttpPost httpRequest = new HttpPost(
							"http://115.28.16.183:8080/HaoJiaYouOrder1/Register");
					// HttpPost httpRequest = new HttpPost(
					// "http://202.193.74.104:8080/HaoJiaYouOrder1/Register");

					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("UserNo", resetPhoneNo1));
					params.add(new BasicNameValuePair("Password", newPasswordStr1));
					params.add(new BasicNameValuePair("Flag", "2"));
					try {
						if (true) {
							httpRequest.setEntity(new UrlEncodedFormEntity(
									params, HTTP.UTF_8));// 设置请求参数项
							HttpClient httpClient = new DefaultHttpClient();
							HttpResponse httpResponse = httpClient
									.execute(httpRequest);// 执行请求返回响应
							if (httpResponse.getStatusLine().getStatusCode() == 200) {// 判断是否请求成功
								String msg = EntityUtils.toString(httpResponse
										.getEntity());
								String msg1 = null;
								if (msg.contains("success")) {
									msg1 = "success";
								} else if (msg.contains("failure")) {
									msg1 = "failure";
								}
								//Toast.makeText(CarUserResetPassword.this, msg1,Toast.LENGTH_SHORT).show();
								if (msg1.equals("success")) {
									Toast.makeText(CarUserResetPassword.this,
											"密码重置成功！！", Toast.LENGTH_SHORT)
											.show();
									Bundle x = new Bundle();
									x.putString("UserNo", cellphoneNO);
									//x.putString("Password", userPwdStr1);
									Intent intent = new Intent();
									intent.putExtras(x);
									CarUserResetPassword.this.setResult(1, intent);
									finish();
								} else if (msg1.equals("failure")) {
									newPassword1.setText("");
									newPassword2.setText("");
									Toast.makeText(CarUserResetPassword.this,
											"密码重置失败！！", Toast.LENGTH_SHORT)
											.show();
								}
							} else {
								showDialog("密码重置异常！");
							}
						} else {
							showDialog("请求超时！");
							// 关闭连接
						}
					} catch (Exception e) {
						//Toast.makeText(CarUserResetPassword.this,
								//e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}
			}

		});

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == -9) {
				requestCodeBtn.setText("重新发送(" + i + ")");
			} else if (msg.what == -8) {
				requestCodeBtn.setText("获取验证码");
				requestCodeBtn.setClickable(true);
				i = 30;
			} else {
				int event = msg.arg1;
				int result = msg.arg2;
				Object data = msg.obj;
				Log.e("event", "event=" + event);

				if (result == SMSSDK.RESULT_COMPLETE) {
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) { // 短信注册成功后，返回提示
						Toast.makeText(getApplicationContext(), "提交验证码成功",
								Toast.LENGTH_SHORT).show(); // 提交验证码成功

					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						Toast.makeText(getApplicationContext(), "验证码已经发送",
								Toast.LENGTH_SHORT).show();
					} else {
						((Throwable) data).printStackTrace();
					}
				}
			}
		}
	};

	// 获取短信中的验证码
	private class SmsObserver extends ContentObserver {
		private String validateCode;

		public SmsObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			StringBuilder sb = new StringBuilder();
			Cursor cursor = getContentResolver().query(
					Uri.parse("content://sms/inbox"), null, null, null, null);
			cursor.moveToNext();
			sb.append("body=" + cursor.getString(cursor.getColumnIndex("body")));
			Pattern pattern = Pattern.compile("[^0-9]");
			Matcher matcher = pattern.matcher(sb.toString());
			validateCode = matcher.replaceAll("");
			editor.putString("receivedCode", validateCode);
			editor.commit();
			mEditText.setText(validateCode); // 自动填充验证码
			cursor.close();
			super.onChange(selfChange);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();
		getContentResolver()
				.unregisterContentObserver(new SmsObserver(handler));

	}

	// 验证手机号是否合法
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

	// 验证密码否符合要求
	public static boolean validatePwd(String password) {
		String validateFormat = "[a-zA-Z0-9]{5,15}";// 验证密码6-16字母和数字组成
		Pattern pattern = Pattern.compile(validateFormat);
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}

	private void showDialog(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("友情提醒");// 设置标题
		builder.setIcon(R.drawable.logo);// 设置图标
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
			CarUserResetPassword.this.setResult(0);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class EditChangedListener implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String inputCode1 = mEditText.getText().toString().trim();
			String Code1 = sharedPreferences.getString("receivedCode", "");

			if ((mEditText.length() == 4) && (inputCode1.equals(Code1))) {
				mTextView.setVisibility(View.VISIBLE);
				mTextView.setTextColor(Color.parseColor("#22CB22"));
				mTextView.setText("输入正确！");
			} else if ((mEditText.length() == 4) && !(inputCode1.equals(Code1))) {
				mTextView.setVisibility(View.VISIBLE);
				mTextView.setTextColor(Color.parseColor("#F33507"));
				mTextView.setText("输入错误！");
			} else {
				mTextView.setVisibility(View.INVISIBLE);
				mTextView.setText("");
			}
		}
	}

/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.car_user_reset_password, menu);
		return true;
	}*/
}
