package com.xiaobukuaipao.vzhi.fragment;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ImWrapFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// 
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// 创建一个Loader
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// 以前创建的Loader完成加载
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// 重置
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	}
	
	
}
