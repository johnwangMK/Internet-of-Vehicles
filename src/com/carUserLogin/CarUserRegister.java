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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.guet.haojiayou.R;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class CarUserRegister extends Activity implements OnClickListener {
	int i = 30;
	private boolean showPassword1 = true;
	private boolean showPassword2 = true;

	private EditText inputCodeEt; // 验证码输入框
	private EditText userNo_r; // 手机号输入框
	private EditText userPassword1;
	private EditText userPassword2;

	private TextView mTextView; // 显示验证码输入状态信息

	private Button requestCodeBtn; // 获取验证码按钮
	private Button btn_register;
	private Button show_password1;
	private Button show_password2;
	private Button read_treaty;

	private CheckBox checkBox_read;

	SharedPreferences sharedPreferences;
	Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.car_user_register);
		init();
	}

	private void init() {
		inputCodeEt = (EditText) findViewById(R.id.user_reister_yanzma);
		userNo_r = (EditText) findViewById(R.id.user_register_cellphoneNo);
		userPassword1 = (EditText) findViewById(R.id.user_register_password1);
		userPassword2 = (EditText) findViewById(R.id.user_register_password2);
		mTextView = (TextView) findViewById(R.id.tv_codestatus);

		show_password1 = (Button) findViewById(R.id.btn_reister_show_password1);
		show_password2 = (Button) findViewById(R.id.btn_reister_show_password2);
		read_treaty = (Button) findViewById(R.id.register_txt_treaty);
		requestCodeBtn = (Button) findViewById(R.id.btn_reister_get_yanzma);
		btn_register = (Button) findViewById(R.id.btn_user_register);

		checkBox_read = (CheckBox) findViewById(R.id.register_chk_read_treaty);
		requestCodeBtn.setOnClickListener(this);
		show_password1.setOnClickListener(this);
		show_password2.setOnClickListener(this);
		btn_register.setOnClickListener(this);
		read_treaty.setOnClickListener(this);

		sharedPreferences = getSharedPreferences("validateCodeData",
				MODE_PRIVATE);
		editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
		inputCodeEt.addTextChangedListener(new EditChangedListener());

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
	}

	public void onClick(View v) {
		final String phoneNums = userNo_r.getText().toString().trim();
		final String userPwdStr1 = userPassword1.getText().toString().trim();
		final String userPwdStr2 = userPassword2.getText().toString().trim();
		final String validateCode = inputCodeEt.getText().toString().trim();
		
		switch (v.getId()) {
		case R.id.btn_reister_show_password1: {
			if (showPassword1) {// 显示密码
				showPassword1 = !showPassword1;
				show_password1.setText("隐藏");
				userPassword1
						.setTransformationMethod(HideReturnsTransformationMethod
								.getInstance());
				userPassword1.setSelection(userPassword1.getText().toString()
						.length());
			} else {// 隐藏密码
				showPassword1 = !showPassword1;
				show_password1.setText("显示");
				userPassword1
						.setTransformationMethod(PasswordTransformationMethod
								.getInstance());
				userPassword1.setSelection(userPassword1.getText().toString()
						.length());
			}
			break;
		}

		case R.id.btn_reister_show_password2: {
			if (showPassword2) {// 显示密码
				showPassword2 = !showPassword2;
				show_password2.setText("隐藏");
				userPassword2
						.setTransformationMethod(HideReturnsTransformationMethod
								.getInstance());
				userPassword2.setSelection(userPassword2.getText().toString()
						.length());
			} else {// 隐藏密码
				showPassword2 = !showPassword2;
				show_password2.setText("显示");
				userPassword2
						.setTransformationMethod(PasswordTransformationMethod
								.getInstance());
				userPassword2.setSelection(userPassword2.getText().toString()
						.length());
			}
			break;
		}

		case R.id.register_txt_treaty:
			showDialog1("一、总则\n"
					+ "1.1 好加油的所有权和运营权归好加油公司所有。\n"
					+ "1.2 用户在注册之前，应当仔细阅读本协议，并同意遵守本协议后方可成为注册用户。一旦注册成功，则用户与好加油之间自动形成协议关系，用户应当受本协议的约束。\n"
					+ "1.3 本协议可由好加油公司随时更新，用户应当及时关注并同意本公司不承担通知义务。本系统的通知、公告、声明或其它类似内容是本协议的一部分。\n"
					+ "二、服务内容\n"
					+ "2.1 好加油的具体内容由本公司根据实际情况提供。\n "
					+ "2.2 本公司仅提供相关的网络服务，除此之外与相关网络服务有关的设备及所需的费用均应由用户自行负担。\n"
					+ "三、用户帐号\n"
					+ "3.1 经本系统完成注册程序并通过认证的用户即成为正式用户，可以获得本系统规定用户所应享有的一切权限；未经认证仅享有本系统规定的部分权限。好加油有权对权限设计进行变更。\n"
					+ "3.2 用户只能按照注册要求使用实名制手机号注册。用户有义务保证密码和帐号的安全，用户利用该密码和帐号所进行的一切活动引起的任何损失或损害，由用户自行承担全部责任，本公司不承担任何责任。如用户发现帐号遭到未授权的使用或发生其他任何安全问题，应立即修改帐号密码并妥善保管，如有必要，请通知本公司。因黑客行为或用户的保管疏忽导致帐号非法使用，本公司不承担任何责任。\n"
					+ "四、使用规则\n"
					+ "4.1 遵守中华人民共和国相关法律法规，包括但不限于《中华人民共和国计算机信息系统安全保护条例》、《计算机软件保护条例》、《最高人民法院关于审理涉及计算机网络著作权纠纷案件适用法律若干问题的解释(法释[2004]1号)》、《全国人大常委会关于维护互联网安全的决定》、《互联网电子公告服务管理规定》、《互联网新闻信息服务管理规定》、《互联网著作权行政保护办法》和《信息网络传播权保护条例》等有关计算机互联网规定和知识产权的法律和法规、实施办法。\n"
					+ "4.2 用户对其自行发表、上传或传送的内容负全部责任，所有用户不得在本系统任何页面发布、转载、传送含有下列内容之一的信息，否则本公司有权自行处理并不通知用户：\n"
					+ "(1)违反宪法确定的基本原则的；\n"
					+ "(2)危害国家安全，泄漏国家机密，颠覆国家政权，破坏国家统一的；\n"
					+ "(3)损害国家荣誉和利益的；\n"
					+ "(4)煽动民族仇恨、民族歧视，破坏民族团结的；\n"
					+ "(5)破坏国家宗教政策，宣扬邪教和封建迷信的；\n"
					+ "(6)散布谣言，扰乱社会秩序，破坏社会稳定的；\n"
					+ "(7)散布淫秽、色情、赌博、暴力、恐怖或者教唆犯罪的；\n"
					+ "(8)侮辱或者诽谤他人，侵害他人合法权益的；\n"
					+ "(9)煽动非法集会、结社、游行、示威、聚众扰乱社会秩序的；\n"
					+ "(10)以非法民间组织名义活动的；\n"
					+ "(11)含有法律、行政法规禁止的其他内容的。\n"
					+ "4.3 用户承诺对其发表或者上传于本系统的所有信息(即属于《中华人民共和国著作权法》规定的作品，包括但不限于文字、图片、音乐、电影、表演和录音录像制品和电脑程序等)均享有完整的知识产权，或者已经得到相关权利人的合法授权；如用户违反本条规定造成本公司被第三人索赔的，用户应全额补偿本公司一切费用(包括但不限于各种赔偿费、诉讼代理费及为此支出的其它合理费用)；\n"
					+ "4.4 当第三方认为用户发表或者上传于本系统的信息侵犯其权利，并根据《信息网络传播权保护条例》或者相关法律规定向本公司发送权利通知书时，用户同意本公司可以自行判断决定删除涉嫌侵权信息，除非用户提交书面证据材料排除侵权的可能性，本公司将不会自动恢复上述删除的信息；\n"					
					+ "4.5 如用户在使用网络服务时违反上述任何规定，本公司有权要求用户改正或直接采取一切必要的措施(包括但不限于删除用户张贴的内容、暂停或终止用户使用网络服务的权利)以减轻用户不当行为而造成的影响。\n"
					+ "五、隐私保护\n"
					+ "5.1 本公司不对外公开或向第三方提供单个用户的注册资料及用户在使用网络服务时存储在本公司的非公开内容，但下列情况除外：\n"
					+ "(1)事先获得用户的明确授权；\n"
					+ "(2)根据有关的法律法规要求；\n"
					+ "(3)按照相关政府主管部门的要求；\n"
					+ "(4)为维护社会公众的利益。\n"
					+ "5.2 本公司可能会与第三方合作向用户提供相关的网络服务，在此情况下，如该第三方同意承担与本公司同等的保护用户隐私的责任，则本公司有权将用户的注册资料等提供给该第三方。\n"
					+ "5.3 在不透露单个用户隐私资料的前提下，本公司有权对整个用户数据库进行分析并对用户数据库进行商业上的利用。\n"
					+ "六、版权声明\n"
					+ "6.1 本系统的文字、图片、音频、视频等版权均归好加油公司享有或与作者共同享有，未经本公司许可，不得任意转载。\n"
					+ "6.2 本系统特有的标识、版面设计、编排方式等版权均属好加油公司享有，未经本公司许可，不得任意复制或转载。\n"
					+ "6.3 使用本系统的任何内容均应注明“来源于好加油”及署上作者姓名，按法律规定需要支付稿酬的，应当通知本公司及作者及时支付稿酬，并独立承担一切法律责任。\n"
					+ "6.4 本公司享有所有作品用于其它用途的优先权，包括但不限于网站、电子杂志、平面出版等，但在使用前会通知作者，并按同行业的标准支付稿酬。\n"
					+ "6.5 本系统所有内容仅代表作者自己的立场和观点，与本公司无关，由作者本人承担一切法律责任。\n"
					+ "6.6 恶意转载本系统内容的，本公司保留将其诉诸法律的权利。\n"
					+ "七、责任声明\n"
					+ "7.1 用户明确同意其使用本公司网络服务所存在的风险及一切后果将完全由用户本人承担，好加油对此不承担任何责任。\n"
					+ "7.2 本系统无法保证网络服务一定能满足用户的要求，也不保证网络服务的及时性、安全性、准确性。\n"
					+ "7.3 本系统不保证为方便用户而设置的外部链接的准确性和完整性，同时，对于该外部链接指向的不由本系统实际控制的任何网页上的内容，本公司不承担任何责任。\n"
					+ "7.4 对于因不可抗力或本公司不能控制的原因造成的网络服务中断或其它缺陷，本公司不承担任何责任，但将尽力减少因此而给用户造成的损失和影响。\n"
					+ "7.5 对于本系统向用户提供的下列产品或者服务的质量缺陷本身及其引发的任何损失，本公司无需承担任何责任：\n"
					+ "(1)本系统向用户免费提供的各项网络服务；\n"
					+ "(2)本系统向用户赠送的任何产品或者服务。\n"
					+ "7.6 本公司有权于任何时间暂时或永久修改或终止本服务(或其任何部分)，而无论其通知与否，本公司对用户和任何第三人均无需承担任何责任。\n"
					+ "八、附则\n"
					+ "8.1 本协议的订立、执行和解释及争议的解决均应适用中华人民共和国法律。\n"
					+ "8.2 如本协议中的任何条款无论因何种原因完全或部分无效或不具有执行力，本协议的其余条款仍应有效并且有约束力。\n"
					+ "8.3 本协议解释权及修订权归好加油公司所有。");
			break;

		case R.id.btn_reister_get_yanzma: {
			// 1. 通过规则判断手机号
			if (phoneNums.equals("")) {
				showDialog("请输入手机号码！");
				return;
			} else if (!isPhoneNumberValid(phoneNums)) {
				showDialog("手机号码输入有误！");
				return;
			}

			// 2. 通过sdk发送短信验证
			getContentResolver().registerContentObserver(
					Uri.parse("content://sms"), true,
					new SmsObserver(new Handler()));
			SMSSDK.getVerificationCode("86", phoneNums);

			// 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
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
			break;
		}

		case R.id.btn_user_register: {
			sharedPreferences = getSharedPreferences("validateCodeData",
					MODE_PRIVATE);
			String yanzhenCode = sharedPreferences
					.getString("validateCode", "");
			String inputCode2 = inputCodeEt.getText().toString().trim();
			SMSSDK.submitVerificationCode("86", phoneNums, inputCode2);
			if (phoneNums.equals("")) {
				showDialog("请输入手机号码！");
			} else if (!isPhoneNumberValid(phoneNums)) {
				showDialog("手机号码输入有误！");
			} else if (userPwdStr1.equals("")) {
				showDialog("密码不能为空!");
			} else if (!validatePwd(userPwdStr1)) {
				showDialog("请按规定的格式输入密码!");
			} else if (userPwdStr2.equals("")) {
				showDialog("确认密码不能为空!");
			} else if (!(userPwdStr1.equals(userPwdStr2))) {
				showDialog("两次输入的密码不一致!");
			} else if (validateCode.equals("")) {
				showDialog("请输入短信验证码!");
			} else if (!(validateCode.equals(yanzhenCode))) {
				mTextView.setVisibility(View.VISIBLE);
				mTextView.setTextColor(Color.parseColor("#F33507"));
				mTextView.setText("输入错误！");
			} else if (!checkBox_read.isChecked()) {
				showDialog("请确认已阅读并同意《用户注册协议》!");
			} else {
				//Toast.makeText(CarUserRegister.this,"用户名：" + phoneNums + "\n密码：" + userPwdStr1,Toast.LENGTH_SHORT).show();
				HttpPost httpRequest = new HttpPost(
						"http://115.28.16.183:8080/HaoJiaYouOrder1/Register");
				// HttpPost httpRequest = new HttpPost(
				// "http://202.193.74.104:8080/HaoJiaYouOrder1/Register");

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("UserNo", phoneNums));
				params.add(new BasicNameValuePair("Password", userPwdStr1));
				params.add(new BasicNameValuePair("Flag", "1"));
				try {
					if (true) {
						httpRequest.setEntity(new UrlEncodedFormEntity(params,
								HTTP.UTF_8));// 设置请求参数项
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
							//Toast.makeText(CarUserRegister.this, msg1,Toast.LENGTH_SHORT).show();
							if (msg1.equals("success")) {
								//将注册的用户名和密码保存到SharedPreferences中
								//SharedPreferencesUtils.put(CarUserRegister.this, "UserNo", phoneNums);
								//SharedPreferencesUtils.put(CarUserRegister.this, "Password", userPwdStr1);
								Bundle x = new Bundle();
								x.putString("UserNo", phoneNums);
								x.putString("Password", userPwdStr1);
								Intent intent = new Intent();
								intent.putExtras(x);
								CarUserRegister.this.setResult(1, intent);
								Toast.makeText(CarUserRegister.this, "注册成功！！",
										Toast.LENGTH_SHORT).show();

								finish();
							} else if (msg1.equals("failure")) {
								showDialog("该手机号已被注册，请重新输入！\n");
								userNo_r.setText("");
								userPassword1.setText("");
								userPassword2.setText("");
							}
						} else {
							showDialog("注册失败！\n");
						}
					} else {
						showDialog("请求超时！\n");
						// 关闭连接
					}

				} catch (Exception e) {
					//Toast.makeText(CarUserRegister.this, e.getMessage(),Toast.LENGTH_SHORT).show();
				}
			}

			break;
		}
		}
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
		private String yanzhengma;

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
			yanzhengma = matcher.replaceAll("");
			editor.putString("validateCode", yanzhengma);
			editor.commit();
			inputCodeEt.setText(yanzhengma);
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

	public static boolean validatePwd(String password) {
		String validateFormat = "[a-zA-Z0-9]{5,15}";// 验证密码6-16字母和数字组成
		Pattern pattern = Pattern.compile(validateFormat);
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}

	private void showDialog(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("温馨提示");// 设置标题
		builder.setMessage(msg).setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showDialog1(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("用户注册协议");// 设置标题
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
			CarUserRegister.this.setResult(0);
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
		public void beforeTextChanged(CharSequence s, int start, int count,	int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String inputCode1 = inputCodeEt.getText().toString().trim();
			String Code1 = sharedPreferences.getString("validateCode", "");
			if ((inputCodeEt.length() == 4) && (inputCode1.equals(Code1))) {
				mTextView.setVisibility(View.VISIBLE);
				mTextView.setTextColor(Color.parseColor("#22CB22"));
				mTextView.setText("输入正确！");
			} else if ((inputCodeEt.length() == 4)
					&& !(inputCode1.equals(Code1))) {
				mTextView.setVisibility(View.VISIBLE);
				mTextView.setTextColor(Color.parseColor("#F33507"));
				mTextView.setText("输入错误！");
			} else {
				mTextView.setVisibility(View.INVISIBLE);
				mTextView.setText("");
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.car_user_register, menu);
		return true;
	}
}