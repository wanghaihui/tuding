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

public class PositionDescriptFragment extends CallBackFragment {
	
    private Bundle mBundle;
    private View view;
    private String mDescStr;
    private EditText mDesc;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mBundle = getArguments();
    	mDescStr = mBundle.getString(GlobalConstants.FILL);
    	view =  inflater.inflate(R.layout.fragment_position_desc_edit, container, false);
    	return view;
	}
    


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHeaderMenuByLeft();
		setHeaderMenuByCenterTitle(R.string.position_desc_str);
		setHeaderMenuByRight(R.string.general_tips_save).setOnClickListener(
				new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(StringUtils.isNotEmpty(mDesc.getText().toString())){
							mBundle.putString(GlobalConstants.FILL, mDesc.getText().toString());
						}
						onBackRequest.onBackData(mBundle);
					}
				});
		
		mDesc = (EditText) view.findViewById(R.id.position_desc);
		if(StringUtils.isNotEmpty(mDescStr)){
			mDesc.setText(mDescStr);
		}
	}



	@Override
	public void onEventMainThread(Message msg) {
		
	}


}
