package cn.guet.haojiayou.ui;

import java.util.HashMap;
import java.util.Map;

import cn.guet.haojiayou.R;
import cn.guet.haojiayou.R.layout;
import cn.guet.haojiayou.bean.FillOilOrder;
import cn.guet.haojiayou.utils.NetService;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;

public class EnsurePay extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ensure_pay);
		Bundle bundle  = this.getIntent().getExtras();
		final FillOilOrder odOrder = (FillOilOrder) bundle.getSerializable("orderlist");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("共需支付金额￥"+odOrder.totalMoney+".\n确定支付吗？")
				.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						 updateOrderStatus(odOrder);
						Intent intent = new Intent(EnsurePay.this,OrderListActivity.class);
						intent.putExtra("status", "1");
						startActivity(intent);
						finish();
					}
				}).setNegativeButton("取消",new android.content.DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						 finish();
					}
				} ).create().show();
	}
    /**
     * 更改订单状态
     * @param position
     */
    public void  updateOrderStatus(FillOilOrder odOrder){  
    	final FillOilOrder  order =  odOrder;
    	System.out.println("order"+order);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String path = "http://115.28.16.183:8080/HaoJiaYouOrder1/UpdateOilOrder";
				//String path = "http://202.193.75.94:8080/HaoJiaYouOrder1/UpdateOilOrder";
				Map<String, String> params = new HashMap<String, String>();
				params.put("OrderID", order.orderID);
				params.put("OderStatus", order.orderStatus);
				params.put("Flag","2");
				try {
					String msg = NetService.sendGetRequest(path,params, "utf-8");

					if (msg.toString().contains("success")) {
						System.out.println("服务器更改订单状态成功！");
					}

				} catch (Exception e) {
					// TODO Auto-generated catch
					// block
					e.printStackTrace();
				}
			}
		}).start();
		
    }
}
