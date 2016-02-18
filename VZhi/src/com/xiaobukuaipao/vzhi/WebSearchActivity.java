package com.xiaobukuaipao.vzhi;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.event.PositionEventLogic;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.web.TiesShares;

/**
 * 扫码登录验证的策略
 * 使用web核心访问页面　
 * 通过页面按钮点击的js交互
 * 
 * @author hongxin.bai
 *
 */
public class WebSearchActivity extends BaseFragmentActivity {

	private String url;
	private WebView mWebView;
	private TextView mTitle;
	private ProgressBar mProgress;
	
	private PositionEventLogic positionEventLogic;
	
	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		url = getIntent().getStringExtra(GlobalConstants.INNER_URL);
		setContentView(R.layout.activity_web_search);
		setHeaderMenuByCenterTitle(url);
		setHeaderMenuByLeft(this);
		
		mTitle = (TextView) findViewById(R.id.ivTitleName);
		mProgress = (ProgressBar) findViewById(R.id.web_progress);
		mProgress.setMax(100);
		
		
		
		mWebView = (WebView) findViewById(R.id.web);
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.getSettings().setJavaScriptEnabled(true);
		// 设置可以访问文件
		mWebView.getSettings().setAllowFileAccess(true);
		// 设置支持缩放
//		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.setWebChromeClient(new WebChromeClient(){
			public void onProgressChanged(WebView view, int progress) {
				// Activities and WebViews measure progress with different
				// scales.
				// The progress meter will automatically disappear when we reach
				// 100%
				
				mTitle.setEllipsize(TruncateAt.END);
				mTitle.setText(view.getTitle());
				
				mProgress.setProgress(progress);
				mProgress.setVisibility(mProgress.getProgress() == mProgress.getMax() ? View.INVISIBLE: View.VISIBLE);
			}
		});
		
		// 设置WebViewClient
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mTitle.setText(url);
//				mWebView.loadUrl(url);
				
				
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
		});
		
		mWebView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(WebSearchActivity.this, description,Toast.LENGTH_SHORT).show();
			}
			
		});
		// 提高渲染级别
		mWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
//		// 最后加载图片
//		mWebView.getSettings().setBlockNetworkImage(true);
		// 不使用缓存
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebView.getSettings().setDefaultTextEncodingName("utf-8");  
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + " jobooking/" + info.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
//		Logcat.d("@@@", "mWebView.getSettings().getUserAgentString():" + mWebView.getSettings().getUserAgentString());
		mWebView.addJavascriptInterface(new TiesShares(WebSearchActivity.this), "tuding");
//		url = "file:///android_asset/test.html";
		mWebView.loadUrl(url);
		
		positionEventLogic = new PositionEventLogic();
		positionEventLogic.register(this);
	}
	
	public void onEventMainThread(Message msg) {
		
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				case R.id.award_shared_count:
					Logcat.d("@@@", "infoResult:" + infoResult.getResult());
//					VToast.show(this, infoResult.getMessage().getMsg());
					break;
			}
		}
		
		
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		positionEventLogic.unregister(this);
	}
	
	// clear the cache before time numDays
	public int clearCacheFolder(File dir, long numDays) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, numDays);
					}

					if (child.lastModified() < numDays) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}

	public PositionEventLogic getPositionEventLogic() {
		return positionEventLogic;
	}

	
}
