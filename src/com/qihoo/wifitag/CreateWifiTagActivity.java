package com.qihoo.wifitag;

import com.qihoo.wifitag.util.DBUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



public class CreateWifiTagActivity extends FragmentActivity{
	private ViewPager view_file_list = null;
	private TextView text_ssid = null;
	private EditText edit_tag_name = null;
	private CheckBox check_public = null;
	private CheckBox check_private = null;
	private ImageView btn_confirm = null;
	private ImageView btn_cancel = null;
	private TextView text_tips = null;
	private String ssid = null;
	private String password = null;
	private ImageView btnBackward = null;
	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		

		
		if (resultCode == RESULT_OK){
//			setContentView(R.layout.create_wifi_tag_selected);

			
			this.ssid = data.getStringExtra("ssid");
			this.password = data.getStringExtra("password");
			
			
			Toast.makeText(CreateWifiTagActivity.this, "正在验证连接 " + ssid + " ...", Toast.LENGTH_LONG).show();
			
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					if (msg.what == 0){
						setContentView(R.layout.create_wifi_tag_selected);
				    	text_ssid = (TextView)findViewById(R.id.create_tag_ap_selected_ssid);
				    	edit_tag_name = (EditText)findViewById(R.id.create_wifi_tag_name);
				    	check_public = (CheckBox)findViewById(R.id.create_tag_ap_check_public);
				    	check_private = (CheckBox)findViewById(R.id.create_tag_ap_check_private);
				    	btn_confirm = (ImageView)findViewById(R.id.create_wifi_tag_confirm);
				    	btn_cancel = (ImageView)findViewById(R.id.create_wifi_tag_cancel);
				    	text_tips = (TextView)findViewById(R.id.create_tag_ap_mode_tips);
				    	btnBackward = (ImageView)findViewById(R.id.create_tag_selected_backward);
				    	check_public.setChecked(true);
				    	check_public.setOnClickListener(new OnCheckPublicKClickListener());
				    	check_private.setOnClickListener(new OnCheckPrivateKClickListener());
				    	btn_confirm.setOnClickListener(new OnBtnConfirmClickListener());
				    	btn_cancel.setOnClickListener(new OnBtnCancelClickListener());
				    	text_tips.setOnClickListener(new OnTipsClickListener());
				    	btnBackward.setOnClickListener(new OnBackClickListener());
				    	
						Toast.makeText(CreateWifiTagActivity.this, "连接 " + ssid + " 成功!", Toast.LENGTH_LONG).show();
					}else if (msg.what == -1){
						Toast.makeText(CreateWifiTagActivity.this, "连接 " + ssid + " 失败!", Toast.LENGTH_LONG).show();
						//CreateWifiTagActivity.this.setContentView(R.layout.create_wifi_tag_main);
						//view_file_list.invalidate();
						//view_file_list.setAdapter(new CreateWifiTagContentPager(getSupportFragmentManager(), CreateWifiTagActivity.this));
					}
				}
			};
			
			new Thread(new Runnable()
			{
				@Override
				public void run(){
					try {
						WifiConnect wifiConnect = new WifiConnect(CreateWifiTagActivity.this);
						WifiConnect.ConnectStatus connectStatus = wifiConnect.connect(ssid, password, 0);
						wifiConnect.disconnect();
						
						if (connectStatus.equals(WifiConnect.ConnectStatus.CONNECT_STATUS_OK)){
							handler.sendEmptyMessage(0);
						}else{
							handler.sendEmptyMessage(-1);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
			}).start();

		}else if (resultCode == RESULT_CANCELED){
			Toast.makeText(this, "CANCEL", Toast.LENGTH_LONG).show();
		}
		
	}
       
	private class OnBackClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			CreateWifiTagActivity.this.finish();
		}
	}
	private class OnTipsClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if (text_tips.getText().toString().equals("为智能贴取个名字")){
				text_tips.setText("");
				text_tips.setTextColor(Color.BLACK);
				text_tips.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
		}
	}
    
	private class OnCheckPublicKClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			check_private.setChecked(false);
			text_tips.setText("公共模式不会对写入信息进行加密，没有安装本应用的用户也能获取密码");
		}
	}
	
	private class OnCheckPrivateKClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			check_public.setChecked(false);
			text_tips.setText("私有模式会对写入信息加密，只有写入用户在本应用帮助下，才能快速连接wifi");
		}
	}
	
	private class OnBtnConfirmClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			String tagName = edit_tag_name.getText().toString();
			
			if (DBUtil.isTagExists(tagName)){
				Toast.makeText(CreateWifiTagActivity.this, "\"" + tagName + "\" 已经存在！", Toast.LENGTH_LONG).show();
				edit_tag_name.setText("");
			}else{
				Intent intentWriteTag = new Intent(CreateWifiTagActivity.this, WriteTagActivity.class);
				intentWriteTag.putExtra("ssid", CreateWifiTagActivity.this.ssid);
				intentWriteTag.putExtra("password", CreateWifiTagActivity.this.password);
				intentWriteTag.putExtra("tagname", tagName);
				intentWriteTag.putExtra("isPrivate", check_public.isChecked());
				CreateWifiTagActivity.this.startActivity(intentWriteTag);
			}
		}
	}

	private class OnBtnCancelClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			CreateWifiTagActivity.this.finish();
		}
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.create_wifi_tag_main);
    	

    	btnBackward = (ImageView)findViewById(R.id.create_tag_main_backward);
        
        view_file_list = (ViewPager) findViewById(R.id.ap_list);
        
        view_file_list.setAdapter(new CreateWifiTagContentPager(getSupportFragmentManager(), this));
        view_file_list.setOnPageChangeListener(new OnDockChangeListener());
        
    }

    public class OnDockChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int position) {
		}
    }
    
	public void onNewUserToken(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onUserCookieInvalid(String arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
