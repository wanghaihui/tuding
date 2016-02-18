package com.xiaobukuaipao.vzhi.fragment;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.user.Experience;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineView;

public class JobDescripFragment extends CallBackFragment {

	private FormItemByLineView jobNameView;
	private FormItemByLineView jobCompanyView;
	private FormItemByLineView jobTimeView;
	private TextView jobDesTextView;
	
    private Experience mExperience;
    private Bundle mBundle;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mBundle = getArguments();
    	mExperience = (Experience) mBundle.getParcelable(GlobalConstants.GUIDE_JOB_EXPERIENCE_OBJECT);
    	view =  inflater.inflate(R.layout.fragment_job_desc, container, false);
    	return view;
	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHeaderMenuByLeft();
		setHeaderMenuByCenterTitle(R.string.register_jobexp_desc);
		setHeaderMenuByRight(R.string.general_tips_save).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mExperience.desc = jobDesTextView.getText().toString();
						onBackRequest.onBackData(mBundle);
					}
				});

		jobCompanyView = (FormItemByLineView) view.findViewById(R.id.register_job_exp_company_id);
		jobNameView = (FormItemByLineView) view.findViewById(R.id.register_job_exp_name_id);
		jobTimeView = (FormItemByLineView) view.findViewById(R.id.register_job_exp_time_id);
		//限制150个字
		jobDesTextView = (EditText) getActivity().findViewById(R.id.job_experience_text_id);
		if(mExperience !=null){
			jobCompanyView.getFormContent().setText(StringUtils.isEmpty(mExperience.getCorp())?"":mExperience.getCorp());
			jobNameView.getFormContent().setText(StringUtils.isEmpty(mExperience.getPosition())?"":mExperience.getPosition());
			jobTimeView.getFormContent().setText((StringUtils.isEmpty(mExperience.getBeginStr()) ? "" : mExperience.getBeginStr()) + (StringUtils.isEmpty(mExperience.getEndStr()) ? "~" + "至今" : ("~" + mExperience.getEndStr())));
			jobDesTextView.setText(StringUtils.isEmpty(mExperience.desc) ? "" : mExperience.desc);
		}
		
	}



	@Override
	public void onEventMainThread(Message msg) {
		
	}


}
