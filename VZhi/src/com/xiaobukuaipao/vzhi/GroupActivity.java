package com.xiaobukuaipao.vzhi;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.xiaobukuaipao.vzhi.adapter.GroupFragmentPagerAdapter;
import com.xiaobukuaipao.vzhi.fragment.BaseFragment.OnItemSelectedListener;
import com.xiaobukuaipao.vzhi.widget.seeking.PagerSlidingTabStrip;

/**
 * 群列表,目前有匿名投递群,和实名兴趣群<br>
 * 1.群入口:投递群的加群入口只有投递简历系统自动添加,实名兴趣群只有系统推荐信息才能添加<br>
 * 2.退群措施,当前页面进入群聊天页面,会带入一个标志位,当对群信息操作成功的时候以便于根据此节点标志追溯,并更新当前列表<br>
 * 3.意义:投递群便于投递相同职位的人互相交流心得,预估自己的竞争价值,并可以根据预估采取对应措施,兴趣群增加应用的留存,增加活跃度完全是社交功能
 * 
 *	@since 2015年01月04日20:56:41
 */
public class GroupActivity extends BaseFragmentActivity implements OnItemSelectedListener {
	/** 
     * PagerSlidingTabStrip的实例 
     */  
    private PagerSlidingTabStrip tabs;  
    /** 
     * 获取当前屏幕的密度 
     */  
    private DisplayMetrics dm;
	/**
	 * 群列表页适配器
	 */
	private GroupFragmentPagerAdapter mPagerAdapter;
	/**
	 * 群列表页
	 */
	private  ViewPager mPager ;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        setHeaderMenuByCenterTitle(R.string.person_group_str);
        setHeaderMenuByLeft(this);
        initUIAndData();
	}
	
	private void initUIAndData() {
		//初始化数据
        dm = getResources().getDisplayMetrics();  
        mPager =  (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);  
        
        mPagerAdapter = new GroupFragmentPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setTitles(getResources().getStringArray(R.array.group_name));
        
        mPager.setOffscreenPageLimit(2);
        mPager.setAdapter(mPagerAdapter);  
        tabs.setViewPager(mPager);  
        
        setTabsValue();  
	}
	
	/**
	 * 对PagerSlidingTabStrip的各项属性进行赋值
	 */
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		tabs.setUnderlineHeight(1);
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight(4);
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, dm));
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(getResources().getColor(R.color.base_interface_color));
		// 取消点击Tab时的背景色
		tabs.setTabBackground(0);
		// 竖向填充
		tabs.setFillViewport(true);
		//
		tabs.setUnderlineColorResource(R.color.general_color_cccccc);
		tabs.setTextColorResource(R.color.general_color_cccccc);
		tabs.setSelectedTabTextColor(getResources().getColor(R.color.base_interface_color));
	}

	@Override
	public void onItemSelected(int position) {
		
	}

	
}
