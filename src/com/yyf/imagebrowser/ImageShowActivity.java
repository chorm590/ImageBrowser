package com.yyf.imagebrowser;

import java.util.ArrayList;

import com.yyf.imagebrowser.adapter.ImageShowBaseAdapter;
import com.yyf.imagebrowser.adapter.ImageShowPagerAdapter;
import com.yyf.imagebrowser.adapter.ImageShowPagerAdapter.ViewHolder;
import com.yyf.imagebrowser.entity.ImageEntity;
import com.yyf.imagebrowser.fragment.FgmtSortByFolder;
import com.yyf.imagebrowser.tools.DataStorage;
import com.yyf.imagebrowser.view.ThumbnailGallery;
import com.yyf.imagebrowser.view.ThumbnailGallery.OnImgChangeListener;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class ImageShowActivity extends BaseActivity {

	private ArrayList<ImageEntity> list;
	private ViewPager viewPager;
	private FrameLayout frameLayout;
	private ThumbnailGallery thumbnailGallery;
	private ImageShowBaseAdapter ISBAdapter;
	private ImageShowPagerAdapter ISPAdaper;
	private int position;
	private DataStorage ds;
	/**记录当前是否全屏状态的标志位。<br/>默认是非全屏状态，即自定义画廊与星级指示器处于可见状态。*/
	private boolean isFullScreen;
	private ViewHolder ispHolder;
	
	private PointF midPointF;
	private PointF startPointF;
	private Matrix curMatrix;
	private Matrix matrix;
	private int baseDis;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
//		这里要加ViewPager组件实现滑动浏览效果。
		enableViews();
	}
	
	/**
	 * 受到点击式触摸的召唤，要把ThumbnailGallery与StarIndicator的
	 * 显示或隐藏起来。
	 * */
	private void fullScreenStateSwitch(){
		//若当前是全屏显示状态，则切换为非全屏状态。
		if(isFullScreen){
			thumbnailGallery.setVisibility(View.VISIBLE);
			ISPAdaper.getViewHodler(position).starIndicator.setVisibility(View.VISIBLE);
			isFullScreen = false;
		}else {
			thumbnailGallery.setVisibility(View.INVISIBLE);
			ISPAdaper.getViewHodler(position).starIndicator.setVisibility(View.INVISIBLE);
			isFullScreen = true;
		}// else  --  end.
		
		/*
		 * 对下面这段代码的解释：
		 * 		本方法实现的功能就是显示/隐藏ThumbnailGallery与StarIndicator用的。
		 * 那么又由于图片的左、右滑动展示是由ViewPager来实现的。ViewPager的特性
		 * 就是预加载当前显示页的前一页与后一页到内存中。那么如果此时在当前显示的页面
		 * 中更改了这些组件的显示状态，那么该最新的显示状态将不会被应用在之前已加载
		 * 到内存中的前一页与后一页中。而下面这段代码正是用于在更改了自定义画廊与
		 * 星级指示器后对前一页与后一页#重新加载#一次以使最新的组件状态得以应用。
		 * */
		if(position!=list.size()-1){
			ISPAdaper.destroyItem(viewPager, position+1, null);
			ISPAdaper.instantiateItem(viewPager, position+1);
		}
		//这两条if语句可不能合并为一条哦，否则会发生意想不到的意外的。
		if(position!=0){
			ISPAdaper.destroyItem(viewPager, position-1, null);
			ISPAdaper.instantiateItem(viewPager, position-1);
		}
	}// fullScreenStateSwitch()  --  end.
	
	public boolean getIsFullScreen(){
		return isFullScreen;
	}

	/**
	 * 为Views的显示作最后的组装。
	 * */
	private void enableViews() {
		//先给ViewPager设置适配器。
		viewPager.setAdapter(ISPAdaper);
		//
		thumbnailGallery.setAdapter(ISBAdapter);
		thumbnailGallery.initSelectedImg(position+1);
		
	}
	
	/**
	 * 切换ViewPager中当前显示的页面。<br/>
	 * 主要用于实现ViewPager与ThumbnailGallery中显示的图片的同步。
	 * */
	private void switchViewPagerImg(int position){
		viewPager.setCurrentItem(position-1);
	}

	/**
	 * 做一些初始化操作。
	 * 提取相应图片集合数据，创建帧布局添加到母容器中。
	 * */
	private void init(){
		ds = DataStorage.getInstance();
		
		startPointF = new PointF();
		midPointF = new PointF();
		curMatrix = new Matrix();
		matrix = new Matrix();
		
		position = getIntent().getIntExtra("pos", 0);
		// Get image collection object.
		list = FgmtSortByFolder.getInstance().getDataList(); //如果这个东东返回的是null则说明手机内存不够用，把手机扔掉吧。
		setTitle(list.get(position).getFileName());
		Log.d("mylog", "ImageShowActivity - path:"+list.get(position).getPath()+"  pos:"+position);
		frameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.img_show_layout, null);
		viewPager = (ViewPager) frameLayout.findViewById(R.id.viewPagerImgShow);
		//给ViewPager注册一个自定义的触摸事件，这非常重要。
		viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
		//给ViewPager注册触摸事件监听。
		viewPager.setOnTouchListener(new MyViewPagerOnTouchListener());
		thumbnailGallery = (ThumbnailGallery) frameLayout.findViewById(R.id.thumbnailGalleryImgShow);
		llSelfActionbar = (LinearLayout) findViewById(R.id.llSelfActionbar);

		linearLayout.addView(frameLayout);
		
		ISBAdapter = new ImageShowBaseAdapter(this, list);
		ISPAdaper = new ImageShowPagerAdapter(this, list);
		/*
		 * 注册画廊当前选择图片改变回调监听器。
		 * 当选择的图片发生改变时需要改变ActionBar的标题栏与ViewPager中的显示图片。
		 * */
		thumbnailGallery.setOnImgChangeListener(new OnImgChangeListener() {
			
			@Override
			public void onImgChange(boolean isFromUser, int position) {
				setTitle(list.get(position-1).getFileName());
				ImageShowActivity.this.position = position-1;//自定义画廊中图片是从1开始编号的。
				Log.d("mylog", "the position="+ImageShowActivity.this.position);
				if(isFromUser){
					switchViewPagerImg(position);
				}
			}
		});
	}// init()  --  end.
	
	/**
	 * ViewPager的触摸事件响应方法。主要用于页面切换与增、减星级等。
	 * */
	private class MyViewPagerOnTouchListener implements OnTouchListener {

		float xStart, xDistance;
		float yStart, yDistance;
		private final byte MODE_DRAG = 7;
		private final byte MODE_ZOOM = 77;
		/**拖拉模式或缩放模式的标志位。*/
		private byte mode;
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			 switch(event.getAction()&MotionEvent.ACTION_MASK){
			 	case MotionEvent.ACTION_DOWN:{
			 		Log.d("mylog", "vp down.");
			 		xStart = event.getX();
			 		yStart = event.getY();
			 		startPointF.x = event.getX();
			 		startPointF.y = event.getY();
			 		//
			 		ispHolder = ISPAdaper.getViewHodler(position);
			 		mode = MODE_DRAG;
			 		//获取图片当前的矩阵。
			 		curMatrix.set(ispHolder.iv.getImageMatrix());
			 		
			 	}break;
			 	case MotionEvent.ACTION_POINTER_DOWN:{
			 		Log.d("mylog", "vp pointer down.");
			 		mode = MODE_ZOOM;
			 		//计算两指的中点。
			 		float x = event.getX(1) + event.getX(0);
			 		float y = event.getY(1) + event.getY(0);
			 		midPointF.set(x/2, y/2);
			 	}break;
			 	case MotionEvent.ACTION_MOVE:{
			 		Log.v("mylog", "vp move.");
			 		if(mode == MODE_ZOOM){
			 			Log.d("mylog", "zoom the image.");
			 			//防止触点越界导致程序报错。
			 			if(event.getPointerCount()!=2){
			 				mode = 0;
			 				break;
			 			}
			 			float xdis = event.getX(1) - event.getX(0);
		 				float ydis = event.getY(1) - event.getY(0);
			 			if(baseDis==0){
			 				baseDis = (int) (xdis*xdis+ydis*ydis);
			 				Log.i("mylog", "baseDis="+baseDis);
			 			}else{
			 				float scale = (xdis*xdis+ydis*ydis)/baseDis;
			 				// 缩放因子的有效范围是：0.5~2.5
			 				scale = scale <= 0.6f ? 0.6f : scale;
			 				scale = scale >= 2.5f ? 2.5f : scale;
			 				Log.d("mylog", "scale="+scale);
			 				matrix.set(curMatrix);
			 				matrix.postScale(scale, scale, midPointF.x, midPointF.y);
			 				ispHolder.iv.setImageMatrix(matrix);
			 			}
			 		// 缩放处理代码结束。
			 		} else if(mode == MODE_DRAG){
			 			Log.d("mylog", "drag the image.");
			 			//这个功能实在不想做了。暂时先这样吧。
			 			
			 		// 拖曳处理代码结束。
			 		}//else  --  end.
			 	}break;
			 	case MotionEvent.ACTION_POINTER_UP:{
			 		Log.d("mylog", "pointer up.");
			 		//要把用于缩放的两指原始距离置位，否则会无法那个。
			 		baseDis = 0;
			 	}break;
			 	case MotionEvent.ACTION_UP:{
			 		Log.d("mylog", "vp up.");
			 		//当我缩放过，我就不希望你再来更改我的图片的星级了。
			 		if(mode == MODE_ZOOM){
			 			mode = 0;
			 			ispHolder = null;
			 			break;
			 		}
			 		xDistance = event.getX()-xStart;
			 		yDistance = event.getY()-yStart;
			 		Log.v("mylog", "ImageShowActivity - yDistance:"+yDistance);
			 		/*
			 		 * 这里进行左右滑动或是上下滑动的动作判断。
			 		 * 左右滑动的优先级高于上下滑动。不检测斜向滑动。
			 		 * */
			 		if(xDistance*xDistance>40000){ //当左右滑动的距离超过200像素点时即判断为左右方向发生了滑动。
			 			//把该触摸事件交给ViewPager去处理，让该动作触发页面的切换。
			 			Log.d("mylog", "left and right slide the screen.");
			 			return false;
			 		}
			 		
			 		if(yDistance>139){ //移动距离为正，发生向下滑动。
			 			Log.i("mylog", "down slide. minuts a star.");
			 			ispHolder.starIndicator.minOneLevel();
			 			ds.setStarLevel(list.get(viewPager.getCurrentItem()).getPath(), 
			 					ispHolder.starIndicator.getCurrentLevel());
			 			//2016.3.4 .
			 			list.get(position).setStarLevel(ispHolder.starIndicator.getCurrentLevel());
			 			return true;
			 		}else if(yDistance<-150){ //否则，则说明发生了向上的滑动。
			 			Log.i("mylog", "up slide, add a star.");
			 			ispHolder.starIndicator.addOneLevel();
			 			ds.setStarLevel(list.get(viewPager.getCurrentItem()).getPath(), 
			 					ispHolder.starIndicator.getCurrentLevel());
			 			//2016.3.4 .
			 			list.get(position).setStarLevel(ispHolder.starIndicator.getCurrentLevel());
			 			return true;
			 		}
			 		
			 		//判断点击事件。
			 		if(xDistance*xDistance<100 && yDistance*yDistance<64){
			 			Log.d("mylog", "ViewPager - You click the screen.");
			 			fullScreenStateSwitch();
			 			return true;
			 		}
			 		//模式置位.
			 		mode = 0;
			 		//
			 		ispHolder = null;
			 	}//action_up  --  end.
			 } //switch  --  end.
			 
			return false;
		}
		
	}// inner class  --  end.
	
	/**
	 * ViewPager页面切换时触发的事件响应方法。
	 * */
	private class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int arg0) {
			thumbnailGallery.setSelectedImg(arg0+1);
		}
		
	}
	
}
