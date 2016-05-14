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
	 * ����ͼ��·������Ѱ��ǰͼ����Ǽ���
	 * �����ݿ���û�е�ǰͼƬ���Ǽ���¼ʱ����Ĭ���Ǽ���
	 * Ĭ���Ǽ�Ϊ3����
	 * */
	public int getStarLevel(String path){ //����˵���path�ǲ�����Ϊ�յġ�
		return sp.getInt(path, DEFAULT_STAR_LEVEL);
	}
	
	/**
	 * �������õ��Ǽ�д�뵽���ݿ��С�
	 * */
	public void setStarLevel(String path, int level){
		editor.putInt(path, level);
		editor.apply();
		Log.v("mylog", "DataStorage - data was saved. level="+level);
	}
	
	// ��������
	private DataStorage(Context context) {
		instance = this;
		// Get SharedPreferences object.
		sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		// Get editor object.
		editor = sp.edit();
	}
	
	public static void init(Context context){
		//����ʽ����ģʽ��
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
