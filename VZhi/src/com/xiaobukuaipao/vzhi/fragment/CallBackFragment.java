package com.xiaobukuaipao.vzhi.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.event.ProfileEventLogic;
import com.xiaobukuaipao.vzhi.event.RegisterEventLogic;

public abstract class CallBackFragment extends Fragment {

	protected int replaceId = R.id.common_fragment_section_id;

	protected OnBackRequest onBackRequest;

	protected ProfileEventLogic mProfileEventLogic;
	protected RegisterEventLogic mRegisterEventLogic;
	
	/**
	 * 每个页面菜单栏目 左侧菜单显示
	 */
	public void setHeaderMenuByLeft() {
		FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(
				R.id.menu_bar_back_btn_layout);
		frameLayout.setVisibility(View.VISIBLE);
		frameLayout.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						getActivity().onBackPressed();
					}
				});
	}

	public Button setHeaderMenuByRight(int title) {
		Button menuBarRight = (Button) getActivity().findViewById(
				R.id.menu_bar_right);
		menuBarRight.setVisibility(View.VISIBLE);
		// 大于表示要更改菜单栏右边按钮标题，否则保持按钮默认的标题
		if (title > 0) {
			menuBarRight.setText(title);
		}
		return menuBarRight;
	}

	/**
	 * 
	 * 每个页面菜单栏目中间文字描述
	 * 
	 * @param title
	 */
	public void setHeaderMenuByCenterTitle(int title) {
		TextView textTitleView = (TextView) getActivity().findViewById(
				R.id.ivTitleName);
		textTitleView.setText(title);
	}

	/**
	 * 按钮btnId触发的事情全部走这个流程
	 * 
	 * @param btnId
	 */
	public void onClickListenerBySaveDataAndRedirectActivity(int btnId) {
		getActivity().findViewById(btnId).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						confirmNextAction();
						chooseRedirectActivity(view);
					}
				});
	}

	public void confirmNextAction() {
	}

	public void chooseRedirectActivity(View view) {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mRegisterEventLogic = new RegisterEventLogic();
		mProfileEventLogic = new ProfileEventLogic();
		mRegisterEventLogic.register(this);
		mProfileEventLogic.register(this);
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroy() {
		mProfileEventLogic.unregister(this);
		mRegisterEventLogic.unregister(this);
		super.onDestroy();
	}
	@Override
	public void onDestroyView() {
		
		
		super.onDestroyView();
	}
	public abstract void onEventMainThread(Message msg);
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onBackRequest = (OnBackRequest) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnBackRequest");
		}
	}

}
