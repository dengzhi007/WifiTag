package com.qihoo.wifitag;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

public class WifiClearConfigService extends Service{
	private String ssid;
	private int netId;
	private int clearPwdTimeout;
	private WifiManager wifiManager;
	private boolean isServiceRun;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		this.ssid = intent.getStringExtra("ssid");
		this.netId = intent.getIntExtra("netId", -1);
		this.clearPwdTimeout = intent.getIntExtra("clearPwdTimeout", -1);
		this.isServiceRun = true;
		wifiManager = (WifiManager)WifiClearConfigService.this.getSystemService(Context.WIFI_SERVICE);
		clearPassword();
		Log.i("WifiLog", "WifiClearConfigService start!");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private boolean isDisconnectedWifi(){
		int wifiState = wifiManager.getWifiState();
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String currentSSID = wifiInfo.getSSID();
		int currentNetId = wifiInfo.getNetworkId();
		
		if (wifiState ==  WifiManager.WIFI_STATE_DISABLED
		    || (wifiInfo != null
		    	&&	wifiState == WifiManager.WIFI_STATE_ENABLED 
	    	    && (currentSSID == null 
	    	        || currentNetId == -1 
	    	        || !currentSSID.equals("\"" + WifiClearConfigService.this.ssid + "\"") 
	    	        || wifiInfo.getNetworkId() != WifiClearConfigService.this.netId))){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		WifiClearConfigService.this.isServiceRun = false;
		super.onDestroy();
	}
	
	private void clearPassword(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (WifiClearConfigService.this.isServiceRun){
					
					try {
						boolean isTimedout = false;
						for (int i = 0; i != WifiClearConfigService.this.clearPwdTimeout; ++i){
							if (isDisconnectedWifi()){
								isTimedout = true;
								Thread.sleep(1000);
								Log.i("WifiLog", "disconnectedWifi");
							}else{
								isTimedout = false;
								Log.i("WifiLog", "connectedWifi");
								break;
							}
						}
						if (isTimedout){
							WifiConnect wifiConnect = null;
							if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED){
								wifiConnect = new WifiConnect(WifiClearConfigService.this);
								wifiConnect.openWifi();
							}
							Thread.sleep(10000);
							if (wifiManager.getConnectionInfo().getNetworkId() == netId){
								wifiConnect.disconnect();
							}
							Thread.sleep(5000);
							boolean result = wifiManager.removeNetwork(WifiClearConfigService.this.netId);
							Thread.sleep(5000);
							if (wifiConnect != null){
								wifiManager.setWifiEnabled(false);
								Log.i("WifiLog", "");
							}
							Log.i("WifiLog", result + "");
							break;
						}
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
