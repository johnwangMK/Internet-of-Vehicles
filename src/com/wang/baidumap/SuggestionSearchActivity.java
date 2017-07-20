package com.wang.baidumap;

import java.util.ArrayList;
import java.util.List;

import cn.guet.haojiayou.R;
import cn.guet.haojiayou.adapter.SuggestionSearchAdapter;
import cn.guet.haojiayou.ui.NearByFragment;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.wang.entity.SuggestionInfo;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class SuggestionSearchActivity extends Activity implements OnGetSuggestionResultListener {
	
	private SuggestionSearch mSuggestionSearch = null; 
	private EditText keyWordsText = null;
	private Button search_btn = null;
	private ListView suggestion_listview;
	private ImageView suggestion_back_btn;
	private List<SuggestionInfo> resultList = new ArrayList<SuggestionInfo>();
	private SuggestionSearchAdapter sugAdapter = null;
	private FragmentTransaction transaction;
	private FragmentManager manager;
	private int resultCode;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggestion_search);
		manager = getFragmentManager();
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);
		keyWordsText = (EditText) findViewById(R.id.suggestion_search_text);
		search_btn = (Button) findViewById(R.id.suggestion_search_btn);
		suggestion_listview = (ListView) findViewById(R.id.suggestion_listview);
		suggestion_back_btn = (ImageView) findViewById(R.id.back_suggestion_search);
		
		Bundle bundle = this.getIntent().getExtras();
		resultCode = bundle.getInt("tag");
		
		keyWordsText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				String key = keyWordsText.getText().toString();
				if(key != null && !key.equals("")){
					mSuggestionSearch.requestSuggestion(new SuggestionSearchOption().keyword(key).city("桂林"));
					sugAdapter = new SuggestionSearchAdapter(resultList, SuggestionSearchActivity.this);
					suggestion_listview.setAdapter(sugAdapter);
				}
			}
		});
		search_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String key = keyWordsText.getText().toString();
				if(key != null && !key.equals("")){
					mSuggestionSearch.requestSuggestion(new SuggestionSearchOption().keyword(key).city("桂林"));
					sugAdapter = new SuggestionSearchAdapter(resultList, SuggestionSearchActivity.this);
					suggestion_listview.setAdapter(sugAdapter);
				}else{
					Toast.makeText(SuggestionSearchActivity.this, "请输入目的地", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		suggestion_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				//NearByFragment nearByFragment = new NearByFragment();
				//Intent intent = new Intent(SuggestionSearchActivity.this,RouteActivity.class);
				Intent intent = new Intent();
				//Bundle bundle = new Bundle();
				double longitude = resultList.get(position).getPt().longitude;
				double latitude = resultList.get(position).getPt().latitude;
				String key = resultList.get(position).getKey();
				//bundle.putDouble("longitude", longitude);
				//bundle.putDouble("latitude", latitude);
				//bundle.putString("key", key);
				//bundle.putInt("tag", tag);
				//intent.putExtras(bundle);
				//startActivity(intent);
				intent.putExtra("key", key);
				intent.putExtra("longitude", longitude);
				intent.putExtra("latitude", latitude);
				SuggestionSearchActivity.this.setResult(resultCode, intent);
				finish();
				
			}
		});
		suggestion_back_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SuggestionSearchActivity.this.setResult(0);
				finish();
				
			}
		});
	}
	
	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if(res == null||res.getAllSuggestions() == null){
			Toast.makeText(SuggestionSearchActivity.this, "未找到结果", Toast.LENGTH_SHORT).show();
			return;
		}
		sugAdapter.removeAll();
/*		for(int i = 0;i<res.getAllSuggestions().size();i++){
			SuggestionInfo si = new SuggestionInfo();
			si.setKey(res.getAllSuggestions().get(i).key);
			si.setDistrict(res.getAllSuggestions().get(i).district);
			si.setPt(res.getAllSuggestions().get(i).pt);
			resultList.add(si);
		}*/
		for(SuggestionResult.SuggestionInfo info : res.getAllSuggestions()){
				SuggestionInfo si = new SuggestionInfo();
				if(info.pt != null){
					si.setKey(info.key);
					si.setDistrict(info.district);
					si.setPt(info.pt);
					resultList.add(si);
				}
			//sugAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	

}
