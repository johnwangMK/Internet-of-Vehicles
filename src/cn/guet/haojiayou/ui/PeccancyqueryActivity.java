package cn.guet.haojiayou.ui;

import java.util.List;
import cn.guet.haojiayou.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class PeccancyqueryActivity extends FragmentActivity implements OnCheckedChangeListener{
	
	private RadioGroup radioGroup;
	private RadioButton rbTongZhi, rbDongTai;
    
	ImageView img_back_weizhang ;
	List<Fragment> list = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_peccancyquery);
		
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		rbTongZhi = (RadioButton) findViewById(R.id.rbTongZhi);
		rbDongTai = (RadioButton) findViewById(R.id.rbDongTai);
		
		img_back_weizhang = (ImageView) findViewById(R.id.img_back_weizhang);
		img_back_weizhang.setOnClickListener(new ClickListener());
		radioGroup.setOnCheckedChangeListener(this);
		rbTongZhi.setChecked(true);
		setDefaultFragment();
		
		
		
//		//点击右边显示
//		iv_add.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                AddPopWindow addPopWindow = new AddPopWindow(getActivity());
//                addPopWindow.showPopupWindow(iv_add);
//            }
//
//        });
	
	}
    private void setDefaultFragment()  
    {  
        FragmentManager fm = getSupportFragmentManager();  
        FragmentTransaction transaction = fm.beginTransaction();  
        transaction.replace(R.id.querypeccancy_fragment, new QueryMycarFragment ());  
        transaction.commit();  
    } 
	
	@Override
	public void onCheckedChanged(RadioGroup arg0, int cheakedId) {
		if (cheakedId == rbTongZhi.getId()) {
			changeFragment(new QueryMycarFragment ());
		} else if (cheakedId == rbDongTai.getId()) {
			changeFragment(new QueryOthersFragment());		}
	}

	private void changeFragment(Fragment targetFragment) {
		//resideMenu.clearIgnoredViewList();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.querypeccancy_fragment, targetFragment, "fragment")
				.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit();
	}
    class ClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.img_back_weizhang:
			     finish();
				break;

//			case R.id.img_back_mycar:
//				finish();
//				break;
				
			default:
				break;
			}
		}
    	
    }
}
