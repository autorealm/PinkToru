package com.sunteorum.pinktoru.adapter;

import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class SamplerImageAdapter extends BaseAdapter {
	Context context;
	Map<String, Bitmap> imap;
	
	public SamplerImageAdapter(Context c, Map<String, Bitmap> al) {
		this.context = c;
		this.imap = al;
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imap.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ImageView iv = null;
		if (arg1 == null) {
			iv = new ImageView(context);
			iv.setAdjustViewBounds(true);
			iv.setLayoutParams(new LayoutParams(40, 40));
			iv.setImageBitmap(imap.get(arg0));
		}
		
		return iv;
	}
	
}
