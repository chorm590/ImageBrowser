package com.yyf.imagebrowser.adapter;

import java.util.ArrayList;
import java.util.Map;

import com.yyf.imagebrowser.R;
import com.yyf.imagebrowser.entity.ImageEntity;
import com.yyf.imagebrowser.view.MyGridview;
import com.yyf.imagebrowser.view.StarIndicator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class SortByStarLvAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<Integer> list;
	private Map<Integer, ArrayList<ImageEntity>> map;
	private SortByStarGvInLvAdapter adapter;
	
	public SortByStarLvAdapter(Context context, ArrayList<Integer> sortList, Map<Integer, ArrayList<ImageEntity>> map) {
		this.context = context;
		list = sortList;
		this.map = map;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Integer getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.sortbystar_lv_layout, null);
			holder.tvHidden = (TextView) convertView.findViewById(R.id.tvSortByStar);
			holder.si = (StarIndicator) convertView.findViewById(R.id.siSortByStar);
			holder.gv = (MyGridview) convertView.findViewById(R.id.mygv);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		int level = getItem(position);
		holder.tvHidden.setText(String.valueOf(level));
		holder.si.setLevel((byte) level);
		setupContents(holder.gv, map.get(getItem(position)));
		
		return convertView;
	}
	
	private void setupContents(GridView gv, ArrayList<ImageEntity> arrayList) {
		adapter = new SortByStarGvInLvAdapter(context, arrayList);
		gv.setAdapter(adapter);
	}

	private class ViewHolder {
		TextView tvHidden;
		StarIndicator si;
		MyGridview gv;
	}

}
