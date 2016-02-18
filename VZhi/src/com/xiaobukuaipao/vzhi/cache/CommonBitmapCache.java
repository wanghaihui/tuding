package com.xiaobukuaipao.vzhi.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.xiaobukuaipao.vzhi.BuildConfig;

/**
 * 对Volley使用双重缓存机制
 * @author haihui.wang
 *
 */
public class CommonBitmapCache implements ImageCache {
	private static final String TAG = CommonBitmapCache.class.getSimpleName();
	
	private static int DISK_IMAGECACHE_SIZE = 1024*1024*10;
	private CompressFormat mCompressFormat = CompressFormat.JPEG;
	private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
	//PNG is lossless so quality is ignored but must be provided
	private int mCompressQuality = 70;
	private static int DISK_IMAGECACHE_QUALITY = 100;
	
	private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    
    private static int IO_BUFFER_SIZE = 8*1024;

	private LruCache<String, Bitmap> mLruCache;
	
	private DiskLruCache mDiskCache;
	
	// 构造函数
	public CommonBitmapCache(Context context) {
		// 10M图片缓存--默认4M
		int maxSize = 10 * 1024 * 1024;
		mLruCache = new LruCache<String, Bitmap>(maxSize) {
			@Override  
            protected int sizeOf(String key, Bitmap bitmap) {  
                return bitmap.getRowBytes() * bitmap.getHeight();  
            }  
		};
		
		
		// 初始化SD卡缓存
		try {
            final File diskCacheDir = getDiskCacheDir(context, "xiaobukuaipao" );
            mDiskCache = DiskLruCache.open( diskCacheDir, APP_VERSION, VALUE_COUNT, DISK_IMAGECACHE_SIZE );
            mCompressFormat = DISK_IMAGECACHE_COMPRESS_FORMAT;
            mCompressQuality = DISK_IMAGECACHE_QUALITY;
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// ImageCache的两个实现函数
	@Override
	public Bitmap getBitmap(String url) {
		// TODO Auto-generated method stub
		Bitmap bitmap = null;
		DiskLruCache.Snapshot snapshot = null;
		
		bitmap = mLruCache.get(url);
		
	    if (bitmap == null) {
	        try {
			    snapshot = mDiskCache.get( createKey(url) );
			    if ( snapshot == null ) {
			        return null;
			    }
			    final InputStream in = snapshot.getInputStream( 0 );
			    if ( in != null ) {
			        final BufferedInputStream buffIn = 
			        new BufferedInputStream( in, IO_BUFFER_SIZE );
			        bitmap = BitmapFactory.decodeStream( buffIn );              
			    }   
			} catch ( IOException e ) {
			    e.printStackTrace();
			} finally {
			    if ( snapshot != null ) {
			        snapshot.close();
			    }
			}

			if ( BuildConfig.DEBUG ) {
			    Log.d( "cache_test_DISK_", bitmap == null ? "" : "image read from disk " + createKey(url));
			}
			
			
			if (bitmap != null) {
			    mLruCache.put(createKey(url), bitmap);
			}
	    } 

	    return bitmap;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		// TODO Auto-generated method stub
		
		Log.i(TAG, "put Bitmap");
		mLruCache.put(url, bitmap);
		
		DiskLruCache.Editor editor = null;
        try {
            editor = mDiskCache.edit( createKey(url) );
            if ( editor == null ) {
                return;
            }

            if( writeBitmapToFile(bitmap, editor ) ) {               
                mDiskCache.flush();
                editor.commit();
                if ( BuildConfig.DEBUG ) {
                   Log.d( "cache_test_DISK_", "image put on disk cache " + createKey(url) );
                }
            } else {
                editor.abort();
                if ( BuildConfig.DEBUG ) {
                    Log.d( "cache_test_DISK_", "ERROR on: image put on disk cache " + createKey(url) );
                }
            }   
        } catch (IOException e) {
            if ( BuildConfig.DEBUG ) {
                Log.d( "cache_test_DISK_", "ERROR on: image put on disk cache " + createKey(url) );
            }
            try {
                if ( editor != null ) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }           
        }
        
        
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	
	/////////////////////////////////////////////////////////////////////////////////////
	// 硬盘缓存
	private File getDiskCacheDir(Context context, String uniqueName) {

        final String cachePath = context.getCacheDir().getPath();
		// final String cachePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return new File(cachePath + File.separator + uniqueName);
    }
	
	private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor )
	        throws IOException, FileNotFoundException {
	        OutputStream out = null;
	        try {
	            out = new BufferedOutputStream( editor.newOutputStream( 0 ), IO_BUFFER_SIZE );
	            return bitmap.compress( mCompressFormat, mCompressQuality, out );
	        } finally {
	            if ( out != null ) {
	                out.close();
	            }
	        }
	    }
	
	public boolean containsKey( String key ) {

        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get( key );
            contained = snapshot != null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( snapshot != null ) {
                snapshot.close();
            }
        }

        return contained;

    }

    public void clearCache() {
        if ( BuildConfig.DEBUG ) {
            Log.d( "cache_test_DISK_", "disk cache CLEARED");
        }
        try {
            mDiskCache.delete();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public File getCacheFolder() {
        return mDiskCache.getDirectory();
    }
	/////////////////////////////////////////////////////////////////////////////////////
    
    /**
	 * Creates a unique cache key based on a url value
	 * @param url
	 * 		url to be used in key creation
	 * @return
	 * 		cache key value
	 */
	private String createKey(String url){
		return String.valueOf(url.hashCode());
	}
}
