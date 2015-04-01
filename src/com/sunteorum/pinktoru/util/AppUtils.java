package com.sunteorum.pinktoru.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import com.sunteorum.pinktoru.entity.UserEntity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;

public class AppUtils {

	final static String HOST_URL = "";
	
	/**
	 * 检测当前Activity是否在前台
	 * @return 是否在前台
	 */
	public boolean isOnForeground(Activity activity) {
		ActivityManager mActivityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
		String mPackageName = activity.getPackageName();
		@SuppressWarnings("deprecation")
		List<RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
	    if (tasksInfo.size() > 0) {
	        if (mPackageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	/**
	 * 获得应用程序名字
	 * @param context
	 * @return
	 */
	public static String getAppName(Context context) {
		PackageManager packageManager = null; 
		ApplicationInfo applicationInfo = null; 
		try { 
			packageManager = context.getPackageManager(); 
			applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0); 
		} catch (PackageManager.NameNotFoundException e) { 
			applicationInfo = null;
		}
		String appName =  (String) packageManager.getApplicationLabel(applicationInfo);
		
		return appName;
	}
	
	/** 
	* 获得软件版本号
	* @param con
	* @return
	*/
	public static int getVersionCode(final Context con) {
		int version = 1;
		PackageManager packageManager = con.getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(con.getPackageName(), 0);
			version = packageInfo.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return version;
	}
	
	/**
	 * 获得软件版本名称
	 * @param context
	 * @return
	 */
	public static String getVersionName(final Context context){
		String versionName = "1.0.0";
		PackageManager packageManager = context.getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			versionName = packageInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 获取手机配置内存信息
	 * @param c 上下文环境
	 * @return Array 1-total 2-avail
	 */
	public static String[] getTotalMemory(Context c) {
		String[] result = {"",""};
		ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);
		long mTotalMem = 0;
		long mAvailMem = mi.availMem;
		String str1 = "/proc/meminfo";
		String str2;
		String[] arrayOfString;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			mTotalMem = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
			localBufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		result[0] = Formatter.formatFileSize(c, mTotalMem);
		result[1] = Formatter.formatFileSize(c, mAvailMem);
		
		return result;
	}
	
	/**
	 * 获取手机配置CPU信息
	 * @return Array 1-cpu型号  2-cpu频率
	 */
	public static String[] getCpuInfo() {
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		String[] cpuInfo = {"", ""};
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
			}
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			cpuInfo[1] += arrayOfString[2];
			localBufferedReader.close();
		} catch (IOException e) {
		}
		
		return cpuInfo;
	}
	
	/**
	 * 获取手机配置CPU核心数
	 * @return
	 */
	public static int getCoreNum() {
		try {
			//Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			//Filter to only list the devices we care about
			File[] files = dir.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					//Check if filename is "cpu", followed by a single digit number
					if(Pattern.matches("cpu[0-9]", pathname.getName())) {
					   return true;
					}
					return false;
				}
				
			});
			//Return the number of cores (virtual CPU devices)
			return files.length;
		} catch(Exception e) {
			return 1;
		}
	}
	
	/**
	 * 获取手机WIFI的MAC地址
	 * @param c
	 * @return
	 */
	public static String getMacAddress(Context c) {
		String result = "";
		WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		result = wifiInfo.getMacAddress();
		
		return result;
	}
	
	/**
	 * 获取手机唯一识别码
	 * @param c
	 * @return
	 */
	public static String getDeviceUUID(Context c) {
		String imei = ((TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		UUID uuid = UUID.randomUUID();
		try {
			String androidId = Secure.ANDROID_ID;
			
			androidId = androidId + ((imei!=null)?imei:"00000000") + android.os.Build.ID;
			uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return uuid.toString();
	}
	
	/**
	 * 获取手机配置信息
	 * @param c
	 * @return
	 */
	public static Map<String, String> getPhoneInfo(Context c) {
		Map<String, String> pinfo = new HashMap<String, String>();
		
		TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		String imsi = tm.getSubscriberId();
		String numer = tm.getLine1Number();
		String neton = tm.getNetworkOperatorName();
		String simon = tm.getSimOperatorName();
		String sn = tm.getSimSerialNumber();
		String nett = "unknown";
		int ntype = tm.getNetworkType();
		switch (ntype) {
		case TelephonyManager.NETWORK_TYPE_1xRTT: nett = "1xRTT";
			break;
		case TelephonyManager.NETWORK_TYPE_CDMA: nett = "CDMA";
			break;
		case TelephonyManager.NETWORK_TYPE_EDGE: nett = "EDGE";
			break;
		case TelephonyManager.NETWORK_TYPE_EHRPD: nett = "eHRPD";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_0: nett = "EVDO revision 0";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_A: nett = "EVDO revision A";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_B: nett = "EVDO revision B";
			break;
		case TelephonyManager.NETWORK_TYPE_GPRS: nett = "GPRS";
			break;
		case TelephonyManager.NETWORK_TYPE_HSDPA: nett = "HSDPA";
			break;
		case TelephonyManager.NETWORK_TYPE_HSPA: nett = "HSPA";
			break;
		case TelephonyManager.NETWORK_TYPE_HSPAP: nett = "HSPA+";
			break;
		case TelephonyManager.NETWORK_TYPE_HSUPA: nett = "HSUPA";
			break;
		case TelephonyManager.NETWORK_TYPE_IDEN: nett = "iDen";
			break;
		case TelephonyManager.NETWORK_TYPE_LTE: nett = "LTE";
			break;
		case TelephonyManager.NETWORK_TYPE_UMTS: nett = "UMTS";
			break;
		case TelephonyManager.NETWORK_TYPE_UNKNOWN: nett = "unknown";
			break;
			
		}
		String phone = "NONE";
		int ptype = tm.getPhoneType();
		switch (ptype) {
		case TelephonyManager.PHONE_TYPE_CDMA: phone = "CDMA";
			break;
		case TelephonyManager.PHONE_TYPE_GSM: phone = "GSM";
			break;
		case TelephonyManager.PHONE_TYPE_NONE: phone = "NONE";
			break;
		case TelephonyManager.PHONE_TYPE_SIP: phone = "SIP";
			break;
		}
		String ProvidersName = "未知运营商";
		if (imsi != null) {
			if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
				ProvidersName = "中国移动";
			} else if (imsi.startsWith("46001")) {
				ProvidersName = "中国联通";
			} else if (imsi.startsWith("46003")) {
				ProvidersName = "中国电信";
			}
		}
		
		//WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
		//int width = wm.getDefaultDisplay().getWidth();
		//int height = wm.getDefaultDisplay().getHeight();
		int height = c.getResources().getDisplayMetrics().heightPixels;
		int width = c.getResources().getDisplayMetrics().widthPixels;
		float density = c.getResources().getDisplayMetrics().density;
		
		int corenum = getCoreNum();
		String[] cpuinfo = getCpuInfo();
		String[] meminfo = getTotalMemory(c);
		
		//String state = "";
		//NetWorkState ns = getConnectState(c);
		//state = ns.name();
		
		pinfo.put("imsi", imsi);
		pinfo.put("imei", imei);
		pinfo.put("line1_number", numer);
		pinfo.put("net_opt_name", neton);
		pinfo.put("sim_opt_name", simon);
		pinfo.put("sim_sn", sn);
		pinfo.put("net_type", nett);
		pinfo.put("phone_type", phone);
		pinfo.put("provider_name", ProvidersName);
		//pinfo.put("net_state", state);
		pinfo.put("s_width", String.valueOf(width));
		pinfo.put("s_height", String.valueOf(height));
		pinfo.put("density", String.valueOf(density));
		
		pinfo.put("model", android.os.Build.MODEL);
		pinfo.put("brand", android.os.Build.BRAND);
		pinfo.put("product", android.os.Build.PRODUCT);
		pinfo.put("id", android.os.Build.ID);
		//pinfo.put("serial", android.os.Build.SERIAL);
		pinfo.put("display", android.os.Build.DISPLAY);
		pinfo.put("sdk_ver", "" + android.os.Build.VERSION.SDK_INT);
		pinfo.put("rel_ver", android.os.Build.VERSION.RELEASE);
		
		pinfo.put("mac", getMacAddress(c));
		pinfo.put("cpu_m", cpuinfo[0]);
		pinfo.put("cpu_s", cpuinfo[1]);
		pinfo.put("core_num", String.valueOf(corenum));
		pinfo.put("mem_t", meminfo[0]);
		pinfo.put("mem_a", meminfo[1]);
		
		return pinfo;
		
	}
	

	public static void accessLocation(final Context mContext) {
		String serviceName = Context.LOCATION_SERVICE;
        final LocationManager locationManager = (LocationManager) mContext.getSystemService(serviceName);
        //locationManager.setTestProviderEnabled(LocationManager.NETWORK_PROVIDER, true);
        //locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
        
        /*String provider = LocationManager.GPS_PROVIDER;
        //String provider = LocationManager.NETWORK_PROVIDER;
        
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        	android.provider.Settings.Secure.setLocationProviderEnabled
        		(getActivity().getContentResolver(), LocationManager.GPS_PROVIDER, false); 
        }*/
        
		Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        
        String provider = locationManager.getBestProvider(criteria, true);
        
    	String imei = "", tel = "", iccid = "", imsi = "";
		try {
			TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
	        imei = tm.getDeviceId();
	        tel = tm.getLine1Number();
	        iccid = tm.getSimSerialNumber();
	        imsi = tm.getSubscriberId();
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("IMEI=" + imei  + "&ICCID=" + iccid + "&IMSI=" + imsi  + "&TEL=" + tel);
		
        LocationListener locationListener = new LocationListener() {
        	
			@Override
			public void onLocationChanged(Location location) {
				String latLongString = "";
    	    	double lat = 0, lng = 0, alt = 0;
    	    	if (location != null) {
    	    		lat = location.getLatitude();
    	    		lng = location.getLongitude();
    	    		alt = location.getAltitude();
    	    		latLongString = "纬度:" + lat + "\n经度:" + lng + "\n海拔:" + alt;
    	    		
    	    		//requestHttpGet(HEAD_URL + "&x=" + lng + "&y=" + lat, null);
    	    	} else {
    	    		
    	    		latLongString = "无法获取地理信息";
    	    	}
    	    	
    	    	List<Address> addList = null;
    			Geocoder ge = new Geocoder(mContext, Locale.getDefault());
    			try {
    				addList = ge.getFromLocation(lat, lng, 1);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    			if (addList != null && addList.size() > 0){
    				for(int i = 0; i < addList.size(); i++){
    					Address ad = addList.get(i);
    					latLongString += " ";
    					latLongString += ad.getCountryName() + " " + ad.getLocality();
    				}
    			}
    			
    			locationManager.removeUpdates(this);
    			//if (latLongString.length() < 3) return;
    			System.out.println("Location:" + latLongString);
			}

			@Override
			public void onProviderDisabled(String provider) {
				System.out.println("Location:NULL - " + provider);
				Location location = locationManager.getLastKnownLocation(provider);
				if (location != null) {
					double lat = location.getLatitude();
					double lng = location.getLongitude();
					System.out.println("&lng=" + lng + "&lat=" + lat);
					locationManager.removeUpdates(this);
				}
			}

			@Override
			public void onProviderEnabled(String provider) {
				System.out.println("Location:Enabled - " + provider);
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				System.out.println("Location:Changed - " + provider);
			}
    		
    		
    	};
    	
    	try {
        	locationManager.requestLocationUpdates(provider, 10000, 0, locationListener);
        	//Toast.makeText(mContext, "开始定位...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
			e.printStackTrace();
		}
    	
	}
	

	public static void appEnter(final Context c, final String uuid) {
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				String url = HOST_URL + "enter.php";
				Map<String, String> params = new HashMap<String, String>();
				Map<String, String> pinfo = getPhoneInfo(c);
				ArrayList<String> disput = new ArrayList<String>();
				disput.add("uuid");
				
				params.put("uuid", uuid);
				params.put("device_info", Common.getJSONFromMap(pinfo, disput));
				params.put("app_name", c.getPackageName());
				params.put("app_ver", "" + AppUtils.getVersionCode(c));
				String result = HttpUtils.postHttpRequest(url, params);
				System.out.println("APP_ENTER:" + result);
			}
			
		}).start();
		
		
	}
	
	public static void sendScore(final Context c, final UserEntity ue, final int gameId, final int levelId, final long score) {
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				String url = HOST_URL + "score.php";
				Map<String, String> params = new HashMap<String, String>();
				
				params.put("game_id", String.valueOf(gameId));
				params.put("level_id", String.valueOf(levelId));
				params.put("user_id", (ue == null)?String.valueOf(0):String.valueOf(ue.getUserId()));
				params.put("app_name", c.getPackageName());
				params.put("app_ver", "" + AppUtils.getVersionCode(c));
				params.put("score", score + "");
				String result = HttpUtils.postHttpRequest(url, params);
				
				System.out.println("SEND_SCORE:" + result);
			}
			
		}).start();
		
		
	}
	
}
