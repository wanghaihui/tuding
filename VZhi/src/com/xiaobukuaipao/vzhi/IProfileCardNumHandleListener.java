package com.xiaobukuaipao.vzhi;

/**
 * 对陌生人收件箱中简历卡片数量的更新监听
 * 1.未处理中，当用户读过了信息，滑走了卡片，更新未处理计数
 * 2.未处理中，当用户点击了感兴趣，感兴趣按钮变为月面试
 * 3.未处理中，当用户点击了聊一聊，更新已联系人计数
 * 4.全部和已联系中，用户点击了聊一聊，更新已联系人计数
 * 
 * @author hongxin.bai
 */
public interface IProfileCardNumHandleListener {

	/**
	 * 对卡片数量的改变
	 * 
	 * @param position
	 * 	处理卡片时候所在的页面索引
	 * @param undiposed
	 * 	未处理数量，不修改传0
	 * @param all
	 * 	全部的数量，如果要再里面维护这个数值，会使用到
	 * @param contacted
	 * 	已联系数量，不修改传0
	 */
	public void OnProfileHasHandled(int position, int undiposed, int all,  int contacted);
	
}
