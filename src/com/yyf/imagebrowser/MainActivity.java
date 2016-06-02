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
 * ������
 * ���������档
 * */
public class MainActivity extends BaseActivity {

	//Ĭ�ϵ�����ʽ�ǡ����ļ�������.
	private byte sortType = Constants.SORT_BY_FOLDER;
	/**���ڼ��ظ�ʽFragment��������*/
	private FrameLayout frameLayout;
	/**������Ƭ���������*/
	private FragmentTransaction fgmtTran; 
	/**FrameLayout��ID*/
	public static final int FL_ID = 7025;
	public static float density;
	public static float screenWidthHalf;
	public static float screenHeight;
	public static Map<String, ArrayList<ImageEntity>> map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		//���ó���������Ĭ�ϵ�����ʽΪ���ļ��з�������
		switchSortType(Constants.SORT_BY_FOLDER);
	}
	
	private void init(){
		//��ȡ��Ļ�ܶ�����Ļ��ȡ�
		density = getResources().getDisplayMetrics().density;
		screenWidthHalf = getResources().getDisplayMetrics().widthPixels / 2;
		screenHeight = getResources().getDisplayMetrics().heightPixels;
		setTitle("ͼƬ�����");
		//Instantiate the FrameLayout which use to contain fragment.
		frameLayout = new FrameLayout(this);
		frameLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT));
		frameLayout.setId(FL_ID);
		//�������Ƭ������ӵ�ĸ�����С�
		linearLayout.addView(frameLayout);
		//��ʼ���Ǽ����ݴ洢����
		DataStorage.init(this);
	}// init method  --  end.
	
	/**
	 * <p>�����л������������ʽ����ѡ������ʽ�У�</p>
	 * ���ļ������򡢰�ʱ�������򡢰��Ǽ������Լ����Ǽ�����
	 * @param sortType ѡ��ֵ��Constants���С�
	 * */
	private void switchSortType(byte sortType){
		Fragment fgmt = null;
		//Begin transaction.
		fgmtTran = getSupportFragmentManager().beginTransaction();
		/*
		 * �ѵ�ǰѡ�������ʽ���µ�����ʽ��־λ�С�
		 * 0��ʾ���ļ�������
		 * 1��ʾ��ʱ��������
		 * 2��ʾ���Ǽ���������
		 * 3��ʾ���Ǽ���������
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
		//����һ���˵�����
		//����һ������ʽ�˵������ʵ����
		PopupMenu popMenu = new PopupMenu(this, view);
		//ӳ��˵��ļ���
		popMenu.getMenuInflater().inflate(R.menu.main, popMenu.getMenu());
		//�жϵ�ǰ������������״̬���Բ˵���������Ӧ�ĵ�����
		popMenu.getMenu().getItem(sortType).setEnabled(false);
		//ע��˵�������������
		popMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				Log.v("mylog", "menu:"+arg0.getTitle());
				switchSortType((byte) (arg0.getItemId() - R.id.sortByFolder));
				return true;
			}
		});
		//���˵�����ʧ�Ժ�
		popMenu.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(PopupMenu arg0) {
				Log.v("mylog", "menu dismiss.");
				//�ò˵����еĲ˵���ť��ImageView�����±�Ϊδѡ��״̬��
				resetAbBtnState(ivMenu, false);
			}
		});
		//������Ϻ���ʾ�ò˵���
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
