package com.sunteorum.pinktoru;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.Time;
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

import com.sunteorum.pinktoru.util.Common;
import com.sunteorum.pinktoru.util.ImageUtils;
import com.sunteorum.pinktoru.view.SquareGridView;

public class HomeActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	Button btnLocal, btnAll, btnMore;
	SquareGridView gridNew, gridPop, gridLast;
	
	PinkToru app = (PinkToru) this.getApplication();
	String capimgPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_home);
		
        
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
		if (resultCode != RESULT_OK) return;
		
		Uri uri;
		
		switch (requestCode) {
		case 1:
			uri = data.getData();
			String s = Common.getUriFilePath(HomeActivity.this, uri);
			Intent i = new Intent(HomeActivity.this, app.getGameClass(1));
			i.setAction("NEW_GAME_ACTION");
			
			Bundle bundle = new Bundle();
			bundle.putInt("imageId", Math.abs(s.hashCode()));
			bundle.putString("imagePath", s);
			
			i.putExtras(bundle);
			startActivity(i);
    		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    		
    		if (!app.offline) finish();
			break;
		case 2:
			Intent i2 = new Intent(HomeActivity.this, app.getGameClass(1));
			i2.setAction("NEW_GAME_ACTION");
			
			Bundle bundle2 = new Bundle();
			bundle2.putInt("imageId", Math.abs(capimgPath.hashCode()));
			bundle2.putString("imagePath", capimgPath);
			
			i2.putExtras(bundle2);
			startActivity(i2);
    		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    		
    		if (!app.offline) finish();
			break;
		case 3:
			uri = data.getData();
			capimgPath = Common.getUriFilePath(HomeActivity.this, uri);
			Bitmap bm = BitmapFactory.decodeFile(capimgPath);
			File f = new File(app.getAppImageDir(), (new File(capimgPath)).getName());
			ImageUtils.saveBitmap(bm, f, true, CompressFormat.JPEG);
			capimgPath = f.getAbsolutePath();
			
			startActivityForResult(Common.cropImageUri(Uri.fromFile(f), 3, 5, 480, 800), 9);
			break;
		case 4:
			
			startActivityForResult(Common.cropImageUri(Uri.fromFile(new File(capimgPath)), 3, 5, 480, 800), 9);
			break;
		case 9:
			Bitmap bmp = data.getParcelableExtra("data");
			
			if (bmp == null) {
				//startUploadPic(new File(capimgPath));
			} else {
				Common.showTip(this, "", "");
			}
			
			break;
		}
		
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_all:
			
			
			break;
		case R.id.btn_loc:
			new AlertDialog.Builder(HomeActivity.this)
			.setTitle("选择来源")
			.setIcon(android.R.drawable.ic_menu_gallery)
			.setPositiveButton("本地图片", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					startPickIntent(1);
				}
			})
			.setNegativeButton("相机拍照", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					startCropIntent(2);
				}
			})
			.create().show();
			
			break;
		case R.id.btn_more:
			
			
			
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
		
		btnAll = (Button) findViewById(R.id.btn_all);
		btnAll.setOnClickListener(this);
		
		btnMore = (Button) findViewById(R.id.btn_more);
		btnMore.setOnClickListener(this);
		
		((Button) findViewById(R.id.btn_msg)).setOnClickListener(this);
		((Button) findViewById(R.id.btn_login)).setOnClickListener(this);
		
		//lstView = (ListView) findViewById(R.id.lst_prize);
		//lstView.setCacheColorHint(Color.TRANSPARENT);
		gridNew = (SquareGridView) findViewById(R.id.grid_new_list);
		gridNew.setCacheColorHint(Color.TRANSPARENT);
		gridNew.setOnItemClickListener(this);
		gridPop = (SquareGridView) findViewById(R.id.grid_pop_list);
		gridPop.setCacheColorHint(Color.TRANSPARENT);
		gridPop.setOnItemClickListener(this);
		gridLast = (SquareGridView) findViewById(R.id.grid_last_list);
		gridLast.setCacheColorHint(Color.TRANSPARENT);
		gridLast.setOnItemClickListener(this);
		
		//padapter = new PrizeAdapter(this, getPrizeList(), true);
		//lstView.setAdapter(padapter);
		//gridView.setAdapter(padapter);
		
		//Common.setListViewHeightBasedOnChildren(lstView);
		

	}

	private void startPickIntent(int flag) {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		
		this.startActivityForResult(intent, flag);
	}
	
	private void startCropIntent(int flag) {
		if (!Common.hasSDCard()) {
			Toast.makeText(this, "未找到可用的存储卡！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Time time = new Time();
		time.setToNow();
		String name = "" + time.hashCode() + ".jpg";
		File f = new File(app.getAppImageDir(), "camera");
		if (!f.exists()) f.mkdirs();
		
		Uri imageUri = Uri.fromFile(new File(f, name));
		capimgPath = imageUri.getPath();
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		
		startActivityForResult(intent, flag);
		
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
