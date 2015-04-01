package com.sunteorum.pinktoru.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * 支持手势缩放的ImageView类
 *
 */
@SuppressLint({ "NewApi", "ClickableViewAccessibility" })
public class ZoomImageView extends View {
    /** 画笔类  **/
	private Paint mPaint;

    private Runnable mRefresh = null;
    /** 缩放手势监听类  **/
    private ScaleGestureDetector mScaleDetector;
    /** 手势识别类  **/
    private GestureDetector mGestureDetector;
    /** 当前被渲染的Bitmap **/
    private Bitmap mBitmap;

    private int mThisWidth = -1, mThisHeight = -1;

    private Runnable mOnLayoutRunnable = null;

    private Matrix mBaseMatrix = new Matrix();
    private Matrix mDisplayMatrix = new Matrix();
    private Matrix mSuppMatrix = new Matrix();
    private Matrix mMatrix = new Matrix();

    /** 最大的拉伸比例   **/
    private float mMaxZoom;

    private float[] mMatrixValues = new float[9];
    private Runnable mFling = null;

    private double mLastDraw = 0;
    static final int sPaintDelay = 250;

    public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init();
    }
    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init();
    }
    public ZoomImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init();
    }

    private void init() {
        mPaint = new Paint();
        // 图像抖动处理
        mPaint.setDither(true);
        // 过滤优化操作  加快显示 
        mPaint.setFilterBitmap(true);
        // 去掉锯齿 
        mPaint.setAntiAlias(true);

        /** 刷新线程  **/
        mRefresh = new Runnable() {
            @Override
            public void run() {
                postInvalidate();
            }
        };

        mScaleDetector = new ScaleGestureDetector(getContext(),new ScaleListener());
        mGestureDetector = new GestureDetector(getContext(),new MyGestureListener());

        // 判断是否是新的API  开启硬件加速
        if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    public Bitmap getImageBitmap() {
        return mBitmap;
    }

    /** 回收Bitmap **/
    public void clear() {
        if(mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        // TODO Auto-generated method stub
        super.onLayout(changed, left, top, right, bottom);

        mThisWidth = right - left;
        mThisHeight = bottom - top;

        Runnable r = mOnLayoutRunnable;
        if (r != null) {
            mOnLayoutRunnable = null;
            r.run();
        }

        if (mBitmap != null) {
            setBaseMatrix(mBitmap, mBaseMatrix);
            setImageMatrix(getImageViewMatrix());
        }
    }

    private void setBaseMatrix(Bitmap bitmap, Matrix matrix) {
        float viewWidth = getWidth();
        float viewHeight = getHeight();
        matrix.reset();
        float widthScale = Math.min(viewWidth / (float)bitmap.getWidth(), 1.0f);
        float heightScale = Math.min(viewHeight / (float)bitmap.getHeight(), 1.0f);
        float scale;
        if (widthScale > heightScale) {
            scale = heightScale;
        } else {
            scale = widthScale;
        }

        /** 算取比例  进行平移   **/
        matrix.setScale(scale, scale);
        matrix.postTranslate(
                (viewWidth  - ((float)bitmap.getWidth()  * scale))/2F, 
                (viewHeight - ((float)bitmap.getHeight() * scale))/2F);
    }

    protected Matrix getImageViewMatrix() {
        mDisplayMatrix.set(mBaseMatrix);
        mDisplayMatrix.postConcat(mSuppMatrix);
        return mDisplayMatrix;
    }

    public void setImageMatrix(Matrix m){
        /** Matrix是否为空并是否定义   **/
        if (m != null && m.isIdentity()) {
            m = null;
        }
        if (m == null && !this.mMatrix.isIdentity() || m != null && !this.mMatrix.equals(m)) {
            this.mMatrix.set(m);
            invalidate();
        }
    }

    static private void translatePoint(Matrix matrix, float [] xy) {
        matrix.mapPoints(xy);
    }

    /**
     * 设置Bitmap 
     * 
     * @param bitmap
     */
    public void setImageBitmap(final Bitmap bitmap) {
        final int viewWidth = getWidth();

        // 开启硬件加速
        if( Build.VERSION.SDK_INT >=  Build.VERSION_CODES.HONEYCOMB && bitmap!=null && bitmap.getHeight()>1800 )
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (viewWidth <= 0)  {
            mOnLayoutRunnable = new Runnable() {
                public void run() {
                    setImageBitmap(bitmap);
                }
            };
            return;
        }
        if (bitmap != null) {
            setBaseMatrix(bitmap, mBaseMatrix);
            this.mBitmap = bitmap;
        } else {
            mBaseMatrix.reset();
            this.mBitmap = bitmap;
        }
        mSuppMatrix.reset();
        setImageMatrix(getImageViewMatrix());
        mMaxZoom = maxZoom();
        zoomTo(zoomDefault());
    }

    public void zoomTo(float scale) {
        float width = getWidth();
        float height = getHeight();
        zoomTo(scale, width/2F, height/2F);
    }

    protected void zoomTo(float scale, float centerX, float centerY) {
        if (scale > mMaxZoom) {
            scale = mMaxZoom;
        }
        float oldScale = getScale();
        float deltaScale = scale / oldScale;
        /** 根据某个中心点按照比例缩放  **/
        mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
        setImageMatrix(getImageViewMatrix());
        center(true, true, false);
    }

    /**
     * 计算中心位置 
     * 
     * @param vertical
     * @param horizontal
     * @param animate
     */
    protected void center(boolean vertical, boolean horizontal, boolean animate) {
        if (mBitmap == null)
            return;
        Matrix m = getImageViewMatrix();
        float [] topLeft  = new float[] { 0, 0 };
        float [] botRight = new float[] { mBitmap.getWidth(), mBitmap.getHeight() };
        translatePoint(m, topLeft);
        translatePoint(m, botRight);
        float height = botRight[1] - topLeft[1];
        float width  = botRight[0] - topLeft[0];
        float deltaX = 0, deltaY = 0;
        if (vertical) {
            int viewHeight = getHeight();
            if (height < viewHeight) {
                deltaY = (viewHeight - height)/2 - topLeft[1];
            } else if (topLeft[1] > 0) {
                deltaY = -topLeft[1];
            } else if (botRight[1] < viewHeight) {
                deltaY = getHeight() - botRight[1];
            }
        }
        if (horizontal) {
            int viewWidth = getWidth();
            if (width < viewWidth) {
                deltaX = (viewWidth - width)/2 - topLeft[0];
            } else if (topLeft[0] > 0) {
                deltaX = -topLeft[0];
            } else if (botRight[0] < viewWidth) {
                deltaX = viewWidth - botRight[0];
            }
        }
        postTranslate(deltaX, deltaY);
        if (animate) {
            Animation a = new TranslateAnimation(-deltaX, 0, -deltaY, 0);
            a.setStartTime(SystemClock.elapsedRealtime());
            a.setDuration(250);
            setAnimation(a);
        }
        setImageMatrix(getImageViewMatrix());
    }

    protected void postTranslate(float dx, float dy) {
        mSuppMatrix.postTranslate(dx, dy);
    }

    public float getScale() {
        return getScale(mSuppMatrix);
    }
    protected float getScale(Matrix matrix) {
        if(mBitmap!=null)
            return getValue(matrix, Matrix.MSCALE_X);
        else
            return 1f;
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    /**
     * 计算最大的拉伸比例
     * 
     * @return
     */
    protected float maxZoom() {
        if (mBitmap == null)
            return 1F;
        float fw = (float) mBitmap.getWidth()  / (float)mThisWidth;
        float fh = (float) mBitmap.getHeight() / (float)mThisHeight;
        float max = Math.max(fw, fh) * 16;
        return max;
    }

    /**
     * 原始显示比例 
     * 
     * @return
     */
    public float zoomDefault() {
        if (mBitmap == null)
            return 1F;
        float fw = (float)mThisWidth/(float)mBitmap.getWidth();
        float fh = (float)mThisHeight/(float)mBitmap.getHeight();
        return Math.max(Math.min(fw, fh),1);
    }

    protected void zoomTo(final float scale, final float centerX, final float centerY, final float durationMs) {
        final float incrementPerMs = (scale - getScale()) / durationMs;
        final float oldScale = getScale();
        final long startTime = System.currentTimeMillis();
        post(new Runnable() {
            public void run() {
                long now = System.currentTimeMillis();
                float currentMs = Math.min(durationMs, (float)(now - startTime));
                float target = oldScale + (incrementPerMs * currentMs);
                zoomTo(target, centerX, centerY);
                if (currentMs < durationMs) {
                    post(this);
                }
            }
        });
    }

    protected void scrollBy( float distanceX, float distanceY, final float durationMs ){
        final float dx = distanceX;
        final float dy = distanceY;
        final long startTime = System.currentTimeMillis();
        mFling = new Runnable() {
            float old_x    = 0;
            float old_y    = 0;
            public void run()
            {
                long now = System.currentTimeMillis();
                float currentMs = Math.min( durationMs, now - startTime );
                float x = easeOut( currentMs, 0, dx, durationMs );
                float y = easeOut( currentMs, 0, dy, durationMs );
                postTranslate( ( x - old_x ), ( y - old_y ) );
                center(true, true, false);
                old_x = x;
                old_y = y;
                if ( currentMs < durationMs ) {
                    post( this );
                }
            }
        };
        post( mFling );
    }

    private float easeOut( float time, float start, float end, float duration){
        return end * ( ( time = time / duration - 1 ) * time * time + 1 ) + start;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        if(mBitmap!=null && !mBitmap.isRecycled() ){
            if( Build.VERSION.SDK_INT >=  Build.VERSION_CODES.HONEYCOMB && getLayerType() == View.LAYER_TYPE_HARDWARE ){
                canvas.drawBitmap(mBitmap, mMatrix, null);
            }else{
                if( (System.currentTimeMillis()-mLastDraw) > sPaintDelay ){
                    canvas.drawBitmap(mBitmap, mMatrix, mPaint);
                    mLastDraw = System.currentTimeMillis();
                }
                else{
                    canvas.drawBitmap(mBitmap, mMatrix, null);
                    removeCallbacks(mRefresh);
                    postDelayed(mRefresh, sPaintDelay);
                }
            }
        }
    }
    /**
     * @author Administrator
     * 
     * 手势缩放监听
     */
    class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // TODO Auto-generated method stub
            //Log.i("ZoomImageView", "onScale");
            if(detector!=null && detector.isInProgress()){
                try{
                    float targetScale = getScale() * detector.getScaleFactor();
                    targetScale = Math.min(maxZoom(), Math.max(targetScale, 1.0f) );
                    zoomTo(targetScale, detector.getFocusX(), detector.getFocusY() );
                    invalidate();
                    return true;
                }catch(IllegalArgumentException e){
                    e.printStackTrace();
                }
            }
            return false;
        }
    };

    /**
     * @author Administrator
     *
     * 手势识别监听
     */
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
            // TODO Auto-generated method stub
            //Log.i("ZoomImageView", "onScroll");
            if((e1 != null && e1.getPointerCount() > 1) || (e2 != null && e2.getPointerCount() > 1)
                    || (mScaleDetector != null && mScaleDetector.isInProgress())){
                return false;
            }

            if(getScale() > zoomDefault() ) {
                removeCallbacks(mFling);
                postTranslate(-distanceX, -distanceY);
                center(true, true, false);
            }

            return true;
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
            Log.i("ZoomImageView", "onFling");
            // TODO Auto-generated method stub
            if ((e1!=null && e1.getPointerCount() > 1) || ( e2!=null && e2.getPointerCount() > 1)) 
                return false;
            if (mScaleDetector.isInProgress())
                return false;
            try{
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if ( Math.abs( velocityX ) > 800 || Math.abs( velocityY ) > 800 ) {
                    scrollBy( diffX / 2, diffY / 2, 300 );
                    invalidate();
                }
            }catch(NullPointerException  e){
                e.printStackTrace();
            }
            return super.onFling( e1, e2, velocityX, velocityY );
        }
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // TODO Auto-generated method stub
            Log.i("ZoomImageView", "onDoubleTap");
            if ( getScale() > zoomDefault() ){
                zoomTo(zoomDefault());
            }
            else 
                zoomTo(zoomDefault()*3, e.getX(), e.getY(),200);
            return true;
        }
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // TODO Auto-generated method stub
            Log.i("ZoomImageView", "onSingleTapConfirmed");
            // 设置点击事件
            if(mImageTouchedListener != null) {
                mImageTouchedListener.onImageTouched();
                return false;
            }
            return super.onSingleTapConfirmed(e);
        }

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if(mBitmap != null) {
            mScaleDetector.onTouchEvent(event);

            if(!mScaleDetector.isInProgress()) {
                mGestureDetector.onTouchEvent(event);
            }
        }

        return true;
    };

    /**
     * 
     * @author Administrator
     * 
     * 点击接口
     */
    private onImageTouchedListener mImageTouchedListener;

    public interface onImageTouchedListener {
        void onImageTouched();
    }

    public void setOnImageTouchedListener(onImageTouchedListener listener ){
        this.mImageTouchedListener = listener;
    }
    
}
