package com.xiaobukuaipao.vzhi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.social.Sender;
import com.xiaobukuaipao.vzhi.domain.social.StrangerCardInfo;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.TimeHandler;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.RoundedImageView;
import com.xiaobukuaipao.vzhi.wrap.SocialWrapActivity;

public class ReplyQuestionActivity extends SocialWrapActivity implements OnClickListener {
	private Context mContext;
	private RoundedImageView mAvatar;
	private TextView mName;
	private TextView mQuestion;
	private EditText mAnswer;
	private StrangerCardInfo strangerInfo;
	private  Sender sender ;
	
	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	private ImageListener mListener;
	private TextView mRequestTime;
	
	@Override
	public void initUIAndData() {
		  setContentView(R.layout.activity_reply_question);
	      setHeaderMenuByCenterTitle(R.string.reply_question_str);
	      setHeaderMenuByLeft(this);
	      mContext = this;
	      mQueue = Volley.newRequestQueue(mContext);
	      mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
	      
	      Intent intent = getIntent();
	      Bundle extras = intent.getExtras();
	      if(extras != null){
	    	  strangerInfo = extras.getParcelable(GlobalConstants.STRANGER_REPLY);
	      }
	      
	      mAvatar = (RoundedImageView) findViewById(R.id.stranger_avatar);
	      mName = (TextView) findViewById(R.id.stranger_name);
	      mQuestion = (TextView) findViewById(R.id.stranger_question);
	      mAnswer = (EditText) findViewById(R.id.answer);
	      mRequestTime = (TextView) findViewById(R.id.request_time);
	      findViewById(R.id.next_btn).setOnClickListener(this);
	      
	      if(strangerInfo != null){
	    	  sender = new Sender(strangerInfo.getSender());
	    	  mName.setText(getString(R.string.stranger_msg_s, sender.getName(),sender.getPosition(),sender.getCorp(),sender.getCity()));
	    	  mQuestion.setText(strangerInfo.getQuestion());
	    	  mListener = ImageLoader.getImageListener(mAvatar,  R.drawable.general_user_avatar, R.drawable.general_user_avatar);
	    	  mImageLoader.get(sender.getAvatar(), mListener);
	    	  mRequestTime.setText(TimeHandler.getInstance(this).time2str(strangerInfo.getTime()));
	      }
	      
	}
	

	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof InfoResult){
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.social_stranger_answer_question:
				VToast.show(mContext, infoResult.getMessage().getMsg());
				if(infoResult.getMessage().status == 0){
					Intent intent = getIntent();
					intent.putExtra("answer", mAnswer.getText().toString());
					setResult(RESULT_OK, intent);
				}else{
					setResult(RESULT_CANCELED);
				}
				AppActivityManager.getInstance().finishActivity(ReplyQuestionActivity.this);
				break;
				
			default:
				break;
			}
		}
	}

	
	@Override
	public void onClick(View v) {
		mSocialEventLogic.cancel(R.id.social_stranger_answer_question);
		mSocialEventLogic.answerQuestion(String.valueOf(strangerInfo.getQuestionid()), mAnswer.getText().toString());
	}
	
}
