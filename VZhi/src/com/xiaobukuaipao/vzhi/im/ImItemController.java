package com.xiaobukuaipao.vzhi.im;

import com.xiaobukuaipao.vzhi.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnLongClickListener;

public class ImItemController implements OnLongClickListener{

	private String title = "未完善";
	private ItemActionListener itemActionListener;
	
	public ImItemController setActionListener(ItemActionListener itemActionListener){
		this.itemActionListener = itemActionListener;
		return this;
	}
	
	public ImItemController removeDeleteListener(ItemActionListener itemActionListener){
		this.itemActionListener = null;
		return this;
	}
	
	@Override
	public boolean onLongClick(View v) {
		if(itemActionListener != null){
			title = itemActionListener.getTitle();
		}
		new AlertDialog.Builder(v.getContext()).setTitle(title).setItems(R.array.message_list_menu, new DialogInterface.OnClickListener() { 
	        @Override 
	        public void onClick(DialogInterface dialog, int which) { 
	        	// click and delete item
	        	if(itemActionListener != null){
	        		itemActionListener.onItemDelete();
	        	}
	        } 
	    }).show(); 
		
		return false;
	}

	public interface ItemActionListener{
		/**
		 * 删除列表项
		 */
		void onItemDelete();
//		void onItemEdit();
		/**
		 * 获取标题
		 * @return
		 */
		String getTitle();
	}
}
