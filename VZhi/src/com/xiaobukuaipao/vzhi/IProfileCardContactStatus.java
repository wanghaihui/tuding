package com.xiaobukuaipao.vzhi;

/**
 *	刷新候选人收件箱中的简历信息
 *	1.主要刷新联系状态和标志，单个对其中的item进行刷新
 *	
 */
public interface IProfileCardContactStatus {

	/**
	 * 列表信息中的联系状态更改
	 * @param position
	 * @param contactStatus
	 */
	public void onProfileCardChange(int position, int contactStatus);
}
