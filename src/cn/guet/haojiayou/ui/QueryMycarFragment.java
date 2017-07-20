package cn.guet.haojiayou.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.carUserLogin.CheckLoginState;
import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.CityInfoJson;
import com.cheshouye.api.client.json.InputConfigJson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.guet.haojiayou.MainActivity;
import cn.guet.haojiayou.R;
import cn.guet.haojiayou.R.layout;
import cn.guet.haojiayou.adapter.MyCarWeizhangAdapter;
import cn.guet.haojiayou.bean.CarInfo;
import cn.guet.haojiayou.db.CarInfoDao;
import cn.guet.haojiayou.utils.GetAssets;
import cn.guet.haojiayou.utils.NetService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;

public class QueryMycarFragment extends Fragment {
	ListView lv_mycar;
	TextView tv1;
	CarInfo carInfo;//得到扫描后的汽车信息
	String carJson;//插入汽车信息json
	String carsJson;//爱车列表json
	ArrayList<CarInfo> carlist;
	public static String userno;//用户名
	TextView tv_weigui,tv_fankuan,tv_koufen;
	String weigui ,fankuan ,fenshu ;
	Drawable drawable;
	String logopath ;
	// 保持对Task的引用
	myAnyTask task;
	View itemview;//listitem的view
	//解析的车牌，发动机号和车架号
	ArrayList<ArrayList<String>> weizhangcarslist = new ArrayList<ArrayList<String>>();
	
	CarInfoDao carInfoDao;//操作本地数据库
	MyCarWeizhangAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.query_mycar_fragment, null);
	}
	
	
    @Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
    	lv_mycar = (ListView) view.findViewById(R.id.lv_mycar);
    	
    	if (CheckLoginState.check(getActivity())) {
    		userno = CheckLoginState.username;
    		initeview();
		}else Toast.makeText(getActivity(), "请先登录！", 2000).show(); 
   	  		
	}

    public void initeview(){
  	 // userno = "13912200019";
  	//  carInfoDao = new CarInfoDao(getActivity());
  	 // carlist = carInfoDao.findCarsByUno(userno); 	  
  	  new myAnyTask().execute();		
		
  }
    
    

		//点击列表项跳转
	    class ListItemClickListener implements OnItemClickListener{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(getActivity(),MycarWeizhangResulet.class);
				intent.putStringArrayListExtra("carinfo",weizhangcarslist.get(position) );
				
				itemview = view;
				startActivityForResult(intent, 50);

			}
	    	
	    }
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		Bundle bundle = data.getExtras();
		
		weigui = bundle.getString("条数");
		fankuan = bundle.getString("罚款");
	    fenshu = bundle.getString("分数");
	    
		TextView tv_weigui = (TextView) itemview.findViewById(R.id.tv_weigui);
		TextView tv_fankuan = (TextView) itemview.findViewById(R.id.tv_fankuan);
		TextView tv_koufen = (TextView) itemview.findViewById(R.id.tv_koufen);
	    
		tv_weigui.setText(weigui+"条");
		tv_fankuan.setText(fankuan+"元");
		tv_koufen.setText(fenshu+"分");
    	
 //	adapter.addDatas(result);
	}



	class myAnyTask extends AsyncTask<Void, Void, ArrayList<CarInfo>>{

    	@Override
    	protected ArrayList<CarInfo> doInBackground(Void... arg0) {
    		// TODO Auto-generated method stub
    		 carlist = getCars();//汽车列表
    		
    		if (null!=carlist) {				  			
    			return carlist;
			}

    		return null;
    	}

		@Override
		protected void onPostExecute(ArrayList<CarInfo> result) {
			// TODO Auto-generated method stub
						
			if (null!=result) {
				
				for (int i = 0; i < carlist.size(); i++) {
					
					ArrayList<String> weizhangcarlist = new ArrayList<String>();
					weizhangcarlist.add(result.get(i).plateNo);
					weizhangcarlist.add(result.get(i).engineNo);
					weizhangcarlist.add(result.get(i).vehicleIDNo);
					
					weizhangcarslist.add(weizhangcarlist);
					System.out.println(weizhangcarslist.toString());
						
				 }  
				
				if(null== result || result.isEmpty()){
					Toast.makeText(getActivity(), "暂无爱车信息，请添加！", Toast.LENGTH_LONG).show();
					}else{	
						
						 adapter = new MyCarWeizhangAdapter(getActivity(), result);
					     lv_mycar.setAdapter(adapter);			
					     lv_mycar.setOnItemClickListener(new ListItemClickListener());	
						
					}
				
		    }
			
		}

    	
	    /**
	     * 获取我的爱车列表
	     */
	    public ArrayList<CarInfo> getCars(){
	    	

					String path = "http://115.28.16.183:8080/HaoJiaYouOrder1/GetCarInfo";
					//userno = "13912200019";
					 Map<String, String> params = new HashMap<String,String>();
					 params.put("UserNo", userno);
					 
					try {
						carsJson = NetService.sendGetRequest(path, params, "utf-8");
			            System.out.println("响应的值是："+carsJson  );
						 if(carsJson.toString().equals("404")||carsJson.toString().equals("500")) {
							 carsJson = null;
							}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						carsJson = null;
//						cwjHandler.post(mUpdateResults); // 告诉UI线程可以更新结果了
						Looper.prepare();
						Toast.makeText(getActivity(),"服务器异常，请联系我们！",Toast.LENGTH_LONG).show();
						Looper.loop();
					}
			Gson gson = new Gson();
			
			if (null!=carsJson) {
				
			carlist = gson.fromJson(carsJson , 
	        		new TypeToken<ArrayList<CarInfo>>(){}.getType());  		
			   return  carlist;
			}
			
			return null;
	    }  
}
}