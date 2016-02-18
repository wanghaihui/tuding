package com.xiaobukuaipao.vzhi.volley.multipart;
/**
 * Progress call back interface
 * @version [Android-BaseLine, 2014-9-22]
 */
public interface ProgressListener
{
    /**
     * Callback method thats called on each byte transfer.
     */
    void onProgress(String key, long transferredBytes, long totalSize);
}
