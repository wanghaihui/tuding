package com.xiaobukuaipao.vzhi.register;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.GuidePageAdapter;
import com.xiaobukuaipao.vzhi.wrap.RegisterWrapActivity;

public class StartRegister extends RegisterWrapActivity implements OnClickListener, OnPageChangeListener {
	
	private ViewPager viewPager;
	
	private GuidePageAdapter guideAdapter;
	
	private ArrayList<View> views;
	
	//底部小店图片  
    private ImageView[] dots ;  
      
    //记录当前选中位置  
    private int currentIndex;
    
    // 手势监听器--用户滑动
    private GestureDetector gestureDetector;
    
    private int flaggingWidth;// 互动翻页所需滚动的长度是当前屏幕宽度的1/3
	
	//引导图片资源 
	private int[] guidePics = {R.drawable.guide_one, R.drawable.guide_two, R.drawable.guide_three, R.drawable.guide_four, R.drawable.guide_five};
	
	private Handler mHandler = new Handler();
	private Runnable loginRunnable = new Runnable() {
		@Override
		public void run() {
			Intent intent = new Intent(StartRegister.this, LoginActivity.class);
			startActivity(intent);
			AppActivityManager.getInstance().finishActivity(StartRegister.this);
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
	}

	public void chooseRedirectActivity(View view) {
		
		switch (view.getId()) {
			case R.id.regist_btn:
				Intent intent = new Intent(StartRegister.this,RegisterExActivity.class);
				startActivity(intent);
				AppActivityManager.getInstance().finishActivity(StartRegister.this);
				break;
			case R.id.login_btn:
				mHandler.removeCallbacks(loginRunnable);
				mHandler.post(loginRunnable);
				break;
		}
		
	}

	@Override
	public void finish() {
		mHandler.removeCallbacks(loginRunnable);
		super.finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// just avoid mem leaks
		for (int i=0; i < views.size(); i++) {
			( (ImageView) views.get(i)).setImageDrawable(null);
		}
		
	}

	@Override
	public void initUIAndData() {
		setContentView(R.layout.login);
		
		gestureDetector = new GestureDetector(this, new GuideViewTouch());
		
		// 获取分辨率  
        DisplayMetrics dm = new DisplayMetrics();  
        getWindowManager().getDefaultDisplay().getMetrics(dm);  
        flaggingWidth = dm.widthPixels / 3;
		
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		
		onClickListenerBySaveDataAndRedirectActivity(R.id.regist_btn);
		onClickListenerBySaveDataAndRedirectActivity(R.id.login_btn);
		
		views = new ArrayList<View>();
		
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.MATCH_PARENT);  
		
		//初始化引导图片列表  
        for(int i=0; i < guidePics.length; i++) {  
            ImageView imageView = new ImageView(this);  
            imageView.setLayoutParams(mParams);
            imageView.setScaleType(ScaleType.FIT_XY);
            imageView.setImageResource(guidePics[i]);
            
            views.add(imageView);  
        }
        
      //初始化Adapter  
        guideAdapter = new GuidePageAdapter(views);  
        viewPager.setAdapter(guideAdapter);  
        //绑定回调  
        viewPager.setOnPageChangeListener(this);  
          
        //初始化底部小点  
        initDots();  
	}
	
	private void initDots() {  
        LinearLayout dotLayout = (LinearLayout) findViewById(R.id.dot_layout);  
  
        dots = new ImageView[guidePics.length];  
  
        //循环取得小点图片  
        for (int i = 0; i < guidePics.length; i++) {  
            dots[i] = (ImageView) dotLayout.getChildAt(i);  
            dots[i].setEnabled(true);//都设为灰色  
            dots[i].setOnClickListener(this);  
            dots[i].setTag(i);//设置位置tag，方便取出与当前位置对应  
        }  
  
        currentIndex = 0;  
        dots[currentIndex].setEnabled(false);//设置为白色，即选中状态  
    }  
	
	@Override
	public void onEventMainThread(Message msg) {
		
	}
	
	/** 
     *设置当前的引导页  
     */  
    private void setCurView(int position)  
    {  
        if (position < 0 || position >= guidePics.length) {  
            return;  
        }  
  
        viewPager.setCurrentItem(position);
    } 

	
	/** 
     *这只当前引导小点的选中  
     */  
    private void setCurDot(int positon)  
    {  
        if (positon < 0 || positon > guidePics.length - 1 || currentIndex == positon) {  
            return;  
        }  
  
        dots[positon].setEnabled(false);  
        dots[currentIndex].setEnabled(true);  
  
        currentIndex = positon;  
    }
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onPageScrollStateChanged(int state) {
		
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		
	}

	@Override
	public void onPageSelected(int position) {
		//设置底部小点选中状态  
        setCurDot(position); 
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onClick(View view) {
		int position = (Integer) view.getTag();  
        setCurView(position);  
        setCurDot(position); 
	}

	
	@Override  
    public boolean dispatchTouchEvent(MotionEvent event) {  
        if (gestureDetector.onTouchEvent(event)) {  
            event.setAction(MotionEvent.ACTION_CANCEL);  
        }  
        return super.dispatchTouchEvent(event);  
    }
	
	
	private class GuideViewTouch extends SimpleOnGestureListener {  
        @Override  
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,  
                float velocityY) {  
            if (currentIndex == guidePics.length - 1) {  
                if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY()  - e2.getY()) && (e1.getX() - e2.getX() <= (-flaggingWidth) || e1  
                                .getX() - e2.getX() >= flaggingWidth)) {  
                    if (e1.getX() - e2.getX() >= flaggingWidth) {  
                        goToLoginActivity();  
                        return true;  
                    }  
                }  
            }  
            return false;  
        }  
    }
	
	private void goToLoginActivity() {
		Intent intent = new Intent(StartRegister.this, LoginActivity.class);
		// Intent intent = new Intent(StartRegister.this, MainRecruitActivity.class);
		startActivity(intent);
		AppActivityManager.getInstance().finishActivity(StartRegister.this);
		// finish();
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		
	}
}
