package com.yyf.imagebrowser.fragment;

import java.util.ArrayList;
import java.util.Map;

import com.yyf.imagebrowser.ImageListActivity;
import com.yyf.imagebrowser.R;
import com.yyf.imagebrowser.adapter.SortByFolderAadapter;
import com.yyf.imagebrowser.entity.ImageEntity;
import com.yyf.imagebrowser.entity.ViewHolder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class FgmtSortByFolder extends Fragment {
	
	private Map<String, ArrayList<ImageEntity>> map;
	private ArrayList<ImageEntity> listDate;
	private SortByFolderAadapter sortByFolderdapter;
	private static FgmtSortByFolder instance;
	private GridView gv;
	private int currentSelectedPos;
	
	public FgmtSortByFolder(Map<String, ArrayList<ImageEntity>> map) {
		instance = this;
		this.map = map;
		currentSelectedPos = -1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		gv = (GridView) inflater.inflate(R.layout.gridview, null);

		sortByFolderdapter = new SortByFolderAadapter(getContext(), map);
		gv.setAdapter(sortByFolderdapter);
		//给这个GridView注册点击监听。
		gv.setOnItemClickListener(new GvItemClickListener());
		
		return gv;
	}
	
	/**
	 * 更新指定文件夹中的展示信息。
	 * */
	public void refreshData(int position){
		//取得被修改的项视图。
		View view = gv.getChildAt(position);
		ViewHolder holder = (ViewHolder) view.getTag();
		//更新封面图。
		sortByFolderdapter.loadImage(holder.iv, position);
		//更新图片数量。
		holder.tvAmount.setText(String.valueOf(sortByFolderdapter.getItem(position).size()));
		Log.d("mylog", "FgmtSortByFolder - refreshData finished.");
	}// refreshData  --  end.
	
	public static FgmtSortByFolder getInstance(){
		return instance;
	}
	
	/**
	 * 获取存有当前文件夹内所有图片信息的集合对象。
	 * */
	public ArrayList<ImageEntity> getDataList(){
		return listDate;
	}
	
	/**
	 * 获取主Activity中点选的文件夹的序号。
	 * @return 返回当前浏览的文件夹的序号。初始返回-1。
	 * */
	public int getCurrentSelectedPos(){
		return currentSelectedPos;
	}
	
	/**
	 * GridView的子项点击事件监听响应类。
	 * */
	private class GvItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			/*
			 * 在这里实现取得所点击文件夹的图像集合对象后
			 * 传到一个新启动的Activity中去具体显示出来。
			 * */
			//step 1. Get the image collection which was clicked.
			listDate = sortByFolderdapter.getItem(position);
			startActivity(new Intent(getActivity(), ImageListActivity.class));
			currentSelectedPos = position;
			Log.v("mylog", "FgmtSortByFolder - Jump pos="+position);
		}
		
	}

}
