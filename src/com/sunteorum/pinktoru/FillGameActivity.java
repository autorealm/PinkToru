package com.sunteorum.pinktoru;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunteorum.pinktoru.adapter.DragImageAdapter;
import com.sunteorum.pinktoru.entity.Piece;
import com.sunteorum.pinktoru.inc.ColorThief;
import com.sunteorum.pinktoru.util.ImageUtils;
import com.sunteorum.pinktoru.view.GalleryDrag;
import com.sunteorum.pinktoru.view.MultiDirectionSlidingDrawer;
import com.sunteorum.pinktoru.view.PieceView;

public class FillGameActivity extends BaseGameActivity {

	private DragImageAdapter adapter;
	private GalleryDrag gallery;
	private Vibrator mVibrator;
	
	private int ett = 0;//提示次数
	private int step = 0;//步数
	
	private ImageView imgWrong, imgRight;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	@Override
	public void init() {
		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		puzzle = (FrameLayout) inflater.inflate(R.layout.activity_game_fill, null);
		
		setContentView(puzzle);
		mVibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		
		space = (FrameLayout) findViewById(R.id.space);
		layGameStatus = (LinearLayout) this.findViewById(R.id.layGameStatus);
		drawer = (MultiDirectionSlidingDrawer) this.findViewById(R.id.drawer);
		handle = (View) this.findViewById(R.id.handle);
		tvGameLevel = ((TextView) findViewById(R.id.tvGameLevel));
		tvGameTime = ((TextView) findViewById(R.id.tvGameTime));
		tvGameStatus = ((TextView) findViewById(R.id.tvGameStatus));
		
		imgWrong = new ImageView(this);
		imgRight = new ImageView(this);
		FrameLayout.LayoutParams _params = new FrameLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		_params.gravity = Gravity.TOP|Gravity.LEFT;
		FrameLayout.LayoutParams _params_ = new FrameLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		_params_.gravity = Gravity.TOP|Gravity.LEFT;
		imgWrong.setLayoutParams(_params);
		imgWrong.setImageResource(R.drawable.btn_cancel);
		imgWrong.setVisibility(8);
		imgRight.setLayoutParams(_params_);
		imgRight.setImageResource(R.drawable.btn_selected);
		imgRight.setVisibility(8);
		puzzle.addView(imgWrong);
		puzzle.addView(imgRight);
		
		puzzle.setBackgroundDrawable(background_drawalbe);
		puzzle.setKeepScreenOn(app.isKeepon());
		puzzle.getChildAt(0).setVisibility(4);
		
		handle.setBackgroundResource(R.drawable.bannershape_1);
		
		INACCURACY = screenWidth / line / 2;

	}

	@Override
	public void onNewGame(final Vector<Piece> pieces) {
		adapter = new DragImageAdapter(this, pieces);
        gallery = (GalleryDrag) findViewById(R.id.gallery_piece_list);
        gallery.setAdapter(adapter);
        gallery.setOnItemLongClickListener(listener);
        
        gallery.setOnDropListener(new GalleryDrag.onDropListener() {
			
			@Override
			public void drop(int pos, int x, int y) {
				Point p = new Point(x, y);
				Piece pie =  pieces.get(pos);//((Piece)allImagePieces.get(pos).getTag());
				Point pm = pie.getMinp();
				
				pm = new Point(pm.x + dx + pie.getPieceWidth() / 2, pm.y + dy + pie.getPieceHeight() / 2);
				
				FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) imgWrong.getLayoutParams();
				flp.leftMargin = x - dx;
				flp.topMargin = y - dy;
				//imgWrong.setLayoutParams(flp);
				
				FrameLayout.LayoutParams rlp = (FrameLayout.LayoutParams) imgRight.getLayoutParams();
				rlp.leftMargin = pm.x - dx;
				rlp.topMargin = pm.y - dy;
				//imgRight.setLayoutParams(rlp);
				
				if (drawer != null && drawer.isOpened()) drawer.animateClose();
				step++;
				
				//Log.i("drop-toger" + pos,pm.x + " - " + pm.y);
				//Log.i("drop-pointer",x + " - " + y);
				if (distance(p, pm, INACCURACY)) {
	    			//Log.i("drop", "XXXXXXXXXXXXXXXXXXXXXXXXXX");
	    			PieceView pib = (PieceView) allPieces.get(getPieceViewPos(pie));
	    			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
	    					(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    			params.leftMargin = pie.getMinp().x;
	    			params.topMargin = pie.getMinp().y;
	    			params.gravity = Gravity.TOP|Gravity.LEFT;
	    			pib.setLayoutParams(params);
	    			pib.setFocusable(false);
	    			space.addView(pib);
	    			gallery.stopDrag();
	    			pieces.remove(pos);
	    			adapter.notifyDataSetChanged();
	    			
	    			//adapter.setSelected(pos, false);
	    			//if (adapter.lostCount() == 0) {
	    			if (pieces.size() == 0) {
	    				space.postDelayed(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								onCompleted();
							}
	    					
	    				}, 500);
	    				
	    			}

	    		} else {
	    			gallery.stopDrag(0, 0);
	    			mVibrator.vibrate(50);
	    			
	    			ett++;//次数自增
	    			if (ett > ERR_TIP_TIMES) return;
	    			
	    			imgWrong.setVisibility(0);
	    			imgRight.setVisibility(0);
	    			
	    			imgWrong.postDelayed(new Runnable() {

						@Override
						public void run() {
							imgWrong.setVisibility(8);
							imgRight.setVisibility(8);
						}
	    				
	    			}, 600);
	    			
	    		}
				
				String str = String.format(Locale.getDefault(), "%.1f",
    					(float) (allPieces.size() - pieces.size()) * 100 /allPieces.size());
    			
				game_status = "步数：" + step + " (" + str + "%)";
    			setGameStatus();
				
			}
		});

	}


	@Override
	public void OnCreatePiece(PieceView pib, int index) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStartGame() {
		Drawable dg = puzzle.getBackground();
		Bitmap blurbg = ImageUtils.fastBlur(ImageUtils.DrawableToBitmap(dg), 8);
		int dominantColor = Color.LTGRAY, themeColor = Color.BLACK;
		try {
			List<int[]> result = ColorThief.compute(ImageUtils.DrawableToBitmap(dg), 3);
			int[] dc1 = result.get(0), dc2 = result.get(1);
			float f = 0.8f;
			dominantColor = Color.argb(180, (int) (dc1[0] * f), (int) (dc1[1] * f), (int) (dc1[2] * f));
			themeColor = Color.rgb((int) (dc2[0] * f), (int) (dc2[1] * f), (int) (dc2[2] * f));
			//if (!bg.isRecycled()) bg.recycle();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		blurbg = ImageUtils.drawGridInBitmap(blurbg, line, row, dominantColor);
		if (blurbg != null) dg = ImageUtils.BitmapToDrawable(this, blurbg);
		
		if (dg != null) {
			space.setBackgroundDrawable(dg);
			space.getBackground().setAlpha(160);
		}
		
		puzzle.setBackgroundColor(themeColor);
		puzzle.getChildAt(0).setVisibility(0);
		
		
	}

	private int getPieceViewPos(Piece piece) {
		int pos = -1;
		for (int i = 0; i< allPieces.size(); i++) {
			Piece pie = (Piece) allPieces.get(i).getPiece();
			if (pie.equals(piece)) {
				pos = i;
				break;
			}
		}
		
		return pos;
	}

	@Override
	public void onFailed() {
		// TODO Auto-generated method stub
		
	}

	
}
