package com.yyf.imagebrowser.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.yyf.imagebrowser.entity.ImageEntity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class ScanImage {
	
	private ContentResolver contentResolver;
	//搜索系统图像数据库的URI。
	private Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	private ArrayList<String> reverseList = new ArrayList<>();
	
	public ScanImage(ContentResolver cr){
		contentResolver = cr;
	}
	
	/**
	 * 根据路径条件删除数据库中的记录。
	 * @param pathWhichNeedToDel 要删除的图片的路径。
	 * @return 被删除掉的行数。
	 * */
	public int delFromImagesDB(String pathWhichNeedToDel){
		String where = MediaStore.Images.Media.DATA+"=?";
		String[] selectionArgs = new String[]{pathWhichNeedToDel};
		return contentResolver.delete(uri, where, selectionArgs);
	}
	
	/**
	 * 这里负责将基础数据加以整理，形成可使用的数据。
	 * */
	public Map<String, ArrayList<ImageEntity>> getImagesSortByTimeLineOutter(){
		Map<String, ArrayList<ImageEntity>> map = null;
		map = getImagesSortByTimeLine();
		//在这里把它们作一些合并操作。
		
		// 把键集合全取出来。
		Iterator<String> ite = map.keySet().iterator();
		ArrayList<String> dateList = new ArrayList<>();
		while(ite.hasNext()){
			String ss = ite.next();
			dateList.add(ss);
		}
		//按时间倒序共取8个时间值插入到reverseList中。
		for(int i = 0; i < 7; i++){
			reverseList.add(dateList.get(dateList.size() - 1 - i));
		}
		reverseList.add("更早以前");
		// 将时间倒序值应用到Map集合中。
		Map<String, ArrayList<ImageEntity>> mapSimple = new HashMap<>();
		for(byte idx = 0; idx < reverseList.size()-1; idx++){
			mapSimple.put(reverseList.get(idx), map.get(reverseList.get(idx)));
			map.remove(reverseList.get(idx));
		}
		ArrayList<ImageEntity> other = new ArrayList<>();
		Iterator<String> itee = map.keySet().iterator();
		while(itee.hasNext()){
			other.addAll(map.get(itee.next()));
		}
		mapSimple.put(reverseList.get(reverseList.size()-1), other);
		
		return mapSimple;
	}
	
	public ArrayList<String> getReverseList(){
		return reverseList;
	}
	
	/**
	 * 在这里负责从数据库中查询数据。
	 * */
	private Map<String, ArrayList<ImageEntity>> getImagesSortByTimeLine(){
		Map<String, ArrayList<ImageEntity>> map = new TreeMap<>();
		String selection = MediaStore.Images.Media.MIME_TYPE +"=? or "+
				MediaStore.Images.Media.MIME_TYPE + "=?";
		String[] selectionArgs = {"image/jpeg", "image/png"};
		Cursor cursor = contentResolver.query(uri, null, selection, selectionArgs, MediaStore.Images.Media.DATE_MODIFIED);
		if(cursor == null){
			Log.e("mylog", "ScanImage the cursor is null.");
			return null;
		}
		
		ImageEntity ie = null;
		while(cursor.moveToNext()){
			// 获取图像的路径。构建这个对象的同时就已经将图像的星级数据查出来了。
			ie = new ImageEntity(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
			long longDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
			String commonDate = parseDate(new Date(longDate).toString());
			if(map.containsKey(commonDate)){
				ArrayList<ImageEntity> lt = map.get(commonDate);
				lt.add(ie);
				map.put(commonDate, lt);
			}else {
				ArrayList<ImageEntity> lt = new ArrayList<>();
				lt.add(ie);
				map.put(commonDate, lt);
			}
		} //  while   ---  end.
		
		return map;
	}
	
	private String parseDate(String date){
		String commonDate = "";
		//  date =   Thu May 19 14:07:54 GMT+08:00 2016
		String[] temp = date.split(" ");
		
		// 几年。。。
		commonDate += temp[5]+"年";
		
		// 几月。。。
		switch (temp[1]) {
			case "Jan":
				commonDate += "1月";
			break;
			case "Feb":
				commonDate += "2月";
			break;
			case "Mar":
				commonDate += "3月";
				break;
			case "Apr":
				commonDate += "4月";
				break;
			case "May":
				commonDate += "5月";
				break;
			case "Jun":
				commonDate += "6月";
				break;
			case "Jul":
				commonDate += "7月";
				break;
			case "Aug":
				commonDate += "8月";
				break;
			case "Sep":
				commonDate += "9月";
				break;
			case "Oct":
				commonDate += "10月";
				break;
			case "Nov":
				commonDate += "11月";
				break;
			case "Dec":
				commonDate += "12月";
				break;
		} // switch 1   ---   end.
		
		//  第几日。。
		commonDate += temp[2]+"日";
		
		//  周几。。
		switch(temp[0]){
			case "Mon":
				commonDate += "  周一";
			break;
			case "Tue":
				commonDate += "  周二";
			break;
			case "Wed":
				commonDate += "  周三";
			break;
			case "Thu":
				commonDate += "  周四";
			break;
			case "Fri":
				commonDate += "  周五";
			break;
			case "Sat":
				commonDate += "  周六";
			break;
			case "Sun":
				commonDate += "  周日";
			break;
		}//  switch3   --   end.
		
		return commonDate;
	}
	
	/**
	 * 用于扫描系统数据库中保存的图片信息。
	 * @return 所有封装成ImageEntity的集合对象。键是父文件夹名，值是该文件夹内的图片数据。
	 * */
	public Map<String, ArrayList<ImageEntity>> getImageCollection(){
		String selection = MediaStore.Images.Media.MIME_TYPE +"=? or "+
									MediaStore.Images.Media.MIME_TYPE + "=?";
		String[] selectionArgs = {"image/jpeg", "image/png"};
		//这个游标里包含了设备上所有的JPG与PNG格式的图像。
		Cursor cursor = contentResolver.query(uri, null, 
				selection/*搜索这两种类型的图像*/, 
				selectionArgs/*具体的两种类型为：JPG与PNG格式*/, 
				MediaStore.Images.Media.DATE_MODIFIED/*按最后修改时间排序*/);
		if(cursor==null){
			Log.e("mylog", "ScanImage the cursor is null.");
			return null;
		}
		
		//在这里把对应的图像按文件夹分类好。
		
		ImageEntity ie = null;
		Map<String, ArrayList<ImageEntity>> map = new HashMap<>();
		ArrayList<ImageEntity> tempList = null;
		while(cursor.moveToNext()){
			ie = new ImageEntity(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
			if(map.containsKey(ie.getParentFolderName())){//如果包含以该图像文件夹名为键的List集合，则直接添加。
				//以该图像的文件夹名为键索引取出该List集合再把该图像实体类对象添加到该List集合中。
				map.get(ie.getParentFolderName()).add(ie);
			}else { //如果以当前文件夹名称为键的List集合不存在，则新建以该文件夹名称为键的List集合并添加到Map集合中。
				tempList = new ArrayList<>();
				tempList.add(ie);
				map.put(ie.getParentFolderName(), tempList);
			}
		}//while  --  end.
		//这个，，，最好还是把游标关一下吧。反正也不碍事。
		cursor.close();
		
		return map;
	}
}
