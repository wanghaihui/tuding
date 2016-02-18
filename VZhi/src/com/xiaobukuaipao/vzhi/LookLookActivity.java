package com.xiaobukuaipao.vzhi;

import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.fragment.BaseFragment.OnItemSelectedListener;
import com.xiaobukuaipao.vzhi.fragment.RecruitPageFragment;
import com.xiaobukuaipao.vzhi.fragment.SeekerPageFragment;
import com.xiaobukuaipao.vzhi.wrap.RecruitWrapActivity;

/**
 *	逛一逛<br>
 * 再用户未登陆的时候可以选择,逛一逛,查看推荐到逛一逛中的信息,这些筛选过后的信息,会引导给予用户充足的关注点,增加留存
 * 
 * 
 * @since 2015年01月04日13:58:10
 */
public class LookLookActivity extends RecruitWrapActivity implements OnPageChangeListener, OnClickListener, OnItemSelectedListener{
	/** 
     * PagerSlidingTabStrip的实例 
     */  
//    private PagerSlidingTabStrip tabs;  
    /** 
     * 获取当前屏幕的密度 
     */  
//    private DisplayMetrics dm;
	/**
	 * 展示页面适配器
	 */
	private LookPagerAdapter pagerAdapter;
	
	/**
	 * 展示页面
	 */
	private  ViewPager pager ;
	
	private LinearLayout tabs;
	
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_looklook);
		setHeaderMenuByLeft(this);
		
		//初始化
//		dm = getResources().getDisplayMetrics();  
		pager = (ViewPager) findViewById(R.id.pager);
//		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pagerAdapter = new LookPagerAdapter(getSupportFragmentManager());
		
		pager.setOffscreenPageLimit(2);
		pagerAdapter.setTitles(getResources().getStringArray(R.array.look_look));
		pager.setAdapter(pagerAdapter);
//		tabs.setViewPager(pager);		
//		setTabsValue();  
		
		tabs = (LinearLayout) findViewById(R.id.tabs);
		String[] titles = getResources().getStringArray(R.array.look_look);
		
		for (int i = 0; i < tabs.getChildCount(); i++) {
			TextView childAt = (TextView) tabs.getChildAt(i);
			childAt.setText(titles[i]);
			childAt.setTag(i);
			// 量取标题的宽度
			childAt.measure(MeasureSpec.makeMeasureSpec(childAt.getWidth(), MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(childAt.getHeight(), MeasureSpec.UNSPECIFIED));
			// 给标题底部加一条横线
			Drawable drawable= getResources().getDrawable(R.drawable.bg_corners_white_line_selector);  
			drawable.setBounds(0, 0, childAt.getMeasuredWidth(), drawable.getMinimumHeight());
			childAt.setCompoundDrawables(null, null, null, drawable);
			
			childAt.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int position = (Integer) v.getTag();
					switch (v.getId()) {
					case R.id.positions:
						pager.setCurrentItem(0);
						break;
					case R.id.seekers:
						pager.setCurrentItem(1);
						break;
					default:
						break;
					}
					
					for (int i = 0; i < tabs.getChildCount(); i++) {
						TextView childAt = (TextView) tabs.getChildAt(i);
						childAt.setEnabled(true);
					}
					tabs.getChildAt(position).setEnabled(false);
				}
			});
		}
		
		tabs.getChildAt(0).setEnabled(false);
		
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				for (int i = 0; i < tabs.getChildCount(); i++) {
					TextView childAt = (TextView) tabs.getChildAt(i);
					childAt.setEnabled(true);
				}
				tabs.getChildAt(arg0).setEnabled(false);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}

	/**
	 * 对PagerSlidingTabStrip的各项属性进行赋值
	 */
//	private void setTabsValue() {
//		// 设置Tab是自动填充满屏幕的
//		tabs.setShouldExpand(true);
//		// 设置Tab的分割线是透明的
//		tabs.setDividerColor(Color.TRANSPARENT);
//		// 设置Tab底部线的高度
//		tabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
//		// 设置Tab Indicator的高度
//		tabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
//		// 设置Tab标题文字的大小
//		tabs.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 19, dm));
//		// 设置Tab Indicator的颜色
//		tabs.setIndicatorColor(getResources().getColor(R.color.white));
//		
//		// 取消点击Tab时的背景色
//		tabs.setTabBackground(0);
//		
//		tabs.setSelectedTabTextColor(getResources().getColor(R.color.white));
//		tabs.setFillViewport(true);
//	}
	
	@Override
	public void onEventMainThread(Message msg) {
		
	}
	public class LookPagerAdapter extends FragmentPagerAdapter {

		private int TAB_SIZE = 2;
		private String[] titles;
		public String[] getTitles() {
			return titles;
		}

		public void setTitles(String[] titles) {
			this.titles = titles;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		public LookPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return new RecruitPageFragment();
			case 1:
				return new SeekerPageFragment();
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return TAB_SIZE;
		}

	}
	@Override
	public void onItemSelected(int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		
	}

}
