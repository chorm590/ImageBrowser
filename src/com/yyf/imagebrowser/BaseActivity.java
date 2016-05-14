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
 * ������������Activity�Ļ�Activity��
 * �ڱ�BaseActivity����Ҫ�������Զ����ActionBar�� 
 * ���ṩ���޸�ActionBar���������ݵķ�����
 * */
@SuppressWarnings("deprecation")
public class BaseActivity extends ActionBarActivity {

	private TextView tvTitle;
	protected ImageView ivBack, ivMenu;
	/**BaseActivity�е�ĸ������*/
	protected LinearLayout linearLayout;
	/**�Զ����ActionBar��*/
	protected LinearLayout llSelfActionbar;
	//��������Ӧ�¼���ͳһʵ����
	private ClickListener clickListener;
	public final static byte BTN_BACK = 1;   //�����Զ���ActionBar�еķ��ؼ���
	public final static byte BTN_MENU = 2;  //�����Զ���ActionBar�еĲ˵�����
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		//find views.
		init();
		//ActionBar��ťע����������
		abClickListener();
	}// onCreate()  --  end.

	/**
	 * ��ActionBar�еİ�ť���õ��������
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
	 * ����ActionBar�еİ�ť�Ƿ�ɼ���
	 * @param whichBtn ����ֵ��BaseActivity����public final static�ķ�ʽ���Ρ�
	 * 	BTN_BACK �����ؼ���  BTN_MENU ����˵�����
	 * @param isVisible true �ɼ� , false ���ɼ���
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
	 * ����ActionBar�еı��⡣
	 * */
	protected void setTitle(String title){
		this.tvTitle.setText(title);
	}// setTitle  --  end.
	
	/**
	 * ���ڽ�ActionBar�е�������ť���ó�δѡ��״̬��
	 * @param view ��ǰ��Ҫ���õ������
	 * @param isSelected trueΪѡ��״̬��falseΪδѡ��״̬��
	 * */
	protected void resetAbBtnState(View view, boolean isSelected){
		view.setSelected(isSelected);
	}
	
	/**
	 * ���ؼ�����Ӧ�¼������Ա�������д��
	 * */
	protected void backBtn(View view) {
		finish();
	}
	
	/**
	 * �Զ����ActionBar�ϵĲ˵�������Ӧ�¼�����������������ʵ�֡�
	 * @param view �������������϶��Ƕ�Ӧ�Ĳ˵���ť��
	 * */
	protected void menuBtn(View view){
		
	}
	
	/**
	 * ����ĵ����Ӧ�¼���
	 * */
	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			/*
			 * ��ActionBar�е�������ť��˵��ֻҪ�û����������ǣ����ǵ�ǰ��Ӧ���Ǳ�ѡ�е�
			 * ״̬����Ӧ���л���Ӧ��ͼƬ����ʾ����ˣ���Ӧ�����ṩһ���������ڵ��û�����
			 * ��ɺ󽫸ð�ť���ó�δѡ��״̬�����resetAbBtnState()������
			 * */
			v.setSelected(true);
			Log.d("mylog", "BaseActivity-View click.");
			switch(v.getId()){
				case R.id.ivSelfActionbarBack:{//���ص���һ��ҳ�档
					Log.v("mylog", "BaseActivity-icon-back");
					//�����ǰ���ؼ��ǿɼ��ģ�������ʱ��������Ӧ��Ӧ��
					if(v.getVisibility()==View.VISIBLE){
						backBtn(v);
					}
				}break;
				case R.id.ivSelfActionbarMenu:{
					Log.v("mylog", "BaseActivity-icon-menu");
					//�����ǰ���ؼ��ǿɼ��ģ�������ʱ��������Ӧ��Ӧ��
					if(v.getVisibility()==View.VISIBLE){
						menuBtn(v);
					}
				}break;
			}//switch  --  end.
		}
		
	}//  class  --  end.

}
