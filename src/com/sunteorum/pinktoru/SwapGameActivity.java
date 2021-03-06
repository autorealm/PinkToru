package com.sunteorum.pinktoru;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunteorum.pinktoru.entity.Piece;
import com.sunteorum.pinktoru.inc.ColorThief;
import com.sunteorum.pinktoru.util.ImageUtils;
import com.sunteorum.pinktoru.view.MultiDirectionSlidingDrawer;
import com.sunteorum.pinktoru.view.PieceView;

public class SwapGameActivity extends BaseGameActivity {

	private String tag = this.getClass().getSimpleName();
	private ArrayList<Point> mPoints;
	private int step = 0;
	private int ett = 0;
	private int lastX;
	private int lastY;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!(v instanceof PieceView)) return true;
		
		int tx, ty;
		PieceView pib = (PieceView) v;
		/*if (tagpib != null) {
        	tagpib.setPadding(0, 0, 0, 0);
        	tagpib.setBackgroundDrawable(null);
        }*/
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			pib.bringToFront();
			lastX = (int) event.getX();
			lastY = (int) event.getY();
			
			if (drawer != null && drawer.isOpened()) drawer.animateClose();
			break;
		case MotionEvent.ACTION_MOVE:
			int dx =(int)event.getX() - lastX;
			int dy =(int)event.getY() - lastY;
			
			int left = v.getLeft() + dx;
            int top = v.getTop() + dy;
            //FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) (v.getLayoutParams());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(v.getWidth(), v.getHeight());
            lp.leftMargin = left;
            lp.topMargin = top;
            lp.gravity = Gravity.TOP|Gravity.LEFT;
            v.setLayoutParams(lp);
            
            /*
            tx = (int) event.getRawX() - ((FrameLayout.LayoutParams)space.getLayoutParams()).leftMargin;
            ty = (int) event.getRawY() - ((FrameLayout.LayoutParams)space.getLayoutParams()).topMargin;
            
            tagpib = getPieceBtnInLocal(tx, ty);
            if (tagpib != null && !tagpib.equals(pib)) {
            	tagpib.setPadding(1, 1, 1, 1);
            	tagpib.setBackgroundResource(R.drawable.itemshape_2);
            }
            */
            
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					
				}
				
			});
			
			break;
		case MotionEvent.ACTION_UP:
			View play = (View) space.getParent();
			FrameLayout.LayoutParams pfp = (FrameLayout.LayoutParams) play.getLayoutParams();
            tx = (int) event.getRawX() - pfp.leftMargin - play.getPaddingLeft();
            ty = (int) event.getRawY() - pfp.topMargin - play.getPaddingTop();
			drop(pib, tx, ty);
			
			//Log.i("drop", "tx: " + tx + " ty: " + ty);
			
			break;        		
		}

		return true;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	@Override
	public void init() {
		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		puzzle = (FrameLayout) inflater.inflate(R.layout.activity_game_swap, null);
		
		setContentView(puzzle);
		
		space = (FrameLayout) findViewById(R.id.space);
		ipin = (android.widget.ImageView) findViewById(R.id.ipin);
		layGameStatus = (LinearLayout) this.findViewById(R.id.layGameStatus);
		drawer = (MultiDirectionSlidingDrawer) this.findViewById(R.id.drawer);
		handle = (View) this.findViewById(R.id.handle);
		tvGameLevel = ((TextView) findViewById(R.id.tvGameLevel));
		tvGameTime = ((TextView) findViewById(R.id.tvGameTime));
		tvGameStatus = ((TextView) findViewById(R.id.tvGameStatus));
		
		puzzle.setBackgroundDrawable(background_drawalbe);
		puzzle.setKeepScreenOn(app.isKeepon());
		
		puzzle.getChildAt(0).setVisibility(4);
		puzzle.getChildAt(1).setVisibility(4);
		
		handle.setBackgroundResource(R.drawable.bannershape_1);
		
		//强制使用纯矩形分图方式
		//app.setPieceCutFlag(0);
		//app.setPieceEdgeWidth(1);
		INACCURACY = 0;
		
		Log.i(tag, String.format("", System.currentTimeMillis()));
	}


	@Override
	public void OnCreatePiece(PieceView pv, int i) {
		Point p = mPoints.get(i);
		int autoX = p.x;
		int autoY = p.y;
		
		pv.setLocation(p);
		//pv.setId(pv.getPiece().getId().hashCode());
		
		if (distance(pv.getMinp(), p, 12)) pv.setTraverse(true);
		else pv.setTraverse(false);
		
		FrameLayout.LayoutParams autoParams = (FrameLayout.LayoutParams) pv.getLayoutParams();
		autoParams.leftMargin = (autoX);
		autoParams.topMargin = (autoY);
		
		space.addView(pv);
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStartGame() {
		Drawable dg = puzzle.getBackground();
		
		int dominantColor = Color.LTGRAY, themeColor = Color.BLACK;
		try {
			List<int[]> result = ColorThief.compute(ImageUtils.DrawableToBitmap(dg), 3);
			int[] dc1 = result.get(0), dc2 = result.get(1);
			float f = 0.8f;
			dominantColor = Color.argb(180, (int) (dc1[0] * f), (int) (dc1[1] * f), (int) (dc1[2] * f));
			themeColor = Color.rgb((int) (dc2[0] * f), (int) (dc2[1] * f), (int) (dc2[2] * f));
			space.setBackgroundColor(themeColor);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Bitmap blurbg = Bitmap.createBitmap(space.getWidth(), space.getHeight(), Config.RGB_565);
		blurbg = ImageUtils.drawGridInBitmap(blurbg, line, row, Color.LTGRAY);
		if (blurbg != null) dg = ImageUtils.BitmapToDrawable(this, blurbg);
		if (dg != null) {
			space.setBackgroundDrawable(dg);
			space.getBackground().setAlpha(120);
			space.postInvalidate();
		}
		
		puzzle.setBackgroundColor(dominantColor);
		puzzle.getChildAt(0).setVisibility(0);
		puzzle.getChildAt(1).setVisibility(0);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onNewGame(Vector<Piece> pieces) {
		mPoints = new ArrayList<Point>();
		Point p = null;
		
		for (int i = 0; i < pieces.size(); i++) {
			p = pieces.get(i).getMinp();
			//p.x = p.x+1;
			//p.y = p.y+1;
			
			mPoints.add(p);
		}
		
		Collections.sort(mPoints, new Comparator() {
		      @Override
		      public int compare(Object o1, Object o2) {
		    	  int rnd = ((int)(Math.random()*10)%2 ==0) ? 1 : -1;
		    	  return rnd * ((int)(Math.random()*mPoints.size()));
		      }
		});
		
	}

	@Override
	public void onFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCompleted() {
		// TODO Auto-generated method stub
		super.onCompleted();
		
	}

	/**
	 * 
	 * @param pv
	 * @param x
	 * @param y
	 */
    public void drop(PieceView pv, int x, int y) {
		Point p = new Point(x, y);
		
		int left, top, left2, top2;
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
				(pv.getWidth(), pv.getHeight());
		left = pv.getLocation().x;
		top = pv.getLocation().y;
		
		final PieceView pib2 = getPieceBtnInLocal(x, y);
		if (pib2 == null || pib2.equals(pv)) {
			params.leftMargin = pv.getLocation().x;
			params.topMargin = pv.getLocation().y;
			params.gravity = Gravity.TOP|Gravity.LEFT;
			pv.setLayoutParams(params);
			
			doVibrate();
			
			ett++;
			if (ett <= ERR_TIP_TIMES)
				doTip(pv);
			
			return;
		}
		
		FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams
				(pv.getWidth(), pv.getHeight());
		left2 = pib2.getLocation().x;
		top2 = pib2.getLocation().y;
		
		params2.leftMargin = left;
		params2.topMargin = top;
		params2.gravity = Gravity.TOP|Gravity.LEFT;
		
		params.leftMargin = left2;
		params.topMargin = top2;
		params.gravity = Gravity.TOP|Gravity.LEFT;
		
		pib2.setVisibility(4);
		pib2.bringToFront();
		pib2.setLayoutParams(params2);
		pv.setLayoutParams(params);
		
		pv.startAnimation(AnimationUtils.loadAnimation(this, R.anim.show_2));
		
		Animation anim = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, (left2 - left) / pv.getPiece().getLineWidth(),
				Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, (top2 - top) / pv.getPiece().getRowHeight(),
				Animation.RELATIVE_TO_SELF, 0f
				);
		anim.setDuration(150);
		anim.setFillAfter(false);
		anim.setInterpolator(new DecelerateInterpolator());
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				
				
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				
				pib2.setVisibility(0);
			}
			
		});
		pib2.startAnimation(anim);
		
		p = new Point(left, top);
		pib2.setLocation(p);
		if (distance(pib2.getMinp(), p, 12)) pib2.setTraverse(true);
		else pib2.setTraverse(false);
		
		p = new Point(left2, top2);
		pv.setLocation(p);
		if (distance(pv.getMinp(), p, 12)) pv.setTraverse(true);
		else pv.setTraverse(false);
		
		step++;
		game_status = "Step: " + String.valueOf(step);
    	setGameStatus();
		hasComplete();
		
	}

    private PieceView getPieceBtnInLocal(int x, int y) {
    	PieceView pib = null;
    	int piececount = allPieces.size();
		
		
		for(int i = 0; i < piececount; i++){
			pib = allPieces.get(i);
			Point op = pib.getLocation();
			if (x > (op.x + INACCURACY) && x < (op.x + pib.getWidth() - INACCURACY)) {
				if (y > (op.y + INACCURACY) && y < (op.y + pib.getHeight() - INACCURACY)) {
					//Log.i("getPieceBtnInLocal", i + ":(" + x + " - " + y + ") " + pib.hashCode());
					return pib;
				}
			}
			
			
		}
    	
    	return null;
    }
    
    private void doTip(PieceView pv) {
    	Piece piece = pv.getPiece();
		final PieceView topv = getPieceBtnInLocal(piece.getMinp().x + piece.getOffset() + piece.getLineWidth() / 2,
				piece.getMinp().y + piece.getOffset() + piece.getRowHeight() / 2);
		if (topv == null) return;
		pv.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shan));
		topv.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shan));
		
		return;
		
    }
}
