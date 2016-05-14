package com.yyf.imagebrowser.adapter;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yyf.imagebrowser.ImageShowActivity;
import com.yyf.imagebrowser.R;
import com.yyf.imagebrowser.entity.ImageEntity;
import com.yyf.imagebrowser.tools.IBApplication;
import com.yyf.imagebrowser.view.StarIndicator;

import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * 用于ViewPager中显示的适配器。
 * */
public class ImageShowPagerAdapter extends PagerAdapter {

	private ArrayList<ImageEntity> list;
	private ArrayList<ViewHolder> viewList;
	private ImageShowActivity context;
	private int listSize;
	
	private static int ivWidth, ivHeight;
	private static int imgWidth, imgHeight;
	private static int drawWidth, drawHeight;
	private static Matrix ivMatrix;
	
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
		//让图片居中适配屏幕尺寸显示
		adaptImg(viewList.get(position).iv);
		
		return viewList.get(position).fl;
	}// instantiateItem  --  end.
	
	private void adaptImg(final ImageView iv){
		iv.post(new Runnable() {
			
			@Override
			public void run() {
				iv.measure(0, 0);
				ivWidth = iv.getWidth();
				ivHeight = iv.getHeight();
				imgWidth = iv.getDrawable().getBounds().width();
				imgHeight = iv.getDrawable().getBounds().height();
				Log.v ("mylog", "ivWidth:"+ivWidth+" ivHeight:"+ivHeight+
						" imgWidth:"+imgWidth+" imgHeight:"+imgHeight);
				// Initialize ivMatirx.
				ivMatrix = iv.getImageMatrix();
				Log.i("mylog", "ivMatrix:"+iv);
				// Make the image adapt screen.
				float scaleWidth = (float)ivWidth / (float)imgWidth;
				float scaleHeight = (float)ivHeight / (float)imgHeight;
				if(scaleWidth * imgHeight <= ivHeight){
					ivMatrix.setScale(scaleWidth, scaleWidth);
					drawWidth = (int) (scaleWidth*imgWidth);
//					drawWidth = ivWidth;
					drawHeight = (int) (scaleWidth*imgHeight);
				}else {
					ivMatrix.setScale(scaleHeight, scaleHeight);
					drawWidth = (int) (scaleHeight * imgWidth);
					drawHeight = (int) (scaleHeight * imgHeight);
//					drawHeight = ivHeight;
				}
				Log.v("mylog", "drawWidth="+drawWidth+" drawHeight="+drawHeight);
				// Center.
				float dx = (ivWidth - drawWidth) / 2.0f;
				float dy = (ivHeight - drawHeight) / 2.0f;
				Log.v("mylog", "Translate X="+dx+" Translate Y="+dy);
				ivMatrix.postTranslate(dx, dy);
				// Commit setup.
				iv.setImageMatrix(ivMatrix);
			}//run  --  end.
		});
	}// adaptImg  --  end.

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
			vh.iv = (ImageView) vh.fl.findViewById(R.id.ivImgShowFL);
//			vh.pb = (ProgressBar) vh.fl.findViewById(R.id.pbImgShowFL);
			vh.starIndicator = (StarIndicator) vh.fl.findViewById(R.id.starIndicatorImgShow);
			viewList.add(vh);
		}

		
		combineView();
	}
	
	public class ViewHolder {
		public FrameLayout fl;
		public ImageView iv;
		public ProgressBar pb;
		public StarIndicator starIndicator;
	}

}
