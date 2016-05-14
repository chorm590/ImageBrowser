package com.yyf.imagebrowser.tools;

import java.io.File;

import android.content.ContentResolver;
import android.util.Log;

/**
 * 使用方法：<br/>
 * 1. 创建一个本类对象；<br/>
 * 2. 调用setPath()方法设置要删除文件的路径；<br/>
 * 3. 调用delete()方法执行删除操作。<br/>
 * */
public class FileDelete {
	
	private File file;
	private ScanImage scanImage;
	private String path;
	
	public FileDelete(ContentResolver contentResolver){
		scanImage = new ScanImage(contentResolver);
	}
	
	public void setPath(String path){
		this.path = path;
		file = new File(this.path);
	}
	
	public boolean delete(){
		/*
		 * 这个删除除了要把存储介质中的文件删掉以外
		 * 还应该再手动地把图片数据库中的对应数据删掉。
		 * 否则的话就会出现某个文件“删除不掉”的情况。
		 * */
		boolean delResult = file.delete();
		int numDels = 0;
		//如果该文件被成功删掉了，则同时删除数据库中的记录。
		if(delResult){
			numDels = scanImage.delFromImagesDB(path);
			Log.v("mylog", "The nums was deleted from Images database is:"+numDels);
			return true;
		}
		
		return false;
	}
}
