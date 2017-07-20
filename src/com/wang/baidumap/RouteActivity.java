package com.wang.baidumap;

import java.util.ArrayList;
import java.util.List;

import cn.guet.haojiayou.R;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine.DrivingStep;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.wang.overlayutil.DrivingRouteOverlay;
import com.wang.overlayutil.OverlayManager;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;

public class RouteActivity extends Activity implements OnGetRoutePlanResultListener,BaiduMap.OnMapClickListener{
	
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private ImageButton startLocation;
	private ImageButton zoomIn;
	private ImageButton zoomOut;
	
	//·���滮���
	private RoutePlanSearch mRSearch;
	private ListView mListView;
	private RouteLine route;
	private OverlayManager routeOverlay;
	private EditText route_start_text;
	private EditText route_end_text;
	private Button reverse_route_btn;
	private Button route_search_btn;
	
	private LocationClient mLocationClient;
	private Boolean isFirstIn = true;
	private LocationMode mCurrentMode;
	//��¼��γ��
	private double mLatitude;
	private double mLongitude;
	//��¼��ǰ���ڳ���
	private String mCurrentCity = null;
	
	private int requestCode;
	//��λSDK������
	public MyLocationListener locListener = new MyLocationListener();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_route);
		
		//��ȡ��ͼ�ؼ�����
		mMapView = (MapView) findViewById(R.id.route_bmapView);
		mMapView.showZoomControls(false);
		startLocation = (ImageButton) findViewById(R.id.route_location);
		mBaiduMap = mMapView.getMap();//��ȡBaiduMap��,BaiduMap��ſ�������Զ���ͼ��
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
		mBaiduMap.setMapStatus(msu);
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		//������λͼ��
		mBaiduMap.setMyLocationEnabled(true);
		//��λ��ʼ��
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(locListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(5000);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		
		mBaiduMap.setOnMapClickListener(this);
		//��ʼ��·������ģ�飬ע��ʱ�����
		mRSearch = RoutePlanSearch.newInstance();
		mRSearch.setOnGetRoutePlanResultListener(this);
		
		//��λ����ǰλ��
		startLocation.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				startLocation.setEnabled(true);
				CenterToLocation(mLatitude, mLongitude);
				initDrivingRouteLine();
				//nearbySearch(mLatitude,mLongitude);
						
			}
		});
		
		//�Զ������Ű�ť
		zoomIn = (ImageButton) findViewById(R.id.route_map_add);
		zoomOut = (ImageButton) findViewById(R.id.route_map_minus);
		zoomIn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				float zoomlevel = mBaiduMap.getMapStatus().zoom;
				if(zoomlevel<=19){
					mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
					zoomOut.setEnabled(true);
				}else{
					Toast.makeText(RouteActivity.this, "已放至最大", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(RouteActivity.this, "已缩至最小", Toast.LENGTH_SHORT).show();
					zoomOut.setEnabled(false);
				}
			}
		});
		
		
		
		//路径规划
		route_start_text = (EditText) findViewById(R.id.route_start_text);
		route_end_text = (EditText) findViewById(R.id.route_end_text);
		reverse_route_btn = (Button) findViewById(R.id.reverse_route_btn);
		route_search_btn = (Button) findViewById(R.id.route_search_btn);
		
		route_start_text.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				requestCode = 1;
				Bundle bundle = new Bundle();
				bundle.putInt("tag", requestCode);
				Intent intent = new Intent(RouteActivity.this, SuggestionSearchActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, requestCode);
				//finish();
			}
		});
		route_end_text.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				requestCode = 2;
				Bundle bundle = new Bundle();
				bundle.putInt("tag", requestCode);
				Intent intent = new Intent(RouteActivity.this, SuggestionSearchActivity.class);
				intent.putExtras(bundle);				
				startActivityForResult(intent, requestCode);
				//finish();
			}
		});
		//路径规划按钮点击事件
		route_search_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String start = route_start_text.getText().toString();
				String end = route_end_text.getText().toString();
				DrivingRouteSearch(start,end);
			}
		});
		//调换起点、终点按钮点击事件
		reverse_route_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String start_text = route_start_text.getText().toString();
				String end_text = route_end_text.getText().toString();
				route_start_text.setText(end_text);
				route_end_text.setText(start_text);
			}
		});
	}
	/**
	 * �û��������վ��ַ�滮����·��
	 */
	public void initDrivingRouteLine(){
		route = null;
		mBaiduMap.clear();
		LatLng st_LatLng = new LatLng(mLatitude, mLongitude);
		PlanNode stNode = PlanNode.withLocation(st_LatLng);
		Bundle bundle = RouteActivity.this.getIntent().getExtras();
		double longitude = bundle.getDouble("longitude");
		double latitude = bundle.getDouble("latitude");
		LatLng en_LatLng = new LatLng(latitude, longitude);
		PlanNode enNode = PlanNode.withLocation(en_LatLng);
		mRSearch.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));
	}
	/**
	 * 更具用户输入的起点、终点，规划路线
	 * @param start
	 * @param end
	 */
	public void DrivingRouteSearch(String start,String end){
		route = null;
		mBaiduMap.clear();
		PlanNode stNode = PlanNode.withCityNameAndPlaceName("桂林", start);
		PlanNode enNode = PlanNode.withCityNameAndPlaceName("桂林", end);
		mRSearch.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));
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
			mCurrentCity = location.getCity();
			//���ö�λͼ��
			BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
			MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
			mBaiduMap.setMyLocationConfigeration(config);
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
	 * ���ҵ�λ���ƶ�����ͼ����
	 * @param latitude
	 * @param longitude
	 */
	private void CenterToLocation(double latitude,double longitude){
		//����
		//mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMaker));
		//mBaiduMap.clear();
		LatLng myLocation = new LatLng(latitude, longitude);
		MapStatus ms = new MapStatus.Builder().target(myLocation).zoom(14).build();
		MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(ms);
		mBaiduMap.setMapStatus(msu);
	}
	
	@Override
	public void onGetBikingRouteResult(BikingRouteResult arg0) {
		
	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		//Toast.makeText(RouteActivity.this, result.error.name(), Toast.LENGTH_SHORT).show();
		if(result == null || result.error != SearchResult.ERRORNO.NO_ERROR){
			Toast.makeText(RouteActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if(result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR){
			//����;�����ַ�����壬ͨ�����½�ڻ�ȡ�����ѯ��Ϣ
			//result.getSuggestionInfo();
			Toast.makeText(RouteActivity.this, "输入终点有歧义，请重新输入", Toast.LENGTH_SHORT).show();
			return;
		}
		if(result.error == SearchResult.ERRORNO.NO_ERROR){
			route = result.getRouteLines().get(0);
			DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
			routeOverlay = overlay; 
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
			List<String> datas = new ArrayList<String>();
			List<DrivingRouteLine> routeLines = result.getRouteLines();
			if(routeLines != null){
				for(int i = 0; i < route.getAllStep().size(); i++){
					datas.add(((DrivingStep)route.getAllStep().get(i)).getInstructions()+"\n");
				}
				//new AlertDialog.Builder(RouteActivity.this).setTitle("驾车路线").setMessage(datas.toString()).show();
			}
		}
	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		
	}
	
	private class MyDrivingRouteOverlay extends DrivingRouteOverlay{

		public MyDrivingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
		}
	}
	
	@Override
	protected void onDestroy() {
		
		mLocationClient.stop();
		mBaiduMap.setMyLocationEnabled(false);
		mRSearch.destroy();
		mMapView.onDestroy();
		mMapView = null;
		//��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		
		super.onPause();
		//��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		//��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ��� 
		mMapView.onResume();
		
	}
	@Override
	public void onMapClick(LatLng latLng) {
		
	}
	@Override
	public boolean onMapPoiClick(MapPoi mapPoi) {
		return false;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode != 0){
			String key = data.getStringExtra("key");
			switch(requestCode){
			case 1:
				route_start_text.setText(key);
				break;
			case 2:
				route_end_text.setText(key);
				break;
			default:
				break;
			}
		}
	}
	
}
