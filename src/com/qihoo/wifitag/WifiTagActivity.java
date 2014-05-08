package com.qihoo.wifitag;


import java.util.ArrayList;
import java.util.List;

import com.qihoo.wifitag.util.DBUtil;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

public class WifiTagActivity extends SlideMenuActivity {
    /** Called when the activity is first created. */
	
	private String[] readTagStr=null;
    
	//write message to tag :  writeTagWaitingList.add(writeStr);
	private List<String[]> writeTagWaitingList = new ArrayList<String[]>();
	
	public static final int WRITE_SUCESS = 0x1234;
	public static final int WRITE_FAIL = 0x1233;


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
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	NFCUtil.init(this);
    	NFCUtil.enableForeground(this);
    	
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
    		Toast.makeText(this, "FIND A TAG...", 50).show();

    		if(NFCUtil.isAvailable()){
    			//process...
    			System.out.println("processing nfc tag");
    			
    			findAtag(true);
    			
    			if(writeTagWaitingList.size() !=0){
    				String[] writeTagStr = writeTagWaitingList.remove(0);
    				
    				Handler h = new Handler(){
    					@Override
    					public void handleMessage(Message msg) {
    						// TODO Auto-generated method stub
    						super.handleMessage(msg);
    						
    						if(msg.what==WRITE_SUCESS){
    							Toast.makeText(WifiTagActivity.this, "write success", 50).show();
    						}else{
    							Toast.makeText(WifiTagActivity.this, "write fail", 50).show();
    						}
    					}
    				};
    				
    				Thread thread = new WriteNFCThread(h, writeTagStr, getIntent());
    				thread.start();

    			}else{
    				
    				readTagStr = NFCUtil.readMessage(intent);
    				
    				StringBuffer sb=new StringBuffer();
    				for(int i=0;i<readTagStr.length;i++){
    					sb.append(readTagStr[i]);
    					sb.append("\n");
    				}
        			
    				
        			Toast.makeText(this,sb.toString(), 50).show();
        			
                    if(isWifiTag(readTagStr)){
                    	try {
                        	readTagSuccess(true);
                			
                			WifiConnect wifiConnect = new WifiConnect(WifiTagActivity.this);
                			WifiConnect.ConnectStatus connectStatus = wifiConnect.connect(readTagStr[0], readTagStr[1], 0);
                			
                			if(connectStatus==WifiConnect.ConnectStatus.CONNECT_STATUS_OK)
                				wificonnected(true);
                			else{
                				wificonnected(false);
                			}
						} catch (Exception e) {
							// TODO: handle exception
							wificonnected(false);
						}

                    }else{
                    	readTagSuccess(false);
                    	wificonnected(false);
                    }
        			
        			
        			
        			
    			}
    			
    		}else{
    			findAtag(false);
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
    
  
    private void findAtag(boolean found){
    	TextView tv=(TextView) findViewById(R.id.findtag);
    	if(found){
    		tv.setBackgroundResource(R.drawable.tagread_ok);
    	}else{
    		tv.setBackgroundResource(R.drawable.tagread_error);
    	}
    	
    }
    
    private void readTagSuccess(boolean success){
    	TextView tv=(TextView) findViewById(R.id.readtagsuccess);
    	if(success){
    		tv.setBackgroundResource(R.drawable.tagread_ok);
    	}else{
    		tv.setBackgroundResource(R.drawable.tagread_error);
    	}
    }
    
    private void wificonnected(boolean connected){
    	TextView tv=(TextView) findViewById(R.id.wificonnected);
    	if(connected){
    		tv.setBackgroundResource(R.drawable.tagread_ok);
    	}else{
    		tv.setBackgroundResource(R.drawable.tagread_error);
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
