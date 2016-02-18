package com.xiaobukuaipao.vzhi.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class CommonBitmapMemoryCache implements ImageCache {

	private LruCache<String, Bitmap> mLruCache;
	
	// 构造函数
	public CommonBitmapMemoryCache() {
		// 10M图片缓存--默认4M
		int maxSize = 4 * 1024 * 1024;
		mLruCache = new LruCache<String, Bitmap>(maxSize) {
			@Override  
            protected int sizeOf(String key, Bitmap bitmap) {  
                return bitmap.getRowBytes() * bitmap.getHeight();  
            }  
		};
	}
		
	@Override
	public Bitmap getBitmap(String url) {
		// TODO Auto-generated method stub
		return mLruCache.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		// TODO Auto-generated method stub
		mLruCache.put(url, bitmap);
	}

}