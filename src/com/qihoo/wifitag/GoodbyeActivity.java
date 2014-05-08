package com.qihoo.wifitag;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

public class GoodbyeActivity extends Activity {
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	
	final Handler h=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			finish();
		}
	};
	
	Thread t=new Thread(){
		public void run() {
			try {
				sleep(5000);
				h.sendEmptyMessage(0x1234);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	t.start();
	
}

@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
	    finish();
		return super.onTouchEvent(event);
	    
	}
}
