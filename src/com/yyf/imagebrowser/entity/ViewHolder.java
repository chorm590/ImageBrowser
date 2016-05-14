package com.yyf.imagebrowser.entity;

import com.yyf.imagebrowser.view.StarIndicator;

import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {
	/**图像的控件*/
	public ImageView iv;
	public TextView tvAmount, tvFolderName;
	/**自定义的星级指示器。*/
	public StarIndicator starIndicator;
	public CheckBox checkBox;
}
