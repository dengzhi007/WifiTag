package com.qihoo.wifitag;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CreateWifiTagApItemAdapter extends BaseAdapter {

	private List<CreateWifiTagApItem> apItems;
	private LayoutInflater inflater;
	private OnClickListener listener;
	private static CreateWifiTagApItemAdapter adapter;
	private CreateWifiTagApItemAdapter(Context context, List<CreateWifiTagApItem> apItems, OnClickListener listener) {
		inflater = LayoutInflater.from(context);
		this.apItems = apItems;
		this.listener = listener;
	}
	public static CreateWifiTagApItemAdapter instance(Context context, List<CreateWifiTagApItem> apItems, OnClickListener listener){
		if(adapter == null){
			adapter = new CreateWifiTagApItemAdapter(context, apItems, listener);
		}
		return adapter;
	}
	
	public void setApItems(List<CreateWifiTagApItem> apItems){
		this.apItems = apItems;
	}
	
	@Override
	public int getCount() {
		return apItems.size();
	}

	@Override
	public Object getItem(int position) {
		return apItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = null;
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.create_wifi_tag_ap_item, parent, false);
			holder = new ViewHolder();
			holder.viewApSignal = (ImageView) convertView.findViewById(R.id.ap_item_signal);
			holder.viewApSsid = (TextView) convertView.findViewById(R.id.ap_item_ssid);
			holder.viewApEncryptType = (TextView) convertView.findViewById(R.id.ap_item_encrypt_type);
			convertView.setTag(holder);
		}
		
		CreateWifiTagApItem item = apItems.get(position);
		holder.viewApSignal.setImageResource(R.drawable.wifi_logo);

		holder.viewApSsid.setText(item.ssid);
		holder.viewApEncryptType.setText(item.encryptType.toString());
		holder.viewApSsid.setTag(position);
		holder.viewApSsid.setTag(position);
		holder.viewApSsid.setOnClickListener(listener);
		
		return convertView;
	}

	
	
	class fileShareClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	}
}

class ViewHolder {
	ImageView viewApSignal;
	TextView viewApSsid;
	TextView viewApEncryptType;
}