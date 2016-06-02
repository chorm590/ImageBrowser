package com.yyf.imagebrowser.fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.yyf.imagebrowser.MainActivity;
import com.yyf.imagebrowser.R;
import com.yyf.imagebrowser.adapter.SortByStarLvAdapter;
import com.yyf.imagebrowser.entity.ImageEntity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class FgmtSortByStar extends Fragment {
	
	private String TAG = "FgmtSortByStarUp";
	private static final byte SCAN_OK = 9;
	private boolean isUp;
	
	private ListView listview;
	private ProgressBar pb;
	private Map<Integer, ArrayList<ImageEntity>> map;
	private Map<String, ArrayList<ImageEntity>> m;
	private SortByStarLvAdapter adapter;
	private ArrayList<Integer> sortList;
	
	
	public FgmtSortByStar(boolean isUp) {
		m = MainActivity.map;
		this.isUp = isUp;
	}

	private void prepareImages() {
		if(m == null){
			Toast.makeText(getContext(), "没有图片数据", Toast.LENGTH_LONG).show();
			return;
		}
		// step1,取出所有图片数据。
		ArrayList<ImageEntity> list = new ArrayList<>();
		Set<String> keySet = m.keySet();
		Iterator<String> ite = keySet.iterator();
		while(ite.hasNext()){
			list.addAll(m.get(ite.next()));
		}
//		//step2,查询星级。
//		for(int i = 0; i < list.size(); i++){
//			list.get(i).setStarLevel(ds.getStarLevel(list.get(i).getPath()));
//		}
		//step3,组装成Map对象。
		map = new TreeMap<>();
		ArrayList<ImageEntity> level0 = new ArrayList<>();
		ArrayList<ImageEntity> level1 = new ArrayList<>();
		ArrayList<ImageEntity> level2 = new ArrayList<>();
		ArrayList<ImageEntity> level3 = new ArrayList<>();
		ArrayList<ImageEntity> level4 = new ArrayList<>();
		ArrayList<ImageEntity> level5 = new ArrayList<>();
		for(int idx = 0; idx < list.size(); idx++){
			switch(list.get(idx).getStarLevel()){
				case 0:{
					level0.add(list.get(idx));
				}break;
				case 1:{
					level1.add(list.get(idx));
				}break;
				case 2:{
					level2.add(list.get(idx));
				}break;
				case 3:{
					level3.add(list.get(idx));
				}break;
				case 4:{
					level4.add(list.get(idx));
				}break;
				case 5:{
					level5.add(list.get(idx));
				}break;
			}
		}
		
		if(level0.size()>0){
			map.put(0, level0);
			Log.d(TAG, "level0 size="+level0.size());
		}
		if(level1.size()>0){
			map.put(1, level1);
			Log.d(TAG, "level1 size="+level1.size());
		}
		if(level2.size()>0){
			map.put(2, level2);
			Log.d(TAG, "level2 size="+level2.size());
		}
		if(level3.size()>0){
			map.put(3, level3);
			Log.d(TAG, "level3 size="+level3.size());
		}
		if(level4.size()>0){
			map.put(4, level4);
			Log.d(TAG, "level4 size="+level4.size());
		}
		if(level5.size()>0){
			map.put(5, level5);
			Log.d(TAG, "level5 size="+level5.size());
		}
		Log.d(TAG, "map size="+map.size());
		//
		addDataToSortList();
	}
	
	private void addDataToSortList(){
		sortList = new ArrayList<>();
		ArrayList<Integer> list = new ArrayList<>();
		Iterator<Integer> ite = map.keySet().iterator();
		
		while(ite.hasNext()){
			list.add(ite.next());
		}
		
		if(isUp){
			sortList.addAll(list);
		}else{
			for(byte idx = 0; idx < list.size(); idx++){
				sortList.add(list.get(list.size() - 1 - idx));
			}
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.listview_progressbar, null);
		listview = (ListView) view.findViewById(R.id.lvLvWithPb);
		pb = (ProgressBar) view.findViewById(R.id.pbLvWithPb);
		
		new Thread(){
			
			public void run() {
				prepareImages();
				handler.sendEmptyMessageDelayed(SCAN_OK, 724);
			};
			
		}.start();
		
		return view;
	}
	
	private Handler handler = new Handler(){
		@Override // 扫描完成之后，发送的消息被handleMessage接收
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK :
				adapter = new SortByStarLvAdapter(getContext(), sortList, map);
				listview.setAdapter(adapter);
				
				pb.setVisibility(View.GONE);
				listview.setVisibility(View.VISIBLE);
				break;
			}
		}
	};

}
