package com.sunteorum.pinktoru.helper;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

import com.sunteorum.pinktoru.util.ImageUtils;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class ImageLoader {

	private LruCache<String, Bitmap> memCache;
	private ExecutorService mPool = null;
	
	private File save_dir;
	
	public interface onLoadedListener {
        void onLoaded(String url, Bitmap bitmap);
	}
	
	public ImageLoader(Context context, File saveDir) {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int mCacheSize = maxMemory / 6;
		
		memCache = new LruCache<String, Bitmap>(mCacheSize) {

			@Override
			protected int sizeOf(String key, Bitmap value) {
				if (value == null)
					return super.sizeOf(key, value);
				else
					return value.getRowBytes() * value.getHeight();
			}
			
		};
		
		save_dir = saveDir;
		if (save_dir == null || !save_dir.isDirectory()) save_dir = context.getCacheDir();
	}

	public ExecutorService getThreadPool() {
		if (mPool == null) {
			synchronized (ExecutorService.class) {
				mPool = Executors.newFixedThreadPool(2);
			}
		}
		
		return mPool;
	}
	
	
	public synchronized void cancelTask() {
		if (mPool != null) {
			mPool.shutdown();
			
		}
	}
	
	@SuppressLint("HandlerLeak")
	public void download(final String url, final onLoadedListener listener) {
		Bitmap bitmap = getCacheBitmap(url);
		if (bitmap != null) {
			listener.onLoaded(url, bitmap);
			return;
		}
		
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				listener.onLoaded(url, (Bitmap) msg.obj);
			}
			
		};
		
		getThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				Bitmap bitmap = getBitmapFormUrl(url);
				
				Message msg = handler.obtainMessage();
				msg.obj = bitmap;
				handler.sendMessage(msg);
				
				if (bitmap == null) return;
				
				File f = new File(save_dir, getSaveFileName(url));
				CompressFormat cf = null;
				if (url.endsWith(".png")) cf = CompressFormat.PNG;
				else cf = CompressFormat.JPEG;
				ImageUtils.saveBitmap(bitmap, f, true, cf);
				
				putBitmapToCache(url, bitmap);
			}
			
		});
		
	}
	
	private Bitmap getCacheBitmap(String url) {
		Bitmap bitmap = null;
		synchronized (memCache) {
			bitmap = memCache.get(url);
		}
		if (bitmap != null) {
			return bitmap;
		} else {
			File file = null;
			String filename = getSaveFileName(url);
			
			if (url.startsWith("file://")) file = new File(Uri.parse(url).getPath());
			else file = new File(save_dir, filename);
			if (file.exists()) {
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inPreferredConfig = Bitmap.Config.RGB_565;
				bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opt);
				if (bitmap != null) {
					putBitmapToCache(url, bitmap);
					return bitmap;
				}
			}
		}
		
		return null;
	}
	
	private void putBitmapToCache(String key, Bitmap bitmap) {
		synchronized (memCache) {
			if (memCache.get(key) == null && bitmap != null) {
			    memCache.put(key, bitmap);
			}
		}
		
	}
	
	private String getSaveFileName(String url) {
		String name = String.valueOf(url.hashCode());
		
		
		return name;
	}
	
	public static Bitmap getBitmapFormUrl(String url) {
		Bitmap bitmap = null;
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setConnectTimeout(6 * 1000);
			conn.setReadTimeout(6 * 1000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			
			bitmap = BitmapFactory.decodeStream(conn.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    if (conn != null) {
		    	conn.disconnect();
		    }
		}
		
		return bitmap;
	}
	
}
