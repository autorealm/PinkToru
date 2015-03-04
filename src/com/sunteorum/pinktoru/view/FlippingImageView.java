package com.sunteorum.pinktoru.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.sunteorum.pinktoru.R;
import com.sunteorum.pinktoru.view.RotateAnimation.Mode;

public class FlippingImageView extends ImageView {

	private Context context;
	private RotateAnimation mAnimation;
	private boolean mIsHasAnimation;
	private Mode mode = Mode.Z;
	private Long duration = 8000L;
	private boolean anmiEffect = false;

	public FlippingImageView(Context context) {
		super(context);
		this.context = context;
	}

	public FlippingImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlippingImageView);
		duration = (long) a.getInt(R.styleable.FlippingImageView_Duration, 8000);
		anmiEffect = a.getBoolean(R.styleable.FlippingImageView_AnimEffect, true);
		int _mode = a.getInt(R.styleable.FlippingImageView_Mode, 2);
		switch (_mode) {
		case 0: mode = Mode.X;break;
		case 1: mode = Mode.Y;break;
		case 2: mode = Mode.Z;
			
		}
		
		a.recycle();
	}

	public FlippingImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	private void setRotateAnimation() {
		if (mIsHasAnimation == false && getWidth() > 0
				&& getVisibility() == View.VISIBLE) {
			mIsHasAnimation = true;
			mAnimation = new RotateAnimation(getWidth() / 2.0F,
					getHeight() / 2.0F, mode);
			mAnimation.setDuration(duration);
			mAnimation.setInterpolator(new LinearInterpolator());
			mAnimation.setRepeatCount(-1);
			mAnimation.setRepeatMode(Animation.RESTART);
			if (anmiEffect) mAnimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					startAnimation(AnimationUtils.loadAnimation(context, R.anim.view_zoom_out));
					
					//System.out.println("onAnimationEnd");
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
				}
				
			});
			setAnimation(mAnimation);
		}
	}

	@SuppressLint("NewApi") 
	private void clearRotateAnimation() {
		if (mIsHasAnimation) {
			mIsHasAnimation = false;
			mAnimation.reset();
			mAnimation.cancel();
			//clearAnimation();
			//setAnimation(null);
			//mAnimation = null;
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		setRotateAnimation();

	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		clearRotateAnimation();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w > 0) {
			setRotateAnimation();
		}
	}

	public void startAnimation() {
		if (mIsHasAnimation) {
			super.startAnimation(mAnimation);
		}
	}
	
	@TargetApi(Build.VERSION_CODES.FROYO)
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if (visibility == View.INVISIBLE || visibility == View.GONE) {
			clearRotateAnimation();
		} else {
			setRotateAnimation();
		}
	}
	
}
