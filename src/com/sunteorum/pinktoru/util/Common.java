package com.sunteorum.pinktoru.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class Common {

	public static boolean hasSDCard() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isEmpty(String text) {
		return android.text.TextUtils.isEmpty(text);
		
	}
	
	public static boolean isNumber(String text) {
		return android.text.TextUtils.isDigitsOnly(text);
		
	}
	
	public static void showToast(Context context, String text) {
		android.widget.Toast.makeText(context, text, android.widget.Toast.LENGTH_SHORT).show();
		
	}

    public static void showTip(Context context, String title, String message) {
    	new AlertDialog.Builder(context)
			.setTitle(title)
			.setMessage(message)
			.setIcon(android.R.drawable.ic_menu_info_details)
			.setPositiveButton("确定", null)
			.create().show();
    	
    }
	
	/**
	 * 清空内存中缓存的图片数据
	 * @param ImgCache
	 */
	public static void  clearCacheBitmap(Map<String, SoftReference<Bitmap>> ImgCache) {
		if (ImgCache == null || ImgCache.isEmpty()) return;
    	Object[] keys = ImgCache.keySet().toArray();
    	for (int i = 0; i < keys.length; i++) {
    		if (ImgCache.containsKey(keys[i]) && ImgCache.get(keys[i]).get() != null) {
    			if (!ImgCache.get(keys[i]).get().isRecycled()) ImgCache.get(keys[i]).get().recycle();
    		}
    	}
    	
    	ImgCache.clear();
    	
    }
    
	public static String formatTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		
		return sdf.format(new Date(time * 1000L));
	}
	
	public static String formatTime(long time, String template) {
		SimpleDateFormat sdf = new SimpleDateFormat(template, Locale.getDefault());
		
		return sdf.format(new Date(time * 1000L));
	}
	
	
	public static String getUriFilePath(Activity activity, Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		android.database.Cursor actualimagecursor = activity.managedQuery(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String file_path = actualimagecursor.getString(actual_image_column_index);
		//File file = new File(file_path);
		//actualimagecursor.close();
		
		return file_path;
	}

	public static Intent cropImageUri(Uri uri, int aspectX, int aspectY, int outputX, int outputY){
		 Intent intent = new Intent("com.android.camera.action.CROP");
		 intent.setDataAndType(uri, "image/*");
		 intent.putExtra("crop", "true");
		 intent.putExtra("aspectX", aspectX);
		 intent.putExtra("aspectY", aspectY);
		 intent.putExtra("outputX", outputX);
		 intent.putExtra("outputY", outputY);
		 intent.putExtra("scale", true);
		 intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		 intent.putExtra("return-data", false);
		 intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		 intent.putExtra("noFaceDetection", true);
		 
		 return intent;
			
	}
	

	public static void setupApp(Context context, String apk, boolean inst) {
		if (apk == null) return;
		if (inst) {
			String fileName = apk;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			context.startActivity(intent);
			
		} else {
			Uri packageURI = Uri.parse("package:" + apk);   
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
			uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			context.startActivity(uninstallIntent);
			
		}
	}

	/**
	 * 取得文件的MD5码
	 * @param file 文件
	 * @return MD5码
	 */
	public static String getFileMD5(File file) {
		if (file == null || !file.isFile()) return null;
		
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
		
	}

	/**
	 * 取得字符串的MD5码
	 * @param str 字符串
	 * @return
	 */
    public static String getStringMD5(String str) {
    	if (str == null) return null;
		MessageDigest md5 = null;
		try {
		    md5 = MessageDigest.getInstance("MD5");
		} catch(Exception e) {
		    e.printStackTrace();
		    return "";
		}
		
		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		
		for(int i = 0; i < charArray.length; i++) {
		    byteArray[i] = (byte)charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		
		StringBuffer hexValue = new StringBuffer();
		for( int i = 0; i < md5Bytes.length; i++) {
		    int val = ((int)md5Bytes[i])&0xff;
		    if(val < 16) {
		        hexValue.append("0");
		    }
		    hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
    }

    /**
     * 以编码GB2312保存文本文件
     * @param context
     * @param filepath
     * @param text
     * @return
     */
    public static boolean saveTextFile(Context context, String filepath, String text) {
		FileOutputStream outputStream;
		
		try {
			outputStream = context.openFileOutput(filepath, Context.MODE_PRIVATE);
			outputStream.write(text.getBytes("gb2312"));
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

    /**
	 * 获取目录全部JPEG图片列表
	 * @return 文件夹中的图片列表
	 */
	public static ArrayList<Map<String, ?>> getImageFileList(File dir) {
		ArrayList<Map<String, ?>> lms = new ArrayList<Map<String, ?>>();
		if (!dir.exists()) return lms;
		for (File f:dir.listFiles()) {
			String name = f.getName().toLowerCase(Locale.getDefault());
			if (!name.endsWith(".jpg") || !name.endsWith(".jpeg")) continue;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", String.valueOf(f.getAbsolutePath().hashCode()));
			map.put("path", f.getParent());
			map.put("name", f.getName());
			map.put("uri", Uri.fromFile(f));
			lms.add(map);
		}
		
		return lms;
	}

	public static String getJSONFromMap(Map<String, String> map, ArrayList<String> disput) {
		JSONObject json = new JSONObject();
		try {
			for (String k:map.keySet()) {
				if (disput != null && disput.contains(k)) continue;
	        	json.put(k, map.get(k));
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return json.toString();
	}
	
	
}
