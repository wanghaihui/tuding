package com.xiaobukuaipao.vzhi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

/**
 * 
 * 为 Key word单独封装一个类
 * 
 */
public class SpannableKeyWordBuilder extends SpannableStringBuilder {

	private int defaultColor = 0xffff6666;

	public final static int MODE_NORMAL = 0x000;

	/**
	 * 数字模式将字符串中的所有数字变为关键字
	 */
	public final static int MODE_NUMBER = MODE_NORMAL + 1;

	/**
	 * 必须手动设置一串字符串里面存在的关键字
	 */
	public final static int MODE_KEYWORD = MODE_NUMBER + 1;

	private ForegroundColorSpan foregroundColorSpan;
	private int flags = SPAN_EXCLUSIVE_EXCLUSIVE;
	private int mode = MODE_NORMAL;
	private String[] keywords;

	public SpannableKeyWordBuilder() {
		super();
	}

	public SpannableKeyWordBuilder(CharSequence text, int start, int end) {
		super(text, start, end);
	}

	public SpannableKeyWordBuilder(CharSequence text) {
		super(text);
	}

	public SpannableKeyWordBuilder(CharSequence text, String... keywords) {
		super(text);
		this.keywords = keywords;
		
	}

	/**
	 * 
	 * @param color
	 * @return
	 */
	public SpannableKeyWordBuilder setKeyColor(int color) {
		this.defaultColor = color;
		return this;
	}

	public SpannableKeyWordBuilder appendKeyWord(String keyWord) {
		int start = length();
		append(keyWord);
		int end = length();
//		Logcat.d("@@@", "关键字: " +keyWord +" : "+subSequence(start, end));
		foregroundColorSpan = new ForegroundColorSpan(defaultColor);
		setSpan(foregroundColorSpan, start, end, flags);

		return this;
	}

	public SpannableKeyWordBuilder build() {
		if (mode == MODE_NUMBER) {
			Pattern pattern = Pattern.compile("[0-9]+");
			Matcher matcher = pattern.matcher(toString());
			while (matcher.find()) {
			
				int start = matcher.start();
				int end = matcher.end();
//				Logcat.d("@@@", "关键数字: " + subSequence(start, end));
				foregroundColorSpan = new ForegroundColorSpan(defaultColor);
				setSpan(foregroundColorSpan, start, end, flags);
			}
		} else if (mode == MODE_KEYWORD) {
			if (keywords != null) {
				for (int i = 0; i < length()&&i < keywords.length; i++) {
					if (toString().contains(keywords[i])) {
						Pattern pattern = Pattern.compile(keywords[i]);//在此处应该过滤关键字中的　正则语法
						Matcher matcher = pattern.matcher(toString());
						while (matcher.find()) {
							int start = matcher.start();
							int end = matcher.end();
//							Logcat.d("@@@", "start:" + start + " end:" + end + " word:" + subSequence(start, end));
							foregroundColorSpan = new ForegroundColorSpan(defaultColor);
							setSpan(foregroundColorSpan, start, end, flags);
						}
					}
				}
			}

		}
		return this;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}

	public String[] getKeywords() {
		return keywords;
	}

	public void setKeywords(String... keywords) {
		this.keywords = keywords;
	}

	public SpannableKeyWordBuilder setFlag(int flags) {
		this.flags = flags;
		return this;
	}
}
