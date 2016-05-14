package com.yyf.imagebrowser.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yyf.imagebrowser.R;
import com.yyf.imagebrowser.entity.ImageEntity;
import com.yyf.imagebrowser.entity.ViewHolder;
import com.yyf.imagebrowser.tools.IBApplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 以文件夹分类的适配器类。
 * */
public class SortByFolderAadapter extends BaseAdapter {

	private Map<String, ArrayList<ImageEntity>> map;
	/**用于存放Map集合中的键，该List集合主要用于getItem(int)方法*/
	private List<String> mapKeyList;
	/**用于使getView方法部分内容只执行一次的集合。*/
	private HashSet<Integer> oneTimeSet;
	private int mapSize; //map集合的元素数量。
	private Context context;
	private ViewHolder holder;
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = View.inflate(context, R.layout.sort_by_folder, null);
			holder = new ViewHolder();
			holder.iv = (ImageView) convertView.findViewById(R.id.ivSortByFolder);
			holder.tvAmount = (TextView) convertView.findViewById(R.id.tvImageAmount);
			holder.tvFolderName = (TextView) convertView.findViewById(R.id.tvFolderName);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(oneTimeSet.contains(position)){
//			Log.v("mylog", "SortByFolderAdapter - current pos was loaded. "+position);
		}else{
			//设置该文件夹的封面图
			loadImage(holder.iv, position);
			//设置该分文件夹下图片数量。
			holder.tvAmount.setText(String.valueOf(getItem(position).size()));
			//设置该文件夹名称。
			holder.tvFolderName.setText(mapKeyList.get(position));
			//当前是第一次加载该条数据，记录已加载过该条项目，防止重复加载浪费性能。
			oneTimeSet.add(position);
		}
		
		return convertView;
	}
	
	/**
	 * 装载本地的图片。
	 * */
	public void loadImage(ImageView iv, int pos){
		String uri = "file://"+getItem(pos).get(0).getPath(); //取集合中第一张图片作为封面图。
		ImageLoader.getInstance().displayImage(uri, iv, IBApplication.getInstance().getDisplayImageOptions());
//		Log.d("mylog", "SortByFolderAdapter - loadImageFinished.");
	}
	
	@Override
	public int getCount() {
		return mapSize;
	}

	@Override
	public ArrayList<ImageEntity> getItem(int position) {
		return map.get(mapKeyList.get(position));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public SortByFolderAadapter(Context context, Map<String, ArrayList<ImageEntity>> map){
		this.map = map;
		this.context = context;
		mapSize = this.map.size();
		mapKeyList = new ArrayList<>(map.keySet());
		oneTimeSet = new HashSet<>();
	}

}
