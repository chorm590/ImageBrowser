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
	//����ϵͳͼ�����ݿ��URI��
	private Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	private ArrayList<String> reverseList = new ArrayList<>();
	
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
	 * ���︺�𽫻������ݼ��������γɿ�ʹ�õ����ݡ�
	 * */
	public Map<String, ArrayList<ImageEntity>> getImagesSortByTimeLineOutter(){
		Map<String, ArrayList<ImageEntity>> map = null;
		map = getImagesSortByTimeLine();
		//�������������һЩ�ϲ�������
		
		// �Ѽ�����ȫȡ������
		Iterator<String> ite = map.keySet().iterator();
		ArrayList<String> dateList = new ArrayList<>();
		while(ite.hasNext()){
			String ss = ite.next();
			dateList.add(ss);
		}
		//��ʱ�䵹��ȡ8��ʱ��ֵ���뵽reverseList�С�
		for(int i = 0; i < 7; i++){
			reverseList.add(dateList.get(dateList.size() - 1 - i));
		}
		reverseList.add("������ǰ");
		// ��ʱ�䵹��ֵӦ�õ�Map�����С�
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
	 * �����︺������ݿ��в�ѯ���ݡ�
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
			// ��ȡͼ���·����������������ͬʱ���Ѿ���ͼ����Ǽ����ݲ�����ˡ�
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
		
		// ���ꡣ����
		commonDate += temp[5]+"��";
		
		// ���¡�����
		switch (temp[1]) {
			case "Jan":
				commonDate += "1��";
			break;
			case "Feb":
				commonDate += "2��";
			break;
			case "Mar":
				commonDate += "3��";
				break;
			case "Apr":
				commonDate += "4��";
				break;
			case "May":
				commonDate += "5��";
				break;
			case "Jun":
				commonDate += "6��";
				break;
			case "Jul":
				commonDate += "7��";
				break;
			case "Aug":
				commonDate += "8��";
				break;
			case "Sep":
				commonDate += "9��";
				break;
			case "Oct":
				commonDate += "10��";
				break;
			case "Nov":
				commonDate += "11��";
				break;
			case "Dec":
				commonDate += "12��";
				break;
		} // switch 1   ---   end.
		
		//  �ڼ��ա���
		commonDate += temp[2]+"��";
		
		//  �ܼ�����
		switch(temp[0]){
			case "Mon":
				commonDate += "  ��һ";
			break;
			case "Tue":
				commonDate += "  �ܶ�";
			break;
			case "Wed":
				commonDate += "  ����";
			break;
			case "Thu":
				commonDate += "  ����";
			break;
			case "Fri":
				commonDate += "  ����";
			break;
			case "Sat":
				commonDate += "  ����";
			break;
			case "Sun":
				commonDate += "  ����";
			break;
		}//  switch3   --   end.
		
		return commonDate;
	}
	
	/**
	 * ����ɨ��ϵͳ���ݿ��б����ͼƬ��Ϣ��
	 * @return ���з�װ��ImageEntity�ļ��϶��󡣼��Ǹ��ļ�������ֵ�Ǹ��ļ����ڵ�ͼƬ���ݡ�
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
