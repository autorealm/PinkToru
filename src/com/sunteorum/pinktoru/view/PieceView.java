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
import android.view.SurfaceHolder;
import android.widget.ImageView;

public class PieceView extends ImageView {
	protected SurfaceHolder holder;
	protected Piece piece;
	
	private Bitmap mBitmap;
	
	private Point minp; //保存碎片中心位置
	private Point location; //保存碎片当前位置
	//是否拼合
	private boolean hasTop = false;
	private boolean hasRight = false;
	private boolean hasFeet = false;
	private boolean hasLeft = false;
	
	private boolean traverse = false;
	
	
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
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		
		this.mBitmap = bm;
	}

	public class GameThread extends Thread {

		@Override
		public void run() {
			super.run();
			Canvas canvas = holder.lockCanvas(null);
			Paint paint = new Paint();
			paint.setColor(Color.BLUE);
			
			canvas.drawBitmap(piece.getBmpEdge(), 0, 0, paint);
			canvas.drawBitmap(piece.getBmpPiece(), 0, 0, paint);
			//canvas.drawRect(new RectF(40,60,80,80), paint);
			
			holder.unlockCanvasAndPost(canvas);
		}
		
	}

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
		
		this.setImageDrawable(sld);
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
