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
 * �ڰ��ļ��з�����������ѡĳһ�ļ��к���ת�Ļ���档
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
		titleBase = listDate.get(0).getParentFolderName(); //����ж�һ�����listDate���Ƿ���Ԫ�ء�
		setTitle(titleBase+"(ALL)");
		//�����Ӧ����ʾ�����
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
		 * ���������ˢ���Ǽ����ݵĹ�����
		 * ���û���ͼƬ�б�Activity����ת��ͼƬ��ϸ����
		 * չʾActivity�������Ƭʱ�����ܻ��޸�ĳЩ��Ƭ���Ǽ���
		 * �ʶ���ͼƬ��ʾActivity�˳�����Ҫ��ͼƬ�б�Activity�е�
		 * ͼƬ�������ݸ���,�磺�Ǽ����ݡ�
		 */
		ilbAdapter.notifyViewsChanged();
		Log.d("mylog", "ImageListActivity - The star level was flushed.");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//�����ǰ��������������ģʽ���Ұ����˷��ؼ������Լ�����������������ؼ��¼���
		if(isMultiSelectMode && keyCode == KeyEvent.KEYCODE_BACK){
			// �������˼���˳���������ģʽ��
			switchMultiSelect();
			ilbAdapter.switchCheckBoxState(isMultiSelectMode);
			//��Ц�ɡ�
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * ��дBaseActivity�е�ѡ�����Ӧ������<br/>
	 * �����л���������ģʽ��
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
					//�л�����������ģʽ��
					case R.id.menuItemMultiSelect :{
						switchMultiSelect();
					}break;
					//ɾ������������ģʽ�±�ѡ�е�ͼƬ��
					case R.id.menuItemDel :{
						delImage();
						refreshMainActivityImgCount();
					}break;
					//����һ���Ի������������Ǽ�ɸѡ����
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
				// ֻҪ����Ի�����ʧ�ˣ��ͻָ��˵���ť��ʽ����
				resetAbBtnState(ivMenu, false);
			}
		});
		popMenu.show();
	}//������Ҫ���ľ��Ǹ���CheckBox��ѡ�������ɾ��ָ���ļ��ˡ�
	
	/**
	 * ����˵�����Ӧ������
	 * */
	private void shareMenu(){
		if(isMultiSelectMode){
			Log.d("mylog", "ke yi share le.");
			ArrayList<Uri> shareList = new ArrayList<>();
			ArrayList<ImageEntity> temp = getSelectedItem();
			if(temp.size() == 0){
				Toast.makeText(this, "������ѡ��һ��ͼƬ", Toast.LENGTH_LONG).show();
				return;
			}else if(temp.size() > 9){
				Toast.makeText(this, "����ֻ�ܷ������ͼƬ", Toast.LENGTH_LONG).show();
				return;
			}else{
				for(int i = 0;i<temp.size();i++){
					shareList.add(Uri.parse("file:///"+temp.get(i).getPath()));
				}
				share(shareList);
			}
		}else{
			// ��ǰ������ͨ���ģʽ���Ƚ�ģʽ�л�Ϊ��ѡģʽ��
			switchMultiSelect();
		}
	}// shareMenu  --  end.
	
	/**
	 * �����ķ����ܡ�
	 * */
	private void share(ArrayList<Uri> shareList){
		isShareMode = true;
		Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
		share.setType("image/*");
		share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, shareList);
		startActivity(Intent.createChooser(share, "����..."));
	}
	
	private ArrayList<ImageEntity> getSelectedItem(){
		ArrayList<ImageEntity> list = new ArrayList<>();
		for(int i=0;i<listDate.size();i++){
			Log.v("mylog", "pos:"+i+"  checkbox state:"+listDate.get(i).getIsSelected());
			//�����ǰͼƬ��CheckBox����ѡ�����¼������
			if(listDate.get(i).getIsSelected()){
				list.add(listDate.get(i));
			}
		}
		
		return list;
	}
	
	/**
	 * ����һ���Ի��򣬸���ѡ����Ǽ�����ɸѡ��ʾͼƬ��
	 * */
	private void classfyByStar(){
		//����һ���Ի��򣬸öԻ�����һ��ȷ�ϼ���һ��ȡ�������Լ�һ��������
		AlertDialog alertDialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle("ѡ���Ǽ���");
		//���øöԻ��������Ϊһ��������
//		builder.setView(R.layout.classfy_star_spinner);
		View view = LayoutInflater.from(this).inflate(R.layout.classfy_star_spinner, null);
		builder.setView(view);
		final Spinner spinner = (Spinner) view.findViewById(R.id.spinnerStarClassfy);
		final CheckBox cb = (CheckBox) view.findViewById(R.id.cbIncludeAbove);
		// ����ȷ�ϰ�ť����
		builder.setPositiveButton("ȷ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ��ת�������Ǽ�ɸѡ��ʾͼƬ�ķ����С�
				showSpecifyLevel((String)spinner.getSelectedItem(), cb.isChecked());
			}
		});
		// ����ȡ����ť����
		builder.setNegativeButton("ȡ��", null);
		alertDialog = builder.create();
		alertDialog.show();
	} // classfyByStar  --  end.
	
	/**
	 * ɸѡ��ָ���Ǽ���ͼƬ��ʾ��
	 * @param b 
	 * */
	private void showSpecifyLevel(String level, boolean b){
		byte mLevel = -1;
		/*
		 * �����������ϡ�
		 * 1. ����ͼƬ���ϣ�
		 * 2. ���Ǽ�ͼƬ���ϣ�
		 * 3. һ�Ǽ�ͼƬ���ϣ�
		 * 			.
		 * 			.
		 * 			.
		 * 6. ���Ǽ�ͼƬ���ϡ�
		 * ����ɸѡ�������Ӧ���ϴ��뵽ImageListBaseAdapter�в����ص�GridView�м������
		 * ͼƬ�İ��ȼ�ɸѡ��ʾ���ܡ�
		 * */
		if(level.equals("ALL")){
			Log.d("mylog", "ImageListActivity - all star level.");
			setTitle(titleBase+"(ALL)");
			mLevel = -1;
		}else if(level.equals("һ��")){
			Log.d("mylog", "ImageListActivity - 1 level!");
			setTitle(titleBase+"(һ��)");
			mLevel = 1;
		}else if(level.equals("����")){
			Log.d("mylog", "ImageListActivity - 2 level!");
			setTitle(titleBase+"(����)");
			mLevel = 2;
		}else if(level.equals("����")){
			Log.d("mylog", "ImageListActivity - 3 level!");
			setTitle(titleBase+"(����)");
			mLevel = 3;
		}else if(level.equals("����")){
			Log.d("mylog", "ImageListActivity - 4 level!");
			setTitle(titleBase+"(����)");
			mLevel = 4;
		}else if(level.equals("����")){
			Log.d("mylog", "ImageListActivity - 5 level!");
			setTitle(titleBase+"(����)");
			mLevel = 5;
		}else if(level.equals("�޼���")){
			Log.d("mylog", "ImageListActivity - no level.");
			setTitle(titleBase+"(���Ǽ�)");
			mLevel = 0;
		} //  if  else  --  end.
		//����ɸѡ�Ǽ���
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
				Toast.makeText(this, "��ǰ�ļ�����û��"+mLevel+"�ǵ�ͼƬ���ݣ�", Toast.LENGTH_LONG).show();
				gvList.setAdapter(null);
				return;
			}
		}//  else  --  end.
		//���濪ʼ�л�����������
		ilbAdapter = new ImageListBaseAdapter(this, listFiltrate);
		gvList.setAdapter(ilbAdapter);
	} // showSpecifyLevel()  --  end.
	
	/**
	 * ����ImageListActivity��ɾ����ĳЩͼƬ�������Ҫ����ImageListActivity<br/>
	 * �е���������⻹��Ҫ����MainActivity�еĶ�Ӧ����Ϣ��<br/>
	 * �������������þ������ڸ���MainActivity�е����ݵġ�
	 * */
	private void refreshMainActivityImgCount(){
		//ɾ��������Ƭ����Ҫ����һ��MainActivity�е�������Ϣ��
		FgmtSortByFolder.getInstance().refreshData(FgmtSortByFolder.getInstance().getCurrentSelectedPos());
	}
	
	/**
	 * ɾ������������ģʽ��ѡ�е�ͼƬ��
	 * */
	private void delImage(){
		Log.v("mylog", "ImageListActivity - delImage.");
		HashSet<Integer> delSet = new HashSet<>();
		ArrayList<Integer> delList = new ArrayList<>();
		//��ImageListBaseAdapter�в�����list���������뱾���еļ���������ͬһ���󣬹�ֱ�Ӵӱ�����ȡ���ɡ�
		for(int i=0;i<listDate.size();i++){
			Log.v("mylog", "pos:"+i+"  checkbox state:"+listDate.get(i).getIsSelected());
			//�����ǰͼƬ��CheckBox����ѡ�����¼������
			if(listDate.get(i).getIsSelected()){
				delSet.add(i);
				delList.add(i);
			}
		}
		//�����Ҫɾ����ɸѡ��
		FileDelete fileDelete = new FileDelete(getContentResolver());
		//�Ȱ�GridView���������ÿա�
		gvList.setAdapter(null);
		ilbAdapter = null;
		Log.d("mylog", "del set size:"+delSet.size()+" delList size:"+delList.size());
		//������ִ��ɾ���ļ��Ĳ�����
		for(int i=0;i<delList.size();i++){
			fileDelete.setPath(listDate.get(delList.get(i)-i/*��� -i �ǳ���Ҫ��
			���û������ɾ�����ͼƬʱ�Ἣ�п��ܱ�Խ���쳣*/).getPath());
			fileDelete.delete();
			//�ѵ�n����ɾ���������Ƴ���
			listDate.remove(delList.get(i)-i);
		}//for  --  end.
		ilbAdapter = new ImageListBaseAdapter(this, listDate);
		gvList.setAdapter(ilbAdapter);
	}// delImage  --  end.
	
	/**
	 * ʹ��Activity�ڡ�����������롰��������������ģʽ���л���
	 * */
	private void switchMultiSelect(){
		if(isMultiSelectMode){
			isMultiSelectMode = false;
			popMenu.getMenu().getItem(0).setEnabled(true);
			//��������취��ÿ��ͼƬ��Item�ϵ�CheckBox����������
			ilbAdapter.switchCheckBoxState(isMultiSelectMode);
		} else { //�����ǰ��δ���ڶ�ѡģʽ������
			isMultiSelectMode = true;
			popMenu.getMenu().getItem(0).setEnabled(false);
			//������취��ÿ��ͼƬItem�ϵ�CheckBox��ʾ������
			ilbAdapter.switchCheckBoxState(isMultiSelectMode);
		}
	}  // switchMultiSelect  --  end.
	
	/**
	 * ������ʼ��������
	 * */
	private void addContainer() {
		gvList = (GridView) LayoutInflater.from(this).inflate(R.layout.gridview, null);
		gvList.setVerticalSpacing(10);
		//�����GridView������ӵ�ĸ������ȥ��
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
			//ͼƬ���ϵĶ���ֱ����FgmtSortByFolder������ȡ���ɡ�
			startActivity(intent);
		}
		
	}

}
