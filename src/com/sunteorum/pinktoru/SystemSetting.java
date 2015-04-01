package com.sunteorum.pinktoru;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SystemSetting extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        
    	addPreferencesFromResource(R.xml.settings);
    	
    	SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(this);
    	setting.registerOnSharedPreferenceChangeListener(this);
    	
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// TODO Auto-generated method stub
		if (key.equals("gamemode")) {
			
		} else if (key.equals("piececutflag")) {
			
		} else if (key.equals("piecerenderflag")) {
			
		} else {
			
			
		}
		
	}
    
    

}
