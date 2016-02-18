package com.xiaobukuaipao.vzhi.web;

import android.app.Activity;
import android.net.Uri;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.PopupWindow.OnDismissListener;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.CustomShareBoard;
import com.umeng.socialize.CustomShareBoard.onCompleteListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.WebSearchActivity;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;

/**
 *	活动分享
 *
 */
public final class TiesShares extends JsObject{
	/**
	 * 获取系统中分享服务实例
	 */
	private UMSocialService mController = UMServiceFactory.getUMSocialService("com.xiaobukuaipao.vzhi");
	
	public TiesShares(Activity activity) {
		super(activity);
	}
	/**
	 * 分享活动
	 * 
	 * @param json
	 */
	@JavascriptInterface
	public void share(final String json){
		Logcat.d("@@@", "json:" + json);
		//{"sharedTitle":"产品总监-你在哪里，HR在线等着你","sharedContent":"8K~25K 1~3年 北京 小步快跑（北京）科技发展有限公司","sharedItemUrl":"http://www.jobooking.cn/award/jd/154"}
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				final ShareContent shareContent = new ShareContent();
				//得到分享的内容
				//根据分享的内容显示对话框
				if(StringUtils.isNotEmpty(json)){
					try{
						JSONObject jsonObj = (JSONObject) JSONObject.parse(json);
						if(jsonObj != null){
							String sharedTitle = jsonObj.getString("sharedTitle");
							if(sharedTitle != null){
								shareContent.sharedTitle = sharedTitle;
							}
							
							String sharedContent = jsonObj.getString("sharedContent");
							if(sharedContent != null){
								shareContent.sharedContent = sharedContent;
							}
							
							String sharedImageUrl = jsonObj.getString("sharedImageUrl");
							if(sharedImageUrl != null){
								shareContent.sharedImageUrl = sharedImageUrl;
							}
							
							String sharedItemUrl = jsonObj.getString("sharedItemUrl");
							if(sharedItemUrl != null){
								shareContent.sharedItemUrl = sharedItemUrl + "?shareuid="+ Uri.encode(GeneralDbManager.getInstance().getUidFromCookie());
							}
							
							String jid = jsonObj.getString("jid");
							if(jid != null){
								shareContent.jid = jid;
							}
							
						}
					} catch(JSONException exception){
						VToast.show(activity, exception.getMessage());
					}
					
				}
				CustomShareBoard customShareBoard = new CustomShareBoard(activity);
				customShareBoard.setOnCompleteListener(new onCompleteListener() {
					
					@Override
					public void onComplete(SHARE_MEDIA platform, int eCode,
							SocializeEntity entity) {
						Logcat.d("@@@", "分享回调");
						if(activity instanceof WebSearchActivity){
							WebSearchActivity webSearchActivity = (WebSearchActivity) activity;
							webSearchActivity.getPositionEventLogic().sendAwardCount(shareContent.jid);
						} 
					}
				});
				
				MobclickAgent.onEvent(activity, "fxzw");
				// 使用友盟分享职位信息

				
//				localImage = new UMImage(activity, "http://www.baidu.com/img/bd_logo1.png");
				String url = shareContent.sharedItemUrl;
				String title = shareContent.sharedTitle;
				String content = shareContent.sharedContent;
				
				// 微信分享
				WeiXinShareContent weixinContent = new WeiXinShareContent();
				weixinContent.setTitle(title);
				weixinContent.setShareContent(content);
				weixinContent.setTargetUrl(url);

				// 微信朋友圈分享
				CircleShareContent circleMedia = new CircleShareContent();
				circleMedia.setShareContent(content);
				circleMedia.setTitle(title);
				circleMedia.setTargetUrl(url);
				
//				// 新浪分享
//				SinaShareContent sinaShareContent = new SinaShareContent();
//				sinaShareContent.setShareContent(content);
//				sinaShareContent.setTitle(title);
//				sinaShareContent.setTargetUrl(url);
//				sinaShareContent.toUrlExtraParams();
//				sinaShareContent.setAppWebSite(url);
				
				UMImage localImage = null;
				if(StringUtils.isNotEmpty(shareContent.sharedImageUrl)){
					localImage = new UMImage(activity, shareContent.sharedImageUrl);
				}else{
					localImage = new UMImage(activity, R.drawable.ic_launcher);
				}
				
				weixinContent.setShareImage(localImage);
				circleMedia.setShareImage(localImage);
//				sinaShareContent.setShareImage(localImage);
				
//				Logcat.d("@@@", shareContent.toString());
				mController.setShareMedia(weixinContent);
				mController.setShareMedia(circleMedia);
//				mController.setShareMedia(sinaShareContent);
				customShareBoard.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss() {
						WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
						lp.alpha = 1.0f;
						activity.getWindow().setAttributes(lp);
					}
				});
				customShareBoard.closeSinaWeibo();
				customShareBoard.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
				WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
				lp.alpha =0.65f;
				activity.getWindow().setAttributes(lp);
			}
		});
	}
	
	class ShareContent{
		String jid = "";
		
		String sharedTitle = "";
		
		String sharedContent = "";
		
		String sharedImageUrl = "";
		
		String sharedItemUrl = "";

	}
	
}
