package com.sunteorum.pinktoru.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class PuzzleView extends View implements OnGestureListener,
		OnDoubleTapListener, OnScaleGestureListener, AnimationListener {
	private GestureDetector gesture;
	private ScaleGestureDetector scaleGesture;
	private boolean firstDraw = true;
	private float scale = 1.0f;

	public PuzzleView(Context context) {
		super(context);
	}

	public PuzzleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PuzzleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void loadPuzzle(Bitmap image, String location) {
		gesture = new GestureDetector(this.getContext(), this);
		scaleGesture = new ScaleGestureDetector(this.getContext(), this);
		
	}

	public void loadPuzzle(String location) {
		gesture = new GestureDetector(this.getContext(), this);
		scaleGesture = new ScaleGestureDetector(this.getContext(), this);
		
	}

	public void savePuzzle(String location) {
		
	}

	@Override
	public void onDraw(Canvas canvas) {

		if (firstDraw) {
			firstDraw = false;
			
		}
		canvas.scale(scale, scale);
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		scaleGesture.onTouchEvent(event);
		gesture.onTouchEvent(event);
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent arg0) {
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e1) {
		// Get the piece that is under this tap.
		
		
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		
		this.invalidate();
		return true;
	}

	private boolean checkSurroundings() {
		

		boolean rv = false;

		return rv;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		

		return true;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		scale *= detector.getScaleFactor();
		this.invalidate();
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
	}

	public void shuffle() {
		
		this.invalidate();
	}

	public void solve() {
		Animation a = new AlphaAnimation(1, 0);
		a.setDuration(2000);
		a.setAnimationListener(this);
		this.startAnimation(a);
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		
		this.invalidate();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}
}
