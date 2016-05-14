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
		//初始化ImageLoader相关组件。
		initImageLoader();
	}
	
	public static IBApplication getInstance(){
		return application;
	}
	
	public DisplayImageOptions getDisplayImageOptions(){
		return displayImageOptions;
	}

	/**
	 * 用于初始化ImageLoader。
	 * */
	@SuppressWarnings("deprecation")
	private void initImageLoader() {
		//ImageLoader基本配置设定。
		ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration
				.Builder(getApplicationContext())
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY-2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.discCacheSize(50*1024*1024) //设置50兆的缓存空间。
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.imageDownloader(new BaseImageDownloader(
						getApplicationContext(), 5000, 20000))//5秒连接超时，20秒下载超时。
				.build();
		//ImageLoader初始化。
		ImageLoader.getInstance().init(imageLoaderConfiguration);
		//配置加载的图片的选项。
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
