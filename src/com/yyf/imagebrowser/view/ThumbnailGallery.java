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
 * �Լ�ʵ�ֵ���ʾ����ͼ�Ļ��ȡ�
 * */
public class ThumbnailGallery extends HorizontalScrollView {

	private Context context;
	private ListAdapter adapter;
	private Scroller scroller;
	/**����Ԫ�ش�С*/
	private int elementsSize;
	/**���ڴ��������ģ��Ŀؼ�*/
	private View adapterModelView;
	/**���ں������ImageView�����Բ��֡�����ͼ��ؼ�����ӵ��������С�*/
	private LinearLayout linearLayout;
	private OnImgChangeListener onImgChangeListener;
	private int preSelectedImg, currSelectedImg;
	private float currTouchPos;
	public static final int THUMBNAIL_WIDTH_BASE = 70;
	public static final int IV_PADDING_WIDTH = 5;
	public static final int IV_DEFAULT_BG_COLOR = Color.WHITE;
	public static final int IV_SELECTED_BG_COLOR = Color.BLUE;
	/**ÿ��ImageViewռ�Ŀ�ȡ�*/
	private int imgWidth;
	/**�������ֵ��ܿ�ȡ�*/
	private int totalWidth;
	private int centerXPos;
	private boolean isFromUser;
	
	/**
	 * ͼ��ı��������
	 * ����ǰѡ���ͼƬ�����ı�ʱ��ص��ü�������
	 * */
	public interface OnImgChangeListener {
		abstract void onImgChange(boolean isFromUser, int position);
	}
	
	public void setOnImgChangeListener(OnImgChangeListener listener){
		onImgChangeListener = listener;
	}
	
	/**
	 * ֱ��ָ����ǰѡ����ͼƬ��
	 * */
	public void setSelectedImg(int position){
		currSelectedImg = position;
		isFromUser = false;
		updateSelectedImg();
	}
	
	/**
	 * ֻ���ڳ����������ʱ���õ�ǰӦ��ѡ����ʾ��ͼƬ��
	 * */
	public void initSelectedImg(int pos){
		currSelectedImg = pos;
		isFromUser = true;
		updateSelectedImg();
	}
	
	/**
	 * ���µ�ǰѡ��ͼƬ�ı�־λ��
	 * */
	private void updateSelectedImg(){
		if(preSelectedImg!=currSelectedImg){
			preSelectedImg = preSelectedImg==0?1:preSelectedImg;
			switchBackground();
			preSelectedImg = currSelectedImg;
			//����ǰ��ע��ͼ��ı������������ûص�������
			if(onImgChangeListener!=null)
				onImgChangeListener.onImgChange(isFromUser, currSelectedImg);
			//ͼ�����ı䣬�����ѡ���ͼ���Ƶ���Ļ�м���ʾ��
			scrollCenter();
		}
	}
	
	/**
	 * ��ĳ��ͼƬ��ѡ��ʱ��Ϊ���и��õ��Ӿ�Ч��<br/>
	 * ��Ҫ������ͼƬ��ʾ����Ļˮƽ�м䡣
	 * */
	private void scrollCenter(){
		//����Ҫ�������پ������ʹ�ñ�ѡ�е�ͼ����ʾ����Ļ�м䡣
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
	 * Ϊ����Զ���Gallery������������<br/>
	 * */
	public void setAdapter(ListAdapter adapter){
		this.adapter = adapter;
		elementsSize = this.adapter.getCount();
		for(short i = 0;i<elementsSize;i++){
			adapterModelView = adapter.getView(i, null, null);
			linearLayout.addView(adapterModelView);//+++����ֱ�Ӱ����пؼ��ӽ�ȥ��֪����û�����⡣+++
		}
		//����ÿ��ͼƬ�Ŀ�����ܿ�ȡ�IV�ڴ˴��ű���ʾ���������ڴ�֮����ܵõ�IV��ʵ�ʿ�ȡ�
		imgWidth = (int) (THUMBNAIL_WIDTH_BASE *MainActivity.density +0.5f+IV_PADDING_WIDTH *2);
		totalWidth = imgWidth*elementsSize;
		Log.d("mylog", "ThumbnailGallery - imgWidth:"+imgWidth+"  totalWidth:"+totalWidth);
	} // setAdapter  --  end.
	
	private void init(){
		linearLayout = new LinearLayout(context);
		linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT));
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		//�Զ��廭�ȵ�����ImageView�������ı�����ɫ���ó�͸��ɫ��
		linearLayout.setBackgroundColor(Color.alpha(0));
		//�����úõ�LinearLayout����ӽ�������ȥ��
		addView(linearLayout);
		//Initialize Scroller.
		scroller = new Scroller(context);
		
		//���ô�����������
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
						//�ж��ǵ������������ƶ�������
						if(distance*distance>100){
							timeEnd = SystemClock.uptimeMillis();
							Log.v("mylog", "ThumbnailGallery - You move,not click.");
							//����ƶ�ʱ�们��ʱ��С��512���룬���ж�Ϊ���ٻ������������Ի�����
							if((timeEnd-timeStart)<512){
								speed = distance/(timeEnd-timeStart)*-1;//����-1��Ϊ�˰ѷ���ȡ������
								scroller.startScroll(getScrollX(), 0, (int)(speed*512), 0, 512);
								invalidate();
							}
							return false;
						}
						//���㵱ǰѡ��������ͼƬ��
						currTouchPos = event.getX()+getScrollX();
						currSelectedImg = (int) Math.ceil(currTouchPos/totalWidth*elementsSize);
						Log.d("mylog", "ThumbnailGallery - currSelectedImg:"+currSelectedImg);
						isFromUser = true;
						//ѡ����ĳ��ͼƬ���ʶ���Ҫ����ѡ�е�ͼƬ�ı�־λ��
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
		 * �ſ�������������ǲ��������败��������<br/>
		 * ��Ӧ�Ĵ������������Ѿ��ڱ�����ʵ�֡�
		 * */
	}
	
}
