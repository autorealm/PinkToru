package com.sunteorum.pinktoru;

import java.io.File;

import com.sunteorum.pinktoru.view.ZoomImageView;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;

public class ReviewActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.fragment_review);
		ZoomImageView imgView = (ZoomImageView) this.findViewById(R.id.ziv_review);
		
		try {
			String f = this.getIntent().getStringExtra("review_file");
			
			f = Uri.parse(f).getPath();
			
			this.setTitle(new File(f).getName());
			
			imgView.setImageBitmap(BitmapFactory.decodeFile(f));
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		
		final LinearLayout titleBar = (LinearLayout) findViewById(R.id.title_bar);
		titleBar.setVisibility(8);
		
		final Animation anim = AnimationUtils.loadAnimation(ReviewActivity.this, R.anim.slide_up);
		//anim.setFillAfter(true);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				titleBar.setVisibility(8);
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
		
		imgView.setOnImageTouchedListener(new ZoomImageView.onImageTouchedListener() {
			
			@Override
			public void onImageTouched() {
				if (titleBar.getVisibility() == 8) {
					titleBar.startAnimation(AnimationUtils.loadAnimation(ReviewActivity.this, R.anim.slide_down));
					titleBar.setVisibility(0);
				} else {
					titleBar.startAnimation(anim);
				}
				
			}
		});
		
		imgView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				
				
				return false;
			}
			
		});
		
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
