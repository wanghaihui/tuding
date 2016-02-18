/**
 * 
 */

package com.umeng.socialize;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/**
 * 
 */
public class CustomShareBoard extends PopupWindow implements OnClickListener{

	private static UMSocialService mController = UMServiceFactory.getUMSocialService("com.xiaobukuaipao.vzhi");

	private Activity mActivity;

	private View sinaWeibo;
    public CustomShareBoard(Activity activity) {
        super(activity);
        mActivity = activity;
        initView(activity);
    	initSocialSDK();
    }

   
	@SuppressWarnings("deprecation")
	private void initView(Context context) {
        View rootView = View.inflate(context ,R.layout.custom_board, null);
        setContentView(rootView);
        rootView.findViewById(R.id.wechat).setOnClickListener(this);
        rootView.findViewById(R.id.wechat_circle).setOnClickListener(this);
        
        sinaWeibo = rootView.findViewById(R.id.weibo_layout);
        rootView.findViewById(R.id.weibo).setOnClickListener(this);
        
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        
    }
	 /**
		 * 初始化SDK，添加一些平台
		 */
		private void initSocialSDK() {
//			UMImage localImage = new UMImage(mActivity, BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.umeng_socialize_wechat));
			
			String appID = "wxb8ed8b15ed87de74";
			String secret = "127ade5d7be578c772e117deec88c1f9";
			// 添加微信平台
			UMWXHandler wxHandler = new UMWXHandler(mActivity,appID,secret);
			wxHandler.addToSocialSDK();
			
			// 支持微信朋友圈
			UMWXHandler wxCircleHandler = new UMWXHandler(mActivity,appID,secret);
			wxCircleHandler.setToCircle(true);
			wxCircleHandler.addToSocialSDK();
			
			SinaSsoHandler sinaSsoHandler = new SinaSsoHandler();
			sinaSsoHandler.addToSocialSDK();
	        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA);
	        mController.getConfig().closeToast();
		
		}



		@Override
		public void onClick(View v) {
			
			if(v.getId() == R.id.wechat){
				performShare(SHARE_MEDIA.WEIXIN);
			}else if(v.getId() == R.id.wechat_circle){
				performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
			}else if(v.getId() == R.id.weibo){
				performShare(SHARE_MEDIA.SINA);
			}
			
		}
		private void performShare(SHARE_MEDIA platform) {
	        mController.postShare(mActivity, platform, new SnsPostListener() {

	            @Override
	            public void onStart() {

	            }

	            @Override
	            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
	                if (eCode == StatusCode.ST_CODE_SUCCESSED) {
	                	
	                    Toast.makeText(mActivity, "分享成功", Toast.LENGTH_SHORT).show();
	                    
	                    if(onCompleteListener != null){
	                    	onCompleteListener.onComplete(platform, eCode, entity);
	                    }
	                }else{
	                	Toast.makeText(mActivity, "分享失败 errCode[" + eCode + "]", Toast.LENGTH_SHORT).show();
	                }
	                
	                dismiss();
	            }
	        });
	        
	      
	    }
		
		onCompleteListener onCompleteListener = null;
		
		public onCompleteListener getOnCompleteListener() {
			return onCompleteListener;
		}

		public void setOnCompleteListener(onCompleteListener onCompleteListener) {
			this.onCompleteListener = onCompleteListener;
		}

		public static interface onCompleteListener{
			 public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity);
		}
		
		public void closeSinaWeibo(){
			sinaWeibo.setVisibility(View.GONE);
		}
}
