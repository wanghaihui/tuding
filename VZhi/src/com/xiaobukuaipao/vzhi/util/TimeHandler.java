package com.xiaobukuaipao.vzhi.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;

import com.xiaobukuaipao.vzhi.R;

/**
 * 
 * 时间标签的显示格式
 * <p>
 * TimeHandler handler = new TimeHandler(context);<br>
 * handler.time2str(time);<br>
 * if(handler.isDisplay()){<br>
 * 	//do Sth.<br>
 * }else{<br>
 * <br>
 * }<br>
 * 
 * </p>
 * 时间之差在5分钟之内   不显示时间标签<br>
 * 时间之差超过5分钟且该条消息的时间是当天的消息  显示 hh:mm<br>
 * 时间之差超过5分钟且该条消息的时间是昨天的消息   显示  昨天 hh:mm<br>
 * 时间之差超过5分钟且该条消息的时间是前天的消息   显示  前天 hh:mm<br>
 * 时间之差超过5分钟且该条消息的时间是最近一周的消息   显示  周几 hh:mm<br>
 * 超过一周的消息  显示  yy-mm-dd hh:mm
 * 
 * 1.处理的时间标签 是根据现在时间改变而不停改变的
 * 2.时间标签 是根据当前时间 给相应时间添加前缀
 * 3.前三天消息间隔是按照短时间聚合
 * 4.以后的天数是按照天来聚合
 */
public class TimeHandler {
	
	public final static int TYPE_5MIN = 1;
	public final static int TYPE_1DAY = TYPE_5MIN + 1;
	public final static int TYPE_2DAY = TYPE_1DAY + 1;
	public final static int TYPE_3DAY= TYPE_2DAY + 1;
	public final static int TYPE_1WEEK = TYPE_3DAY + 1;
	public final static int TYPE_OTHER = TYPE_1WEEK + 1;
	
	private long sec = 1000;
	private long min = sec * 60;
	private long hour = min * 60;
	private long day = hour * 24;
	private MsgRefreshTime refreshTime;
	private int tmpType = -1;
	private long tmpTime = -1;
	
	/**
	 * 聚合的时间标准　
	 */
	private long gapTime;
	/**
	 * 是否可以设置优先聚合时间<br>
	 * 
	 */
	private boolean combine;
	/**
	 * 优先聚合时间
	 */
	private long combineTime;
	
	private Context mContext;
	private TimeHandler(Context context){
		mContext = context;
		refreshTime = new MsgRefreshTime();
	}
	public static TimeHandler timeHandler;
	
	public synchronized static TimeHandler getInstance(Context context){
		if(timeHandler == null){
			timeHandler = new TimeHandler(context);
		}
		return timeHandler;
	}
	
	public void reset(){
		tmpType = -1;
		tmpTime = -1;
		gapTime = -1;
		combineTime = -1;
		combine = false;
	}
	/**
	 * 转换符合的时间字符串
	 * 
	 * @param time
	 * @return
	 */
	public String time2str(long time){
		refreshTime.setRefreshTime(time);
		return refreshTime.getRefreshTime();
	}
	
	/**
	 * 判断这个字符串是否需要显示
	 * 
	 * @return
	 */
	public boolean isDisplay(){
//		Logcat.d("@@@", "tmpTime:" + tmpTime + "    time:" + refreshTime.getTime() + " gapTime:" + gapTime + "  real gap:" + (tmpTime - refreshTime.getTime()));
//		Logcat.d("@@@", refreshTime.getRefreshTime());
//		Logcat.d("@@@", "tmpType:"+tmpType+" refreshTime.getType():"+refreshTime.getType());
		refreshTime.setDisplay(tmpType != refreshTime.getType() || tmpTime - refreshTime.getTime() >  gapTime);
		tmpType = refreshTime.getType();
		tmpTime = refreshTime.getTime();
		return refreshTime.isDisplay();
	}
	
	@SuppressLint("SimpleDateFormat")
	private String handleTime(long time){
		long currentTimeMillis = System.currentTimeMillis();//获取系统当前时间
		if(time > 0 && time < currentTimeMillis - combineTime){//到达聚合时间开始　另外的聚合
			combine = true;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		Calendar calendar = dateFormat.getCalendar();
		calendar.setTime(new Date(currentTimeMillis));
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		long timeInMillis = calendar.getTimeInMillis();
//		Logcat.d("@@@", "one :" + time + "   two:" + timeInMillis);
		if(time > currentTimeMillis - 5 * min){// 5min
			refreshTime.setType(TYPE_5MIN);
			dateFormat.applyPattern(mContext.getResources().getString(R.string.msg_time_pattern_fivemins));
		}else if(time > timeInMillis){ //today
			refreshTime.setType(TYPE_1DAY);
			dateFormat.applyPattern(mContext.getResources().getString(R.string.msg_time_pattern_today));
		}else if(time > timeInMillis -  day){// yesterday
			refreshTime.setType(TYPE_2DAY);
			dateFormat.applyPattern(mContext.getResources().getString(R.string.msg_time_pattern_yesterday));
		}else if(time > timeInMillis - 2 * day){// one more
			refreshTime.setType(TYPE_3DAY);
			dateFormat.applyPattern(mContext.getResources().getString(R.string.msg_time_pattern_onemoreday));
		}else if(time > timeInMillis - 6 * day){// one week
			refreshTime.setType(TYPE_1WEEK);
			dateFormat.applyPattern(mContext.getResources().getString(R.string.msg_time_pattern_inoneweek));
		}else{// out one week
			refreshTime.setType(TYPE_OTHER);
			dateFormat.applyPattern(mContext.getResources().getString(R.string.msg_time_pattern_outoneweek));
		}
		
		String format = dateFormat.format(new Date(time));
		return format;
	} 
	
	
	
	class MsgRefreshTime{
		int type = -1;
		
		/**
		 * 是否显示消息
		 */
		boolean isDisplay;
		/**
		 * 时间戳
		 */
		long refreshTime;
		
		
		public boolean isDisplay() {
			return isDisplay;
		}

		public void setDisplay(boolean isDisplay) {
			this.isDisplay = isDisplay;
		}

		public String getRefreshTime() {
			
			return handleTime(refreshTime);
		}

		public long getTime() {
			
			return refreshTime;
		}
		public void setRefreshTime(long refreshTime) {
			this.refreshTime = refreshTime;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
	}

	public long getSec() {
		return sec;
	}

	public void setSec(long sec) {
		this.sec = sec;
	}

	public long getMin() {
		return min;
	}

	public void setMin(long min) {
		this.min = min;
	}

	public long getHour() {
		return hour;
	}

	public void setHour(long hour) {
		this.hour = hour;
	}

	public long getDay() {
		return day;
	}

	public void setDay(long day) {
		this.day = day;
	}

	public Long getGapTime() {
		return gapTime;
	}

	public void setGapTime(long gapTime) {
		combine = false;//设置完聚合时间　就要置为false
		this.gapTime = gapTime;
	}

	public boolean isCombine() {
		return combine;
	}

	public void setCombine(boolean combine) {
		this.combine = combine;
	}

	public long getCombineTime() {
		return combineTime;
	}

	public void setCombineTime(long combineTime) {
		this.combineTime = combineTime;
	}
	
}
