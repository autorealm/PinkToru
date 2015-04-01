package com.sunteorum.pinktoru;

import java.util.ArrayList;
import java.util.Arrays;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
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
	SquareGridView gridNew, gridPop, gridMy, gridLast;
	SlideLinearLayout slideDrawer;
	
	PinkToru app = (PinkToru) this.getApplication();
	
	private long curtime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_home);

		curtime = System.currentTimeMillis();
		
		init();
        
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "刷新");
		SubMenu sm1 = menu.addSubMenu(1, 2, 0, "更多");
		sm1.add(1, 11, 0, "调试");
		sm1.add(1, 12, 0, "关于");
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
			startActivity(new Intent(HomeActivity.this, SystemSetting.class));
			
			break;
		case 11:
			SharedPreferences sdf = this.getSharedPreferences("CrashHandler", Context.MODE_PRIVATE);
			final Object ks[] = sdf.getAll().keySet().toArray();
			Arrays.sort(ks);
			ArrayList<String> str = new ArrayList<String>();
			for (int i = 0; i < ks.length; i++) {
				str.add(ks[i].toString());
			}
			ArrayAdapter<String> aa = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item,str);
			
			new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.star_big_on)
				.setTitle("Bug 列表")
				.setSingleChoiceItems(aa, 0, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						showPopupInfo(new String[] {ks[which].toString()});
						
					}
					
				})
				.setNegativeButton("关闭", null)
				.show();
			
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
		//if (slideDrawer.isLeftLayoutVisible() || slideDrawer.getVisibility() == 0) {
			//slideDrawer.scrollToRightLayout();
		//} else {
			if ((System.currentTimeMillis() - curtime) > 2000) {
				Toast.makeText(this, "再次按返回键退出", Toast.LENGTH_SHORT).show();
				curtime = System.currentTimeMillis();
			} else {
				super.onBackPressed();
			}
		//}
		
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
		case R.id.btn_new_lay:
					
			break;
		case R.id.btn_pop_lay:
			
			break;
		case R.id.btn_my_lay:
			
			break;
		case R.id.btn_last_lay:
			
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
		
		((Button) findViewById(R.id.btn_new_lay)).setOnClickListener(this);
		((Button) findViewById(R.id.btn_pop_lay)).setOnClickListener(this);
		((Button) findViewById(R.id.btn_my_lay)).setOnClickListener(this);
		((Button) findViewById(R.id.btn_last_lay)).setOnClickListener(this);
		
		gridNew = (SquareGridView) findViewById(R.id.grid_new_list);
		gridNew.setCacheColorHint(Color.TRANSPARENT);
		gridNew.setOnItemClickListener(this);
		gridPop = (SquareGridView) findViewById(R.id.grid_pop_list);
		gridPop.setCacheColorHint(Color.TRANSPARENT);
		gridPop.setOnItemClickListener(this);
		gridMy = (SquareGridView) findViewById(R.id.grid_my_list);
		gridMy.setCacheColorHint(Color.TRANSPARENT);
		gridMy.setOnItemClickListener(this);
		gridLast = (SquareGridView) findViewById(R.id.grid_last_list);
		gridLast.setCacheColorHint(Color.TRANSPARENT);
		gridLast.setOnItemClickListener(this);
		
		//slideDrawer = (SlideLinearLayout) findViewById(R.id.slide_drawer);
		
		//slideDrawer.setScrollEvent(findViewById(android.R.id.content));

		//lstView = (ListView) findViewById(R.id.lst_prize);
		//lstView.setCacheColorHint(Color.TRANSPARENT);
		//padapter = new PrizeAdapter(this, getPrizeList(), true);
		//lstView.setAdapter(padapter);
		//Common.setListViewHeightBasedOnChildren(lstView);
		
		
		
	}

	
	protected void showPopupInfo(String[] strings) {
		//LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		//View layout = inflater.inflate(R.layout.common_list, null);
		View layout = View.inflate(this, R.layout.common_list, null);
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
