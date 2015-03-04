package com.sunteorum.pinktoru;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

public class HomeActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@SuppressLint("InflateParams")
	protected void showPopupInfo(String[] strings) {
		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.common_list, null);
		
		final ListView lstPop = (ListView) layout.findViewById(android.R.id.list);
		lstPop.setCacheColorHint(Color.TRANSPARENT);
		
		//final Handler mHandler = new Handler();
		final PopupWindow mPop = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mPop.setAnimationStyle(android.R.style.Animation_Dialog);
		mPop.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_selector_3));
	 	mPop.setOutsideTouchable(true);
	 	mPop.setFocusable(true);
	 	mPop.showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 20);
	 	mPop.update();
	 	
	 	lstPop.setAdapter(new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, strings));
	 	
	 	
		
	}
	
}
