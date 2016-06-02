package com.yyf.imagebrowser.adapter;

import java.util.ArrayList;
import java.util.Map;

import com.yyf.imagebrowser.R;
import com.yyf.imagebrowser.entity.ImageEntity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

/**
 * 图像的构建全在这儿。
 */
public class SortByTimeLvAdapter extends BaseAdapter {

	protected LayoutInflater mInflater;
	private ArrayList<String> keyList;
	private Map<String, ArrayList<ImageEntity>> map;
	private SortByTimeGvInLvAdapter gvAdapter;
	private Context context;

	public SortByTimeLvAdapter(Context context, ArrayList<String> reverseList,
			Map<String, ArrayList<ImageEntity>> map) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		keyList = reverseList;
		this.map = map;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.v("mylog", "sortbytimelineadapter.getview."+position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sortbytimeline_lvitem, null);
			holder = new ViewHolder();
			holder.mTextViewTitle = (TextView) convertView.findViewById(R.id.date);
			holder.mGrid = (GridView) convertView.findViewById(R.id.mGrid);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		//  time.
		holder.mTextViewTitle.setText(getItem(position));
		//gridview's adapter.
		gvAdapter = new SortByTimeGvInLvAdapter(context, map.get(getItem(position)), holder.mGrid);
		holder.mGrid.setAdapter(gvAdapter);

		return convertView;
	}

	public static class ViewHolder {

		public TextView mTextViewTitle;
		public GridView mGrid;
	}

	@Override
	public int getCount() {
		return keyList.size();
	}

	@Override
	public String getItem(int position) {
		return keyList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
