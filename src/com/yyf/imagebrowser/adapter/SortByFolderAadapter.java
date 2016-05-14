package com.yyf.imagebrowser.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yyf.imagebrowser.R;
import com.yyf.imagebrowser.entity.ImageEntity;
import com.yyf.imagebrowser.entity.ViewHolder;
import com.yyf.imagebrowser.tools.IBApplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ���ļ��з�����������ࡣ
 * */
public class SortByFolderAadapter extends BaseAdapter {

	private Map<String, ArrayList<ImageEntity>> map;
	/**���ڴ��Map�����еļ�����List������Ҫ����getItem(int)����*/
	private List<String> mapKeyList;
	/**����ʹgetView������������ִֻ��һ�εļ��ϡ�*/
	private HashSet<Integer> oneTimeSet;
	private int mapSize; //map���ϵ�Ԫ��������
	private Context context;
	private ViewHolder holder;
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = View.inflate(context, R.layout.sort_by_folder, null);
			holder = new ViewHolder();
			holder.iv = (ImageView) convertView.findViewById(R.id.ivSortByFolder);
			holder.tvAmount = (TextView) convertView.findViewById(R.id.tvImageAmount);
			holder.tvFolderName = (TextView) convertView.findViewById(R.id.tvFolderName);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(oneTimeSet.contains(position)){
//			Log.v("mylog", "SortByFolderAdapter - current pos was loaded. "+position);
		}else{
			//���ø��ļ��еķ���ͼ
			loadImage(holder.iv, position);
			//���ø÷��ļ�����ͼƬ������
			holder.tvAmount.setText(String.valueOf(getItem(position).size()));
			//���ø��ļ������ơ�
			holder.tvFolderName.setText(mapKeyList.get(position));
			//��ǰ�ǵ�һ�μ��ظ������ݣ���¼�Ѽ��ع�������Ŀ����ֹ�ظ������˷����ܡ�
			oneTimeSet.add(position);
		}
		
		return convertView;
	}
	
	/**
	 * װ�ر��ص�ͼƬ��
	 * */
	public void loadImage(ImageView iv, int pos){
		String uri = "file://"+getItem(pos).get(0).getPath(); //ȡ�����е�һ��ͼƬ��Ϊ����ͼ��
		ImageLoader.getInstance().displayImage(uri, iv, IBApplication.getInstance().getDisplayImageOptions());
//		Log.d("mylog", "SortByFolderAdapter - loadImageFinished.");
	}
	
	@Override
	public int getCount() {
		return mapSize;
	}

	@Override
	public ArrayList<ImageEntity> getItem(int position) {
		return map.get(mapKeyList.get(position));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public SortByFolderAadapter(Context context, Map<String, ArrayList<ImageEntity>> map){
		this.map = map;
		this.context = context;
		mapSize = this.map.size();
		mapKeyList = new ArrayList<>(map.keySet());
		oneTimeSet = new HashSet<>();
	}

}
