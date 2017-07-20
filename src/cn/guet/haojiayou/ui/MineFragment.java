package cn.guet.haojiayou.ui;

import com.carUserLogin.CarUserLogin1;
import com.carUserLogin.CheckLoginState;

import cn.guet.haojiayou.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MineFragment extends Fragment implements OnClickListener{
	private static ImageView mine_image;
	private static TextView mine_loginState;
	private static boolean loginState;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.activity_mine_fragment, null);
	}
     @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {
	// TODO Auto-generated method stub
    	 loginState = CheckLoginState.check(getActivity());
          LinearLayout llset =  (LinearLayout) view.findViewById(R.id.LL_settings);
          LinearLayout llabout =  (LinearLayout) view.findViewById(R.id.LL_about);
          LinearLayout llmycar =  (LinearLayout) view.findViewById(R.id.LL_mycar);
          LinearLayout llweizhang = (LinearLayout) view.findViewById(R.id.LL_weizhang);
          LinearLayout order_forpay = (LinearLayout) view.findViewById(R.id.order_forpay);
          LinearLayout order_foroil = (LinearLayout) view.findViewById(R.id.order_foroil);
          LinearLayout order_done = (LinearLayout) view.findViewById(R.id.order_done);
          LinearLayout LL_order_mananger = (LinearLayout) view.findViewById(R.id.LL_order_mananger);

          mine_image = (ImageView) view.findViewById(R.id.mine_image);
          mine_loginState = (TextView) view.findViewById(R.id.mine_loginState_text);
          if(loginState == true){
        	  mine_image.setImageResource(R.drawable.touxiang);
        	  mine_loginState.setText("已登录");
          }
          LL_order_mananger.setOnClickListener(new LayoutListener());
          llset.setOnClickListener(new LayoutListener());
          llabout.setOnClickListener(new LayoutListener());
          llmycar.setOnClickListener(new LayoutListener());
          llweizhang.setOnClickListener(new LayoutListener());
          order_forpay.setOnClickListener(this);
          order_foroil.setOnClickListener(this);
          order_done.setOnClickListener(this);
          
          mine_image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(loginState){
					Intent intent = new Intent(getActivity(),MyInformation.class);
					startActivity(intent);
				}else{
					int requestCode = 1;
					Bundle bundle = new Bundle();
					bundle.putInt("tag", requestCode);
					Intent intent = new Intent(getActivity(),CarUserLogin1.class);
					intent.putExtras(bundle);
					startActivityForResult(intent, requestCode);
				}
			}
		});
   }
     
   private class LayoutListener implements OnClickListener{

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.LL_order_mananger:
			 if (CheckLoginState.check(getActivity())) {//判断是否登录
				 startActivity(new Intent(getActivity(),OrderListActivity.class));
				 getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
		    	 }else {
						Toast.makeText(getActivity(), "请先登录！", Toast.LENGTH_LONG).show();
						startActivity(new Intent(getActivity(),CarUserLogin1.class));
					}
			break;
			
		case R.id.LL_settings:
			Intent intent = new Intent(getActivity(),Setting.class);
			startActivityForResult(intent, 0);
			break;
			
		case R.id.LL_about:
			startActivity(new Intent(getActivity(),About.class));
			break;
			
		case R.id.LL_mycar:
			
			 if (CheckLoginState.check(getActivity())) {//判断是否登录
				 startActivity(new Intent(getActivity(),MycarActivity.class));
		    	 }else {
						Toast.makeText(getActivity(), "请先登录！", Toast.LENGTH_LONG).show();
						startActivity(new Intent(getActivity(),CarUserLogin1.class));
					}
			break;
			
		case R.id.LL_weizhang:
			startActivity(new Intent(getActivity(),PeccancyqueryActivity.class));
			break;
			
		default:
			break;
		}
	  }
	   
   }

@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	
	Intent intent = new Intent(getActivity(),OrderListActivity.class);
	switch (v.getId()) {
	case R.id.order_forpay:
		 if (CheckLoginState.check(getActivity())) {//判断是否登录
			 intent.putExtra("status", "1");
			 startActivity(intent);
	    	 }else {
					Toast.makeText(getActivity(), "请先登录！", Toast.LENGTH_LONG).show();
					startActivity(new Intent(getActivity(),CarUserLogin1.class));
				}
		break;

	case R.id.order_foroil:

		 if (CheckLoginState.check(getActivity())) {//判断是否登录
			 intent.putExtra("status", "2");
			 startActivity(intent);
	    	 }else {
					Toast.makeText(getActivity(), "请先登录！", Toast.LENGTH_LONG).show();
					startActivity(new Intent(getActivity(),CarUserLogin1.class));
				}
		break;
		
	case R.id.order_done:
		 if (CheckLoginState.check(getActivity())) {//判断是否登录
			 intent.putExtra("status", "0");
			 startActivity(intent);
	    	 }else {
					Toast.makeText(getActivity(), "请先登录！", Toast.LENGTH_LONG).show();
					startActivity(new Intent(getActivity(),CarUserLogin1.class));
				}
		break;
		
	default:
		break;
	}
   }


@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
	
	switch(resultCode){
	case 1://登录状态
		mine_image.setImageResource(R.drawable.touxiang);
  	  	mine_loginState.setText("已登录");
  	  	loginState = true;
  	  	break;
	case 0://未登录状态
		mine_image.setImageResource(R.drawable.btn_mine);
  	  	mine_loginState.setText("点击登录");
  	  	loginState = false;
  	  	break;
  	default:
  		break;
	}
}
/**
 * 设置登录后头像
 */
 public static void setLonginlogo(){
		mine_image.setImageResource(R.drawable.touxiang);
  	  	mine_loginState.setText("已登录");
  	  	loginState = true;
 }
 
 /**
  * 设置未登录默认头像
  */
 public static void setNologo(){
		mine_image.setImageResource(R.drawable.btn_mine);
  	  	mine_loginState.setText("点击登录");
  	  	loginState = false;
 }
 
}
