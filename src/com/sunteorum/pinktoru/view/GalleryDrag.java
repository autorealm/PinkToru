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
	private WindowManager windowManager;// windows窗口控制类 
	private WindowManager.LayoutParams windowParams;// 用于控制拖拽项的显示的参数 

	private int scaledTouchSlop;// 判断滑动的一个距离,scroll的时候会用到(24)

	private ImageView dragImageView;// 被拖拽的项(item)，其实就是一个ImageView
	private int dragSrcPosition;// 手指拖动项原始在列表中的位置 
	private int dragPosition;// 手指点击准备拖动的时候,当前拖动项在列表中的位置.

	private int dragPointX;// 在当前数据项中的位置 
	private int dragPointY;
	private int dragOffsetX;
	private int dragOffsetY;// 当前视图和屏幕的距离

	private int upScrollBounce;// 拖动的时候，开始向上滚动的边界 
	private int downScrollBounce;// 拖动的时候，开始向下滚动的边界 

	private final static int step = 1;// ListView 滑动步伐.

	private int current_Step;// 当前步伐.
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
	        	int x = (int) ev.getX();// 获取相对与ListView的x坐标 
	    		int y = (int) ev.getY();// 获取相应与ListView的y坐标 
	    		dragSrcPosition = dragPosition = pointToPosition(x, y);
	    		// 无效不进行处理 
	    		if (dragPosition == AdapterView.INVALID_POSITION) {
	    			//return;
	    			return super.onInterceptTouchEvent(ev);
	    		}
	    		
	    		// 获取当前位置的视图(可见状态)
	    	    itemView = (ImageView) getChildAt(dragPosition - getFirstVisiblePosition());
	    	    
	    	    // 获取到的dragPoint其实就是在你点击指定item项中的高度.
	    	    dragPointX = x - itemView.getLeft();
	    	    dragPointY = y - itemView.getTop();
	    	    // 这个值是固定的:其实就是ListView这个控件与屏幕最顶部的距离（一般为标题栏+状态栏）.
	    	    dragOffsetY = (int) (ev.getRawY() - y);
	    	    dragOffsetX = (int) (ev.getRawX() - x);
	    	    
	    		
	    	    upScrollBounce = getHeight() / 3;// 取得向上滚动的边际，大概为该控件的1/3
	    	    downScrollBounce = getHeight() * 2 / 3;// 取得向下滚动的边际，大概为该控件的2/3
	    	    
	    	    itemView.setDrawingCacheEnabled(true);// 开启cache.
	    	    bm = Bitmap.createBitmap(ImageUtils.DrawableToBitmap(itemView.getDrawable()));// 根据cache创建一个新的bitmap对象.
	    	    
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
    	// item的view不为空，且获取的dragPosition有效 
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
		// 按下 
    	if (ev.getAction() == MotionEvent.ACTION_DOWN) {
    		
	    }
	}
    
    /** 
    * 准备拖动，初始化拖动项的图像 
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
	
	    windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// 不需获取焦点
		    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE// 不需接受触摸事件 
		    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON// 保持设备常开，并保持亮度不变。
		    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;// 窗口占满整个屏幕，忽略周围的装饰边框（例如状态栏）。此窗口需考虑到装饰边框的内容。
	    if (wpflag > 0)
	    	windowParams.flags = windowParams.flags | WindowManager.LayoutParams.FLAG_DIM_BEHIND;//窗口之后的内容变暗。
	    
	    windowParams.format = PixelFormat.TRANSLUCENT;// 默认为不透明，这里设成透明效果.
	    windowParams.windowAnimations = android.R.style.Animation_Dialog;// 窗口所使用的动画设置 
	    
	    ImageView imageView = new ImageView(getContext());
	    imageView.setPadding(0, 0, 0, 0);
	    imageView.setImageBitmap(bm);
	    windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
	    
	    windowManager.addView(imageView, windowParams);
	    
	    dragImageView = imageView;

    } 

    /**
    * 拖动执行，在Move方法中执行 
    * 
    */ 
    public void onDrag(int x, int y) { 
	    
	    if (dragImageView != null) {
		    windowParams.alpha = 0.5f;// 透明度 
		    windowParams.x = x - (int) (dragImageView.getWidth() / 2 + 0.5f);
		    windowParams.y = y - (int) (dragImageView.getHeight() / 2 + 0.5f);
		    windowManager.updateViewLayout(dragImageView, windowParams);// 时时移动.
		    //Log.i("onDrag", "x:" + windowParams.x + " - y:" + windowParams.y);
	    }
	    
	    
    }

	/** 
    * 停止拖动，删除影像 
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
    * 拖动放下的时候 
    * 
    * @param y
    */
    public void onDrop(int x, int y) { 
	    // 为了避免滑动到分割线的时候，返回-1的问题 
	    int tempPosition = pointToPosition(0, y);
	    if (tempPosition != INVALID_POSITION) {
	    	dragPosition = tempPosition;
	    }
	
	    if (mDropListener != null && dragPosition >= 0 && dragPosition < getCount()) {
            mDropListener.drop(dragPosition, x, y);
        }
	    
	    /*// 超出边界处理(如果向上超过第二项Top的话，那么就放置在第一个位置)
	    if (y < getChildAt(0).getTop()) {
		    // 超出上边界 
		    dragPosition = 0;
		    // 如果拖动超过最后一项的最下边那么就防止在最下边 
	    } else if (y > getChildAt(getChildCount() - 1).getBottom()) {
		    // 超出下边界 
		    dragPosition = getAdapter().getCount() - 1;
	    }
	
	    // 数据交换 
	    if (dragPosition < getAdapter().getCount()) {
		    DragListAdapter adapter = (DragListAdapter) getAdapter();
		    adapter.update(dragSrcPosition, dragPosition);
	    }*/

    }
    
    /**
     * 不执行？
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
		startDrag(bm, (int) ev.getRawX(), (int) ev.getRawY());// 初始化影像 
    
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
