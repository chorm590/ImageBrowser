package com.yyf.imagebrowser;

import java.util.ArrayList;
import java.util.Map;

import com.yyf.imagebrowser.entity.ImageEntity;
import com.yyf.imagebrowser.fragment.FgmtSortByFolder;
import com.yyf.imagebrowser.fragment.FgmtSortByStar;
import com.yyf.imagebrowser.fragment.FgmtSortByTimeLine;
import com.yyf.imagebrowser.tools.DataStorage;
import com.yyf.imagebrowser.tools.ScanImage;
import com.yyf.imagebrowser.utils.Constants;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnDismissListener;
import android.widget.PopupMenu.OnMenuItemClickListener;

/**
 * 主程序。
 * 构建主界面。
 * */
public class MainActivity extends BaseActivity {

	//默认的排序方式是“按文件夹排序”.
	private byte sortType = Constants.SORT_BY_FOLDER;
	/**用于加载各式Fragment的容器。*/
	private FrameLayout frameLayout;
	/**排序碎片事务管理器*/
	private FragmentTransaction fgmtTran; 
	/**FrameLayout的ID*/
	public static final int FL_ID = 7025;
	public static float density;
	public static float screenWidthHalf;
	public static float screenHeight;
	public static Map<String, ArrayList<ImageEntity>> map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		//设置程序主界面默认的排序方式为按文件夹分类排序。
		switchSortType(Constants.SORT_BY_FOLDER);
	}
	
	private void init(){
		//获取屏幕密度与屏幕宽度。
		density = getResources().getDisplayMetrics().density;
		screenWidthHalf = getResources().getDisplayMetrics().widthPixels / 2;
		screenHeight = getResources().getDisplayMetrics().heightPixels;
		setTitle("图片浏览器");
		//Instantiate the FrameLayout which use to contain fragment.
		frameLayout = new FrameLayout(this);
		frameLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT));
		frameLayout.setId(FL_ID);
		//把这个碎片容器添加到母容器中。
		linearLayout.addView(frameLayout);
		//初始化星级数据存储对象。
		DataStorage.init(this);
	}// init method  --  end.
	
	/**
	 * <p>用于切换主界面的排序方式，可选的排序方式有：</p>
	 * 按文件夹排序、按时间线排序、按星级升序以及按星级降序。
	 * @param sortType 选项值于Constants类中。
	 * */
	private void switchSortType(byte sortType){
		Fragment fgmt = null;
		//Begin transaction.
		fgmtTran = getSupportFragmentManager().beginTransaction();
		/*
		 * 把当前选择的排序方式更新到排序方式标志位中。
		 * 0表示按文件夹排序
		 * 1表示按时间线排序
		 * 2表示按星级升序排序
		 * 3表示按星级降序排序
		 */
		this.sortType = sortType;
		switch(sortType){
			case Constants.SORT_BY_FOLDER:{
				map = new ScanImage(getContentResolver()).getImageCollection();
				fgmt = new FgmtSortByFolder(map);
			}break;
			case Constants.SORT_BY_TIMELINE:{
				fgmt = new FgmtSortByTimeLine(getContentResolver());
			}break;
			case Constants.SORT_BY_STARUP:{
				fgmt = new FgmtSortByStar(true);
			}break;
			case Constants.SORT_BY_STARTDOWN:{
//				fgmt = new FgmtSortByStarDown();
				fgmt = new FgmtSortByStar(false);
			}break;
		}//  switch  --  end.
		fgmtTran.replace(FL_ID, fgmt);
		fgmtTran.commit();
	}// switchSortType()  --  end.
	
	@Override
	protected void menuBtn(View view) {
		//弹出一个菜单栏。
		//创建一个弹出式菜单对象的实例。
		PopupMenu popMenu = new PopupMenu(this, view);
		//映射菜单文件。
		popMenu.getMenuInflater().inflate(R.menu.main, popMenu.getMenu());
		//判断当前属于哪种排序状态并对菜单项作出相应的调整。
		popMenu.getMenu().getItem(sortType).setEnabled(false);
		//注册菜单项点击监听器。
		popMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				Log.v("mylog", "menu:"+arg0.getTitle());
				switchSortType((byte) (arg0.getItemId() - R.id.sortByFolder));
				return true;
			}
		});
		//当菜单栏消失以后。
		popMenu.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(PopupMenu arg0) {
				Log.v("mylog", "menu dismiss.");
				//让菜单栏中的菜单按钮（ImageView）重新变为未选中状态。
				resetAbBtnState(ivMenu, false);
			}
		});
		//配置完毕后显示该菜单。
		popMenu.show();
	}// menuBtn method  --  end.
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU){
			menuBtn(ivMenu);
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
}//  MainActivity class  --  end.
