package com.sunteorum.pinktoru;

import java.io.File;
import java.util.ArrayList;

import com.sunteorum.pinktoru.adapter.FlowImageAdapter;
import com.sunteorum.pinktoru.db.DataBean;
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
	
	PinkToru app = (PinkToru) this.getApplication();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_flow);
		
		galleryFlow = (GalleryFlow) findViewById(R.id.gallery_image_flow);
        galleryFlow.setAdapter(adpater);
        galleryFlow.setOnItemClickListener(this);
        //galleryFlow.setOnItemSelectedListener(this);
        
        btnOK = (Button)findViewById(R.id.button_ok);
        btnOK.setOnClickListener(this);
        
        btnReturn = (Button)findViewById(R.id.button_return);
        btnReturn.setOnClickListener(this);
        
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) return;
		
		Uri uri;
		
		switch (requestCode) {
		case 1:
			uri = data.getData();
			String s = Common.getUriFilePath(PictureFlowActivity.this, uri);
			Intent i = new Intent(PictureFlowActivity.this, app.getGameClass(1));
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
			Intent i2 = new Intent(PictureFlowActivity.this, app.getGameClass(1));
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
			capimgPath = Common.getUriFilePath(PictureFlowActivity.this, uri);
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		String imgpath = imageList.get(position);
		
		gotoPlayTheGame(new LevelEntity());
	}

	@Override
	public void onClick(View v) {
		int currentViewID = ((Button)v).getId();
		Intent i = null;
		switch (currentViewID) {
		case R.id.button_ok:
			if (imageList == null || imageList.size() == 0) {
				
				return;
			}
			String imgpath = imageList.get(galleryFlow.getSelectedItemPosition());
			
			gotoPlayTheGame(new LevelEntity());
			
			break;
		case R.id.button_return:
			startActivity(i);
			
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
			new AlertDialog.Builder(this)
			.setTitle("")
			.setIcon(android.R.drawable.ic_menu_gallery)
			.setPositiveButton("" ,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					startPickIntent(3);
				}
			})
			.setNegativeButton("", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					startCropIntent(4);
				}
			})
			.create().show();
		}
		
	}

	protected void loadImageList() {
		
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void gotoPlayTheGame(LevelEntity le) {
		Intent i = new Intent(this, app.getGameClass(le.getGameMode()));
		i.setAction("NEW_GAME_ACTION");
		
		DataBean db = DataBean.getInstance(this);
		Cursor cursor = db.getEntry("pt_level", "level_id=" + le.getLevelId());
		cursor.moveToFirst();
		int row = cursor.getInt(cursor.getColumnIndexOrThrow("piece_row"));
		int line = cursor.getInt(cursor.getColumnIndexOrThrow("piece_line"));
		cursor.close();
		db.close();
		
		Bundle bundle = new Bundle();
		bundle.putInt("imageId", le.getImageId());
		bundle.putInt("levelId", le.getLevelId());
		bundle.putInt("level", 1);
		bundle.putInt("row", row);
		bundle.putInt("line", line);
		bundle.putString("imageUrl", le.getImageUrl());
		//bundle.putString("imagePath", le.getGameDesc());
		
		i.putExtras(bundle);
		startActivity(i);
		
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		
		if (!((PinkToru) getApplication()).offline) finish();
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

	protected void startUploadPic(final File imgfile, final String url) {
		if (imgfile == null || !imgfile.exists()) {
			Toast.makeText(this, "文件未找到！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		View view = this.getLayoutInflater().inflate(R.layout.sample_image_item, null);
		ImageView imgItem = (ImageView) view.findViewById(R.id.imageview_sample);
		imgItem.setImageURI(Uri.fromFile(imgfile));
		
		new AlertDialog.Builder(this)
		.setTitle("是否开始上传该图片？")
		.setView(view)
		.setIcon(android.R.drawable.ic_input_add)
		.setPositiveButton("" ,new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					final ProgressDialog progd = ProgressDialog.show(PictureFlowActivity.this, null, "", true, true);
					new Thread(new Runnable() {

						@Override
						public void run() {
							String result = "";
							try {
								//Thread.sleep(5000);
								
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							progd.dismiss();
						}
						
					}).start();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}).setNegativeButton("",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
				
			}
		}).create().show();
		
	}

}
