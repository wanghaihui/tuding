package com.xiaobukuaipao.vzhi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class NestedGridView extends GridView {

	private int maxLines = 0;
	
	public NestedGridView(Context context, AttributeSet attrs) {   
        super(context, attrs);   
    }   
   
    public NestedGridView(Context context) {   
        super(context);   
    }   
   
    public NestedGridView(Context context, AttributeSet attrs, int defStyle) {   
        super(context, attrs, defStyle);   
    }   
   
    @Override   
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   
    	int expandSpec = 0;
    	if(maxLines == 0){
    		expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,    MeasureSpec.AT_MOST);  
    	}else{
    		
//    		expandSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.AT_MOST);
    	}
        
        super.onMeasure(widthMeasureSpec, expandSpec);   
    }

	public int getMaxLines() {
		return maxLines;
	}

	public void setMaxLines(int maxLines) {
		this.maxLines = maxLines;
	}
	
	@Override  
	public boolean dispatchTouchEvent(MotionEvent ev) {  
	    if(ev.getAction() == MotionEvent.ACTION_MOVE)  
	    {  
	        return true;  
	    }  
	    return super.dispatchTouchEvent(ev);  
	}  
    
    
}
