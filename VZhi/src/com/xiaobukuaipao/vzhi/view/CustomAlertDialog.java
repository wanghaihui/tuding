package com.xiaobukuaipao.vzhi.view;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;

public class CustomAlertDialog {
	Context context;
	android.app.AlertDialog ad;
	TextView titleView;
	TextView messageView;
	LinearLayout buttonLayout;
	private TextView mLeftButton, mRightButton;
	
	public CustomAlertDialog(Context context) {
		this.context = context;
		ad = new android.app.AlertDialog.Builder(context).create();
		ad.show();
		// 关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
		Window window = ad.getWindow();
		window.setContentView(R.layout.dialog_alert);
		
		titleView = (TextView) window.findViewById(R.id.title);
        messageView = (TextView) window.findViewById(R.id.message);
        buttonLayout = (LinearLayout) window.findViewById(R.id.buttonLayout);
        mLeftButton = (TextView) window.findViewById(R.id.left_bt);
        mRightButton = (TextView) window.findViewById(R.id.right_bt);
	}
	
	public void setTitle(int resId) {
        titleView.setText(resId);
    }

    public void setTitle(String title) {
        titleView.setVisibility(View.VISIBLE);
        titleView.setText(title);
    }

    public void setMessage(int resId) {
        messageView.setText(resId);
    }

    public void setMessage(String message) {
        messageView.setText(message);
    }

    /**
     * 设置按钮
     *
     * @param text
     * @param listener
     */
    public void setPositiveButton(String text,
                                  final View.OnClickListener listener) {
        mLeftButton.setText(text);
        mLeftButton.setOnClickListener(listener);
    }

    /**
     * 设置按钮
     *
     * @param text
     * @param listener
     */
    public void setNegativeButton(String text,
                                  final View.OnClickListener listener) {
        mRightButton.setText(text);
        mRightButton.setOnClickListener(listener);
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        ad.dismiss();
    }
}
