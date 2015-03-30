package com.sunteorum.pinktoru;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import com.sunteorum.pinktoru.adapter.FlowImageAdapter;
import com.sunteorum.pinktoru.entity.GameEntity;
import com.sunteorum.pinktoru.entity.LevelEntity;
import com.sunteorum.pinktoru.entity.UserEntity;
import com.sunteorum.pinktoru.util.Common;
import com.sunteorum.pinktoru.util.ImageUtils;
import com.sunteorum.pinktoru.view.GalleryFlow;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

public class PictureFlowActivity extends BaseActivity implements OnItemClickListener, OnClickListener {
	private Button btnOK = null;
	private Button btnReturn = null;
	
	private GalleryFlow galleryFlow = null;
	private FlowImageAdapter adpater = null;
	//private Integer[] imagesID = null;
	
	private ArrayList<String> imageList = new ArrayList<String>();
	private String capimgPath = "";
	
	PinkToru app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_flow);
		
		app = (PinkToru) this.getApplication();
		
		galleryFlow = (GalleryFlow) findViewById(R.id.gallery_image_flow);
        galleryFlow.setAdapter(adpater);
        galleryFlow.setOnItemClickListener(this);
        //galleryFlow.setOnItemSelectedListener(this);
        
        btnOK = (Button)findViewById(R.id.button_ok);
        btnOK.setOnClickListener(this);
        
        btnReturn = (Button)findViewById(R.id.button_return);
        btnReturn.setOnClickListener(this);
        
        ((Button)findViewById(R.id.button_review)).setOnClickListener(this);
        
        this.setTitle("本地图片库");
        
        loadImageList();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((PinkToru) getApplication()).init();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (adpater != null) adpater.clearCacheBitmap();
		adpater = null;
		imageList = null;
		
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
			loadImageList();
			break;
		case 11:
			startPickIntent(1);
			
			break;
		case 12:
			startCropIntent(2);
			break;
		case 3:
			startActivity(new Intent(this, SystemSetting.class));
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
			String s = Common.getUriFilePath(PictureFlowActivity.this, uri);
			System.out.println(s);

			gotoPlayTheGame(new GameEntity(1, 1, "", s));
    		
			break;
		case 2:
			
			gotoPlayTheGame(new GameEntity(1, 3, "", capimgPath));
			
			break;
		case 3:
			uri = data.getData();
			capimgPath = Common.getUriFilePath(PictureFlowActivity.this, uri);
			Bitmap bm = BitmapFactory.decodeFile(capimgPath);
			File f = new File(app.getAppImageDir(), (new File(capimgPath)).getName());
			ImageUtils.saveBitmap(bm, f, true, CompressFormat.JPEG);
			capimgPath = f.getAbsolutePath();
			
			startActivityForResult(Common.cropImageUri(Uri.fromFile(f), 3, 5, 540, 960), 9);
			break;
		case 4:
			
			startActivityForResult(Common.cropImageUri(Uri.fromFile(new File(capimgPath)), 3, 5, 540, 960), 9);
			
			break;
		case 9:
			Bitmap bmp = data.getParcelableExtra("data");
			
			if (bmp == null) {
				addImageToGallery(new File(capimgPath));
			} else {
				Common.showTip(this, null, "read bitmap data error!");
			}
			
			break;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		String imgpath = imageList.get(position);
		
		Intent itn = new Intent(this, ReviewActivity.class);
		itn.putExtra("review_file", imageList.get(galleryFlow.getSelectedItemPosition()));
		startActivity(itn);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
	}

	@Override
	public void onClick(View v) {
		int currentViewID = ((Button)v).getId();
		
		switch (currentViewID) {
		case R.id.button_review:
			Intent itn = new Intent(this, ReviewActivity.class);
			itn.putExtra("review_file", imageList.get(galleryFlow.getSelectedItemPosition()));
			startActivity(itn);
    		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    		
			break;
		case R.id.button_ok:
			if (imageList == null || imageList.size() == 0) {
				
				return;
			}
			String imgpath = imageList.get(galleryFlow.getSelectedItemPosition());
			
			gotoPlayTheGame(new GameEntity(1, app.getGameMode(), "默认本地图片游戏", imgpath));
			
			break;
		case R.id.button_return:
			startActivity(new Intent(this, HomeActivity.class));
    		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    		
			finish();
			
			break;
		case 0:
			new AlertDialog.Builder(PictureFlowActivity.this)
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
		default:
			if (!validAccount()) {
				
				return;
			}
			
		}
		
	}

	protected void loadImageList() {
		
		try {
			final ProgressDialog progd = ProgressDialog.show(this, null, "正在加载本地图片...", true, true);
			
			galleryFlow.postDelayed(new Runnable() {

				@Override
				public void run() {
					imageList.clear();
					
					File dir = android.os.Environment.getExternalStorageDirectory();
					
					dir = new File(dir, "图片");
					listLocalImages(dir.getAbsolutePath());
					
					dir = app.getAppImageDir();
					listLocalImages(dir.getAbsolutePath());
					
					adpater = new FlowImageAdapter(app, imageList);
					adpater.setBackRes(R.drawable.itemshape_7);
					galleryFlow.setAdapter(adpater);
					
					progd.dismiss();
				}
				
			}, 200);
			
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void listLocalImages(String path) {
		File sfile = new File(path);
		if (!sfile.exists() || !sfile.isDirectory()) return;
		
		File[] files = sfile.listFiles();
		for (File tfile:files) {
			if (tfile.isDirectory()) {listLocalImages(tfile.getAbsolutePath());}
			String tname = tfile.getName().toLowerCase(Locale.getDefault());
			if (tname.endsWith(".jpg") || tname.endsWith(".jpeg") || tname.endsWith(".png")
					 || tname.endsWith(".bmp")) {
				String f = Uri.fromFile(tfile).toString();
				System.out.println(f);
				imageList.add(f);
				
			}
		}
		
	}
	
	public void gotoPlayTheGame(GameEntity ge) {
		Intent i = new Intent(this, app.getGameClass(ge.getGameMode()));
		i.setAction("NEW_GAME_ACTION");
		
		LevelEntity le = app.getLevelById(1);
		if (le == null) {
			Common.showToast(this, "数据出错了！");
			return;
		}
		
		Bundle bundle = new Bundle();
		bundle.putInt("gameId", 1);
		bundle.putInt("levelId", 1);
		bundle.putInt("stage", 1);
		bundle.putInt("row", le.getPieceRow());
		bundle.putInt("line", le.getPieceLine());
		bundle.putInt("imageId", ge.getGameImageId());
		bundle.putString("imageUri", ge.getGameImageUrl());
		
		i.putExtras(bundle);
		startActivity(i);
		
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		
		if (!((PinkToru) getApplication()).offline) finish();
	}
	
	public void addMore(View view) {
		new AlertDialog.Builder(this)
			.setTitle("添加图片")
			.setIcon(android.R.drawable.ic_menu_gallery)
			.setPositiveButton("相册选取" ,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					startPickIntent(3);
				}
			})
			.setNegativeButton("拍照获取", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					startCropIntent(4);
				}
			})
			.create().show();
	}
	
	protected String getUriFilePath(Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor actualimagecursor = this.managedQuery(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor.getString(actual_image_column_index);
		//File file = new File(img_path);
		//actualimagecursor.close();
		
		return img_path;
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

	protected boolean validAccount() {
		UserEntity ue = ((PinkToru) this.getApplication()).getUser();
		if ( ue == null) {
			new AlertDialog.Builder(this)
			.setTitle("未找到用户登录信息")
			.setMessage("使用该功能需要注册会员用户，请先登录。")
			.setIcon(android.R.drawable.ic_dialog_info)
			.setPositiveButton("现在登录" ,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent i = new Intent(PictureFlowActivity.this, LoginActivity.class);
					startActivity(i);
		    		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
					
				}
			}).setNegativeButton("注册会员" ,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent i = new Intent(PictureFlowActivity.this, RegisterActivity.class);
					startActivity(i);
		    		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
					
				}
			}).create().show();
			
			return false;
		} else {
			//if (ue.getPoints() > 0) return true;
			
		}
		
		return false;
	}

	protected void addImageToGallery(final File imgfile) {
		if (imgfile == null || !imgfile.exists()) {
			Toast.makeText(this, "文件未找到！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		View view = View.inflate(this, R.layout.sample_image_item, null);
		ImageView imgItem = (ImageView) view.findViewById(R.id.imageview_sample);
		imgItem.setImageURI(Uri.fromFile(imgfile));
		
		new AlertDialog.Builder(this)
		.setTitle("是否将该图片放入图库？")
		.setView(view)
		.setIcon(android.R.drawable.ic_input_add)
		.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					final ProgressDialog progd = ProgressDialog.show(PictureFlowActivity.this, null, "请稍后...", true, true);
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								Thread.sleep(1200);
								
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							progd.dismiss();
						}
						
					}).start();
					
					loadImageList();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
				
			}
		}).create().show();
		
	}

}
