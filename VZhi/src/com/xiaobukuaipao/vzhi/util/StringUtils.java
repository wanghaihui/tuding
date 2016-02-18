package com.xiaobukuaipao.vzhi.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xiaobukuaipao.vzhi.im.IMConstants;


public class StringUtils {

	 /**
     * is null or its length is 0
     * 
     * <pre>
     * isEmpty(null) = true;
     * isEmpty(&quot;&quot;) = true;
     * isEmpty(&quot;  &quot;) = false;
     * </pre>
     * 
     * @param str
     * @return if string is null or its size is 0, return true, else return false.
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * 将手机号码格式化为 +86 XXX XXXX XXXX
     */
    public static String formatPhoneNumber(String mobile) {
    	StringBuilder sb = new StringBuilder();
    	sb.append("+86");
    	sb.append(" ");
    	sb.append(mobile.substring(0, 3));
    	sb.append(mobile.substring(3, 7));
    	sb.append(mobile.substring(7));
    	return sb.toString();
    }
    
    /**
     * 组合时间
     */
    public static String formatDate(int year, int month, int dayOfMonth) {
    	StringBuilder sb = new StringBuilder();
    	sb.append(year);
    	sb.append("-");
    	if(month >=0 && month < 9)
    		sb.append(0);
    	sb.append(month+1);
    	sb.append("-");
    	if(dayOfMonth >=0 && dayOfMonth <= 9)
    		sb.append(0);
    	sb.append(dayOfMonth);
    	return sb.toString();
    }
    
    /**
     * 组合时间
     */
    public static String formatDate(int year, int month) {
    	StringBuilder sb = new StringBuilder();
    	sb.append(year);
    	sb.append("-");
    	if(month >= 0 && month <= 9)
    		sb.append(0);
    	sb.append(month);
    	
    	return sb.toString();
    }
    
    /**
     * 组合时间
     */
    public static String formatTime(int hour, int minute) {
    	StringBuilder sb = new StringBuilder();
    	if(hour >=0 && hour <= 9)
    		sb.append(0);
    	sb.append(hour);
    	sb.append(":");
    	if(minute >=0 && minute <= 9)
    		sb.append(0);
    	sb.append(minute);
    	return sb.toString();
    }
    /**
     * compare two string
     * 
     * @param actual
     * @param expected
     * @return
     * @see ObjectUtils#isEquals(Object, Object)
     */
    public static boolean isEquals(String actual, String expected) {
        return ObjectUtils.isEquals(actual, expected);
    }
    
    public static String getMd5(String input) {
        String output = null;
        if (input != null && input.length() > 0)
            try {
                MessageDigest messagedigest = MessageDigest.getInstance("MD5");
                messagedigest.update(input.getBytes(), 0, input.length());
                output = String.format(IMConstants.MD5_KEY, new BigInteger(1,
                        messagedigest.digest()));
            } catch (Exception exception) {
            }
        return output;
    }
    
    
    /**
	 * 判断是否是手机号码
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isCellphone(String str) {
		Pattern pattern = Pattern.compile("1[0-9]{10}");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	
	/**
	 * 判断是否是验证码
	 */
	public static boolean isVerifyCode (String str) {
		Pattern pattern = Pattern.compile("[0-9]{4}");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	
	/**
	 * 判断是否是密码
	 */
	public static boolean isPassword (String str) {
		Pattern pattern = Pattern.compile("^[\\w\\W]{6,}$");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
}
