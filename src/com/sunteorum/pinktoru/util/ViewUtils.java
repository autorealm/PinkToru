package com.sunteorum.pinktoru.util;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ImageView.ScaleType;

@SuppressLint("ClickableViewAccessibility")
public class ViewUtils {

	public ViewUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static interface onTouchCallBack {
	    void onTouch(View v, MotionEvent event);
	}

	public static int px2dip(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(px / scale + 0.5f);
	}
	
	public static int dip2px(Context context, float dip) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dip * scale + 0.5f);
	}
	
	public static class TouchGrowListener implements OnTouchListener {
		public final int GROW_NONE = (1 << 0);
		public final int GROW_LEFT = (1 << 1);
		public final int GROW_RIGHT = (1 << 2);
		public final int GROW_TOP = (1 << 3);
		public final int GROW_BOTTOM = (1 << 4);
		public final int GROW_MOVE = (1 << 5);
		
		private PointF sPoint = new PointF();
		private PointF ePoint = new PointF();
		private float sDis = 0;
    	private PointF mPoint;
		private onTouchCallBack callback;
		int edge = GROW_NONE;
		int mode = 0;
		long lctime = 0;
		
		public TouchGrowListener(onTouchCallBack callback) {
			this.callback = callback;
		}
		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			
	    	switch (arg1.getAction() & MotionEvent.ACTION_MASK) {
	    	case MotionEvent.ACTION_DOWN:
	    		sPoint.set(arg1.getX(), arg1.getY());
	    		ePoint.set(arg1.getX(), arg1.getY());
	    		if (lctime == 0) lctime = System.currentTimeMillis();
				long lt = System.currentTimeMillis() - lctime;
				if (lt > 100 && lt < 500) {
					return false;
				} else {
					
				}
				lctime = System.currentTimeMillis();
				
	    		edge = getHit(getViewRectInScreen(arg0), arg1.getRawX(), arg1.getRawY());
	    		
	    		mode = 0;
	    	break;
	    	case MotionEvent.ACTION_MOVE:
	    		int dx = (int) (arg1.getX() - sPoint.x);
		    	int dy = (int) (arg1.getY() - sPoint.y);
		    	FrameLayout.LayoutParams rlp = (FrameLayout.LayoutParams) arg0.getLayoutParams();
		    	if (mode == 2) return true;
		    	if (mode == 1) {
		    		float eDis = distance(arg1);
		    		if (eDis > 10f) {
		    			int dw = (int)(arg0.getWidth() + eDis - sDis);
		    			rlp.width = checkRect(dw, rlp.width)?dw:rlp.width;
		    			int dh = (int)(arg0.getWidth() + eDis - sDis);
		    			rlp.height = checkRect(dh, rlp.height)?dh:rlp.height;
		    			rlp.leftMargin -= (int) ((eDis - sDis) / 2);
		    			rlp.topMargin -= (int) ((eDis - sDis) / 2);
		    			
		    			arg0.setLayoutParams(rlp);
		    			sDis = eDis;
		    		}
		    	} else if (edge == GROW_NONE) {
					return true;
				} else if (edge == GROW_MOVE) {
					
					
					rlp.leftMargin += (int)dx;
					rlp.topMargin += (int)dy;
					arg0.setLayoutParams(rlp);
					
				} else {
					dx = (int) (arg1.getX() - ePoint.x);
			    	dy = (int) (arg1.getY() - ePoint.y);
					float f = arg0.getWidth() /arg0.getHeight();
					if (((GROW_LEFT | GROW_RIGHT) & edge) == 0) {
						dx = 0;
					}

					if (((GROW_TOP | GROW_BOTTOM) & edge) == 0) {
						dy = 0;
					}
					
					//dx = (((edge & GROW_LEFT) != 0) ? -1 : 1) * dx;
					//dy = (((edge & GROW_TOP) != 0) ? -1 : 1) * dy;
					
					int l = 0, t = 0, r = 0, b = 0;
					l = rlp.leftMargin;
					t = rlp.topMargin;
					r = l + rlp.width;
					b = t + rlp.height;
					
					
					if (((edge & GROW_RIGHT) != 0) && ((edge & GROW_BOTTOM)) != 0) {
						int d = (int) (Math.round(Math.sqrt(dx * dx + dy * dy)));
						float s;
						if (dx > dy) {
							s = (float) (d + rlp.width) / rlp.width;
						} else {
							s = (float) (d + rlp.height) / rlp.height;
						}
						
					} else if ((edge & GROW_RIGHT) != 0) {
						r += dx;
						b += dy;
					} else if ((edge & GROW_BOTTOM) != 0) {
						r += dx;
						b += dy;
					}
					
					if ((dx > 0) && (dy > 0)) {
						rlp.width = checkRect(r - l, rlp.width)?(r - l):rlp.width;
						rlp.height = checkRect(b - t, rlp.height)?(b - t):rlp.height;
					}
					//v.layout(l, t, r, b);
					arg0.setLayoutParams(rlp);
					
				}
				
	    		ePoint.set(arg1.getX(), arg1.getY());
	    		if (callback != null) callback.onTouch(arg0, arg1);
	    	break;
	    	case MotionEvent.ACTION_UP:
	    		mode = 0;
	    		arg0.invalidate();
	    		
	    	break;
	    	case MotionEvent.ACTION_POINTER_UP:
	    		mode = 2;
	    		
	    		
	    	break;
	    	case MotionEvent.ACTION_POINTER_DOWN:
		    	mode = 1;
		    	sDis = distance(arg1);
	    		if (sDis > 10f) mPoint = mid(arg1);
	    	break;
	    	
	    	}
	    	
			return true;
		}

		public int getHit(Rect r, float x, float y) {
			final float hysteresis = 8F;
			int retval = GROW_NONE;
			
			boolean verticalCheck = (y >= r.top - hysteresis)
					&& (y < r.bottom + hysteresis);
			boolean horizCheck = (x >= r.left - hysteresis)
					&& (x < r.right + hysteresis);
			
			if ((Math.abs(r.left - x) < hysteresis) && verticalCheck) {
				retval |= GROW_LEFT;
			}
			if ((Math.abs(r.right - x) < hysteresis) && verticalCheck) {
				retval |= GROW_RIGHT;
			}
			if ((Math.abs(r.top - y) < hysteresis) && horizCheck) {
				retval |= GROW_TOP;
			}
			if ((Math.abs(r.bottom - y) < hysteresis) && horizCheck) {
				retval |= GROW_BOTTOM;
			}
			
			if (retval == GROW_NONE && r.contains((int) x, (int) y)) {
				retval = GROW_MOVE;
			}
			
			return retval;
		}
		
		public void handleMotion(View v, int edge, float dx, float dy) {
			if (edge == GROW_NONE) {
				return;
			} else if (edge == GROW_MOVE) {
				FrameLayout.LayoutParams rlp = (FrameLayout.LayoutParams) v.getLayoutParams();
				
				rlp.leftMargin += (int)dx;
				rlp.topMargin += (int)dy;
				v.setLayoutParams(rlp);
				
			} else {
				FrameLayout.LayoutParams rlp = (FrameLayout.LayoutParams) v.getLayoutParams();
				float f = v.getWidth() /v.getHeight();
				if (((GROW_LEFT | GROW_RIGHT) & edge) == 0) {
					dx = 0;
				}

				if (((GROW_TOP | GROW_BOTTOM) & edge) == 0) {
					dy = 0;
				}
				
				//dx = (((edge & GROW_LEFT) != 0) ? -1 : 1) * dx;
				//dy = (((edge & GROW_TOP) != 0) ? -1 : 1) * dy;
				int l = 0, t = 0, r = 0, b = 0;
				l = rlp.leftMargin;
				t = rlp.topMargin;
				r = l + rlp.width;
				b = t + rlp.height;
				if ((edge & GROW_RIGHT & GROW_BOTTOM) != 0) {
					int d = (int) (Math.round(Math.sqrt(dx * dx + dy * dy)));
					
				} else if ((edge & GROW_RIGHT) != 0) {
					r += dx;
					b += dy;
				} else if ((edge & GROW_BOTTOM) != 0) {
					r += dx;
					b += dy;
				}
				rlp.width = r - l;
				rlp.height = b - t;
				rlp.leftMargin = l;
				rlp.topMargin = t;
				//v.layout(l, t, r, b);
				v.setLayoutParams(rlp);
			}
		}
	}
	
	public static class TouchScaleListener implements OnTouchListener {
		private PointF startPoint = new PointF();
		private Matrix matrix = new Matrix();
		private Matrix currentMaritx = new Matrix();
		
		private int mode = 0; // ���ڱ��ģʽ 
		private static final int DRAG = 1; // �϶� 
		private static final int ZOOM = 2; // �Ŵ� 
		private float startDis = 0;
		private PointF midPoint; // ���ĵ� 
		
		private onTouchCallBack callback;
		
		public TouchScaleListener(onTouchCallBack callback) {
			this.callback = callback;
		}
    	
    	@Override 
    	public boolean onTouch(View v, MotionEvent event) {
    		((ImageView) v).setScaleType(ScaleType.MATRIX);
    		
	    	switch (event.getAction() & MotionEvent.ACTION_MASK) {
	    	case MotionEvent.ACTION_DOWN:
	    		matrix.set(((ImageView) v).getImageMatrix());
	    		currentMaritx.set(matrix);
	            startPoint.set(event.getX(), event.getY());
	            mode = DRAG;
	            v.bringToFront();
	    	break;
	    	case MotionEvent.ACTION_MOVE: // �ƶ��¼� 
		    	if (mode == DRAG) { // ͼƬ�϶��¼� 
			    	float dx = event.getX() - startPoint.x; // x���ƶ����� 
			    	float dy = event.getY() - startPoint.y;
			    	matrix.set(currentMaritx); // �ڵ�ǰ��λ�û������ƶ� 
			    	matrix.postTranslate(dx, dy);
			    	
		    	} else if (mode == ZOOM) { // ͼƬ�Ŵ��¼� 
			    	float endDis = distance(event); // �������� 
			    	if (endDis > 10f) {
				    	float scale = endDis / startDis;// �Ŵ��� 
				    	matrix.set(currentMaritx);
				    	matrix.postScale(scale, scale, midPoint.x, midPoint.y);
			    	}
		    	}
	    	break;
	    	case MotionEvent.ACTION_UP:
	    		mode = 0;
	    	break;
	    	// ����ָ�뿪��Ļ������Ļ���д���(��ָ)
	    	case MotionEvent.ACTION_POINTER_UP:
	    		mode = 0;
	    	break;
	    	// ����Ļ���Ѿ��д��㣨��ָ��,����һ����ָѹ����Ļ 
	    	case MotionEvent.ACTION_POINTER_DOWN:
		    	mode = ZOOM;
		    	startDis = distance(event);
		    	if (startDis > 10f) {
			    	midPoint = mid(event);
			    	currentMaritx.set(((ImageView) v).getImageMatrix());// ��¼��ǰ�����ű��� 
		    	}

	    	break;
	    	
	    	}
	    	
	    	((ImageView) v).setImageMatrix(matrix);
	    	if (callback != null) callback.onTouch(v, event);
	    	
	    	return true;
    	}

    }

	public static class TouchDragListener implements OnTouchListener {

		private PointF startPoint = new PointF();

    	private int mode = 0; // ���ڱ��ģʽ 
    	private static final int DRAG = 1; // �϶� 
    	private static final int ZOOM = 2; // �Ŵ� 
    	
    	private onTouchCallBack callback;
    	boolean skiptrans = true;
    	
    	public TouchDragListener(onTouchCallBack callback) {
    		this.callback = callback;
    	}
    	
    	@SuppressLint("ClickableViewAccessibility")
		@Override
    	public boolean onTouch(View v, MotionEvent event) {
    		
	    	switch (event.getAction() & MotionEvent.ACTION_MASK) {
	    	case MotionEvent.ACTION_DOWN:
	    		if (v instanceof ImageView) {
	    			Bitmap bitmap = v.getDrawingCache();
	    			if (skiptrans && bitmap != null && (bitmap.getWidth() >= event.getX())
	    					&& (bitmap.getHeight() >= event.getY())
		    				&& bitmap.getPixel((int)event.getX(), (int)event.getY()) == 0) {
		    			
		    			return false; 
		    		}
	    		}
	    		
	            startPoint.set(event.getX(), event.getY());
	            mode = DRAG;
	            v.bringToFront();
	            v.requestFocus();
	            
	    	break;
	    	case MotionEvent.ACTION_MOVE: // �ƶ��¼� 
		    	if (mode == DRAG) {
			    	int dx = (int) (event.getX() - startPoint.x + 0.5f);
			    	int dy = (int) (event.getY() - startPoint.y + 0.5f);
			    	
			    	if (v.getParent() instanceof FrameLayout) {
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
		    	
		    	} else if (mode == ZOOM) { // �Ŵ��¼� 
		    		
		    	}
		    	
	    	break;
	    	case MotionEvent.ACTION_UP:
	    		mode = 0;
	    		//v.clearFocus();
	    	break;
	    	case MotionEvent.ACTION_POINTER_UP:
	    		mode = 0;
	    		//v.destroyDrawingCache();
	    	break;
	    	case MotionEvent.ACTION_POINTER_DOWN:
		    	mode = ZOOM;
		    	
	    	break;
	    	
	    	}
	    	
	    	if (callback != null) callback.onTouch(v, event);
	    	
	    	return true;
    	}

    }
	
	public static void forceShowOverflowMenu(Context context) {
		try {
			ViewConfiguration config = ViewConfiguration.get(context);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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

	@SuppressWarnings("deprecation")
	public static Bitmap getViewBitmap(View view, String backcolor) {
		Drawable resd = view.getBackground();
		view.setBackgroundDrawable(null);
		view.setDrawingCacheEnabled(true);
		Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache().getWidth(),
				view.getDrawingCache().getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
        paint.setAlpha(200);
        
		String c = "FFFFFF";
		if (backcolor != null && backcolor.length() > 0) c = backcolor;
		canvas.drawColor(Color.parseColor("#" + c)); //"#" + Integer.toHexString(color)
		canvas.drawBitmap(view.getDrawingCache(), 0, 0, paint);
		//canvas.save(Canvas.ALL_SAVE_FLAG);
		
		view.setDrawingCacheEnabled(false);
		view.setBackgroundDrawable(resd);;
		
		return bmp;
	}
	
	private static float distance(MotionEvent event) {
		// �����ߵľ��� 
		float dx = event.getX(1) - event.getX(0);
		float dy = event.getY(1) - event.getY(0);
		return FloatMath.sqrt(dx * dx + dy * dy);
	}
	
	private static PointF mid(MotionEvent event) {
		//��������֮�����ĵ�ľ��� 
		float midx = event.getX(1) + event.getX(0);
		float midy = event.getY(1) - event.getY(0);
		
		return new PointF(midx / 2, midy / 2);
	}

    /**
     * �ı�View��Z��
     * @param v View (ע�⸸Layout)
     * @param front �Ǽ�ǰ��,�������
     */
    public static void bringTo(View v, boolean front) {
    	ViewGroup flay = (ViewGroup) v.getParent();
    	if (front) {
    		v.bringToFront();
    		flay.invalidate();
    		return;
    	}
    	
    	//FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) flay.getLayoutParams();
    	ArrayList<View> views = new ArrayList<View>();
    	for (int i = 0; i < flay.getChildCount(); i++) {
    		View view = flay.getChildAt(i);
    		if (view.equals(v)) continue;
    		views.add(view);
    		//view.bringToFront();
    	}
    	
    	for (View view:views) {
    		view.bringToFront();
    	}
    	
    	flay.postInvalidate();
    }
    
    public static void layout(View v, int dx, int dy) {
    	int l = v.getLeft() + dx;
		int t = v.getTop() + dy;
		int r = l + v.getWidth();
		int b = t + v.getHeight();
		
		v.layout(l, t, r, b);
		v.postInvalidate();
    }
    
    /**
     * �ƶ�View��ָ������
     * @param v View
     * @param left ����X����
     * @param top ����Y����
     */
    public static void moveView(final View v, int left, int top) {
    	if (v.getParent() instanceof FrameLayout) {
	    	FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v.getLayoutParams();
	        if (left > -1) lp.leftMargin = left;
	        if (top > -1) lp.topMargin = top;
	        lp.gravity = Gravity.TOP|Gravity.LEFT;
	        
	        v.setLayoutParams(lp);
    	} else {
    		ViewGroup.LayoutParams vlp = v.getLayoutParams();
    		int w = vlp.width;
    		int h = vlp.height;
    		
    		v.layout(left, top, w + left, top - h);
			v.postInvalidate();
    	}
    }

    /**
     * ����View
     * @param v
     * @param scale ������
     */
	public static void zoomView(View v, float scale) {
		ViewGroup.LayoutParams vlp = v.getLayoutParams();
		int w = vlp.width;
		int h = vlp.height;
		if (w == 0) w = 1;
		if (h == 0) h = 1;
		int w1 = (int) ((float)w * scale + 0.5);
		int h1 = (int) ((float)h * scale + 0.5);
		
		if (v.getParent() instanceof FrameLayout) {
			FrameLayout.LayoutParams rlp = (FrameLayout.LayoutParams) v.getLayoutParams();
			
			if (checkRect(w1, w) && checkRect(h1, h)) {
				rlp.width = w1;
				rlp.height = h1;
				rlp.leftMargin = (int) Math.round(rlp.leftMargin + (float) ((w - rlp.width) / 2));
				rlp.topMargin = (int) Math.round(rlp.topMargin + (float) ((h - rlp.height) / 2));
				v.setLayoutParams(rlp);
				v.destroyDrawingCache();
				
			}
		} else {
			if (checkRect(w1, w) && checkRect(h1, h)) {
				float dx = (float) ((w - w1) / 2);
				float dy = (float) ((h - h1) / 2);
				int l = (int) Math.round(v.getLeft() + dx);
				int t = (int) Math.round( v.getTop() + dy);
				int r = (int) Math.round(v.getRight() - dx);
				int b = (int) Math.round( v.getBottom() - dy);
				v.layout(l, t, r, b);
				v.postInvalidate();
			}
		}
	}

    /**
     * ʹͼ��View������ʾ
     * @param view View
     * @param fill �Ƿ�ʹView��Ӧ����������ʾ
     */
    public static void alignCenter(View view, boolean fill) {
    	ViewGroup.LayoutParams vlp = view.getLayoutParams();
    	int sheight = vlp.height;
    	int swidth = vlp.width;
    	int h = view.getHeight();
    	int w = view.getWidth();
    	if (fill) {
	    	if ((float) (w * ((float) sheight / swidth)) > h) {
	    		h = (int) ((float)swidth * h / w + 0.5f);
	    		w = swidth;
	    	} else {
	    		w = (int) ((float)sheight * w / h + 0.5f);
	    		h = sheight;
	    	}
    	}
    	int left = (int) ((swidth - w) / 2 + 0.5f);
    	int top = (int) ((sheight - h) / 2 + 0.5f);
    	
    	if (view.getParent() instanceof FrameLayout) {
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
	        lp.leftMargin = left;
	        lp.topMargin = top;
	        lp.gravity = Gravity.TOP|Gravity.LEFT;
	        
	        view.setLayoutParams(lp);
	        view.destroyDrawingCache();
    	} else {
    		
    		view.layout(left, top, w + left, top - h);
			view.postInvalidate();
    	}
    }
    
    /**
     * ��ȡƥ����������
     * @param width ��ܿ��
     * @param height ��ܸ߶�
     * @param w Դ��
     * @param h Դ��
     * @param a �����
     */
    public static float getSpaceScale(int width, int height, int w, int h, int a) {
    	float s = 1f;
    	if (a < 1) a = 1;
    	if ((float) w * (height / width) > h) {
    		if (width > w * a) {
	    		s = a;
	    		h = a * h;
	    		w = a * w;
	    	} else {
	    		s = (float) width / w;
	    		h = (int) ((float)width * h / w + 0.5f);
	    		w = width;
	    	}
    	} else {
    		if (height > h * a) {
	    		s = a;
	    		h = a * h;
	    		w = a * w;
	    	} else {
	    		s = (float) height / h;
	    		w = (int) ((float)height * w / h + 0.5f);
	    		h = height;
	    	}
    	}
    	
    	return s;
    }

    public static float getSpaceScale(Rect pr, Rect sr, int a) {
    	return getSpaceScale(pr.width(), pr.height(), sr.width(), sr.height(), a);
    }
    
	public static Rect getViewRectInScreen(View v) {
		final int w = v.getWidth();
		final int h = v.getHeight();
		Rect r = new Rect();
		
		r.left = v.getLeft();
		r.top = v.getTop();
		r.right = r.left + w;
		r.bottom = r.top + h;
		
		ViewParent p = v.getParent();
		while (p instanceof View) {
			v = (View)p;
			p = v.getParent();
			
			r.left += v.getLeft();
			r.top += v.getTop();
			r.right = r.left + w;
			r.bottom = r.top + h;
		}
		
		return r;
	}
	
	/**
	 * �������
	 * @param dx ���ź�ߴ�
	 * @param d ����ǰ�ߴ�
	 * @return �Ƿ�δ�����ߴ�
	 */
	private static boolean checkRect(int dx, int d) {
		boolean iss = true;
		if (dx > d) iss = false;
		if (iss) {
			if (dx < 48) return false;
		} else {
			if (dx > 2048) return false;
		}
		//if (d < 48 || d > 2048) return false;
		return true;
	}
	
	public static int getTouchViewPosition(int x, int y, View[] vs) {
		if (vs == null || vs.length < 2) return 0;
		
		for (int i = 0; i < vs.length; i++) {
			Rect r = new Rect();
			vs[i].getGlobalVisibleRect(r);
			if (r != null && r.contains(x, y) && vs[i].getDrawingCache().getPixel(x, y) != 0) {
				return i;
			}
		}
		
		return 0;
	}
	
	public static int[] getImageViewState(ImageView iv) {
		int [] ints = new int[4];
		Matrix matrix = iv.getImageMatrix();
		Rect rect = iv.getDrawable().getBounds();
		float[] values = new float[9]; matrix.getValues(values);
		ints[0] = (int) (values[2]);
		ints[1] = (int) (values[5]);
		ints[2] = (int) (values[2] + rect.width() * values[0]);
		ints[3] = (int) (values[5] + rect.height() * values[0]);
		
		return ints;
	}
	
}
