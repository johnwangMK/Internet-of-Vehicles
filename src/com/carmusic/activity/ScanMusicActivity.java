package com.carmusic.activity;

import java.util.List;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import cn.guet.haojiayou.R;

import com.carmusic.adapter.ScanListAdapter;
import com.carmusic.custom.CarMDialog;
import com.carmusic.entity.ScanData;
import com.carmusic.util.Common;
import com.carmusic.util.MusicManager;

public class ScanMusicActivity extends SettingActivity {
	private ListView lv_scan_music_list;
	private MusicManager musicManager;
	private ScanListAdapter adapter;
	private List<ScanData> datas;
	
	public static final int SCAN_MUSIC_OK=1;
	public static final int SCAN_MUSIC_CANCEL=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_scan_music);

		setBackButton();
		setTopTitle(getResources().getString(R.string.scan_title));
		
		musicManager=new MusicManager(this);
		
		lv_scan_music_list=(ListView)this.findViewById(R.id.lv_scan_music_list);
		//获取音乐媒体库目录
		datas=musicManager.searchByDirectory();
		adapter=new ScanListAdapter(this,datas);
		lv_scan_music_list.setAdapter(adapter);
		lv_scan_music_list.setOnItemClickListener(itemClickListener);

		((Button)this.findViewById(R.id.btn_scan_add)).setOnClickListener(listener);
		((Button)this.findViewById(R.id.btn_scan_ok)).setOnClickListener(listener);
	}
	
	private OnClickListener listener=new OnClickListener() {
		
		public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.btn_scan_add:
				//添加扫描目录
				Intent it=new Intent(ScanMusicActivity.this,ScanDirectoryActivity.class);
				it.putExtra("rs",adapter.getCheckFilePath());
				startActivityForResult(it, SCAN_MUSIC_OK);
				break;
			case R.id.btn_scan_ok:
				//开始扫描歌曲
				if(progressDialog==null){
					progressDialog=new ProgressDialog(ScanMusicActivity.this);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				}
				progressDialog.setTitle("扫描歌曲");
				progressDialog.setMessage("正在扫描歌曲,请稍后...");
				progressDialog.setCancelable(false);
				progressDialog.show();
				new Thread(runnable).start();
				break;
			default:
				break;
			}
			
		}
	};
	private Runnable runnable=new Runnable() {
		
		public void run() {
			musicManager.scanMusic(adapter.getCheckFilePath(),handler);
		}
	};
	private ProgressDialog progressDialog;
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data=msg.getData();
			String rs=data.getString("rs");
			switch (msg.what) {
			case 0:
				progressDialog.setMessage(rs);
				break;
			case 1:
				progressDialog.cancel();
				progressDialog.dismiss();
				CarMDialog dialog=Common.createConfirmDialog(ScanMusicActivity.this, "确定", "扫描歌曲结果", 
						rs, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						dialog.dismiss();
						Intent it=new Intent(ScanMusicActivity.this,ListMainActivity.class);
						setResult(1, it);
						finish();
					}
				});
				dialog.show();
				break;
			default:
				break;
			}
		}
		
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode==SCAN_MUSIC_OK&&resultCode==SCAN_MUSIC_OK){//添加扫描目录
			String rs=data.getStringExtra("rs");
			String[] filePaths=rs.split("\\$*\\$");//分隔出文件路径
			datas.clear();
			for (int i = 0; i < filePaths.length; i++) {
				if(!filePaths[i].trim().equals("")){
					//重新构建ListView
					datas.add(new ScanData(filePaths[i],true));
					adapter.notifyDataSetChanged();
				}
			}
			adapter.setCheckFilePath(rs);
		}
	}

	private OnItemClickListener itemClickListener=new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			CheckBox cb_scan_item=(CheckBox)view.findViewById(R.id.cb_scan_item);
			cb_scan_item.setChecked(!cb_scan_item.isChecked());
		}
		
	};
}
