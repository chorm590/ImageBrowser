package com.yyf.imagebrowser.view;

import com.yyf.imagebrowser.MainActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Scroller;

/**
 * 自己实现的显示缩略图的画廊。
 * */
public class ThumbnailGallery extends HorizontalScrollView {

	private Context context;
	private ListAdapter adapter;
	private Scroller scroller;
	/**数据元素大小*/
	private int elementsSize;
	/**用于存放适配器模板的控件*/
	private View adapterModelView;
	/**用于横向加载ImageView的线性布局。所有图像控件均添加到该容器中。*/
	private LinearLayout linearLayout;
	private OnImgChangeListener onImgChangeListener;
	private int preSelectedImg, currSelectedImg;
	private float currTouchPos;
	public static final int THUMBNAIL_WIDTH_BASE = 70;
	public static final int IV_PADDING_WIDTH = 5;
	public static final int IV_DEFAULT_BG_COLOR = Color.WHITE;
	public static final int IV_SELECTED_BG_COLOR = Color.BLUE;
	/**每张ImageView占的宽度。*/
	private int imgWidth;
	/**滚动布局的总宽度。*/
	private int totalWidth;
	private int centerXPos;
	private boolean isFromUser;
	
	/**
	 * 图像改变监听器。
	 * 当当前选择的图片发生改变时会回调该监听器。
	 * */
	public interface OnImgChangeListener {
		abstract void onImgChange(boolean isFromUser, int position);
	}
	
	public void setOnImgChangeListener(OnImgChangeListener listener){
		onImgChangeListener = listener;
	}
	
	/**
	 * 直接指定当前选定的图片。
	 * */
	public void setSelectedImg(int position){
		currSelectedImg = position;
		isFromUser = false;
		updateSelectedImg();
	}
	
	/**
	 * 只用于初次启动软件时设置当前应该选中显示的图片。
	 * */
	public void initSelectedImg(int pos){
		currSelectedImg = pos;
		isFromUser = true;
		updateSelectedImg();
	}
	
	/**
	 * 更新当前选择图片的标志位。
	 * */
	private void updateSelectedImg(){
		if(preSelectedImg!=currSelectedImg){
			preSelectedImg = preSelectedImg==0?1:preSelectedImg;
			switchBackground();
			preSelectedImg = currSelectedImg;
			//若当前已注册图像改变监听器，则调用回调方法。
			if(onImgChangeListener!=null)
				onImgChangeListener.onImgChange(isFromUser, currSelectedImg);
			//图像发生改变，则把新选择的图像移到屏幕中间显示。
			scrollCenter();
		}
	}
	
	/**
	 * 当某张图片被选中时，为了有更好的视觉效果<br/>
	 * 需要将这张图片显示到屏幕水平中间。
	 * */
	private void scrollCenter(){
		//计算要滚动多少距离才能使得被选中的图像显示于屏幕中间。
		centerXPos = (int) ((float)imgWidth*(float)currSelectedImg - (float)imgWidth/2 - 
				MainActivity.screenWidthHalf );
		centerXPos = centerXPos<0?0:centerXPos;
		Log.d("mylog", "centerXPos="+centerXPos);
		scroller.startScroll(getScrollX(), 0, centerXPos-getScrollX(), 0, 512);
		//Start to scroll.This method will call 'computeScroll()'.
		invalidate();
	}
	
	@Override
	public void computeScroll() {
		if(scroller.computeScrollOffset()){
			scrollTo(scroller.getCurrX(), 0);
			invalidate();
		}
	}
	
	private void switchBackground(){
		linearLayout.getChildAt(preSelectedImg-1).setBackgroundColor(IV_DEFAULT_BG_COLOR);
		linearLayout.getChildAt(currSelectedImg-1).setBackgroundColor(IV_SELECTED_BG_COLOR);
	}
	
	/**
	 * 为这个自定义Gallery设置适配器。<br/>
	 * */
	public void setAdapter(ListAdapter adapter){
		this.adapter = adapter;
		elementsSize = this.adapter.getCount();
		for(short i = 0;i<elementsSize;i++){
			adapterModelView = adapter.getView(i, null, null);
			linearLayout.addView(adapterModelView);//+++这里直接把所有控件加进去不知道有没有问题。+++
		}
		//计算每张图片的宽度与总宽度。IV在此处才被显示出来，故在此之后才能得到IV的实际宽度。
		imgWidth = (int) (THUMBNAIL_WIDTH_BASE *MainActivity.density +0.5f+IV_PADDING_WIDTH *2);
		totalWidth = imgWidth*elementsSize;
		Log.d("mylog", "ThumbnailGallery - imgWidth:"+imgWidth+"  totalWidth:"+totalWidth);
	} // setAdapter  --  end.
	
	private void init(){
		linearLayout = new LinearLayout(context);
		linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT));
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		//自定义画廊的容纳ImageView的容器的背景颜色设置成透明色。
		linearLayout.setBackgroundColor(Color.alpha(0));
		//把配置好的LinearLayout对象加进画廊里去。
		addView(linearLayout);
		//Initialize Scroller.
		scroller = new Scroller(context);
		
		//设置触摸监听器。
		super.setOnTouchListener(new OnTouchListener() {
			float speed;
			float distance;
			long timeStart;
			long timeEnd;
			float xStart;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:{
						xStart = event.getX();
						timeStart = SystemClock.uptimeMillis();
					}break;
					case MotionEvent.ACTION_UP:{
						distance = event.getX() - xStart;
						//判断是单击触摸还是移动触摸。
						if(distance*distance>100){
							timeEnd = SystemClock.uptimeMillis();
							Log.v("mylog", "ThumbnailGallery - You move,not click.");
							//如果移动时间滑动时间小于512毫秒，则判定为快速滑动，触发惯性滑动。
							if((timeEnd-timeStart)<512){
								speed = distance/(timeEnd-timeStart)*-1;//乘以-1是为了把方向取个反。
								scroller.startScroll(getScrollX(), 0, (int)(speed*512), 0, 512);
								invalidate();
							}
							return false;
						}
						//计算当前选择了哪张图片。
						currTouchPos = event.getX()+getScrollX();
						currSelectedImg = (int) Math.ceil(currTouchPos/totalWidth*elementsSize);
						Log.d("mylog", "ThumbnailGallery - currSelectedImg:"+currSelectedImg);
						isFromUser = true;
						//选择了某张图片，故而需要更新选中的图片的标志位。
						updateSelectedImg();
					}break;
				}//switch  --  end.
				
				return false;
			}
		});
	}
	
	public ThumbnailGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}
	
	@Override
	public void setOnTouchListener(OnTouchListener l) {
		/*
		 * 放空这个方法，就是不想再另设触摸监听。<br/>
		 * 相应的触摸监听方法已经在本类中实现。
		 * */
	}
	
}
