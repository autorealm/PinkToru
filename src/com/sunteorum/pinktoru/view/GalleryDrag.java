package com.sunteorum.pinktoru.view;

import com.sunteorum.pinktoru.util.ImageUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowAnimationFrameStats;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController.AnimationParameters;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;

@SuppressLint("ClickableViewAccessibility")
@SuppressWarnings({ "deprecation", "unused" })
public class GalleryDrag extends Gallery {
	private WindowManager windowManager;// windows���ڿ����� 
	private WindowManager.LayoutParams windowParams;// ���ڿ�����ק�����ʾ�Ĳ��� 

	private int scaledTouchSlop;// �жϻ�����һ������,scroll��ʱ����õ�(24)

	private ImageView dragImageView;// ����ק����(item)����ʵ����һ��ImageView
	private int dragSrcPosition;// ��ָ�϶���ԭʼ���б��е�λ�� 
	private int dragPosition;// ��ָ���׼���϶���ʱ��,��ǰ�϶������б��е�λ��.

	private int dragPointX;// �ڵ�ǰ�������е�λ�� 
	private int dragPointY;
	private int dragOffsetX;
	private int dragOffsetY;// ��ǰ��ͼ����Ļ�ľ���

	private int upScrollBounce;// �϶���ʱ�򣬿�ʼ���Ϲ����ı߽� 
	private int downScrollBounce;// �϶���ʱ�򣬿�ʼ���¹����ı߽� 

	private final static int step = 1;// ListView ��������.

	private int current_Step;// ��ǰ����.
	private int wpflag = 1;
	
	private onDropListener mDropListener;
	public boolean mHasPerformedLongPress = false; 
	private CheckForLongPress mPendingCheckForLongPress;
	private MotionEvent mme;
	private Bitmap bm;
	private Handler mHandler = new Handler();
	private ImageView itemView;
	private int perx, pery;
	private boolean inreturn = false;
	
    public GalleryDrag(Context context) {
            super(context);
            
    }
    
    public GalleryDrag(Context context, AttributeSet attrs) {
            super(context, attrs);
            
    }

    public GalleryDrag(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	if (mHasPerformedLongPress) {
    		mHasPerformedLongPress = false;
    		
    		
        }
    	//System.out.println("MotionEvent:" + ev.getAction());
    	switch (ev.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	        	mme = ev;
	        	int x = (int) ev.getX();// ��ȡ�����ListView��x���� 
	    		int y = (int) ev.getY();// ��ȡ��Ӧ��ListView��y���� 
	    		dragSrcPosition = dragPosition = pointToPosition(x, y);
	    		// ��Ч�����д��� 
	    		if (dragPosition == AdapterView.INVALID_POSITION) {
	    			//return;
	    			return super.onInterceptTouchEvent(ev);
	    		}
	    		
	    		// ��ȡ��ǰλ�õ���ͼ(�ɼ�״̬)
	    	    itemView = (ImageView) getChildAt(dragPosition - getFirstVisiblePosition());
	    	    
	    	    // ��ȡ����dragPoint��ʵ����������ָ��item���еĸ߶�.
	    	    dragPointX = x - itemView.getLeft();
	    	    dragPointY = y - itemView.getTop();
	    	    // ���ֵ�ǹ̶���:��ʵ����ListView����ؼ�����Ļ����ľ��루һ��Ϊ������+״̬����.
	    	    dragOffsetY = (int) (ev.getRawY() - y);
	    	    dragOffsetX = (int) (ev.getRawX() - x);
	    	    
	    		
	    	    upScrollBounce = getHeight() / 3;// ȡ�����Ϲ����ı߼ʣ����Ϊ�ÿؼ���1/3
	    	    downScrollBounce = getHeight() * 2 / 3;// ȡ�����¹����ı߼ʣ����Ϊ�ÿؼ���2/3
	    	    
	    	    itemView.setDrawingCacheEnabled(true);// ����cache.
	    	    bm = Bitmap.createBitmap(ImageUtils.DrawableToBitmap(itemView.getDrawable()));// ����cache����һ���µ�bitmap����.
	    	    
	        	postCheckForLongClick();
            break;
	        case MotionEvent.ACTION_MOVE:
	        	if(Math.abs(ev.getX() - mme.getX()) > 10 || Math.abs(ev.getY() - mme.getY()) > 10){
	        		mHandler.removeCallbacks(mPendingCheckForLongPress);
	        		
	            }
	        break;
	        case MotionEvent.ACTION_UP:
	        	
	        break;
	        case MotionEvent.ACTION_CANCEL:
	            mHasPerformedLongPress = false;
	            if (mPendingCheckForLongPress != null) {
	            	mHandler.removeCallbacks(mPendingCheckForLongPress);
	            }
            break;
            
	    }
	    return super.onInterceptTouchEvent(ev); 
    } 

    @Override 
    public boolean onTouchEvent(MotionEvent ev) {
    	if (ev.getAction() == MotionEvent.ACTION_UP) {
    		mHasPerformedLongPress = false;
    		mHandler.removeCallbacks(mPendingCheckForLongPress);
    	} else if (ev.getAction() == MotionEvent.ACTION_MOVE &&
    			!isTouchInItem(itemView, (int)ev.getX(), (int)ev.getY())) {
    		mHasPerformedLongPress = false;
    		mHandler.removeCallbacks(mPendingCheckForLongPress);
    	}
    	// item��view��Ϊ�գ��һ�ȡ��dragPosition��Ч 
	    if (dragImageView != null && dragPosition != INVALID_POSITION) {
		    int action = ev.getAction();
		    switch (action) {
			    case MotionEvent.ACTION_UP:
			    	int upX = (int) ev.getRawX();
				    int upY = (int) ev.getRawY();
				    //stopDrag();
				    onDrop(upX, upY);
				    
			    break;
			    case MotionEvent.ACTION_MOVE:
				    int moveX = (int) ev.getRawX();
				    int moveY = (int) ev.getRawY();
				    onDrag(moveX, moveY);
				    
			    break;
			    case MotionEvent.ACTION_DOWN:
			    	
			    break;
			    default:
			    break;
		    }
		    
		    return true;
	    }

	    return super.onTouchEvent(ev);
    }

    @Override
	public void onLongPress(MotionEvent ev) {
		// TODO Auto-generated method stub
		super.onLongPress(ev);
		// ���� 
    	if (ev.getAction() == MotionEvent.ACTION_DOWN) {
    		
	    }
	}
    
    /** 
    * ׼���϶�����ʼ���϶����ͼ�� 
    * 
    * @param bm 
    * @param y 
    */ 
    private void startDrag(Bitmap bm, int x, int y) {
    	stopDrag();
    	perx = x - (int) (bm.getWidth() / 2 + 0.5f);
    	pery = y - (int) (bm.getHeight() / 2 + 0.5f);
		windowParams = new WindowManager.LayoutParams();
	    windowParams.gravity = Gravity.TOP | Gravity.LEFT;
	    windowParams.x = perx;
	    windowParams.y = pery;
	    windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
	    windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
	
	    windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// �����ȡ����
		    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE// ������ܴ����¼� 
		    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON// �����豸���������������Ȳ��䡣
		    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;// ����ռ��������Ļ��������Χ��װ�α߿�����״̬�������˴����迼�ǵ�װ�α߿�����ݡ�
	    if (wpflag > 0)
	    	windowParams.flags = windowParams.flags | WindowManager.LayoutParams.FLAG_DIM_BEHIND;//����֮������ݱ䰵��
	    
	    windowParams.format = PixelFormat.TRANSLUCENT;// Ĭ��Ϊ��͸�����������͸��Ч��.
	    windowParams.windowAnimations = android.R.style.Animation_Dialog;// ������ʹ�õĶ������� 
	    
	    ImageView imageView = new ImageView(getContext());
	    imageView.setPadding(0, 0, 0, 0);
	    imageView.setImageBitmap(bm);
	    windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
	    
	    windowManager.addView(imageView, windowParams);
	    
	    dragImageView = imageView;

    } 

    /**
    * �϶�ִ�У���Move������ִ�� 
    * 
    */ 
    public void onDrag(int x, int y) { 
	    
	    if (dragImageView != null) {
		    windowParams.alpha = 0.5f;// ͸���� 
		    windowParams.x = x - (int) (dragImageView.getWidth() / 2 + 0.5f);
		    windowParams.y = y - (int) (dragImageView.getHeight() / 2 + 0.5f);
		    windowManager.updateViewLayout(dragImageView, windowParams);// ʱʱ�ƶ�.
		    //Log.i("onDrag", "x:" + windowParams.x + " - y:" + windowParams.y);
	    }
	    
	    
    }

	/** 
    * ֹͣ�϶���ɾ��Ӱ�� 
    */ 
    public void stopDrag() {
	    if (dragImageView != null) {
		    windowManager.removeView(dragImageView);
		    dragImageView = null;
	    }
    }

    public void stopDrag(int tx, int ty) {
    	if (dragImageView != null) {
    		int sx = windowParams.x;
    		int sy = windowParams.y;
    		windowParams.x = tx;
    		windowParams.y = ty;
    		if (itemView != null) {
    			windowParams.width = itemView.getWidth();
    			windowParams.height = itemView.getHeight();
    		}
    		if (tx == 0 & ty == 0) {
    			windowParams.x = perx;
    			windowParams.y = pery;
    			
    		}
    		
		    windowManager.updateViewLayout(dragImageView, windowParams);
		    stopDrag();
	    }
    }
    
    public void setOnDropListener(onDropListener listener) {
        mDropListener = listener;
    }
    
    public interface onDropListener {
    	void drop(int pos, int x, int y);
    }
    
    public void setDimFlag(boolean flag) {
    	if (flag) wpflag = 1;
    	else wpflag = 0;
    }
    
    /**
    * �϶����µ�ʱ�� 
    * 
    * @param y
    */
    public void onDrop(int x, int y) { 
	    // Ϊ�˱��⻬�����ָ��ߵ�ʱ�򣬷���-1������ 
	    int tempPosition = pointToPosition(0, y);
	    if (tempPosition != INVALID_POSITION) {
	    	dragPosition = tempPosition;
	    }
	
	    if (mDropListener != null && dragPosition >= 0 && dragPosition < getCount()) {
            mDropListener.drop(dragPosition, x, y);
        }
	    
	    /*// �����߽紦��(������ϳ����ڶ���Top�Ļ�����ô�ͷ����ڵ�һ��λ��)
	    if (y < getChildAt(0).getTop()) {
		    // �����ϱ߽� 
		    dragPosition = 0;
		    // ����϶��������һ������±���ô�ͷ�ֹ�����±� 
	    } else if (y > getChildAt(getChildCount() - 1).getBottom()) {
		    // �����±߽� 
		    dragPosition = getAdapter().getCount() - 1;
	    }
	
	    // ���ݽ��� 
	    if (dragPosition < getAdapter().getCount()) {
		    DragListAdapter adapter = (DragListAdapter) getAdapter();
		    adapter.update(dragSrcPosition, dragPosition);
	    }*/

    }
    
    /**
     * ��ִ�У�
     */
    public void animReturn() {
    	if (dragImageView == null) return;
    	AnimationSet animset = new AnimationSet(true);
    	Animation tAnimation, mAnimation;
		
		animset.setDuration(300);
		animset.setInterpolator(new OvershootInterpolator());
		animset.setFillAfter(true);
		
		tAnimation = new TranslateAnimation(windowParams.x, perx, windowParams.y, pery);
				/*(Animation.RELATIVE_TO_SELF, 0.0F,
				Animation.RELATIVE_TO_SELF, perx,
				Animation.RELATIVE_TO_SELF, 0.0F,
				Animation.RELATIVE_TO_SELF, pery);*/
		tAnimation.setDuration(300);
		tAnimation.setInterpolator(new DecelerateInterpolator());
		animset.addAnimation(tAnimation);
		
		mAnimation = new AlphaAnimation(1.0f, 0f);
		mAnimation.setDuration(300);
		mAnimation.setInterpolator(new LinearInterpolator());
		animset.addAnimation(mAnimation);
		
		animset.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				inreturn = false;
				stopDrag();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				inreturn = true;
				System.out.println("onAnimationStart");
			}
			
		});
		
		dragImageView.startAnimation(animset);
		System.out.println("startAnimation");
    }
    
	private boolean isTouchInItem(View dragView, int x, int y) {
	    if(dragView == null){
	        return false;
	    }
	    int leftOffset = dragView.getLeft();
	    int topOffset = dragView.getTop();
	    if(x < leftOffset || x > leftOffset + dragView.getWidth()){
	        return false;
	    }
	    
	    if(y < topOffset || y > topOffset + dragView.getHeight()){
	        return false;
	    }
	    
	    return true;
	}
	
	private void performedLongPress(MotionEvent ev) {
		startDrag(bm, (int) ev.getRawX(), (int) ev.getRawY());// ��ʼ��Ӱ�� 
    
	}
	
    private class CheckForLongPress implements Runnable {
        private int mOriginalWindowAttachCount;
        
        @Override
		public void run() {
            if (hasWindowFocus() && mOriginalWindowAttachCount == getWindowAttachCount()
                    && mHasPerformedLongPress) {
                if (performLongClick()) {
                    mHasPerformedLongPress = false;
                }
                //System.out.println("--performedLongPress--");
                performedLongPress(mme);
            }
        }
        
		public void rememberWindowAttachCount() {
            mOriginalWindowAttachCount = getWindowAttachCount();
        }
		
    }
    
    private void postCheckForLongClick() {
        mHasPerformedLongPress = true;
        if (mPendingCheckForLongPress == null) {
            mPendingCheckForLongPress = new CheckForLongPress();
        }
        mPendingCheckForLongPress.rememberWindowAttachCount();
        mHandler.postDelayed(mPendingCheckForLongPress, 400);//ViewConfiguration.getLongPressTimeout()
    }
    
    
}
