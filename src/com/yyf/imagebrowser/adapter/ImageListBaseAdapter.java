package com.yyf.imagebrowser.adapter;

import java.util.ArrayList;
import java.util.HashSet;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yyf.imagebrowser.R;
import com.yyf.imagebrowser.entity.ImageEntity;
import com.yyf.imagebrowser.entity.ViewHolder;
import com.yyf.imagebrowser.tools.IBApplication;
import com.yyf.imagebrowser.view.StarIndicator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

public class ImageListBaseAdapter extends BaseAdapter {

	private ArrayList<ImageEntity> list;
	private HashSet<Integer> oneTimeSet;
	private int collectionSize;
	private Context context;
	private ViewHolder holder;
	private boolean isFlushData;
	private boolean isCBVisible;
	
	public ImageListBaseAdapter(Context context, ArrayList<ImageEntity> listDate) {
		list = listDate;
		this.context = context;
		collectionSize = list.size();
		oneTimeSet = new HashSet<>();
		Log.v("mylog", "collectionSize="+collectionSize);
	}
	
	/**
	 * 操作CheckBox的显示或隐藏的方法。
	 * */
	public void switchCheckBoxState(boolean isMultiSelectMode) {
		//当前已进入多选模式。
		isCBVisible = isMultiSelectMode;
		//因为CheckBox的可见状态发生了改变，故而刷新一次。
		notifyViewsChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.imageview, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.ivUseToShowImg);
			holder.starIndicator = (StarIndicator) convertView.findViewById(R.id.starIndicator);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.cbImgShow);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(!isFlushData && oneTimeSet.contains(position)){
			Log.v("mylog", "ImageListBaseAdapter - current pos was loadded.");
			return convertView;
		}else {
			//load the specified image.
			//if(!isFlushData)//加上这个条件判断是为了避免重复加载图片造成不必要的性能消耗。
				ImageLoader.getInstance().displayImage("file://"+getItem(position).getPath(), 
					holder.iv, 
					IBApplication.getInstance().getDisplayImageOptions());
			//load current image's star level. 
			//当当前图片没有被用户主动评分时则显示默认星级，默认星级为2星。
			holder.starIndicator.setLevel((byte)getItem(position).getStarLevel());
			//setup the CheckBox's visible state.
			if(isCBVisible){
				//没办法了，只能给每个CheckBox注册选中状态改变监听器。
				holder.checkBox.setOnCheckedChangeListener(new CBCheckChangeListener(position));
				holder.checkBox.setVisibility(View.VISIBLE);
				//只需要在CheckBox处于可见状态时设置它的选中状态即可。
				holder.checkBox.setChecked(getItem(position).getIsSelected());
			}
			else{
				holder.checkBox.setVisibility(View.INVISIBLE);
				
			}
			//Mark that current position's view was loaded.
			oneTimeSet.add(position);
		}
		
		return convertView;
	}
	
	public void notifyViewsChanged() {
		isFlushData = true;
		super.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return collectionSize;
	}

	@Override
	public ImageEntity getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * CheckBox的选中状态改变监听器。
	 * */
	private class CBCheckChangeListener implements OnCheckedChangeListener {

		private int pos;
		
		public CBCheckChangeListener(int p) {
			pos = p;
		}
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			getItem(pos).setIsSelected(isChecked);
		}
		
	}

}
