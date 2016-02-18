package com.xiaobukuaipao.vzhi.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;

public abstract class BaseFragment extends Fragment {
	protected Activity activity;
	protected OnItemSelectedListener mOnItemSelectedListener;
	
	protected int replaceId = R.id.common_fragment_section_id;
	
	// Container Activity must implement this interface
    public interface OnItemSelectedListener {
        public void onItemSelected(int position);
    }

	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
		try {
			mOnItemSelectedListener = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnItemSelectedListener");
        }

	}
	
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	}
	
	/**
     *  每个页面菜单栏目 左侧菜单显示
     */
    public void setHeaderMenuByLeft() {
        FrameLayout frameLayout = (FrameLayout) activity.findViewById(R.id.menu_bar_back_btn_layout);
        frameLayout.setVisibility(View.VISIBLE);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }
    
    
    public void setHeaderMenuByRight(int title) {
        Button menuBarRight = (Button) getActivity().findViewById(R.id.menu_bar_right);
        menuBarRight.setVisibility(View.VISIBLE);
        // 大于表示要更改菜单栏右边按钮标题，否则保持按钮默认的标题
        if (title > 0) {
            menuBarRight.setText(title);
        }
        
    }
	
   /**
    * 按钮btnId触发的事情全部走这个流程
    *
    * @param btnId
    */
   public void onClickListenerBySaveDataAndRedirectActivity(int btnId) {
       getActivity().findViewById(btnId).setOnClickListener(new View.OnClickListener() {
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

	/**
     * EventBus订阅者事件通知的函数, UI线程
     * 
     * @param msg
     */
   public void onEventMainThread(Message msg) {}
    
  
    
	/**
	 * 
	 * 每个页面菜单栏目中间文字描述
	 * 
	 * @param title
	 */
	public void setHeaderMenuByCenterTitle(int title) {
		TextView textTitleView = (TextView) getActivity().findViewById(R.id.ivTitleName);
		textTitleView.setText(title);
	}
	
	public void onResume() {
	    super.onResume();
//	    MobclickAgent.onPageStart("MainScreen"); //统计页面
	}
	public void onPause() {
	    super.onPause();
//	    MobclickAgent.onPageEnd("MainScreen"); 
	}
	
	

}
