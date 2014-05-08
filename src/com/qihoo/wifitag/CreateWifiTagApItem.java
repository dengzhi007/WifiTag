package com.qihoo.wifitag;

import com.qihoo.wifitag.WifiConnect.WifiEncryptType;

public class CreateWifiTagApItem {
	public String ssid;
	public WifiEncryptType encryptType;

	public CreateWifiTagApItem(String ssid, WifiEncryptType encryptType) {
		this.ssid = ssid;
		this.encryptType = encryptType;
	}
}