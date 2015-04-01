package com.sunteorum.pinktoru;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;

public class MainActivity extends BaseActivity {

	private Intent intent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		final View root_view = findViewById(android.R.id.content);
		//getWindow().getDecorView();
		
		intent = new Intent(MainActivity.this, HomeActivity.class);
		
		//设置动画
		final Animation hide = AnimationUtils.loadAnimation(this, R.anim.hide_half);
		hide.setAnimationListener(hide_listener);
		
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				root_view.startAnimation(hide);
				
			}
			
		}, 800);
		
		
	}

	AnimationListener hide_listener = new AnimationListener() {

		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			
			finish();
		}

		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
	};

}
