package com.sunteorum.pinktoru;

import com.sunteorum.pinktoru.util.Common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class CustomActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_custom);
		
		init();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	protected void init() {
		
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

}
