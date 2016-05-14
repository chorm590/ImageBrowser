package com.yyf.imagebrowser.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class DataStorage {
	
	private static DataStorage instance;
	private String spName = "Star_Level";
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private final int DEFAULT_STAR_LEVEL = 2;
	
	/**
	 * 根据图像路径名查寻当前图像的星级。
	 * 当数据库中没有当前图片的星级记录时返回默认星级。
	 * 默认星级为3级。
	 * */
	public int getStarLevel(String path){ //按理说这个path是不可能为空的。
		return sp.getInt(path, DEFAULT_STAR_LEVEL);
	}
	
	/**
	 * 将新设置的星级写入到数据库中。
	 * */
	public void setStarLevel(String path, int level){
		editor.putInt(path, level);
		editor.apply();
		Log.v("mylog", "DataStorage - data was saved. level="+level);
	}
	
	// 构造器。
	private DataStorage(Context context) {
		instance = this;
		// Get SharedPreferences object.
		sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		// Get editor object.
		editor = sp.edit();
	}
	
	public static void init(Context context){
		//懒汉式单例模式。
		if(instance==null){
			synchronized (DataStorage.class) {
				if(instance==null){
					new DataStorage(context);
				}
			}
		}
	}

	public static DataStorage getInstance(){
		return instance;
	}
	
}
