package cn.guet.haojiayou.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.carUserLogin.CheckLoginState;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.guet.haojiayou.R;
import cn.guet.haojiayou.adapter.MessageAdapter;
import cn.guet.haojiayou.adapter.OrderListAdapter;
import cn.guet.haojiayou.bean.CarInfo;
import cn.guet.haojiayou.bean.FillOilOrder;
import cn.guet.haojiayou.bean.PushInfo;
import cn.guet.haojiayou.ui.OrderListActivity.ItemClickListener;
import cn.guet.haojiayou.utils.NetService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class MessageFragment extends Fragment{
	//private SharedPreferences sharedPreferences;
    public ListView lv;
    private MessageAdapter adapter;
    private ArrayList<PushInfo> Messagelist;
    private String messagejson;
    public static String CID = "";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.activity_message_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);
		
		lv = (ListView) view.findViewById(R.id.lv_message);
		
//		boolean loginState = CheckLoginState.check(getActivity());
//		if(loginState == true){
//			//Toast.makeText(getActivity(), "已登录", Toast.LENGTH_SHORT).show();
//			initview();
//		}else{
//			Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
//		}
	}

	public  void initview(){
		new myAnyTask().execute();
	}
	public  void initvoid(){
			
		new myAnyTask2().execute();
	}		
	
    class myAnyTask extends AsyncTask<Void, Void, ArrayList<PushInfo>>{

    	@Override
    	protected ArrayList<PushInfo> doInBackground(Void... arg0) {
    		// TODO Auto-generated method stub
    		Messagelist = getMessageByCID();
    		if (null!= Messagelist) {   			
			     return Messagelist;			
			}	
    		return null;  		
    	}

		@Override
		protected void onPostExecute(ArrayList<PushInfo> result) {
			// TODO Auto-generated method stub			
          
			if(null== result|| result.isEmpty() ){
				Toast.makeText(getActivity(), "您暂无消息哦！", Toast.LENGTH_LONG).show();
			}else{	
				
				adapter = new MessageAdapter(result, getActivity());
				lv.setAdapter(adapter);				
			
			}
		}
    	
    }
    
    class myAnyTask2 extends AsyncTask<Void, Void, ArrayList<PushInfo>>{

    	@Override
    	protected ArrayList<PushInfo> doInBackground(Void... arg0) {
    		// TODO Auto-generated method stub	
    		return null;  		
    	}

		@Override
		protected void onPostExecute(ArrayList<PushInfo> result) {
			// TODO Auto-generated method stub			
				adapter = new MessageAdapter(result, getActivity());
				lv.setAdapter(adapter);				

		}
    	
    }
     ArrayList<PushInfo> getMessageByCID(){
    	 
 		 String path = "http://115.28.16.183:8080/HaoJiaYouOrder1/GetPushMessage";
 		// String path = "http://202.193.74.157:8080/HaoJiaYouOrder1/GetPushMessage";
 			Map<String, String> params = new HashMap<String, String>();
 			params.put("CID", CID);

 			try {
 				messagejson = NetService.sendGetRequest(path, params, "utf-8");
 				System.out.println("响应的值是：" + messagejson);

 				if (messagejson.toString().equals("404")
 						|| messagejson.toString().equals("500")) {
 					messagejson = null;
 				}

 			} catch (Exception e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 				String msg = NetService.ExceptionCode(e);
 				System.out.println("网络异常：" + msg);
 				messagejson = null;
 			//	cwjHandler.post(mUpdateResults); // 告诉UI线程可以更新结果了
 				Looper.prepare();
 				Toast.makeText(getActivity(),"服务器异常，请联系我们！",Toast.LENGTH_LONG).show();
 				Looper.loop();
 			}

 			Gson gson = new Gson();

 			if (null != messagejson) {

 				Messagelist = gson.fromJson(messagejson,
 						new TypeToken<ArrayList<PushInfo>>() {
 						}.getType());
 				return Messagelist;
 			}
    	return null;
    }	
     
     
}
