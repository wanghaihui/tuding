package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class GuidePageAdapter extends PagerAdapter {
	//界面列表  
    private List<View> views;  
      
    public GuidePageAdapter (List<View> views){  
        this.views = views;  
    }  
  
    //销毁arg1位置的界面  
    @Override  
    public void destroyItem(View container, int position, Object object) {  
        ((ViewPager) container).removeView(views.get(position));       
    }  
  
    @Override  
    public void finishUpdate(View container) {
          
    }  
  
    //获得当前界面数  
    @Override  
    public int getCount() {  
        if (views != null)  
        {  
            return views.size();  
        }  
          
        return 0;  
    }  
      
  
    //初始化arg1位置的界面  
    @Override  
    public Object instantiateItem(View container, int position) {  
          
        ((ViewPager) container).addView(views.get(position), 0);  
          
        return views.get(position);  
    }  
  
    //判断是否由对象生成界面  
    @Override  
    public boolean isViewFromObject(View view, Object object) {  
        return (view == object);  
    }  
  
    @Override  
    public void restoreState(Parcelable state, ClassLoader loader) {  
          
    }  
  
    @Override  
    public Parcelable saveState() {  
        return null;  
    }  
  
    @Override  
    public void startUpdate(View container) {  
          
    }  

}
