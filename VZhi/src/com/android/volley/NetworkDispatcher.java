/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley;

import java.util.concurrent.BlockingQueue;

import android.annotation.TargetApi;
import android.app.Application;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.vzhi.VZhiApplication;
import com.xiaobukuaipao.vzhi.util.Logcat;

/**
 * Provides a thread for performing network dispatch from a queue of requests.
 *
 * Requests added to the specified queue are processed from the network via a
 * specified {@link Network} interface. Responses are committed to cache, if
 * eligible, using a specified {@link Cache} interface. Valid responses and
 * errors are posted back to the caller via a {@link ResponseDelivery}.
 */
public class NetworkDispatcher extends Thread {
	private static final String TAG = NetworkDispatcher.class.getSimpleName();
    /** The queue of requests to service. */
    private final BlockingQueue<Request<?>> mQueue;
    /** The network interface for processing requests. */
    private final Network mNetwork;
    /** The cache to write to. */
    private final Cache mCache;
    /** For posting responses and errors. */
    private final ResponseDelivery mDelivery;
    /** Used for telling us to die. */
    private volatile boolean mQuit = false;

    /**
     * Creates a new network dispatcher thread.  You must call {@link #start()}
     * in order to begin processing.
     *
     * @param queue Queue of incoming requests for triage
     * @param network Network interface to use for performing requests
     * @param cache Cache interface to use for writing responses to cache
     * @param delivery Delivery interface to use for posting responses
     */
    public NetworkDispatcher(BlockingQueue<Request<?>> queue,
            Network network, Cache cache,
            ResponseDelivery delivery) {
        mQueue = queue;
        mNetwork = network;
        mCache = cache;
        mDelivery = delivery;
    }

    /**
     * Forces this dispatcher to quit immediately.  If any requests are still in
     * the queue, they are not guaranteed to be processed.
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void addTrafficStatsTag(Request<?> request) {
        // Tag the request (if API >= 14)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        	// 流量统计
            TrafficStats.setThreadStatsTag(request.getTrafficStatsTag());
        }
    }

    long httpCount = 0;
    
    long httpRepeatCount = 0;
    
    Request<?> request;
    
    @Override
    public void run() {
    	Logcat.d(TAG, "NetworkDispatcher: start new dispatcher : " + Thread.currentThread().getName() + ":" + Thread.currentThread().getId());
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        
        while (true) {
            long startTimeMs = SystemClock.elapsedRealtime();
            
            Request<?> request;
            try {
                // Take a request from the queue.
                request = mQueue.take();
                
                Logcat.d(TAG, "NetworkDispatcher: request : " + Thread.currentThread().getName() + ":" + Thread.currentThread().getId() +" : "+ request.getCacheKey());
                
                if (request.equals(this.request)) {
                	httpRepeatCount ++;
                	String test = "httpRequest:" + request + "   tag:" + request.getTag() + "  httpRepeatCount:" + httpRepeatCount +"  thread:" + Thread.currentThread().getName();
                	Logcat.e("@@@", test);
                	MobclickAgent.reportError(VZhiApplication.getInstance(), test);
                }
                
                this.request = request;
                
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
                continue;
            }

            try {
                request.addMarker("network-queue-take");

                // If the request was cancelled already, do not perform the
                // network request.
                if (request.isCanceled()) {
                    request.finish("network-discard-cancelled");
                    continue;
                }

                addTrafficStatsTag(request);
                
                // Perform the network request.
                Log.i(TAG, "perform request");
                NetworkResponse networkResponse = mNetwork.performRequest(request);
                Log.i(TAG, "network complete");
                request.addMarker("network-http-complete");
                
                // If the server returned 304 AND we delivered a response already,
                // we're done -- don't deliver a second identical response.
                // 用来验证新鲜度的请求
                if (networkResponse.notModified && request.hasHadResponseDelivered()) {
                    request.finish("not-modified");
                    continue;
                }

                // Parse the response here on the worker thread.
                Log.i(TAG, "parse network response");
                Response<?> response = request.parseNetworkResponse(networkResponse);
                Log.i(TAG, "network parse complete");
                request.addMarker("network-parse-complete");

                // Write to cache if applicable.
                // TODO: Only update cache metadata instead of entire record for 304s.
                if (request.shouldCache() && response.cacheEntry != null) {
                    mCache.put(request.getCacheKey(), response.cacheEntry);
                    request.addMarker("network-cache-written");
                }

                // Post the response back.
                request.markDelivered();
                
                Log.i(TAG, "post response");
                mDelivery.postResponse(request, response);
                
            } catch (VolleyError volleyError) {
                volleyError.setNetworkTimeMs(SystemClock.elapsedRealtime() - startTimeMs);
                parseAndDeliverNetworkError(request, volleyError);
            } catch (Exception e) {
                VolleyLog.e(e, "Unhandled exception %s", e.toString());
                VolleyError volleyError = new VolleyError(e);
                volleyError.setNetworkTimeMs(SystemClock.elapsedRealtime() - startTimeMs);
                mDelivery.postError(request, volleyError);
            }
        }
    }

    private void parseAndDeliverNetworkError(Request<?> request, VolleyError error) {
        error = request.parseNetworkError(error);
        mDelivery.postError(request, error);
    }
}
