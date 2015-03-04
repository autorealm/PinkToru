package com.sunteorum.pinktoru;

import java.util.ArrayList;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sunteorum.pinktoru.entity.Piece;
import com.sunteorum.pinktoru.util.ImageUtils;
import com.sunteorum.pinktoru.view.MultiDirectionSlidingDrawer;
import com.sunteorum.pinktoru.view.PieceView;

public class PintuGameActivity extends BaseGameActivity {

	ArrayList<PieceView> movePieces = new ArrayList<PieceView>();
	int lastX;
	int lastY;
	boolean skip = false;
	
	
	@Override
	public boolean onTouch(final View v, MotionEvent event) {
		switch(event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			PieceView pib = (PieceView) v;
			Bitmap bmp = pib.getDrawingCache();
			if (bmp != null && bmp.getPixel((int)event.getX(), (int)event.getY()) == 0) {
				skip = true;
				return false;
			} else skip = false;
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			
			//�Ѹ���ͼ����������������ͼ֮��
			//puzzle.bringChildToFront(v);
			displayFront((PieceView)v);
			//�����ʼ�˶�������ر���
			if (drawer != null && drawer.isOpened()) drawer.animateClose();
			
			break;
		case MotionEvent.ACTION_MOVE:
			if (skip == true) return true;
			int dx =(int)event.getRawX() - lastX;
			int dy =(int)event.getRawY() - lastY;
			movePieces.clear();
			checkMove((PieceView)v, dx, dy, movePieces);
			//setOpacity(movePieces);
			
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					
					moveSomePieces(movePieces);
					//movePieces.clear();   //�����ƶ��ı�־����տ��ƶ���¼
					cleanPath();
					if (app.isAbsinmove()) {
						PieceView firstPiece = checkAbsorb((PieceView)v);
						cleanPath();
						absorb(firstPiece);
					} else absorb((PieceView)v);
					
					displayLast(level);
				}
				
			});
			
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			
			break;
		case MotionEvent.ACTION_UP:
			if (skip == true) return true;
			//��ȡ����Ƭ������·����Ȼ���ƶ���Ƭ
			cleanPath();
			
			PieceView firstPiece = checkAbsorb((PieceView)v);
			cleanPath();
			absorb(firstPiece);
			
			displayLast(level);
			//��������ʾ��ǰ��
			//displayFront(firstPiece);
			
			//setOpacity(allPieces);
			
			//�ж��Ƿ����
			hasComplete();
			
			break;        		
		}

		return true;
	
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	@Override
	void init() {
		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		puzzle = (FrameLayout) inflater.inflate(R.layout.activity_game, null);
		
		setContentView(puzzle);
		
		space = (FrameLayout) findViewById(R.id.space);
		//layGameStatus = (LinearLayout) this.findViewById(R.id.layGameStatus);
		drawer = (MultiDirectionSlidingDrawer) this.findViewById(R.id.drawer);
		handle = (View) this.findViewById(R.id.handle);
		tvGameLevel = ((TextView) findViewById(R.id.tvGameLevel));
		tvGameTime = ((TextView) findViewById(R.id.tvGameTime));
		tvGameStatus = ((TextView) findViewById(R.id.tvGameStatus));
		
		puzzle.setBackgroundDrawable(background_drawalbe);
		puzzle.setKeepScreenOn(true);
	}

	@SuppressWarnings("deprecation")
	@Override
	void onStartGame() {
		
		//������ģ������
		Drawable dg = puzzle.getBackground();
		Bitmap blurbg = ImageUtils.fastBlur(ImageUtils.DrawableToBitmap(dg), 10);
		if (blurbg != null) dg = ImageUtils.BitmapToDrawable(this, blurbg);
		if (dg != null) {
			dg.setAlpha(160);
			space.setBackgroundDrawable(dg);
			//puzzle.getBackground().setAlpha(120);
		}
		
		puzzle.setBackgroundColor(Color.LTGRAY);
		//puzzle.getChildAt(0).setVisibility(0);
		
		//����Ƭ���ӻ�
		int piececount = allPieces.size();
		for(int i=0; i< piececount; i++) {
			PieceView pib = (PieceView) allPieces.get(i);
			pib.setVisibility(0);
			
		}
		
	}

	@Override
	void onNewGame(Vector<Piece> pieces) {
		// TODO Auto-generated method stub

	}


	@Override
	void OnCreatePuzzle(PieceView pib, int index) {
		pib.setVisibility(8);
		puzzle.addView(pib);
		
	}

    /**
     * ʹ��Ƭǰ��
     * @param curPiece
     */
    private void displayFront(PieceView curPiece) {
    	puzzle.bringChildToFront(curPiece);   //�Ѹ���ͼ����������������ͼ֮��
    	curPiece.postInvalidate();
    	
    	int id = curPiece.getId();
    	int curRow = id / line;
    	int curLine = id % line;
    	
    	//top
    	if (curPiece.isHasTop()) {
    		PieceView topPiece = (PieceView) allPieces.get((curRow - 1) * line + curLine);
    		if (!topPiece.isTraverse()) {
    			topPiece.setTraverse(true);
    			displayFront(topPiece);
    		}
    		
    	}
    	
    	//right
    	if (curPiece.isHasRight()) {
    		PieceView rightPiece = (PieceView) allPieces.get(id + 1);
    		if (!rightPiece.isTraverse()) {
    			rightPiece.setTraverse(true);
        		displayFront(rightPiece);
    		}
    		
    	}
    	
    	//feet
    	if (curPiece.isHasFeet()) {
    		PieceView feetPiece = (PieceView) allPieces.get((curRow + 1) * line + curLine);
    		if (!feetPiece.isTraverse()) {
    			feetPiece.setTraverse(true);
        		displayFront(feetPiece);
    		}
    		
    	}
    	
    	//left
    	if (curPiece.isHasLeft()) {
    		PieceView leftPiece = (PieceView) allPieces.get(id - 1);
    		if (!leftPiece.isTraverse()) {
    			leftPiece.setTraverse(true);
        		displayFront(leftPiece);
    		}
    		
    	}
    	
    }
    
    /**
     * �ж���һ���ƶ�����Ƭ����ѭ������
     * @param curPIB
     * @param dx
     * @param dy
     * @param movePieces
     */
    private void checkMove(PieceView curPIB, int dx, int dy, ArrayList<PieceView> movePieces) {
    	int l = curPIB.getLeft() + dx;
    	int t = curPIB.getTop() + dy;
    	curPIB.setLocation(new Point(l, t));

    	/*
		if (l < 0) {
			l = 0;
			r = l + curPIB.getWidth();
		}
		if (r > screenWidth) {
			r = screenWidth;
			l = r - curPIB.getWidth();
		}
		if (t < 0) {
			t = 0;
			b = t + curPIB.getHeight();
		}
		if (b > screenHeight) {
			b = screenHeight;
			t = b - curPIB.getHeight();
		}
		*/
    	
		//curPIB.layout(l, t, r, b);
    	movePieces.add(curPIB);
    	
		int id = curPIB.getId();
    	int curRow = id / line;
    	int curLine = id % line;
    	
    	//top
    	if (curPIB.isHasTop()) {
    		PieceView topPIB = (PieceView) allPieces.get((curRow - 1) * line + curLine);
    		if (!topPIB.isTraverse()) {
    			topPIB.setTraverse(true);
    			checkMove(topPIB, dx, dy, movePieces);
    		}
    		
    	}
    	
    	//right
    	if (curPIB.isHasRight()) {
    		PieceView rightPIB = (PieceView) allPieces.get(id + 1);
        	if (!rightPIB.isTraverse()) {
        		rightPIB.setTraverse(true);
        		checkMove(rightPIB, dx, dy, movePieces);
        	}
    		
    	}
    	
    	//feet
    	if (curPIB.isHasFeet()) {
    		PieceView feetPIB = (PieceView) allPieces.get((curRow + 1) * line + curLine);
        	if (!feetPIB.isTraverse()) {
        		feetPIB.setTraverse(true);
        		checkMove(feetPIB, dx, dy, movePieces);
        	}
    		
    	}
    	
    	//left
    	if (curPIB.isHasLeft()) {
    		PieceView leftPIB = (PieceView) allPieces.get(id - 1);
        	if (!leftPIB.isTraverse()) {
        		leftPIB.setTraverse(true);
        		checkMove(leftPIB, dx, dy, movePieces);
        	}
    		
    	}
    	
    }

	/**
	 * һ���ƶ���Ƭ
	 * @param absorbPieces
	 */
	private void moveSomePieces(final ArrayList<PieceView> absorbPieces) {
		
		for (int i=0; i<absorbPieces.size(); i++) {
			
			PieceView piece = (PieceView) absorbPieces.get(i);
			Point loc = piece.getLocation();
			piece.layout(loc.x, loc.y, loc.x + piece.getWidth(), loc.y + piece.getHeight());
			
			FrameLayout.LayoutParams alp = (FrameLayout.LayoutParams) piece.getLayoutParams();
			alp.gravity = Gravity.TOP|Gravity.LEFT;
			alp.leftMargin = loc.x;
			alp.topMargin = loc.y;
			piece.setLayoutParams(alp);
			
		}
		
	}
    
    /**
     * ƴ����Ƭ
     * @param curPiece
     */
    private void absorb(PieceView curPiece) {
    	Point curMinp = curPiece.getMinp();
    	Point curLoc = curPiece.getLocation();
    	curPiece.layout(curLoc.x, curLoc.y, curLoc.x + curPiece.getWidth(), curLoc.y + curPiece.getHeight());
    	
    	FrameLayout.LayoutParams alp = (FrameLayout.LayoutParams) curPiece.getLayoutParams();
    	alp.gravity = Gravity.TOP|Gravity.LEFT;
		alp.leftMargin = curLoc.x;
		alp.topMargin = curLoc.y;
		curPiece.setLayoutParams(alp);
		
		puzzle.bringChildToFront(curPiece);   //�Ѹ���ͼ����������������ͼ֮��
		curPiece.postInvalidate();
		
    	int id = curPiece.getId();
    	int curRow = id / line;
    	int curLine = id % line;
    	
    	//top
    	if (curPiece.isHasTop()) {
    		PieceView topPiece = (PieceView) allPieces.get((curRow - 1) * line + curLine);
    		if (!topPiece.isTraverse()) {
    			Point topMinp = topPiece.getMinp();
        		topPiece.setLocation(new Point(curLoc.x + (topMinp.x - curMinp.x),
        				curLoc.y + (topMinp.y - curMinp.y)));
        		topPiece.setTraverse(true);
        		absorb(topPiece);
    		}
    		
    	}
    	
    	//right
    	if (curPiece.isHasRight()) {
    		PieceView rightPiece = (PieceView) allPieces.get(id + 1);
    		if (!rightPiece.isTraverse()) {
    			Point rightMinp = rightPiece.getMinp();
        		rightPiece.setLocation(new Point(curLoc.x + (rightMinp.x - curMinp.x),
        				curLoc.y + (rightMinp.y - curMinp.y)));
        		rightPiece.setTraverse(true);
        		absorb(rightPiece);
    		}
    		
    	}
    	
    	//feet
    	if (curPiece.isHasFeet()) {
    		PieceView feetPiece = (PieceView) allPieces.get((curRow + 1) * line + curLine);
    		if (!feetPiece.isTraverse()) {
    			Point feetMinp = feetPiece.getMinp();
        		feetPiece.setLocation(new Point(curLoc.x + (feetMinp.x - curMinp.x),
        				curLoc.y + (feetMinp.y - curMinp.y)));
        		feetPiece.setTraverse(true);
        		absorb(feetPiece);
    		}
    		
    	}
    	
    	//left
    	if (curPiece.isHasLeft()) {
    		PieceView leftPiece = (PieceView) allPieces.get(id - 1);
    		if (!leftPiece.isTraverse()) {
    			Point leftMinp = leftPiece.getMinp();
        		leftPiece.setLocation(new Point(curLoc.x + (leftMinp.x - curMinp.x),
        				curLoc.y + (leftMinp.y - curMinp.y)));
        		leftPiece.setTraverse(true);
        		absorb(leftPiece);
    		}
    		
    	}
    	
    }
    
    /**
     * ������ƴ�ϵ���Ƭ
     * @param v ��ǰ����Ƭ
     * @return �׸�����ƴ�ϵ���Ƭ
     */
    private PieceView checkAbsorb(PieceView v) {
    	PieceView firstPiece = null;
    	
    	PieceView curPiece = (PieceView) v;
    	curPiece.setTraverse(true);
    	
    	int curId = curPiece.getId();
    	int curRow = curId / line;
    	int curLine = curId % line;
       	Point curMinp = curPiece.getMinp();
    	Point curLoc = curPiece.getLocation();
    	
    	//��top��right��feet��left��ʼ����������������־
 
    	//top
    	if (curRow > 0) {   //��ǰ��Ƭ�����������Ƭ
    		int topPieceId = (curRow - 1) * line + curLine;
    		if (!curPiece.isHasTop()) {  //����������Ƭ��δ����
    			//��������������Ƭ����û����ײ����õ�������Ƭ��λ���ж��Ƿ�����
    			PieceView topPiece = (PieceView) allPieces.get(topPieceId);
	    		Point topLoc = topPiece.getLocation();
	    		Point topMinp = topPiece.getMinp();
	    		
	    		//�����������������������
	    		if (distance(curMinp, topMinp, curLoc, topLoc, INACCURACY)) {
	    			curPiece.setHasTop(true);
	    			topPiece.setHasFeet(true);
	    			if (firstPiece == null) {
	    				firstPiece = topPiece;
	    			}

	    		}
    		} else {  //����������Ƭ�Ѿ�����,�Ҳ�����������Դ��������ѭ����,������������Ƭ����
    			PieceView topPiece = (PieceView) allPieces.get(topPieceId);
    			if (!topPiece.isTraverse()) {
    				checkAbsorb(topPiece);
    			}

    		}
    	}
    	
    	//right
    	if (curLine < (line -1)) {  //��ǰ��Ƭ�����������Ƭ
    		int rightPieceId = curId + 1;
    		if (!curPiece.isHasRight()) {  //����������Ƭ��Ϊ����
    			//��������������Ƭ����û����ײ����õ�������Ƭ��λ���ж��Ƿ�����
    			PieceView rightPiece = (PieceView) allPieces.get(rightPieceId);
	    		Point rightLoc = rightPiece.getLocation();
	    		Point rightMinp = rightPiece.getMinp();

	    		//�����������������������
	    		if (distance(curMinp, rightMinp, curLoc, rightLoc, INACCURACY)) {
	    			curPiece.setHasRight(true);
	    			rightPiece.setHasLeft(true);
	    			if (firstPiece == null) {
	    				firstPiece = rightPiece;
	    			}
	    			
	    		}
    		} else {
    			PieceView rightPiece = (PieceView) allPieces.get(rightPieceId);
    			if (!rightPiece.isTraverse()) {
    				checkAbsorb(rightPiece);
    			}

    		}
    	}
    	
    	//feet
    	if (curRow < (row - 1)) {
    		int feetPieceId = (curRow + 1) * line + curLine;
    		if (!curPiece.isHasFeet()) {
    			//��������������Ƭ����û����ײ����õ�������Ƭ��λ���ж��Ƿ�����
    			PieceView feetPiece = (PieceView) allPieces.get(feetPieceId);
	    		Point feetLoc = feetPiece.getLocation();
	    		Point feetMinp = feetPiece.getMinp();
	    		
	    		//�����������������������
	    		if (distance(curMinp, feetMinp, curLoc, feetLoc, INACCURACY)) {
	    			curPiece.setHasFeet(true);
	    			feetPiece.setHasTop(true);
	    			if (firstPiece == null) {
	    				firstPiece = feetPiece;
	    			}
	    			
	    		}
    		} else {
    			PieceView feetPiece = (PieceView) allPieces.get(feetPieceId);
    			if (!feetPiece.isTraverse()) {
    				checkAbsorb(feetPiece);
    			}
    		}

    	}

    	//left
    	if (curLine > 0) {
    		int leftPieceId = curId - 1;
    		if (!curPiece.isHasLeft()) {
    			//��������������Ƭ����û����ײ����õ�������Ƭ��λ���ж��Ƿ�����
    			PieceView leftPiece = (PieceView) allPieces.get(leftPieceId);
	    		Point leftLoc = leftPiece.getLocation();
	    		Point leftMinp = leftPiece.getMinp();
	    		
	    		//�����������������������
	    		if (distance(curMinp, leftMinp, curLoc, leftLoc, INACCURACY)) {
	    			curPiece.setHasLeft(true);
	    			leftPiece.setHasRight(true);
	    			if (firstPiece == null) {
	    				firstPiece = leftPiece;
	    			}
	    			
	    		}
    		} else {
    			PieceView leftPiece = (PieceView) allPieces.get(leftPieceId);
    			if (!leftPiece.isTraverse()) {
    				checkAbsorb(leftPiece);
    			}
    		}

    	}
    	if (firstPiece == null) {
    		firstPiece = v;
    	}
    	return firstPiece;
    	
    }

    /**
     * ǰ����ʾδƴ�ϵ���Ƭ
     * @param num δƴ�ϵ���Ƭ����
     */
    private void displayLast(int num) {
    	int last = row * line;
    	for (int i=0; i<allPieces.size(); i++) {
    		PieceView piece = (PieceView) allPieces.get(i);
    		if (piece.isTraverse()) {
    			
    			last--;
    		} else {
    			
    		}
    	}
    	if (last <= Math.round(row * line * 1/3)) {
    		for (int i=0; i<allPieces.size(); i++) {
    			PieceView piece = (PieceView) allPieces.get(i);
        		if (!piece.isTraverse()) {
        			puzzle.bringChildToFront(piece);   //�Ѹ���ͼ����������������ͼ֮��
        	    	piece.postInvalidate();
        		}
            	
        	}
    	
    	}
    }
    
    @SuppressLint("NewApi")
    private void setOpacity(ArrayList<PieceView> pieces) {
    	if (android.os.Build.VERSION.SDK_INT < 11) return; 
    	for (int i=0; i<allPieces.size(); i++) {
    		PieceView piece = (PieceView) allPieces.get(i);
    		if (pieces.contains(piece) || piece.isTraverse()) {
    			piece.setAlpha(255);
    			//piece.setAlpha(1f);
    		} else {
    			piece.setAlpha(210);
    			//piece.setAlpha(0.95f);
    		}
        	
    	}
    }

    
}
