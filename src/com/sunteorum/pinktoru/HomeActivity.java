package com.sunteorum.pinktoru;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.sunteorum.pinktoru.view.SlideLinearLayout;
import com.sunteorum.pinktoru.view.SquareGridView;

public class HomeActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	Button btnLocal, btnCustom, btnMore;
	SquareGridView gridNew, gridPop, gridLast;
	SlideLinearLayout slideDrawer;
	
	PinkToru app = (PinkToru) this.getApplication();
	
	private long curtime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_home);
		
		init();
        
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		curtime = System.currentTimeMillis();
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "刷新");
		SubMenu sm1 = menu.addSubMenu(1, 2, 0, "本地图片");
		sm1.add(1, 11, 0, "相册选取");
		sm1.add(1, 12, 0, "拍照获得");
		menu.add(0, 3, 0, "设置");
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			
			break;
		case 1:
			
			break;
		case 3:
			
			break;
		case 11:
			
			break;
		case 12:
			
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		
	}

	@Override
	public void onBackPressed() {
		if (slideDrawer.isLeftLayoutVisible() || slideDrawer.getVisibility() == 0) {
			slideDrawer.scrollToRightLayout();
		} else {
			if ((System.currentTimeMillis() - curtime) > 2000) {
				Toast.makeText(this, "再次按返回键退出", Toast.LENGTH_SHORT).show();
				curtime = System.currentTimeMillis();
			} else {
				super.onBackPressed();
			}
		}
		
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_cus:
			startActivity(new Intent(getApplicationContext(), CustomActivity.class));
			
			break;
		case R.id.btn_loc:
			startActivity(new Intent(getApplicationContext(), PictureFlowActivity.class));
			break;
		case R.id.btn_more:
			
			break;
		case R.id.btn_login:
			startActivity(new Intent(getApplicationContext(), LoginActivity.class));
			break;
		case R.id.btn_msg:
			
			break;
		case R.id.btn_renew:
			startActivity(new Intent(getApplicationContext(), MainActivity.class));
			break;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg0.getId()) {
		case R.id.grid_last_list:
			//String purl = M_URL + "images/f1.jpg";
			//int rnd = (int) ((Math.random()*imgList.size()));
			
			
			//preStartGame(padapter.getPrizeEntityAtIndex(arg2));
			
			
			break;
		case R.id.grid_new_list:
			
			
			
			break;
		case R.id.grid_pop_list:
			
			
			
			break;
		}
		
	}

	private void init() {
		//获得屏幕的宽和高
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		float density = dm.density;
		
		btnLocal = (Button) findViewById(R.id.btn_loc);
		btnLocal.setOnClickListener(this);
		
		btnCustom = (Button) findViewById(R.id.btn_cus);
		btnCustom.setOnClickListener(this);
		
		btnMore = (Button) findViewById(R.id.btn_more);
		btnMore.setOnClickListener(this);
		
		((Button) findViewById(R.id.btn_msg)).setOnClickListener(this);
		((Button) findViewById(R.id.btn_login)).setOnClickListener(this);
		((Button) findViewById(R.id.btn_renew)).setOnClickListener(this);
		
		gridNew = (SquareGridView) findViewById(R.id.grid_new_list);
		gridNew.setCacheColorHint(Color.TRANSPARENT);
		gridNew.setOnItemClickListener(this);
		gridPop = (SquareGridView) findViewById(R.id.grid_pop_list);
		gridPop.setCacheColorHint(Color.TRANSPARENT);
		gridPop.setOnItemClickListener(this);
		gridLast = (SquareGridView) findViewById(R.id.grid_last_list);
		gridLast.setCacheColorHint(Color.TRANSPARENT);
		gridLast.setOnItemClickListener(this);
		
		slideDrawer = (SlideLinearLayout) findViewById(R.id.slide_drawer);
		slideDrawer.setScrollEvent(this.getWindow().getDecorView());

		//lstView = (ListView) findViewById(R.id.lst_prize);
		//lstView.setCacheColorHint(Color.TRANSPARENT);
		//padapter = new PrizeAdapter(this, getPrizeList(), true);
		//lstView.setAdapter(padapter);
		//Common.setListViewHeightBasedOnChildren(lstView);
		
		
		
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
