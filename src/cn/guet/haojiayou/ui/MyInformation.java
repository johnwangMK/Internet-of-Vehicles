package cn.guet.haojiayou.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.CityInfoJson;

import cn.guet.haojiayou.R;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyInformation extends Activity implements OnClickListener{
	private LinearLayout ll_myinfo_name;
	private LinearLayout ll_myinfo_sex;
	private LinearLayout ll_myinfo_age;
	private LinearLayout ll_myinfo_job;
	private LinearLayout ll_myinfo_personsign;
	private LinearLayout ll_myinfo_zone;
	private TextView myinfo_name;
	private TextView myinfo_sex;
	private TextView myinfo_age;
	private TextView myinfo_job;
	private TextView myinfo_zone;
	private TextView myinfo_signature;
	private TextView myinfo_phonenumber;
	private ImageView back_myinfo;
	
	private String sex = null;
	private int year;
	private int month;
	private int day;
	SharedPreferences sharedPreferences;
	Editor editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_information);
		
		sharedPreferences = getSharedPreferences("carUserLoginData",MODE_PRIVATE);
		String name = sharedPreferences.getString("name", "好加油");
		String sex = sharedPreferences.getString("sex", "男");
		int age = sharedPreferences.getInt("age", 22);
		String job = sharedPreferences.getString("job", "学生");
		String zone = sharedPreferences.getString("zone", "广西桂林");
		String signature = sharedPreferences.getString("signature", "好加油，加好油，加油好");
		String phonenumber = sharedPreferences.getString("userphoneNo", "13800138000");
		editor = sharedPreferences.edit();
		
		back_myinfo = (ImageView) findViewById(R.id.back_myinfo);
		ll_myinfo_name = (LinearLayout) findViewById(R.id.LL_myinfo_name);
		ll_myinfo_sex = (LinearLayout) findViewById(R.id.LL_myinfo_sex);
		ll_myinfo_age = (LinearLayout) findViewById(R.id.LL_myinfo_age);
		ll_myinfo_job = (LinearLayout) findViewById(R.id.LL_myinfo_job);
		ll_myinfo_zone = (LinearLayout) findViewById(R.id.LL_myinfo_zone);
		ll_myinfo_personsign = (LinearLayout) findViewById(R.id.LL_myinfo_personsign);
		
		myinfo_name = (TextView) findViewById(R.id.myinfo_name);
		myinfo_sex = (TextView) findViewById(R.id.myinfo_sex);
		myinfo_age = (TextView) findViewById(R.id.myinfo_age);
		myinfo_job = (TextView) findViewById(R.id.myinfo_job);
		myinfo_zone = (TextView) findViewById(R.id.myinfo_zone);
		myinfo_signature = (TextView) findViewById(R.id.myinfo_personsign);
		myinfo_phonenumber = (TextView) findViewById(R.id.myinfo_phonenumber);
		
		myinfo_name.setText(name);
		myinfo_sex.setText(sex);
		myinfo_age.setText(age + "");
		myinfo_job.setText(job);
		myinfo_zone.setText(zone);
		myinfo_signature.setText(signature);
		myinfo_phonenumber.setText(phonenumber);
		
		ll_myinfo_name.setOnClickListener(this);
		ll_myinfo_sex.setOnClickListener(this);
		back_myinfo.setOnClickListener(this);
		ll_myinfo_age.setOnClickListener(this);
		ll_myinfo_job.setOnClickListener(this);
		ll_myinfo_personsign.setOnClickListener(this);
		ll_myinfo_zone.setOnClickListener(this);
		
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back_myinfo:
			finish();
			break;
		case R.id.LL_myinfo_name:
			final EditText name_text = new EditText(this);
			name_text.setBackground(null);
			name_text.setSingleLine(true);
			new AlertDialog.Builder(this).setTitle("修改昵称").setView(name_text)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = name_text.getText().toString();
						if(name != null && name.length() <= 10){
							myinfo_name.setText(name);
							editor.remove("name");
							editor.putString("name", name);
							editor.commit();
						}
						
					}
				}).setNegativeButton("取消", null).create().show();
			break;
			
		case R.id.LL_myinfo_sex:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final String[] sexchoice = {"男","女","未知"};
			builder.setTitle("选择性别");
			builder.setSingleChoiceItems(sexchoice, 1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					sex = sexchoice[which];
					
				}
			});
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					myinfo_sex.setText(sex);
					editor.remove("sex");
					editor.putString("sex", sex);
					editor.commit();
				}
			});
			builder.setNegativeButton("取消", null);
			builder.create().show();
			break;
			
		case R.id.LL_myinfo_age:
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			Date date = new Date();
			calendar.setTime(date);
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);
			DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
				
				@Override
				public void onDateSet(DatePicker view, int myyear, int mymonth, int myday) {
					/*if(myyear <= year || mymonth <= month || myday <= day){
						int age = year - myyear;
						myinfo_age.setText(age + "");
						editor.remove("age");
						editor.putInt("age", age);
						editor.commit();
					}*/
					if(myyear <= year){
						if(mymonth <= month){
							if(myday <= day){
								int age = year - myyear;
								myinfo_age.setText(age + "");
								editor.remove("age");
								editor.putInt("age", age);
								editor.commit();
							}
						}
					}
				}
			}, year, month, day);
			datePickerDialog.show();
			break;
		case R.id.LL_myinfo_job:
			final EditText job_text = new EditText(this);
			job_text.setBackground(null);
			job_text.setSingleLine(true);
			new AlertDialog.Builder(this).setTitle("填写职业").setView(job_text)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String job = job_text.getText().toString();
						if(job != null && job.length() <= 10){
							myinfo_job.setText(job);
							editor.remove("job");
							editor.putString("job", job);
							editor.commit();
						}
						
					}
				}).setNegativeButton("取消", null).create().show();
			break;
		case R.id.LL_myinfo_personsign:
			final EditText sign_text = new EditText(this);
			sign_text.setBackground(null);
			sign_text.setSingleLine(true);
			new AlertDialog.Builder(this).setTitle("编辑签名").setView(sign_text)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String sign = sign_text.getText().toString();
						if(sign != null && sign.length() <= 20){
							myinfo_signature.setText(sign);
							editor.remove("signature");
							editor.putString("signature", sign);
							editor.commit();
						}
						
					}
				}).setNegativeButton("取消", null).create().show();
			break;
		case R.id.LL_myinfo_zone:
			break;
		default:
			break;
		}
		
	}

}
