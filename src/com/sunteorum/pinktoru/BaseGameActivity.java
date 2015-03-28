package com.sunteorum.pinktoru;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.sunteorum.pinktoru.entity.GameEntity;
import com.sunteorum.pinktoru.entity.LevelEntity;
import com.sunteorum.pinktoru.entity.Piece;
import com.sunteorum.pinktoru.entity.PieceFactory;
import com.sunteorum.pinktoru.util.Common;
import com.sunteorum.pinktoru.util.ImageUtils;
import com.sunteorum.pinktoru.util.ViewUtils;
import com.sunteorum.pinktoru.view.FlippingImageView;
import com.sunteorum.pinktoru.view.MultiDirectionSlidingDrawer;
import com.sunteorum.pinktoru.view.MultiDirectionSlidingDrawer.OnDrawerCloseListener;
import com.sunteorum.pinktoru.view.MultiDirectionSlidingDrawer.OnDrawerOpenListener;
import com.sunteorum.pinktoru.view.MultiDirectionSlidingDrawer.OnDrawerScrollListener;
import com.sunteorum.pinktoru.view.PieceView;


abstract class BaseGameActivity extends BaseActivity implements OnTouchListener {

	final private String tag = "GameActivity";
	protected PinkToru app;
	protected int screenWidth;
	protected int screenHeight;
	protected Drawable background_drawalbe;
	
	protected int INACCURACY = 12;//�ж�ƴ�ϵľ���
	protected int ERR_TIP_TIMES = 9;//������ʾ����
	
	protected ArrayList<PieceView> allPieces = new ArrayList<PieceView>();
	
	protected FrameLayout puzzle = null; //������
	protected FrameLayout space = null; //��Ϸ����
	
	private ArrayList<LevelEntity> games;
	protected int imageId = 0;//ͼƬID
	protected String imageUri;//ͼƬ�ĵ�ַ
	protected String imagePath;//ͼƬ�ı���·��
	
	protected int gameId = 0;//��ϷID
	protected int levelId = 0;//�ؿ�ID
	protected int stage;//����
	protected int row;//��
	protected int line;//��
	
	protected int dx, dy; //��Ϸ����������ֵľ���
	
	protected GameEntity ge;
	protected LevelEntity le;
	protected MultiDirectionSlidingDrawer drawer;
	protected View handle;
	protected LinearLayout layGameStatus;
	protected TextView tvGameLevel, tvGameTime, tvGameStatus;
	
	protected long lctime;//���浱ǰʱ��ֵ
	protected long start_time;//���濪ʼ��Ϸʱ��ֵ
	protected Handler mHandler = new Handler();
	
	protected String game_status = "0";
	protected String game_time = "00:00";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final String action = this.getIntent().getAction();
		Log.i(tag, "action = " + action);
		
		if (this.getIntent().hasExtra("games"))
			games = getIntent().getParcelableArrayListExtra("games");
		
		Bundle bundle = this.getIntent().getExtras();
		imageId = bundle.containsKey("imageId") ? bundle.getInt("imageId") : 0;
		gameId = bundle.containsKey("imageId") ? bundle.getInt("gameId") : 1;
		levelId = bundle.containsKey("levelId") ? bundle.getInt("levelId") : 0;
		stage = bundle.containsKey("stage") ? bundle.getInt("stage") : 1;
		row = bundle.containsKey("row") ? bundle.getInt("row") : 3;
		line = bundle.containsKey("line") ? bundle.getInt("line") : 2;
		imageUri = bundle.getString("imageUri");
		
		app = (PinkToru) getApplication();
		
		Log.i(tag, "imageUri = " + imageUri);
		if (games != null) {
			if (games.size() < stage) {
				Common.showTip(this, "�������", "��Ϸ���ݶ�ȡʧ�ܣ�");
				return;
			}
			
			le = games.get(stage - 1);
			row = le.getPieceRow();
			line = le.getPieceLine();
			if (!android.text.TextUtils.isEmpty(le.getImageUrl()))
				imageUri = le.getImageUrl();
			
		}
		
		ge = app.getGameById(gameId);
		
		if ((imageUri == null || imageUri.trim().length() == 0) && ge != null)
			imageUri = ge.getGameImageUrl();
		
		if (imageUri != null && imageUri.trim().length() > 0) {
			imagePath = app.getCacheImagePath(imageUri);
			
		}
		
		Log.i(tag, "imagePath = " + imagePath);
		if (imagePath == null || imagePath.trim().length() == 0) {
			Common.showTip(this, "�������", "��ϷͼƬ��Դ��ȡʧ�ܣ�");
			return;
		}
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		
		background_drawalbe = ImageUtils.readDrawable(this, imagePath, screenWidth, screenHeight);
		lctime = System.currentTimeMillis();
		
		init();
		process();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		System.gc();
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		long l = System.currentTimeMillis() - lctime;
		
		if (l < 1600) {
			showExitDialog();
			
			//super.onBackPressed();
		} else {
			lctime = System.currentTimeMillis();
		}
		
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (allPieces != null) {
			for (int i=0; i < allPieces.size(); i++) {
				PieceView p = (PieceView) allPieces.get(i);
				if (Piece.class.isInstance(p.getTag())) {
					Piece piece = (Piece) p.getTag();
					if (!piece.getBmpPiece().isRecycled()) piece.getBmpPiece().recycle();
					if (!piece.getBmpEdge().isRecycled()) piece.getBmpEdge().recycle();
				}
			}
			allPieces.clear();
		}
		
		if (puzzle != null) puzzle.setBackgroundDrawable(null);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(0, 1, 0, R.string.btn_review);
		menu.add(0, 2, 0, R.string.exit);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			
			break;
		case 1:
			showPopupReview(3000);
			break;
		case 2:
			showExitDialog();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void showExitDialog() {
		new AlertDialog.Builder(BaseGameActivity.this)
			.setTitle("�˳���ǰ����Ϸ��")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					finish();
				}
			}).setNegativeButton("ȡ��", null)
			.create().show();
	}

	/**
	 * ��ʼ����setContentView �������ݽ��棬findViewById �ҳ��ؼ�
	 */
	abstract void init ();
	
	protected void process() {
		//��ʾ����׼����Ϸ�Ľ�����ʾ��
		final ProgressDialog progd = new ProgressDialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		View fay = View.inflate(this, R.layout.popup_game_loading, null);
		
		progd.show();
		progd.setContentView(fay, params);
		progd.getWindow().setGravity(Gravity.CENTER);
		LinearLayout lay = (LinearLayout) progd.findViewById(R.id.pgdLayout);
		lay.setOnTouchListener(new ViewUtils.TouchDragListener(null));
		
		final TextView txtName = (TextView) progd.findViewById(R.id.txtGameName);
		TextView txtDesc = (TextView) progd.findViewById(R.id.txtGameDesc);
		ImageButton btnClose = (ImageButton) progd.findViewById(R.id.btnGameClose);
		txtName.setText("׼����ʼ�� " + stage + " ��...");
		txtDesc.setText((le != null) ? le.getLevelDesc() : "");
		
		final FlippingImageView mFivIcon = (FlippingImageView) progd.findViewById(R.id.game_loading_icon);
		if (mFivIcon != null) mFivIcon.startAnimation();
		txtDesc.setMovementMethod(ScrollingMovementMethod.getInstance());
		lay.postInvalidate();
		
		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				
			}
			
		});
		
		progd.setCancelable(false);
		//progd.setMessage("" + level + "");
		progd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				
			}
			
			
		});
		
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if ("NEW_GAME_ACTION".equalsIgnoreCase(getIntent().getAction())) {
					newPuzzle(imagePath, row, line);
					
				} else if ("RETURN_GAME_ACTION".equalsIgnoreCase(getIntent().getAction())) {
					
				}
				
				if (mFivIcon != null) {
					mFivIcon.clearAnimation();
					mFivIcon.setImageResource(R.drawable.ic_android);
				}
				txtName.setText("��Ϸ�������");
				
				onStartGame();
				
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				progd.dismiss();
				
				dx = (puzzle.getWidth() - space.getWidth()) / 2;
				dy = (puzzle.getHeight() - space.getHeight()) / 2;
				start_time = System.currentTimeMillis();
				
				//�ӳ���ʾ����״̬��
				mHandler.postDelayed(new Runnable(){

					@Override
					public void run() {
						setDrawer();
						setGameTime();
						setGameStatus();
						
					}
				}, 800);
			}
			
		}, 800);
		
	}

	/**
	 * ��Ϸ��ȫ�������ʱ
	 */
	abstract void onStartGame();
	
	private void setDrawer() {
		
		if(android.os.Build.VERSION.SDK_INT >= 11) {
			//drawer.setAlpha(0.35f);
		}
		
		//handle.getBackground().setAlpha(120);
		layGameStatus.bringToFront();
		if (handle.getBackground() == null)
			handle.setBackgroundResource(R.drawable.bannershape_1);
		final Drawable da = handle.getBackground(); 
		drawer.setFocusable(true);
		drawer.setVisibility(0);
		
		drawer.setOnDrawerScrollListener(new OnDrawerScrollListener() {

			@Override
			public void onScrollStarted() {
				// TODO Auto-generated method stub
				layGameStatus.setVisibility(8);
				handle.setBackgroundResource(R.drawable.bannershape_1);
			}

			@Override
			public void onScrollEnded() {
				// TODO Auto-generated method stub
				drawer.postDelayed(new Runnable() {

					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (drawer.isOpened()) {
							drawer.bringToFront();
							drawer.postInvalidate();
						} else {
							handle.setBackgroundDrawable(da);
							layGameStatus.setVisibility(0);
							layGameStatus.bringToFront();
						}
					}
					
				}, 300);
				
			}
			
		});
		
		drawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				drawer.bringToFront();
				drawer.postInvalidate();
				layGameStatus.setVisibility(8);
				handle.setBackgroundResource(R.drawable.bannershape_1);
				
				//if(android.os.Build.VERSION.SDK_INT >= 11) drawer.setAlpha(1f);
			}
			
		});
		
		drawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onDrawerClosed() {
				// TODO Auto-generated method stub
				handle.setBackgroundDrawable(da);
				layGameStatus.setVisibility(0);
				layGameStatus.bringToFront();
				
				//if(android.os.Build.VERSION.SDK_INT >= 11) drawer.setAlpha(0.35f);
			}
			
		});
		
		//drawer.animateOpen();
		
	}
	
	private void setGameTime() {
		final Timer atimer = new Timer();
		final Time t = new Time();
        
	 	TimerTask totask = new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						//if (atimer != null) atimer.cancel();
						t.set(System.currentTimeMillis() - start_time + app.getGameTime());
						game_time = t.format("%M:%S");
						setGameStatus();
					}
					
				});
				
			}
			
		};
		
		atimer.schedule(totask, 0, 1000);
	}
	
	protected void setGameStatus() {
		tvGameLevel.setText("�� " + stage + " ��");
		tvGameTime.setText("����ʱ��" + game_time);
		tvGameStatus.setText(game_status);
	}

	/**
	 * ��ʾ��ǰ��Ϸ��ͼƬԤ��
	 * @param delay �ӳٹر�ʱ��
	 * @return
	 */
	public PopupWindow showPopupReview(long delay) {
	 	final PopupWindow mPop = new PopupWindow(new LinearLayout(this), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	 	mPop.setAnimationStyle(R.style.PopupAnimation);
	 	mPop.setBackgroundDrawable(background_drawalbe);
	 	//mPop.getBackground().setAlpha(190);
	 	mPop.setOutsideTouchable(true);
	 	mPop.setFocusable(true);
	 	mPop.showAtLocation(puzzle, Gravity.CENTER, 0, 0);
	 	mPop.setTouchInterceptor(new OnTouchListener() {
	
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
						mPop.dismiss();
			            return true;
			        } else {
			        	v.performClick();
			        }
					
			        return false;
				}
	 		
	 	});
	 	
	 	mPop.update();
	 	
	 	final Timer atimer = new Timer();
	 	TimerTask totask = new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mPop.dismiss();
						if (atimer != null) atimer.cancel();
					}
					
				});
				
			}
			
		};
		
		
		atimer.schedule(totask, delay);
		
	 	return mPop;
	}

	/**
	 * ���ڼ�����Ϸ�����з�ͼ����
	 * @param pieces
	 */
	abstract void onNewGame(Vector<Piece> pieces);
	
	protected void newPuzzle(String imagePath, int row, int line) {
		Bitmap wallpaper = BitmapFactory.decodeFile(imagePath);
		Bitmap newWallpPaper = ImageUtils.zoomBitmap(wallpaper, space.getWidth(), space.getHeight());

        Vector<Piece> pieces = createAllPieces(newWallpPaper, row, line);
        
        onNewGame(pieces);
        
        //�Ը����п�ͼƬ��װ��imagebutton����
        createAllPieceView(pieces);
        
        //����װ�õ�imageview������Ƶ�ƴͼ����
        createPuzzle();
        
	}
	
	/**
	 * �Ѵ���һ��Ƭ
	 * @param pib
	 * @param index
	 */
	abstract void OnCreatePiece(PieceView pib, int index);
	
	@SuppressLint("ClickableViewAccessibility")
	private void createPuzzle() {
		
		int piececount = allPieces.size();
		for (int i = 0; i < piececount; i++) {
			PieceView pv = allPieces.get(i);
			
			int autoX = (int) (Math.random() * (screenWidth - screenWidth/line));
			int autoY = (int) (Math.random() * (screenHeight - screenHeight/row));
			Point loc = new Point(autoX, autoY);
			pv.setLocation(loc);
			
			//FrameLayout.LayoutParams.WRAP_CONTENT
			FrameLayout.LayoutParams autoParams = new FrameLayout.LayoutParams
					(pv.getPiece().getPieceWidth(), pv.getPiece().getPieceHeight());
			autoParams.leftMargin = (autoX);
			autoParams.topMargin = (autoY);
			autoParams.gravity = Gravity.TOP|Gravity.LEFT;
			
			pv.setLayoutParams(autoParams);
			pv.setDrawingCacheEnabled(true);
			pv.setFocusable(true);
			pv.setOnClickListener(null);
			pv.setOnTouchListener(this);
			
			OnCreatePiece(pv, i);
			
		}
		
	}

	private Vector<Piece> createAllPieces(Bitmap bitmap, int row, int line) {
		PieceFactory pf = new PieceFactory(this);
		pf.setPintuValue(app);
		//pu.setPieceCutFlag(0);
		
    	pf.setImage(bitmap);
    	pf.setRowAndLine(row, line);
    	
    	return pf.getAllPiece();
	}
    
	private void createAllPieceView(Vector<Piece> pieces) {
		for(int i=0; i < pieces.size(); i++){
			Piece piece = (Piece) pieces.get(i);
			PieceView pv = new PieceView(this, piece);
			
			pv.setId(i);  //��Ƭ��ΨһID
			pv.setMinp(piece.getMinp());     //������Ƭ���ⲿ��ʼ��,��ͼǰ�ĵ�λ
			pv.setLocation(new Point(0, 0));
			pv.setTag(piece);
			
			pv.setPadding(0, 0, 0, 0);
			pv.setScaleType(android.widget.ImageView.ScaleType.FIT_XY);
			pv.setImageBitmap(piece.getBmpPiece());
			if (pv.getBackground() != null)
				pv.getBackground().setAlpha(0);
			
			
			allPieces.add(pv);
		}
		
	}

	protected boolean distance(Point srcloc, Point destloc, int inaccuracy) {
		//��ǰX����Ĳ�ֵ����ԭ����������Ĳ�ֵ�ӽ�ʱ
		if (Math.abs(srcloc.x - destloc.x) <= inaccuracy) {
			if (Math.abs(srcloc.y - destloc.y) <= inaccuracy) {
				return true;
			}
		}
		return false;
	}
    
	protected boolean distance(Point srckey, Point destkey, Point srcloc, Point destloc, int inaccuracy) {
		//��ǰX����Ĳ�ֵ����ԭ����������Ĳ�ֵ�ӽ�ʱ
		if (Math.abs((srckey.x - destkey.x) - (srcloc.x - destloc.x)) <= inaccuracy) {
			if (Math.abs((srckey.y - destkey.y) - (srcloc.y - destloc.y)) <= inaccuracy) {
				return true;
			}
		}
		return false;
	}
    
	protected void cleanPath() {
		if (allPieces == null) return;
		for(int i=0; i < allPieces.size(); i++) {
			PieceView pv = (PieceView) allPieces.get(i);
			pv.setTraverse(false);
	    	
		}
		
	}
    
	protected int getNextGameLevel() {
		int max_level = 8;
		//����Ѿ�������Ѷȣ���û����һ��
		if (games != null) max_level = games.size();
		int next_level = stage + 1;
		if (next_level > max_level) {
			next_level = 1;
		}
		
		return next_level;
	}

	protected Bundle getNextGameBundle() {
		Bundle bundle = new Bundle();
		bundle.putInt("imageId", imageId);
		bundle.putInt("levelId", levelId);
		bundle.putInt("gameId", gameId);
		
		long score = System.currentTimeMillis() - start_time;
		int next_lv = getNextGameLevel();
		if (next_lv == 1) {
			bundle.putInt("stage", 1);
			bundle.putString("title", "Congratulations ! Game Clear !");
		} else {
			bundle.putInt("stage", next_lv);
			bundle.putString("title", "Stage Complete !");
		}
		
		bundle.putParcelableArrayList("games", games);
		
		bundle.putString("imagePath", imagePath);
		bundle.putString("imageUri", imageUri);
		bundle.putLong("score", score);
		
		return bundle;
	}
	
	/**
	 * �ж��Ƿ����ƴͼ
	 */
	public void hasComplete() {
		int finish = 0;
		for (int i = 0; i < allPieces.size(); i++) {
			PieceView piece = (PieceView) allPieces.get(i);
			if (piece.isTraverse()) {
				finish ++;
			}
			
		}
		if (finish == row * line) {
			puzzle.postDelayed(new Runnable() {
	
				@Override
				public void run() {
					onCompleted();
				}
				
			}, 500);
		}
	}

	/**
	 * ƴͼ���ʱ����ת���ɼ�����
	 */
	protected void onCompleted() {
		Intent i = new Intent(BaseGameActivity.this, ScoreActivity.class);
		i.setAction("GAME_COMPLETE_ACTION");
		
		i.putExtras(this.getNextGameBundle());
		
		startActivity(i);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		
		//��ɹؿ�ʱ�Ľӿڷ���
		//String uuid = app.getUUID();
		
		finish();
	}
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			//return false;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	public OnItemLongClickListener listener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			if (drawer != null && drawer.isOpened()) drawer.animateClose();
			
			return false;
		}
		
	};
	
}
