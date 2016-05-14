package com.yyf.imagebrowser.tools;

import java.io.File;

import android.content.ContentResolver;
import android.util.Log;

/**
 * ʹ�÷�����<br/>
 * 1. ����һ���������<br/>
 * 2. ����setPath()��������Ҫɾ���ļ���·����<br/>
 * 3. ����delete()����ִ��ɾ��������<br/>
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
		 * ���ɾ������Ҫ�Ѵ洢�����е��ļ�ɾ������
		 * ��Ӧ�����ֶ��ذ�ͼƬ���ݿ��еĶ�Ӧ����ɾ����
		 * ����Ļ��ͻ����ĳ���ļ���ɾ���������������
		 * */
		boolean delResult = file.delete();
		int numDels = 0;
		//������ļ����ɹ�ɾ���ˣ���ͬʱɾ�����ݿ��еļ�¼��
		if(delResult){
			numDels = scanImage.delFromImagesDB(path);
			Log.v("mylog", "The nums was deleted from Images database is:"+numDels);
			return true;
		}
		
		return false;
	}
}
