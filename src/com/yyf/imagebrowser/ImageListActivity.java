package com.yyf.imagebrowser;

import java.util.ArrayList;
import java.util.HashSet;

import com.yyf.imagebrowser.adapter.ImageListBaseAdapter;
import com.yyf.imagebrowser.entity.ImageEntity;
import com.yyf.imagebrowser.fragment.FgmtSortByFolder;
import com.yyf.imagebrowser.tools.FileDelete;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnDismissListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * 在按文件夹分类排序界面点选某一文件夹后跳转的活动界面。
 * */
public class ImageListActivity extends BaseActivity {

	private ImageListBaseAdapter ilbAdapter;
	private ArrayList<ImageEntity> listDate;
	private ArrayList<ImageEntity> listFiltrate;
	private boolean isMultiSelectMode;
	private boolean isShareMode;
	private PopupMenu popMenu;
	private GridView gvList;
	private String titleBase;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//initialize
		listDate = FgmtSortByFolder.getInstance().getDataList();
		listFiltrate = new ArrayList<>();
		titleBase = listDate.get(0).getParentFolderName(); //最好判断一下这个listDate中是否有元素。
		setTitle(titleBase+"(ALL)");
		//添加相应的显示组件。
		addContainer();
	}// onCreate()  --  end.
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(isShareMode){
			Log.d("mylog", "share  true.");
			isShareMode = false;
			switchMultiSelect();
		}
		
		/*
		 * 在这里完成刷新星级数据的工作。
		 * 当用户从图片列表Activity中跳转到图片详细内容
		 * 展示Activity中浏览照片时，可能会修改某些照片的星级，
		 * 故而在图片显示Activity退出后需要对图片列表Activity中的
		 * 图片进行数据更新,如：星级数据。
		 */
		ilbAdapter.notifyViewsChanged();
		Log.d("mylog", "ImageListActivity - The star level was flushed.");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//如果当前正处于批量操作模式，且按下了返回键，则按自己的需求消费这个返回键事件。
		if(isMultiSelectMode && keyCode == KeyEvent.KEYCODE_BACK){
			// 这里的意思是退出批量操作模式。
			switchMultiSelect();
			ilbAdapter.switchCheckBoxState(isMultiSelectMode);
			//已笑纳。
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 重写BaseActivity中的选项按键响应方法。<br/>
	 * 用以切换批量操作模式。
	 * */
	@Override
	protected void menuBtn(View view) {
		super.menuBtn(view);
		popMenu = new PopupMenu(this, view);
		popMenu.getMenuInflater().inflate(R.menu.switch_multi_mode, popMenu.getMenu());
		popMenu.getMenu().getItem(0).setEnabled(!isMultiSelectMode);
		popMenu.getMenu().getItem(1).setEnabled(isMultiSelectMode);
		//
		popMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				switch(arg0.getItemId()){
					//切换到批量操作模式。
					case R.id.menuItemMultiSelect :{
						switchMultiSelect();
					}break;
					//删除在批量操作模式下被选中的图片。
					case R.id.menuItemDel :{
						delImage();
						refreshMainActivityImgCount();
					}break;
					//弹出一个对话框根据输入的星级筛选排序。
					case R.id.menuItemStar :{
						classfyByStar();
					}break;
					case R.id.menuItemShare :{
						shareMenu();
					}break;
				}
				return false;
			}
		});
		//
		popMenu.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(PopupMenu arg0) {
				// 只要这个对话框消失了，就恢复菜单按钮的式样。
				resetAbBtnState(ivMenu, false);
			}
		});
		popMenu.show();
	}//接下来要做的就是根据CheckBox的选择情况来删除指定文件了。
	
	/**
	 * 分享菜单的响应方法。
	 * */
	private void shareMenu(){
		if(isMultiSelectMode){
			Log.d("mylog", "ke yi share le.");
			ArrayList<Uri> shareList = new ArrayList<>();
			ArrayList<ImageEntity> temp = getSelectedItem();
			if(temp.size() == 0){
				Toast.makeText(this, "请至少选择一张图片", Toast.LENGTH_LONG).show();
				return;
			}else if(temp.size() > 9){
				Toast.makeText(this, "至多只能分享九张图片", Toast.LENGTH_LONG).show();
				return;
			}else{
				for(int i = 0;i<temp.size();i++){
					shareList.add(Uri.parse("file:///"+temp.get(i).getPath()));
				}
				share(shareList);
			}
		}else{
			// 当前处于普通浏览模式，先将模式切换为多选模式。
			switchMultiSelect();
		}
	}// shareMenu  --  end.
	
	/**
	 * 真正的分享功能。
	 * */
	private void share(ArrayList<Uri> shareList){
		isShareMode = true;
		Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
		share.setType("image/*");
		share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, shareList);
		startActivity(Intent.createChooser(share, "分享到..."));
	}
	
	private ArrayList<ImageEntity> getSelectedItem(){
		ArrayList<ImageEntity> list = new ArrayList<>();
		for(int i=0;i<listDate.size();i++){
			Log.v("mylog", "pos:"+i+"  checkbox state:"+listDate.get(i).getIsSelected());
			//如果当前图片的CheckBox被勾选，则记录下它。
			if(listDate.get(i).getIsSelected()){
				list.add(listDate.get(i));
			}
		}
		
		return list;
	}
	
	/**
	 * 弹出一个对话框，根据选择的星级级别筛选显示图片。
	 * */
	private void classfyByStar(){
		//弹出一个对话框，该对话框有一个确认键和一个取消键。以及一个下拉框。
		AlertDialog alertDialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle("选择星级：");
		//设置该对话框的内容为一个下拉框。
//		builder.setView(R.layout.classfy_star_spinner);
		View view = LayoutInflater.from(this).inflate(R.layout.classfy_star_spinner, null);
		builder.setView(view);
		final Spinner spinner = (Spinner) view.findViewById(R.id.spinnerStarClassfy);
		final CheckBox cb = (CheckBox) view.findViewById(R.id.cbIncludeAbove);
		// 设置确认按钮。。
		builder.setPositiveButton("确认", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 跳转到处理按星级筛选显示图片的方法中。
				showSpecifyLevel((String)spinner.getSelectedItem(), cb.isChecked());
			}
		});
		// 设置取消按钮。。
		builder.setNegativeButton("取消", null);
		alertDialog = builder.create();
		alertDialog.show();
	} // classfyByStar  --  end.
	
	/**
	 * 筛选出指定星级的图片显示。
	 * @param b 
	 * */
	private void showSpecifyLevel(String level, boolean b){
		byte mLevel = -1;
		/*
		 * 设置六个集合。
		 * 1. 所有图片集合；
		 * 2. 无星级图片集合；
		 * 3. 一星级图片集合；
		 * 			.
		 * 			.
		 * 			.
		 * 6. 五星级图片集合。
		 * 根据筛选结果将对应集合传入到ImageListBaseAdapter中并加载到GridView中即可完成
		 * 图片的按等级筛选显示功能。
		 * */
		if(level.equals("ALL")){
			Log.d("mylog", "ImageListActivity - all star level.");
			setTitle(titleBase+"(ALL)");
			mLevel = -1;
		}else if(level.equals("一星")){
			Log.d("mylog", "ImageListActivity - 1 level!");
			setTitle(titleBase+"(一星)");
			mLevel = 1;
		}else if(level.equals("二星")){
			Log.d("mylog", "ImageListActivity - 2 level!");
			setTitle(titleBase+"(二星)");
			mLevel = 2;
		}else if(level.equals("三星")){
			Log.d("mylog", "ImageListActivity - 3 level!");
			setTitle(titleBase+"(三星)");
			mLevel = 3;
		}else if(level.equals("四星")){
			Log.d("mylog", "ImageListActivity - 4 level!");
			setTitle(titleBase+"(四星)");
			mLevel = 4;
		}else if(level.equals("五星")){
			Log.d("mylog", "ImageListActivity - 5 level!");
			setTitle(titleBase+"(五星)");
			mLevel = 5;
		}else if(level.equals("无级别")){
			Log.d("mylog", "ImageListActivity - no level.");
			setTitle(titleBase+"(无星级)");
			mLevel = 0;
		} //  if  else  --  end.
		//这里筛选星级。
		listFiltrate.clear();
		
		if(mLevel==-1){
			listFiltrate.addAll(listDate);
		}else{
			if(b){
				for(int i=0;i<listDate.size();i++){
					if(listDate.get(i).getStarLevel() >= mLevel){
						listFiltrate.add(listDate.get(i));
					}
				}
			}else {
				for(int i=0;i<listDate.size();i++){
					if(listDate.get(i).getStarLevel()==mLevel){
						listFiltrate.add(listDate.get(i));
					}
				}
			}
			Log.d("mylog", "ImageListActivity - listFiltrate size="+listFiltrate.size());
			if(listFiltrate.size()==0){
				Toast.makeText(this, "当前文件夹下没有"+mLevel+"星的图片数据！", Toast.LENGTH_LONG).show();
				gvList.setAdapter(null);
				return;
			}
		}//  else  --  end.
		//下面开始切换适配器。。
		ilbAdapter = new ImageListBaseAdapter(this, listFiltrate);
		gvList.setAdapter(ilbAdapter);
	} // showSpecifyLevel()  --  end.
	
	/**
	 * 当在ImageListActivity中删除了某些图片后除了需要更新ImageListActivity<br/>
	 * 中的相关数据外还需要更新MainActivity中的对应项信息。<br/>
	 * 而本方法的作用就是用于更新MainActivity中的数据的。
	 * */
	private void refreshMainActivityImgCount(){
		//删除了有照片，需要更新一下MainActivity中的数据信息。
		FgmtSortByFolder.getInstance().refreshData(FgmtSortByFolder.getInstance().getCurrentSelectedPos());
	}
	
	/**
	 * 删除在批量操作模式中选中的图片。
	 * */
	private void delImage(){
		Log.v("mylog", "ImageListActivity - delImage.");
		HashSet<Integer> delSet = new HashSet<>();
		ArrayList<Integer> delList = new ArrayList<>();
		//在ImageListBaseAdapter中操作的list集合数据与本类中的集合数据是同一对象，故直接从本类中取即可。
		for(int i=0;i<listDate.size();i++){
			Log.v("mylog", "pos:"+i+"  checkbox state:"+listDate.get(i).getIsSelected());
			//如果当前图片的CheckBox被勾选，则记录下它。
			if(listDate.get(i).getIsSelected()){
				delSet.add(i);
				delList.add(i);
			}
		}
		//完成需要删除的筛选。
		FileDelete fileDelete = new FileDelete(getContentResolver());
		//先把GridView的适配器置空。
		gvList.setAdapter(null);
		ilbAdapter = null;
		Log.d("mylog", "del set size:"+delSet.size()+" delList size:"+delList.size());
		//在这里执行删除文件的操作。
		for(int i=0;i<delList.size();i++){
			fileDelete.setPath(listDate.get(delList.get(i)-i/*这个 -i 非常重要。
			如果没有它在删除多个图片时会极有可能报越界异常*/).getPath());
			fileDelete.delete();
			//把第n个被删除的数据移除。
			listDate.remove(delList.get(i)-i);
		}//for  --  end.
		ilbAdapter = new ImageListBaseAdapter(this, listDate);
		gvList.setAdapter(ilbAdapter);
	}// delImage  --  end.
	
	/**
	 * 使本Activity在“正常浏览”与“批量操作”两种模式中切换。
	 * */
	private void switchMultiSelect(){
		if(isMultiSelectMode){
			isMultiSelectMode = false;
			popMenu.getMenu().getItem(0).setEnabled(true);
			//在这里想办法让每个图片的Item上的CheckBox隐藏起来。
			ilbAdapter.switchCheckBoxState(isMultiSelectMode);
		} else { //如果当前尚未处于多选模式。。。
			isMultiSelectMode = true;
			popMenu.getMenu().getItem(0).setEnabled(false);
			//这里想办法让每个图片Item上的CheckBox显示出来。
			ilbAdapter.switchCheckBoxState(isMultiSelectMode);
		}
	}  // switchMultiSelect  --  end.
	
	/**
	 * 继续初始化工作。
	 * */
	private void addContainer() {
		gvList = (GridView) LayoutInflater.from(this).inflate(R.layout.gridview, null);
		gvList.setVerticalSpacing(10);
		//把这个GridView对象添加到母容器中去。
		linearLayout.addView(gvList);
		this.ilbAdapter = new ImageListBaseAdapter(this, listDate);
		gvList.setAdapter(this.ilbAdapter);
		gvList.setOnItemClickListener(new GvItemClickListener());
	}

	/**
	 * 
	 * */
	private class GvItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.i("mylog", "gridview item.");
			Intent intent = new Intent();
			intent.setClass(ImageListActivity.this, ImageShowActivity.class);
			intent.putExtra("pos", position);
			//图片集合的对象直接在FgmtSortByFolder对象中取即可。
			startActivity(intent);
		}
		
	}

}
