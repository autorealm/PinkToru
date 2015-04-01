package com.sunteorum.pinktoru.view;

import com.sunteorum.pinktoru.entity.Piece;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

public class PieceView extends View {
	protected SurfaceHolder holder;
	protected Piece piece;
	
	private Bitmap mBitmap; //原始碎片图片
	
	private Point minp; //保存碎片中心位置
	private Point location; //保存碎片当前位置
	
	//是否拼合
	private boolean hasTop = false;
	private boolean hasRight = false;
	private boolean hasFeet = false;
	private boolean hasLeft = false;
	
	private boolean traverse = false;
	
	private Paint paint;
	
	
	public PieceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public PieceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public PieceView(Context context, Piece piece) {
		super(context);
		this.piece = piece;
		this.minp = piece.getMinp();
		
		this.mBitmap = piece.getBmpPiece();
		
		paint = new Paint();
		//paint.setColor(Color.TRANSPARENT);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth(1f);
		paint.setAntiAlias(true);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		
		//canvas.drawARGB(120, 120, 60, 30);
		canvas.drawBitmap(mBitmap, 0, 0, paint);
		
		canvas.save();
	}

	public void setImageBitmap(Bitmap bm) {
		this.measure(bm.getWidth(), bm.getHeight());
		this.mBitmap = bm;
		this.postInvalidate();
	}
	
	public Bitmap getPieceBitmap() {
		return mBitmap;
	}
	
	public void setPieceAlpha(int alpha) {
		paint.setAlpha(alpha);
		this.postInvalidate();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	public class GameThread extends Thread {

		@Override
		public void run() {
			super.run();
			Canvas canvas = holder.lockCanvas(null);
			paint.setColor(Color.BLUE);
			
			canvas.drawBitmap(piece.getBmpPiece(), 0, 0, paint);
			//canvas.drawRect(new RectF(40,60,80,80), paint);
			
			holder.unlockCanvasAndPost(canvas);
		}
		
	}

	@SuppressWarnings("deprecation")
	public void setStateDrawable(Bitmap b, Paint p) {
		
		if (p == null) {
			p = new Paint();
			p.setColor(Color.GREEN);
			p.setStyle(Paint.Style.STROKE);
			p.setStrokeWidth(16f);
			p.setAntiAlias(true);
		}

		Bitmap bitmap = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(b.extractAlpha(), 0, 0, p);
		
		StateListDrawable sld = new StateListDrawable();
		sld.addState(new int[]{android.R.attr.state_pressed}, new BitmapDrawable(this.getResources(), bitmap));
		sld.addState(new int[]{-android.R.attr.state_pressed}, new BitmapDrawable(this.getResources(), b));
		
		this.setBackgroundDrawable(sld);
    }

	
	public Piece getPiece() {
		return piece;
	}

	public Point getMinp() {
		return minp;
	}

	public void setMinp(Point minp) {
		this.minp = minp;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public boolean isHasTop() {
		return hasTop;
	}

	public void setHasTop(boolean hasTop) {
		this.hasTop = hasTop;
	}

	public boolean isHasRight() {
		return hasRight;
	}

	public void setHasRight(boolean hasRight) {
		this.hasRight = hasRight;
	}

	public boolean isHasFeet() {
		return hasFeet;
	}

	public void setHasFeet(boolean hasFeet) {
		this.hasFeet = hasFeet;
	}

	public boolean isHasLeft() {
		return hasLeft;
	}

	public void setHasLeft(boolean hasLeft) {
		this.hasLeft = hasLeft;
	}

	public boolean isTraverse() {
		return traverse;
	}

	public void setTraverse(boolean traverse) {
		this.traverse = traverse;
	}

	
}
