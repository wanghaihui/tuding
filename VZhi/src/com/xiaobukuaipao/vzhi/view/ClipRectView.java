package com.xiaobukuaipao.vzhi.view;

import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ClipRectView extends View {
	public static final int BORDERDISTANCE = 50;
	private Paint paint;
	
	public ClipRectView(Context context) {
		super(context, null);
	}
	
	public ClipRectView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		// TODO Auto-generated constructor stub
		paint = new Paint();
	}
	
	public ClipRectView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		if (paint == null) {
			paint = new Paint();
		}
	}
	
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// 屏幕的宽和高
		int width = this.getWidth();
		int height = this.getHeight();
		
		int borderLength = (width - BORDERDISTANCE * 2) / 2;
		
		paint.setColor(0xaa000000);
		
		// 绘制阴影区域
		// 上方
		canvas.drawRect(0, 0, width, (height - borderLength *2) /2, paint);
		// 左方
		canvas.drawRect(0, (height - borderLength *2) /2, BORDERDISTANCE, (height + borderLength *2) /2, paint);
		// 下方
		canvas.drawRect(0, (height + borderLength *2) /2, width, height, paint);
		// 右方
		canvas.drawRect(width - BORDERDISTANCE, (height - borderLength *2) /2, width,(height + borderLength *2) /2, paint);
		
		// 绘制周围白色的裁剪区域
		paint.setColor(color.white);
		paint.setStrokeWidth(2.0f);
		
		// 上方
		canvas.drawRect(BORDERDISTANCE, (height - borderLength *2) /2, width - BORDERDISTANCE, (height - borderLength *2) /2, paint);
		// 左方
		canvas.drawRect(BORDERDISTANCE, (height - borderLength *2) /2, BORDERDISTANCE, (height + borderLength *2) /2, paint);
		// 下方
		canvas.drawRect(BORDERDISTANCE, (height + borderLength *2) /2, width - BORDERDISTANCE, (height + borderLength *2) /2, paint);
		// 右方
		canvas.drawRect(width - BORDERDISTANCE, (height - borderLength *2) /2, width - BORDERDISTANCE, (height + borderLength *2) /2, paint);
	}
	
}
