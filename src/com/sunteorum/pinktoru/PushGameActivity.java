package com.sunteorum.pinktoru;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunteorum.pinktoru.entity.Piece;
import com.sunteorum.pinktoru.inc.ColorThief;
import com.sunteorum.pinktoru.util.ImageUtils;
import com.sunteorum.pinktoru.view.MultiDirectionSlidingDrawer;
import com.sunteorum.pinktoru.view.PieceView;

public class PushGameActivity extends BaseGameActivity {

	private String tag = this.getClass().getSimpleName();
	private ArrayList<Point> mPoints;
	private int step = 0;
	private int ett = 0;
	private int lastX;
	private int lastY;
	private int hitv;
	private PieceView tPieceView;
	private Point point = null;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!(v instanceof PieceView)) return true;
		
		int dx = 0, dy = 0;
		PieceView pv = (PieceView) v;
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			pv.bringToFront();
			lastX = (int) event.getX();
			lastY = (int) event.getY();
			
			point = getPieceAround(pv);
			
			if (point != null)
			System.out.println ("X : " + point.x + " Y : " + point.y);
			
			if (drawer != null && drawer.isOpened()) drawer.animateClose();
			break;
		case MotionEvent.ACTION_MOVE:
			dx = (int) event.getX() - lastX;
			dy = (int) event.getY() - lastY;
			
			int left = v.getLeft(), top = v.getTop();
			
			if (point == null) return true;
			if (point.x == 1) {
				left = v.getLeft() + dx;
				if (left < tPieceView.getLocation().x) left = tPieceView.getLocation().x;
				if (dx > 0) if (left > pv.getLocation().x) {left = pv.getLocation().x;}
				else {};
			} else if (point.x == -1) {
				left = v.getLeft() + dx;
				if (left > tPieceView.getLocation().x) left = tPieceView.getLocation().x;
				if (dx < 0) if (left < pv.getLocation().x) {left = pv.getLocation().x;}
				else {};
			} else if (point.y == 1) {
				top = v.getTop() + dy;
				if (top < tPieceView.getLocation().y) top = tPieceView.getLocation().y;
				if (dy > 0) if (top > pv.getLocation().y) {top = pv.getLocation().y;}
				else {};
			} else if (point.y == -1) {
				top = v.getTop() + dy;
				if (top > tPieceView.getLocation().y) top = tPieceView.getLocation().y;
				if (dy < 0) if (top < pv.getLocation().y) {top = pv.getLocation().y;}
				else {};
			}
			
            //FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) (v.getLayoutParams());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(v.getWidth(), v.getHeight());
            
            lp.leftMargin = left;
            lp.topMargin = top;
            lp.gravity = Gravity.TOP|Gravity.LEFT;
            v.setLayoutParams(lp);
            
			break;
		case MotionEvent.ACTION_UP:
			if (point == null) return true;
			if (pv.getLocation().x == pv.getLeft() && pv.getLocation().y == pv.getTop()) return true;
			
			View play = (View) space.getParent();
			FrameLayout.LayoutParams pfp = (FrameLayout.LayoutParams) play.getLayoutParams();
			int tx = (int) event.getRawX() - pfp.leftMargin - play.getPaddingLeft();
			int ty = (int) event.getRawY() - pfp.topMargin - play.getPaddingTop();
			push(pv, tx, ty);
			
			//Log.i("drop", "tx: " + tx + " ty: " + ty);
			
			break;
		}

		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void init() {
		puzzle = (FrameLayout) View.inflate(this, R.layout.activity_game_swap, null);
		
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
		
		INACCURACY = 0;
		
		Log.i(tag, String.format("", System.currentTimeMillis()));
	}


	@Override
	public void OnCreatePiece(PieceView pv, int i) {
		Point p = mPoints.get(i);
		int autoX = p.x;
		int autoY = p.y;
		
		pv.setLocation(p);
		
		if (distance(pv.getMinp(), p, 12)) pv.setTraverse(true);
		else pv.setTraverse(false);
		
		FrameLayout.LayoutParams autoParams = (FrameLayout.LayoutParams) pv.getLayoutParams();
		autoParams.leftMargin = (autoX);
		autoParams.topMargin = (autoY);
		
		if (i == hitv) {
			pv.setTraverse(true);
			pv.setEnabled(false);
			pv.setVisibility(8);
			tPieceView = pv;
		}
		
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
			space.getBackground().setAlpha(80);
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
		
		hitv = (int) Math.random()*pieces.size();
		
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
    public void push(PieceView pv, int x, int y) {

		step++;
		game_status = "Step: " + String.valueOf(step);
    	setGameStatus();
    	
		if (pv.equals(getPieceBtnInLocal(x, y))) {
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) pv.getLayoutParams();
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
		
		swap(pv, tPieceView);
		
	}

    private void swap(PieceView fpv, PieceView tpv) {
    	if (fpv == null || tpv == null) return;
    	if (fpv.equals(tpv)) return;
    	
    	int left, top, left2, top2;
    	
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
				(fpv.getWidth(), fpv.getHeight());
		left = fpv.getLocation().x;
		top = fpv.getLocation().y;
		
    	FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams
				(tpv.getWidth(), tpv.getHeight());
		left2 = tpv.getLocation().x;
		top2 = tpv.getLocation().y;
		
		params2.leftMargin = left;
		params2.topMargin = top;
		params2.gravity = Gravity.TOP|Gravity.LEFT;
		
		params.leftMargin = left2;
		params.topMargin = top2;
		params.gravity = Gravity.TOP|Gravity.LEFT;
		
		tpv.setLayoutParams(params2);
		fpv.setLayoutParams(params);
		
		Point p = new Point(left, top);
		tpv.setLocation(p);
		if (distance(tpv.getMinp(), p, 12)) tpv.setTraverse(true);
		else tpv.setTraverse(false);
		
		p = new Point(left2, top2);
		fpv.setLocation(p);
		if (distance(fpv.getMinp(), p, 12)) fpv.setTraverse(true);
		else fpv.setTraverse(false);
		
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
		
		swap(pv, topv);
		
		return;
		
    }
    
    private Point getPieceAround(PieceView pv) {
    	if (pv == null) return null;
    	Point p = tPieceView.getLocation();
    	int dx = tPieceView.getPiece().getLineWidth();
    	int dy = tPieceView.getPiece().getRowHeight();
    	PieceView rpv = getPieceBtnInLocal(p.x + dx + dx / 2, p.y + dy /2);
    	if (pv.equals(rpv)) return new Point(1, 0);
    	rpv = getPieceBtnInLocal(p.x + dx / 2, p.y + dy + dy / 2);
    	if (pv.equals(rpv)) return new Point(0, 1);
    	rpv = getPieceBtnInLocal(p.x - dx / 2, p.y + dy / 2);
    	if (pv.equals(rpv)) return new Point(-1, 0);
    	rpv = getPieceBtnInLocal(p.x + dx / 2, p.y - dy / 2);
    	if (pv.equals(rpv)) return new Point(0, -1);
    	
    	return null;
    }
}
