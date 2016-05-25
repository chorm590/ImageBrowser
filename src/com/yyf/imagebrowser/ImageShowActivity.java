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
	/**��¼��ǰ�Ƿ�ȫ��״̬�ı�־λ��<br/>Ĭ���Ƿ�ȫ��״̬�����Զ��廭�����Ǽ�ָʾ�����ڿɼ�״̬��*/
	private boolean isFullScreen;
	private ViewHolder ispHolder;
	
	//һ�����ڱ�־��ʾ/������ͼ�����Ƿ��ڶ����еı�־λ��
	private boolean isGoing;
	
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
			adapter.getViewHodler(position).starIndicator.setVisibility(View.VISIBLE);
			isFullScreen = false;
		}else {
			thumbnailGallery.setVisibility(View.INVISIBLE);
			adapter.getViewHodler(position).starIndicator.setVisibility(View.INVISIBLE);
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
			adapter.destroyItem(mViewPager, position+1, null);
			adapter.instantiateItem(mViewPager, position+1);
		}
		//������if���ɲ��ܺϲ�Ϊһ��Ŷ������ᷢ�����벻��������ġ�
		if(position!=0){
			adapter.destroyItem(mViewPager, position-1, null);
			adapter.instantiateItem(mViewPager, position-1);
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
		mViewPager.setAdapter(adapter);
		//
		thumbnailGallery.setAdapter(ISBAdapter);
		thumbnailGallery.initSelectedImg(position+1);
		
	}
	
	/**
	 * �л�ViewPager�е�ǰ��ʾ��ҳ�档<br/>
	 * ��Ҫ����ʵ��ViewPager��ThumbnailGallery����ʾ��ͼƬ��ͬ����
	 * */
	private void switchViewPagerImg(int position){
		mViewPager.setCurrentItem(position-1);
	}

	/**
	 * ��һЩ��ʼ��������
	 * ��ȡ��ӦͼƬ�������ݣ�����֡������ӵ�ĸ�����С�
	 * */
	private void init(){
		
		setBtnVisible(BTN_MENU, false);
		
		ds = DataStorage.getInstance();
		
		position = getIntent().getIntExtra("pos", 0);
		// Get image collection object.
		list = FgmtSortByFolder.getInstance().getDataList(); //�������������ص���null��˵���ֻ��ڴ治���ã����ֻ��ӵ��ɡ�
		setTitle(list.get(position).getFileName());
		Log.d("mylog", "ImageShowActivity - path:"+list.get(position).getPath()+"  pos:"+position);
		frameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.img_show_layout, null);
		mViewPager = (HackyViewPager) frameLayout.findViewById(R.id.hackyViewPager1);
		//��ViewPagerע��һ���Զ���Ĵ����¼�����ǳ���Ҫ��
		mViewPager.addOnPageChangeListener(new MyOnPageChangeListener());
		thumbnailGallery = (ThumbnailGallery) frameLayout.findViewById(R.id.thumbnailGalleryImgShow);
		llSelfActionbar = (LinearLayout) findViewById(R.id.llSelfActionbar);

		linearLayout.addView(frameLayout);
		
		ISBAdapter = new ImageShowBaseAdapter(this, list);
		adapter = new ImageShowPagerAdapter(this, list);
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
		
		/*
		 * mViewPagerע�ᴥ���ص���
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
