package com.xiaobukuaipao.vzhi.fragment;

import android.os.Bundle;
import android.os.Message;

import com.xiaobukuaipao.vzhi.event.PositionEventLogic;
import com.xiaobukuaipao.vzhi.event.ProfileEventLogic;
import com.xiaobukuaipao.vzhi.event.SocialEventLogic;

import de.greenrobot.event.EventBus;

public abstract class PositionWrapFragment extends BaseFragment {

	protected PositionEventLogic mPositionEventLogic;
	protected SocialEventLogic mSocialEventLogic;
	protected ProfileEventLogic mProfileEventLogic;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mPositionEventLogic = new PositionEventLogic();
		mSocialEventLogic = new SocialEventLogic();
		mProfileEventLogic = new ProfileEventLogic();
		mPositionEventLogic.register(this);
		mSocialEventLogic.register(this);
		mProfileEventLogic.register(this);
		EventBus.getDefault().register(this);
		initUIAndData();
	}
	
	protected abstract void initUIAndData();
	
	/**
     * EventBus订阅者事件通知的函数, UI线程
     * 
     * @param msg
     */
    public abstract void onEventMainThread(Message msg);
    
    @Override
	public void onDestroy() {
		super.onDestroy();
		mPositionEventLogic.unregister(this);
		mSocialEventLogic.unregister(this);
		mProfileEventLogic.unregister(this);
		EventBus.getDefault().unregister(this);
	}

}
