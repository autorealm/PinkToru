package com.sunteorum.pinktoru;

import java.io.File;
import java.text.DecimalFormat;
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
import android.text.InputType;
import android.text.format.Time;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

public class PictureFlowActivity extends BaseActivity implements OnItemClickListener, 
		OnItemLongClickListener, OnClickListener {
	private Button btnOK = null;
	private Button btnReturn = null;
	
	private GalleryFlow galleryFlow = null;
	private FlowImageAdapter adapter = null;
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
        galleryFlow.setAdapter(adapter);
        galleryFlow.setOnItemClickListener(this);
        //galleryFlow.setOnItemSelectedListener(this);
        galleryFlow.setOnCreateContextMenuListener(this);
        
        btnOK = (Button)findViewById(R.id.button_ok);
        btnOK.setOnClickListener(this);
        
        btnReturn = (Button)findViewById(R.id.button_return);
        btnReturn.setOnClickListener(this);
        
        ((Button)findViewById(R.id.button_review)).setOnClickListener(this);
        
        this.setTitle("����ͼƬ��");
        
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
		if (adapter != null) adapter.clearCacheBitmap();
		adapter = null;
		imageList = null;
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "ˢ��");
		SubMenu sm1 = menu.addSubMenu(1, 2, 0, "����ͼƬ");
		sm1.add(1, 11, 0, "���ѡȡ");
		sm1.add(1, 12, 0, "���ջ��");
		menu.add(0, 3, 0, "����");
		
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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		menu.add(0, 1, 1, "��Ϊ");
		menu.add(0, 2, 2, "������");
		menu.add(0, 3, 3, "ɾ��");
		menu.add(0, 4, 4, "����");
		
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();   
		final int id = (int) info.id;
		final File sfile = new File(Uri.parse(imageList.get(id)).getPath());
		switch (item.getItemId()) {
		case 1:
			Uri uri = Uri.fromFile(sfile);
			Intent it = new Intent(Intent.ACTION_VIEW);
			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			it.setDataAndType(uri, "image/*");
			startActivity(Intent.createChooser(it, null));
			
			break;
		case 2:
			renameFile(sfile, id);
			
			break;
		case 3:
			new AlertDialog.Builder(this)
			.setTitle("��ȷ��")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setMessage("�Ƿ�ɾ���ļ���" + sfile.getAbsolutePath() + " ��")
			.setPositiveButton(R.string.btn_cancel, null)
			.setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (sfile.delete()) {
						imageList.remove(id);
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(PictureFlowActivity.this, "ɾ���ļ� ʧ��", Toast.LENGTH_SHORT).show();
					}
				}
				
			})
			.show();
			
			break;
		case 4:
			showFileInfo(sfile);
			
			break;
		default:
			Toast.makeText(PictureFlowActivity.this, "δ�ṩ", Toast.LENGTH_SHORT).show();
			
		}
		
		return super.onContextItemSelected(item);
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
			
			startActivityForResult(Common.cropImageUri(Uri.fromFile(f), 3, 5, app.DEF_WITCH, app.DEF_WITCH), 9);
			break;
		case 4:
			
			startActivityForResult(Common.cropImageUri(Uri.fromFile(new File(capimgPath)), 3, 5, app.DEF_WITCH, app.DEF_HEIGHT), 9);
			
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
		itn.putExtra("review_file", imgpath);
		startActivity(itn);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
		return false;
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
			
			gotoPlayTheGame(new GameEntity(1, app.getGameMode(), "Ĭ�ϱ���ͼƬ��Ϸ", imgpath));
			
			break;
		case R.id.button_return:
			startActivity(new Intent(this, HomeActivity.class));
    		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    		
			finish();
			
			break;
		case 0:
			new AlertDialog.Builder(PictureFlowActivity.this)
			.setTitle("ѡ����Դ")
			.setIcon(android.R.drawable.ic_menu_gallery)
			.setPositiveButton("����ͼƬ", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					startPickIntent(1);
				}
			})
			.setNegativeButton("�������", new DialogInterface.OnClickListener() {
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
			final ProgressDialog progd = ProgressDialog.show(this, null, "���ڼ��ر���ͼƬ...", true, true);
			
			galleryFlow.postDelayed(new Runnable() {

				@Override
				public void run() {
					imageList.clear();

					listLocalImages(app.getAppImageDir().getAbsolutePath());
					
					File dir = android.os.Environment.getExternalStorageDirectory();
					
					dir = new File(dir, "ͼƬ");
					listLocalImages(dir.getAbsolutePath());
					
					adapter = new FlowImageAdapter(app, imageList);
					adapter.setBackRes(R.drawable.itemshape_7);
					galleryFlow.setAdapter(adapter);
					
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
			Common.showToast(this, "���ݳ����ˣ�");
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
			.setTitle("���ͼƬ")
			.setIcon(android.R.drawable.ic_menu_gallery)
			.setPositiveButton("���ѡȡ" ,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					startPickIntent(3);
				}
			})
			.setNegativeButton("���ջ�ȡ", new DialogInterface.OnClickListener() {
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
			Toast.makeText(this, "δ�ҵ����õĴ洢����", Toast.LENGTH_SHORT).show();
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
			.setTitle("δ�ҵ��û���¼��Ϣ")
			.setMessage("ʹ�øù�����Ҫע���Ա�û������ȵ�¼��")
			.setIcon(android.R.drawable.ic_dialog_info)
			.setPositiveButton("���ڵ�¼" ,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent i = new Intent(PictureFlowActivity.this, LoginActivity.class);
					startActivity(i);
		    		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
					
				}
			}).setNegativeButton("ע���Ա" ,new DialogInterface.OnClickListener() {
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
			Toast.makeText(this, "�ļ�δ�ҵ���", Toast.LENGTH_SHORT).show();
			return;
		}
		
		View view = View.inflate(this, R.layout.sample_image_item, null);
		ImageView imgItem = (ImageView) view.findViewById(R.id.imageview_sample);
		imgItem.setImageURI(Uri.fromFile(imgfile));
		
		new AlertDialog.Builder(this)
		.setTitle("�Ƿ񽫸�ͼƬ����ͼ�⣿")
		.setView(view)
		.setIcon(android.R.drawable.ic_input_add)
		.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					final ProgressDialog progd = ProgressDialog.show(PictureFlowActivity.this, null, "���Ժ�...", true, true);
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

	protected void renameFile(final File sfile, final int p) {
		final EditText edt = new EditText(this);
		edt.setText(sfile.getName());
		edt.setInputType(InputType.TYPE_CLASS_TEXT);
		
		new AlertDialog.Builder(this)
		.setTitle("������")
		.setView(edt)
		.setIcon(android.R.drawable.ic_menu_edit)
		.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				File nfile = new File(sfile.getParent() + "/" + edt.getText().toString());
				
				if (sfile.renameTo(nfile)) {
					imageList.get(p).replace(imageList.get(p), Uri.fromFile(nfile).toString());
					
					adapter.notifyDataSetChanged();
				} else {
					System.out.println("<rename error!> " + sfile.getName());
				}
			}})
		.setNegativeButton("ȡ��", null)
		.show();
		
	}

	protected void showFileInfo(final File sfile) {
		new AlertDialog.Builder(this)
		.setTitle("�ļ���Ϣ")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setMessage(getFileInfo(sfile))
		.show();
		
	}

	protected String getFileInfo(File sfile) {
		String info = "";
		if (!sfile.exists()) return info;
		Time t = new Time();
		t.set(sfile.lastModified());
		DecimalFormat fnum = new DecimalFormat("##0.00"); 
		String ftype = "", flen = "";
		if (sfile.isFile()) {ftype = "�ļ�"; 
			if (sfile.length() < 1024 * 1024)
			flen = fnum.format((float)sfile.length() / 1024) + " KB";
			else flen = fnum.format((float)sfile.length() / 1024 / 1024) + " MB";
		}
		else if (sfile.isDirectory()) {
			File[] files = sfile.listFiles();
			ftype = "�ļ���";
			if (files != null) flen = "����  " + files.length + " ������";
			else flen = "NULL";
			}
		
		info = "���ƣ� " + sfile.getName() + "\n" +
			"���ͣ� " + ftype + "\n" +
			"·���� " + sfile.getParent() + "\n" +
			"��С�� " + flen + "\n" +
			"�޸�ʱ�䣺 " + t.format("%Y-%m-%d %H:%M:%S") + "\n\n" +
			"�ɶ��� " + (sfile.canRead()?"��":"��") + "\t" +
			" ��д�� " + (sfile.canWrite()?"��":"��") + "\n" +
			"ִ�У� " + (sfile.canExecute()?"��":"��") + "\t" +
			" ���أ� " + (sfile.isHidden()?"��":"��") + "\n";
		
		return info;
	}

}
