package com.sunteorum.pinktoru.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 自动适应列表高度的GridView扩展类
 *
 */
public class SquareGridView extends GridView {

	public SquareGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SquareGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SquareGridView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	// TODO Auto-generated constructor stub 
    	
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
    	MeasureSpec.AT_MOST);
    	super.onMeasure(widthMeasureSpec, expandSpec);
    	
	} 
	
}