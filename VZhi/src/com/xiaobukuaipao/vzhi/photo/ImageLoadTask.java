package com.xiaobukuaipao.vzhi.photo;

import java.io.File;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

/**
 * 使用contentProvider扫描图片异步任务
 * 
 */

// 图片加载任务
public class ImageLoadTask extends BaseTask {
	
	public static final String TAG = ImageLoadTask.class.getSimpleName();
	
    /**
     * 上下文对象
     */
    private Context mContext = null;

    /**
     * 存放图片<文件夹,该文件夹下的图片列表>键值对
     */
    private ArrayList<ImageGroup> mGruopList = new ArrayList<ImageGroup>();

    public ImageLoadTask(Context context) {
        super();
        mContext = context;
        result = mGruopList;
    }

    public ImageLoadTask(Context context, OnTaskResultListener listener) {
        super();
        mContext = context;
        result = mGruopList;
        setOnResultListener(listener);
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Boolean doInBackground(Void... params) {
    	// 外部内容URI
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        
        ContentResolver mContentResolver = mContext.getContentResolver();
        
        // 构建查询条件，且只查询jpeg和png的图片
        StringBuilder selection = new StringBuilder();
        selection.append(Media.MIME_TYPE).append("=?");
        selection.append(" or ");
        selection.append(Media.MIME_TYPE).append("=?");

        Cursor mCursor = null;
        try {
            // 初始化游标
            mCursor = mContentResolver.query(mImageUri, null, selection.toString(), new String[] {
                    "image/jpeg", "image/png"
            }, Media.DATE_ADDED);
            
            // 遍历结果
            while (mCursor.moveToNext()) {
                // 获取图片的路径
                String path = mCursor.getString(mCursor.getColumnIndex(Media.DATA));

                // 获取该图片的所在文件夹的路径
                File file = new File(path);
                
                String parentName = "";
                if (file.getParentFile() != null) {
                    parentName = file.getParentFile().getName();
                } else {
                    parentName = file.getName();
                }
                
                // 构建一个imageGroup对象
                ImageGroup item = new ImageGroup();
                // 设置imageGroup的文件夹名称
                item.setDirName(parentName);
                
                int searchIdx = mGruopList.indexOf(item);
                if (searchIdx >= 0) {
                    // 如果是，该组的图片数量+1
                    ImageGroup imageGroup = mGruopList.get(searchIdx);
                    imageGroup.addImage(path);
                } else {
                    // 否则，将该对象加入到groupList中
                    item.addImage(path);
                    mGruopList.add(item);
                }
                
            }
            
        } catch (Exception e) {
            // 输出日志
            Log.i(TAG, e.toString());
            return false;
        } finally {
            // 关闭游标
            if (mCursor != null && !mCursor.isClosed()) {
                mCursor.close();
            }
        }
        
        return true;
    }
    
}
