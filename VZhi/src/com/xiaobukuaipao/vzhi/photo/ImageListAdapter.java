package com.xiaobukuaipao.vzhi.photo;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.core.ImageLoader;
import com.xiaobukuaipao.vzhi.core.ImageLoader.Type;

/**
 * 某个图片组中图片列表适配器
 * 
 */
public class ImageListAdapter extends BaseAdapter {
	
	
	public static final String TAG = ImageListAdapter.class.getSimpleName();
    /**
     * 上下文对象
     */
    private Context mContext = null;

    /**
     * 图片列表
     */
    private ArrayList<String> mDataList = new ArrayList<String>();

    /**
     * 容器
     */
    private View mContainer = null;

    public ImageListAdapter(Context context, ArrayList<String> list, View container) {
        mDataList = list;
        mContext = context;
        mContainer = container;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public String getItem(int position) {
        if (position < 0 || position > mDataList.size()) {
            return null;
        }
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.image_list_item, null);
            holder.mImageIv = (SquaredImageView) view.findViewById(R.id.list_item_iv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final String path = getItem(position);
        Log.i(TAG, "photo path : " + path);
        /*holder.mImageIv.setTag(path);
        Bitmap bitmap = LocalImageLoader.getInstance().loadImage(path, holder.mImageIv.getPoint(),
                new ImageCallBack() {
                    @Override
                    public void onImageLoader(Bitmap bitmap, String path) {
                        ImageView mImageView = (ImageView) mContainer.findViewWithTag(path);
                        if (bitmap != null && mImageView != null) {
                            mImageView.setImageBitmap(bitmap);
                        }
                        
                    }
                });
        if (bitmap != null) {
            holder.mImageIv.setImageBitmap(bitmap);
        } else {
            holder.mImageIv.setImageResource(R.drawable.pic_thumb);
        }*/
        
        Picasso.with(mContext)
	        .load(new File(path))
	        .resize(240, 240)
	        .centerCrop()
	        .placeholder(R.drawable.photo_picker_default)
	        .into(holder.mImageIv);
        ImageLoader.getInstance(3, Type.LIFO).loadImage(path, holder.mImageIv);
        
        return view;
    }

    static class ViewHolder {
        public SquaredImageView mImageIv;
    }

}
