package com.qihoo.wifitag;

import java.io.FileFilter;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Text;

import com.qihoo.wifitag.WifiConnect.apInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class CreateWifiTagFragment extends Fragment implements OnClickListener {
	private ListView ap_list_view;
	private static List<CreateWifiTagApItem> ap_list = new ArrayList<CreateWifiTagApItem>();
	private static ImageView btnFolderBackward = null;
	private Context context;
	private static String currentFolderPath = "/";
	private static CreateWifiTagApItemAdapter adapter = null;
	
	public CreateWifiTagFragment(Context context) {
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.create_wifi_tag_ap_list, container, false);
		findView(view);
		btnFolderBackward = (ImageView)getActivity().findViewById(R.id.create_tag_backward);
		btnFolderBackward.setOnClickListener(new FolderBackwardListener());
		
		adapter = CreateWifiTagApItemAdapter.instance(getActivity(), ap_list, this);
		refreshFileList(currentFolderPath);
		ap_list_view.setOnItemClickListener(new FolderClickListener());
		ap_list_view.setAdapter(adapter);
		return view;
	}

	private void findView(View v) {
		ap_list_view = (ListView) v.findViewById(R.id.ap_list);
	}
	
	private void refreshFileList(String rootNodePath) {
		ap_list.clear();

		WifiConnect wifiConnect = new WifiConnect(context);
		List<apInfo> apInfo = wifiConnect.getApList();
		for (int i = 0; i != apInfo.size(); ++i){
			ap_list.add(new CreateWifiTagApItem(apInfo.get(i).ssid, apInfo.get(i).encryptType));
		}	
		
		adapter.notifyDataSetInvalidated();
	}
	
	class FolderClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			String ssid = ap_list.get(position).ssid;
			//Intent intent = new Intent(getActivity(), WriteTagActivity.class);
			Intent intentInputPwd = new Intent(getActivity(), CreateWifiTagInputPwdActivity.class);
			intentInputPwd.putExtra("ssid", ssid);
			
			getActivity().startActivityForResult(intentInputPwd, 0);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	class FolderBackwardListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//
		}
	}
	
	private void showPopupWindows(View v) {
		// menu.showAtLocation(v, Gravity.CENTER, 0, 0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
}