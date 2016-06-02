package com.yyf.imagebrowser.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yyf.imagebrowser.R;
import com.yyf.imagebrowser.entity.ImageEntity;
import com.yyf.imagebrowser.tools.IBApplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;

public class SortByTimeGvInLvAdapter extends BaseAdapter {
	protected LayoutInflater mInflater;
//	private GridView mGridView;
	private List<ImageEntity> list;

	public SortByTimeGvInLvAdapter(Context cn, List<ImageEntity> list, GridView g) {
		this.list = list;
//		this.mGridView = g;
		this.mInflater = LayoutInflater.from(cn);

	}

	@Override
	public int getCount() {

		return list.size();
	}

	@Override
	public ImageEntity getItem(int position) {

		return list.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.v("mylog", "sortbytimegvinlvadapter.getview."+position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.imageview_checkbox, null);
			holder = new ViewHolder();
			holder.mImageView = (ImageView) convertView.findViewById(R.id.ivIvWithCb);
			holder.cb = (CheckBox) convertView.findViewById(R.id.cbIvWithCb);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		ImageLoader.getInstance().displayImage("file://"+getItem(position).getPath(), holder.mImageView,
				IBApplication.getInstance().getDisplayImageOptions());
		holder.cb.setVisibility(View.GONE);

		return convertView;
	}

	public static class ViewHolder {
		private ImageView mImageView;
		private CheckBox cb;
	}

}
