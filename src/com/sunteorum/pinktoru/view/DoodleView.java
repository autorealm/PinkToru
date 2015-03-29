	package com.sunteorum.pinktoru.view;

	import android.content.Context;
	import android.graphics.Canvas;
	import android.graphics.Color;
	import android.graphics.Paint;
	import android.graphics.Path;
	import android.graphics.Rect;
	import android.graphics.Paint.Style;
	import android.view.MotionEvent;
	import android.view.SurfaceHolder;
	import android.view.SurfaceView;

	public class DoodleView extends SurfaceView {
	private Context mContext;
	private float mX;
	private float mY;

	private SurfaceHolder holder;
	private Canvas canvas;
	private float mCurveEndX;
	private float mCurveEndY;

	private final Paint mGesturePaint = new Paint();
	private final Path mPath = new Path();
	private final Rect mInvalidRect = new Rect();

	private boolean isDrawing;

	public DoodleView(Context context) {
		super(context);
		mContext = context;
		holder = this.getHolder();
		mGesturePaint.setAntiAlias(true);
		mGesturePaint.setStyle(Style.STROKE);
		mGesturePaint.setStrokeWidth(5);
		mGesturePaint.setColor(Color.WHITE);
		
	}

	public void drawCanvas() {
		try {
			canvas = holder.lockCanvas();
			if (canvas != null) {
				canvas.drawColor(Color.BLACK);
				canvas.drawPath(mPath, mGesturePaint);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (canvas != null)
				holder.unlockCanvasAndPost(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchDown(event);
				invalidate();
				return true;

			case MotionEvent.ACTION_MOVE:
				if (isDrawing) {
					Rect rect = touchMove(event);
					if (rect != null) {
						invalidate(rect);
					}
					return true;
				}           
				break;
			case MotionEvent.ACTION_UP:
				if (isDrawing) {
					touchUp(event);
					invalidate();
					return true;
				}
				break;        
		}
		
		return super.onTouchEvent(event);
	}

	private void touchDown(MotionEvent event) {
		isDrawing = true;
		mPath.reset();
		float x = event.getX();
		float y = event.getY();
		
		mX = x;
		mY = y;
		
		mPath.moveTo(x, y);
		
		mInvalidRect.set((int) x, (int) y, (int) x , (int) y);
		mCurveEndX = x;
		mCurveEndY = y;
	}

	private Rect touchMove(MotionEvent event) {
		Rect areaToRefresh = null;

		final float x = event.getX();
		final float y = event.getY();

		final float previousX = mX;
		final float previousY = mY;

		final float dx = Math.abs(x - previousX);
		final float dy = Math.abs(y - previousY);
		
		if (dx >= 3 || dy >= 3) {
			areaToRefresh = mInvalidRect;
			areaToRefresh.set((int) mCurveEndX , (int) mCurveEndY ,
					(int) mCurveEndX, (int) mCurveEndY);
			
		  //���ñ��������ߵĲ�����Ϊ�����յ��һ��
			float cX = mCurveEndX = (x + previousX) / 2;
			float cY = mCurveEndY = (y + previousY) / 2;

			//ʵ�ֻ��Ʊ�����ƽ�����ߣ�previousX, previousYΪ�����㣬cX, cYΪ�յ�
			mPath.quadTo(previousX, previousY, cX, cY);
			//mPath.lineTo(x, y);

			// union with the control point of the new curve
			/*areaToRefresh����������border(��͸�����������border)��
			 * borderֵ���������ƻ��ʴ�ϸֵ����
			 */
			areaToRefresh.union((int) previousX, (int) previousY,
					(int) previousX, (int) previousY);
		   /* areaToRefresh.union((int) x, (int) y,
					(int) x, (int) y);*/

			
			// union with the end point of the new curve
			areaToRefresh.union((int) cX, (int) cY ,
					(int) cX, (int) cY);

			//�ڶ���ִ��ʱ����һ�ν������õ�����ֵ����Ϊ�ڶ��ε��õĳ�ʼ����ֵ
			mX = x;
			mY = y;
			drawCanvas();
		}
		return areaToRefresh;
	}

	private void touchUp(MotionEvent event) {
		isDrawing = false;
	}

	}
