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
	/**��¼��ǰ�Ƿ�ȫ��״̬�ı�־λ��<br/>Ĭ���Ƿ�ȫ��״̬�����Զ��廭�����Ǽ�ָʾ�����ڿɼ�״̬��*/
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
//		����Ҫ��ViewPager���ʵ�ֻ������Ч����
		enableViews();
	}
	
	/**
	 * �ܵ����ʽ�������ٻ���Ҫ��ThumbnailGallery��StarIndicator��
	 * ��ʾ������������
	 * */
	private void fullScreenStateSwitch(){
		//����ǰ��ȫ����ʾ״̬�����л�Ϊ��ȫ��״̬��
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
		 * ��������δ���Ľ��ͣ�
		 * 		������ʵ�ֵĹ��ܾ�����ʾ/����ThumbnailGallery��StarIndicator�õġ�
		 * ��ô������ͼƬ�����һ���չʾ����ViewPager��ʵ�ֵġ�ViewPager������
		 * ����Ԥ���ص�ǰ��ʾҳ��ǰһҳ���һҳ���ڴ��С���ô�����ʱ�ڵ�ǰ��ʾ��ҳ��
		 * �и�������Щ�������ʾ״̬����ô�����µ���ʾ״̬�����ᱻӦ����֮ǰ�Ѽ���
		 * ���ڴ��е�ǰһҳ���һҳ�С���������δ������������ڸ������Զ��廭����
		 * �Ǽ�ָʾ�����ǰһҳ���һҳ#���¼���#һ����ʹ���µ����״̬����Ӧ�á�
		 * */
		if(position!=list.size()-1){
			ISPAdaper.destroyItem(viewPager, position+1, null);
			ISPAdaper.instantiateItem(viewPager, position+1);
		}
		//������if���ɲ��ܺϲ�Ϊһ��Ŷ������ᷢ�����벻��������ġ�
		if(position!=0){
			ISPAdaper.destroyItem(viewPager, position-1, null);
			ISPAdaper.instantiateItem(viewPager, position-1);
		}
	}// fullScreenStateSwitch()  --  end.
	
	public boolean getIsFullScreen(){
		return isFullScreen;
	}

	/**
	 * ΪViews����ʾ��������װ��
	 * */
	private void enableViews() {
		//�ȸ�ViewPager������������
		viewPager.setAdapter(ISPAdaper);
		//
		thumbnailGallery.setAdapter(ISBAdapter);
		thumbnailGallery.initSelectedImg(position+1);
		
	}
	
	/**
	 * �л�ViewPager�е�ǰ��ʾ��ҳ�档<br/>
	 * ��Ҫ����ʵ��ViewPager��ThumbnailGallery����ʾ��ͼƬ��ͬ����
	 * */
	private void switchViewPagerImg(int position){
		viewPager.setCurrentItem(position-1);
	}

	/**
	 * ��һЩ��ʼ��������
	 * ��ȡ��ӦͼƬ�������ݣ�����֡������ӵ�ĸ�����С�
	 * */
	private void init(){
		ds = DataStorage.getInstance();
		
		startPointF = new PointF();
		midPointF = new PointF();
		curMatrix = new Matrix();
		matrix = new Matrix();
		
		position = getIntent().getIntExtra("pos", 0);
		// Get image collection object.
		list = FgmtSortByFolder.getInstance().getDataList(); //�������������ص���null��˵���ֻ��ڴ治���ã����ֻ��ӵ��ɡ�
		setTitle(list.get(position).getFileName());
		Log.d("mylog", "ImageShowActivity - path:"+list.get(position).getPath()+"  pos:"+position);
		frameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.img_show_layout, null);
		viewPager = (ViewPager) frameLayout.findViewById(R.id.viewPagerImgShow);
		//��ViewPagerע��һ���Զ���Ĵ����¼�����ǳ���Ҫ��
		viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
		//��ViewPagerע�ᴥ���¼�������
		viewPager.setOnTouchListener(new MyViewPagerOnTouchListener());
		thumbnailGallery = (ThumbnailGallery) frameLayout.findViewById(R.id.thumbnailGalleryImgShow);
		llSelfActionbar = (LinearLayout) findViewById(R.id.llSelfActionbar);

		linearLayout.addView(frameLayout);
		
		ISBAdapter = new ImageShowBaseAdapter(this, list);
		ISPAdaper = new ImageShowPagerAdapter(this, list);
		/*
		 * ע�ử�ȵ�ǰѡ��ͼƬ�ı�ص���������
		 * ��ѡ���ͼƬ�����ı�ʱ��Ҫ�ı�ActionBar�ı�������ViewPager�е���ʾͼƬ��
		 * */
		thumbnailGallery.setOnImgChangeListener(new OnImgChangeListener() {
			
			@Override
			public void onImgChange(boolean isFromUser, int position) {
				setTitle(list.get(position-1).getFileName());
				ImageShowActivity.this.position = position-1;//�Զ��廭����ͼƬ�Ǵ�1��ʼ��ŵġ�
				Log.d("mylog", "the position="+ImageShowActivity.this.position);
				if(isFromUser){
					switchViewPagerImg(position);
				}
			}
		});
	}// init()  --  end.
	
	/**
	 * ViewPager�Ĵ����¼���Ӧ��������Ҫ����ҳ���л����������Ǽ��ȡ�
	 * */
	private class MyViewPagerOnTouchListener implements OnTouchListener {

		float xStart, xDistance;
		float yStart, yDistance;
		private final byte MODE_DRAG = 7;
		private final byte MODE_ZOOM = 77;
		/**����ģʽ������ģʽ�ı�־λ��*/
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
			 		//��ȡͼƬ��ǰ�ľ���
			 		curMatrix.set(ispHolder.iv.getImageMatrix());
			 		
			 	}break;
			 	case MotionEvent.ACTION_POINTER_DOWN:{
			 		Log.d("mylog", "vp pointer down.");
			 		mode = MODE_ZOOM;
			 		//������ָ���е㡣
			 		float x = event.getX(1) + event.getX(0);
			 		float y = event.getY(1) + event.getY(0);
			 		midPointF.set(x/2, y/2);
			 	}break;
			 	case MotionEvent.ACTION_MOVE:{
			 		Log.v("mylog", "vp move.");
			 		if(mode == MODE_ZOOM){
			 			Log.d("mylog", "zoom the image.");
			 			//��ֹ����Խ�絼�³��򱨴�
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
			 				// �������ӵ���Ч��Χ�ǣ�0.5~2.5
			 				scale = scale <= 0.6f ? 0.6f : scale;
			 				scale = scale >= 2.5f ? 2.5f : scale;
			 				Log.d("mylog", "scale="+scale);
			 				matrix.set(curMatrix);
			 				matrix.postScale(scale, scale, midPointF.x, midPointF.y);
			 				ispHolder.iv.setImageMatrix(matrix);
			 			}
			 		// ���Ŵ�����������
			 		} else if(mode == MODE_DRAG){
			 			Log.d("mylog", "drag the image.");
			 			//�������ʵ�ڲ������ˡ���ʱ�������ɡ�
			 			
			 		// ��ҷ������������
			 		}//else  --  end.
			 	}break;
			 	case MotionEvent.ACTION_POINTER_UP:{
			 		Log.d("mylog", "pointer up.");
			 		//Ҫ���������ŵ���ָԭʼ������λ��������޷��Ǹ���
			 		baseDis = 0;
			 	}break;
			 	case MotionEvent.ACTION_UP:{
			 		Log.d("mylog", "vp up.");
			 		//�������Ź����ҾͲ�ϣ�������������ҵ�ͼƬ���Ǽ��ˡ�
			 		if(mode == MODE_ZOOM){
			 			mode = 0;
			 			ispHolder = null;
			 			break;
			 		}
			 		xDistance = event.getX()-xStart;
			 		yDistance = event.getY()-yStart;
			 		Log.v("mylog", "ImageShowActivity - yDistance:"+yDistance);
			 		/*
			 		 * ����������һ����������»����Ķ����жϡ�
			 		 * ���һ��������ȼ��������»����������б�򻬶���
			 		 * */
			 		if(xDistance*xDistance>40000){ //�����һ����ľ��볬��200���ص�ʱ���ж�Ϊ���ҷ������˻�����
			 			//�Ѹô����¼�����ViewPagerȥ�����øö�������ҳ����л���
			 			Log.d("mylog", "left and right slide the screen.");
			 			return false;
			 		}
			 		
			 		if(yDistance>139){ //�ƶ�����Ϊ�����������»�����
			 			Log.i("mylog", "down slide. minuts a star.");
			 			ispHolder.starIndicator.minOneLevel();
			 			ds.setStarLevel(list.get(viewPager.getCurrentItem()).getPath(), 
			 					ispHolder.starIndicator.getCurrentLevel());
			 			//2016.3.4 .
			 			list.get(position).setStarLevel(ispHolder.starIndicator.getCurrentLevel());
			 			return true;
			 		}else if(yDistance<-150){ //������˵�����������ϵĻ�����
			 			Log.i("mylog", "up slide, add a star.");
			 			ispHolder.starIndicator.addOneLevel();
			 			ds.setStarLevel(list.get(viewPager.getCurrentItem()).getPath(), 
			 					ispHolder.starIndicator.getCurrentLevel());
			 			//2016.3.4 .
			 			list.get(position).setStarLevel(ispHolder.starIndicator.getCurrentLevel());
			 			return true;
			 		}
			 		
			 		//�жϵ���¼���
			 		if(xDistance*xDistance<100 && yDistance*yDistance<64){
			 			Log.d("mylog", "ViewPager - You click the screen.");
			 			fullScreenStateSwitch();
			 			return true;
			 		}
			 		//ģʽ��λ.
			 		mode = 0;
			 		//
			 		ispHolder = null;
			 	}//action_up  --  end.
			 } //switch  --  end.
			 
			return false;
		}
		
	}// inner class  --  end.
	
	/**
	 * ViewPagerҳ���л�ʱ�������¼���Ӧ������
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
