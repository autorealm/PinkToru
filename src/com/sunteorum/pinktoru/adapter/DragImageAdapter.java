package com.sunteorum.pinktoru.adapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import com.sunteorum.pinktoru.R;
import com.sunteorum.pinktoru.entity.Piece;
import com.sunteorum.pinktoru.util.ViewUtils;
import com.sunteorum.pinktoru.view.GalleryDrag;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class DragImageAdapter extends BaseAdapter {
	private Context mContext;
	
	private Vector<Piece> mImageList;
	
	SparseBooleanArray selsta = new SparseBooleanArray();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DragImageAdapter(Context c, Vector<Piece> imgList){
		this.mContext = c;
		this.mImageList = imgList;
		initSelect(true);
		
		//随机分布Piece
		Collections.sort(imgList, new Comparator() {
		      @Override
		      public int compare(Object o1, Object o2) {
		    	  int rnd = ((int)(Math.random()*10)%2 ==0) ? 1 : -1;
		    	  return rnd * ((int)(Math.random()*mImageList.size()));
		      }
		    }
		);
		
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView iv = null;
		if (convertView == null) {
			iv = new ImageView(mContext);
			iv.setAdjustViewBounds(true);
			int wh = parent.getHeight() - ViewUtils.dip2px(mContext, 8);
			iv.setLayoutParams(new GalleryDrag.LayoutParams(wh, wh));
			iv.setImageBitmap(mImageList.get(position).getBmpPiece());
			iv.setBackgroundResource(R.drawable.itemshape_1);
			int sc = ViewUtils.dip2px(mContext, 2);
			iv.setPadding(sc, sc, sc, sc);
			convertView = iv;
		} else {
			iv = (ImageView) convertView;
		}
		
		convertView.setTag(mImageList.get(position));
		
		if (selsta.get(position))
			iv.setVisibility(0);
		else
			iv.setVisibility(8);
		
		return convertView;
	}
	
	public HashMap<Integer, Bitmap> getDataCache() {
		return null;
	}
	
	protected void initSelect(boolean selected) {
		selsta.clear();
		for (int i = 0; i < mImageList.size(); i++) {
			selsta.put(i, selected);
			
		}
	}

	public int getLostedCount() {
		int count = 0;
		for (int i = 0; i < mImageList.size(); i++) {
			if (selsta.get(i)) {
				count++;
			}
			
		}
		
		return count;
	}
	
	public void setSelected(int index, boolean selected) {
		selsta.put(index, selected);
		this.notifyDataSetChanged();
		
	}
	
	
}
