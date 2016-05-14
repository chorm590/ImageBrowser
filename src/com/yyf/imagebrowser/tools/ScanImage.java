package com.yyf.imagebrowser.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
	 * 用于扫描系统数据库中保存的图片信息。
	 * @return 所有封装成ImageEntity的集合对象。
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
