package com.sunteorum.pinktoru;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sunteorum.pinktoru.adapter.DragGridAdapter;
import com.sunteorum.pinktoru.entity.GameEntity;
import com.sunteorum.pinktoru.entity.LevelEntity;
import com.sunteorum.pinktoru.helper.LoadImageThread;
import com.sunteorum.pinktoru.util.Common;
import com.sunteorum.pinktoru.view.DragGridView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;

public class CustomActivity extends BaseActivity {

	private DragGridAdapter adapter;
	private DragGridView gridStages;
	private EditText edtName, edtDesc;
	private ImageView imgIcon;
	private Button btnMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_custom);
		
		init();
		
		if (getIntent() == null || !getIntent().hasExtra("game")) return;
		GameEntity ge = getIntent().getParcelableExtra("game");
		
		edtName.setText(ge.getGameName());
		edtDesc.setText(ge.getGameDesc());
		String surl = (android.text.TextUtils.isEmpty(ge.getGameIconUrl()) ? 
				ge.getGameImageUrl() : ge.getGameIconUrl());
		new LoadImageThread(app, surl, imgIcon, 0, null).start();
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		
		new AlertDialog.Builder(this)
		.setTitle("要退出游戏的定制吗？")
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				finish();
			}
		})
		.setNegativeButton(R.string.btn_cancel, null)
		.create().show();
		
	}

	protected void init() {
		gridStages = (DragGridView) findViewById(R.id.grid_game_stages);
		edtName = (EditText) findViewById(R.id.edt_game_name);
		edtDesc = (EditText) findViewById(R.id.edt_game_desc);
		btnMode = (Button) findViewById(R.id.btn_game_mode);
		imgIcon = (ImageView) findViewById(R.id.img_game_image);
		
		adapter = new DragGridAdapter(this, getStages(null));
		gridStages.setAdapter(adapter);
		gridStages.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				showStageEditer(null);
				
			}
			
		});
		
		btnMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	}
	
	protected List<Map<String, Object>> getStages(String json) {
		List<Map<String, Object>> stages = new ArrayList<Map<String, Object>>();
		
		if (json == null) {
			Map<String, Object> map;
			for (int i = 1; i < 10; i++) {
				map = new HashMap<String, Object>();
				
				map.put("name", "Stage " + i + " ");
				map.put("desc", "");
				
				stages.add(map);
			}
		}
		
		return stages;
	}
	
	protected void dlog(String[] effects, final boolean[] effect_flags) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle("选择提示效果");
		builder.setMultiChoiceItems(effects, effect_flags, new DialogInterface.OnMultiChoiceClickListener() {

			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				effect_flags[which] = isChecked;
				Common.showToast(getApplicationContext(), "" + effect_flags[which]);
			}
			
		}).setPositiveButton("确认", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				int len = effect_flags.length;
				for (int i=0; i<len; i++) {
					//db.updateEntry("pt_effect", i+1, "is_checked", effect_flags[i]);
				}
				
				dialog.dismiss();
			}
			
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
			
		}).show();
	}
	
	protected void openLevelDialog(String[] levels, int currentLevel) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("选择难易度");
		builder.setSingleChoiceItems(levels, currentLevel, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int item) {
				// TODO Auto-generated method stub
				
				dialog.cancel();
				
			}
			
		}).show();
		
	}


	protected void showStageEditer(LevelEntity le) {
		View layout = View.inflate(this, R.layout.fragment_stage, null);
		
		if (le != null) {
			
		}
		
		final PopupWindow mPop = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		mPop.setAnimationStyle(android.R.style.Animation_Translucent);
		mPop.setBackgroundDrawable(getResources().getDrawable(R.drawable.backgroud_1));
	 	mPop.setOutsideTouchable(true);
	 	mPop.setFocusable(true);
	 	mPop.showAtLocation(findViewById(android.R.id.content), android.view.Gravity.CENTER, 0, 0);
	 	mPop.update();
	 	
	 	OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				switch (v.getId()) {
					
				}
				
				
				
			}
	 	};
	 	
	}

}
