package cn.guet.haojiayou.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.igexin.sdk.PushManager;
import com.wang.baidumap.MyPopupWindow;
import com.wang.baidumap.StationActivity;
import com.wang.baidumap.SuggestionSearchActivity;
import com.wang.entity.SuggestionInfo;
import com.wang.juhe.Data;
import com.wang.juhe.Juhe;
import com.wang.juhe.StationInfo;
import com.wang.overlayutil.OverlayManager;

import cn.guet.haojiayou.MainActivity;
import cn.guet.haojiayou.PushDemoReceiver;
import cn.guet.haojiayou.R;
import cn.guet.haojiayou.adapter.AutoAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class NearByFragment extends Fragment implements OnGetSuggestionResultListener, OnGetGeoCoderResultListener{

    boolean hasNetwoke = false;
	
	
	//建议查询相关
	private SuggestionSearch mSuggestionSearch = null;
	private EditText keyWordsView = null;
	private AutoAdapter sugAdapter = null;
	private List<SuggestionInfo> mList = new ArrayList<SuggestionInfo>();
	
	private MapView mMapView = null;
	private BaiduMap mBaiduMap;
	private ImageButton startLocation;
	private ImageButton traffic;
	private ImageButton zoomIn;
	private ImageButton zoomOut;
	
	//地理编码相关
	GeoCoder mGeoSearch = null;
	private ImageButton showPoiList;
	private List<LatLng> allPoiLatLng;
	//�����ܱ߼���վ��� 
	private PoiSearch mPoiSearch; 
	//��λ���
	private LocationClient mLocationClient;
	//�Ƿ��һ�ζ�λ
	private Boolean isFirstIn = true;
	//��λͼ����ʾ��ʽ
	private LocationMode mCurrentMode;
	//��λͼ������
	private BitmapDescriptor mCurrentMaker = null;
	//��¼��γ��
	public static double mLatitude = 25.2894;
	public static double mLongitude = 110.344187;
	//��ͼ���ľ�γ��
	private double mapcenter_latitude = 25.2894;
	private double mapcenter_longitude = 110.344187;
	//�Զ���ͼ��
	private BitmapDescriptor mIconLocation;
	//ͨ��ۺ���ݵõ��ļ���վ���ʵ����Ϣ
	private StationInfo stationInfo = null;
	String StationInfo_json = null;
	//��λSDK������
	public MyLocationListener locListener = new MyLocationListener();
	private int requestCode;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		SDKInitializer.initialize(getActivity().getApplicationContext());	
		
		View view = inflater.inflate(R.layout.activity_near_by_fragment, container,false);		
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		mMapView.showZoomControls(false);
		startLocation = (ImageButton) view.findViewById(R.id.location);
		traffic = (ImageButton) view.findViewById(R.id.traffic);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
		mBaiduMap.setMapStatus(msu);
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		keyWordsView = (EditText) view.findViewById(R.id.autotext);
		showPoiList = (ImageButton) view.findViewById(R.id.poi_list_btn);
		zoomIn = (ImageButton) view.findViewById(R.id.map_add);
		zoomOut = (ImageButton) view.findViewById(R.id.map_minus);
		return view;
	}
	@Override
  	public void onViewCreated(View view, Bundle savedInstanceState) {
			
		//���õ�ͼ״̬�ı�ʱ�����
		mBaiduMap.setOnMapStatusChangeListener(mapStatusChangeListener);
		//������λͼ��
		mBaiduMap.setMyLocationEnabled(true);
		//��λ��ʼ��
		mLocationClient = new LocationClient(getActivity());
		mLocationClient.registerLocationListener(locListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(5000);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		//����POI����ʵ��
		mPoiSearch = PoiSearch.newInstance();
		//����POI����������
		mPoiSearch.setOnGetPoiSearchResultListener(new poiSearchResultListener());
		//��������
		//mSuggestionSearch = SuggestionSearch.newInstance();
		//mSuggestionSearch.setOnGetSuggestionResultListener(this);
		//keyWordsView = (EditText) view.findViewById(R.id.autotext);
		keyWordsView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				requestCode = 1;
				Bundle bundle = new Bundle();
				bundle.putInt("tag", requestCode);
				Intent intent = new Intent(getActivity(), SuggestionSearchActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, requestCode);
				
			}
		});
		
		//��ʼ���������������ģ�飬ע������¼�
		//mGeoSearch = GeoCoder.newInstance();
		//mGeoSearch.setOnGetGeoCodeResultListener(this);
		//showPoiList = (ImageButton) view.findViewById(R.id.poi_list_btn);
		showPoiList.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), StationActivity.class);
				Bundle bundle = new Bundle();
				bundle.putDouble("lon", mLongitude);
				bundle.putDouble("lat", mLatitude);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		
		//��λ����ǰλ��
		startLocation.setOnClickListener(new View.OnClickListener() {
				
			@Override
			public void onClick(View v) {
				startLocation.setEnabled(true);
				CenterToLocation(mLatitude, mLongitude);
				//nearbySearch(mLatitude,mLongitude);
				
			}
		});
		//boolean flag=true;
		//������ͨ���
		traffic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Ato-generated method stub
				if(!mBaiduMap.isTrafficEnabled()){
					traffic.setBackgroundResource(R.drawable.traffic_true);
					mBaiduMap.setTrafficEnabled(true);
				}else{
					traffic.setBackgroundResource(R.drawable.traffic_false);
					mBaiduMap.setTrafficEnabled(false);
				}
			}
			
		});
		
		//�Զ������Ű�ť
		zoomIn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				float zoomlevel = mBaiduMap.getMapStatus().zoom;
				if(zoomlevel<=19){
					mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
					zoomOut.setEnabled(true);
				}else{
					Toast.makeText(getActivity(), "已放至最大", Toast.LENGTH_SHORT).show();
					zoomIn.setEnabled(false);
				}
				
			}
		});
		zoomOut.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				float zoomlevel = mBaiduMap.getMapStatus().zoom;
				if(zoomlevel>4){
					mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
					zoomIn.setEnabled(true);
				}else{
					Toast.makeText(getActivity(), "已缩至最小", Toast.LENGTH_SHORT).show();
					zoomOut.setEnabled(false);
				}
			}
		});
		
		//�ۺ���ݷ���json��ݽ����õ�StationInfo
		try {
			stationInfo = new getStationInfoTask().execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if(stationInfo == null){
			Toast.makeText(getActivity(), "请检查你的网络", Toast.LENGTH_SHORT).show();
			return;
		}else{
			//Toast.makeText(getActivity(), stationInfo.getResultcode(), Toast.LENGTH_LONG).show();
			addJuheStation();
		}
		
}
  
	
	/**
	 * ��λSDK������
	 * @author Angel
	 *
	 */
	public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			
			if(location == null||mBaiduMap == null){
				return;
			}
			MyLocationData data = new MyLocationData.Builder()
				.accuracy(location.getRadius())
				.direction(100)
				.latitude(location.getLatitude())
				.longitude(location.getLongitude())
				.build();
			//���ö�λ���
			mBaiduMap.setMyLocationData(data);
			mCurrentMode = LocationMode.NORMAL;
			//��ȡ��γ��
			mLatitude = location.getLatitude();
			mLongitude = location.getLongitude();
			//��һ�ζ�λ��ʱ�򣬵�ͼ������ʾΪ��λ����λ��
			if(isFirstIn){
				isFirstIn = false;
				//����������ݽṹ
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(msu);
			}
			
		}
		
	}
	/**
	 * POI����������
	 * @author Angel
	 *
	 */
	public class poiSearchResultListener implements OnGetPoiSearchResultListener{
		
		@Override
		public void onGetPoiResult(PoiResult poiResult) {
			//Toast.makeText(MainActivity.this, poiResult.error.toString(), Toast.LENGTH_LONG).show();
			if(poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND){
				Toast.makeText(getActivity(), "未找到结果", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(poiResult.error == SearchResult.ERRORNO.NO_ERROR){
				mBaiduMap.clear();
				/*for(int i=0;i<poiResult.getTotalPoiNum();i++){
					LatLng ll = poiResult.getAllAddr().get(i).location;
					allPoiLatLng.add(ll);
				}*/
				PoiOverlay poiOverlay = new PoiOverlay(mBaiduMap);
				poiOverlay.setData(poiResult);//����POI���
				mBaiduMap.setOnMarkerClickListener(poiOverlay);//Ϊ��������ӵ��ʱ��
				poiOverlay.addToMap();//�����е�overlay��ӵ���ͼ��
				//poiOverlay.zoomToSpan();
				return;
			}
		}
		@Override
		public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
			
			//String name = poiDetailResult.getName();
			//String detail = "��ַ��" + poiDetailResult.getAddress();
			MyPopupWindow popupWindow = new MyPopupWindow(getActivity(), poiDetailResult);
			View rootview = LayoutInflater.from(getActivity()).inflate(R.layout.popupwindow, null);
			popupWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
			
		}
	}
	
	public OnMapStatusChangeListener mapStatusChangeListener = new OnMapStatusChangeListener() {
		
		@Override
		public void onMapStatusChangeStart(MapStatus status) {
			
		}
		
		@Override
		public void onMapStatusChangeFinish(MapStatus status) {
			LatLng ll = status.target;
			mapcenter_latitude = ll.latitude;
			mapcenter_longitude = ll.longitude;
			try {
				stationInfo = new getStationInfoTask().execute().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			if(stationInfo == null){
				Toast.makeText(getActivity(), "请检查你的网络", Toast.LENGTH_SHORT).show();
				return;
			}else{
				//Toast.makeText(getActivity(), stationInfo.getResultcode(), Toast.LENGTH_LONG).show();
				addJuheStation();
			}
			//nearbySearch(mapcenter_latitude,mapcenter_longitude);
		}
		
		@Override
		public void onMapStatusChange(MapStatus status) {
			
		}
	};
	
	
	/**
	 * 将我的位置显示到屏幕中心
	 * @param latitude
	 * @param longitude
	 */
	private void CenterToLocation(double latitude,double longitude){
		//����
		//mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMaker));
		//mBaiduMap.clear();
		LatLng myLocation = new LatLng(latitude, longitude);
		MapStatus ms = new MapStatus.Builder().target(myLocation).zoom(16.0f).build();
		MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(ms);
		mBaiduMap.setMapStatus(msu);
	}
	/**
	 * 点击通过聚合数据接口获得的加油站弹出底部弹窗
	 * @param data
	 * @param i
	 */
	public void showPopupWindow(Data[] data, int i){
		MyPopupWindow popupWindow = new MyPopupWindow(getActivity(),data, i);
		View rootview = LayoutInflater.from(getActivity()).inflate(R.layout.popupwindow, null);
		popupWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
	}
	/**
	 * 百度地图提供的附近加油站检索结果
	 * @param latitude
	 * @param longitude
	 */
	private void nearbySearch(double latitude,double longitude){
		PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
		nearbySearchOption.location(new LatLng(latitude, longitude));//��������
		nearbySearchOption.keyword("加油站");//�����ؼ���
		nearbySearchOption.radius(8000);//�����뾶����λ����
		nearbySearchOption.pageCapacity(20);//��ҳÿҳ������
		nearbySearchOption.pageNum(0);//��ʾ��һҳ
		mPoiSearch.searchNearby(nearbySearchOption);
		
	}
	
	/**
	 * 显示聚合数据接口得到的加油站到地图上
	 */
	public void addJuheStation(){
		if(stationInfo.getResult() == null){
			Toast.makeText(getActivity(), "未找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		
		mBaiduMap.clear();
		JuheOverlay juheOverlay = new JuheOverlay(mBaiduMap);
		juheOverlay.setData(stationInfo);
		mBaiduMap.setOnMarkerClickListener(juheOverlay);
		juheOverlay.addToMap();
		//juheOverlay.zoomToSpan();
		
	}
	
	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if(res == null||res.getAllSuggestions() == null){
			return;
		}
		sugAdapter.clear();
		SuggestionInfo si =new SuggestionInfo();
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			if (info.key != null){
				si.setKey(info.key);
				si.setDistrict(info.district);
				mList.add(si);
		}
		sugAdapter.notifyDataSetChanged(); 
		}
	}
	
	@Override
	public void onDestroy() {
		
		mPoiSearch.destroy();
		mSuggestionSearch.destroy();
		mLocationClient.stop();
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	@Override
	public void onPause() {
		
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onResume() {
		
		super.onResume();
		mMapView.onResume();
		
	}
	
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(getActivity(), "未找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
	}
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(getActivity(), "未找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		
	}
	/**
	 * 请求网络获得聚合数据接口提供的加油站数据
	 * @author Angel
	 *
	 */
	public class getStationInfoTask extends AsyncTask<Void, Void, StationInfo>{

		@Override
		protected StationInfo doInBackground(Void... arg0) {
			StationInfo info = null;
			try {
				info = Juhe.getRequest((double)mapcenter_longitude, (double)mapcenter_latitude);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return info;
		}

		@Override
		protected void onPostExecute(StationInfo result) {
			super.onPostExecute(result);
		}
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(resultCode != 0){
			double longitude = data.getDoubleExtra("longitude", 110.344187);
			double latitude = data.getDoubleExtra("latitude", 25.2894);
			String key = data.getStringExtra("key");
			keyWordsView.setText(key);
			mapcenter_latitude = latitude;
			mapcenter_longitude = longitude;
			CenterToLocation(latitude, longitude);
			try {
				stationInfo = new getStationInfoTask().execute().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			addJuheStation();
		}
	}


	/**
	 * ������ʾpoi��overlay
	 */
	public class PoiOverlay extends OverlayManager {

	    private static final int MAX_POI_SIZE = 20;

	    private PoiResult mPoiResult = null;
	    /**
	     * ���캯��
	     */
	    public PoiOverlay(BaiduMap baiduMap) {
	        super(baiduMap);
	    }

	    /**
	     * ����poi���
	     */
	    public void setData(PoiResult poiResult) {
	        this.mPoiResult = poiResult;
	    }

	    @Override
	    public List<OverlayOptions> getOverlayOptions() {
	        if (mPoiResult == null || mPoiResult.getAllPoi() == null) {
	            return null;
	        }
	        List<OverlayOptions> markerList = new ArrayList<OverlayOptions>();
	        int markerSize = 0;
	        for (int i = 0; i < mPoiResult.getAllPoi().size()
	                && markerSize < MAX_POI_SIZE; i++) {
	            if (mPoiResult.getAllPoi().get(i).location == null) {
	                continue;
	            }
	            markerSize++;
	            Bundle bundle = new Bundle();
	            bundle.putInt("index", i);
	            if(mPoiResult.getAllPoi().get(i).name.contains("中石化")){
	            	markerList.add(new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromResource(R.drawable.v_map_gas_zhongshihua)).extraInfo(bundle)
                  .position(mPoiResult.getAllPoi().get(i).location));
	            }else if(mPoiResult.getAllPoi().get(i).name.contains("中石油")){
	            	markerList.add(new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromResource(R.drawable.v_map_gas_zhongshiyou)).extraInfo(bundle)
                  .position(mPoiResult.getAllPoi().get(i).location));
	            }else if(mPoiResult.getAllPoi().get(i).name.contains("道达尔")){
	            	markerList.add(new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromResource(R.drawable.v_map_gas_daodaer)).extraInfo(bundle)
                  .position(mPoiResult.getAllPoi().get(i).location));
	            }else if(mPoiResult.getAllPoi().get(i).name.contains("乐福")){
	            	markerList.add(new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromResource(R.drawable.v_map_gas_yuefu)).extraInfo(bundle)
                  .position(mPoiResult.getAllPoi().get(i).location));
	            }else if(mPoiResult.getAllPoi().get(i).name.contains("中化")){
	            	markerList.add(new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromResource(R.drawable.v_map_gas_zhonghua)).extraInfo(bundle)
                  .position(mPoiResult.getAllPoi().get(i).location));
	            }else{
	            	markerList.add(new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromResource(R.drawable.v_map_gas_bail)).extraInfo(bundle)
                  .position(mPoiResult.getAllPoi().get(i).location));
	            }
	            
	            
	        }
	        return markerList;
	    }

	    /**
	     * ��ȡPoiOverlay��poi���
	     */
	    public PoiResult getPoiResult() {
	        return mPoiResult;
	    }

	    /**
	     * ��д�˷����Ըı�Ĭ�ϵ����Ϊ
	     */
	    
	    public boolean onPoiClick(int i) {
	        if (mPoiResult.getAllPoi() != null
	                && mPoiResult.getAllPoi().get(i) != null) {
	            //Toast.makeText(BMapManager.getContext(),"��ַ��" + mPoiResult.getAllPoi().get(i).address + "���֣�" + mPoiResult.getAllPoi().get(i).name, Toast.LENGTH_SHORT).show();
	            getPoiInfo(mPoiResult.getAllPoi().get(i));
	        }
	        return false;
	    }

	    @Override
	    public boolean onMarkerClick(Marker marker) {
	        if (!mOverlayList.contains(marker)) {
	            return false;
	        }
	        if (marker.getExtraInfo() != null) {
	        	LatLng ll = marker.getPosition();
	        	CenterToLocation(ll.latitude, ll.longitude);
	            return onPoiClick(marker.getExtraInfo().getInt("index")); 
	        }
	        return false;
	    }
	    
	    @Override
	    public boolean onPolylineClick(Polyline polyline) {
	        return false;
	    }
	    
	    private void getPoiInfo(PoiInfo info){
	    	
	    	mPoiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(info.uid));
	 
	    }
	}

	public class JuheOverlay extends OverlayManager{
		
		private static final int MAX_POI_SIZE = 20;
		private StationInfo mStationInfo;

		public JuheOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}
		
		public void setData(StationInfo stationInfo) {
	        this.mStationInfo = stationInfo;
	    }

		@Override
		public boolean onMarkerClick(Marker marker) {
			if (!mOverlayList.contains(marker)) {
	            return false;
	        }
	        if (marker.getExtraInfo() != null) {
	        	LatLng ll = marker.getPosition();
	        	CenterToLocation(ll.latitude, ll.longitude);
	            return onPoiClick(marker.getExtraInfo().getInt("index"));
	        }
			return false;
		}
		
		

		private boolean onPoiClick(int i) {
			if (mStationInfo.getResult() != null&&mStationInfo.getResult().getData().length !=0) {
	            Data[] data = mStationInfo.getResult().getData();
	            showPopupWindow(data, i);
	        }
			return false;
		}

		@Override
		public boolean onPolylineClick(Polyline polyline) {
			return false;
		}

		@Override
		public List<OverlayOptions> getOverlayOptions() {
			
			if(mStationInfo == null || mStationInfo.getResult() == null){
				return null;
			}
			List<OverlayOptions> markerList = new ArrayList<OverlayOptions>();
			int markerSize = 0;
			for(int i = 0;i < mStationInfo.getResult().getData().length && markerSize < MAX_POI_SIZE; i++){
				markerSize++;
				Bundle bundle = new Bundle();
				bundle.putInt("index", i);
				Data[] data = mStationInfo.getResult().getData();
				double lat = data[i].getLat();
				double lon = data[i].getLon();
			 	LatLng location = new LatLng(lat, lon);
				if(data[i].getBrandname().contains("中石化")){
					markerList.add(new MarkerOptions()
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.v_map_gas_zhongshihua)).extraInfo(bundle)
					.position(location));
				}else if(data[i].getBrandname().contains("中石油")){
					markerList.add(new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromResource(R.drawable.v_map_gas_zhongshiyou)).extraInfo(bundle)
                  .position(location));
				}else if(data[i].getBrandname().contains("道达尔")){
					markerList.add(new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromResource(R.drawable.v_map_gas_daodaer)).extraInfo(bundle)
                  .position(location));
				}else if(data[i].getBrandname().contains("乐福")){
					markerList.add(new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromResource(R.drawable.v_map_gas_yuefu)).extraInfo(bundle)
                  .position(location));
				}else if(data[i].getBrandname().contains("中化")){
					markerList.add(new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromResource(R.drawable.v_map_gas_zhonghua)).extraInfo(bundle)
                  .position(location));
				}else{
					markerList.add(new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromResource(R.drawable.v_map_gas_bail)).extraInfo(bundle)
                  .position(location));
				}
			}
			return markerList;
		}
		
	}


}
