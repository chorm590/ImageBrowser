package com.yyf.imagebrowser.entity;

import com.yyf.imagebrowser.tools.DataStorage;

/**
 * 扫描出的图像的实体类。
 * */
public class ImageEntity {
	/**图像的路径。*/
	private String path;
	/**图像的父文件夹名称。*/
	private String parentFolderName;
	/**当前图片的文件名。*/
	private String name;
	private String temp;
	/**表征当前图像在CheckBox中是否被选中的标志位。*/
	private boolean isSelected;
	private int starLevel;
	
	/**
	 * 根据图片的绝对路径来创建ImageEntity对象。
	 * */
	public ImageEntity(String path){
		this.path = path;
		//得到图像的路径后就开始计算父文件夹名。
		setParentFolder();
		//提取当前图片的文件名。
		setFileName();
		//获取星级数据。
		setStar();
	}
	
	public int getStarLevel(){
		return starLevel;
	}
	
	public void setStarLevel(int level){
		this.starLevel = level;
	}
	
	/**
	 * 表征当前图像在CheckBox中是否被选中的标志位。
	 * */
	public boolean getIsSelected(){
		return isSelected;
	}
	
	/**
	 * 表征当前图像在CheckBox中是否被选中的标志位。
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
	 * 在这里根据图片的绝对路径查寻对应的星级。
	 * */
	private void setStar(){
		//根据路径查寻图片的星级，查到了就返回对应星级，查寻不到则返回默认级别：2级。
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
