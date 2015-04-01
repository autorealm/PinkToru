package com.sunteorum.pinktoru;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.sunteorum.pinktoru.entity.UserEntity;
import com.sunteorum.pinktoru.util.AppUtils;
import com.sunteorum.pinktoru.util.HttpUtils;
import com.sunteorum.pinktoru.view.FlippingImageView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity {

	/** 手势监听 */
	GestureDetector mGestureDetector;
	/** 是否需要监听手势关闭功能 */
	private boolean mNeedBackGesture = false;
	
	PinkToru app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		app = (PinkToru) this.getApplication();
		
		if (mGestureDetector == null) {
			mGestureDetector = new GestureDetector(getApplicationContext(),
					new BackGestureListener(this));
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) return;
		
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onTrimMemory(int level) {
		// TODO Auto-generated method stub
		super.onTrimMemory(level);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	/**
	 * 返回
	 * @param view
	 */
	public void doBack(View view) {
		onBackPressed();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if(mNeedBackGesture){
			return mGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
		}
		
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 设置是否进行手势监听
	 */
	public void setNeedBackGesture(boolean mNeedBackGesture){
		this.mNeedBackGesture = mNeedBackGesture;
	}

	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title) {
		TextView txtTitle = (TextView) this.findViewById(R.id.title);
		if (txtTitle != null) txtTitle.setText(title);
		
	}
	
	/**
	 * 检测新版本
	 */
	protected void checkNewVersion() {
		String params = "?app_name=" + this.getPackageName() + "&ver_code=" + AppUtils.getCoreNum();
		
		String url = "http://app.sunteorum.com/pinktoru/version.php" + params;
		
		HttpUtils.requestHttpGet(url, new HttpUtils.ResultCallBack() {
			
			@Override
			public void onResult(String url, int code, String result) {
				
				if (code != 200) {
					//System.out.println(result);
					return;
				}
				
				if (BaseActivity.this.isFinishing()) return;
				try {
					JSONObject jso = new JSONObject(result);
					
					if (!jso.has("app_info")) return;
					
					jso = jso.getJSONObject("app_info");
					final String durl = jso.get("apk_url").toString();
					String desc = jso.get("update_desc").toString();
					String ver = jso.get("app_ver").toString();
					
					new AlertDialog.Builder(BaseActivity.this)
					.setTitle("发现新版本 (" + ver + ")")
					.setMessage(Html.fromHtml(desc))
					.setIcon(android.R.drawable.ic_menu_info_details)
					.setCancelable(false)
					.setPositiveButton("更新", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							app.downloadReceiver(durl);
							Toast.makeText(BaseActivity.this, "正在后台下载更新...", Toast.LENGTH_SHORT).show();
							finish();
						}
						
					})
					.setNegativeButton("关闭",  null)
					.create().show();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
		});
	}

	/**
	 * 检查游戏
	 * @param context
	 * @param g_id
	 * @param handler
	 */
	protected void checkRemain(final Context context, int g_id, final Handler handler) {
		String params = "?UID=" + "" + "&g_id=" + g_id + "&_t=" + String.valueOf(System.currentTimeMillis());
		String url = "http://app.sunteorum.com/pinktoru/check.php" + params;
		final ProgressDialog progd = new ProgressDialog(context);
		
		if (progd != null && !progd.isShowing()) {
			
			progd.show();
			progd.setContentView(R.layout.common_loading);
			FlippingImageView mFivIcon = (FlippingImageView) progd.findViewById(R.id.fiv_loading_icon);
			if (mFivIcon != null) mFivIcon.startAnimation();
			progd.setCancelable(false);
			progd.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					//Toast.makeText(HomeActivity.this, "已取消进入游戏", Toast.LENGTH_SHORT).show();
				}
				
			});
		}
		
		if (handler != null) {
			handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (progd != null && progd.isShowing()) {
					progd.setCancelable(true);
					
				}
				
			}
			
		}, 10000);}
		
		HttpUtils.requestHttpGet(url, new HttpUtils.ResultCallBack() {
			
			@Override
			public void onResult(String url, int code, String result) {
				//System.out.println(result);
				if (progd != null && progd.isShowing()) progd.dismiss();
				if (code != 200) {
					if (handler != null) handler.sendEmptyMessage(0);
					return;
				}
				//if (context.isRestricted()) return;
				if (BaseActivity.this.isFinishing()) return;
				try {
					JSONObject jso = new JSONObject(result);
					int i = jso.getInt("result");
					if (i == 0) {
						if (handler != null) handler.sendEmptyMessage(0);
						return;
					}
					
					String msg = jso.getString("msg");
					String title = jso.getString("title");
					
					new AlertDialog.Builder(context)
						.setTitle(title)
						.setMessage(Html.fromHtml(msg))
						.setIcon(android.R.drawable.ic_menu_info_details)
						.setCancelable(false)
						.setPositiveButton("确定",  new DialogInterface.OnClickListener(){
	
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (handler != null) handler.sendEmptyMessage(0);
							}
							
						})
						.setNegativeButton("返回",  null)
						.create().show();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				
			}
		});
	}

	/**
	 * 自动登录
	 */
	public void autoLogin() {
		SharedPreferences sdf = getSharedPreferences("pt_config", Context.MODE_PRIVATE);
		if (!sdf.contains("username") || !sdf.contains("password")) return;
		final String username = sdf.getString("username", "");
		final String password = sdf.getString("password", "");
		final UserEntity ue = new UserEntity(username, password);
		ue.setPoints(9999);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> pmap = new HashMap<String, String>();
				final String murl = "http://app.sunteorum.com/pinktoru/login.php";
				pmap.put("user_name", username);
				pmap.put("user_password", password);
				String rst = HttpUtils.postHttpRequest(murl, pmap);
				if (rst == null || rst.length() == 0) {
					return;
				}
				String userId = "";
				String userEm = "";
				String userPh = "";
				
				try {
				JSONObject json = new JSONObject(rst);
				System.out.println(json.getString("msg"));
				if (json.has("user_info")) {
					JSONObject jso = json.getJSONObject("user_info");
					userId = jso.get("user_id").toString();
					userEm = jso.get("user_email").toString();
					userPh = jso.get("user_phone").toString();
					
					String userUid = jso.get("user_uid").toString();
					app.saveConfig("uuid", userUid);
					
				} else return;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				ue.setUserId(Integer.parseInt(userId));
				ue.setEmail(userEm);
				ue.setPhone(userPh);
				
				app.setUser(ue);
			}
			
		}).start();
		
	}

}
