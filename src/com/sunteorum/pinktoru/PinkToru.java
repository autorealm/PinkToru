package com.sunteorum.pinktoru;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.sunteorum.pinktoru.entity.LevelEntity;
import com.sunteorum.pinktoru.entity.UserEntity;
import com.sunteorum.pinktoru.util.AppUtils;

import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;

/**
 * Ӧ�õ�ȫ�ֱ���
 * @author KYO
 *
 */
public class PinkToru extends Application {
	final String CONF = "pt_config";	//�������ñ�����
	final String APP_DIR = "FADWorks/PinkToru";
	final String APP_CACHE_DIR = "Cache";
	final String APP_IMAGE_DIR = "Image";
	
	private UserEntity user;
	protected ArrayList<LevelEntity> games;
	//protected PrizeEntity pe;
	protected boolean offline = false;	//�Ƿ�������Ϸģʽ��������
	final int INACCURACY = 12;	//�ж�ƴ�ϵľ���
	
	private int gamePass = 1;	//��ǰ��Ϸ�Ĺ���(�ڼ���)������Ϸ�Ѷȵȼ�
	private long gameTime = 0;	//ͬһ��Ϸ�ۼ�ʱ��
	
	private boolean absinmove = true;	//�Ƿ��ƶ�ʱ����ƴ���ж�
	
	private int gameMode = 0;	//��Ϸģʽ
	private int pieceCutFlag = 0;	//��Ƭ�ָʽ
	private int pieceRenderFlag = 1;	//��Ƭ��Ⱦ��ʽ
	private int pieceEdgeWidth = 16;	//��Ƭ��Ե���
	private int pieceShadowOffset = 3;	//��Ƭ��Ӱƫ����
	private int pieceShadowColor = Color.DKGRAY;	//��Ƭ��Ӱ��ɫ
	private int pieceKochCurveN = 2;
	
	private PTReceiver mReceiver = null;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		CrashHandler.getInstance().init(getApplicationContext());
		
		
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		if (mReceiver != null) unregisterReceiver(mReceiver);
		this.saveConfig("last_exit_time", System.currentTimeMillis());
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	
	public void init() {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this) ;
		absinmove = prefs.getBoolean("absinmove", true);
		
		try {
			gameMode = Integer.parseInt(prefs.getString("gamemode", "0"));
			pieceCutFlag = Integer.parseInt(prefs.getString("piececutflag", "0"));
			pieceRenderFlag = Integer.parseInt(prefs.getString("piecerenderflag", "1"));
			
			pieceKochCurveN = Integer.parseInt(prefs.getString("piecekochcurven", "2"));
			pieceEdgeWidth = Integer.parseInt(prefs.getString("pieceedgewidth", "16"));
			pieceShadowOffset = Integer.parseInt(prefs.getString("pieceshadowoffset", "3"));
			pieceShadowColor = Integer.parseInt(prefs.getString("pieceshadowcolor", "" + Color.DKGRAY));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void setGames(String json) {
		games = new ArrayList<LevelEntity>();
		try {
			//System.out.println(json);
			JSONArray jso = new JSONArray(json);
			for (int i = 0; i < jso.length(); i++) {
				LevelEntity ge = new LevelEntity(jso.optString(i));
				games.add(ge);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	public LevelEntity getGameAtIndex(int index) {
		if (games == null || games.size() < index + 1) return null;
		LevelEntity ge = games.get(index);
		return ge;
	}
	
	public String getGameImageAtIndex(int index) {
		if (games == null || games.size() < index + 1) return null;
		LevelEntity le = games.get(index);
		return le.getImageUrl();
	}
	
	public void setGameModeAtIndex(int index) {
		if (games == null || games.size() < index + 1) return; //this.gameMode = 0;
		LevelEntity ge = games.get(index);
		this.gameMode = ge.getGameMode() - 1;
		System.out.println("GameMode:" + this.gameMode);
	}
	
	public Class<?> getGameClass(int gameMode) {
		Class<?> GAME = null;
		if (gameMode == 1) {
			GAME = PintuGameActivity.class;
		}  else if (gameMode == 2) {
			GAME = FillGameActivity.class;
		}  else if (gameMode == 3) {
			GAME = SwapGameActivity.class;
		} else {
			GAME = PokeGameActivity.class;
		}
		
		return GAME;
	}
	
	public String getUUID() {
		String uuid = "";
		if (getConfigString("uuid", "").equals("") || getConfigString("uuid", "").length() < 8) {
			uuid = AppUtils.getDeviceUUID(this);
			saveConfig("uuid", uuid);
		} else {
			uuid = getConfigString("uuid", "");
		}
		
		return uuid;
	}


	public long getGameTime() {
		return gameTime;
	}

	public void setGameTime(long gameTime) {
		this.gameTime = gameTime;
	}

	public void setTotalGameTime(long gameTime) {
		this.gameTime += gameTime;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public int getGamePass() {
		return gamePass;
	}

	public void setGamePass(int gamePass) {
		this.gamePass = gamePass;
	}

	public boolean isAbsinmove() {
		return absinmove;
	}

	public void setAbsinmove(boolean absinmove) {
		this.absinmove = absinmove;
	}

	public int getGameMode() {
		return gameMode;
	}

	public void setGameMode(int gameMode) {
		this.gameMode = gameMode;
	}

	public int getPieceCutFlag() {
		return pieceCutFlag;
	}

	public void setPieceCutFlag(int pieceCutFlag) {
		this.pieceCutFlag = pieceCutFlag;
	}

	public int getPieceRenderFlag() {
		return pieceRenderFlag;
	}

	public void setPieceRenderFlag(int pieceRenderFlag) {
		this.pieceRenderFlag = pieceRenderFlag;
	}

	public int getPieceEdgeWidth() {
		return pieceEdgeWidth;
	}

	public void setPieceEdgeWidth(int pieceEdgeWidth) {
		this.pieceEdgeWidth = pieceEdgeWidth;
	}

	public int getPieceShadowOffset() {
		return pieceShadowOffset;
	}

	public void setPieceShadowOffset(int pieceShadowOffset) {
		this.pieceShadowOffset = pieceShadowOffset;
	}

	public int getPieceShadowColor() {
		return pieceShadowColor;
	}

	public void setPieceShadowColor(int pieceShadowColor) {
		this.pieceShadowColor = pieceShadowColor;
	}

	public int getPieceKochCurveN() {
		return pieceKochCurveN;
	}

	public void setPieceKochCurveN(int pieceKochCurveN) {
		this.pieceKochCurveN = pieceKochCurveN;
	}

	/**
	 * ��ȡӦ�õ�Ĭ�ϻ���Ŀ¼
	 * @return
	 */
	public File getAppCacheDir() {
		File cacheDir;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			cacheDir = new File(Environment.getExternalStorageDirectory(), APP_DIR + File.separator + APP_CACHE_DIR);
		else
			return this.getCacheDir();
		if (!cacheDir.exists()) cacheDir.mkdirs();
		
		return cacheDir;
	}
	
	public File getAppImageDir() {
		File cacheDir;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			cacheDir = new File(Environment.getExternalStorageDirectory(), APP_DIR + File.separator + APP_IMAGE_DIR);
		else
			return new File(this.getFilesDir() + File.separator + APP_IMAGE_DIR);
		if (!cacheDir.exists()) cacheDir.mkdirs();
		
		return cacheDir;
	}
	
	public String getCacheImagePath(String imageUrl) {
		File cacheDir = getAppCacheDir();
		File f = new File(cacheDir, this.getCacheImageName(imageUrl));
		String imagePath = f.getAbsolutePath();
		if (!f.exists()) {
			return null;
		}
		
		return imagePath;
	}
	
	public String getCacheImageName(String imageUrl) {
		String name = String.valueOf(imageUrl.hashCode());
		
		return name;
	}
	
	protected void downloadReceiver(String fileUrl) {
	    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		
		Uri uri = Uri.parse(fileUrl);
		DownloadManager.Request request = new DownloadManager.Request(uri);
		
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);  
		
		//��ֹ����֪ͨ���Ⱥ�̨���أ����Ҫʹ����һ���������һ��Ȩ�ޣ�android.permission.DOWNLOAD_WITHOUT_NOTIFICATION  
		//request.setShowRunningNotification(false);  
		
		//����ʾ���ؽ���  
		request.setVisibleInDownloadsUi(false);
		/* �������غ��ļ���ŵ�λ��,���sdcard�����ã���ô������������������ò��������sdcard���ã����غ���ļ�
			��/mnt/sdcard/Android/data/packageName/filesĿ¼���棬���sdcard������,������������������������ã����غ���ļ���/cache���  Ŀ¼����
		*/
		//request.setDestinationInExternalFilesDir(this, null, "tar.apk");
		long id = downloadManager.enqueue(request);
		//TODO ��id����ã��ڽ���������Ҫ�ã���ñ�����Preferences����
		
		mReceiver = new PTReceiver(id);
		IntentFilter filter = new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE");
		
		registerReceiver(mReceiver, filter);
		
	}
	
	/**
	 * ����Ӧ������
	 * @param conf
	 * @return
	 */
	protected boolean saveConfig(Map<String, Object> conf) {
		SharedPreferences sdf = this.getSharedPreferences(CONF, Context.MODE_PRIVATE);
		SharedPreferences.Editor edt = sdf.edit();
		
		for (String k : conf.keySet()) {
			Object v = conf.get(k);
			
			if (String.class.isInstance(v)) {
				edt.putString(k, v.toString());
				
			} else if (Integer.class.isInstance(v)) {
				edt.putInt(k, Integer.parseInt(v.toString()));
				
			} else if (Long.class.isInstance(v)) {
				edt.putLong(k, Long.parseLong(v.toString()));
				
			} else if (Boolean.class.isInstance(v)) {
				edt.putBoolean(k, Boolean.parseBoolean(v.toString()));
				
			} else if (Float.class.isInstance(v)) {
				edt.putFloat(k, Float.parseFloat(v.toString()));
				
			} else {
				edt.putString(k, v.toString());
			}
			
		}
		
		return edt.commit();
	}
	
	protected boolean saveConfig(String k, Object v) {
		SharedPreferences sdf = this.getSharedPreferences(CONF, Context.MODE_PRIVATE);
		SharedPreferences.Editor edt = sdf.edit();
		
		if (String.class.isInstance(v)) {
			edt.putString(k, v.toString());
			
		} else if (Integer.class.isInstance(v)) {
			edt.putInt(k, Integer.parseInt(v.toString()));
			
		} else if (Long.class.isInstance(v)) {
			edt.putLong(k, Long.parseLong(v.toString()));
			
		} else if (Boolean.class.isInstance(v)) {
			edt.putBoolean(k, Boolean.parseBoolean(v.toString()));
			
		} else if (Float.class.isInstance(v)) {
			edt.putFloat(k, Float.parseFloat(v.toString()));
			
		} else {
			edt.putString(k, v.toString());
		}
		
		return edt.commit();
	}
	
	protected boolean removeConfig(String key) {
		SharedPreferences sdf = this.getSharedPreferences(CONF, Context.MODE_PRIVATE);
		SharedPreferences.Editor edt = sdf.edit();
		if (sdf.contains(key)) {
			edt.remove(key);
			return edt.commit();
		}
		
		return true;
	}
	
	protected boolean containsConfig(String key) {
		SharedPreferences sdf = this.getSharedPreferences(CONF, Context.MODE_PRIVATE);
		return sdf.contains(key);
		
	}
	
	protected Map<String, Object> getConfigMap(String conf_name) {
		String cname = conf_name;
		if (conf_name == null || conf_name.trim().length() == 0) cname = CONF;
		
		SharedPreferences sdf = this.getSharedPreferences(cname, Context.MODE_PRIVATE);
		Map<String, Object> conf = new HashMap<String, Object>();
		/*
		Map<String,?> all = sdf.getAll();
		Object[] sk = all.keySet().toArray();
		for (int i = 0; i < sk.length; i++) {
			conf.put(sk[i].toString(), all.get(sk[i].toString()).toString());
		}
		*/
		List<Map.Entry<String, ?>> lme = new ArrayList<Map.Entry<String, ?>>(sdf.getAll().entrySet());
		for (int i = 0; i< lme.size(); i++) {
			try {
				conf.put(lme.get(i).getKey(), lme.get(i).getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return conf;
	}
	
	protected String getConfig(String key) {
		SharedPreferences sdf = this.getSharedPreferences(CONF, Context.MODE_PRIVATE);
		if (sdf.contains(key)) return sdf.getString(key, null);
		else return null;
		
	}
	
	protected String getConfigString(String key, String default_value) {
		SharedPreferences sdf = this.getSharedPreferences(CONF, Context.MODE_PRIVATE);
		
		return sdf.getString(key, default_value);
		
	}
	
	protected int getConfigInt(String key, int default_value) {
		SharedPreferences sdf = this.getSharedPreferences(CONF, Context.MODE_PRIVATE);
		
		return sdf.getInt(key, default_value);
		
	}
	
	protected boolean getConfigBoolean(String key, boolean default_value) {
		SharedPreferences sdf = this.getSharedPreferences(CONF, Context.MODE_PRIVATE);
		
		return sdf.getBoolean(key, default_value);
		
	}
	
	protected long getConfigLong(String key, long default_value) {
		SharedPreferences sdf = this.getSharedPreferences(CONF, Context.MODE_PRIVATE);
		
		return sdf.getLong(key, default_value);
		
	}
	
	protected float getConfigFloat(String key, float default_value) {
		SharedPreferences sdf = this.getSharedPreferences(CONF, Context.MODE_PRIVATE);
		
		return sdf.getFloat(key, default_value);
		
	}
	
}
