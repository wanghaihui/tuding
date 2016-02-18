package com.xiaobukuaipao.vzhi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.view.CustomAlertDialog;

/**
 * 
 * 
 */
public class BaseChatFragmentActivity extends FragmentActivity {
	private static final String TAG = BaseChatFragmentActivity.class.getSimpleName();
	
	protected static final String STATE_RELOGIN = "relogin";
	
	/**
	 * ActionBar 左侧菜单显示
	 */
	public void setHeaderMenuByLeft(final Activity activity) {
		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.menu_bar_back_btn_layout);
		frameLayout.setVisibility(View.VISIBLE);
		// bug
		frameLayout.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					backActivity();
					activity.onBackPressed();
				}
			});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		AppActivityManager.getInstance().addActivity(this);
	}

	public void backActivity() {

	}

	/**
	 * 按钮btnId触发的事情全部走这个流程
	 * 
	 * @param btnId
	 */
	public void onClickListenerBySaveDataAndRedirectActivity(int btnId) {
		findViewById(btnId).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				confirmNextAction();
				chooseRedirectActivity(view);
			}
		});
	}

	/**
	 * 在本页面即Activity中选择哪个页面进行跳转， 如果子类不覆盖此方法，就表示该页面不需要跳转， 或者不对跳转页面进行判断
	 * 
	 * @param view
	 */
	public void chooseRedirectActivity(View view) {

	}

	/**
	 * <p>
	 * Activity下一步执行的动作(保存数据，下一个页面的跳转), 由子类覆盖实现具体动作
	 * </p>
	 */
	public void confirmNextAction() {

	}

	/**
	 * ActionBar中间的Title
	 * 
	 * @param title
	 */
	public void setHeaderMenuByCenterTitle(int title) {
		TextView textTitleView = (TextView) findViewById(R.id.ivTitleName);
		textTitleView.setText(getResources().getString(title));
	}

	/**
	 * ActionBar中间的Title
	 * 
	 * @param title
	 */
	public void setHeaderMenuByCenterTitle(String title) {
		TextView textTitleView = (TextView) findViewById(R.id.ivTitleName);
		textTitleView.setText(title);
	}

	public View setHeaderMenuByRight(int title) {
		return setHeaderMenuByRight(title, -1, -1);
	}

	public View setHeaderMenuByRight2(int title) {
		return setHeaderMenuByRight2(title, -1, -1);
	}
	
	public <T extends View> View setHeaderMenuByRight(int title, int textSize, int textCorlor) {
		
		View v =  findViewById(R.id.menu_bar_right);
		if(v instanceof TextView){
			TextView menuBarRight = (TextView)v;
			menuBarRight.setVisibility(View.VISIBLE);
			// 大于表示要更改菜单栏右边按钮标题，否则保持按钮默认的标题
			if (title > 0) {
				menuBarRight.setText(title);
				if (textSize != -1)
					menuBarRight.setTextSize(textSize);
				if (textCorlor != -1)
					menuBarRight.setTextColor(textCorlor);
			}
			menuBarRight.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					confirmNextAction();
					chooseRedirectActivity(view);
				}
			});
			return (View) menuBarRight;
		}
		return v;
	}

	public <T extends View> View setHeaderMenuByRight2(int title, int textSize, int textCorlor) {
		
		View v =  findViewById(R.id.menu_bar_right2);
		if(v instanceof TextView){
			TextView menuBarRight = (TextView)v;
			menuBarRight.setVisibility(View.VISIBLE);
			// 大于表示要更改菜单栏右边按钮标题，否则保持按钮默认的标题
			if (title > 0) {
				menuBarRight.setText(title);
				if (textSize != -1)
					menuBarRight.setTextSize(textSize);
				if (textCorlor != -1)
					menuBarRight.setTextColor(textCorlor);
			}
			return (View) menuBarRight;
		}
		return v;
	}
	/**
	 * 显示提示对话框
	 */
	public void showTipDialog(String dialogMsg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(dialogMsg);
		builder.setTitle(R.string.delete_dialog_title);
		builder.setPositiveButton(R.string.delete_dialog_sure,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		// 创建并显示
		builder.create().show();
	}

	/**
	 * 确认消息提示窗口
	 * 
	 * @param msg
	 */
	public void showConfirmDialog(String title, String msg) {
		final CustomAlertDialog ad = new CustomAlertDialog(this);
		ad.setMessage(msg);
		ad.setTitle(StringUtils.isEmpty(title) ? getString(R.string.register_dialog_top_title)
				: title);

		ad.setPositiveButton(getResources().getString(R.string.btn_cancel),
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						ad.dismiss();
					}
				});

		ad.setNegativeButton(getResources().getString(R.string.btn_confirm),
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						ad.dismiss();
						confirmNextAction();
					}
				});
	}

	@Override
	protected void onDestroy() {
		/*if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this);
		}*/
		Log.i(TAG, "base chat fragment finish activity");
		AppActivityManager.getInstance().finishActivity(this);
		super.onDestroy();
	}

	/**
	 * 保存本地数据
	 */
	protected void saveLocalDataBy(String key, String value) {
		SharedPreferences.Editor editor = getSharedPreferences(
				getString(R.string.shareData_preferences), Context.MODE_PRIVATE)
				.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	
	
	/**
	 * 当edittext获取焦点,点击其他区域输入法隐藏
	 * 
	 * @see android.app.Activity#dispatchTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			View v = (View) getCurrentFocus().getParent();
			
			Log.i(TAG, "chat dispatch touch event");
			if (isShouldHideInput(v, event)) {
				Log.i(TAG, "is should hide input");
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			} else {
				Log.i(TAG, "is not should hide input");
			}
			return super.dispatchTouchEvent(event);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		if (getWindow().superDispatchTouchEvent(event)) {
			return true;
		}
		return super.onTouchEvent(event);
	}

	public boolean isShouldHideInput(View v, MotionEvent event) {
		boolean flag = false;
		Log.d(TAG, "v.id:" + v.getId() + "    cons:" + R.id.chat_list + "    " + R.id.chat_sender);
		if (v != null && (v instanceof EditText)) {

			int[] leftTop = { 0, 0 };
			// 获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件
				flag = false;
			} else {
				EditText e = (EditText) v;
				e.clearFocus();
				flag = true;
			}
		}else if(R.id.chat_list == v.getId()){
			flag = true;
			
		}else if(R.id.chat_sender == v.getId()){
			flag = false;
		}

		Log.i(TAG, "flag : " + flag);
		return flag;
	}
}
