package com.xiaobukuaipao.vzhi.view;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class KeyboardDetectorLinearLayout extends LinearLayout {
    private List<IKeyboardChanged> keyboardListeners;

    public KeyboardDetectorLinearLayout(Context context) {
        super(context);
    }

    public KeyboardDetectorLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int proposedheight = MeasureSpec.getSize(heightMeasureSpec);
        final int actualHeight = getHeight();

        if (actualHeight > proposedheight) {
            notifyKeyboardShown();
        } else {
            notifyKeyboardHidden();
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void notifyKeyboardHidden() {
        for (IKeyboardChanged listener : keyboardListeners) {
            listener.onKeyboardHidden();
        }
    }

    private void notifyKeyboardShown() {
        for (IKeyboardChanged listener : keyboardListeners) {
            listener.onKeyboardShown();
        }
    }

    public void setKeyboardListeners(List<IKeyboardChanged> keyboardListeners) {
        this.keyboardListeners = keyboardListeners;
    }
}
