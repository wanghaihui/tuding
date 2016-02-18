package com.xiaobukuaipao.vzhi.domain.user;

import java.io.Serializable;

import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.RandomUtils;
import com.xiaobukuaipao.vzhi.util.StringUtils;
@Deprecated
public class JobExperience implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String startTime = ""; //教育的开始时间
    private String endTime = ""; //教育的结束时间
    private String time;

    private int viewId; //视图id
    private String companyName; //公司名称
    private String jobName; // 职位名称
    private String description; //职位描述
    private String salary; //目前在职的薪水

    public String getTime() {
        if (StringUtils.isEmpty(getStartTime()) || StringUtils.isEmpty(getEndTime())) {
            return "";
        }
        return new StringBuffer(this.getStartTime()).append(GlobalConstants.GUIDE_JOB_DEFAULT_TIME).append(this.getEndTime()).toString();
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Override
    public String toString() {
        return "time=" + this.time + " ,companyName=" + this.companyName + " ,jobName=" + this.jobName + " ,description=" + this.description + " ,salary=" + this.salary;
    }

    @Override
    public int hashCode() {
        return RandomUtils.getRandom(1000000) * RandomUtils.getRandom(0,198000000);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public boolean isFinishInfo() {
        if (StringUtils.isNotEmpty(getStartTime()) && StringUtils.isNotEmpty(getEndTime())
                && StringUtils.isNotEmpty(this.jobName)
                && StringUtils.isNotEmpty(this.companyName)) {
            return true;
        } else {
            return false;
        }
    }
}
