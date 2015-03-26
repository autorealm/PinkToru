package com.sunteorum.pinktoru.helper;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.widget.ImageView;

import com.sunteorum.pinktoru.PinkToru;
import com.sunteorum.pinktoru.util.ImageUtils;

public class LoadImageThread  extends Thread {
	private ImageView imgview = null;
	private Callback callback = null;
	private String url;
	private int def_res_id;
	private Map<String, SoftReference<Bitmap>> imgcache;
	PinkToru app;
	Handler mHandler = new Handler();
	
	public static interface Callback {
	    void onGetImage(Bitmap bmp, String url);
	}
	
	public LoadImageThread(PinkToru app, String url, ImageView iv, int resId, Map<String, SoftReference<Bitmap>> imgCache) {
		this.imgview = iv;
		this.url = url;
		this.def_res_id = resId;
		this.imgcache = imgCache;
		this.app = app;
	}
	
	public LoadImageThread(PinkToru app, String url, Map<String, SoftReference<Bitmap>> imgCache, Callback callBack) {
		this.url = url;
		this.imgcache = imgCache;
		this.callback = callBack;
		this.app = app;
	}
	
	private void putImage(final Bitmap bmp) {
		mHandler.removeCallbacks(this);
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				if (imgview != null) {
					if (bmp != null) imgview.setImageBitmap(bmp);
					else imgview.setImageResource(def_res_id);
					
					imgview.postInvalidate();
					imgview.destroyDrawingCache();
				} else if (callback != null) {
					
					callback.onGetImage(bmp, url);
				}
    			
			}
			
		});
		
		
	}

	protected Bitmap getBitmapFromCache(String key, Map<String, SoftReference<Bitmap>> cache) {
		if (cache == null || cache.isEmpty()) return null;
		synchronized (cache) {
	    	if (cache.containsKey(key)) {
	            SoftReference<Bitmap> bitmapReference = cache.get(key);
	            if (bitmapReference != null) {
	            	Bitmap bmp = bitmapReference.get();
	                if (bmp != null) {
	                    return bmp;
	                } else {
	                	cache.remove(key);
	                }
	            }
	        }
    	}
    	
    	return null;
    }
    
	protected void putBitmapToCache(String key, Bitmap bitmap, Map<String, SoftReference<Bitmap>> cache) {
		if (cache == null || bitmap == null) return;
		synchronized (cache) {
			cache.put(key, new SoftReference<Bitmap>(bitmap));
			
		}
		
	}
	
    public static void setImageFromUrl(final ImageView iv, final String url, final Map<String, SoftReference<Bitmap>> cache) {
    	new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					InputStream is = new URL(url).openStream();
					final Bitmap bmp = BitmapFactory.decodeStream(is);
					if (cache != null) cache.put(url, new SoftReference<Bitmap>(bmp));
					iv.post(new Runnable() {

						@Override
						public void run() {
							iv.setImageBitmap(bmp);
							
						}
						
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}).start();
    	
    }

	@Override
	public void run() {
		Bitmap bmp = null;
		File f = null;
    	try {
    		bmp = getBitmapFromCache(url, imgcache);
    		if (bmp != null) {
	    		putImage(bmp);
	    		return;
		    
    		}
    		
    		String name = app.getCacheImageName(url);
    		f = new File(app.getAppCacheDir(), name);
    		
    		if (f.exists()) {
    			BitmapFactory.Options opts = new BitmapFactory.Options();
    			//opts.inSampleSize = 2;
    			//opts.inJustDecodeBounds = false;
    			opts.inPreferredConfig = Bitmap.Config.RGB_565;
    			
    			bmp = BitmapFactory.decodeFile(f.getAbsolutePath(), opts);
    		} else {
    			InputStream is = new URL(url).openStream();
    			bmp = BitmapFactory.decodeStream(is);
    			is.close();
    		}
    		
    		if (bmp != null) {
    			CompressFormat cf = null;
    			if (url.endsWith(".png")) cf = CompressFormat.PNG;
    			else cf = CompressFormat.JPEG;
    			ImageUtils.saveBitmap(bmp, f, false, cf);
    			//System.out.println("saveImageToSDCard:" + sv);
    			
    			putBitmapToCache(url, bmp, imgcache);
        		
        	}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
		
    	putImage(bmp);
	}
	
}
