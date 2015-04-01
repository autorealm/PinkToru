package com.sunteorum.pinktoru;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.sunteorum.pinktoru.entity.UserEntity;
import com.sunteorum.pinktoru.util.Common;
import com.sunteorum.pinktoru.util.HttpUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity implements OnFocusChangeListener {
	String username;
	String password;
	String email;
	String phone;
	
	final String murl = "http://app.sunteorum.com/pinktoru/reg.php";
	Handler mHandler = new Handler();
	PinkToru app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		app = (PinkToru) this.getApplication();
		
		findViewById(R.id.edt_reg_username).setOnFocusChangeListener(this);
		findViewById(R.id.edt_reg_password).setOnFocusChangeListener(this);
		findViewById(R.id.edt_reg_email).setOnFocusChangeListener(this);
		
		((Button) findViewById(R.id.btnRegister)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startRegister();
				
			}
			
		});
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) return;
		
		String txt = ((EditText) v).getText().toString();
		if (txt == null || txt.length() < 4) {
			
			return;
		}
		switch (v.getId()) {
		case R.id.edt_reg_username:
			username = txt;
			break;
		case R.id.edt_reg_password:
			password = txt;
			break;
		case R.id.edt_reg_email:
			if (!matchEmail(txt)) {
				email = null;
			} else email = txt;
			
			break;
		}
		
	}
	
	private boolean validate() {
		EditText edtum = (EditText) findViewById(R.id.edt_reg_username);
		EditText edtpw = (EditText) findViewById(R.id.edt_reg_password);
		EditText edtem = (EditText) findViewById(R.id.edt_reg_email);
		
		ArrayList<String> err = new ArrayList<String>();
		String txt1 = edtum.getText().toString();
		String txt2 = edtpw.getText().toString();
		String txt3 = edtem.getText().toString();
		
		if (txt1.length() < 2 || txt2.length() < 4) {
			err.add("输入字数过少，用户名和密码不能少于4位数。");
		} else {
			username = txt1;
			password = txt2;
		}
		
		if (!matchEmail(txt3)) {
			err.add("邮箱地址不规范，邮箱格式 \"name@sample.com\"。");
		} else {
			email = txt3;
		}
		
		if (!matchPhone(txt1)) {
			err.add("手机号码不规范，手机号码需要11位数字。");
		} else {
			phone = txt1;
		}
		
		if (err.size() == 0)
			return true;
		else {
			String msg = "";
			for (int i = 1; i < err.size() + 1; i++) {
				if ( i == 1) msg = "\t" + i + ". " + err.get(0);
				else msg += "\n\t" + i + ". " + err.get(i - 1);
			}
			
			Common.showTip(RegisterActivity.this, "无法进行注册", "请检查以下错误：\n\n" + msg);
			return false;
		}
		
	}
	
	private boolean matchEmail(String text) {
		if (Pattern.compile("\\w[\\w.-]*@[\\w.]+\\.\\w+").matcher(text).matches()) {
			return true;
		}
		
		return false;
	}

	private boolean matchPhone(String text) {
		if (Pattern.compile("(\\d{11})|(\\+\\d{3,})").matcher(text).matches()) {
			return true;
		}
		
		return false;
	}
	
	private void startRegister() {
		
		if (!validate()) {
			return;
		}
		
		app.putAsyncTask(new AsyncTask<Void, String, Boolean>() {
		
		ProgressDialog progd;
		Map<String, String> pmap = new HashMap<String, String>();
		String msg = null;
		int uid;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			String imei = "",tel = "",iccid = "",imsi = "";
			try {
				TelephonyManager tm = (TelephonyManager) RegisterActivity.this.getSystemService(TELEPHONY_SERVICE);
		        imei = tm.getDeviceId();
		        tel = tm.getLine1Number();
		        iccid =tm.getSimSerialNumber();
		        imsi =tm.getSubscriberId();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			pmap.put("user_name", username);
			pmap.put("user_password", password);
			pmap.put("user_email", email);
			pmap.put("user_phone", phone);
			pmap.put("user_uid", app.getConfigString("uuid", ""));
			pmap.put("imei", imei);
			pmap.put("tel", tel);
			pmap.put("iccid", iccid);
			pmap.put("imsi", imsi);
			
			final AsyncTask<Void, String, Boolean> ask = this;
			
			progd = ProgressDialog.show(RegisterActivity.this, null, "正在注册,请稍后…", true, false);
			progd.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					ask.cancel(true);
					progd.cancel();
					Toast.makeText(RegisterActivity.this, "已取消注册", Toast.LENGTH_SHORT).show();
				}
				
			});
			
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (progd != null && progd.isShowing()) {
						progd.setCancelable(true);
						Toast.makeText(RegisterActivity.this, "", Toast.LENGTH_SHORT).show();
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
				String user_id = jso.get("user_id").toString();
				if (user_id.length() > 0)
					uid = Integer.parseInt(user_id);
				else
					uid = 000000;
				
				if (i == 0) publishProgress();
				else return false;
				
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
				Toast.makeText(RegisterActivity.this, "出现未知错误", Toast.LENGTH_SHORT).show();
			else
				if (progd != null && progd.isShowing()) progd.setTitle("用户验证成功...");
			
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (progd != null && progd.isShowing()) progd.dismiss();
			if (result) {
				UserEntity ue = new UserEntity(username, password);
				ue.setPoints(300);
				ue.setUserId(uid);
				ue.setEmail(email);
				ue.setPhone(phone);
				
				app.setUser(ue);
				
				Toast.makeText(RegisterActivity.this, "注册成功，新账户已登录", Toast.LENGTH_SHORT).show();
				setResult(Activity.RESULT_OK, RegisterActivity.this.getIntent());
				
				finish();
			} else {
				Common.showTip(RegisterActivity.this, "注册失败", msg);
				
			}
		}
		
		});
	}
	
}
