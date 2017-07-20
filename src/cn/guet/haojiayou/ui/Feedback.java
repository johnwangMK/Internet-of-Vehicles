package cn.guet.haojiayou.ui;

import cn.guet.haojiayou.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Feedback extends Activity {
	private TextView send_btn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		send_btn = (TextView) findViewById(R.id.feedback_send);
		send_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(Feedback.this);
				builder.setTitle("提示");
				builder.setMessage("发送成功！");
				builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
					}
				});
				builder.create().show();
			}
		});
	}

}
