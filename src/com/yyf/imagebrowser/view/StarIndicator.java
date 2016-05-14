package com.yyf.imagebrowser.view;


import com.yyf.imagebrowser.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * �Զ�����Ǽ�ָʾ����
 * ����� 0 ~ 5 �������ȼ���
 * */
public class StarIndicator extends LinearLayout {

	private View view;
	private ImageView[] iv = new ImageView[5];
	private byte level;
	
	public StarIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	/**
	 * �����Ǽ�����Ҫ���ڳ�ʼ����ʾʱ��
	 * */
	public void setLevel(byte level){
		//������ֵ�жϷ�ֹ����ű�Խ�����������Ҫ�Ĵ���
		level = level<0?0:level;
		level = level>5?5:level;
		if(this.level>level){
			for(byte i=level;i<this.level;i++)
				iv[i].setImageResource(R.drawable.star_unselected);
			return;
		}
		for(byte i=0;i<level;i++){
			iv[i].setImageResource(R.drawable.star_selected);
		}
		//ֱ��������ͼ����Ǽ����ǧ��Ҫ���˸���ȫ���Ǽ���־λ��
		this.level = level;
	}
	
	public byte getCurrentLevel(){
		Log.d("mylog", "getCurrentLevel:"+level);
		return level;
	}
	
	private void init() {
		view = LayoutInflater.from(getContext()).inflate(R.layout.star_indicator, null);
		iv[0] = (ImageView) view.findViewById(R.id.ivStar1);
		iv[1] = (ImageView) view.findViewById(R.id.ivStar2);
		iv[2] = (ImageView) view.findViewById(R.id.ivStar3);
		iv[3] = (ImageView) view.findViewById(R.id.ivStar4);
		iv[4] = (ImageView) view.findViewById(R.id.ivStar5);
		//Make the starIndicator's layout fit in the container.
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT));
		addView(view);
	}
	
	public void addOneLevel(){
		if(level==5){
			Log.v("mylog", "highest star.");
			return;
		}
		level++;
		iv[level-1].setImageResource(R.drawable.star_selected);
	}

	public void minOneLevel(){
		if(level==0){
			Log.v("mylog", "lowest star.");
			return;
		}
		level--;
		iv[level].setImageResource(R.drawable.star_unselected);
	}

}
