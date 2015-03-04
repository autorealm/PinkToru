package com.sunteorum.pinktoru.adapter;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sunteorum.pinktoru.PinkToru;
import com.sunteorum.pinktoru.helper.LoadImageThread;
import com.sunteorum.pinktoru.util.ImageUtils;
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
	private ArrayList<String> mImageList;
	protected Map<String, SoftReference<Bitmap>> imgcache = new HashMap<String, SoftReference<Bitmap>>();
	
	public FlowImageAdapter(PinkToru app, ArrayList<String> imgList) {
		this.app = app;
		this.mContext = app.getApplicationContext();
		this.mImageList = imgList;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return mImageList.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressWarnings("deprecation")
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView iv = null;
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		if (null == convertView) {
			iv = new ImageView(mContext);
			iv.setLayoutParams(new GalleryFlow.LayoutParams((int) Math.round(screenWidth * 0.50),
					(int) Math.round(screenHeight * 0.60)));
			iv.setAdjustViewBounds(true);
			
			convertView = iv;
		} else {
			iv = (ImageView) convertView;
			
		}
		
		final ImageView view = iv;
		
		String url = mImageList.get(position);
		
		new LoadImageThread(app, url, imgcache, new LoadImageThread.Callback() {
			
			@Override
			public void onGetImage(Bitmap bmp, String url) {
				final Bitmap current = ImageUtils.createReflectedImage(mContext, bmp);
				if (!bmp.isRecycled()) bmp.recycle();
				if (current == null) Log.i("FlowImageAdapter_getView_" + position, "Bitmap == NULL");
				view.setImageBitmap(current);
				view.postInvalidate();
				view.destroyDrawingCache();
				
			}
		}).start();
		
		
		return convertView;
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
