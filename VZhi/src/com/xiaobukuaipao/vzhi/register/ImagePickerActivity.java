package com.xiaobukuaipao.vzhi.register;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.BaseFragmentActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.photo.ImageGroup;
import com.xiaobukuaipao.vzhi.photo.ImageListAdapter;
import com.xiaobukuaipao.vzhi.photo.ImageLoadTask;
import com.xiaobukuaipao.vzhi.photo.LocalImageLoader;
import com.xiaobukuaipao.vzhi.photo.OnTaskResultListener;
import com.xiaobukuaipao.vzhi.photo.SDcardUtil;
import com.xiaobukuaipao.vzhi.photo.TaskUtil;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

// 图片选择器--多图片加载
// ContentProvider的标准接口实现多线程机制来连接数据,一般是用loadermanager异步加载数据
public class ImagePickerActivity extends BaseFragmentActivity {
	
	public static final String TAG = ImagePickerActivity.class.getSimpleName();

//	private int mScreenHeight;
	private GridView mPhotoGridView;

	// 全部的数量
	int totalCount = 0;
	
	// 照相机
	public static final String CAMERA_DEFAULT = "camera_default";

	/**
     * 图片扫描一般任务
     */
    private ImageLoadTask mLoadTask = null;
    
    
    /**
     * 保存所有的图片URL列表
     */
    private ArrayList<String> mTotalImageList;
    
    /**
     * 适配器
     */
    private ImageListAdapter mImageAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_picker);
		
		// 获得屏幕高度
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);

		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.title_photo_picker);
		
		findViews();
		// 加载图片
		loadImages();
		// 设置监听器
		setUIListeners();
	}

	private void findViews() {
		mPhotoGridView = (GridView) findViewById(R.id.photo_picker_gridView);
	}
	
	private void loadImages() {
		if (!SDcardUtil.hasExternalStorage()) {
            return;
        }

        // 线程正在执行
        if (mLoadTask != null && mLoadTask.getStatus() == Status.RUNNING) {
            return;
        }

        mLoadTask = new ImageLoadTask(this, new OnTaskResultListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onResult(boolean success, String error, Object result) {
                // 如果加载成功
                if (success && result != null && result instanceof ArrayList) {
                	
                    setImageAdapter((ArrayList<ImageGroup>)result);
                } else {
                    // 加载失败，显示错误提示
                }
            }
        });
        
        // 执行加载任务
        TaskUtil.execute(mLoadTask);
	}
	
	
	private void setUIListeners() {
		// GridView中每一个单元格的点击事件
		mPhotoGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String path = (String) parent.getItemAtPosition(position);
				Intent intent = new Intent();
				// 添加标记事件
				intent.putExtra(GlobalConstants.CLIP_PHOTO_URL, path);
				setResult(RESULT_OK, intent);
				AppActivityManager.getInstance().finishActivity(ImagePickerActivity.this);
				// finish();
				// 清空内存缓存
				LocalImageLoader.getInstance().clearMemoryCache();
				LocalImageLoader.getInstance().clearImagesList();
				LocalImageLoader.getInstance().clearOnLoadingList();
				
			}
		});
	}
	
	/**
	    * 构建GridView的适配器
	    * 
	    * @param data
	    */
	   private void setImageAdapter(ArrayList<ImageGroup> data) {
		   if (mTotalImageList == null) {
			   mTotalImageList = new ArrayList<String>();
		   } else {
			   // 清空数据
			   mTotalImageList.clear();
		   }
		   // 遍历所有图片，将所有图片url保存在一个ArrayList中
		   for(int i=0; i < data.size(); i++) {
			   for(int j=0; j < data.get(i).getImages().size(); j++) {
				   mTotalImageList.add(data.get(i).getImages().get(j));
			   }
		   }
		   
		   // 此时应该保存了手机中的所有图片信息
		   mImageAdapter = new ImageListAdapter(this, mTotalImageList, mPhotoGridView);
		   mPhotoGridView.setAdapter(mImageAdapter);
	   }
}
