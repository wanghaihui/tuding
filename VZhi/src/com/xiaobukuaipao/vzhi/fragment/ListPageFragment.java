package com.xiaobukuaipao.vzhi.fragment;

import android.R.anim;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.RecruitServiceActivity;
import com.xiaobukuaipao.vzhi.SearchPositionPreviewActivity;
import com.xiaobukuaipao.vzhi.SearchTalentPreviewActivity;
import com.xiaobukuaipao.vzhi.SigningSelfActivity;
import com.xiaobukuaipao.vzhi.event.IntentionCompleteEvent;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

import de.greenrobot.event.EventBus;

public class ListPageFragment extends BaseFragment {
	
	public static final String TAG = ListPageFragment.class.getSimpleName();
	
    private LinearLayout tabs;  
	private RecruitPageFragment recruitPageFragment;
	private FragmentTransaction transaction;
	private SeekerPageFragment seekerPageFragment;

	private TextView mRightBtn;

	private View strip0;

	private View strip1;

	private int curPosition;
	private View mLeftBtn;
	
	public ListPageFragment() {
	}
	
	public ListPageFragment(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_tab_list, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initUIAndData();
	}
	
	private void initUIAndData() {
		
		recruitPageFragment = new RecruitPageFragment();
		seekerPageFragment = new SeekerPageFragment();
		transaction = getChildFragmentManager().beginTransaction();
		transaction.add(R.id.common_pager, recruitPageFragment, "recruit");
		transaction.add(R.id.common_pager, seekerPageFragment, "seeker");
		
		transaction.hide(seekerPageFragment);
		transaction.show(recruitPageFragment);
		transaction.commit();
		
		mRightBtn = (TextView) getActivity().findViewById(R.id.menu_right_btn);
		mLeftBtn = getActivity().findViewById(R.id.menu_left_btn_layout);
		
		tabs = (LinearLayout) getActivity().findViewById(R.id.tabs);
		strip0 = tabs.findViewById(R.id.positions);
		strip1 = tabs.findViewById(R.id.seekers);
		updateStrip(strip0, 0);
		updateStrip(strip1, 1);
		
		strip0.setEnabled(false);
		
		/*mRightBtn.setVisibility(View.GONE);*/
		
		if (curPosition == 0) {
			mRightBtn.setText(getActivity().getResources().getString(R.string.find_chance_publish_position));
		} else if (curPosition == 1) {
			mRightBtn.setText(getActivity().getResources().getString(R.string.find_person_self_recommended));
		}
		
		mRightBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (curPosition == 0) {
					getActivity().startActivity(new Intent(getActivity(), RecruitServiceActivity.class));
				} else if(curPosition == 1) {
					getActivity().startActivityForResult(new Intent(getActivity(), SigningSelfActivity.class), 100);
					getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, anim.fade_out);
				}
				
			}
		});
		
		mLeftBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (curPosition == 0) {
					Intent intent = new Intent(getActivity(), SearchPositionPreviewActivity.class);
					getActivity().startActivity(intent);
				} else if (curPosition == 1) {
					Intent intent = new Intent(getActivity(), SearchTalentPreviewActivity.class);
					getActivity().startActivity(intent);
				}
			}
		});
	}

	private void updateStrip(View v, final int position){
		if(v instanceof TextView){
			TextView childAt = (TextView) v;
			String[] titles = getResources().getStringArray(R.array.look_look);
			childAt.setText(titles[position]);
			
			//量取标题的宽度
			childAt.measure(MeasureSpec.makeMeasureSpec(childAt.getWidth(), MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(childAt.getHeight(), MeasureSpec.UNSPECIFIED));
			//给标题底部加一条横线
			Drawable drawable= getResources().getDrawable(R.drawable.bg_corners_white_line_selector);  
			drawable.setBounds(0, 0, childAt.getMeasuredWidth(), drawable.getMinimumHeight());
			childAt.setCompoundDrawables(null, null, null, drawable);
			
			childAt.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					transaction = getChildFragmentManager().beginTransaction();
					transaction.hide(seekerPageFragment);
					transaction.hide(recruitPageFragment);
					
					strip0.setEnabled(true);
					strip1.setEnabled(true);
					
					mRightBtn.setVisibility(View.GONE);
					switch (v.getId()) {
					case R.id.positions:
						transaction.show(recruitPageFragment);
						strip0.setEnabled(false);
						mRightBtn.setText(getActivity().getResources().getString(R.string.find_chance_publish_position));
						mRightBtn.setVisibility(View.VISIBLE);
						curPosition = 0;
						break;
					case R.id.seekers:
						transaction.show(seekerPageFragment);
						strip1.setEnabled(false);
						mRightBtn.setText(getActivity().getResources().getString(R.string.find_person_self_recommended));
						mRightBtn.setVisibility(View.VISIBLE);
						curPosition = 1;
						break;
					default:
						break;
					}
					transaction.commit();
				}
			});
		}
	}
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (data != null) {
			if (requestCode == RecruitPageFragment.REQUEST_CODE) {
				SharedPreferences sp = this.getActivity().getSharedPreferences("tuding_uid", Context.MODE_PRIVATE);
				sp.edit().putBoolean(GlobalConstants.INTENTION_COMPLETE, true).commit();
				
				IntentionCompleteEvent event = new IntentionCompleteEvent(data.getBooleanExtra("intention_complete", false));
				EventBus.getDefault().post(event);
			}
		}
		
	}
	

}
