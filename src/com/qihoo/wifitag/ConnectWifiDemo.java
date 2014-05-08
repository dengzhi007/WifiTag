package com.qihoo.wifitag;

import com.qihoo.wifitag.util.RSA;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectWifiDemo extends Activity {
	
	Button btn_connect = null;
	Button btn_disconnect = null;
	Button btn_encrypt = null;
	TextView text_encrypted_str = null;
	TextView text_decrypted_str = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wangxitest);
        
        btn_connect = (Button)findViewById(R.id.btn_connect);
        btn_disconnect = (Button)findViewById(R.id.btn_disconnect);
        btn_encrypt = (Button)findViewById(R.id.btn_encrypt);
        text_encrypted_str = (TextView)findViewById(R.id.text_encrypted_str);
        text_decrypted_str = (TextView)findViewById(R.id.text_decrypted_str);
        
        btn_connect.setOnClickListener(new BtnConnectOnClickListener());
        btn_disconnect.setOnClickListener(new BtnDisconnectOnClickListener());
        btn_encrypt.setOnClickListener(new BtnEncryptOnClickListener());
    }
      
    private class BtnConnectOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			String ssid = "";
			String password = "questdjklfjasojfeojfejfjdfslfjsdaljfsa15654w3et6541gz5g4t35b465";
			int clearPwdTimeout = 20;
			try {
				Toast.makeText(ConnectWifiDemo.this, "Connecting to " + ssid + " ...", Toast.LENGTH_LONG).show();
				WifiConnect wifiConnect = new WifiConnect(ConnectWifiDemo.this);
				WifiConnect.ConnectStatus connectStatus = wifiConnect.connect(ssid, password, clearPwdTimeout);
				String resultMsg = "Connect to " + ssid + (connectStatus == WifiConnect.ConnectStatus.CONNECT_STATUS_OK ? " successfully!" : " unsuccessfully!");
				Toast.makeText(ConnectWifiDemo.this, resultMsg, Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
    }
    
    private class BtnDisconnectOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			WifiConnect wifiConnect = new WifiConnect(ConnectWifiDemo.this);
			boolean isDisconnected = wifiConnect.disconnect();
			
			String resultMsg = "Disconnect current wifi " + (isDisconnected ? " successfully!" : " unsuccessfully!");
			Toast.makeText(ConnectWifiDemo.this, resultMsg, Toast.LENGTH_LONG).show();
		}
    }
    
    private class BtnEncryptOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			String plaintext = "snser@qq.com";
			RSA rsa = new RSA();
			
			try {
				String encryptedstr = rsa.encrypt(plaintext);
				String decryptedstr = rsa.decrypt(encryptedstr);
				text_encrypted_str.setText("encryptedstr:\n" + encryptedstr);
				text_decrypted_str.setText("decryptedstr:\n" + decryptedstr);
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
    	
    }
}


