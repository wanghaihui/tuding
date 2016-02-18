package com.xiaobukuaipao.vzhi.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

public class FileCache {

	/**
	 * @param json
	 * @param filename
	 */
	public static boolean saveData(Context context, String json, String filename) {
		FileOutputStream outputStream = null;
		FileInputStream inputStream = null;
		
		File cacheDir = context.getCacheDir();
		
		File file = new File(cacheDir.getPath() + File.separator + filename);
	
		if(cacheDir.isDirectory()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
			try {
				outputStream = new FileOutputStream(file);
				outputStream.write(json.getBytes());
				outputStream.flush();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} finally{
				if(outputStream != null){
					try {
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(inputStream != null){
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return true;
	}

	public static String readData(Context context,String filename){
		
		FileInputStream inputStream = null;
		
		ByteArrayOutputStream arrayOutputStream = null;
		File cacheDir =  context.getCacheDir();
		File file = new File(cacheDir.getPath() + File.separator +filename);
		
		if(file.exists()){
			if(NetworkUtils.isNetWorkAvalible(context) && System.currentTimeMillis() - file.lastModified() > 2 * 60 * 1000){//文件每隔2分钟会自动从新读取网络数据
				return null;
			}
		}
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		arrayOutputStream = new ByteArrayOutputStream();
		int len = 0;
		byte[] buffer = new byte[4 * 1024];
		StringBuilder sb = new StringBuilder();
		try {
			while((len =  inputStream.read(buffer)) != -1){
				arrayOutputStream.write(buffer, 0, len);
			}
			arrayOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(arrayOutputStream != null){
				try {
					arrayOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		sb.append(arrayOutputStream.toString());
		Log.i("@@@", sb.toString());
		return sb.toString();
	}

}
