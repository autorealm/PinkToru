package com.sunteorum.pinktoru;

import com.sunteorum.pinktoru.util.ImageUtils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.TextView;

public class ScoreActivity extends BaseActivity {
	private int imageId;
	private int levelId;
	private String imagePath;
	private String action;
	private long score;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		

		Intent i = this.getIntent();
		Bundle bundle = i.getExtras();
		action = i.getAction();
		imageId = bundle.getInt("imageId");
		levelId = bundle.containsKey("levelId")?bundle.getInt("levelId"):1;
		score = bundle.containsKey("score")?bundle.getLong("score"):0;
		imagePath = bundle.getString("imagePath");
		String title = bundle.containsKey("title")?bundle.getString("title"):"";
		Drawable bg = ImageUtils.readDrawable(this, imagePath, 0, 0);
		this.getWindow().getDecorView().setBackgroundDrawable(bg);
		
		TextView tvTitle = (TextView)findViewById(R.id.txtTitle);
        tvTitle.setText(title);
        TextView tvScore = (TextView)findViewById(R.id.txtScore);
        Time t = new Time();
        t.set(score);
        tvScore.setText(t.format("%M:%S"));
        ((PinkToru) this.getApplication()).setTotalGameTime(score);
        
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
