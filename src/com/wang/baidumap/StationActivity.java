package com.wang.baidumap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import cn.guet.haojiayou.R;
import cn.guet.haojiayou.adapter.StationListAdapter;

import com.google.gson.Gson;
import com.wang.cache.ACache;
import com.wang.juhe.Data;
import com.wang.juhe.Juhe;
import com.wang.juhe.StationInfo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class StationActivity extends Activity {
	public double mLongitude;
	public double mLatitude;
	public ListView listView;
	public ImageView back_btn;
	public int length;
	public int List_length;
	public List<Map<String,Object>> mList = null;
	private View popLoader;
	Handler cwjHandler;

	public List<Map<String,Object>> sortList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.station);
		Bundle bundle  = this.getIntent().getExtras();
		mLongitude = bundle.getDouble("lon");
		mLatitude = bundle.getDouble("lat");
		popLoader = findViewById(R.id.popLoader_station);
		listView = (ListView) findViewById(R.id.station_listView);
		popLoader.setVisibility(View.VISIBLE);
		cwjHandler = new Handler();
		try {
			mList = new MyTask().execute().get();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}
		try {
			if(mList != null){
				listView.setAdapter(new StationListAdapter(mList, StationActivity.this));
			}else{
				Toast.makeText(StationActivity.this, "请检查你的网络", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(StationActivity.this, StationDetailActivity.class);
				Bundle  bundle = new Bundle();
				bundle.putString("gs_name", (String) mList.get(position).get("name"));
				bundle.putString("gs_address", (String) mList.get(position).get("address"));
				bundle.putString("gastprice_90", (String) mList.get(position).get("gastprice_90"));
				bundle.putString("gastprice_93", (String) mList.get(position).get("gastprice_93"));
				bundle.putString("gastprice_97", (String) mList.get(position).get("gastprice_97"));
				bundle.putString("gastprice_0", (String) mList.get(position).get("gastprice_0"));
				bundle.putDouble("longitude", (Double) mList.get(position).get("longitude"));
				bundle.putDouble("latitude", (Double) mList.get(position).get("latitude"));
				bundle.putString("brandname", (String) mList.get(position).get("brandname"));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		back_btn = (ImageView) findViewById(R.id.back_station);
		back_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StationActivity.this.finish();
			}
		});
	}
	
	
	class MyTask extends AsyncTask<Void, Void, List<Map<String,Object>>>{

		@Override
		protected List<Map<String, Object>> doInBackground(Void... arg0) {
			StationInfo info = null;
			try {
				
				info = Juhe.getRequest(mLongitude, mLatitude);
				length = info.getResult().getData().length;
				cwjHandler.post(mUpdateResults); 
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(info != null){
				Data data[] = info.getResult().getData();
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				for(int i=0;i<data.length;i++){
					Map<String,Object > map = new HashMap<String,Object>();
					map.put("brandname", data[i].getBrandname());
					map.put("name", data[i].getName());
					map.put("distance", data[i].getDistance());
					map.put("address", data[i].getAddress());
					map.put("gastprice_90", data[i].getGastprice().getP90());
					map.put("gastprice_93", data[i].getGastprice().getP93());
					map.put("gastprice_97", data[i].getGastprice().getP97());
					map.put("gastprice_0", data[i].getGastprice().getP0());
					map.put("longitude", data[i].getLon());
					map.put("latitude", data[i].getLat());
					list.add(map);
				}
				return list;
			}else{
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Map<String, Object>> result) {
			
		}	
		
	}
	
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			popLoader.setVisibility(View.GONE);
		}
	};
}
