package com.yyf.imagebrowser.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FgmtSortByTimeLine extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		TextView tv = new TextView(getActivity());
		tv.setText("sort by time line fgmt");
		
		return tv;
	}

	

}
