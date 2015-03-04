package com.sunteorum.pinktoru;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private EditText edtAccount;
	private EditText edtPassword;
	private String mAccount;
	private String mPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
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
		// TODO Auto-generated method stub
		
	}

	protected boolean Empty(EditText editText) {
		String text = editText.getText().toString().trim();
		if (text != null && text.length() > 0) {
			return false;
		}
		
		return true;
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
