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
	//����ϵͳͼ�����ݿ��URI��
	private Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	
	public ScanImage(ContentResolver cr){
		contentResolver = cr;
	}
	
	/**
	 * ����·������ɾ�����ݿ��еļ�¼��
	 * @param pathWhichNeedToDel Ҫɾ����ͼƬ��·����
	 * @return ��ɾ������������
	 * */
	public int delFromImagesDB(String pathWhichNeedToDel){
		String where = MediaStore.Images.Media.DATA+"=?";
		String[] selectionArgs = new String[]{pathWhichNeedToDel};
		return contentResolver.delete(uri, where, selectionArgs);
	}
	
	/**
	 * ����ɨ��ϵͳ���ݿ��б����ͼƬ��Ϣ��
	 * @return ���з�װ��ImageEntity�ļ��϶���
	 * */
	public Map<String, ArrayList<ImageEntity>> getImageCollection(){
		String selection = MediaStore.Images.Media.MIME_TYPE +"=? or "+
									MediaStore.Images.Media.MIME_TYPE + "=?";
		String[] selectionArgs = {"image/jpeg", "image/png"};
		//����α���������豸�����е�JPG��PNG��ʽ��ͼ��
		Cursor cursor = contentResolver.query(uri, null, 
				selection/*�������������͵�ͼ��*/, 
				selectionArgs/*�������������Ϊ��JPG��PNG��ʽ*/, 
				MediaStore.Images.Media.DATE_MODIFIED/*������޸�ʱ������*/);
		if(cursor==null){
			Log.e("mylog", "ScanImage the cursor is null.");
			return null;
		}
		
		//������Ѷ�Ӧ��ͼ���ļ��з���á�
		
		ImageEntity ie = null;
		Map<String, ArrayList<ImageEntity>> map = new HashMap<>();
		ArrayList<ImageEntity> tempList = null;
		while(cursor.moveToNext()){
			ie = new ImageEntity(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
			if(map.containsKey(ie.getParentFolderName())){//��������Ը�ͼ���ļ�����Ϊ����List���ϣ���ֱ����ӡ�
				//�Ը�ͼ����ļ�����Ϊ������ȡ����List�����ٰѸ�ͼ��ʵ���������ӵ���List�����С�
				map.get(ie.getParentFolderName()).add(ie);
			}else { //����Ե�ǰ�ļ�������Ϊ����List���ϲ����ڣ����½��Ը��ļ�������Ϊ����List���ϲ���ӵ�Map�����С�
				tempList = new ArrayList<>();
				tempList.add(ie);
				map.put(ie.getParentFolderName(), tempList);
			}
		}//while  --  end.
		//�����������û��ǰ��α��һ�°ɡ�����Ҳ�����¡�
		cursor.close();
		
		return map;
	}
}
