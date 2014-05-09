package com.qihoo.wifitag;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.qihoo.wifitag.R;

public class CreateWifiTagInputPwdActivity extends Activity{

	private static String ssid = null;
	private EditText text_input_pwd = null;
	private ImageView btn_ok = null;
	private ImageView btn_cancel = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);   
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);  

		
		setContentView(R.layout.create_wifi_tag_input_pwd);
		
		
		text_input_pwd = (EditText)findViewById(R.id.create_wifi_tag_pwd);
		btn_ok = (ImageView)findViewById(R.id.create_wifi_tag_pwd_ok);
		btn_cancel = (ImageView)findViewById(R.id.create_wifi_tag_pwd_cancel);
		
		text_input_pwd.setOnClickListener(new OnTextInputPwdClickListener());
		btn_ok.setOnClickListener(new OnBtnOKClickListener());
		btn_cancel.setOnClickListener(new OnBtnCancelClickListener());
		
		TextView textSsid = (TextView)findViewById(R.id.create_wifi_tag_ap_ssid);
		this.ssid = getIntent().getStringExtra("ssid");
		textSsid.setText(this.ssid);
	}
	
	private class OnTextInputPwdClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
//			if (text_input_pwd.getText().toString().equals("请输入访问密码")){
//				text_input_pwd.setText("");
//				text_input_pwd.setTextColor(Color.BLACK);
//				text_input_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//			}
		}
	}
	
	private class OnBtnOKClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intentResult = new Intent(CreateWifiTagInputPwdActivity.this, CreateWifiTagActivity.class);
			intentResult.putExtra("ssid", CreateWifiTagInputPwdActivity.ssid);
			intentResult.putExtra("password", text_input_pwd.getText().toString());
			CreateWifiTagInputPwdActivity.this.setResult(RESULT_OK, intentResult);
			finish();
		}
	}
	
	private class OnBtnCancelClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intentResult = new Intent(CreateWifiTagInputPwdActivity.this, CreateWifiTagActivity.class);
			CreateWifiTagInputPwdActivity.this.setResult(RESULT_CANCELED, intentResult);
			finish();
		}
		
	}

}
