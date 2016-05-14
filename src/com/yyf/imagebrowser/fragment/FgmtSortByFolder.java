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
		//�����GridViewע����������
		gv.setOnItemClickListener(new GvItemClickListener());
		
		return gv;
	}
	
	/**
	 * ����ָ���ļ����е�չʾ��Ϣ��
	 * */
	public void refreshData(int position){
		//ȡ�ñ��޸ĵ�����ͼ��
		View view = gv.getChildAt(position);
		ViewHolder holder = (ViewHolder) view.getTag();
		//���·���ͼ��
		sortByFolderdapter.loadImage(holder.iv, position);
		//����ͼƬ������
		holder.tvAmount.setText(String.valueOf(sortByFolderdapter.getItem(position).size()));
		Log.d("mylog", "FgmtSortByFolder - refreshData finished.");
	}// refreshData  --  end.
	
	public static FgmtSortByFolder getInstance(){
		return instance;
	}
	
	/**
	 * ��ȡ���е�ǰ�ļ���������ͼƬ��Ϣ�ļ��϶���
	 * */
	public ArrayList<ImageEntity> getDataList(){
		return listDate;
	}
	
	/**
	 * ��ȡ��Activity�е�ѡ���ļ��е���š�
	 * @return ���ص�ǰ������ļ��е���š���ʼ����-1��
	 * */
	public int getCurrentSelectedPos(){
		return currentSelectedPos;
	}
	
	/**
	 * GridView���������¼�������Ӧ�ࡣ
	 * */
	private class GvItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			/*
			 * ������ʵ��ȡ��������ļ��е�ͼ�񼯺϶����
			 * ����һ����������Activity��ȥ������ʾ������
			 * */
			//step 1. Get the image collection which was clicked.
			listDate = sortByFolderdapter.getItem(position);
			startActivity(new Intent(getActivity(), ImageListActivity.class));
			currentSelectedPos = position;
			Log.v("mylog", "FgmtSortByFolder - Jump pos="+position);
		}
		
	}

}
