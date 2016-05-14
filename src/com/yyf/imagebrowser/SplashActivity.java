package com.yyf.imagebrowser;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

@SuppressWarnings("deprecation")
public class SplashActivity extends ActionBarActivity {

	private Handler handler;
	private ImageView ivBg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//兼容版本下无法通过主题同时隐藏状态栏与标题栏。
		//hide the status bar.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//set the content view.
		setContentView(R.layout.activity_splash);
		
		//find views.
		findViews();
		
		//set SplashActiviti'es background.
		ivBg.setImageResource(R.drawable.splashactivity);
		
		//jump to the main activity after a period of delay.
		handler = new Handler();
		
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				startActivity(new Intent(SplashActivity.this, MainActivity.class));
				//end current activity.
				finish();
				Log.v("mylog", "SplashActivity was finished.");
			}
		}, 1000);
	}//onCreate()  --  end.
	
	/**
	 * find views' object from xml files.
	 * */
	private void findViews(){
		ivBg = (ImageView) findViewById(R.id.img_bg);
		
	}
}
