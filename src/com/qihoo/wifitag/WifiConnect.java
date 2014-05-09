package com.qihoo.wifitag;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiConnect {
	public enum WifiEncryptType
	{
		WIFI_ENCRYPT_WEP, WIFI_ENCRYPT_WPA, WIFI_ENCRYPT_NOPASS, WIFI_ENCRYPT_UNKNOWN
	}
	public enum ConnectStatus
	{
		CONNECT_STATUS_OK, 
		CONNECT_STATUS_ERROR_NET, 
		CONNECT_STATUS_ERROR_AP, 
		CONNECT_STATUS_ERROR_CONFIG, 
		CONNECT_STATUS_ERROR_CONNECT
	}
	public class apInfo{
		public String ssid;
		public WifiEncryptType encryptType;
		
		public apInfo(String ssid, WifiEncryptType encryptType){
			this.ssid = ssid;
			this.encryptType = encryptType;
		}
	}
	private Context context = null;
	private WifiManager wifiManager = null;
	private ConnectivityManager connectManager = null;
	private String ssid = null;
	private String password = null;
	private WifiEncryptType encryptType = null;
	private int clearPwdTimeout;
	private static int netId;
	private static Intent intentStartService = null;
	
	
	public WifiConnect(Context context){
		this.context = context;
		this.wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		this.connectManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	/**
	 * connect to a WIFI
	 * @param ssid
	 * @param password
	 * @param encryptType
	 * @param clearPwdTimeout (unit : seconds)
	 * @return whether the connect operation is successfully or not
	 * @throws InterruptedException 
	 */
	public ConnectStatus connect(String ssid, String password, int clearPwdTimeout) throws InterruptedException{
		this.ssid = ssid;
		this.password = password;
		this.clearPwdTimeout = clearPwdTimeout;
		
		if (intentStartService != null){
			context.stopService(intentStartService);
		}
		
		if (!openWifi()){
			return ConnectStatus.CONNECT_STATUS_ERROR_NET;
		}
		
		List<WifiConnect.apInfo> apList = this.getApList();
		for (WifiConnect.apInfo apListItem : apList){
			if (apListItem.ssid.equals(this.ssid)){
				this.encryptType = apListItem.encryptType;
			}
		}
		if (this.encryptType == null){
			return ConnectStatus.CONNECT_STATUS_ERROR_AP;
		}
		
		netId = this.wifiManager.addNetwork(getWifiConfig());
		if (-1 == netId) {
			return ConnectStatus.CONNECT_STATUS_ERROR_CONFIG;
		}
		
		boolean isConnected = this.wifiManager.enableNetwork(netId, true);
		boolean isConnectToDestSsid = false;
		
		//Thread.sleep(5000);
		
		for (int i = 0; isConnected && i != 5; ++i){
			try {
				State wifiState = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
				String ss = wifiManager.getConnectionInfo().getSSID();
				if (wifiState == State.CONNECTED
				    && (wifiManager.getConnectionInfo().getSSID().equals(this.ssid)
				        || wifiManager.getConnectionInfo().getSSID().equals("\"" + this.ssid + "\""))){
					isConnectToDestSsid = true;
					break;
				}else{
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		if (this.clearPwdTimeout > 0){
			intentStartService = new Intent(context, WifiClearConfigService.class);
			intentStartService.putExtra("ssid", this.ssid);
			intentStartService.putExtra("netId", netId);
			intentStartService.putExtra("clearPwdTimeout", this.clearPwdTimeout);
			context.startService(intentStartService);
		}

		return isConnectToDestSsid == true ? ConnectStatus.CONNECT_STATUS_OK : ConnectStatus.CONNECT_STATUS_ERROR_CONNECT;
	}
	
	/**
	 * disconnect current connected WIFI
	 * @return whether the disconnect operation is successfully or not
	 */
	public boolean disconnect() {
		boolean isDisconnected = this.wifiManager.disableNetwork(netId);
		this.wifiManager.removeNetwork(netId);
		if (intentStartService != null){
			context.stopService(intentStartService);
		}

		return isDisconnected;
	}
	
	/**
	 * 
	 * @return apList
	 * @throws InterruptedException 
	 */
	public List<WifiConnect.apInfo> getApList(){
		List<WifiConnect.apInfo> apList = new ArrayList<WifiConnect.apInfo>();
		
		if (!wifiManager.isWifiEnabled()){
			try {
				this.openWifi();
				Thread.sleep(5000);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		List<ScanResult> apListRaw = wifiManager.getScanResults();
		
		for (ScanResult apListRawItem : apListRaw){
			String encryptType = apListRawItem.capabilities.toString();
			if (encryptType.contains("WPA")){
				apList.add(new apInfo(apListRawItem.SSID, WifiEncryptType.WIFI_ENCRYPT_WPA));
			}else if (encryptType.contains("WEP")){
				apList.add(new apInfo(apListRawItem.SSID, WifiEncryptType.WIFI_ENCRYPT_WEP));
			}else if (encryptType.equals("")){
				apList.add(new apInfo(apListRawItem.SSID, WifiEncryptType.WIFI_ENCRYPT_NOPASS));
			}else{
				apList.add(new apInfo(apListRawItem.SSID, WifiEncryptType.WIFI_ENCRYPT_UNKNOWN));
			}
		}
		
		return apList;
	}
	
	/**
	 * open WIFI module
	 * @return whether open operation is successfully or not
	 */
	public boolean openWifi(){
		boolean isWifiOpened = this.wifiManager.isWifiEnabled();
		
        if(!isWifiOpened && !this.wifiManager.setWifiEnabled(true))
    	{
    		 return false;
    	}
        
        while(wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED)
        {
        	 try{
        		 Thread.currentThread();
        		 Thread.sleep(100);
        	 }
        	 catch(InterruptedException ie){
        		 Log.e("WifiConnect", ie.toString());
        	 }
        }
		
		return true;
	}
	
	private WifiConfiguration isSSIDConfigured() {
   	 	List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
	    for (WifiConfiguration existingConfig : existingConfigs) 
	    {
			if (existingConfig.SSID.equals("\"" + this.ssid + "\""))
			{
				return existingConfig;
			}
	    }
	    
	    return null; 
	}
	
	private WifiConfiguration getWifiConfig() {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + this.ssid + "\"";

		WifiConfiguration tempConfig = this.isSSIDConfigured();
		if (tempConfig != null) {
			wifiManager.removeNetwork(tempConfig.networkId);
		}

		if (encryptType == WifiEncryptType.WIFI_ENCRYPT_NOPASS)
		{
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (encryptType == WifiEncryptType.WIFI_ENCRYPT_WEP)
		{
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + password + "\"";
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (encryptType == WifiEncryptType.WIFI_ENCRYPT_WPA)
		{
			config.preSharedKey = "\"" + password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		
		return config;
	}
}
