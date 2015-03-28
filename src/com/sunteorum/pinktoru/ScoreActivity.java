package com.sunteorum.pinktoru;

import java.util.ArrayList;

import com.sunteorum.pinktoru.entity.LevelEntity;
import com.sunteorum.pinktoru.helper.LoadImageThread;
import com.sunteorum.pinktoru.util.ImageUtils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ScoreActivity extends BaseActivity implements OnClickListener {
	private int imageId, stage;
	private int gameId, levelId;
	private String imageUri, imagePath;
	private long score;
	private ArrayList<LevelEntity> games = null;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = View.inflate(this, R.layout.activity_score, null);
		setContentView(rootView);
		
		Intent i = this.getIntent();
		Bundle bundle = i.getExtras();
		
		if (this.getIntent().hasExtra("games"))
			games = getIntent().getParcelableArrayListExtra("games");
		
		imageId = bundle.containsKey("imageId") ? bundle.getInt("imageId") : 0;
		gameId = bundle.containsKey("imageId") ? bundle.getInt("gameId") : 1;
		levelId = bundle.containsKey("levelId") ? bundle.getInt("levelId") : 0;
		stage = bundle.containsKey("stage")?bundle.getInt("stage"):1;
		score = bundle.containsKey("score")?bundle.getLong("score"):0;
		imageUri = bundle.getString("imageUri");
		imagePath = bundle.getString("imagePath");
		String title = bundle.containsKey("title")?bundle.getString("title"):"";
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		Drawable bg = ImageUtils.readDrawable(this, imagePath, dm.widthPixels, dm.heightPixels);
		rootView.setBackgroundDrawable(bg);
		
		findViewById(R.id.btnContinue).setOnClickListener(this);
		findViewById(R.id.btnExit).setOnClickListener(this);
		
		TextView tvTitle = (TextView)findViewById(R.id.txtTitle);
        tvTitle.setText(title);
        TextView tvScore = (TextView)findViewById(R.id.txtScore);
        Time t = new Time();
        t.set(score);
        tvScore.setText(t.format("%M:%S")); //String.format("%M:%S", score);
        
        app.setTotalGameTime(score);
        
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		int currentViewID = v.getId();
		Intent i = null;
		int mode = 1, row, line;
		if (games == null && gameId > 1) {
			
		}
		
		if (games != null && games.size() >= stage) {
			mode = games.get(stage -1).getGameMode();
			row = games.get(stage -1).getPieceRow();
			line = games.get(stage -1).getPieceLine();
			imageUri = games.get(stage -1).getImageUrl();
		} else {
			LevelEntity le = app.getLevelById((levelId > 0) ? levelId : stage);
			if (le != null) {
				mode = le.getGameMode();
				row = le.getPieceRow();
				line = le.getPieceLine();
				if (!android.text.TextUtils.isEmpty(le.getImageUrl())) imageUri = le.getImageUrl();
			} else {
				mode = app.getGameMode();
				row = 12;
				line = 9;
			}
		}
		
		Class<?> GAME = app.getGameClass(mode);
		
		switch (currentViewID) {
		case R.id.btnContinue:

			i = new Intent(ScoreActivity.this, GAME);
			String action = "NEW_GAME_ACTION";
			i.setAction(action);
			
			Bundle bundle = new Bundle();
			bundle.putInt("imageId", imageId);
			bundle.putString("imageUri", imageUri);
			bundle.putInt("levelId", levelId);
			bundle.putInt("row", row);
			bundle.putInt("line", line);
			
			//读取该级别的分配
			if (app.offline) {
				/*MyDBAdapter db = new MyDBAdapter(PicView.this);
				db.open();
				Cursor cursor = db.getEntry("pt_level", "level_id", levelId);
				cursor.moveToFirst();
				int row = cursor.getInt(cursor.getColumnIndexOrThrow("piece_row"));
				int line = cursor.getInt(cursor.getColumnIndexOrThrow("piece_line"));
				cursor.close();
				db.close();
				
				bundle.putString("imagePath", imagePath);
				bundle.putInt("levelId", levelId);
				bundle.putInt("row", row);
				bundle.putInt("line", line);*/
			}
			
			if (!android.text.TextUtils.isEmpty(imageUri) && app.getCacheImagePath(imageUri) == null) {
				final Intent intent = i;
				intent.putExtras(bundle);
				final ProgressDialog progdlg = ProgressDialog.show(ScoreActivity.this, null, "正在读取资源，请稍候...", true, true);
				new LoadImageThread(app, imageUri, null, new LoadImageThread.Callback() {

					@Override
					public void onGetImage(Bitmap bmp, String url) {
						progdlg.dismiss();
						startActivity(intent);
			    		finish();
					}
					
				}).start();
				
				return;
			
			}
			
			i.putExtras(bundle);
			
			break;
		case R.id.btnExit:
			finish();
			return;
		default:
			
		}
		
		startActivity(i);
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		finish();
	}
	
	

}
