package com.xiaobukuaipao.vzhi.domain;

import java.io.Serializable;

import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.RandomUtils;
import com.xiaobukuaipao.vzhi.util.StringUtils;

@Deprecated
public class Education implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String background; //学历
    private String school; // 学校
    private String major; //专业
    private String startTime = ""; //教育的开始时间
    private String endTime = ""; //教育的结束时间
    @SuppressWarnings("unused")
	private String time;

    private int viewId; //视图id

    public String getTime() {
        if (StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) {
            return "";
        }
        return new StringBuffer(this.startTime).append(GlobalConstants.GUIDE_EDUCATION_TIME).append(this.endTime).toString();
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    @Override
    public String toString() {
        return "time=" + this.getTime() + " ,school=" + this.school + " ,major=" + this.major + " ,background=" + background;
    }

    @Override
    public int hashCode() {
        return RandomUtils.getRandom(10000) * RandomUtils.getRandom(0,100000000);
    }
}
