package com.qihoo.wifitag;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CreateWifiTagContentPager extends FragmentPagerAdapter {

	private Fragment[] fragmentsDirectory;

	public CreateWifiTagContentPager(FragmentManager fm, Context context) {
		super(fm);
		fragmentsDirectory = new Fragment[1];
		fragmentsDirectory[0] = new CreateWifiTagFragment(context);
	}

	@Override
	public Fragment getItem(int position) {
		return fragmentsDirectory[position];
	}

	@Override
	public int getCount() {
		return fragmentsDirectory.length;
	}
}