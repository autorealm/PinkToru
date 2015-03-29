package com.sunteorum.pinktoru.adapter;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sunteorum.pinktoru.PinkToru;
import com.sunteorum.pinktoru.helper.LoadImageThread;
import com.sunteorum.pinktoru.util.ImageUtils;
import com.sunteorum.pinktoru.util.ViewUtils;
import com.sunteorum.pinktoru.view.GalleryFlow;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class FlowImageAdapter extends BaseAdapter {
	private Context mContext;
	PinkToru app = null;
	private int width = 0, height = 0;
	private int screenWidth, screenHeight;
	private ArrayList<String> mImageList;
	private int backRes = 0;
	protected Map<String, SoftReference<Bitmap>> imgcache = new HashMap<String, SoftReference<Bitmap>>();
	
	public FlowImageAdapter(PinkToru app, ArrayList<String> imgList) {
		this.app = app;
		this.mContext = app.getApplicationContext();
		this.mImageList = imgList;
		
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mImageList.size();
	}

	@Override
	public Bitmap getItem(int position) {
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ImageView iv = null;
		
		if (null == convertView) {
			iv = new ImageView(mContext);
			if (width > 0 && height > 0 && width <= screenWidth && height <= screenHeight) {
				iv.setLayoutParams(new GalleryFlow.LayoutParams(width, height));
			} else {
				iv.setLayoutParams(new GalleryFlow.LayoutParams((int) Math.round(screenWidth * 0.74f),
					(int) Math.round(screenHeight * 0.68f)));
			}
			
			int sc = ViewUtils.dip2px(mContext, 12);
			iv.setAdjustViewBounds(true);
			iv.setPadding(sc, sc, sc, sc);
			iv.setBackgroundResource(backRes);
			convertView = iv;
		} else {
			iv = (ImageView) convertView;
			
		}
		
		final ImageView view = iv;
		
		String url = mImageList.get(position);
		Bitmap bmp = getBitmapFromCache(url);
		if (bmp != null) {
			iv.setImageBitmap(bmp);
			iv.postInvalidate();
		} else {
			LoadImageThread loadThread = new LoadImageThread(app, url, null, new LoadImageThread.Callback() {

				@Override
				public void onGetImage(Bitmap bmp, String url) {
					if (bmp == null) {
						System.out.println("X : " + url);
						return;
					}
					
					Bitmap current = null;
					if (bmp.getWidth() > bmp.getHeight() * 1.2f) {
						current = ImageUtils.createReflectedImage(bmp);
						if (!bmp.isRecycled()) bmp.recycle();
						
					} else current = bmp;
					
					if (current == null) Log.i("FlowImageAdapter_getView_" + position, "Bitmap == NULL");
					if (imgcache != null)
						imgcache.put(url, new SoftReference<Bitmap>(current));
					view.setImageBitmap(current);
					view.postInvalidate();
					
				}
			});
			loadThread.setMaxImageSize(Math.max(screenHeight, screenWidth));
			loadThread.start();
			
		}
		
		
		return convertView;
	}
	
	private synchronized Bitmap getBitmapFromCache(String key) {
		if (imgcache == null) return null;
		final SoftReference<Bitmap> bitmapReference = imgcache.get(key);
		if (bitmapReference != null) {
			final Bitmap bitmap = bitmapReference.get();
			if (bitmap != null) {
				return bitmap;
			}
		}
		
		return null;
	}
	
	public void setBackRes(int backRes) {
		this.backRes = backRes;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public synchronized void setWidthAndHeight(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public Map<String, SoftReference<Bitmap>> getDataCache() {
		return imgcache;
	}
	
	public void clearCacheBitmap() {
		if (imgcache == null) return;
		try {
    	Object[] keys = imgcache.keySet().toArray();
    	for (int i = 0; i < keys.length; i++) {
    		if (imgcache.containsKey(keys[i]) && imgcache.get(keys[i]).get() != null) {
    			if (!imgcache.get(keys[i]).get().isRecycled()) imgcache.get(keys[i]).get().recycle();
    		}
    	}
		} catch (Exception e) {e.printStackTrace();}
		
    	imgcache.clear();
    	
    }
	
	public class ViewHolder {
		ImageView imgview_holder;
	}
	
}
