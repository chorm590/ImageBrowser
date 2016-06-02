package com.yyf.imagebrowser.fragment;

import java.util.ArrayList;
import java.util.Map;

import com.yyf.imagebrowser.R;
import com.yyf.imagebrowser.adapter.SortByTimeLvAdapter;
import com.yyf.imagebrowser.entity.ImageEntity;
import com.yyf.imagebrowser.tools.ScanImage;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

public class FgmtSortByTimeLine extends Fragment {

	private ListView listview;
	private ProgressBar pb;
	private SortByTimeLvAdapter adapter;
	private ScanImage si;
	private Map<String, ArrayList<ImageEntity>> map;
	private ArrayList<String> reverseList;
	
	
	private static final byte SCAN_OK = 99;
	
	public FgmtSortByTimeLine(ContentResolver contentResolver) {
		si = new ScanImage(contentResolver);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.listview_progressbar, null);
		listview = (ListView) view.findViewById(R.id.lvLvWithPb);
		pb = (ProgressBar) view.findViewById(R.id.pbLvWithPb);
		
		new Thread(){
			
			public void run() {
				prepareImages(); // 获取图片信息
			};
			
		}.start();
		
		return view;
	}
	
	private void prepareImages(){
		map = si.getImagesSortByTimeLineOutter();
		reverseList = si.getReverseList();
		handler.sendEmptyMessageDelayed(SCAN_OK, 1024);
	}

	private Handler handler = new Handler(){
		@Override // 扫描完成之后，发送的消息被handleMessage接收
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK :
				adapter = new SortByTimeLvAdapter(getActivity(), reverseList, map);
				listview.setAdapter(adapter);
				
				pb.setVisibility(View.GONE);
				listview.setVisibility(View.VISIBLE);
				break;
			}
		}
	};

}
