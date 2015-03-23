package com.sunteorum.pinktoru.util;

import java.io.File;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Common {

	public static boolean hasSDCard() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		
		return true;
	}
	
	public static void showToast(Context context, String text) {
		android.widget.Toast.makeText(context, text, android.widget.Toast.LENGTH_SHORT).show();
	}

    public static void showTip(Context context, String title, String message) {
    	new AlertDialog.Builder(context)
		.setTitle(title)
		.setMessage(message)
		.setIcon(android.R.drawable.ic_menu_info_details)
		.setPositiveButton("确定",null)
		.create().show();
    }
    

	public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            Log.i("setListViewHeightBasedOnChildren", "NULL");
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        if (params.height < (height - 12) || params.height > (height + 12)) {
        	params.height = height;
        	listView.setLayoutParams(params);
        }
        
        
	}
	
	/**
	 * 清空内存中缓存的图片数据
	 * @param ImgCache
	 */
	public static void  clearCacheBitmap(Map<String, SoftReference<Bitmap>> ImgCache) {
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
	
	public static class TouchDragListener implements OnTouchListener {

		private PointF startPoint = new PointF();

    	private int mode = 0; // 用于标记模式 
    	private static final int DRAG = 1; // 拖动 
    	private static final int ZOOM = 2; // 放大 
    	private boolean isFrame = true;
    	
    	public TouchDragListener(boolean isframelayout) {
    		this.isFrame = isframelayout;
    	}
    	
    	@SuppressLint("ClickableViewAccessibility")
		@Override
    	public boolean onTouch(View v, MotionEvent event) {
    		
	    	switch (event.getAction() & MotionEvent.ACTION_MASK) {
	    	case MotionEvent.ACTION_DOWN:
	            startPoint.set(event.getX(), event.getY());
	            mode = DRAG;
	            v.bringToFront();
	            
	    	break;
	    	case MotionEvent.ACTION_MOVE: // 移动事件 
		    	if (mode == DRAG) {
			    	int dx = (int) (event.getX() - startPoint.x);
			    	int dy = (int) (event.getY() - startPoint.y);
			    	if (isFrame) {
				    	int left = v.getLeft() + dx;
		                int top = v.getTop() + dy;
		                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(v.getWidth(), v.getHeight());
		                lp.leftMargin = left;
		                lp.topMargin = top;
		                lp.gravity = Gravity.TOP|Gravity.LEFT;
		                v.setLayoutParams(lp);
			    	} else {
			    		int l = v.getLeft() + dx;
						int t = v.getTop() + dy;
						int r = l + v.getWidth();
						int b = t + v.getHeight();
						
						v.layout(l, t, r, b);
						v.postInvalidate();
			    	}
		    	
		    	} else if (mode == ZOOM) { // 放大事件 
		    		
		    	}
		    	
	    	break;
	    	case MotionEvent.ACTION_UP:
	    		mode = 0;
	    	break;
	    	case MotionEvent.ACTION_POINTER_UP:
	    		mode = 0;
	    	break;
	    	case MotionEvent.ACTION_POINTER_DOWN:
		    	mode = ZOOM;
		    	
	    	break;
	    	
	    	}
	    	
	    	return true;
    	}

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
