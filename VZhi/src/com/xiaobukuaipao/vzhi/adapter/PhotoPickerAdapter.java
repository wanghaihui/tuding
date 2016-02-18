package com.xiaobukuaipao.vzhi.adapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.core.ImageManager;

public class PhotoPickerAdapter  extends CommonAdapter<String> {

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public static List<String> mSelectedImage = new LinkedList<String>();

	/**
	 * 文件夹路径
	 */
	private String mDirPath;
	
	private Handler mHandler;

	public PhotoPickerAdapter(Context context, List<String> mDatas, int itemLayoutId,
			String dirPath, Handler mHandler)
	{
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
		this.mHandler = mHandler;
	}
	
	
	@Override
	public void convert(final ViewHolder helper, final String item, final int position)
	{

		/*String path;
		if (mDatas != null && mDatas.size() > position) {
			path = mDatas.get(position);
		} else {
			path = "camera_default";
		}
		*/
		final ImageView imageView  = helper.getView(R.id.id_item_image);
		if (mDirPath != null) {
			ImageManager.from(mContext).displayImage(imageView, mDirPath + "/" + item, R.drawable.camera_small_grey, 100, 100);
		}
		
		//设置no_pic
		/*helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
		//设置图片
		if (mDirPath != null) {
			helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);
		}
		
		final ImageView mImageView = helper.getView(R.id.id_item_image);*/
		
		imageView.setColorFilter(null);
		//设置ImageView的点击事件
		imageView.setOnClickListener(new OnClickListener()
		{
			//选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v)
			{
				if (position == 0) {
					Message msg = mHandler.obtainMessage();
					msg.what = 1;
					mHandler.sendMessage(msg);
				} else {
					if (mDirPath != null) {
						Message msg = mHandler.obtainMessage();
						msg.what = 2;
						msg.obj = mDirPath + "/" + item;
						mHandler.sendMessage(msg);
					}
				}
			}
		});
		
		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		
		/*if (mSelectedImage.contains(mDirPath + "/" + item))
		{
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}*/

	}

}
