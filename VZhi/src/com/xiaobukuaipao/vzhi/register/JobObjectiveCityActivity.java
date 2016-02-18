package com.xiaobukuaipao.vzhi.register;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.xiaobukuaipao.vzhi.R;


public class JobObjectiveCityActivity extends RegisterResidentSearchActivity {
	
	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHeaderMenuByCenterTitle(R.string.job_objective_intention_city);
		
	}
}
