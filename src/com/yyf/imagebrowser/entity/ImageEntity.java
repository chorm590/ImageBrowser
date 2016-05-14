package com.yyf.imagebrowser.entity;

import com.yyf.imagebrowser.tools.DataStorage;

/**
 * ɨ�����ͼ���ʵ���ࡣ
 * */
public class ImageEntity {
	/**ͼ���·����*/
	private String path;
	/**ͼ��ĸ��ļ������ơ�*/
	private String parentFolderName;
	/**��ǰͼƬ���ļ�����*/
	private String name;
	private String temp;
	/**������ǰͼ����CheckBox���Ƿ�ѡ�еı�־λ��*/
	private boolean isSelected;
	private int starLevel;
	
	/**
	 * ����ͼƬ�ľ���·��������ImageEntity����
	 * */
	public ImageEntity(String path){
		this.path = path;
		//�õ�ͼ���·����Ϳ�ʼ���㸸�ļ�������
		setParentFolder();
		//��ȡ��ǰͼƬ���ļ�����
		setFileName();
		//��ȡ�Ǽ����ݡ�
		setStar();
	}
	
	public int getStarLevel(){
		return starLevel;
	}
	
	public void setStarLevel(int level){
		this.starLevel = level;
	}
	
	/**
	 * ������ǰͼ����CheckBox���Ƿ�ѡ�еı�־λ��
	 * */
	public boolean getIsSelected(){
		return isSelected;
	}
	
	/**
	 * ������ǰͼ����CheckBox���Ƿ�ѡ�еı�־λ��
	 * */
	public void setIsSelected(boolean isSelected){
		this.isSelected = isSelected;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getParentFolderName() {
		return parentFolderName;
	}
	
	public String getFileName(){
		return name;
	}
	
	/**
	 * ���������ͼƬ�ľ���·����Ѱ��Ӧ���Ǽ���
	 * */
	private void setStar(){
		//����·����ѰͼƬ���Ǽ����鵽�˾ͷ��ض�Ӧ�Ǽ�����Ѱ�����򷵻�Ĭ�ϼ���2����
		starLevel = DataStorage.getInstance().getStarLevel(path);
	}
	
	private void setFileName(){
		name = path.substring(path.lastIndexOf("/")+1);
	}
	
	private void setParentFolder(){
		temp = path.substring(0, path.lastIndexOf("/"));
		parentFolderName = temp.substring(temp.lastIndexOf("/")+1);
	}
	
}
