package com.yyf.imagebrowser.adapter;

import java.util.ArrayList;
import java.util.HashSet;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yyf.imagebrowser.MainActivity;
import com.yyf.imagebrowser.entity.ImageEntity;
import com.yyf.imagebrowser.tools.IBApplication;
import com.yyf.imagebrowser.view.ThumbnailGallery;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * 用于ThumbnailGallery的适配器。
 * */
public class ImageShowBaseAdapter extends BaseAdapter {

	private ArrayList<ImageEntity> list;
	private HashSet<Integer> oneTimeSet;
	private Context context;
	private int listSize;
	private ImageView iv;
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			iv = new ImageView(context);
			iv.setLayoutParams(new LinearLayout.LayoutParams(
					(int) (ThumbnailGallery.THUMBNAIL_WIDTH_BASE *MainActivity.density +0.5f), 
					LayoutParams.MATCH_PARENT));
			((LayoutParams)iv.getLayoutParams()).setMargins(5, 0, 5, 0);
			iv.setPadding(3, 3, 3, 3);
			iv.setBackgroundColor(ThumbnailGallery.IV_DEFAULT_BG_COLOR);
			convertView = iv;
		}else {
			iv = (ImageView) convertView;
		}
		//单次加载检测。
		if(oneTimeSet.contains(position)){
			Log.v("mylog", "ImageShowBaseAdapter - This position was loadded.");
			return convertView;
		}else{
			//Load image.
			ImageLoader.getInstance().displayImage("file://"+getItem(position).getPath(), iv, 
					IBApplication.getInstance().getDisplayImageOptions());
			oneTimeSet.add(position);
			Log.v("mylog", "ImageShowBaseAdapter - oneTimeSet:"+position);
		}
		
		return convertView;
	}
	
	@Override
	public int getCount() {
		return listSize;
	}

	@Override
	public ImageEntity getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public ImageShowBaseAdapter(Context context, ArrayList<ImageEntity> list){
		this.context = context;
		this.list = list;
		listSize = this.list.size();
		oneTimeSet = new HashSet<>();
	}

}
