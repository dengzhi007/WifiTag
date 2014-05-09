package com.qihoo.wifitag;


import java.util.ArrayList;
import java.util.List;

import com.qihoo.wifitag.util.DBUtil;
import com.qihoo.wifitag.util.RSA;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WifiTagActivity extends SlideMenuActivity {
    /** Called when the activity is first created. */
	
	private String[] readTagStr=null;
    
	//write message to tag :  writeTagWaitingList.add(writeStr);
	private List<String[]> writeTagWaitingList = new ArrayList<String[]>();
	
	public static final int WRITE_SUCESS = 0x1233;
	public static final int WRITE_FAIL = 0x1234;
	public static final int READ_SUCCESS = 0x1235;
	public static final int WIFI_CONNECTED = 0x1236;
	public static final int WIFI_DISCONNECT = 0x1237;
	
	
	private TextView tvWifiText=null;
	
	private TextView tvReadtagsuccesstext=null;
	private TextView tvWificonnectedtext=null;
	private TextView tvReadtagsuccess=null;
	private TextView tvWificonnected=null;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        setContentLayout(R.layout.main);
        
        NFCUtil.init(this);
        if(!NFCUtil.isNFCDevice){
        	Intent intent=new Intent(WifiTagActivity.this,GoodbyeActivity.class);
        	startActivity(intent);
        	finish();
        }
        
        DBUtil.init(this, 1);
     
        processIntent(getIntent());
        
        tvWifiText=(TextView) findViewById(R.id.wifitext);
        //tvWifiText.setText("heheheh");
        //tvFindtag=(TextView) findViewById(R.id.findtag);
        
        
        tvReadtagsuccesstext=(TextView) findViewById(R.id.readtagsuccesstext);
        tvWificonnectedtext=(TextView) findViewById(R.id.wificonnectedtext);
        tvReadtagsuccess=(TextView) findViewById(R.id.readtagsuccess);
        tvWificonnected=(TextView) findViewById(R.id.wificonnected);
        Button topbarRightBtn=(Button) findViewById(R.id.topbarRightBtn);
        topbarRightBtn.setAlpha(1);
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	NFCUtil.init(this);
    	NFCUtil.enableForeground(this);
    	
    }
    
    @Override
    public void onClickOfTopbarRightBtn(View source) {
    	// TODO Auto-generated method stub
    	super.onClickOfTopbarRightBtn(source);
    	Intent intent=new Intent(WifiTagActivity.this,CreateWifiTagActivity.class);
    	startActivity(intent);
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	Log.d("wifitag", "new intent in");
    	super.onNewIntent(intent);
    	
    	setIntent(intent);
    	
    	processIntent(intent);
    	Log.d("wifitag", "new intent out");
    }
    
    
    private void processIntent(Intent intent){
    	
    	if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
    			||NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
    			||NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
    		//
    		System.out.println("nfc tag found");
    		//Toast.makeText(this, "FIND A TAG...", 50).show();

    		if(NFCUtil.isAvailable()){
    			//process...
    			System.out.println("processing nfc tag");
    			
    			//findAtag(true);
    			tvWifiText.setText("");
    			
    			tvReadtagsuccesstext.setVisibility(1);
    			tvReadtagsuccess.setBackgroundResource(R.drawable.tagread_wait);
    			
    			final Handler h = new Handler(){
					@Override
					public void handleMessage(Message msg) {
						// TODO Auto-generated method stub
						super.handleMessage(msg);
						
						if(msg.what==WRITE_SUCESS){
							Toast.makeText(WifiTagActivity.this, "write success", 50).show();
						}
						if(msg.what==WRITE_FAIL){
							Toast.makeText(WifiTagActivity.this, "write fail", 50).show();
						}
						if(msg.what==READ_SUCCESS){
							//Toast.makeText(WifiTagActivity.this, "write fail", 50).show();
							readTagSuccess(true);
						}
						if(msg.what==WIFI_CONNECTED){
							//Toast.makeText(WifiTagActivity.this, "write fail", 50).show();
							wificonnected(true);
							if(readTagStr!=null&&readTagStr.length>0)
								tvWifiText.setText("已成功连接上WiFi: "+readTagStr[0]);
							
						}
						if(msg.what==WIFI_DISCONNECT){
							//Toast.makeText(WifiTagActivity.this, "write fail", 50).show();
							wificonnected(false);
							
						}
					}
				};
    			
    			if(writeTagWaitingList.size() !=0){
    				String[] writeTagStr = writeTagWaitingList.remove(0);
    				Thread thread = new WriteNFCThread(h, writeTagStr, getIntent());
    				thread.start();

    			}else{
    				
    				readTagStr = NFCUtil.readMessage(intent);
    				
    				if(readTagStr==null||readTagStr.length!=3){
    					readTagSuccess(false);
    					tvWificonnectedtext.setVisibility(1);
                    	wificonnected(false);
                    	return;
    				}
    					
    				/*
    				StringBuffer sb=new StringBuffer();
    				for(int i=0;i<readTagStr.length;i++){
    					sb.append(readTagStr[i]);
    					sb.append("\n");
    				}
        			
    				
        			Toast.makeText(this,sb.toString(), 50).show();*/
    				/*
    				System.out.println(readTagStr[0]);
    				System.out.println(readTagStr[1]);
    				System.out.println(readTagStr[2]);
    				*/
    				Thread thread = new Thread(){
    					public void run() {
    						
    						try {
                            	
    							String ssid=null;
    							String password=null;
    		    				if(readTagStr[2].equals("1")){
    		    					RSA rsa = new RSA();
    		    					//ssid = rsa.decrypt(readTagStr[0]);
    		    					ssid=readTagStr[0];
    		    					password = rsa.decrypt(readTagStr[1]);
    		    					
    		    				}else{
    		    					ssid=readTagStr[0];
    		    					password=readTagStr[1];
    		    				}
    		    				
    		    				System.out.println(readTagStr[0]);
    		    				System.out.println(readTagStr[1]);
    							
                    			WifiConnect wifiConnect = new WifiConnect(WifiTagActivity.this);
                    			WifiConnect.ConnectStatus connectStatus = wifiConnect.connect(ssid, password, 0);
                    			
                    			if(connectStatus==WifiConnect.ConnectStatus.CONNECT_STATUS_OK)
                    				h.sendEmptyMessage(WIFI_CONNECTED);
                    			else{
                    				h.sendEmptyMessage(WIFI_DISCONNECT);
                    			}
    						} catch (Exception e) {
    							// TODO: handle exception
    							h.sendEmptyMessage(WIFI_DISCONNECT);
    						}
    						
    					}
    				};
    				
        			
                    if(isWifiTag(readTagStr)){
                    	//h.sendEmptyMessage(READ_SUCCESS);
                    	readTagSuccess(true);
                    	tvWificonnectedtext.setVisibility(1);
                    	tvWificonnected.setBackgroundResource(R.drawable.tagread_wait);
                    	thread.start();
                    }else{
                    	readTagSuccess(false);
                    	tvWificonnectedtext.setVisibility(1);
                    	wificonnected(false);
                    }
    			}
    		}else{
    			//findAtag(false);
    		}
    	}
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	NFCUtil.disableForeground(this);
    }
    
    private void hiddenInputMethod() {
    	InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		if(inputMethodManager!=null && getCurrentFocus() != null)
		{
			inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
    }
    
  /*
    private void findAtag(boolean found){
    	
    	if(found&&tvFindtag!=null){
    		tvFindtag.setBackgroundResource(R.drawable.tagread_ok);
    	}else{
    		tvFindtag.setBackgroundResource(R.drawable.tagread_error);
    	}
    	
    }
    */
    
    
    private void readTagSuccess(boolean success){
    	
    	if(success&&tvReadtagsuccess!=null){
    		tvReadtagsuccess.setBackgroundResource(R.drawable.tagread_ok);
    	}else{
    		tvReadtagsuccess.setBackgroundResource(R.drawable.tagread_error);
    	}
    }
    
    private void wificonnected(boolean connected){
    	
    	if(connected&&tvWificonnected!=null){
    		tvWificonnected.setBackgroundResource(R.drawable.tagread_ok);
    	}else{
    		tvWificonnected.setBackgroundResource(R.drawable.tagread_error);
    	}
    }
    
    private boolean isWifiTag(String[] msgStr){
    	/*
    	if(msgStr.length!=NFCUtil.tagInfo.length)
    		return false;
    	
    	if(!msgStr[0].startsWith("request:"))
    		return false;
    	
    	if(!msgStr[1].startsWith("ssid:"))
    		return false;
    	
    	if(!msgStr[2].startsWith("password:"))
    		return false;
    	*/
    	return true;
    }
    
    
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	writeTagWaitingList.clear();
    	super.onDestroy();
    }
   
    
}

class WriteNFCThread extends Thread {
	private Handler handler;
	private String[] writeMsg = null;
	private Intent intent = null;
	public WriteNFCThread(Handler handler, String[] writeMsg, Intent intent) {
		// TODO Auto-generated constructor stub
		this.handler = handler;
		this.writeMsg = writeMsg;
		this.intent = intent;
	}
	
	@Override
	public void run() {
		if(NFCUtil.writeMessage(writeMsg, intent)){
			handler.sendEmptyMessage(WifiTagActivity.WRITE_SUCESS);
		}else{
			handler.sendEmptyMessage(WifiTagActivity.WRITE_FAIL);
		}
	}
}
