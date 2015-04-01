package com.sunteorum.pinktoru;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.sunteorum.pinktoru.entity.UserEntity;
import com.sunteorum.pinktoru.util.AppUtils;
import com.sunteorum.pinktoru.util.Common;
import com.sunteorum.pinktoru.util.HttpUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private EditText edtAccount;
	private EditText edtPassword;
	private String mAccount;
	private String mPassword;
	
	protected Handler mHandler = new Handler();
	public PinkToru app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		app = (PinkToru) this.getApplication();
		
		edtAccount = (EditText) findViewById(R.id.edtUsername);
		edtPassword = (EditText) findViewById(R.id.edtPassword);
		
		initUserFromShared();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:
			if (app.getUser() != null) {
				new AlertDialog.Builder(LoginActivity.this)
					.setTitle("要注销登录吗？")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int which) {
							app.setUser(null);
							String uuid = AppUtils.getDeviceUUID(LoginActivity.this);
							app.saveConfig("uuid", uuid);
							Toast.makeText(LoginActivity.this, "已注销登录", Toast.LENGTH_SHORT).show();
							
							finish();
							
						}
					}).setNegativeButton("取消", null)
					.create().show();
				
			} else {
			
				startLogin();
			}
			
			break;
		}
		
		
	}

	protected boolean Empty(EditText editText) {
		String text = editText.getText().toString().trim();
		if (text != null && text.length() > 0) {
			return false;
		}
		
		return true;
	}

	private boolean validateAccount() {
		mAccount = null;
		if (Empty(edtAccount)) {
			Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
			edtAccount.requestFocus();
			return false;
		}
		String account = edtAccount.getText().toString().trim();
		
		if (account.length() < 3) {
			Toast.makeText(this, "账号格式不正确", Toast.LENGTH_SHORT).show();
			edtAccount.requestFocus();
			return false;
		}
		
		mAccount = account;
		return true;
	}

	private boolean validatePassword() {
		mPassword = null;
		if (Empty(edtPassword)) {
			Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			edtPassword.requestFocus();
			return false;
		}
		String pwd = edtPassword.getText().toString().trim();
		if (pwd.length() < 4) {
			Toast.makeText(this, "密码位数不足", Toast.LENGTH_SHORT).show();
			edtPassword.requestFocus();
			return false;
		}
		
		mPassword = pwd;
		return true;
	}
	
	protected void startLogin() {
		
		if ((!validateAccount()) || (!validatePassword())) {
			return;
		}
		
		app.putAsyncTask(new AsyncTask<Void, String, Boolean>() {
			final String murl = "http://app.sunteorum.com/pinktoru/login.php";
			ProgressDialog progd;
			Map<String, String> pmap = new HashMap<String, String>();
			String msg = null;
			String userId;
			String userEm;
			String userPh;
			String userUid;
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pmap.put("user_name", mAccount);
				pmap.put("user_password", mPassword);
				
				progd = new ProgressDialog(LoginActivity.this);
				progd.setTitle(null);
				progd.setMessage("正在登录,请稍后…");
				progd.setIndeterminate(true);
				progd.setCancelable(false);
				progd.show();
				
				final AsyncTask<Void, String, Boolean> ask = this;
				progd.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						ask.cancel(true);
						progd.cancel();
						Toast.makeText(LoginActivity.this, "已取消登录", Toast.LENGTH_SHORT).show();
					}
					
				});
				
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (progd != null && progd.isShowing()) {
							progd.setCancelable(true);
							
						}
						
					}
					
				}, 10*1000);
				
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					String rst = HttpUtils.postHttpRequest(murl, pmap);
					if (rst == null || rst.length() == 0) {
						msg = "服务器无回应，请与管理员联系。";
						return false;
					}
					
					JSONObject json = new JSONObject(rst);
					int i = json.getInt("result");
					msg = json.getString("msg");
					if (!json.has("user_info")) {
						msg = "未找到用户信息";
						return false;
					}
					JSONObject jso = json.getJSONObject("user_info");
					userId = jso.get("user_id").toString();
					userEm = jso.get("user_email").toString();
					userPh = jso.get("user_phone").toString();
					
					userUid = jso.get("user_uid").toString();
					app.saveConfig("uuid", userUid);
					
					if (i == 0) publishProgress("用户验证成功");
					else return false;
					
					//已下待完成获取用户数据
					Thread.sleep(500);
					publishProgress("正在读取用户数据...");
					//rst = Common.getConnectResult("http://adr.czclub.cn:8909/DIY/user.php?id=" + userId);
					
					HttpUtils.getConnectResult("http://pt.939j.com/pt_init.php?UID=" + userUid);
					
					Thread.sleep(500);
					if (true) {
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
					msg = e.getMessage();
				}
				return false;
			}

			@Override
			protected void onProgressUpdate(String... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
				if (values == null)
					Toast.makeText(LoginActivity.this, "出现未知错误", Toast.LENGTH_SHORT).show();
				else 
					if (progd != null && progd.isShowing()) progd.setMessage(values[0]);
				
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (progd != null && progd.isShowing()) progd.dismiss();
				if (result) {
					//测试使用帐号
					UserEntity ue = new UserEntity(mAccount, mPassword);
					ue.setUserId(Integer.valueOf(userId));
					ue.setPoints(9999);
					ue.setUuid(userUid);
					ue.setEmail(userEm);
					ue.setPhone(userPh);
					
					app.setUser(ue);
					
					setUserToShared(true);
					
					Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
					setResult(Activity.RESULT_OK, LoginActivity.this.getIntent());
					
					finish();
				} else {
					
					Common.showTip(LoginActivity.this, "登录失败", msg);
					
				}
			}
		});
		
	}
	
	private void initUserFromShared() {
		SharedPreferences sdf = this.getSharedPreferences("pt_config", Context.MODE_PRIVATE);
		if (!sdf.contains("username") || !sdf.contains("password")) return;
		
		edtAccount.setText(sdf.getString("username", ""));
		edtPassword.setText(sdf.getString("password", ""));
		
	}

	protected boolean setUserToShared(boolean save) {
		SharedPreferences sdf = this.getSharedPreferences("pt_config", Context.MODE_PRIVATE);
		SharedPreferences.Editor edt = sdf.edit();
		if (save) {
			if (mAccount == null || mPassword == null) return false;
			if (mAccount.length() == 0 || mPassword.length() == 0) return false;
			edt.putString("username", mAccount);
			edt.putString("password", mPassword);
		} else {
			edt.remove("username");
			edt.remove("password");
		}
		
		return edt.commit();
		
	}

	
}
