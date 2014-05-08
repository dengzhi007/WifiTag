package com.qihoo.wifitag;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SlideMenuActivity extends Activity {
	
	private enum menuSelected{tagread, mytag};

	private int[] topbarResID = new int[]{R.drawable.tagread_topbar,R.drawable.mytag_topbar};
	
	public int currentMenuRow=0;
	
	private int screenWidth=640;
	private int rightPadding=80;
	
	private int leftEdgeOfLeftMargin=-560;
	private int rightEdgeOfLeftMargin=0;
	
	private View menuLayout=null;
	private ViewGroup contentLayout=null;
	
	private LinearLayout.LayoutParams menuParams;  
	
	public boolean isMenuVisible=false;
	
	private int shouldSlideVelocity=200;
	//手指按下，滑动，放开的x坐标
	private float xDown,xMove,xUp;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.slidemenu);
		
		
		try {
			if(getIntent().getExtras().get("currentMenuRow")!=null)
				currentMenuRow=(Integer) getIntent().getExtras().get("currentMenuRow");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
			
		
		init();
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setSelectedMenu(currentMenuRow);
	}
	
	protected void setContentLayout(int resID) {
		
		getLayoutInflater().inflate(resID, contentLayout);
		
	}
	

	
	public void setSelectedMenu(int row){
		
		Button tagreadBtn=(Button) findViewById(R.id.slideMenuBtnTagread);
		Button mytagBtn=(Button) findViewById(R.id.slideMenuBtnMytag);
		switch(row){
		case 0:
			tagreadBtn.setBackgroundResource(R.drawable.tagmenu_tagread_green);
			mytagBtn.setBackgroundResource(R.drawable.tagmenu_mytag_white);
			
			break;
		case 1:
			tagreadBtn.setBackgroundResource(R.drawable.tagmenu_tagread_white);
			mytagBtn.setBackgroundResource(R.drawable.tagmenu_mytag_green);
			
			break;
		default :
				
			break;
			
			
		}
	}
	
	private void setTopbarBackground(int drawableResID){
		TextView topbarBg= (TextView) findViewById(R.id.topbarbackground);
		topbarBg.setBackgroundResource(drawableResID);
		
	}
	

	
	
	
	public void onClickOfMenuBtn(View source){
		//按顺序从0到1 表示一触即连 我的wifi贴
	
		Intent intent=null;
		switch(source.getId()){
		case R.id.backward:
			
			if(isMenuVisible){
				slideToContent();
	    	}else{
	    		slideToMenu();
	    	}
			
			break;
			
		
		case R.id.slideMenuBtnTagread:
			
			//setTopbarBackground(int drawableID);
			//setSelectedMenu(int row);
			if(currentMenuRow==0)
				return;
			currentMenuRow=0;
			slideToContent();
			//setSelectedMenu(0);
			intent=new Intent();
			intent.setAction("android.intent.action.readtag");
			intent.putExtra("currentMenuRow", (Integer)0);
			startActivity(intent);
			
			break;
		case R.id.slideMenuBtnMytag:
			if(currentMenuRow==1)
				return;	
			currentMenuRow=1;
			slideToContent();
			//setSelectedMenu(1);
			intent=new Intent();
			intent.setAction("android.intent.action.mytag");
			intent.putExtra("currentMenuRow", (Integer)1);
			startActivity(intent);
	        break;
		default:
			
			break;
		
		}
		
	}
	
	public void onClickOfTopbarRightBtn(View source){
		
		
	}
	
    private void init(){
    	
    	WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);  
        screenWidth = window.getDefaultDisplay().getWidth();  
        rightPadding= (int)screenWidth/5;
        
        contentLayout = (ViewGroup) findViewById(R.id.contentLayout);  
        menuLayout = findViewById(R.id.menuLayout);  
        menuParams = (LinearLayout.LayoutParams) menuLayout.getLayoutParams();  
        
        menuParams.width = screenWidth - rightPadding;  
        // 左边缘的值赋值为menu宽度的负数  
        leftEdgeOfLeftMargin = -menuParams.width;  
        // menu的leftMargin设置为左边缘的值，这样初始化时menu就变为不可见  
        menuParams.leftMargin = leftEdgeOfLeftMargin;  
        
        contentLayout.getLayoutParams().width = screenWidth;  
        
    	setSelectedMenu(currentMenuRow);
    	
    	setTopbarBackground(topbarResID[currentMenuRow]);
    }
	
    

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	// TODO Auto-generated method stub
    	/*
    	
    	switch (event.getAction()) {  
        case MotionEvent.ACTION_DOWN:  
            // 手指按下时，记录按下时的横坐标  
            xDown = event.getRawX();  
            break;  
        case MotionEvent.ACTION_MOVE:  
            // 手指移动时，对比按下时的横坐标，计算出移动的距离，来调整menu的leftMargin值，从而显示和隐藏menu  
            xMove = event.getRawX();  
            int distanceX = (int) (xMove - xDown);  
            if (isMenuVisible) {  
                menuParams.leftMargin = distanceX;  
            } else {  
                menuParams.leftMargin = leftEdgeOfLeftMargin + distanceX;  
            }  
            if (menuParams.leftMargin < leftEdgeOfLeftMargin) {  
                menuParams.leftMargin = leftEdgeOfLeftMargin;  
            } else if (menuParams.leftMargin > rightEdgeOfLeftMargin) {  
                menuParams.leftMargin = rightEdgeOfLeftMargin;  
            }  
            menuLayout.setLayoutParams(menuParams);  
            break;  
        case MotionEvent.ACTION_UP:  
            // 手指抬起时，进行判断当前手势的意图，从而决定是滚动到menu界面，还是滚动到main界面  
            xUp = event.getRawX();  
            if (wantToShowMenu()){  
                 slideToMenu();  

            }else{  
            	 slideToContent();  
            }  
            
            break;  
        }  */
        return super.onTouchEvent(event);
    }
    
    private boolean wantToShowContent(){  
        return xUp - xDown < 0 && isMenuVisible;  
    } 
   
    private boolean wantToShowMenu(){
    	return xUp - xDown > 0 && !isMenuVisible;
    }
    
    private void slideToContent(){
    	
    	new ScrollTask().execute(-10); 
    }
    
    private void slideToMenu(){
    	
    	new ScrollTask().execute(10); 
    }
    
    private class ScrollTask extends AsyncTask<Integer, Integer, Integer> {  
    	  
        @Override  
        protected Integer doInBackground(Integer... speed) {  
            int leftMargin = menuParams.leftMargin;  
            // 根据传入的速度来滚动界面，当滚动到达左边界或右边界时，跳出循环。  
            while (true) {  
                leftMargin = leftMargin + speed[0];  
                if (leftMargin > rightEdgeOfLeftMargin) {  
                    leftMargin = rightEdgeOfLeftMargin;  
                    break;  
                }  
                if (leftMargin < leftEdgeOfLeftMargin) {  
                    leftMargin = leftEdgeOfLeftMargin;  
                    break;  
                }  
                publishProgress(leftMargin);  
               
                sleep(1);  
            }  
            if (speed[0] > 0) {  
                isMenuVisible = true;  
            } else {  
                isMenuVisible = false;  
            }  
            return leftMargin;  
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... leftMargin) {  
            menuParams.leftMargin = leftMargin[0];  
            menuLayout.setLayoutParams(menuParams);  
            
        }  
  
        @Override  
        protected void onPostExecute(Integer leftMargin) {  
            menuParams.leftMargin = leftMargin;  
            menuLayout.setLayoutParams(menuParams);  
            if(isMenuVisible){
            	setTopbarBackground(R.drawable.tagmenu_topbar);
            }else{
            	setTopbarBackground(topbarResID[currentMenuRow]);
            }
            	
        }  
        
        private void sleep(long millis) {  
            try {  
                Thread.sleep(millis);  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
        } 
    }  

    
}
