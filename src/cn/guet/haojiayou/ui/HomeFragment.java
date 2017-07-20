package cn.guet.haojiayou.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import com.carUserLogin.CarUserLogin1;
import com.carUserLogin.CheckLoginState;
import com.carmusic.activity.ListMainActivity;
import com.wang.baidumap.StationActivity;

import cn.guet.haojiayou.R;
import cn.guet.haojiayou.adapter.ViewPagerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import cn.guet.haojiayou.R;
import cn.guet.haojiayou.adapter.ViewPagerAdapter;


public class HomeFragment extends Fragment implements Runnable{
	
    private ViewPager mViewPager;
    private ListView lv;
    private ViewGroup group;
    private ViewPagerAdapter mAdapter;
    private List<ImageView> mViewPicture;
    private ImageView[] mImageViews = null;
    private ImageView imageView = null;
    private AtomicInteger what = new AtomicInteger(0);
    private boolean isContinue = true;
	private int index = 0;
	NearByFragment nearByFragment;
	private double longitude = 110.344187;
	private double latitude = 25.2894;
    
    private final Handler viewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mViewPager.setCurrentItem(msg.what);
            super.handleMessage(msg);
        }
    };
	
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_home_fragment, container,false);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initeCyclePager(view);
		initeGridView(view);
        //initeListView(view);
	}
	
	/**
	 * 初始化轮播界面
	 * @param view  Fragment视图
	 */
	 private void initeCyclePager(View view){
		 
	        mViewPager = (ViewPager)view.findViewById(R.id.viewpager);
	        group = (ViewGroup)view.findViewById(R.id.viewGroup);
	        
	        mViewPicture = new ArrayList<ImageView>();
	        ImageView img1 = new ImageView(getActivity());
	        img1.setBackgroundResource(R.drawable.lunbo01);
	        mViewPicture.add(img1);

	        ImageView img2 = new ImageView(getActivity());
	        img2.setBackgroundResource(R.drawable.lunbo02);
	        mViewPicture.add(img2);

	        ImageView img3 = new ImageView(getActivity());
	        img3.setBackgroundResource(R.drawable.lunbo03);
	        mViewPicture.add(img3);

	        ImageView img4 = new ImageView(getActivity());
	        img4.setBackgroundResource(R.drawable.lunbo04);
	        mViewPicture.add(img4);

	        mImageViews = new ImageView[mViewPicture.size()];

	        for (int i = 0; i < mViewPicture.size(); i++) {
	            imageView = new ImageView(getActivity());
	            imageView.setLayoutParams(new LayoutParams(20, 20));
	            imageView.setPadding(5, 5, 10, 10);
	            mImageViews[i] = imageView;
	            if (i == 0) {
	                mImageViews[i].setBackgroundResource(R.drawable.icon_point_pre);
	            } else {
	                mImageViews[i].setBackgroundResource(R.drawable.icon_point);
	            }
	            group.addView(mImageViews[i]);
	        }

	        mViewPager.setOnPageChangeListener(new GuidePageChangeListener());
	        mAdapter = new ViewPagerAdapter(mViewPicture);
	        mViewPager.setAdapter(mAdapter);
	        mViewPager.setOnTouchListener(new GuideOnTouchListener());
	        
	        new Thread(this).start();
	 }
	 
	 private void initeGridView(View view){
		     
		 GridView gridview =  (GridView) view.findViewById(R.id.gridView_Home);
		//创建一个裝map類型的数组列表
			ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
			final int[] image_id = new int[] { R.drawable.gas,
					R.drawable.car, R.drawable.search_weizhang, R.drawable.music,
					};
			String[] str = new String[] { "点击加油", "我的爱车", "违章查询", "我的音乐" };
			//把文字和图片分别装进map里面，然后在添加到列表里面
			for (int i = 0; i < image_id.length; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("文字", str[i]);
				map.put("图片", image_id[i]);
				list.add(map);	
	        }
			SimpleAdapter sa = new SimpleAdapter(getActivity(), list,
					R.layout.griditem, new String[] { "图片", "文字" }, new int[] {
							R.id.itemImage, R.id.itemName });
			gridview.setAdapter(sa);
			gridview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					switch (image_id[arg2]) {
					
					case R.drawable.gas:
						 latitude = (double)nearByFragment.mLatitude;
						 longitude = (double)nearByFragment.mLongitude;
						 Intent intent = new Intent(getActivity(), StationActivity.class);
						 Bundle bundle = new Bundle();
						 bundle.putDouble("lon", longitude);
						 bundle.putDouble("lat", latitude);
						 intent.putExtras(bundle);
						 startActivity(intent);
						break;
						
					case R.drawable.car:
						if (CheckLoginState.check(getActivity())) {//判断是否登录
							//MycarActivity.userno = CheckLoginState.username;
//							if(MainActivity.mineFragment != null) {
//								MainActivity.mineFragment .setLonginlogo();
//							}
							startActivity(new Intent(getActivity(),MycarActivity.class));
				    	 }else {
								Toast.makeText(getActivity(), "请先登录！", Toast.LENGTH_LONG).show();
								//MineFragment.setNologo();
								startActivity(new Intent(getActivity(),CarUserLogin1.class));
							}	
						//startActivity(new Intent(getActivity(),MycarActivity.class));
						break;
						
					case R.drawable.search_weizhang:
						 startActivity(new Intent(getActivity(),PeccancyqueryActivity.class));
						break;
						
					case R.drawable.music:
						startActivity(new Intent(getActivity(), ListMainActivity.class));
						break;
						
					default:
						break;
						
					}
				}
			});
			
	 } 
	 
    private final class GuidePageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            what.getAndSet(arg0);
            for (int i = 0; i < mImageViews.length; i++) {
                mImageViews[arg0].setBackgroundResource(R.drawable.icon_point_pre);
                if (arg0 != i) {
                    mImageViews[i].setBackgroundResource(R.drawable.icon_point);
                }
            }
        }
    }

    private final class GuideOnTouchListener implements OnTouchListener{

        /* (non-Javadoc)
         * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
         */
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    isContinue = false;
                    break;
                case MotionEvent.ACTION_UP:
                    isContinue = true;
                    break;
                default:
                    isContinue = true;
                    break;
            }
            return false;
        }

    }

    private void whatOption() {
        what.incrementAndGet();
        if (what.get() > mImageViews.length - 1) {
            what.getAndAdd(-4);
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {

        }
    }
    @Override
    public void run() {
        while (true) {
            if (isContinue) {
                viewHandler.sendEmptyMessage(what.get());
                whatOption();
            }
        }
    }
}
