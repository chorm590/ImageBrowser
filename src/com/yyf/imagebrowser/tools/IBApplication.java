package com.yyf.imagebrowser.tools;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.yyf.imagebrowser.R;

import android.app.Application;
import android.graphics.Bitmap.Config;

public class IBApplication extends Application {

	private DisplayImageOptions displayImageOptions;
	private static IBApplication application;
	
	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		//��ʼ��ImageLoader��������
		initImageLoader();
	}
	
	public static IBApplication getInstance(){
		return application;
	}
	
	public DisplayImageOptions getDisplayImageOptions(){
		return displayImageOptions;
	}

	/**
	 * ���ڳ�ʼ��ImageLoader��
	 * */
	@SuppressWarnings("deprecation")
	private void initImageLoader() {
		//ImageLoader���������趨��
		ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration
				.Builder(getApplicationContext())
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY-2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.discCacheSize(50*1024*1024) //����50�׵Ļ���ռ䡣
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.imageDownloader(new BaseImageDownloader(
						getApplicationContext(), 5000, 20000))//5�����ӳ�ʱ��20�����س�ʱ��
				.build();
		//ImageLoader��ʼ����
		ImageLoader.getInstance().init(imageLoaderConfiguration);
		//���ü��ص�ͼƬ��ѡ�
		displayImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_image)
				.showImageOnFail(R.drawable.error)
				.showImageForEmptyUri(R.drawable.error_on_img_uri)
				.cacheOnDisk(true)
				.cacheInMemory(true)
				.considerExifParams(true)
				.bitmapConfig(Config.RGB_565)
				.build();
	}//initImageLoader method  --  end.
	
}
