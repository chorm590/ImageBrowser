package com.yyf.imagebrowser;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 本程序中所有Activity的基Activity。
 * 在本BaseActivity中主要定义了自定义的ActionBar。 
 * 且提供了修改ActionBar标题栏内容的方法。
 * */
@SuppressWarnings("deprecation")
public class BaseActivity extends ActionBarActivity {

	private TextView tvTitle;
	protected ImageView ivBack, ivMenu;
	/**BaseActivity中的母容器。*/
	protected LinearLayout linearLayout;
	/**自定义的ActionBar栏*/
	protected LinearLayout llSelfActionbar;
	//组件点击响应事件的统一实例。
	private ClickListener clickListener;
	public final static byte BTN_BACK = 1;   //代表自定义ActionBar中的返回键。
	public final static byte BTN_MENU = 2;  //代表自定义ActionBar中的菜单键。
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		//find views.
		init();
		//ActionBar按钮注册点击监听。
		abClickListener();
	}// onCreate()  --  end.

	/**
	 * 给ActionBar中的按钮设置点击监听。
	 * */
	private void abClickListener() {
		ivBack.setOnClickListener(clickListener);
		ivMenu.setOnClickListener(clickListener);
	}// abClickListener  --  end.

	private void init() {
		tvTitle = (TextView) findViewById(R.id.tvSelfActionbarTitle);
		ivBack = (ImageView) findViewById(R.id.ivSelfActionbarBack);
		ivMenu = (ImageView) findViewById(R.id.ivSelfActionbarMenu);
		
		linearLayout =  (LinearLayout) findViewById(R.id.llBaseActivity);
		//instance.
		clickListener = new ClickListener();
	}// init method  --  end.
	
	/**
	 * 设置ActionBar中的按钮是否可见。
	 * @param whichBtn 常量值于BaseActivity中以public final static的方式修饰。
	 * 	BTN_BACK 代表返回键。  BTN_MENU 代表菜单键。
	 * @param isVisible true 可见 , false 不可见。
	 * */
	protected void setBtnVisible(byte whichBtn, boolean isVisible){
		int visibility = isVisible?View.VISIBLE :View.INVISIBLE;
		switch(whichBtn){
			case BTN_BACK:{
				ivBack.setVisibility(visibility);
			}break;
			case BTN_MENU:{
				ivMenu.setVisibility(visibility);
			}break;
		}
	}// setBtnVisible method  --  end.
	
	/**
	 * 设置ActionBar中的标题。
	 * */
	protected void setTitle(String title){
		this.tvTitle.setText(title);
	}// setTitle  --  end.
	
	/**
	 * 用于将ActionBar中的两个按钮设置成未选中状态。
	 * @param view 当前需要设置的组件。
	 * @param isSelected true为选中状态，false为未选中状态。
	 * */
	protected void resetAbBtnState(View view, boolean isSelected){
		view.setSelected(isSelected);
	}
	
	/**
	 * 返回键的响应事件，可以被子类重写。
	 * */
	protected void backBtn(View view) {
		finish();
	}
	
	/**
	 * 自定义的ActionBar上的菜单键的响应事件，需在子类中重新实现。
	 * @param view 参数不消讲，肯定是对应的菜单按钮。
	 * */
	protected void menuBtn(View view){
		
	}
	
	/**
	 * 组件的点击响应事件。
	 * */
	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			/*
			 * 拿ActionBar中的两个按钮来说，只要用户触摸了它们，它们当前就应该是被选中的
			 * 状态，就应该切换相应的图片来显示。因此，还应该再提供一个方法用于当用户操作
			 * 完成后将该按钮设置成未选中状态。详见resetAbBtnState()方法。
			 * */
			v.setSelected(true);
			Log.d("mylog", "BaseActivity-View click.");
			switch(v.getId()){
				case R.id.ivSelfActionbarBack:{//返回到上一个页面。
					Log.v("mylog", "BaseActivity-icon-back");
					//如果当前返回键是可见的，则点击它时会作出相应反应。
					if(v.getVisibility()==View.VISIBLE){
						backBtn(v);
					}
				}break;
				case R.id.ivSelfActionbarMenu:{
					Log.v("mylog", "BaseActivity-icon-menu");
					//如果当前返回键是可见的，则点击它时会作出相应反应。
					if(v.getVisibility()==View.VISIBLE){
						menuBtn(v);
					}
				}break;
			}//switch  --  end.
		}
		
	}//  class  --  end.

}
