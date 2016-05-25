package com.yyf.imagebrowser.adapter;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yyf.imagebrowser.ImageShowActivity;
import com.yyf.imagebrowser.R;
import com.yyf.imagebrowser.entity.ImageEntity;
import com.yyf.imagebrowser.photoview.PhotoView;
import com.yyf.imagebrowser.tools.IBApplication;
import com.yyf.imagebrowser.view.StarIndicator;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

/**
 * 用于ViewPager中显示的适配器。
 * */
public class ImageShowPagerAdapter extends PagerAdapter {

	private ArrayList<ImageEntity> list;
	private ArrayList<ViewHolder> viewList;
	private ImageShowActivity context;
	private int listSize;
	
	private void combineView(){
		
	}
	
	/**
	 * 获取指定的View。
	 * */
	public ViewHolder getViewHodler(int position){
		return viewList.get(position);
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(viewList.get(position).fl);
		Log.d("mylog", "destroyItem."+position);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		
		Log.d("mylog", "ImageShowPagerAdapter -  pos:"+position);
		container.addView(viewList.get(position).fl);
		ImageLoader.getInstance().displayImage("file://"+list.get(position).getPath(), viewList.get(position).iv, 
				IBApplication.getInstance().getDisplayImageOptions());
		viewList.get(position).starIndicator.setLevel((byte)list.get(position).getStarLevel());
		//根据是否全屏状态设置显示状态。
		if(context.getIsFullScreen()){
			viewList.get(position).starIndicator.setVisibility(View.INVISIBLE);
		}else {
			viewList.get(position).starIndicator.setVisibility(View.VISIBLE);
		}
		
		return viewList.get(position).fl;
	}// instantiateItem  --  end.
	
	@Override
	public int getCount() {
		return listSize;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==arg1;
	}
	
	public ImageShowPagerAdapter(ImageShowActivity context , ArrayList<ImageEntity> list){
		this.list = list;
		this.context = context;
		listSize = this.list.size();
		ViewHolder vh=null;
		viewList = new ArrayList<>();
		for(int i=0;i<listSize;i++){
			vh = new ViewHolder();
			vh.fl = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.img_show_framelayout, null);
			vh.iv = (PhotoView) vh.fl.findViewById(R.id.photoview1);
//			vh.pb = (ProgressBar) vh.fl.findViewById(R.id.pbImgShowFL);
			vh.starIndicator = (StarIndicator) vh.fl.findViewById(R.id.starIndicatorImgShow);
			viewList.add(vh);
		}

		
		combineView();
	}
	
	public class ViewHolder {
		public FrameLayout fl;
		public PhotoView iv;
		public ProgressBar pb;
		public StarIndicator starIndicator;
	}

}
