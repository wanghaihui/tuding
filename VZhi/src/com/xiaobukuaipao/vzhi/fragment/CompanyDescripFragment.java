package com.xiaobukuaipao.vzhi.fragment;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;

public class CompanyDescripFragment extends CallBackFragment {
	private TextView jobDesTextView;
	
    private Bundle mBundle;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mBundle = getArguments();
    	view =  inflater.inflate(R.layout.fragment_company_desc, container, false);
    	return view;
	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHeaderMenuByLeft();
		setHeaderMenuByCenterTitle(R.string.corp_edit_desc);
		setHeaderMenuByRight(R.string.general_tips_save).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						String desc = jobDesTextView.getText().toString();
						if(StringUtils.isNotEmpty(desc)){
							mBundle.putString(GlobalConstants.FILL, desc);
						}
						onBackRequest.onBackData(mBundle);
					}
				});
		jobDesTextView = (TextView) view.findViewById(R.id.compant_desc_text_id);
		String desc = mBundle.getString(GlobalConstants.FILL);
		if(StringUtils.isNotEmpty(desc)){
			jobDesTextView.setText(desc);
		}
		
	}



	@Override
	public void onEventMainThread(Message msg) {
		
	}


}
