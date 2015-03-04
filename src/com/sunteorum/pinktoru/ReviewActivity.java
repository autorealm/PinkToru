package com.sunteorum.pinktoru;

import com.sunteorum.pinktoru.view.ZoomImageView;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

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
			
			f = Uri.parse(f).toString();
			imgView.setImageBitmap(BitmapFactory.decodeFile(f));
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
