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
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

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

	protected PinkToru app;
	protected int screenWidth;
	protected int screenHeight;
	protected Drawable background_drawalbe;
	
	protected int INACCURACY = 12;//判断拼合的距离
	protected int ERR_TIP_TIMES = 9;//错误提示次数
	
	protected ArrayList<PieceView> allPieces = new ArrayList<PieceView>();
	
	protected FrameLayout puzzle = null; //根布局
	protected FrameLayout space = null; //游戏窗口
	
	protected int imageId = 0;//图片ID
	protected String imagePath;//图片的本地路径
	
	protected int levelId = 0;//关卡ID
	protected int level;//关数
	protected int row;//行
	protected int line;//列
	
	protected int dx, dy; //游戏窗口与根布局的距离
	
	protected LevelEntity le;
	protected MultiDirectionSlidingDrawer drawer;
	protected View handle;
	protected LinearLayout layGameStatus;
	protected TextView tvGameLevel, tvGameTime, tvGameStatus;
	
	protected long lctime;//储存当前时间值
	protected long start_time;//储存开始游戏时间值
	protected Handler mHandler = new Handler();
	
	protected String game_status = "0";
	protected String game_time = "00:00";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final String action = this.getIntent().getAction();
		Log.i("Game", "action = " + action);
		
		Bundle bundle = this.getIntent().getExtras();
		//imageId = bundle.getInt("imageId");
		//levelId = bundle.getInt("levelId");
		level = bundle.containsKey("level") ? bundle.getInt("level") : 1;
		row = bundle.containsKey("row") ? bundle.getInt("row") : 3;
		line = bundle.containsKey("line") ? bundle.getInt("line") : 2;
		String imageUrl = bundle.getString("imageUrl");
		imagePath = bundle.getString("imagePath");
		
		app = (PinkToru) getApplication();
		if (!app.offline) {
			if (app.games == null || app.games.size() < level) {
				Common.showTip(this, "程序出错", "游戏数据读取失败！");
				return;
			}
			
			le = app.games.get(level - 1);
			row = le.getPieceRow();
			line = le.getPieceLine();
			
			if (imageUrl == null && imagePath == null) {
				imageUrl = le.getImageUrl();
			}
		}
		
		if (imageUrl != null && imageUrl.trim().length() > 0) {
			imagePath = app.getCacheImagePath(imageUrl);
			if (imagePath == null || imagePath.trim().length() == 0) {
				Common.showTip(this, "程序出错", "游戏图片资源读取失败！");
				return;
			}
		}
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		
		background_drawalbe = ImageUtils.readDrawable(this, imagePath, screenWidth, screenHeight);
		init();
		process();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		lctime = System.currentTimeMillis();
		
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
		if (app.offline) {
			if (l < 1600) {
				super.onBackPressed();
			} else {
				lctime = System.currentTimeMillis();
				Toast.makeText(this, "再次按返回退出", Toast.LENGTH_SHORT).show();
			}
			return;
		}
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
		
		puzzle.setBackgroundDrawable(null);
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
		.setTitle("退出当前的游戏吗？")
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				finish();
			}
		}).setNegativeButton("取消", null).create().show();
	}

	/**
	 * 初始化，setContentView 设置内容界面，findViewById 找出控件
	 */
	abstract void init ();
	
	protected void process() {
		//显示正在准备游戏的进度提示框
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
		txtName.setText("准备开始第 " + level + " 关...");
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
				txtName.setText("游戏加载完成");
				
				onStartGame();
				
				try {
					this.wait(400);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				progd.dismiss();
				
				dx = (puzzle.getWidth() - space.getWidth()) / 2;
				dy = (puzzle.getHeight() - space.getHeight()) / 2;
				start_time = System.currentTimeMillis();
				
				//延迟显示顶部状态条
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
	 * 游戏完全加载完成时
	 */
	abstract void onStartGame();
	
	private void setDrawer() {
		
		if(android.os.Build.VERSION.SDK_INT >= 11) {
			//drawer.setAlpha(0.35f);
		}
		
		//handle.getBackground().setAlpha(120);
		layGameStatus.bringToFront();
		handle.setBackgroundResource(R.drawable.bannershape_1);
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

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (drawer.isOpened()) {
							drawer.bringToFront();
							drawer.postInvalidate();
						} else {
							handle.setBackgroundResource(R.drawable.backgroud_2);
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

			@Override
			public void onDrawerClosed() {
				// TODO Auto-generated method stub
				handle.setBackgroundResource(R.drawable.backgroud_2);
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
		tvGameLevel.setText("第 " + level + " 关");
		tvGameTime.setText("共用时：" + game_time);
		tvGameStatus.setText(game_status);
	}

	/**
	 * 显示当前游戏的图片预览
	 * @param delay 延迟关闭时间
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
	 * 正在加载游戏，进行分图处理
	 * @param pieces
	 */
	abstract void onNewGame(Vector<Piece> pieces);
	
	protected void newPuzzle(String imagePath, int row, int line) {
		Bitmap wallpaper = BitmapFactory.decodeFile(imagePath);
		Bitmap newWallpPaper = ImageUtils.zoomBitmap(wallpaper, screenWidth, screenHeight);

        Vector<Piece> pieces = createAllPieces(newWallpPaper, row, line);
        
        onNewGame(pieces);
        
        //对各个切块图片包装成imagebutton待用
        createAllPieceView(pieces);
        
        //将包装好的imageview随机绘制到拼图板上
        createPuzzle();
        
	}
	
	/**
	 * 已创建一碎片
	 * @param pib
	 * @param index
	 */
	abstract void OnCreatePuzzle(PieceView pib, int index);
	
	@SuppressLint("ClickableViewAccessibility")
	private void createPuzzle() {
		
		int piececount = allPieces.size();
		for (int i = 0; i < piececount; i++) {
			PieceView pib = allPieces.get(i);
			
			int autoX = (int) (Math.random() * (screenWidth - screenWidth/line));
			int autoY = (int) (Math.random() * (screenHeight - screenHeight/row));
			Point loc = new Point(autoX, autoY);
			pib.setLocation(loc);
			
			FrameLayout.LayoutParams autoParams = new FrameLayout.LayoutParams
					(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			autoParams.leftMargin = (autoX);
			autoParams.topMargin = (autoY);
			autoParams.gravity = Gravity.TOP|Gravity.LEFT;
			
			pib.setLayoutParams(autoParams);
			pib.setDrawingCacheEnabled(true);
			pib.setFocusable(true);
			pib.setOnClickListener(null);
			pib.setOnTouchListener(this);
			
			OnCreatePuzzle(pib, i);
			
		}
		
	}

	private Vector<Piece> createAllPieces(Bitmap bitmap, int row, int line) {
		PieceFactory pu = new PieceFactory(this);
		pu.setPintuValue(app);
		//pu.setPieceCutFlag(0);
		
    	pu.setImage(bitmap);
    	pu.setRowAndLine(row, line);
    	
    	return pu.getAllPiece();
	}
    
	private void createAllPieceView(Vector<Piece> pieces) {
		for(int i=0; i < pieces.size(); i++){
			Piece piece = (Piece) pieces.get(i);			
			PieceView pib = new PieceView(this, piece);
			
			pib.setId(i);  //碎片的唯一ID
			pib.setMinp(piece.getMinp());     //整个碎片的外部开始点,切图前的点位
			pib.setLocation(new Point(0,0));
			pib.setTag(piece);
			
			pib.setPadding(0, 0, 0, 0);
			//pib.setScaleType(ImageView.ScaleType.FIT_XY);
			pib.setImageBitmap(piece.getBmpPiece());
			if (pib.getBackground() != null)
				pib.getBackground().setAlpha(0);
			
			
			allPieces.add(pib);
		}
		
	}

	protected boolean distance(Point srcloc, Point destloc, int inaccuracy) {
		//当前X坐标的差值，与原来的虚坐标的差值接近时
		if (Math.abs(srcloc.x - destloc.x) <= inaccuracy) {
			if (Math.abs(srcloc.y - destloc.y) <= inaccuracy) {
				return true;
			}
		}
		return false;
	}
    
	protected boolean distance(Point srckey, Point destkey, Point srcloc, Point destloc, int inaccuracy) {
		//当前X坐标的差值，与原来的虚坐标的差值接近时
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
		//如果已经是最大难度，则没有下一关
		if (!app.offline) max_level = app.games.size();
		int next_level = level + 1;
		if (next_level > max_level) {
			next_level = 1;
		}
		
		return next_level;
	}

	protected Bundle getNextGameBundle() {
		Bundle bundle = new Bundle();
		bundle.putInt("imageId", imageId);
		bundle.putInt("levelId", levelId);
		
		long score = System.currentTimeMillis() - start_time;
		int next_lv = getNextGameLevel();
		if (next_lv == 1) {
			bundle.putInt("level", 1);
			bundle.putString("title", "");
		} else {
			bundle.putInt("level", next_lv);
			bundle.putString("title", "");
		}
		
		bundle.putString("imagePath", imagePath);
		bundle.putLong("score", score);
		
		return bundle;
	}
	
	/**
	 * 判断是否完成拼图
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
	 * 拼图完成时，跳转到成绩界面
	 */
	protected void onCompleted() {
		Intent i = new Intent(BaseGameActivity.this, ScoreActivity.class);
		i.setAction("GAME_COMPLETE_ACTION");
		
		i.putExtras(this.getNextGameBundle());
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		startActivity(i);
		
		//完成关卡时的接口访问
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
