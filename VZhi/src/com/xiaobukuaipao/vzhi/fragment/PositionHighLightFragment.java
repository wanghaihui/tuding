package com.xiaobukuaipao.vzhi.fragment;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;

public class PositionHighLightFragment extends CallBackFragment {

	
    private Bundle mBundle;
    private View view;
	private EditText mHightLight;
	private String mHightStr;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mBundle = getArguments();
    	mHightStr = mBundle.getString(GlobalConstants.FILL);
    	view =  inflater.inflate(R.layout.fragment_position_highlight_edit, container, false);
    	return view;
	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHeaderMenuByLeft();
	    setHeaderMenuByCenterTitle(R.string.publish_position_highlight);
		setHeaderMenuByRight(R.string.general_tips_save).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(StringUtils.isNotEmpty(mHightLight.getText().toString())){
							mBundle.putString(GlobalConstants.FILL, mHightLight.getText().toString());
						}
						onBackRequest.onBackData(mBundle);
					}
				});
		
		mHightLight = (EditText) view.findViewById(R.id.position_highlight);
		if(StringUtils.isNotEmpty(mHightStr)){
			mHightLight.setText(mHightStr);
		}
	}

	@Override
	public void onEventMainThread(Message msg) {
		
	}
}
