package com.xiaobukuaipao.vzhi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;

public class UserProfileTipView extends LinearLayout {

    public UserProfileTipView(Context context) {
        this(context, null);
    }
    public UserProfileTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UserProfileTipView);
        String userTip1 = typedArray.getString(R.styleable.UserProfileTipView_userTip1);
        String userTip2  = typedArray.getString(R.styleable.UserProfileTipView_userTip2);
        typedArray.recycle();
        
        View view = LayoutInflater.from(context).inflate(R.layout.section_flag, this, true);

        TextView tip1Title = (TextView)view.findViewById(R.id.section_flag_txt);
        TextView tip2Title = (TextView)view.findViewById(R.id.section_flag_tips_txt);

        tip1Title.setText(userTip1);
        tip2Title.setText(userTip2);
        
    }
}
