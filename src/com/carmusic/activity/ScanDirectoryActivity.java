package com.carmusic.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import cn.guet.haojiayou.R;

import com.carmusic.adapter.ScanListAdapter;
import com.carmusic.entity.ScanData;
import com.carmusic.util.Common;
import com.carmusic.util.ScanMusicFilterFile;

public class ScanDirectoryActivity extends SettingActivity {
	private ListView lv_scan_music_list;
	private File[] files;
	private List<ScanData> data;
	private ScanMusicFilterFile myFilterFile;//过滤文件
	private File currrentFile;//当前文件的路径
	private ScanListAdapter adapter;
	private String rootFilePath;//父级文件的路径
	private String rs="";//返回结果

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_scan_music_adddirectory);
		
		resultCode=ScanMusicActivity.SCAN_MUSIC_CANCEL;
		setBackButton();
		setTopTitle(getResources().getString(R.string.scan_directory_title));
		
		//检测SD卡
		if(!Common.isExistSdCard()){
			Toast.makeText(this, "请先插入SD卡", Toast.LENGTH_SHORT).show();
			setResult(RESULT_CANCELED);
			this.finish();
		}
		
		lv_scan_music_list=(ListView)this.findViewById(R.id.lv_scan_music_list);
		
		((Button)this.findViewById(R.id.btn_scan_add)).setOnClickListener(listener);
		((Button)this.findViewById(R.id.btn_scan_back)).setOnClickListener(listener);
		((Button)this.findViewById(R.id.btn_scan_directory_goup)).setOnClickListener(listener);

		//获取默认音乐目录的选取结果
		rs=getIntent().getStringExtra("rs");
		
		//读取mnt根目录
		myFilterFile=new ScanMusicFilterFile();
		lv_scan_music_list=(ListView)this.findViewById(R.id.lv_scan_music_list);
		currrentFile=Environment.getExternalStorageDirectory().getParentFile();//获取sd卡路径的父目录文件
		rootFilePath=currrentFile.getPath().toLowerCase();//设置根目录的路径
		data=new ArrayList<ScanData>();
		getFilePath(currrentFile);//遍历目录
		adapter=new ScanListAdapter(this,data);
		adapter.setCheckFilePath(rs);
		lv_scan_music_list.setAdapter(adapter);
		lv_scan_music_list.setOnItemClickListener(itemClickListener);
	}

	private OnClickListener listener=new OnClickListener() {
		
		public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.btn_scan_add:
				//添加扫描目录
				Intent addit=new Intent(ScanDirectoryActivity.this,ScanMusicActivity.class);
				addit.putExtra("rs", adapter.getCheckFilePath());
				setResult(ScanMusicActivity.SCAN_MUSIC_OK, addit);
				finish();
				break;
			case R.id.btn_scan_directory_goup:
				//判断是否是父级目录
				if(!currrentFile.getPath().toLowerCase().equals(rootFilePath)){
					currrentFile=currrentFile.getParentFile();
					getFilePath(currrentFile);
					adapter.notifyDataSetChanged();
				}
				break;
			case R.id.btn_scan_back:
				setResult(-1);
				finish();
				break;
			default:
				break;
			}
		}
	};
	
	private OnItemClickListener itemClickListener=new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			//进入下一级目录
			currrentFile=files[position];
			rs=adapter.getCheckFilePath();
			getFilePath(currrentFile);
			adapter.notifyDataSetChanged();
		}
		
	};
	
	private void getFilePath(File parent){
		data.clear();
		files=parent.listFiles(myFilterFile);
		for(File file:files){
			String fp=file.getPath().toLowerCase();
			//判断是否是选中的
			ScanData d=new ScanData(fp+"/",false);
			if(rs.contains("$"+fp+"/$")){
				d.setChecked(true);
			}
			data.add(d);
		}
	}
}
