package com.xiaobukuaipao.vzhi.im;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class ImUtils {
	
	/**
	 * 根据类型来匹配显示类型(display type)
	 * @param type
	 * @return
	 */
	public static int matchDisplayType (int type) {
		switch (type) {
			case IMConstants.TYPE_P2P_TEXT:
				return IMConstants.DISPLAY_TYPE_TEXT;
			case IMConstants.TYPE_P2P_REAL_CARD:
				return IMConstants.DISPLAY_TYPE_REAL_PROFILE_CARD;
			case IMConstants.TYPE_P2P_NICK_CARD:
				return IMConstants.DISPLAY_TYPE_NICK_PROFILE_CARD;
			case IMConstants.TYPE_P2P_JOB_INVITATION:
				return IMConstants.DISPLAY_TYPE_JOB_INVITATION;
			case IMConstants.TYPE_P2P_JOB_INVITATION_ACCEPT:
				return IMConstants.DISPLAY_TYPE_PROMPT_TEXT;
			case IMConstants.TYPE_P2P_JOB_INVITATION_IGNORE:
				return IMConstants.DISPLAY_TYPE_PROMPT_TEXT;
			case IMConstants.TYPE_P2P_INTERVIEW_INVITATION:
				return IMConstants.DISPLAY_TYPE_INTERVIEW_INVITATION;
			case IMConstants.TYPE_P2P_INTERVIEW_ACCEPT:
				return IMConstants.DISPLAY_TYPE_PROMPT_TEXT;
			case IMConstants.TYPE_P2P_INTERVIEW_IGNORE:
				return IMConstants.DISPLAY_TYPE_PROMPT_TEXT;
			case IMConstants.TYPE_P2P_JOB_SUMMARY:
				return IMConstants.DISPLAY_TYPE_JOB_SUMMARY;
			case IMConstants.TYPE_P2P_JOB_APPLY:
				return IMConstants.DISPLAY_TYPE_COMPOSE_JSON_PROMPT_TEXT;
			case IMConstants.TYPE_P2P_JOB_APPLY_READ:
				return IMConstants.DISPLAY_TYPE_COMPOSE_PROMPT_TEXT;
			case IMConstants.TYPE_P2P_JOB_APPLY_INTEREST:
				return IMConstants.DISPLAY_TYPE_PROMPT_TEXT;
			case IMConstants.TYPE_P2P_BUDDY_INVITATION_ACCEPT:
				return IMConstants.DISPLAY_TYPE_PROMPT_TEXT;
			case IMConstants.TYPE_P2P_JOB_APPLY_CONTACT_READ:
				return IMConstants.DISPLAY_TYPE_PROMPT_TEXT;
		}
		return IMConstants.DISPLAY_TYPE_TEXT;
	}
	
	/**
	 * 群组框中的类型匹配
	 * @param type
	 * @return
	 */
	public static int matchDisplayGroupType (int type) {
		switch (type) {
			case IMConstants.TYPE_GROUP_TEXT:
				return IMConstants.DISPLAY_TYPE_GROUP_TEXT;
			case IMConstants.TYPE_GROUP_JOIN:
				return IMConstants.DISPLAY_TYPE_GROUP_JOIN;
			case IMConstants.TYPE_GROUP_QUIT:
				return IMConstants.DISPLAY_TYPE_GROUP_QUIT;
			case IMConstants.TYPE_P2P_JOB_APPLY_READ:
				return IMConstants.DISPLAY_TYPE_GROUP_JOB_APPLY_READ;
			case IMConstants.TYPE_P2P_JOB_APPLY_INTEREST:
				return IMConstants.DISPLAY_TYPE_GROUP_JOB_APPLY_INTEREST;
			case IMConstants.TYPE_GROUP_INTERVIEW_INVITATION:
				return IMConstants.DISPLAY_TYPE_GROUP_PROMPT_TEXT;
			case IMConstants.TYPE_GROUP_INTERVIEW_ACCEPT:
				return IMConstants.DISPLAY_TYPE_GROUP_PROMPT_TEXT;
			case IMConstants.TYPE_GROUP_INTERVIEW_IGNORE:
				return IMConstants.DISPLAY_TYPE_GROUP_PROMPT_TEXT;
			case IMConstants.TYPE_GROUP_JOB_APPLY_READ:
				return IMConstants.DISPLAY_TYPE_GROUP_PROMPT_TEXT;
			case IMConstants.TYPE_GROUP_JOB_APPLY_INTEREST:
				return IMConstants.DISPLAY_TYPE_GROUP_PROMPT_TEXT;
			case IMConstants.TYPE_GROUP_JOB_APPLY_CONTACT_READ:
				return IMConstants.DISPLAY_TYPE_GROUP_PROMPT_TEXT;
		}
		return IMConstants.DISPLAY_TYPE_GROUP_TEXT;
	}
	
	/**
	 * 是否在Im中
	 * @param context
	 * @return
	 */
	public static boolean isInIm(Context context) {
        try {
            if (context instanceof Activity) {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).size() > 0 ? am
                        .getRunningTasks(1).get(0).topActivity : null;
                        
                if (cn != null && cn.getClassName().contains(GlobalConstants.APPLICATION_PACKAGE_NAME)) {
                    return true;
                }
                return false;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
	
}
