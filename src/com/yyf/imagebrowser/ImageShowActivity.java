package com.yyf.imagebrowser;

import java.util.ArrayList;

import com.yyf.imagebrowser.adapter.ImageShowBaseAdapter;
import com.yyf.imagebrowser.adapter.ImageShowPagerAdapter;
import com.yyf.imagebrowser.adapter.ImageShowPagerAdapter.ViewHolder;
import com.yyf.imagebrowser.entity.ImageEntity;
import com.yyf.imagebrowser.fragment.FgmtSortByFolder;
import com.yyf.imagebrowser.photoview.HackyViewPager;
import com.yyf.imagebrowser.photoview.PhotoViewAttacher;
import com.yyf.imagebrowser.photoview.PhotoViewAttacher.TouchEventCallback;
import com.yyf.imagebrowser.tools.DataStorage;
import com.yyf.imagebrowser.view.ThumbnailGallery;
import com.yyf.imagebrowser.view.ThumbnailGallery.OnImgChangeListener;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class ImageShowActivity extends BaseActivity {

	private ArrayList<ImageEntity> list;
	private HackyViewPager mViewPager;
	private FrameLayout frameLayout;
	private ThumbnailGallery thumbnailGallery;
	private ImageShowBaseAdapter ISBAdapter;
	private ImageShowPagerAdapter adapter;
	private int position;
	private DataStorage ds;
	/**记录当前是否全屏状态的标志位。<br/>默认是非全屏状态，即自定义画廊与星级指示器处于可见状态。*/
	private boolean isFullScreen;
	private ViewHolder ispHolder;
	
	//一个用于标志显示/隐藏视图操作是否在队列中的标志位。
	private boolean isGoing;
	
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
			adapter.getViewHodler(position).starIndicator.setVisibility(View.VISIBLE);
			isFullScreen = false;
		}else {
			thumbnailGallery.setVisibility(View.INVISIBLE);
			adapter.getViewHodler(position).starIndicator.setVisibility(View.INVISIBLE);
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
			adapter.destroyItem(mViewPager, position+1, null);
			adapter.instantiateItem(mViewPager, position+1);
		}
		//这两条if语句可不能合并为一条哦，否则会发生意想不到的意外的。
		if(position!=0){
			adapter.destroyItem(mViewPager, position-1, null);
			adapter.instantiateItem(mViewPager, position-1);
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
		mViewPager.setAdapter(adapter);
		//
		thumbnailGallery.setAdapter(ISBAdapter);
		thumbnailGallery.initSelectedImg(position+1);
		
	}
	
	/**
	 * 切换ViewPager中当前显示的页面。<br/>
	 * 主要用于实现ViewPager与ThumbnailGallery中显示的图片的同步。
	 * */
	private void switchViewPagerImg(int position){
		mViewPager.setCurrentItem(position-1);
	}

	/**
	 * 做一些初始化操作。
	 * 提取相应图片集合数据，创建帧布局添加到母容器中。
	 * */
	private void init(){
		
		setBtnVisible(BTN_MENU, false);
		
		ds = DataStorage.getInstance();
		
		position = getIntent().getIntExtra("pos", 0);
		// Get image collection object.
		list = FgmtSortByFolder.getInstance().getDataList(); //如果这个东东返回的是null则说明手机内存不够用，把手机扔掉吧。
		setTitle(list.get(position).getFileName());
		Log.d("mylog", "ImageShowActivity - path:"+list.get(position).getPath()+"  pos:"+position);
		frameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.img_show_layout, null);
		mViewPager = (HackyViewPager) frameLayout.findViewById(R.id.hackyViewPager1);
		//给ViewPager注册一个自定义的触摸事件，这非常重要。
		mViewPager.addOnPageChangeListener(new MyOnPageChangeListener());
		thumbnailGallery = (ThumbnailGallery) frameLayout.findViewById(R.id.thumbnailGalleryImgShow);
		llSelfActionbar = (LinearLayout) findViewById(R.id.llSelfActionbar);

		linearLayout.addView(frameLayout);
		
		ISBAdapter = new ImageShowBaseAdapter(this, list);
		adapter = new ImageShowPagerAdapter(this, list);
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
		
		/*
		 * mViewPager注册触摸回调。
		 * */
		PhotoViewAttacher.setTouchCallback(new TouchEventCallback() {
			
			@Override
			public void onTouchEventCallback(byte mode) {
				ispHolder = adapter.getViewHodler(position);
				switch(mode){
					case DOWN:{
						ispHolder.starIndicator.minOneLevel();
						ds.setStarLevel(list.get(position).getPath(), ispHolder.starIndicator.getCurrentLevel());
						list.get(position).setStarLevel(ispHolder.starIndicator.getCurrentLevel());
					}break;
					case UP:{
						ispHolder.starIndicator.addOneLevel();
						ds.setStarLevel(list.get(position).getPath(), ispHolder.starIndicator.getCurrentLevel());
						list.get(position).setStarLevel(ispHolder.starIndicator.getCurrentLevel());
					}break;
					case CLICK:{
						if(isGoing){
							handler.removeMessages(9);
							isGoing = false;
						}else {
							isGoing = true;
							handler.sendEmptyMessageDelayed(9, 712);
						}
					}break;
				}
			}
		});
	}// init()  --  end.
	
	private Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			Log.d("mylog", "qie huan zhong......");
			fullScreenStateSwitch();
			isGoing = false;
		};
	};
	
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
